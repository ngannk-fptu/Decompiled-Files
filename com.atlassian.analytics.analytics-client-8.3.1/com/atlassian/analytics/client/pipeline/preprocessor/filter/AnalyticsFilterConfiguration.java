/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.pipeline.preprocessor.filter;

import com.atlassian.analytics.client.eventfilter.BlacklistFilter;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.AnalyticsFilter;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.NoOpAnalyticFilter;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.WhitelistAnalyticFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsFilterConfiguration {
    @Bean
    public AnalyticsFilter noOpAnalyticFilter() {
        return new NoOpAnalyticFilter();
    }

    @Bean
    public AnalyticsFilter whitelistAnalyticFilter(BlacklistFilter blacklistFilter, WhitelistFilter whitelistFilter) {
        return new WhitelistAnalyticFilter(blacklistFilter, whitelistFilter);
    }
}

