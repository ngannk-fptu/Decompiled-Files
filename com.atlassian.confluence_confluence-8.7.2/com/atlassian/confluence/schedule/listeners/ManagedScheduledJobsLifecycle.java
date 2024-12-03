/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.listeners;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.schedule.ManagedScheduledJobInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedScheduledJobsLifecycle
implements LifecycleItem {
    private static final Logger log = LoggerFactory.getLogger(ManagedScheduledJobsLifecycle.class);
    private ManagedScheduledJobInitialiser managedScheduledJobFactory;

    public void setManagedScheduledJobFactory(ManagedScheduledJobInitialiser managedScheduledJobFactory) {
        this.managedScheduledJobFactory = managedScheduledJobFactory;
    }

    public void startup(LifecycleContext context) {
        log.debug("Confluence is up! Can now initialise managed scheduled jobs.");
        try {
            this.managedScheduledJobFactory.initialiseManagedScheduledJobs();
        }
        catch (RuntimeException ex) {
            log.error("Failed to initialize scheduled jobs", (Throwable)ex);
        }
    }

    public void shutdown(LifecycleContext context) {
    }
}

