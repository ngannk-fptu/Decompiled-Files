/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.caesium.impl.RunTimeCalculator
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.core.spi.SchedulerServiceConfiguration
 *  com.atlassian.scheduler.cron.CronSyntaxException
 */
package com.atlassian.confluence.plugins.schedule.admin.support;

import com.atlassian.scheduler.caesium.impl.RunTimeCalculator;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.spi.SchedulerServiceConfiguration;
import com.atlassian.scheduler.cron.CronSyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CronExpressionValidator {
    private final RunTimeCalculator runTimeCalculator;

    public CronExpressionValidator(SchedulerServiceConfiguration schedulerConfig) {
        this.runTimeCalculator = new RunTimeCalculator(schedulerConfig);
    }

    public List<Date> getFutureSchedules(String cronExpressionValue, int limit) throws CronSyntaxException {
        return this.getFutureSchedules(cronExpressionValue, limit, null);
    }

    public List<Date> getFutureSchedules(String cronExpressionValue, int limit, Date fromDate) throws CronSyntaxException {
        if (fromDate == null) {
            fromDate = new Date();
        }
        ArrayList<Date> futureSchedules = new ArrayList<Date>(limit);
        Schedule schedule = Schedule.forCronExpression((String)cronExpressionValue);
        Date nextRunTime = this.runTimeCalculator.nextRunTime(schedule, fromDate);
        while (nextRunTime != null && futureSchedules.size() < limit) {
            futureSchedules.add(nextRunTime);
            nextRunTime = this.runTimeCalculator.nextRunTime(schedule, nextRunTime);
        }
        return futureSchedules;
    }
}

