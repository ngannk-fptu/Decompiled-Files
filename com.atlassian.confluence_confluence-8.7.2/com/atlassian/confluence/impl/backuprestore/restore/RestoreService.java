/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Stopwatch
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutorFactory;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfoFactory;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.BackupPropertiesValidator;
import com.atlassian.confluence.impl.backuprestore.restore.HiLoGeneratorInitialiserOnSiteRestore;
import com.atlassian.confluence.impl.backuprestore.restore.ImportedObjectsDispatcher;
import com.atlassian.confluence.impl.backuprestore.restore.ImportedObjectsDispatcherFactory;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReader;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReaderFactory;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.events.OnRestoreEventsSender;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSearchIndexer;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSearchIndexerFactory;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.ImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.ImportTaskRunnerFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollectorFactory;
import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.internal.spaces.persistence.SpaceDaoInternal;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Stopwatch;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreService {
    private static final Logger log = LoggerFactory.getLogger(RestoreService.class);
    private final EventPublisher eventPublisher;
    private final BackupContainerReaderFactory backupContainerReaderFactory;
    private final ImportedObjectsDispatcherFactory importedObjectsDispatcherFactory;
    private final ExportableEntityInfoFactory exportableEntityInfoFactory;
    private final SessionFactory sessionFactory;
    private final OnRestoreEventsSender onRestoreEventsSender;
    private final ImportTaskRunnerFactory importTaskRunnerFactory;
    private final BackupPropertiesValidator backupPropertiesValidator;
    private final OnRestoreSearchIndexerFactory onRestoreSearchIndexerFactory;
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final ReentrantLock restoreLock = new ReentrantLock();
    private final RestoreDao restoreDao;
    private final SpaceDaoInternal spaceDaoInternal;
    private final CacheFlusher cacheFlusher;
    private final HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore;
    private final StatisticsCollectorFactory statisticsCollectorFactory;
    private final ParallelTasksExecutorFactory parallelTasksExecutorFactory;

    public RestoreService(SessionFactory sessionFactory, BackupContainerReaderFactory backupContainerReaderFactory, ImportedObjectsDispatcherFactory importedObjectsDispatcherFactory, OnRestoreEventsSender onRestoreEventsSender, ExportableEntityInfoFactory exportableEntityInfoFactory, ImportTaskRunnerFactory importTaskRunnerFactory, BackupPropertiesValidator backupPropertiesValidator, OnRestoreSearchIndexerFactory onRestoreSearchIndexerFactory, RestoreDao restoreDao, BackupRestoreJobDao backupRestoreJobDao, SpaceDaoInternal spaceDaoInternal, CacheFlusher cacheFlusher, EventPublisher eventPublisher, HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore, StatisticsCollectorFactory statisticsCollectorFactory, ParallelTasksExecutorFactory parallelTasksExecutorFactory) {
        this.eventPublisher = eventPublisher;
        this.backupContainerReaderFactory = backupContainerReaderFactory;
        this.onRestoreEventsSender = onRestoreEventsSender;
        this.importedObjectsDispatcherFactory = importedObjectsDispatcherFactory;
        this.exportableEntityInfoFactory = exportableEntityInfoFactory;
        this.sessionFactory = sessionFactory;
        this.importTaskRunnerFactory = importTaskRunnerFactory;
        this.backupPropertiesValidator = backupPropertiesValidator;
        this.onRestoreSearchIndexerFactory = onRestoreSearchIndexerFactory;
        this.restoreDao = restoreDao;
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.spaceDaoInternal = spaceDaoInternal;
        this.cacheFlusher = cacheFlusher;
        this.hiLoGeneratorInitialiserOnSiteRestore = hiLoGeneratorInitialiserOnSiteRestore;
        this.statisticsCollectorFactory = statisticsCollectorFactory;
        this.parallelTasksExecutorFactory = parallelTasksExecutorFactory;
    }

    public void doRestore(BackupRestoreJob job, BackupRestoreSettings settings, int threadsNumber) throws BackupRestoreException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        JobScope restoreJobScope = job.getJobScope();
        Long restoreJobId = job.getId();
        log.info("{} restore [{}] has started.", (Object)restoreJobScope, (Object)restoreJobId);
        String fileName = settings.getFileName();
        if (StringUtils.isEmpty((CharSequence)fileName)) {
            throw new BackupRestoreException("File name can't be empty");
        }
        this.restoreLock.lock();
        try (ParallelTasksExecutor parallelTasksExecutor = this.parallelTasksExecutorFactory.create(job, threadsNumber);
             ImportTaskRunner importTaskRunner = this.importTaskRunnerFactory.createImportTaskRunner(job, settings, this.hiLoGeneratorInitialiserOnSiteRestore);){
            try {
                HibernateMetadataHelper hibernateMetadataHelper = new HibernateMetadataHelper(this.exportableEntityInfoFactory, this.sessionFactory, false);
                OnRestoreSearchIndexer searchIndexer = this.onRestoreSearchIndexerFactory.createOnRestoreSearchIndexer(restoreJobScope, parallelTasksExecutor);
                this.readAllObjectsAndRestore(job, settings, stopwatch, restoreJobScope, restoreJobId, parallelTasksExecutor, importTaskRunner, hibernateMetadataHelper, searchIndexer);
            }
            catch (Exception e) {
                log.debug("Start interrupting all tasks");
                parallelTasksExecutor.interruptAllJobs();
                throw e;
            }
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            this.onRestoreEventsSender.sendFailureEvent(job, e.getMessage());
            throw new BackupRestoreException(e);
        }
        finally {
            this.restoreLock.unlock();
            log.info("{} restore [{}] completed", (Object)job.getJobScope(), (Object)job.getId());
        }
    }

    private void readAllObjectsAndRestore(BackupRestoreJob job, BackupRestoreSettings settings, Stopwatch stopwatch, JobScope restoreJobScope, Long restoreJobId, ParallelTasksExecutor parallelTasksExecutor, ImportTaskRunner importTaskRunner, HibernateMetadataHelper hibernateMetadataHelper, OnRestoreSearchIndexer searchIndexer) throws BackupRestoreException, ExecutionException, InterruptedException, TimeoutException {
        try (BackupContainerReader containerReader = this.backupContainerReaderFactory.createBackupContainerReader(new File(settings.getFilePath()));){
            StatisticsCollector jobStatisticsInfo;
            BackupProperties backupProperties = this.backupPropertiesValidator.validatePropertiesAgainstBackupJob(job, containerReader.getBackupProperties());
            BackupRestoreJob validatedJob = this.validateSpaceKeys(job, backupProperties, parallelTasksExecutor);
            log.info("{} restore [{}] setup and validation completed.", (Object)restoreJobScope, (Object)restoreJobId);
            this.onRestoreEventsSender.sendStartEvents(validatedJob, settings, backupProperties.getSpaceKeys(), containerReader.getLegacyBackupProperties());
            log.info("{} restore [{}] running pre-import tasks.", (Object)restoreJobScope, (Object)restoreJobId);
            importTaskRunner.runPreImportTasks();
            log.info("{} restore [{}] pre-import tasks completed.", (Object)restoreJobScope, (Object)restoreJobId);
            try (StatisticsCollector statisticsCollector = this.statisticsCollectorFactory.createStatisticsCollector(validatedJob.getId(), validatedJob.getJobScope(), validatedJob.getJobOperation(), this.eventPublisher, this.backupRestoreJobDao, parallelTasksExecutor);){
                statisticsCollector.createEmptyStatisticsRecord();
                RestoreService.retrieveTotalObjectsFromBackupDescriptorAndUpdateStatistics(backupProperties, statisticsCollector);
                ImportedObjectsDispatcher importedObjectsDispatcher = this.importedObjectsDispatcherFactory.createImportedObjectsDispatcher(validatedJob, backupProperties.getJobSource(), JobScope.SPACE.equals((Object)restoreJobScope) ? Optional.of(new HashSet<String>(backupProperties.getSpaceKeys())) : Optional.empty(), settings.getFilePath(), parallelTasksExecutor, hibernateMetadataHelper, new OnObjectsProcessingHandlerImpl(searchIndexer, statisticsCollector), backupProperties.getBackupAttachments());
                this.readInputObjectsAndUpdateNextHiValueSynchronously(validatedJob, parallelTasksExecutor, statisticsCollector, containerReader, importedObjectsDispatcher);
                log.info("{} restore [{}] processing remaining gathered objects.", (Object)restoreJobScope, (Object)restoreJobId);
                RestoreService.processDataFromAllStashes(parallelTasksExecutor, importedObjectsDispatcher);
                importedObjectsDispatcher.runDeferredOperations();
                parallelTasksExecutor.waitUntilAllGlobalJobsComplete();
                BackupRestoreJob jobToUpdate = this.backupRestoreJobDao.getById(job.getId());
                jobToUpdate.setJobState(JobState.COMPLETING);
                this.backupRestoreJobDao.updateInNewTransaction(jobToUpdate);
                log.info("{} restore [{}] processing plugin data sync.", (Object)restoreJobScope, (Object)restoreJobId);
                parallelTasksExecutor.runGlobalTaskAsync(() -> {
                    this.readPluginData(validatedJob.getJobScope(), containerReader);
                    return null;
                }, "importing plugin data");
                parallelTasksExecutor.waitUntilAllGlobalJobsComplete();
                log.info("{} restore [{}] running post-import tasks.", (Object)restoreJobScope, (Object)restoreJobId);
                importTaskRunner.runPostImportTasks(validatedJob, settings, backupProperties);
                if (!settings.isSkipReindex()) {
                    searchIndexer.flush();
                }
                jobStatisticsInfo = statisticsCollector;
            }
            parallelTasksExecutor.waitUntilAllGlobalJobsComplete();
            Thread.sleep(1000L);
            this.cacheFlusher.flushCaches();
            this.onRestoreEventsSender.sendFinishEvents(validatedJob, settings, backupProperties.getSpaceKeys(), containerReader.getLegacyBackupProperties());
            log.info("{} restore [{}] ended in {}. Processed {} objects, accepted {} of them.", new Object[]{restoreJobScope, restoreJobId, stopwatch, jobStatisticsInfo.getProcessedObjectsCounter(), jobStatisticsInfo.getPersistedObjectsCount()});
        }
    }

    private BackupRestoreJob validateSpaceKeys(BackupRestoreJob job, BackupProperties backupProperties, ParallelTasksExecutor parallelTasksExecutor) throws BackupRestoreException, ExecutionException, InterruptedException {
        if (job.getJobScope().equals((Object)JobScope.SPACE)) {
            Collection<String> spaceKeysCollection = backupProperties.getSpaceKeys();
            this.checkThatRestoredSpacesDoNotExistInTheDatabase(spaceKeysCollection);
            Callable<BackupRestoreJob> task = () -> {
                BackupRestoreJob currentJob = this.backupRestoreJobDao.getById(job.getId());
                currentJob.addSpaceKeys(spaceKeysCollection);
                this.backupRestoreJobDao.update(currentJob);
                return currentJob;
            };
            return parallelTasksExecutor.runTaskAsync(task, "Update space keys").get();
        }
        return job;
    }

    private void readInputObjectsAndUpdateNextHiValueSynchronously(BackupRestoreJob job, ParallelTasksExecutor parallelTasksExecutor, StatisticsCollector statisticsCollector, BackupContainerReader containerReader, ImportedObjectsDispatcher importedObjectsDispatcher) throws BackupRestoreException, ExecutionException, InterruptedException, TimeoutException {
        AtomicLong readObjectCounter = new AtomicLong();
        log.info("{} restore [{}] starting reading all XML objects from {}.", new Object[]{job.getJobScope(), job.getId(), job.getFileName()});
        containerReader.readObjects(importedObject -> {
            if (importedObjectsDispatcher.processIncomingImportedObject((ImportedObjectV2)importedObject)) {
                this.hiLoGeneratorInitialiserOnSiteRestore.registerNewId(importedObject.getId());
            } else {
                statisticsCollector.onObjectSkipping(Collections.singleton(importedObject), SkippedObjectsReason.PERSISTER_NOT_FOUND);
            }
            readObjectCounter.incrementAndGet();
        });
        parallelTasksExecutor.waitUntilAllStageJobsComplete();
        if (JobScope.SITE.equals((Object)job.getJobScope())) {
            this.hiLoGeneratorInitialiserOnSiteRestore.updateHiLoIdGenerator();
            this.onRestoreEventsSender.sendUnlockDatabaseEvent();
        }
        this.cacheFlusher.flushCaches();
        statisticsCollector.setTotalNumberOfObjects(readObjectCounter.get());
        log.info("{} restore [{}] finished reading all XML objects from {}.", new Object[]{job.getJobScope(), job.getId(), job.getFileName()});
    }

    private void checkThatRestoredSpacesDoNotExistInTheDatabase(Collection<String> spaceKeys) throws BackupRestoreException {
        List existingSpaceKeys = spaceKeys.stream().map(this.spaceDaoInternal::getSpace).filter(Objects::nonNull).map(Space::getKey).collect(Collectors.toList());
        if (!existingSpaceKeys.isEmpty()) {
            String message = "Space(s) with the keys '%s' already exist. Please delete them before restoring a space with the same key(s).";
            throw new BackupRestoreException(String.format("Space(s) with the keys '%s' already exist. Please delete them before restoring a space with the same key(s).", String.join((CharSequence)", ", existingSpaceKeys)));
        }
    }

    private static void processDataFromAllStashes(ParallelTasksExecutor parallelTasksExecutor, ImportedObjectsDispatcher importedObjectsDispatcher) throws BackupRestoreException, ExecutionException, InterruptedException, TimeoutException {
        int phaseCounter = 0;
        while (importedObjectsDispatcher.processNextStashPhase()) {
            parallelTasksExecutor.waitUntilAllStageJobsComplete();
            log.debug("The current phase {} has been finished", (Object)phaseCounter);
        }
        log.debug("All stashes have been processed in {} phases.", (Object)phaseCounter);
    }

    private static void retrieveTotalObjectsFromBackupDescriptorAndUpdateStatistics(BackupProperties backupProperties, StatisticsCollector statisticsCollector) {
        backupProperties.getTotalNumberOfObjects().ifPresent(statisticsCollector::setTotalNumberOfObjects);
    }

    private void readPluginData(JobScope jobScope, BackupContainerReader containerReader) {
        if (JobScope.SPACE.equals((Object)jobScope)) {
            return;
        }
        this.restoreDao.doInTransaction(tx -> {
            try {
                containerReader.readPluginModuleData();
                log.info("Finished processing plugin data");
            }
            catch (BackupRestoreException e) {
                throw new RuntimeException("Unable to restore plugin data: " + e.getMessage(), e);
            }
            return null;
        });
    }

    @VisibleForTesting
    static class OnObjectsProcessingHandlerImpl
    implements OnObjectsProcessingHandler {
        private final OnRestoreSearchIndexer onRestoreSearchIndexer;
        private final StatisticsCollector statisticsCollector;

        OnObjectsProcessingHandlerImpl(OnRestoreSearchIndexer onRestoreSearchIndexer, StatisticsCollector statisticsCollector) {
            this.onRestoreSearchIndexer = onRestoreSearchIndexer;
            this.statisticsCollector = statisticsCollector;
        }

        @Override
        public void onObjectsPersist(Collection<ImportedObjectV2> persistedObjects) throws BackupRestoreException {
            this.onRestoreSearchIndexer.onObjectsPersisting(persistedObjects);
            this.statisticsCollector.onObjectPersisting(persistedObjects);
        }

        @Override
        public void onObjectsSkipping(Collection<ImportedObjectV2> skippedObjects, SkippedObjectsReason reason) {
            this.statisticsCollector.onObjectSkipping(skippedObjects, reason);
        }

        @Override
        public void onObjectsReusing(Collection<ImportedObjectV2> reusedObjects) {
            this.statisticsCollector.onObjectReusing(reusedObjects);
        }
    }
}

