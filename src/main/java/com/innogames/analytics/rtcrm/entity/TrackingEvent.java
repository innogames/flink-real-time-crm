package com.innogames.analytics.rtcrm.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TrackingEvent {

	private String schemaVersion;
	private String eventId;
	private String systemType;
	private String systemName;
	private String game;
	private String market;
	private Integer playerId;
	private String eventType;
	private String eventName;
	private String eventScope;
	private String createdAt;
	private String receivedAt;
	private String hostname;

	private Map<String, Object> context;

	private Map<String, Object> data;

	public TrackingEvent() {
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(final String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(final String eventId) {
		this.eventId = eventId;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(final String systemType) {
		this.systemType = systemType;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(final String systemName) {
		this.systemName = systemName;
	}

	public String getGame() {
		return game;
	}

	public void setGame(final String game) {
		this.game = game;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(final String market) {
		this.market = market;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(final Integer playerId) {
		this.playerId = playerId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(final String eventName) {
		this.eventName = eventName;
	}

	public String getEventScope() {
		return eventScope;
	}

	public void setEventScope(final String eventScope) {
		this.eventScope = eventScope;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(final String createdAt) {
		this.createdAt = createdAt;
	}

	public String getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(final String receivedAt) {
		this.receivedAt = receivedAt;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(final Map<String, Object> context) {
		this.context = context;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(final Map<String, Object> data) {
		this.data = data;
	}

}
