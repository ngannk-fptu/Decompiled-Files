/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 */
package com.atlassian.analytics.client.pipeline.preprocessor;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.analytics.client.pipeline.preprocessor.anonymizer.MetaAnonymizer;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.AnalyticsFilter;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;
import java.util.Map;

public class EventPreprocessor {
    private final AnalyticsConfigService analyticsConfigService;
    private final AnalyticsFilter whitelistAnalyticFilter;
    private final MetaAnonymizer metaAnonymizer;

    public EventPreprocessor(AnalyticsConfigService analyticsConfigService, AnalyticsFilter whitelistAnalyticFilter, MetaAnonymizer metaAnonymizer) {
        this.analyticsConfigService = analyticsConfigService;
        this.whitelistAnalyticFilter = whitelistAnalyticFilter;
        this.metaAnonymizer = metaAnonymizer;
    }

    public boolean canCollect(RawEvent event) {
        return this.analyticsConfigService.canCollectAnalytics() && this.whitelistAnalyticFilter.canCollect(event);
    }

    public ProcessedEvent preprocess(RawEvent event) {
        ProcessedEvent.Builder builder = new ProcessedEvent.Builder(event);
        ProcessedEvent.Builder builderWithAnonymizedFields = this.metaAnonymizer.addAnonymizedFields(builder, event);
        Map<String, Object> whitelistedProperties = this.whitelistAnalyticFilter.filter(event);
        return builderWithAnonymizedFields.properties(whitelistedProperties).build();
    }
}

