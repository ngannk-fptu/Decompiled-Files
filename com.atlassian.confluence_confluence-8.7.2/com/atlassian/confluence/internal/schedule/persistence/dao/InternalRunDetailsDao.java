/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunOutcome
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.schedule.persistence.dao;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.schedule.caesium.SchedulerRunDetails;
import com.atlassian.confluence.schedule.managers.SchedulerRunDetailsPurgeMode;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Internal
public interface InternalRunDetailsDao {
    public static final String SCHEDULER_RUN_DETAILS = "scheduler_run_details";

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int purgeOldRunDetails(SchedulerRunDetailsPurgeMode var1, int var2);

    public long count(Optional<JobId> var1, long var2, RunOutcome var4);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int purgeAll();

    public List<SchedulerRunDetails> getRecentRunDetails(JobId var1, int var2);

    public List<SchedulerRunDetails> getRecentRunDetails(JobId var1);
}

