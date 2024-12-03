/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmSchedulerUpgradeTask
implements LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(UpmSchedulerUpgradeTask.class);
    private final PluginScheduler pluginScheduler;

    public UpmSchedulerUpgradeTask(PluginScheduler pluginScheduler) {
        this.pluginScheduler = (PluginScheduler)Preconditions.checkNotNull((Object)pluginScheduler, (Object)"pluginScheduler");
    }

    public void onStart() {
        this.unscheduleJob("upmPluginNotificationJob");
        this.unscheduleJob("upmPluginLicenseExpiryCheckSchedulerJob");
    }

    private void unscheduleJob(String jobName) {
        try {
            this.pluginScheduler.unscheduleJob(jobName);
        }
        catch (IllegalArgumentException e) {
            log.debug("Could not unschedule job '" + jobName + "'. This is a harmless error if the job had previously been unscheduled.", (Throwable)e);
        }
    }
}

