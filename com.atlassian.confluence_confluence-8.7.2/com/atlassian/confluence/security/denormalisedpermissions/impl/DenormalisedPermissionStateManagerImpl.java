/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.annotations.Internal;
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
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.dao.DenormalisedChangeLogDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.dao.DenormalisedServiceStateDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class DenormalisedPermissionStateManagerImpl
implements DenormalisedPermissionStateManager,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedPermissionStateManagerImpl.class);
    private static final boolean SKIP_AUTO_TURNING_ON = true;
    private static final long STALE_DATA_LAG = Long.getLong("confluence.denormalised_permissions.stale_data_lag", 60000L);
    public static final long MAX_ALLOWED_RELOAD_STATE_DELAY = Long.getLong("confluence.denormalised_permissions.max_allowed_reload_state_delay", 10000L);
    public static final int SERVICE_STATE_RELOADING_INTERVAL_WHEN_SERVICE_IS_DISABLED = 30000;
    public static final int SERVICE_STATE_RELOADING_INTERVAL = 5000;
    private final EventPublisher eventPublisher;
    private final DenormalisedServiceStateDao denormalisedServiceStateDao;
    private final PlatformTransactionManager transactionManager;
    private final DenormalisedPermissionStateLogService denormalisedPermissionStateLogService;
    private final DenormalisedLockService denormalisedLockService;
    private final DenormalisedChangeLogDao denormalisedChangeLogDao;
    private final AtomicBoolean schedulingEnabled = new AtomicBoolean();
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));
    private final AtomicReference<DenormalisedServiceStateRecord> currentSpaceServiceState = new AtomicReference();
    private final AtomicReference<DenormalisedServiceStateRecord> currentContentServiceState = new AtomicReference();
    private final AtomicLong currentSpaceServiceStateLastUpdateTime = new AtomicLong();
    private final AtomicLong currentContentServiceStateLastUpdateTime = new AtomicLong();
    private final AtomicLong lastTimeServiceStatesWereRetrievedFromDB = new AtomicLong();

    public DenormalisedPermissionStateManagerImpl(EventPublisher eventPublisher, DenormalisedServiceStateDao denormalisedServiceStateDao, PlatformTransactionManager transactionManager, DenormalisedPermissionStateLogService denormalisedPermissionStateLogService, DenormalisedLockService denormalisedLockService, DenormalisedChangeLogDao denormalisedChangeLogDao) {
        this.eventPublisher = eventPublisher;
        this.denormalisedServiceStateDao = denormalisedServiceStateDao;
        this.transactionManager = transactionManager;
        this.denormalisedPermissionStateLogService = denormalisedPermissionStateLogService;
        this.denormalisedLockService = denormalisedLockService;
        this.denormalisedChangeLogDao = denormalisedChangeLogDao;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @Override
    @Scheduled(fixedDelay=3000L)
    public void scheduled() {
        if (this.isSchedulingEnabled()) {
            if (this.areAllServicesDisabled() && System.currentTimeMillis() - this.lastTimeServiceStatesWereRetrievedFromDB.get() < 30000L) {
                return;
            }
            if (System.currentTimeMillis() - this.lastTimeServiceStatesWereRetrievedFromDB.get() < 5000L) {
                return;
            }
            this.reloadServiceState();
        }
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.enableScheduling();
        try {
            this.createStateRecordsIfTheyDoNotExist();
        }
        catch (Exception e) {
            log.error("createStateRecordsIfTheyDoNotExist threw an exception: " + e.getMessage(), (Throwable)e);
            this.denormalisedPermissionStateLogService.addMessageToStateLog(StateChangeInformation.MessageLevel.WARNING, "Fast permissions state records were not created. Message: " + e.getMessage());
            return;
        }
        this.reloadServiceState();
    }

    private void turnFastPermissionsOnByDefault() throws ExecutionException, InterruptedException {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
        Future<Void> future = this.executor.submit(() -> this.lambda$turnFastPermissionsOnByDefault$1((TransactionDefinition)transactionDefinition));
        future.get();
    }

    @Internal
    public void reloadServiceState() {
        try {
            DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
            new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition).execute(status -> {
                this.getRealServiceStatesFromDBAndUpdateCache();
                return null;
            });
        }
        catch (Exception e) {
            log.error("reloadServiceState failed: " + e.getMessage(), (Throwable)e);
        }
    }

    private boolean areAllServicesDisabled() {
        DenormalisedServiceStateRecord spaceRecord = this.currentSpaceServiceState.get();
        DenormalisedServiceStateRecord contentRecord = this.currentContentServiceState.get();
        return !(spaceRecord != null && DenormalisedPermissionServiceState.DISABLED != spaceRecord.getState() || contentRecord != null && DenormalisedPermissionServiceState.DISABLED != contentRecord.getState());
    }

    @Override
    public boolean isApiReady() {
        return this.isContentApiReady() && this.isSpaceApiReady();
    }

    @Override
    public boolean isSpaceApiReady() {
        if (System.getProperty("testBatchRunner.numberOfBatches") != null || System.getProperty("atlassian.product.test-lib.version") != null) {
            return false;
        }
        return System.currentTimeMillis() - this.currentSpaceServiceStateLastUpdateTime.get() < MAX_ALLOWED_RELOAD_STATE_DELAY && this.currentSpaceServiceState.get() != null && DenormalisedPermissionServiceState.SERVICE_READY == this.calculateStatus(this.currentSpaceServiceState.get());
    }

    @Override
    public boolean isContentApiReady() {
        if (System.getProperty("testBatchRunner.numberOfBatches") != null || System.getProperty("atlassian.product.test-lib.version") != null) {
            return false;
        }
        return System.currentTimeMillis() - this.currentContentServiceStateLastUpdateTime.get() < MAX_ALLOWED_RELOAD_STATE_DELAY && this.currentContentServiceState.get() != null && DenormalisedPermissionServiceState.SERVICE_READY == this.calculateStatus(this.currentContentServiceState.get());
    }

    private Map<DenormalisedServiceStateRecord.ServiceType, DenormalisedServiceStateRecord> getRealServiceStatesFromDBAndUpdateCache() {
        List<DenormalisedServiceStateRecord> stateRecords = this.denormalisedPermissionStateLogService.getAllStateRecords();
        Map allStateRecords = stateRecords.stream().collect(Collectors.toMap(DenormalisedServiceStateRecord::getServiceType, Function.identity()));
        HashMap<DenormalisedServiceStateRecord.ServiceType, DenormalisedServiceStateRecord> map = new HashMap<DenormalisedServiceStateRecord.ServiceType, DenormalisedServiceStateRecord>();
        DenormalisedServiceStateRecord contentStateRecord = (DenormalisedServiceStateRecord)allStateRecords.get((Object)DenormalisedServiceStateRecord.ServiceType.CONTENT);
        DenormalisedServiceStateRecord spaceStateRecord = (DenormalisedServiceStateRecord)allStateRecords.get((Object)DenormalisedServiceStateRecord.ServiceType.SPACE);
        map.put(DenormalisedServiceStateRecord.ServiceType.SPACE, spaceStateRecord);
        map.put(DenormalisedServiceStateRecord.ServiceType.CONTENT, contentStateRecord);
        this.updateCachedData(spaceStateRecord, contentStateRecord);
        return map;
    }

    private void updateCachedData(DenormalisedServiceStateRecord spaceStateRecord, DenormalisedServiceStateRecord contentStateRecord) {
        long currentTime = System.currentTimeMillis();
        this.currentContentServiceState.set(contentStateRecord);
        this.currentContentServiceStateLastUpdateTime.set(currentTime);
        this.currentSpaceServiceState.set(spaceStateRecord);
        this.currentSpaceServiceStateLastUpdateTime.set(currentTime);
        this.lastTimeServiceStatesWereRetrievedFromDB.set(currentTime);
    }

    private String getPrintableServiceState() {
        Map<DenormalisedServiceStateRecord.ServiceType, DenormalisedServiceStateRecord> statuses = this.getRealServiceStatesFromDBAndUpdateCache();
        DenormalisedServiceStateRecord spaceStateRecord = statuses.get((Object)DenormalisedServiceStateRecord.ServiceType.SPACE);
        DenormalisedServiceStateRecord contentStateRecord = statuses.get((Object)DenormalisedServiceStateRecord.ServiceType.CONTENT);
        return "SPACE status: " + (spaceStateRecord != null ? spaceStateRecord.getState() : DenormalisedPermissionServiceState.DISABLED) + ", CONTENT status: " + (spaceStateRecord != null ? contentStateRecord.getState() : DenormalisedPermissionServiceState.DISABLED);
    }

    @Override
    public DenormalisedPermissionServiceState getSpaceServiceState(boolean realTimeData) {
        boolean isCacheStalled = System.currentTimeMillis() - this.lastTimeServiceStatesWereRetrievedFromDB.get() > 60000L;
        DenormalisedServiceStateRecord stateRecord = realTimeData || this.currentSpaceServiceState.get() == null || isCacheStalled ? this.getRealServiceStatesFromDBAndUpdateCache().get((Object)DenormalisedServiceStateRecord.ServiceType.SPACE) : this.currentSpaceServiceState.get();
        return this.calculateStatus(stateRecord);
    }

    @Override
    public DenormalisedPermissionServiceState getContentServiceState(boolean realTimeData) {
        boolean isCacheStalled = System.currentTimeMillis() - this.lastTimeServiceStatesWereRetrievedFromDB.get() > 60000L;
        DenormalisedServiceStateRecord stateRecord = realTimeData || this.currentContentServiceState.get() == null || isCacheStalled ? this.getRealServiceStatesFromDBAndUpdateCache().get((Object)DenormalisedServiceStateRecord.ServiceType.CONTENT) : this.currentContentServiceState.get();
        return this.calculateStatus(stateRecord);
    }

    private DenormalisedPermissionServiceState calculateStatus(DenormalisedServiceStateRecord stateRecord) {
        long lag;
        if (stateRecord == null) {
            return DenormalisedPermissionServiceState.DISABLED;
        }
        if (DenormalisedPermissionServiceState.SERVICE_READY == stateRecord.getState() && (lag = System.currentTimeMillis() - stateRecord.getLastUpToDateTimestamp()) > STALE_DATA_LAG) {
            return DenormalisedPermissionServiceState.STALE_DATA;
        }
        return stateRecord.getState();
    }

    @Override
    public void enableService() {
        this.enableSpaceService();
        this.enableContentService();
    }

    @VisibleForTesting
    public void enableSpaceService() {
        this.enableService(DenormalisedServiceStateRecord.ServiceType.SPACE, DenormalisedLockService.LockName.SPACE_STATUS);
    }

    @VisibleForTesting
    public void enableContentService() {
        this.enableService(DenormalisedServiceStateRecord.ServiceType.CONTENT, DenormalisedLockService.LockName.CONTENT_STATUS);
    }

    private void enableService(DenormalisedServiceStateRecord.ServiceType serviceType, DenormalisedLockService.LockName lockName) {
        try {
            this.createStateRecordsIfTheyDoNotExist();
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("Unable to create state records on enabling the service: " + e.getMessage(), (Throwable)e);
            return;
        }
        this.executor.submit(() -> {
            DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
            new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition).execute(status -> {
                try {
                    this.denormalisedLockService.acquireLockForTransaction(lockName);
                    DenormalisedServiceStateRecord stateRecord = this.denormalisedPermissionStateLogService.getServiceStateRecord(serviceType);
                    if (!stateRecord.getState().isEnablingAllowed()) {
                        this.denormalisedPermissionStateLogService.addMessageToStateLog(StateChangeInformation.MessageLevel.WARNING, serviceType.getDisplayName() + " service can't be enabled when it's in " + stateRecord.getState().name() + " state");
                        return null;
                    }
                    this.denormalisedPermissionStateLogService.changeState(stateRecord, DenormalisedPermissionServiceState.INITIALISING, StateChangeInformation.MessageLevel.INFO, serviceType.getDisplayName() + " service has been enabled. Initialisation started. Previous state was: " + this.getPrintableServiceState());
                    this.eventPublisher.publish((Object)DenormalisedPermissionChangeStateAnalyticsEvent.buildChangeStateEvent(serviceType, DenormalisedPermissionServiceState.INITIALISING));
                }
                catch (Exception e) {
                    log.error("Unable to enable the service " + serviceType.getDisplayName() + ": " + e.getMessage(), (Throwable)e);
                    this.denormalisedPermissionStateLogService.addMessageToStateLog(StateChangeInformation.MessageLevel.ERROR, "Unable to enable " + serviceType.getDisplayName() + " service: " + e.getMessage());
                }
                return null;
            });
        });
    }

    @VisibleForTesting
    @Internal
    public void createStateRecordsIfTheyDoNotExist() throws ExecutionException, InterruptedException {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
        Future<Void> future = this.executor.submit(() -> this.lambda$createStateRecordsIfTheyDoNotExist$6((TransactionDefinition)transactionDefinition));
        future.get();
    }

    @Override
    public Long getSpacePermissionUpdateLag() {
        return this.getPermissionServiceUpdateLag(DenormalisedServiceStateRecord.ServiceType.SPACE);
    }

    @Override
    public Long getContentPermissionUpdateLag() {
        return this.getPermissionServiceUpdateLag(DenormalisedServiceStateRecord.ServiceType.CONTENT);
    }

    private Long getPermissionServiceUpdateLag(DenormalisedServiceStateRecord.ServiceType serviceType) {
        DenormalisedServiceStateRecord state = this.denormalisedServiceStateDao.getRecord(serviceType);
        if (state != null && state.getLastUpToDateTimestamp() > 0L) {
            return Math.max(0L, System.currentTimeMillis() - state.getLastUpToDateTimestamp());
        }
        return null;
    }

    @Override
    public void disableService(boolean cleanDenormalisedData) {
        try {
            this.createStateRecordsIfTheyDoNotExist();
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("Unable to create state records on disabling the service: " + e.getMessage(), (Throwable)e);
            return;
        }
        this.disableService(DenormalisedServiceStateRecord.ServiceType.SPACE, DenormalisedLockService.LockName.SPACE_STATUS, cleanDenormalisedData);
        this.disableService(DenormalisedServiceStateRecord.ServiceType.CONTENT, DenormalisedLockService.LockName.CONTENT_STATUS, cleanDenormalisedData);
    }

    private void disableService(DenormalisedServiceStateRecord.ServiceType serviceType, DenormalisedLockService.LockName lockName, boolean cleanDenormalisedData) {
        this.executor.submit(() -> {
            DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
            new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition).execute(status -> {
                try {
                    this.denormalisedLockService.acquireLockForTransaction(lockName);
                    DenormalisedServiceStateRecord stateRecord = this.denormalisedServiceStateDao.getRecord(serviceType);
                    if (!stateRecord.getState().isDisablingAllowed()) {
                        this.denormalisedPermissionStateLogService.addMessageToStateLog(StateChangeInformation.MessageLevel.WARNING, serviceType.getDisplayName() + " service can't be disabled when it's in " + stateRecord.getState().name() + " state");
                        return null;
                    }
                    this.denormalisedPermissionStateLogService.changeState(stateRecord, DenormalisedPermissionServiceState.SHUTTING_DOWN, StateChangeInformation.MessageLevel.INFO, serviceType.getDisplayName() + " service disabling started. Previous state was: " + this.getPrintableServiceState());
                    this.eventPublisher.publish((Object)DenormalisedPermissionChangeStateAnalyticsEvent.buildChangeStateEvent(serviceType, DenormalisedPermissionServiceState.SHUTTING_DOWN));
                }
                catch (Exception e) {
                    log.error("Unable to disable " + serviceType.getDisplayName() + " service: " + e.getMessage(), (Throwable)e);
                    this.denormalisedPermissionStateLogService.addMessageToStateLog(StateChangeInformation.MessageLevel.ERROR, "Unable to disable " + serviceType.getDisplayName() + " service: " + e.getMessage());
                }
                return null;
            });
        });
    }

    @Override
    public List<StateChangeInformation> getStateChangeLog(int limit) {
        return this.denormalisedPermissionStateLogService.getStateChangeLog(limit);
    }

    private boolean areBothServicesDisabled() {
        return this.denormalisedServiceStateDao.getAllRecords().stream().map(DenormalisedServiceStateRecord::getState).allMatch(state -> state == DenormalisedPermissionServiceState.DISABLED);
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

    @EventListener
    public void onApplicationStoppingEvent(ApplicationStoppingEvent event) {
        this.disableScheduling();
    }

    public void destroy() throws Exception {
        this.disableScheduling();
        this.executor.shutdownNow();
        this.eventPublisher.unregister((Object)this);
    }

    private void enableScheduling() {
        this.schedulingEnabled.set(true);
    }

    private void disableScheduling() {
        this.schedulingEnabled.set(false);
    }

    private boolean isSchedulingEnabled() {
        return this.schedulingEnabled.get();
    }

    private /* synthetic */ Void lambda$createStateRecordsIfTheyDoNotExist$6(TransactionDefinition transactionDefinition) throws Exception {
        new TransactionTemplate(this.transactionManager, transactionDefinition).execute(status -> {
            for (DenormalisedServiceStateRecord.ServiceType serviceType : DenormalisedServiceStateRecord.ServiceType.values()) {
                if (this.denormalisedServiceStateDao.getRecord(serviceType) != null) continue;
                this.denormalisedServiceStateDao.createRecord(serviceType, DenormalisedPermissionServiceState.DISABLED);
                log.debug("New service state record ({}) was created with 'DISABLED' state.", (Object)serviceType);
            }
            return null;
        });
        return null;
    }

    private /* synthetic */ Void lambda$turnFastPermissionsOnByDefault$1(TransactionDefinition transactionDefinition) throws Exception {
        new TransactionTemplate(this.transactionManager, transactionDefinition).execute(status -> {
            if (this.denormalisedChangeLogDao.getLastRecords(1).isEmpty() && this.areBothServicesDisabled()) {
                log.info("Turning on fast permissions service.");
                this.enableService();
            }
            return null;
        });
        return null;
    }
}

