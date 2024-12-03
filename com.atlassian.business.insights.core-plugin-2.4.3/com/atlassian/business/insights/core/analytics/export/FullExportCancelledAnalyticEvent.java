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

@EventName(value="data-pipeline.export.full.cancelled")
public class FullExportCancelledAnalyticEvent
extends AnalyticEvent {
    private final long exportJobTime;

    public FullExportCancelledAnalyticEvent(@Nonnull String pluginVersion, long exportJobTime) {
        super(pluginVersion);
        this.exportJobTime = exportJobTime;
    }

    public long getExportJobTime() {
        return this.exportJobTime;
    }
}

