/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.schedule.ManagedScheduledJob;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ManagedScheduledJobRegistrationService {
    public void registerManagedScheduledJob(ManagedScheduledJob var1);

    public void unregisterManagedScheduledJob(ManagedScheduledJob var1);
}

