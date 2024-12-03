/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.statistics;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.converters.BackupRestoreJobResultToJsonConverter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.domain.BackupRestoreJobStatisticsRecord;
import com.atlassian.confluence.impl.backuprestore.hibernate.ArtificialHibernateEntity;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.ConfluenceLockerOnSiteRestore;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreInProgressEvent;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.statistics.JobStatisticsInfo;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsCollector
implements JobStatisticsInfo,
AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(StatisticsCollector.class);
    private static final long UPDATE_INTERVAL_MS = Long.getLong("confluence.backuprestore.statistics-collector.update-interval-ms", 3000L);
    private final AtomicLong lastUpdateTime = new AtomicLong();
    private final EventPublisher eventPublisher;
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final ParallelTasksExecutor parallelTasksExecutor;
    private final AtomicLong totalNumberOfObjects = new AtomicLong();
    private final AtomicLong persistedObjectsCounter = new AtomicLong();
    private final AtomicLong processedObjectsCounter = new AtomicLong();
    private final AtomicLong skippedObjectsCounter = new AtomicLong();
    private final AtomicLong reusedObjectsCounter = new AtomicLong();
    private final ReentrantLock lock = new ReentrantLock();
    private final Object mutex = new Object();
    private final long jobId;
    private final JobScope jobScope;
    private final JobOperation jobOperation;
    private final long startTime;
    private final BackupRestoreJobResultToJsonConverter backupRestoreJobResultToJsonConverter = new BackupRestoreJobResultToJsonConverter();
    private final Supplier<Long> currentTimeSupplier;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    public StatisticsCollector(long jobId, JobScope jobScope, JobOperation jobOperation, EventPublisher eventPublisher, BackupRestoreJobDao backupRestoreJobDao, ParallelTasksExecutor parallelTasksExecutor) {
        this(jobId, jobScope, jobOperation, eventPublisher, backupRestoreJobDao, parallelTasksExecutor, System::currentTimeMillis);
    }

    @VisibleForTesting
    StatisticsCollector(long jobId, JobScope jobScope, JobOperation jobOperation, EventPublisher eventPublisher, BackupRestoreJobDao backupRestoreJobDao, ParallelTasksExecutor parallelTasksExecutor, Supplier<Long> currentTimeSupplier) {
        this.jobId = jobId;
        this.jobScope = jobScope;
        this.jobOperation = jobOperation;
        this.startTime = currentTimeSupplier.get();
        this.eventPublisher = eventPublisher;
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.parallelTasksExecutor = parallelTasksExecutor;
        this.currentTimeSupplier = currentTimeSupplier;
        this.lastUpdateTime.set(currentTimeSupplier.get());
    }

    @Override
    public long getPersistedObjectsCount() {
        return this.persistedObjectsCounter.get();
    }

    @Override
    public long getSkippedObjectsCount() {
        return this.skippedObjectsCounter.get();
    }

    public long getReusedObjectsCount() {
        return this.reusedObjectsCounter.get();
    }

    private void createEmptyStatisticsRecord(long jobId) throws ExecutionException, InterruptedException {
        this.parallelTasksExecutor.runTaskAsync(() -> this.createAndSaveNewJobStatisticsRecord(jobId), "creating statistics record").get();
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getTotalNumberOfObjects() {
        return this.totalNumberOfObjects.get();
    }

    @Override
    public long getProcessedObjectsCounter() {
        return this.processedObjectsCounter.get();
    }

    @Override
    public void close() {
        if (this.isClosed.compareAndSet(false, true)) {
            this.flush();
            this.logWarnings();
        }
    }

    private void logWarnings() {
        if (this.skippedObjectsCounter.get() == 0L) {
            return;
        }
        log.warn("Restore process skipped {} objects. To mute the full list of skipped objects, change logging to WARN for class {}", (Object)this.skippedObjectsCounter.get(), (Object)this.getClass().getName());
    }

    public void onObjectPersisting(Collection<ImportedObjectV2> incomingPersistedObjects) {
        if ((incomingPersistedObjects = this.excludeArtificialObjects(incomingPersistedObjects)) != null && !incomingPersistedObjects.isEmpty()) {
            this.persistedObjectsCounter.addAndGet(incomingPersistedObjects.size());
            this.processedObjectsCounter.addAndGet(incomingPersistedObjects.size());
        }
        this.tryToUpdateStatisticsAsync();
    }

    private Collection<ImportedObjectV2> excludeArtificialObjects(Collection<ImportedObjectV2> importedObjects) {
        return importedObjects.stream().filter(importedObject -> !ArtificialHibernateEntity.class.isAssignableFrom(importedObject.getEntityClass())).collect(Collectors.toList());
    }

    public void onObjectReusing(Collection<ImportedObjectV2> importedObjects) {
        if (importedObjects != null && !importedObjects.isEmpty()) {
            this.reusedObjectsCounter.addAndGet(importedObjects.size());
            this.processedObjectsCounter.addAndGet(importedObjects.size());
        }
        this.tryToUpdateStatisticsAsync();
    }

    public void onObjectsExporting(Collection<EntityObjectReadyForExport> exportedEntities) {
        if (exportedEntities != null && !exportedEntities.isEmpty()) {
            this.persistedObjectsCounter.addAndGet(exportedEntities.size());
            this.processedObjectsCounter.addAndGet(exportedEntities.size());
        }
        this.tryToUpdateStatisticsAsync();
    }

    public void onObjectsSkipping(Collection<DbRawObjectData> exportedEntities, SkippedObjectsReason skippedObjectsReason, String errorMessage) {
        if (exportedEntities != null && !exportedEntities.isEmpty()) {
            this.skippedObjectsCounter.addAndGet(exportedEntities.size());
            this.processedObjectsCounter.addAndGet(exportedEntities.size());
            if (log.isInfoEnabled()) {
                exportedEntities.forEach(importedObject -> log.info("Object was skipped with a reason {} and message: {}. Object: {}", new Object[]{skippedObjectsReason, errorMessage, importedObject}));
            }
        }
        this.tryToUpdateStatisticsAsync();
    }

    public void onObjectSkipping(Collection<ImportedObjectV2> importedObjects, SkippedObjectsReason skippedObjectsReason) {
        if (importedObjects != null && !importedObjects.isEmpty()) {
            this.skippedObjectsCounter.addAndGet(importedObjects.size());
            this.processedObjectsCounter.addAndGet(importedObjects.size());
            if (log.isInfoEnabled()) {
                importedObjects.forEach(importedObject -> log.info("Object was skipped with a reason {}: {}", (Object)skippedObjectsReason, importedObject));
            }
        }
        this.tryToUpdateStatisticsAsync();
    }

    public void flush() {
        this.forceUpdateStatisticsAsync();
    }

    private void forceUpdateStatisticsAsync() {
        this.lastUpdateTime.set(this.currentTimeSupplier.get());
        this.updateStatisticsInTheSeparateThread();
    }

    private void tryToUpdateStatisticsAsync() {
        if (!this.lock.tryLock()) {
            return;
        }
        try {
            if (this.isTimeToUpdate()) {
                this.lastUpdateTime.set(this.currentTimeSupplier.get());
                this.updateStatisticsInTheSeparateThread();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private void sendSiteRestoreEventWithProgress() {
        RestoreInProgressEvent event = new RestoreInProgressEvent(this, this.jobScope, this.processedObjectsCounter.get(), this.totalNumberOfObjects.get(), ConfluenceLockerOnSiteRestore.isDatabaseLocked(), ConfluenceLockerOnSiteRestore.isDisplayJohnson());
        this.eventPublisher.publish((Object)event);
    }

    private boolean isTimeToUpdate() {
        return this.lastUpdateTime.get() + UPDATE_INTERVAL_MS < this.currentTimeSupplier.get();
    }

    private void updateStatisticsInTheSeparateThread() {
        this.parallelTasksExecutor.runTaskAsync(() -> {
            this.updateStatisticsInTheDatabaseSynchronously();
            if (JobScope.SITE.equals((Object)this.jobScope) && JobOperation.RESTORE.equals((Object)this.jobOperation)) {
                this.sendSiteRestoreEventWithProgress();
            }
            return null;
        }, "update statistics");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateStatisticsInTheDatabaseSynchronously() {
        Object object = this.mutex;
        synchronized (object) {
            BackupRestoreJobResult jobResult = new BackupRestoreJobResult();
            jobResult.setTotalObjectsCount(this.totalNumberOfObjects.get());
            jobResult.setPersistedObjectsCount(this.persistedObjectsCounter.get());
            jobResult.setSkippedObjectsCount(this.skippedObjectsCounter.get());
            jobResult.setProcessedObjectsCount(this.processedObjectsCounter.get());
            jobResult.setReusedObjectsCount(this.reusedObjectsCounter.get());
            String statisticsAsJson = this.backupRestoreJobResultToJsonConverter.apply(jobResult);
            this.backupRestoreJobDao.updateStatistics(this.jobId, statisticsAsJson);
        }
    }

    private BackupRestoreJobStatisticsRecord createAndSaveNewJobStatisticsRecord(long id) {
        BackupRestoreJobStatisticsRecord restoreJobStatisticsRecord = new BackupRestoreJobStatisticsRecord();
        restoreJobStatisticsRecord.setId(id);
        restoreJobStatisticsRecord.setStatistics("{}");
        this.backupRestoreJobDao.save(restoreJobStatisticsRecord);
        return restoreJobStatisticsRecord;
    }

    public void setTotalNumberOfObjects(Long totalNumberOfObjects) {
        this.totalNumberOfObjects.set(totalNumberOfObjects);
    }

    public void createEmptyStatisticsRecord() throws ExecutionException, InterruptedException {
        this.createEmptyStatisticsRecord(this.jobId);
    }
}

