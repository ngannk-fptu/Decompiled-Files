/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.scheduler.config.JobId;
import java.util.Collection;

public interface ManagedScheduledJobRegistry {
    public Collection<ManagedScheduledJob> getManagedScheduledJobs();

    public ManagedScheduledJob getManagedScheduledJob(JobId var1);

    public boolean isManaged(JobId var1);

    public void addManagedScheduledJob(ManagedScheduledJob var1);

    public void removeManagedScheduledJob(ManagedScheduledJob var1);
}

