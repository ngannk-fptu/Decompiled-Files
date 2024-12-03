/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.schedule;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleAnalyticEvent;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import javax.annotation.Nonnull;

@EventName(value="data-pipeline.export.schedule.updated")
public class ExportScheduleUpdateAnalyticEvent
extends ExportScheduleAnalyticEvent {
    public ExportScheduleUpdateAnalyticEvent(@Nonnull String pluginVersion, @Nonnull ScheduleConfig scheduleConfig) {
        super(pluginVersion, scheduleConfig);
    }
}

