/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.reminder.job;

import com.atlassian.confluence.extra.calendar3.reminder.job.CalendarReminderJob;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.springframework.stereotype.Component;

@Component
public class DefaultReminderJob
implements JobRunner {
    private final CalendarReminderJob calendarReminderJob;

    public DefaultReminderJob(CalendarReminderJob calendarReminderJob) {
        this.calendarReminderJob = calendarReminderJob;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.calendarReminderJob.execute();
        return JobRunnerResponse.success();
    }
}

