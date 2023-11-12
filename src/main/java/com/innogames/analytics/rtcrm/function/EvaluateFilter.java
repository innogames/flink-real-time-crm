package com.innogames.analytics.rtcrm.function;

import com.innogames.analytics.rtcrm.App;
import com.innogames.analytics.rtcrm.entity.Campaign;
import com.innogames.analytics.rtcrm.entity.TrackingEvent;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public class EvaluateFilter extends BroadcastProcessFunction<TrackingEvent, Campaign, Tuple2<TrackingEvent, Campaign>> {

	@Override
	public void processElement(
		final TrackingEvent event,
		final ReadOnlyContext readOnlyContext,
		final Collector<Tuple2<TrackingEvent, Campaign>> collector
	) throws Exception {
		if (event == null) {
			return;
		}

		final Instant now = Instant.now();
		final Iterable<Map.Entry<Integer, Campaign>> campaignState = readOnlyContext.getBroadcastState(App.CAMPAIGN_STATE_DESCRIPTOR).immutableEntries();
		final Stream<Campaign> campaigns = StreamSupport.stream(
			campaignState.spliterator(),
			false
		).map(Map.Entry::getValue);

		campaigns
			.filter(Campaign::isEnabled)
			.filter(crmCampaign -> crmCampaign.getGame().equals(event.getGame()))
			.filter(crmCampaign -> crmCampaign.getEventName().equals(event.getEventName()))
			.filter(crmCampaign -> crmCampaign.getStartDate() != null && now.isAfter(Instant.parse(crmCampaign.getStartDate())))
			.filter(crmCampaign -> crmCampaign.getEndDate() != null && now.isBefore(Instant.parse(crmCampaign.getEndDate())))
			.filter(crmCampaign -> evaluateScriptResult(evaluateScript(crmCampaign, event)))
			.map(crmCampaign -> new Tuple2<>(event, crmCampaign))
			.forEach(collector::collect);
	}

	@Override
	public void processBroadcastElement(
		final Campaign campaign,
		final Context context,
		final Collector<Tuple2<TrackingEvent, Campaign>> collector
	) throws Exception {
		if (campaign == null) {
			return;
		}

		context.getBroadcastState(App.CAMPAIGN_STATE_DESCRIPTOR).put(campaign.getCampaignId(), campaign);
	}

	private Object evaluateScript(final Campaign campaign, final TrackingEvent event) {
		try {
			final JSObject filterFunction = campaign.getFilterFunction();
			return filterFunction.call(null, event);
		} catch (final Exception e) {
			return false;
		}
	}

	private boolean evaluateScriptResult(final Object result) {
		if (result instanceof Boolean) {
			return (boolean) result;
		} else if (ScriptObjectMirror.isUndefined(result)) {
			return false;
		} else {
			return false;
		}
	}

}
