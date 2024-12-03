/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space;

import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.security.denormalisedpermissions.impl.DenormalisedChangeLogListenerBase;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedLockService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedPermissionStateLogService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.DenormalisedSpacePermissionsUpdater;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.DenormalisedSpaceChangeLogDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.DenormalisedSpaceChangeLog;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class DenormalisedSpaceChangeLogListener
extends DenormalisedChangeLogListenerBase {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedSpaceChangeLogListener.class);
    private static final int BATCH_SIZE = Integer.getInteger("confluence.denormalised_space_permissions.log_processing_batch_size", 1000);
    private static final int BATCH_PROCESSING_LIMIT_SEC = Integer.getInteger("confluence.denormalised_space_permissions.log_processing_time_limit_sec", 5);
    private static final DenormalisedServiceStateRecord.ServiceType SERVICE_TYPE = DenormalisedServiceStateRecord.ServiceType.SPACE;
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));
    private final DenormalisedSpacePermissionsUpdater denormalisedSpacePermissionsUpdater;
    private final DenormalisedSpaceChangeLogDao denormalisedSpaceChangeLogDao;
    private final DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor;

    public DenormalisedSpaceChangeLogListener(EventPublisher eventPublisher, DenormalisedSpacePermissionsUpdater denormalisedSpacePermissionsUpdater, DenormalisedSpaceChangeLogDao denormalisedSpaceChangeLogDao, PlatformTransactionManager txManager, DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor, DenormalisedPermissionStateLogService denormalisedPermissionStateLogService, DenormalisedLockService denormalisedLockService, DenormalisedPermissionStateManager denormalisedPermissionStateManager) {
        super(eventPublisher, txManager, denormalisedPermissionStateLogService, denormalisedLockService, denormalisedPermissionStateManager);
        this.denormalisedSpacePermissionsUpdater = denormalisedSpacePermissionsUpdater;
        this.denormalisedSpaceChangeLogDao = denormalisedSpaceChangeLogDao;
        this.denormalisedPermissionsDdlExecutor = denormalisedPermissionsDdlExecutor;
    }

    @Override
    protected boolean isServiceEnabled() {
        DenormalisedPermissionServiceState state = this.denormalisedPermissionStateManager.getSpaceServiceState(false);
        return state != null && DenormalisedPermissionServiceState.DISABLED != state;
    }

    @Override
    public void processChangedRecords() {
        StopWatch watch = StopWatch.createStarted();
        List<DenormalisedSpaceChangeLog> changedSpaceRecords = this.denormalisedSpaceChangeLogDao.findSpaceChangeLogRecords(BATCH_SIZE);
        log.debug("Found {} space records for processing", (Object)changedSpaceRecords.size());
        if (changedSpaceRecords.size() == 0) {
            this.denormalisedLockService.acquireLockForTransaction(DenormalisedLockService.LockName.SPACE_STATUS);
            this.denormalisedPermissionStateLogService.updateLastUpToDateTimeStamp(SERVICE_TYPE, watch.getStartTime());
            return;
        }
        long deadline = watch.getStartTime() + (long)BATCH_PROCESSING_LIMIT_SEC * 1000L;
        List<DenormalisedSpaceChangeLog> processedRecords = this.updateDenormalisedPermissionsForSpaceList(changedSpaceRecords, deadline);
        this.denormalisedSpaceChangeLogDao.removeSpaceChangeLogRecords(processedRecords);
        log.debug("Processed {} space change records of {} in {}, time limit is {} sec", new Object[]{processedRecords.size(), changedSpaceRecords.size(), watch, BATCH_PROCESSING_LIMIT_SEC});
        if (changedSpaceRecords.size() < BATCH_SIZE && processedRecords.size() == changedSpaceRecords.size()) {
            this.denormalisedLockService.acquireLockForTransaction(DenormalisedLockService.LockName.SPACE_STATUS);
            this.denormalisedPermissionStateLogService.updateLastUpToDateTimeStamp(SERVICE_TYPE, watch.getStartTime());
        }
    }

    private List<DenormalisedSpaceChangeLog> updateDenormalisedPermissionsForSpaceList(List<DenormalisedSpaceChangeLog> changedRecords, long deadline) {
        Set<Long> processedSpaceIds;
        Map<Long, List<DenormalisedSpaceChangeLog>> spacesWithChangeLogRecords = changedRecords.stream().filter(record -> record.getSpaceId() != null).collect(Collectors.groupingBy(DenormalisedSpaceChangeLog::getSpaceId, Collectors.toList()));
        ArrayList<DenormalisedSpaceChangeLog> processedRecords = new ArrayList<DenormalisedSpaceChangeLog>(this.extractChangedChangeLogRecords(spacesWithChangeLogRecords, processedSpaceIds = this.denormalisedSpacePermissionsUpdater.updateSpacePermissions(spacesWithChangeLogRecords.keySet(), deadline)));
        if (processedRecords.size() < changedRecords.size()) {
            processedRecords.addAll(changedRecords.stream().filter(record -> record.getSpaceId() == null).collect(Collectors.toList()));
        }
        return processedRecords;
    }

    @Override
    protected void deleteAllRecordsFromChangeLogTable() {
        this.denormalisedSpaceChangeLogDao.removeAllSpaceChangeLogRecords();
    }

    @Override
    protected void deactivateTriggers() {
        this.denormalisedPermissionsDdlExecutor.deactivateSpaceTriggers();
    }

    @Override
    protected DenormalisedServiceStateRecord.ServiceType getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    protected DenormalisedLockService.LockName getLockNameForStatus() {
        return DenormalisedLockService.LockName.SPACE_STATUS;
    }

    @Override
    protected DenormalisedLockService.LockName getLockNameForLogProcessor() {
        return DenormalisedLockService.LockName.SPACE_LOG_PROCESSOR;
    }

    @Override
    protected void activateTriggersInSeparateTransaction() {
        try {
            this.executor.submit(this.denormalisedPermissionsDdlExecutor::activateSpaceTriggers).get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected int updateAllRecordsInSmallSeparateTransactions() throws ExecutionException, InterruptedException {
        return this.denormalisedSpacePermissionsUpdater.updateAllSpacePermissions(this.schedulingEnabled, () -> this.denormalisedPermissionStateManager.getSpaceServiceState(true));
    }

    private List<DenormalisedSpaceChangeLog> extractChangedChangeLogRecords(Map<Long, List<DenormalisedSpaceChangeLog>> idsWithChangeLogRecords, Set<Long> processedIds) {
        return idsWithChangeLogRecords.entrySet().stream().filter(e -> processedIds.contains(e.getKey())).flatMap(e -> ((List)e.getValue()).stream()).collect(Collectors.toList());
    }
}

