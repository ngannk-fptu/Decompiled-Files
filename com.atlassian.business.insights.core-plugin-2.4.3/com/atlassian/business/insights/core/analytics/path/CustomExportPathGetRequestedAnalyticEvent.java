/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.path;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathAnalyticEvent;
import javax.annotation.Nonnull;

@EventName(value="data-pipeline.custom.export.path.get.requested")
public class CustomExportPathGetRequestedAnalyticEvent
extends CustomExportPathAnalyticEvent {
    public CustomExportPathGetRequestedAnalyticEvent(@Nonnull String pluginVersion) {
        super(pluginVersion);
    }
}

