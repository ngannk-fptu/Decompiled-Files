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

@EventName(value="data-pipeline.export.schedule.deleted")
public class ExportScheduleDeletedAnalyticEvent
extends AnalyticEvent {
    public ExportScheduleDeletedAnalyticEvent(@Nonnull String pluginVersion) {
        super(pluginVersion);
    }
}

