/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.export;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import javax.annotation.Nonnull;

@EventName(value="data-pipeline.export.full.triggered")
public class FullExportTriggeredAnalyticEvent
extends AnalyticEvent {
    private final long exportTimespanInDays;

    public FullExportTriggeredAnalyticEvent(@Nonnull String pluginVersion, long exportTimespanInDays) {
        super(pluginVersion);
        this.exportTimespanInDays = exportTimespanInDays;
    }

    public long getExportTimespanInDays() {
        return this.exportTimespanInDays;
    }
}

