package com.innogames.analytics.rtcrm.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Trigger {

	private Campaign campaign;
	private TrackingEvent event;

	public Trigger() {
	}

	public Trigger(final Campaign campaign, final TrackingEvent event) {
		this.campaign = campaign;
		this.event = event;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(final Campaign campaign) {
		this.campaign = campaign;
	}

	public TrackingEvent getEvent() {
		return event;
	}

	public void setEvent(final TrackingEvent event) {
		this.event = event;
	}

}
