/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau;

import com.atlassian.analytics.client.hash.AnalyticsEmailHasher;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.GeneralExtractorConfiguration;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.MetaPropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.MauAwarePropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.MauService;
import com.atlassian.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={GeneralExtractorConfiguration.class})
public class MauAwareMetaPropertyExtractorConfiguration {
    @Bean
    public MauService mauUtils(CacheManager cacheManager, AnalyticsEmailHasher analyticsEmailHasher) {
        return new MauService(cacheManager, analyticsEmailHasher);
    }

    @Bean
    public MauAwarePropertyExtractor mauExtractor(MauService mauService, MetaPropertyExtractor metaPropertyExtractor) {
        return new MauAwarePropertyExtractor(mauService, metaPropertyExtractor);
    }
}

