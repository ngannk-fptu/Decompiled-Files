/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobId
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm;

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmSchedulerUpgradeTask
implements UpmProductDataStartupComponent {
    private static final Logger log = LoggerFactory.getLogger(UpmSchedulerUpgradeTask.class);
    private final SchedulerService pluginScheduler;

    public UpmSchedulerUpgradeTask(SchedulerService pluginScheduler) {
        this.pluginScheduler = Objects.requireNonNull(pluginScheduler, "pluginScheduler");
    }

    @Override
    public void onStartupWithProductData() {
        this.unscheduleJob("upmPluginNotificationJob");
        this.unscheduleJob("upmPluginLicenseExpiryCheckSchedulerJob");
    }

    private void unscheduleJob(String jobName) {
        try {
            this.pluginScheduler.unscheduleJob(JobId.of((String)jobName));
        }
        catch (IllegalArgumentException e) {
            log.debug("Could not unschedule job '" + jobName + "'. This is a harmless error if the job had previously been unscheduled.", (Throwable)e);
        }
    }
}

