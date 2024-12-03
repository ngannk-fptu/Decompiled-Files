/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.Primary
 */
package com.atlassian.analytics.client.pipeline.preprocessor;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.analytics.client.pipeline.preprocessor.EventPreprocessor;
import com.atlassian.analytics.client.pipeline.preprocessor.anonymizer.AnonymizerConfiguration;
import com.atlassian.analytics.client.pipeline.preprocessor.anonymizer.MetaAnonymizer;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.AnalyticsFilter;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.AnalyticsFilterConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(value={AnonymizerConfiguration.class, AnalyticsFilterConfiguration.class})
public class EventPreprocessorConfiguration {
    @Bean
    @Primary
    public EventPreprocessor whitelistingAndAnonymizingEventProcessor(AnalyticsConfigService analyticsConfigService, @Qualifier(value="defaultMetaAnonymizer") MetaAnonymizer metaAnonymizer, @Qualifier(value="whitelistAnalyticFilter") AnalyticsFilter analyticsFilter) {
        return new EventPreprocessor(analyticsConfigService, analyticsFilter, metaAnonymizer);
    }

    @Bean
    public EventPreprocessor anonymizingEventProcessor(AnalyticsConfigService analyticsConfigService, @Qualifier(value="defaultMetaAnonymizer") MetaAnonymizer metaAnonymizer, @Qualifier(value="noOpAnalyticFilter") AnalyticsFilter analyticsFilter) {
        return new EventPreprocessor(analyticsConfigService, analyticsFilter, metaAnonymizer);
    }
}

