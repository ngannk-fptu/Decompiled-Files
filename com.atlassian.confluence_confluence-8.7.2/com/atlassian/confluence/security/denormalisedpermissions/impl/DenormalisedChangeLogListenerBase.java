/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.event.events.admin.AsyncImportStartedEvent;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.DenormalisedPermissionChangeStateAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedLockService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedPermissionStateLogService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class DenormalisedChangeLogListenerBase {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedChangeLogListenerBase.class);
    protected final AtomicBoolean schedulingEnabled = new AtomicBoolean();
    private final EventPublisher eventPublisher;
    private final PlatformTransactionManager txManager;
    protected final DenormalisedPermissionStateLogService denormalisedPermissionStateLogService;
    protected final DenormalisedLockService denormalisedLockService;
    protected final DenormalisedPermissionStateManager denormalisedPermissionStateManager;

    protected DenormalisedChangeLogListenerBase(EventPublisher eventPublisher, PlatformTransactionManager txManager, DenormalisedPermissionStateLogService denormalisedPermissionStateLogService, DenormalisedLockService denormalisedLockService, DenormalisedPermissionStateManager denormalisedPermissionStateManager) {
        this.eventPublisher = eventPublisher;
        this.txManager = txManager;
        this.denormalisedPermissionStateLogService = denormalisedPermissionStateLogService;
        this.denormalisedLockService = denormalisedLockService;
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.enableScheduling();
    }

    @EventListener
    public void onApplicationStoppingEvent(ApplicationStoppingEvent event) {
        this.disableScheduling();
    }

    @Scheduled(fixedDelay=3000L)
    public void onCronEvent() {
        if (this.isSchedulingEnabled()) {
            try {
                if (this.isServiceEnabled()) {
                    this.processLogRecords();
                }
            }
            catch (Exception e) {
                log.error("Unable to process records: " + e.getMessage(), (Throwable)e);
            }
        }
    }

    protected abstract boolean isServiceEnabled();

    @VisibleForTesting
    public void processLogRecords() {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
        new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            this.denormalisedLockService.acquireLockForTransaction(this.getLockNameForLogProcessor());
            DenormalisedPermissionServiceState serviceState = this.denormalisedPermissionStateLogService.getServiceState(this.getServiceType());
            switch (serviceState) {
                case DISABLED: {
                    break;
                }
                case INITIALISING: {
                    this.updateAllRecords();
                    break;
                }
                case SHUTTING_DOWN: {
                    this.disableDenormalisedPermissions();
                    break;
                }
                case ERROR: {
                    break;
                }
                case SERVICE_READY: 
                case STALE_DATA: {
                    this.processChangedRecords();
                    break;
                }
                default: {
                    throw new IllegalStateException("Undefined service state found: " + serviceState.name() + " for " + this.getServiceType() + "service type");
                }
            }
            return null;
        });
    }

    @VisibleForTesting
    public abstract void processChangedRecords();

    protected abstract void deleteAllRecordsFromChangeLogTable();

    protected abstract void deactivateTriggers();

    protected abstract DenormalisedServiceStateRecord.ServiceType getServiceType();

    protected abstract DenormalisedLockService.LockName getLockNameForStatus();

    protected abstract DenormalisedLockService.LockName getLockNameForLogProcessor();

    protected abstract void activateTriggersInSeparateTransaction();

    protected abstract int updateAllRecordsInSmallSeparateTransactions() throws ExecutionException, InterruptedException;

    private void disableDenormalisedPermissions() {
        DenormalisedServiceStateRecord.ServiceType serviceType = this.getServiceType();
        try {
            StopWatch watch = StopWatch.createStarted();
            this.deactivateTriggers();
            this.deleteAllRecordsFromChangeLogTable();
            long duration = watch.getTime();
            this.denormalisedPermissionStateLogService.changeState(serviceType, DenormalisedPermissionServiceState.DISABLED, StateChangeInformation.MessageLevel.INFO, serviceType.getDisplayName() + " service has been disabled. Duration: " + duration, 0L);
            this.eventPublisher.publish((Object)DenormalisedPermissionChangeStateAnalyticsEvent.buildServiceDisabledEvent(serviceType, duration));
        }
        catch (Exception e) {
            log.error("Unable to disable the service " + serviceType + ": " + e.getMessage(), (Throwable)e);
            this.denormalisedPermissionStateLogService.changeState(serviceType, DenormalisedPermissionServiceState.ERROR, StateChangeInformation.MessageLevel.ERROR, serviceType.getDisplayName() + " service disabling failed. See more information in logs. Error message: " + e.getMessage(), null);
            this.eventPublisher.publish((Object)DenormalisedPermissionChangeStateAnalyticsEvent.buildChangeStateEvent(serviceType, DenormalisedPermissionServiceState.ERROR));
        }
    }

    protected void switchToReadyStateIfItIsInInitialisingState(StopWatch watch, int processedRecordsCount) {
        this.denormalisedLockService.acquireLockForTransaction(this.getLockNameForStatus());
        DenormalisedServiceStateRecord.ServiceType serviceType = this.getServiceType();
        DenormalisedPermissionServiceState serviceState = this.denormalisedPermissionStateLogService.getServiceState(serviceType);
        long duration = watch.getTime();
        if (DenormalisedPermissionServiceState.INITIALISING != serviceState) {
            this.denormalisedPermissionStateLogService.addMessageToStateLog(StateChangeInformation.MessageLevel.WARNING, "Initialisation was interrupted because " + serviceType.getDisplayName() + " service state has been changed to " + serviceState + ". Initialisation took " + duration + " ms.");
            return;
        }
        this.denormalisedPermissionStateLogService.changeState(serviceType, DenormalisedPermissionServiceState.SERVICE_READY, StateChangeInformation.MessageLevel.INFO, serviceType.getDisplayName() + " service is ready. Initialisation took " + duration + " ms.", watch.getStartTime());
        this.eventPublisher.publish((Object)DenormalisedPermissionChangeStateAnalyticsEvent.buildServiceReadyEvent(serviceType, duration, processedRecordsCount));
    }

    private void updateAllRecords() {
        block2: {
            DenormalisedServiceStateRecord.ServiceType serviceType = this.getServiceType();
            StopWatch watch = StopWatch.createStarted();
            try {
                this.deleteAllRecordsFromChangeLogTable();
                this.activateTriggersInSeparateTransaction();
                int processedRecordsCount = this.updateAllRecordsInSmallSeparateTransactions();
                this.switchToReadyStateIfItIsInInitialisingState(watch, processedRecordsCount);
            }
            catch (InterruptedException | ExecutionException e) {
                log.error(serviceType.getDisplayName() + " service: unable to update all permissions: " + e.getMessage(), (Throwable)e);
                this.denormalisedPermissionStateLogService.changeState(serviceType, DenormalisedPermissionServiceState.ERROR, StateChangeInformation.MessageLevel.ERROR, serviceType.getDisplayName() + " service: unable to update all permissions: " + e.getMessage(), null);
                this.eventPublisher.publish((Object)DenormalisedPermissionChangeStateAnalyticsEvent.buildChangeStateEvent(serviceType, DenormalisedPermissionServiceState.ERROR));
                if (!(e instanceof InterruptedException)) break block2;
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void enableScheduling() {
        this.schedulingEnabled.set(true);
    }

    protected void disableScheduling() {
        this.schedulingEnabled.set(false);
    }

    protected boolean isSchedulingEnabled() {
        return this.schedulingEnabled.get();
    }

    @PreDestroy
    public void preDestroy() {
        this.disableScheduling();
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onImportStartEvent(AsyncImportStartedEvent event) {
        if (event.isSiteImport()) {
            this.disableScheduling();
        }
    }

    @EventListener
    public void onImportFinishEvent(AsyncImportFinishedEvent event) {
        if (event.isSiteImport()) {
            this.enableScheduling();
        }
    }
}

