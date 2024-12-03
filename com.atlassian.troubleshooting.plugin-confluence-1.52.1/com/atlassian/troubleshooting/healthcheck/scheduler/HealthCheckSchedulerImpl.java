/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.scheduler;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckManager;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.NotificationService;
import com.atlassian.troubleshooting.healthcheck.scheduler.HealthCheckJob;
import com.atlassian.troubleshooting.healthcheck.scheduler.HealthCheckScheduler;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthCheckSchedulerImpl
implements HealthCheckScheduler,
LifecycleAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckSchedulerImpl.class);
    private static final String JOB_NAME = HealthCheckSchedulerImpl.class.getName() + ":job";
    private static final long INTERVAL = Long.getLong("atlassian.healthcheck.scheduler.interval-ms", TimeUnit.HOURS.toMillis(1L));
    private final HealthStatusPersistenceService healthStatusPersistenceService;
    private final HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService;
    private final NotificationService notificationService;
    private final PluginScheduler pluginScheduler;
    private final SupportHealthCheckManager healthCheckService;
    private final EventPublisher eventPublisher;
    private final PluginInfo pluginInfo;
    private final Map<String, Object> jobData = new HashMap<String, Object>();

    @Autowired
    public HealthCheckSchedulerImpl(HealthStatusPersistenceService healthStatusPersistenceService, HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService, NotificationService notificationService, PluginScheduler pluginScheduler, SupportHealthCheckManager healthCheckService, EventPublisher eventPublisher, PluginInfo pluginInfo) {
        this.healthStatusPersistenceService = healthStatusPersistenceService;
        this.healthStatusPropertiesPersistenceService = healthStatusPropertiesPersistenceService;
        this.notificationService = notificationService;
        this.pluginScheduler = pluginScheduler;
        this.healthCheckService = healthCheckService;
        this.eventPublisher = eventPublisher;
        this.pluginInfo = pluginInfo;
    }

    public static String getJobName() {
        return JOB_NAME;
    }

    @Override
    public void schedule() {
        this.jobData.put("healthCheckManager", this.healthCheckService);
        this.jobData.put("healthStatusPersistenceService", this.healthStatusPersistenceService);
        this.jobData.put("healthStatusPropertiesPersistenceService", this.healthStatusPropertiesPersistenceService);
        this.jobData.put("notificationService", this.notificationService);
        this.jobData.put("eventPublisher", this.eventPublisher);
        this.jobData.put("pluginInfo", this.pluginInfo);
        this.pluginScheduler.scheduleJob(JOB_NAME, HealthCheckJob.class, this.jobData, new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1L)), INTERVAL);
        LOGGER.debug(String.format("Scheduled Healthcheck Job running every %dms", INTERVAL));
    }

    public void onStart() {
        try {
            LOGGER.debug("Removing the old job if applicable...");
            this.pluginScheduler.unscheduleJob(JOB_NAME);
        }
        catch (IllegalArgumentException e) {
            LOGGER.debug("Attempting to remove the previous job failed - likely because it did not exist previously. This should be safe to ignore");
        }
        LOGGER.debug("Scheduling the job");
        this.schedule();
        LOGGER.debug("Startup complete");
    }

    public void onStop() {
        this.pluginScheduler.unscheduleJob(JOB_NAME);
    }
}

