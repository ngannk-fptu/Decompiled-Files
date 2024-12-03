/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.backuprestore.BackupRestoreManager;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.backuprestore.exception.NotPermittedException;
import com.atlassian.confluence.backuprestore.exception.TheSameSpaceBackupRestoreJobAlreadyInProgressException;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreFilesystemManager;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreJobRunner;
import com.atlassian.confluence.impl.backuprestore.BackupRestorePermissionsHelper;
import com.atlassian.confluence.impl.backuprestore.ConfluenceBackupRestoreJobCanceller;
import com.atlassian.confluence.impl.backuprestore.IndependentBackupRestoreJobManager;
import com.atlassian.confluence.impl.backuprestore.analytics.BackupRestoreFinishJobAnalyticsEvent;
import com.atlassian.confluence.impl.backuprestore.analytics.BackupRestoreStartJobAnalyticsEvent;
import com.atlassian.confluence.impl.backuprestore.converters.JsonToBackupRestoreJobResultConverter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.domain.BackupRestoreJobStatisticsRecord;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ThreadFactories;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceBackupRestoreManager
implements BackupRestoreManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceBackupRestoreManager.class);
    private static final String SPACE_BACKUP_RESTORE_JOB_ALREADY_IN_PROGRESS = "backup-restore.the-same-space-backup-restore-already-in-progress";
    static final String JOBS_BACKUP_RESTORE_CLUSTER_LOCK_NAME = "jobs_backup_restore";
    private final BackupRestoreJobRunner backupRestoreJobRunner;
    private final BackupRestorePermissionsHelper permissionsHelper;
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final ClusterLockService clusterLockService;
    private final Supplier<Instant> currentTimeSupplier;
    private final ConfluenceBackupRestoreJobCanceller confluenceBackupRestoreJobCanceller;
    private final ExecutorService executorService;
    private final ZduManager zduManager;
    private final EventPublisher eventPublisher;
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;
    private final IndependentBackupRestoreJobManager independentBackupRestoreJobManager;
    private final JsonToBackupRestoreJobResultConverter jsonToBackupRestoreJobResultConverter = new JsonToBackupRestoreJobResultConverter();

    public ConfluenceBackupRestoreManager(BackupRestoreJobRunner backupRestoreJobRunner, BackupRestorePermissionsHelper permissionsHelper, BackupRestoreJobDao backupRestoreJobDao, ClusterLockService clusterLockService, ConfluenceBackupRestoreJobCanceller confluenceBackupRestoreJobCanceller, EventPublisher eventPublisher, BackupRestoreFilesystemManager backupRestoreFilesystemManager, ZduManager zduManager, IndependentBackupRestoreJobManager independentBackupRestoreJobManager) {
        this(Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)ConfluenceBackupRestoreManager.class.getSimpleName())), Instant::now, backupRestoreJobRunner, permissionsHelper, backupRestoreJobDao, clusterLockService, confluenceBackupRestoreJobCanceller, eventPublisher, backupRestoreFilesystemManager, zduManager, independentBackupRestoreJobManager);
    }

    @VisibleForTesting
    ConfluenceBackupRestoreManager(ExecutorService executorService, Supplier<Instant> currentTimeSupplier, BackupRestoreJobRunner backupRestoreJobRunner, BackupRestorePermissionsHelper permissionsHelper, BackupRestoreJobDao backupRestoreJobDao, ClusterLockService clusterLockService, ConfluenceBackupRestoreJobCanceller confluenceBackupRestoreJobCanceller, EventPublisher eventPublisher, BackupRestoreFilesystemManager backupRestoreFilesystemManager, ZduManager zduManager, IndependentBackupRestoreJobManager independentBackupRestoreJobManager) {
        this.executorService = executorService;
        this.backupRestoreJobRunner = backupRestoreJobRunner;
        this.permissionsHelper = permissionsHelper;
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.clusterLockService = clusterLockService;
        this.confluenceBackupRestoreJobCanceller = confluenceBackupRestoreJobCanceller;
        this.currentTimeSupplier = currentTimeSupplier;
        this.eventPublisher = eventPublisher;
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
        this.zduManager = zduManager;
        this.independentBackupRestoreJobManager = independentBackupRestoreJobManager;
    }

    @Override
    public synchronized BackupRestoreJob startSiteBackup(BackupRestoreSettings siteBackupSettings) throws NotPermittedException {
        return this.startSiteBackupOrRestore(siteBackupSettings);
    }

    @Override
    public synchronized BackupRestoreJob startSiteRestore(BackupRestoreSettings siteRestoreSettings) throws NotPermittedException {
        if (!new File(siteRestoreSettings.getFilePath()).exists()) {
            throw new IllegalArgumentException(String.format("File %s cannot be found", siteRestoreSettings.getFileName()));
        }
        return this.startSiteBackupOrRestore(siteRestoreSettings);
    }

    private synchronized BackupRestoreJob startSiteBackupOrRestore(BackupRestoreSettings siteSettings) throws NotPermittedException {
        this.permissionsHelper.assertUserHasSystemAdminPermissions();
        this.assertJobType((Enum<?>)JobScope.SITE, (Enum<?>)siteSettings.getJobScope());
        if (siteSettings.getJobOperation() == JobOperation.RESTORE) {
            this.cancelAllJobsFromQueue();
        }
        BackupRestoreJob job = this.createAndSaveNewJob(siteSettings.getJobOperation(), JobScope.SITE, JobState.QUEUED, siteSettings);
        this.createAndSaveNewJobSettingsRecord(job.getId(), siteSettings);
        this.triggerJobProcessingAsync();
        return job;
    }

    @Override
    public synchronized BackupRestoreJob startSpaceRestore(BackupRestoreSettings spaceRestoreSettings) throws NotPermittedException {
        this.permissionsHelper.assertUserHasSystemAdminPermissions();
        if (!new File(spaceRestoreSettings.getFilePath()).exists()) {
            throw new IllegalArgumentException(String.format("File %s cannot be found", spaceRestoreSettings.getFileName()));
        }
        this.assertJobType((Enum<?>)JobOperation.RESTORE, (Enum<?>)spaceRestoreSettings.getJobOperation());
        this.assertJobType((Enum<?>)JobScope.SPACE, (Enum<?>)spaceRestoreSettings.getJobScope());
        BackupRestoreJob job = this.createAndSaveNewJob(JobOperation.RESTORE, JobScope.SPACE, JobState.QUEUED, spaceRestoreSettings);
        this.createAndSaveNewJobSettingsRecord(job.getId(), spaceRestoreSettings);
        this.triggerJobProcessingAsync();
        return job;
    }

    @Override
    public Optional<BackupRestoreJob> getJob(Long jobId) throws NotPermittedException {
        BackupRestoreJob job = this.backupRestoreJobDao.getById(jobId);
        if (job == null) {
            return Optional.empty();
        }
        BackupRestoreSettings publicBackupRestoreSettings = this.getSettingsById(jobId);
        if (job.getJobOperation() == JobOperation.BACKUP && job.getJobScope() == JobScope.SPACE) {
            this.permissionsHelper.assertUserCanBackupSpaces(publicBackupRestoreSettings.getSpaceKeys());
        } else {
            this.permissionsHelper.assertUserHasSystemAdminPermissions();
        }
        return Optional.of(job);
    }

    @Override
    public synchronized BackupRestoreJob startSpaceBackup(BackupRestoreSettings spaceBackupSettings) throws NotPermittedException, TheSameSpaceBackupRestoreJobAlreadyInProgressException, IllegalArgumentException {
        this.assertJobType((Enum<?>)JobOperation.BACKUP, (Enum<?>)spaceBackupSettings.getJobOperation());
        this.assertJobType((Enum<?>)JobScope.SPACE, (Enum<?>)spaceBackupSettings.getJobScope());
        this.permissionsHelper.assertUserCanBackupSpaces(spaceBackupSettings.getSpaceKeys());
        this.checkTheSpaceBackupsAreNotInProgressOrQueued(spaceBackupSettings.getSpaceKeys());
        BackupRestoreJob job = this.createAndSaveNewJob(JobOperation.BACKUP, JobScope.SPACE, JobState.QUEUED, spaceBackupSettings);
        this.createAndSaveNewJobSettingsRecord(job.getId(), spaceBackupSettings);
        this.triggerJobProcessingAsync();
        return job;
    }

    private void triggerJobProcessingAsync() {
        this.executorService.submit(this::processJobsFromTheQueue);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void processJobsFromTheQueue() {
        ClusterLock lock = this.clusterLockService.getLockForName(JOBS_BACKUP_RESTORE_CLUSTER_LOCK_NAME);
        if (lock != null && !lock.tryLock()) {
            log.debug("Job processing was not triggered because the cluster lock was not acquired");
            return;
        }
        try {
            while (true) {
                if (this.zduManager.getUpgradeStatus().getState().equals((Object)ZduStatus.State.ENABLED)) {
                    return;
                }
                this.repairJobsWithInvalidStatus();
                BackupRestoreJob job = this.backupRestoreJobDao.getNextJobForProcessing();
                if (job == null) {
                    return;
                }
                log.debug("Found job for processing: {}", (Object)job);
                this.runJobAndUpdateJobProperties(job, this.getSettingsById(job.getId()));
                continue;
                break;
            }
        }
        catch (Exception e) {
            log.error("Something went wrong during the backup/restore jobs processing: " + e.getMessage(), (Throwable)e);
            return;
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private void runJobAndUpdateJobProperties(BackupRestoreJob job, BackupRestoreSettings backupRestoreSettings) throws BackupRestoreException {
        BackupRestoreJob finalJob;
        this.eventPublisher.publish((Object)new BackupRestoreStartJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation()));
        try {
            Optional<BackupRestoreJob> processingJob = this.tryChangeStateToProcessing(job.getId());
            if (processingJob.isEmpty()) {
                log.warn("Couldn't change job state to PROCESSING as it's state have already changed from QUEUED. Job: {}", (Object)job);
                return;
            }
            this.backupRestoreJobRunner.runJobSynchronously(processingJob.get(), backupRestoreSettings);
            finalJob = this.updateJobStateAfterCompletion(job.getId(), JobState.FINISHED, null);
        }
        catch (InterruptedException e) {
            finalJob = this.updateJobStateAfterCompletion(job.getId(), JobState.CANCELLED, null);
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            finalJob = this.updateJobStateAfterCompletion(job.getId(), JobState.FAILED, e);
        }
        this.sendFinishAnalyticsEvents(finalJob, backupRestoreSettings);
    }

    private void sendFinishAnalyticsEvents(BackupRestoreJob job, BackupRestoreSettings backupRestoreSettings) {
        Long backupZipSize;
        BackupRestoreJobResult jobStatistics = this.getStatisticsById(job.getId()).orElse(null);
        Long jobTotalObjects = jobStatistics != null ? Long.valueOf(jobStatistics.getTotalObjectsCount()) : null;
        Long l = backupZipSize = JobOperation.BACKUP.equals((Object)job.getJobOperation()) ? this.backupRestoreFilesystemManager.getFileSize(job.getFileName(), job.getJobScope()) : null;
        Boolean attachmentsIncluded = JobOperation.BACKUP.equals((Object)job.getJobOperation()) ? Boolean.valueOf(!backupRestoreSettings.isSkipAttachments()) : null;
        this.eventPublisher.publish((Object)new BackupRestoreFinishJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), job.getJobState(), jobTotalObjects, backupZipSize, job.getTotalTimeElapsed(job.getFinishProcessingTime()), attachmentsIncluded));
    }

    private BackupRestoreJob updateJobStateAfterCompletion(long jobId, JobState probableNextJobState, Exception exception) throws BackupRestoreException {
        JobState nextJobState = probableNextJobState;
        BackupRestoreJob job = this.backupRestoreJobDao.getById(jobId);
        if (probableNextJobState.equals((Object)JobState.FAILED)) {
            if (job.getJobState().equals((Object)JobState.CANCELLING)) {
                log.info("Backup restore job {} cancelled", (Object)job);
                nextJobState = JobState.CANCELLED;
            } else {
                String errorMessage = exception.getMessage();
                log.warn("Backup restore job {} failed: {}", new Object[]{job, errorMessage, exception});
                job.setErrorMessage(this.truncateMessage(errorMessage, 1000));
            }
        }
        job.setFinishProcessingTime(this.getCurrentTime());
        job.setJobState(nextJobState);
        try {
            this.updateJobStateInSeparateTransaction(job);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return job;
    }

    private String truncateMessage(String errorMessage, int maxLength) {
        if (errorMessage.length() <= maxLength) {
            return errorMessage;
        }
        return errorMessage.substring(0, maxLength - 3) + "...";
    }

    private Optional<BackupRestoreJob> tryChangeStateToProcessing(long jobId) throws BackupRestoreException, InterruptedException {
        try {
            Callable<Optional> task = () -> {
                if (!this.backupRestoreJobDao.startProcessingJobWithOptimisticLock(jobId)) {
                    return Optional.empty();
                }
                BackupRestoreJob job = this.backupRestoreJobDao.getById(jobId);
                return Optional.of(job);
            };
            return this.executorService.submit(task).get();
        }
        catch (ExecutionException e) {
            throw new BackupRestoreException(e);
        }
    }

    private void updateJobStateInSeparateTransaction(BackupRestoreJob job) throws BackupRestoreException, InterruptedException {
        try {
            this.executorService.submit(() -> this.backupRestoreJobDao.update(job)).get();
        }
        catch (ExecutionException e) {
            throw new BackupRestoreException(e);
        }
    }

    private void repairJobsWithInvalidStatus() throws InterruptedException, BackupRestoreException {
        BackupRestoreJob job;
        while ((job = this.backupRestoreJobDao.getNextActiveJob()) != null) {
            log.debug("Found job in invalid state: {}", (Object)job);
            job.setErrorMessage(String.format("Job has not been completed and it is now in a wrong state: %s. Marking as FAILED", job.getJobState()));
            job.setJobState(JobState.FAILED);
            job.setFinishProcessingTime(this.getCurrentTime());
            this.updateJobStateInSeparateTransaction(job);
        }
        return;
    }

    @Override
    public synchronized Optional<BackupRestoreJob> cancelJob(Long jobId) throws NotPermittedException {
        BackupRestoreJob jobToCancel = this.backupRestoreJobDao.getById(jobId);
        if (jobToCancel == null) {
            return Optional.empty();
        }
        return this.confluenceBackupRestoreJobCanceller.cancelJob(jobToCancel, this.getSettingsById(jobId), AuthenticatedUserThreadLocal.getUsername());
    }

    @Override
    public int cancelAllJobsFromQueue() throws NotPermittedException {
        this.permissionsHelper.assertUserHasSystemAdminPermissions();
        return this.confluenceBackupRestoreJobCanceller.cancelAllJobsFromQueue();
    }

    @Override
    public Collection<BackupRestoreJob> findJobs(BackupRestoreJobsSearchFilter backupRestoreJobsSearchFilter) {
        List<BackupRestoreJob> foundJobs = this.backupRestoreJobDao.findJobs(backupRestoreJobsSearchFilter);
        if (foundJobs.isEmpty()) {
            return Collections.emptyList();
        }
        if (this.permissionsHelper.hasSysadminPermissions()) {
            return foundJobs;
        }
        ArrayList<BackupRestoreJob> permittedJobs = new ArrayList<BackupRestoreJob>();
        for (BackupRestoreJob job : foundJobs) {
            if (job.getJobScope() != JobScope.SPACE || job.getJobOperation() != JobOperation.BACKUP || !this.permissionsHelper.hasPermissionToBackupSpaces(this.getSettingsById(job.getId()).getSpaceKeys())) continue;
            permittedJobs.add(job);
        }
        return permittedJobs;
    }

    @Override
    public void assertUserHasSystemAdminPermissions() throws NotPermittedException {
        this.permissionsHelper.assertUserHasSystemAdminPermissions();
    }

    private void checkTheSpaceBackupIsNotInProgressOrQueued(String spaceKey) throws TheSameSpaceBackupRestoreJobAlreadyInProgressException {
        BackupRestoreJobsSearchFilter.Builder filterBuilder = new BackupRestoreJobsSearchFilter.Builder(List.of(JobState.PROCESSING, JobState.QUEUED));
        filterBuilder.bySpaceKey(spaceKey);
        List<BackupRestoreJob> spaceJobs = this.backupRestoreJobDao.findJobs(filterBuilder.build());
        if (!spaceJobs.isEmpty()) {
            throw new TheSameSpaceBackupRestoreJobAlreadyInProgressException(SPACE_BACKUP_RESTORE_JOB_ALREADY_IN_PROGRESS);
        }
    }

    private void checkTheSpaceBackupsAreNotInProgressOrQueued(Set<String> spaceKeys) throws TheSameSpaceBackupRestoreJobAlreadyInProgressException {
        if (spaceKeys.size() == 1) {
            this.checkTheSpaceBackupIsNotInProgressOrQueued(spaceKeys.iterator().next());
        }
    }

    private void assertJobType(Enum<?> expected, Enum<?> real) {
        if (expected != real) {
            throw new IllegalArgumentException("Invalid job property. Expected " + expected + " but provided " + real);
        }
    }

    private BackupRestoreJob createAndSaveNewJob(JobOperation jobOperation, JobScope jobScope, JobState jobState, BackupRestoreSettings backupRestoreSettings) {
        return this.independentBackupRestoreJobManager.createAndSaveNewJob(this.executorService, jobOperation, jobScope, jobState, this.getCurrentTime(), AuthenticatedUserThreadLocal.getUsername(), backupRestoreSettings);
    }

    private BackupRestoreSettings createAndSaveNewJobSettingsRecord(long jobId, BackupRestoreSettings backupRestoreSettings) {
        return this.independentBackupRestoreJobManager.createAndSaveNewJobSettingsRecord(this.executorService, jobId, backupRestoreSettings);
    }

    private Instant getCurrentTime() {
        return this.currentTimeSupplier.get();
    }

    @Override
    public BackupRestoreSettings getSettingsById(long jobId) {
        return this.independentBackupRestoreJobManager.getSettingsById(this.executorService, jobId);
    }

    @Override
    public Optional<BackupRestoreJobResult> getStatisticsById(long jobId) {
        BackupRestoreJobStatisticsRecord statisticsRecord = this.backupRestoreJobDao.getStatisticsById(jobId);
        if (statisticsRecord == null) {
            return Optional.empty();
        }
        return Optional.of(this.jsonToBackupRestoreJobResultConverter.apply(statisticsRecord.getStatistics()));
    }
}

