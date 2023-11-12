package com.innogames.analytics.rtcrm.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Config {

	private int defaultParallelism;
	private int checkpointInterval;
	private String checkpointDir;
	private String kafkaBootstrapServers;
	private String kafkaGroupId;
	private String eventTopic;
	private String campaignTopic;
	private String triggerTopic;

	public Config() {
	}

	public int getDefaultParallelism() {
		return defaultParallelism;
	}

	public void setDefaultParallelism(final int defaultParallelism) {
		this.defaultParallelism = defaultParallelism;
	}

	public int getCheckpointInterval() {
		return checkpointInterval;
	}

	public void setCheckpointInterval(final int checkpointInterval) {
		this.checkpointInterval = checkpointInterval;
	}

	public String getCheckpointDir() {
		return checkpointDir;
	}

	public void setCheckpointDir(final String checkpointDir) {
		this.checkpointDir = checkpointDir;
	}

	public String getKafkaBootstrapServers() {
		return kafkaBootstrapServers;
	}

	public void setKafkaBootstrapServers(final String kafkaBootstrapServers) {
		this.kafkaBootstrapServers = kafkaBootstrapServers;
	}

	public String getEventTopic() {
		return eventTopic;
	}

	public void setEventTopic(final String eventTopic) {
		this.eventTopic = eventTopic;
	}

	public String getKafkaGroupId() {
		return kafkaGroupId;
	}

	public void setKafkaGroupId(final String kafkaGroupId) {
		this.kafkaGroupId = kafkaGroupId;
	}

	public String getCampaignTopic() {
		return campaignTopic;
	}

	public void setCampaignTopic(final String campaignTopic) {
		this.campaignTopic = campaignTopic;
	}

	public String getTriggerTopic() {
		return triggerTopic;
	}

	public void setTriggerTopic(final String triggerTopic) {
		this.triggerTopic = triggerTopic;
	}

}
