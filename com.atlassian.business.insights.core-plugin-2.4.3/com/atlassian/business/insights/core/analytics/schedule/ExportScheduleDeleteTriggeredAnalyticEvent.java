/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.schedule;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import javax.annotation.Nonnull;

@EventName(value="data-pipeline.export.schedule.delete.triggered")
public class ExportScheduleDeleteTriggeredAnalyticEvent
extends AnalyticEvent {
    public ExportScheduleDeleteTriggeredAnalyticEvent(@Nonnull String pluginVersion) {
        super(pluginVersion);
    }
}

