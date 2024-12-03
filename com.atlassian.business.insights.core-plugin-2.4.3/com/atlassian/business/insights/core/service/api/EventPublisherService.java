/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface EventPublisherService {
    public void publish(@Nonnull AnalyticEvent var1);

    public String getPluginVersion();
}

