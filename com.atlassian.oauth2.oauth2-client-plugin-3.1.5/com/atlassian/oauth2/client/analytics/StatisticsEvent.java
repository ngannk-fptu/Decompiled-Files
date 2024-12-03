/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.Map;
import javax.annotation.Nonnull;

@EventName(value="plugins.oauth2.client.statistics")
public class StatisticsEvent {
    public static final String TOTAL = "total";
    private final String product;
    private final Map<String, Long> configurations;
    private final Map<String, Long> tokens;

    public StatisticsEvent(@Nonnull String product, @Nonnull Map<String, Long> configurations, @Nonnull Map<String, Long> tokens) {
        this.product = product;
        this.configurations = configurations;
        this.tokens = tokens;
    }

    public String getProduct() {
        return this.product;
    }

    public Map<String, Long> getConfigurations() {
        return this.configurations;
    }

    public Map<String, Long> getTokens() {
        return this.tokens;
    }
}

