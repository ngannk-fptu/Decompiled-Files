/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ScheduledJobStatus;

public interface ScheduledJobStatusManager {
    public ScheduledJobStatus getScheduledJobStatus(ManagedScheduledJob var1);
}

