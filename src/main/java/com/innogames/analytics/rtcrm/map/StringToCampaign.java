package com.innogames.analytics.rtcrm.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innogames.analytics.rtcrm.entity.Campaign;
import org.apache.flink.api.common.functions.MapFunction;

public class StringToCampaign implements MapFunction<String, Campaign> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Campaign map(final String campaignString) {
		try {
			return objectMapper.readValue(campaignString, Campaign.class);
		} catch (final JsonProcessingException e) {
			return null;
		}
	}

}
