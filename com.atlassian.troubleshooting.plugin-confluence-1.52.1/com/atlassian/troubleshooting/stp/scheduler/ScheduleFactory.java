/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.Schedule
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.scheduler.config.Schedule;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.Validate;

public class ScheduleFactory {
    public Schedule createSchedule(long intervalMillis, int startHour, int startMinute) {
        Validate.isTrue((intervalMillis > 0L ? 1 : 0) != 0, (String)"Interval must be positive: %s", (long)intervalMillis);
        Validate.isTrue((startHour >= 0 && startHour <= 23 ? 1 : 0) != 0, (String)"Start hour must be between 0 & 23: %s", (long)startHour);
        Validate.isTrue((startMinute >= 0 && startMinute <= 60 ? 1 : 0) != 0, (String)"Start minute must be between 0 & 60: %s", (long)startMinute);
        GregorianCalendar nextExecutionDate = new GregorianCalendar();
        nextExecutionDate.set(11, startHour);
        nextExecutionDate.set(12, startMinute);
        nextExecutionDate.set(13, 0);
        nextExecutionDate.set(14, 0);
        while (nextExecutionDate.getTime().getTime() < System.currentTimeMillis()) {
            ((Calendar)nextExecutionDate).add(14, (int)intervalMillis);
        }
        return Schedule.forInterval((long)intervalMillis, (Date)nextExecutionDate.getTime());
    }
}

