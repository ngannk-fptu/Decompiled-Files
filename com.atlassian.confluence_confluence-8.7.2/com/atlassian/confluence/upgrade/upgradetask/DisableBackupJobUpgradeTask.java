/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.atlassian.scheduler.config.JobId
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.cluster.ClusterConfigurationHelper;
import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.scheduler.config.JobId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisableBackupJobUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(DisableBackupJobUpgradeTask.class);
    private final ScheduledJobManager scheduledJobManager;
    private final ClusterConfigurationHelper clusterConfigurationHelper;
    private static final String JOB_NAME = "BackupJob";
    private static final String KB_LINK = "https://confluence.atlassian.com/doc/configuring-backups-138348.html";

    public DisableBackupJobUpgradeTask(ScheduledJobManager scheduledJobManager, ClusterConfigurationHelper clusterConfigurationHelper) {
        this.scheduledJobManager = scheduledJobManager;
        this.clusterConfigurationHelper = clusterConfigurationHelper;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return "7801";
    }

    public String getShortDescription() {
        return "Disables backup job";
    }

    public void doUpgrade() throws Exception {
        DisableBackupJobUpgradeTask.disableAutomaticBackup(this.scheduledJobManager, this.clusterConfigurationHelper);
    }

    public static void disableAutomaticBackup(ScheduledJobManager scheduledJobManager, ClusterConfigurationHelper clusterConfigurationHelper) {
        if (!clusterConfigurationHelper.isClusteredInstance()) {
            return;
        }
        try {
            JobId jobId = JobId.of((String)JOB_NAME);
            ScheduledJobStatus scheduledJobStatus = scheduledJobManager.getScheduledJob(jobId);
            if (scheduledJobStatus == null) {
                log.warn("BackupJob was not found, so it was not disabled. You should manually disable backup job, see https://confluence.atlassian.com/doc/configuring-backups-138348.html");
            } else {
                ExecutionStatus previousStatus = scheduledJobStatus.getStatus();
                scheduledJobManager.disable(jobId);
                log.info("BackupJob has been disabled. Previous status: {}", (Object)previousStatus);
            }
        }
        catch (Exception e) {
            log.error("Unable to disable BackupJob. You should manually disable backup job, see https://confluence.atlassian.com/doc/configuring-backups-138348.html, error message: " + e.getMessage(), (Throwable)e);
        }
    }
}

