/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.Lists
 *  net.java.ao.ActiveObjectsException
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.time.DateUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.event.NewHealthcheckFailureEvent;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceService;
import com.atlassian.troubleshooting.stp.persistence.SupportHealthcheckSchema;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthStatusPersistenceServiceImpl
implements HealthStatusPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(HealthStatusPersistenceServiceImpl.class);
    private static final Integer DEFAULT_NUMBER_OF_DAYS = 15;
    private final ActiveObjects ao;
    private final HealthStatusPropertiesPersistenceService propertiesPersistenceService;
    private final EventPublisher eventPublisher;
    private final ClusterService clusterService;
    private final Function<SupportHealthcheckSchema.HealthCheckStatusAO, HealthCheckStatus> aoToHealthCheckStatusConverter = status -> HealthCheckStatus.builder().id(status.getID()).completeKey(status.getCompleteKey()).name(status.getStatusName()).description(status.getDescription()).isHealthy(status.getIsHealthy()).isSoftLaunch(false).isEnabled(true).failureReason(status.getFailureReason()).application(status.getApplicationName()).nodeId(status.getNodeId()).time(status.getFailedDate().getTime()).severity(SupportHealthStatus.Severity.valueOf(status.getSeverity())).build();

    @Autowired
    public HealthStatusPersistenceServiceImpl(ActiveObjects ao, HealthStatusPropertiesPersistenceService propertiesPersistenceService, EventPublisher eventPublisher, ClusterService clusterService) {
        this.ao = ao;
        this.propertiesPersistenceService = propertiesPersistenceService;
        this.eventPublisher = eventPublisher;
        this.clusterService = clusterService;
    }

    @Override
    public synchronized void storeFailedStatuses(List<HealthCheckStatus> statuses) {
        List<HealthCheckStatus> currentFailures = statuses.stream().filter(status -> !status.isHealthy()).filter(status -> !status.isSoftLaunch()).filter(HealthCheckStatus::isEnabled).collect(Collectors.toList());
        LOG.debug("Persisting {} failed health checks to the AO table", (Object)currentFailures.size());
        Date currentDate = new Date();
        List<HealthCheckStatus> oldFailures = this.getFailedStatuses(SupportHealthStatus.Severity.UNDEFINED);
        List<HealthCheckStatus> newFailures = this.difference(currentFailures, oldFailures);
        List<HealthCheckStatus> resolvedFailures = this.difference(this.filterOutOtherNodes(oldFailures), currentFailures);
        for (HealthCheckStatus newFailure : newFailures) {
            this.storeNewFailure(newFailure, currentDate);
        }
        for (HealthCheckStatus resolvedFailure : resolvedFailures) {
            this.resolveOldFailure(resolvedFailure, currentDate);
        }
        this.propertiesPersistenceService.storeLastRun();
    }

    private List<HealthCheckStatus> filterOutOtherNodes(List<HealthCheckStatus> oldFailures) {
        Set<String> otherNodes = this.getOtherActiveNodes();
        return oldFailures.stream().filter(s -> s.getNodeId() == null || !otherNodes.contains(s.getNodeId())).collect(Collectors.toList());
    }

    private Set<String> getOtherActiveNodes() {
        HashSet<String> nodes = new HashSet<String>(this.clusterService.getNodeIds());
        this.clusterService.getCurrentNodeId().ifPresent(nodes::remove);
        return nodes;
    }

    private void storeNewFailure(HealthCheckStatus status, Date currentDate) {
        try {
            String failureReason = status.getFailureReason();
            if (failureReason.length() > 450) {
                failureReason = failureReason.substring(0, 450);
            }
            SupportHealthcheckSchema.HealthCheckStatusAO statusEntity = (SupportHealthcheckSchema.HealthCheckStatusAO)this.ao.create(SupportHealthcheckSchema.HealthCheckStatusAO.class, new DBParam[]{new DBParam("STATUS_NAME", (Object)status.getName()), new DBParam("COMPLETE_KEY", (Object)status.getCompleteKey()), new DBParam("DESCRIPTION", (Object)status.getDescription()), new DBParam("IS_HEALTHY", (Object)status.isHealthy()), new DBParam("FAILURE_REASON", (Object)failureReason), new DBParam("APPLICATION_NAME", (Object)status.getApplication()), new DBParam("NODE_ID", (Object)status.getNodeId()), new DBParam("SEVERITY", (Object)status.getSeverity().toString()), new DBParam("FAILED_DATE", (Object)currentDate), new DBParam("IS_RESOLVED", (Object)false), new DBParam("RESOLVED_DATE", null)});
            statusEntity.save();
            this.eventPublisher.publish((Object)new NewHealthcheckFailureEvent(status));
            LOG.debug("{} check has failed and has be persisted to the AO table.", (Object)statusEntity.getStatusName());
        }
        catch (ActiveObjectsException e) {
            LOG.error("There's a problem persisting new health status, {}, into the database", (Object)status.getName());
            LOG.error("Stacktrace for failure:", (Throwable)e);
        }
    }

    private List<HealthCheckStatus> difference(List<HealthCheckStatus> original, List<HealthCheckStatus> toRemove) {
        return original.stream().filter(healthStatusRepresentation -> {
            for (HealthCheckStatus existing : toRemove) {
                if (!existing.getName().equals(healthStatusRepresentation.getName()) || !Objects.equals(existing.getNodeId(), healthStatusRepresentation.getNodeId()) || !existing.getSeverity().equals((Object)healthStatusRepresentation.getSeverity())) continue;
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    public List<HealthCheckStatus> getFailedStatuses(SupportHealthStatus.Severity severityLimit) {
        Query query = Query.select().where("IS_HEALTHY = ? AND IS_RESOLVED = ?", new Object[]{Boolean.FALSE, Boolean.FALSE}).order("SEVERITY ASC");
        List allFailedStatuses = Arrays.stream(this.ao.find(SupportHealthcheckSchema.HealthCheckStatusAO.class, query)).map(this.aoToHealthCheckStatusConverter).collect(Collectors.toList());
        return allFailedStatuses.stream().filter(input -> input.getSeverity().compareTo(severityLimit) >= 0).collect(Collectors.toList());
    }

    @Override
    public List<Integer> deleteFailedStatusRecord() {
        SupportHealthcheckSchema.HealthCheckStatusAO[] statuses;
        Date timeLimit = DateUtils.addDays((Date)new Date(), (int)(-DEFAULT_NUMBER_OF_DAYS.intValue()));
        ArrayList removedStatusIds = Lists.newArrayList();
        for (SupportHealthcheckSchema.HealthCheckStatusAO checkStatus : statuses = (SupportHealthcheckSchema.HealthCheckStatusAO[])this.ao.find(SupportHealthcheckSchema.HealthCheckStatusAO.class, Query.select().where("RESOLVED_DATE < ?", new Object[]{timeLimit}))) {
            removedStatusIds.add(checkStatus.getID());
            this.ao.delete(new RawEntity[]{checkStatus});
        }
        return removedStatusIds;
    }

    private void resolveOldFailure(HealthCheckStatus resolvedStatus, Date currentDate) {
        LOG.debug("Resolving the existing {} failed check", (Object)resolvedStatus.getName());
        SupportHealthcheckSchema.HealthCheckStatusAO healthCheckStatusAO = (SupportHealthcheckSchema.HealthCheckStatusAO)this.ao.get(SupportHealthcheckSchema.HealthCheckStatusAO.class, (Object)resolvedStatus.getId());
        if (healthCheckStatusAO != null && !healthCheckStatusAO.getIsResolved().booleanValue()) {
            healthCheckStatusAO.setIsHealthy(true);
            healthCheckStatusAO.setIsResolved(true);
            healthCheckStatusAO.setResolvedDate(currentDate);
            healthCheckStatusAO.save();
        }
    }
}

