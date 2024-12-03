/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.schedule;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ManagedScheduledJobInitialiser {
    public void initialiseManagedScheduledJobs();
}

