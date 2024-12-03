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

@EventName(value="data-pipeline.export.schedule.executed")
public class ExportScheduleExecutedAnalyticEvent
extends ExportScheduleAnalyticEvent {
    public ExportScheduleExecutedAnalyticEvent(@Nonnull String pluginVersion, @Nonnull ScheduleConfig scheduleConfig) {
        super(pluginVersion, scheduleConfig);
    }
}

