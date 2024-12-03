/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobId
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.upgrade;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.troubleshooting.stp.scheduler.ScheduleFactory;
import com.atlassian.troubleshooting.stp.scheduler.SchedulerServiceProvider;
import com.atlassian.troubleshooting.stp.scheduler.TaskSettingsStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class StpSchedulerCleanUpTask
implements PluginUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(StpSchedulerCleanUpTask.class);
    private static final int BUILD_NUMBER = 1;
    private static final String DESCRIPTION = "Verify if the obsolete Health Check scheduler exists and remove it.";
    private static final String STP_PLUGIN_KEY = "com.atlassian.support.stp";
    private static final String TASK_ID_HEALTH_REPORT = "HealthReportScheduledTask";
    private static final String TASK_ID_HERCULES_SCAN = "HerculesScheduledScanTask";
    private final SchedulerServiceProvider schedulerServiceProvider;
    private final PluginScheduler pluginScheduler;
    private final TaskSettingsStore taskSettingsStore;

    @Autowired
    public StpSchedulerCleanUpTask(SchedulerServiceProvider schedulerServiceProvider, PluginScheduler pluginScheduler, PluginSettingsFactory pluginSettingsFactory, ScheduleFactory scheduleFactory) {
        this.pluginScheduler = pluginScheduler;
        this.schedulerServiceProvider = schedulerServiceProvider;
        this.taskSettingsStore = new TaskSettingsStore(TASK_ID_HEALTH_REPORT, pluginSettingsFactory.createGlobalSettings(), scheduleFactory);
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return DESCRIPTION;
    }

    public Collection<Message> doUpgrade() {
        LOG.info("Running STP upgrade task {}. Checking if obsolete scheduler tasks exists in the instance", (Object)1);
        ArrayList<String> taskIds = new ArrayList<String>(2);
        taskIds.add(TASK_ID_HEALTH_REPORT);
        taskIds.add(TASK_ID_HERCULES_SCAN);
        this.unschedulePluginSchedulerTask(taskIds);
        this.unscheduleHealthReportTask();
        LOG.info("STP upgrade task {} is completed.", (Object)1);
        return Collections.emptyList();
    }

    public String getPluginKey() {
        return STP_PLUGIN_KEY;
    }

    private void unschedulePluginSchedulerTask(List<String> taskIds) {
        for (String taskId : taskIds) {
            try {
                this.pluginScheduler.unscheduleJob(taskId);
            }
            catch (IllegalArgumentException ex) {
                LOG.debug("The task '{}' is not found in the system, skipping...", (Object)taskId);
            }
        }
    }

    private void unscheduleHealthReportTask() {
        SchedulerService schedulerService = this.schedulerServiceProvider.getSchedulerService();
        schedulerService.unscheduleJob(JobId.of((String)TASK_ID_HEALTH_REPORT));
        this.taskSettingsStore.clear();
    }
}

