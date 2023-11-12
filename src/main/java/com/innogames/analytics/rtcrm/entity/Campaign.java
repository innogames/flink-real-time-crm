package com.innogames.analytics.rtcrm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.innogames.analytics.rtcrm.script.NashornEngine;
import javax.script.ScriptException;
import org.openjdk.nashorn.api.scripting.JSObject;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Campaign {

	private int campaignId;
	private boolean enabled;
	private String game;
	private String eventName;
	private String startDate;
	private String endDate;
	private String filter;

	@JsonIgnore
	private transient JSObject filterFunction;

	public Campaign() {
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public int getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(final int campaignId) {
		this.campaignId = campaignId;
	}

	public String getGame() {
		return game;
	}

	public void setGame(final String game) {
		this.game = game;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(final String eventName) {
		this.eventName = eventName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}

	public JSObject getFilterFunction() throws ScriptException {
		if (filterFunction == null) {
			filterFunction = (JSObject) NashornEngine.getInstance().eval(getFilter());
		}

		return filterFunction;
	}

}
