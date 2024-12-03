/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.scheduling.PluginJob
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.scheduler;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckManager;
import com.atlassian.troubleshooting.healthcheck.event.HealthcheckScheduledFinishedEvent;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckJob
implements PluginJob {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckJob.class);

    public void execute(Map<String, Object> jobDataMap) {
        LOG.debug("Performing a scheduled HealthCheck of the instance. Brace yourselves.");
        SupportHealthCheckManager healthCheckManager = (SupportHealthCheckManager)jobDataMap.get("healthCheckManager");
        HealthStatusPersistenceService healthStatusPersistenceService = (HealthStatusPersistenceService)jobDataMap.get("healthStatusPersistenceService");
        NotificationService notificationService = (NotificationService)jobDataMap.get("notificationService");
        EventPublisher eventPublisher = (EventPublisher)jobDataMap.get("eventPublisher");
        PluginInfo pluginInfo = (PluginInfo)jobDataMap.get("pluginInfo");
        for (HealthCheckStatus status : healthCheckManager.runAllHealthChecks()) {
            eventPublisher.publish((Object)new HealthcheckScheduledFinishedEvent(status.getCompleteKey(), status.isHealthy(), status.getFailureReason(), status.getSeverity().ordinal(), pluginInfo.getPluginVersion()));
        }
        List<Integer> recordIds = healthStatusPersistenceService.deleteFailedStatusRecord();
        if (!recordIds.isEmpty()) {
            notificationService.deleteDismissById(recordIds);
        }
        LOG.debug("Scheduled HealthCheck complete! Aww Yiss");
    }
}

