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

@EventName(value="data-pipeline.export.full.finished")
public class FullExportFinishedAnalyticEvent
extends AnalyticEvent {
    private final long exportJobRuntime;
    private final int numberOfEntitiesExported;
    private final int numberOfRowsExported;

    public FullExportFinishedAnalyticEvent(@Nonnull String pluginVersion, long exportJobRuntime, int numberOfEntitiesExported, int numberOfRowsExported) {
        super(pluginVersion);
        this.exportJobRuntime = exportJobRuntime;
        this.numberOfEntitiesExported = numberOfEntitiesExported;
        this.numberOfRowsExported = numberOfRowsExported;
    }

    public long getExportJobRuntime() {
        return this.exportJobRuntime;
    }

    public int getNumberOfEntitiesExported() {
        return this.numberOfEntitiesExported;
    }

    public int getNumberOfRowsExported() {
        return this.numberOfRowsExported;
    }
}

