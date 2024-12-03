/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ScheduleConfigService {
    @Nonnull
    public Optional<ScheduleConfig> getExportSchedule();

    public void setExportSchedule(@Nullable ScheduleConfig var1);

    @Nonnull
    public Optional<ZonedDateTime> getNextRunTime();
}

