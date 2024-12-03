/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  org.apache.commons.lang3.tuple.Pair
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.annotations.Internal;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Internal
public interface SchedulerRunDetailsManager {
    public static final long ALL_JOBS_TTL_MILLIS = TimeUnit.HOURS.toMillis(Integer.getInteger("all.jobs.ttl.hours", 2160).intValue());
    public static final long UNSUCCESSFUL_JOBS_TTL_MILLIS = TimeUnit.HOURS.toMillis(Integer.getInteger("unsuccessful.jobs.ttl.hours", 168).intValue());

    public Pair<Integer, Integer> purgeOldRunDetails();

    public long count(JobId var1, long var2, RunOutcome var4);

    public RunDetails addRunDetails(JobId var1, Date var2, RunOutcome var3, @Nullable String var4);
}

