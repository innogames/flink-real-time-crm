package com.innogames.analytics.rtcrm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innogames.analytics.rtcrm.config.Config;
import com.innogames.analytics.rtcrm.entity.Campaign;
import com.innogames.analytics.rtcrm.entity.TrackingEvent;
import com.innogames.analytics.rtcrm.function.EvaluateFilter;
import com.innogames.analytics.rtcrm.map.StringToCampaign;
import com.innogames.analytics.rtcrm.map.StringToTrackingEvent;
import com.innogames.analytics.rtcrm.sink.TriggerCampaignSink;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class App {

	// pass config path via parameter or use config from resources as default
	private static final String configParameter = "config";
	private static final String configResource = "config.json";

	// state key: campaign ID
	// state value: campaign definition
	public static final MapStateDescriptor<Integer, Campaign> CAMPAIGN_STATE_DESCRIPTOR = new MapStateDescriptor<>(
		"CampaignState",
		BasicTypeInfo.INT_TYPE_INFO,
		TypeInformation.of(new TypeHint<>() {})
	);

	public static void main(String[] args) throws Exception {
		// 1 - parse config
		final ParameterTool parameterTool = ParameterTool.fromArgs(args);
		final Config config = readConfigFromFileOrResource(new ObjectMapper(), new TypeReference<>() {}, parameterTool);

		// 2 - create local env with web UI enabled, use StreamExecutionEnvironment.getExecutionEnvironment() for deployment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

		// 3 - make config variables available to functions
		env.getConfig().setGlobalJobParameters(ParameterTool.fromMap(Map.of(
			"kafka_bootstrap_servers", config.getKafkaBootstrapServers(),
			"trigger_topic", config.getTriggerTopic()
		)));

		// 4 - configure env
		env.setParallelism(config.getDefaultParallelism());
		env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
			6, // number of restart attempts
			Time.of(10, TimeUnit.SECONDS) // delay
		));

		// Checkpoints are a mechanism to recover from failures only(!)
		env.enableCheckpointing(config.getCheckpointInterval());

		// If a checkpoint directory is configured FileSystemCheckpointStorage will be used,
		// otherwise the system will use the JobManagerCheckpointStorage
		env.getCheckpointConfig().setCheckpointStorage(config.getCheckpointDir());

		// Checkpoint state is kept when the owning job is cancelled or fails
		env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

		// 5 - create and prepare streams
		final String kafkaBootstrapServers = config.getKafkaBootstrapServers();
		final String kafkaGroupId = config.getKafkaGroupId();

		final String eventTopic = config.getEventTopic();
		final String campaignTopic = config.getCampaignTopic();

		// 5.1 - broadcast stream for campaigns, without group ID to always consume all data from topic
		final KafkaSource<String> campaignSource = buildKafkaSource(kafkaBootstrapServers, campaignTopic, OffsetsInitializer.earliest());
		final BroadcastStream<Campaign> campaignStream = env.fromSource(campaignSource, WatermarkStrategy.noWatermarks(), campaignTopic)
			.setParallelism(1).name("campaign-stream")
			.map(new StringToCampaign()).setParallelism(1).name("parse-campaign")
			.broadcast(CAMPAIGN_STATE_DESCRIPTOR);

		// 5.2 - main stream for events, use group ID to resume reading from topic
		final KafkaSource<String> eventSource = buildKafkaSource(kafkaBootstrapServers, eventTopic, kafkaGroupId);
		final SingleOutputStreamOperator<TrackingEvent> eventStream = env.fromSource(eventSource, WatermarkStrategy.noWatermarks(), eventTopic)
			.map(new StringToTrackingEvent()).name("parse-event");

		// 6 - create and run pipeline
		eventStream
			.connect(campaignStream)
			.process(new EvaluateFilter()).name("evaluate-filter")
			.addSink(new TriggerCampaignSink()).name("trigger-campaign");

		env.execute("Real-time CRM Pipeline");
	}

	private static KafkaSource<String> buildKafkaSource(final String brokers, final String topic, final OffsetsInitializer offsetsInitializer) {
		return KafkaSource.<String>builder()
			.setBootstrapServers(brokers)
			.setTopics(topic)
			.setStartingOffsets(offsetsInitializer)
			.setValueOnlyDeserializer(new SimpleStringSchema())
			.build();
	}

	private static KafkaSource<String> buildKafkaSource(final String brokers, final String topic, final String groupId) {
		return KafkaSource.<String>builder()
			.setBootstrapServers(brokers)
			.setTopics(topic)
			.setGroupId(groupId)
			.setStartingOffsets(OffsetsInitializer.latest())
			.setValueOnlyDeserializer(new SimpleStringSchema())
			.build();
	}

	private static <T> T readConfigFromFileOrResource(
		final ObjectMapper objectMapper,
		final TypeReference<T> typeReference,
		final ParameterTool parameterTool
	) throws IOException {
		if (parameterTool.has(configParameter)) {
			return objectMapper.readValue(new File(parameterTool.get(configParameter)), typeReference);
		}

		return objectMapper.readValue(readResource(configResource), typeReference);
	}

	public static String readResource(final String resource) throws IOException {
		final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (final InputStream inputStream = classLoader.getResourceAsStream(resource)) {
			if (inputStream == null) {
				return null;
			}

			try (
				final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				final BufferedReader reader = new BufferedReader(inputStreamReader)
			) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}

}
