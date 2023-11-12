package com.innogames.analytics.rtcrm.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innogames.analytics.rtcrm.entity.TrackingEvent;
import org.apache.flink.api.common.functions.MapFunction;

public class StringToTrackingEvent implements MapFunction<String, TrackingEvent> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public TrackingEvent map(final String eventString) {
		try {
			return objectMapper.readValue(eventString, TrackingEvent.class);
		} catch (final JsonProcessingException e) {
			return null;
		}
	}

}
