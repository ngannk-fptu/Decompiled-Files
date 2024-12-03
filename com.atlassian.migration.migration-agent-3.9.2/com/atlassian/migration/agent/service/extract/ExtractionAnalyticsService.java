/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.TimerMetricEvent
 *  com.atlassian.cmpt.analytics.events.TimerMetricEvent$Builder
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.cmpt.analytics.events.TimerMetricEvent;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.extract.BucketCountUtil;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ExtractionAnalyticsService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ExtractionAnalyticsService.class);
    private final AnalyticsEventService analyticsEventService;

    public ExtractionAnalyticsService(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    public void sendExtractionAnalytics(String metricName, long elapsedTime, int size) {
        try {
            TimerMetricEvent event = ((TimerMetricEvent.Builder)new TimerMetricEvent.Builder(metricName).value(elapsedTime).tags((Map)ImmutableMap.of((Object)"count", (Object)BucketCountUtil.resolveCountTag(size)))).build();
            this.analyticsEventService.saveAnalyticsEventAsync(() -> event);
        }
        catch (Exception e) {
            log.error("Error occurred when building analytics for extraction {}", (Throwable)e);
        }
    }
}

