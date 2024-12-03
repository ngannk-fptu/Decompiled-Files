/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.schedule;

import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import com.atlassian.business.insights.core.rest.model.Weekdays;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import java.util.List;
import javax.annotation.Nonnull;

public class ExportScheduleAnalyticEvent
extends AnalyticEvent {
    private final List<Weekdays> days;
    private final int repeatIntervalInWeeks;
    private final int time;
    private final long fromDateInEpoch;
    private final int schemaVersion;

    public ExportScheduleAnalyticEvent(@Nonnull String pluginVersion, @Nonnull ScheduleConfig scheduleConfig) {
        super(pluginVersion);
        this.days = scheduleConfig.getDays();
        this.repeatIntervalInWeeks = scheduleConfig.getRepeatIntervalInWeeks();
        this.time = this.convertTimeToInt(scheduleConfig.getTime());
        this.fromDateInEpoch = this.convertDateToEpoch(scheduleConfig.getFromDate());
        this.schemaVersion = scheduleConfig.getSchemaVersion();
    }

    public boolean isScheduledMon() {
        return this.days.contains((Object)Weekdays.MONDAY);
    }

    public boolean isScheduledTue() {
        return this.days.contains((Object)Weekdays.TUESDAY);
    }

    public boolean isScheduledWed() {
        return this.days.contains((Object)Weekdays.WEDNESDAY);
    }

    public boolean isScheduledThu() {
        return this.days.contains((Object)Weekdays.THURSDAY);
    }

    public boolean isScheduledFri() {
        return this.days.contains((Object)Weekdays.FRIDAY);
    }

    public boolean isScheduledSat() {
        return this.days.contains((Object)Weekdays.SATURDAY);
    }

    public boolean isScheduledSun() {
        return this.days.contains((Object)Weekdays.SUNDAY);
    }

    public int getRepeatIntervalInWeeks() {
        return this.repeatIntervalInWeeks;
    }

    public int getRepeatTime() {
        return this.time;
    }

    public long getFromDateInEpoch() {
        return this.fromDateInEpoch;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    private long convertDateToEpoch(String fromDate) {
        return DateConversionUtil.parseIsoOffsetDatetime(fromDate).toEpochMilli();
    }

    private int convertTimeToInt(String time) {
        return Integer.parseInt(time.replace(":", ""));
    }
}

