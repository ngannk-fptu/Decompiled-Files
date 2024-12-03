/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general;

import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.MetaPropertyExtractor;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.sen.SenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralExtractorConfiguration {
    @Bean
    public MetaPropertyExtractor generalExtractor(PropertyExtractor propertyExtractor, AnalyticsPropertyService analyticsPropertyService, SenProvider senProvider) {
        return new MetaPropertyExtractor(propertyExtractor, analyticsPropertyService, senProvider);
    }
}

