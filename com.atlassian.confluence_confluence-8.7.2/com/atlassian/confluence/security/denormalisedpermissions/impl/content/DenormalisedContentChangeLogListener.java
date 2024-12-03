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
package com.atlassian.confluence.security.denormalisedpermissions.impl.content;

import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.security.denormalisedpermissions.impl.DenormalisedChangeLogListenerBase;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.DenormalisedContentPermissionsUpdater;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.DenormalisedContentChangeLogDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.RealContentAndPermissionsDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.DenormalisedContentChangeLog;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedLockService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedPermissionStateLogService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.HashMap;
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

public class DenormalisedContentChangeLogListener
extends DenormalisedChangeLogListenerBase {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedContentChangeLogListener.class);
    private static final int BATCH_SIZE = Integer.getInteger("confluence.denormalised_content_permissions.log_processing_batch_size", 1000);
    private static final int BATCH_PROCESSING_LIMIT_SEC = Integer.getInteger("confluence.denormalised_content_permissions.log_processing_time_limit_sec", 5);
    private static final DenormalisedServiceStateRecord.ServiceType SERVICE_TYPE = DenormalisedServiceStateRecord.ServiceType.CONTENT;
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));
    private final DenormalisedContentPermissionsUpdater denormalisedContentPermissionsUpdater;
    private final DenormalisedContentChangeLogDao denormalisedContentChangeLogDao;
    private final RealContentAndPermissionsDao realContentAndPermissionsDao;
    private final DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor;

    public DenormalisedContentChangeLogListener(EventPublisher eventPublisher, DenormalisedContentPermissionsUpdater denormalisedContentPermissionsUpdater, DenormalisedContentChangeLogDao denormalisedContentChangeLogDao, PlatformTransactionManager txManager, DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor, RealContentAndPermissionsDao realContentAndPermissionsDao, DenormalisedPermissionStateLogService denormalisedPermissionStateLogService, DenormalisedLockService denormalisedLockService, DenormalisedPermissionStateManager denormalisedPermissionStateManager) {
        super(eventPublisher, txManager, denormalisedPermissionStateLogService, denormalisedLockService, denormalisedPermissionStateManager);
        this.denormalisedPermissionsDdlExecutor = denormalisedPermissionsDdlExecutor;
        this.denormalisedContentPermissionsUpdater = denormalisedContentPermissionsUpdater;
        this.denormalisedContentChangeLogDao = denormalisedContentChangeLogDao;
        this.realContentAndPermissionsDao = realContentAndPermissionsDao;
    }

    @Override
    protected boolean isServiceEnabled() {
        DenormalisedPermissionServiceState state = this.denormalisedPermissionStateManager.getContentServiceState(false);
        return state != null && DenormalisedPermissionServiceState.DISABLED != state;
    }

    @Override
    public void processChangedRecords() {
        StopWatch watch = StopWatch.createStarted();
        List<DenormalisedContentChangeLog> changedContentRecords = this.denormalisedContentChangeLogDao.findContentChangeLogRecords(BATCH_SIZE);
        log.debug("Found {} content records for processing", (Object)changedContentRecords.size());
        if (changedContentRecords.size() == 0) {
            this.denormalisedLockService.acquireLockForTransaction(DenormalisedLockService.LockName.CONTENT_STATUS);
            this.denormalisedPermissionStateLogService.updateLastUpToDateTimeStamp(SERVICE_TYPE, watch.getStartTime());
            return;
        }
        long deadline = watch.getStartTime() + (long)BATCH_PROCESSING_LIMIT_SEC * 1000L;
        List<DenormalisedContentChangeLog> processedRecords = this.updateDenormalisedPermissionsForContentList(changedContentRecords, deadline);
        this.denormalisedContentChangeLogDao.removeContentChangeLogRecords(processedRecords);
        log.debug("Processed {} content change records of {} in {}, time limit is {} sec", new Object[]{processedRecords.size(), changedContentRecords.size(), watch, BATCH_PROCESSING_LIMIT_SEC});
        if (changedContentRecords.size() < BATCH_SIZE && processedRecords.size() == changedContentRecords.size()) {
            this.denormalisedLockService.acquireLockForTransaction(DenormalisedLockService.LockName.CONTENT_STATUS);
            this.denormalisedPermissionStateLogService.updateLastUpToDateTimeStamp(SERVICE_TYPE, watch.getStartTime());
        }
    }

    private List<DenormalisedContentChangeLog> updateDenormalisedPermissionsForContentList(List<DenormalisedContentChangeLog> changedRecords, long deadline) {
        HashMap<Long, List<DenormalisedContentChangeLog>> pagesWithChangeLogRecords = new HashMap<Long, List<DenormalisedContentChangeLog>>(changedRecords.stream().filter(record -> record.getContentId() != null).collect(Collectors.groupingBy(DenormalisedContentChangeLog::getContentId, Collectors.toList())));
        List<DenormalisedContentChangeLog> recordsWithoutContentId = changedRecords.stream().filter(record -> record.getContentId() == null).collect(Collectors.toList());
        Set<Long> contentPermSetIds = recordsWithoutContentId.stream().map(DenormalisedContentChangeLog::getContentPermissionSetId).collect(Collectors.toSet());
        Map<Long, Long> contentIdToPermissionSetIdMap = this.realContentAndPermissionsDao.getContentPermissionSetIdsForContentPermissionIds(contentPermSetIds);
        List recordsWithoutData = recordsWithoutContentId.stream().filter(record -> record.getContentPermissionSetId() == 0L).collect(Collectors.toList());
        if (recordsWithoutData.size() > 0) {
            log.warn("Found {} content change log record(s) without both content id and content permission set id. Will be skipped.", (Object)recordsWithoutData.size());
        }
        for (DenormalisedContentChangeLog recordWithoutContentId : recordsWithoutContentId) {
            Long pageId = this.getPageIdFromContentChangeLog(contentIdToPermissionSetIdMap, recordWithoutContentId);
            if (pageId == null) continue;
            pagesWithChangeLogRecords.compute(pageId, (pageIdNotNeededHere, denormalisedContentChangeLogs) -> {
                if (denormalisedContentChangeLogs == null) {
                    denormalisedContentChangeLogs = new ArrayList<DenormalisedContentChangeLog>();
                }
                denormalisedContentChangeLogs.add(recordWithoutContentId);
                return denormalisedContentChangeLogs;
            });
        }
        Set<Long> processedContentIds = this.denormalisedContentPermissionsUpdater.updateContentViewPermissions(pagesWithChangeLogRecords.keySet(), deadline);
        List<DenormalisedContentChangeLog> processedRecords = this.extractChangedChangeLogRecords(pagesWithChangeLogRecords, processedContentIds);
        recordsWithoutContentId.forEach(record -> {
            long contentPermissionSetId = record.getContentPermissionSetId();
            Long contentId = (Long)contentIdToPermissionSetIdMap.get(contentPermissionSetId);
            if (contentId == null || processedContentIds.contains(contentId)) {
                processedRecords.add((DenormalisedContentChangeLog)record);
            }
        });
        return processedRecords;
    }

    @Override
    protected void deleteAllRecordsFromChangeLogTable() {
        this.denormalisedContentChangeLogDao.removeAllContentChangeLogRecords();
    }

    @Override
    protected void deactivateTriggers() {
        this.denormalisedPermissionsDdlExecutor.deactivateContentTriggers();
    }

    @Override
    protected void activateTriggersInSeparateTransaction() {
        try {
            this.executor.submit(this.denormalisedPermissionsDdlExecutor::activateContentTriggers).get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected DenormalisedLockService.LockName getLockNameForStatus() {
        return DenormalisedLockService.LockName.CONTENT_STATUS;
    }

    @Override
    protected DenormalisedLockService.LockName getLockNameForLogProcessor() {
        return DenormalisedLockService.LockName.CONTENT_LOG_PROCESSOR;
    }

    @Override
    protected int updateAllRecordsInSmallSeparateTransactions() throws ExecutionException, InterruptedException {
        return this.denormalisedContentPermissionsUpdater.updateAllContentPermissions(this.schedulingEnabled, () -> this.denormalisedPermissionStateManager.getContentServiceState(true));
    }

    @Override
    protected DenormalisedServiceStateRecord.ServiceType getServiceType() {
        return SERVICE_TYPE;
    }

    private Long getPageIdFromContentChangeLog(Map<Long, Long> contentIdToPermissionSetIdMap, DenormalisedContentChangeLog recordWithoutContentId) {
        Long contentPermissionSetId = recordWithoutContentId.getContentPermissionSetId();
        if (contentPermissionSetId == null) {
            return null;
        }
        return contentIdToPermissionSetIdMap.get(contentPermissionSetId);
    }

    private List<DenormalisedContentChangeLog> extractChangedChangeLogRecords(Map<Long, List<DenormalisedContentChangeLog>> idsWithChangeLogRecords, Set<Long> processedIds) {
        return idsWithChangeLogRecords.entrySet().stream().filter(e -> processedIds.contains(e.getKey())).flatMap(e -> ((List)e.getValue()).stream()).collect(Collectors.toList());
    }
}

