/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 */
package com.atlassian.upm.schedule;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.schedule.UpmScheduler;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public interface UpmScheduledJob
extends JobRunner {
    public DateTime getStartTime();

    public Option<Duration> getInterval();

    public void execute(UpmScheduler.RunMode var1);
}

