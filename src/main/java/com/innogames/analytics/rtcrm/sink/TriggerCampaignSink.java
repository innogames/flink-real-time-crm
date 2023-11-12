package com.innogames.analytics.rtcrm.sink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innogames.analytics.rtcrm.entity.Campaign;
import com.innogames.analytics.rtcrm.entity.TrackingEvent;
import com.innogames.analytics.rtcrm.entity.Trigger;
import com.innogames.analytics.rtcrm.kafka.StringProducer;
import java.util.Properties;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class TriggerCampaignSink extends RichSinkFunction<Tuple2<TrackingEvent, Campaign>> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private transient StringProducer stringProducer;
	private String triggerTopic;

	@Override
	public void open(Configuration configuration) {
		final ParameterTool parameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

		triggerTopic = parameters.getRequired("trigger_topic");

		final String kafkaBootstrapServers = parameters.getRequired("kafka_bootstrap_servers");
		final Properties properties = new Properties();
		properties.put("bootstrap.servers", kafkaBootstrapServers);

		stringProducer = new StringProducer(properties);
		stringProducer.start();
	}

	@Override
	public void invoke(final Tuple2<TrackingEvent, Campaign> tuple, final Context context) throws Exception {
		final TrackingEvent event = tuple.f0;
		final Campaign campaign = tuple.f1;

		stringProducer.send(triggerTopic, objectMapper.writeValueAsString(new Trigger(campaign, event)));
		stringProducer.flush();
	}

	@Override
	public void close() throws Exception {
		super.close();
		stringProducer.stop();
	}

}
