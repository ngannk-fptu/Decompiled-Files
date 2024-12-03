/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.NotPermittedException;
import com.atlassian.confluence.event.events.admin.ZduStartEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreJobRunner;
import com.atlassian.confluence.impl.backuprestore.BackupRestorePermissionsHelper;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.events.TerminateBackupRestoreJobRequest;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ConfluenceBackupRestoreJobCanceller
implements DisposableBean,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceBackupRestoreJobCanceller.class);
    private final EventPublisher eventPublisher;
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final BackupRestorePermissionsHelper permissionsHelper;
    private final BackupRestoreJobRunner backupRestoreJobRunner;

    public ConfluenceBackupRestoreJobCanceller(EventPublisher eventPublisher, BackupRestoreJobDao backupRestoreJobDao, BackupRestorePermissionsHelper permissionsHelper, BackupRestoreJobRunner backupRestoreJobRunner) {
        this.eventPublisher = eventPublisher;
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.permissionsHelper = permissionsHelper;
        this.backupRestoreJobRunner = backupRestoreJobRunner;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(ClusterEventWrapper eventWrapper) {
        Event wrappedEvent = eventWrapper.getEvent();
        if (wrappedEvent instanceof TerminateBackupRestoreJobRequest) {
            TerminateBackupRestoreJobRequest terminateBackupRestoreJobRequest = (TerminateBackupRestoreJobRequest)wrappedEvent;
            this.backupRestoreJobRunner.terminateProcess(terminateBackupRestoreJobRequest.getJobId());
        }
    }

    @EventListener
    public void onZduStartEventListener(ZduStartEvent zduStartEvent) {
    }

    public Optional<BackupRestoreJob> cancelJob(BackupRestoreJob job, BackupRestoreSettings settings, String terminator) throws NotPermittedException {
        this.checkIfUserCanCancelJob(job, settings);
        this.cancelJob(job, terminator);
        return Optional.ofNullable(this.backupRestoreJobDao.getById(job.getId()));
    }

    private boolean cancelJob(BackupRestoreJob job, String terminator) {
        if (!job.getJobState().isCancellable()) {
            log.debug("Job can't be cancelled while it is in the state '{}'", (Object)job.getJobState());
            return false;
        }
        if (job.getJobState().equals((Object)JobState.QUEUED) && this.backupRestoreJobDao.cancelQueuedJobWithOptimisticLock(job.getId(), terminator)) {
            return true;
        }
        if (this.backupRestoreJobDao.cancelRunningJobWithOptimisticLock(job.getId(), terminator)) {
            this.terminateJob(job.getId());
            return true;
        }
        log.debug("Unable to cancel the job with id {} because it is not in the cancellable state.", (Object)job.getId());
        return false;
    }

    private void checkIfUserCanCancelJob(BackupRestoreJob job, BackupRestoreSettings publicBackupRestoreSettings) throws NotPermittedException {
        if (job.getJobScope() == JobScope.SITE || job.getJobScope() == JobScope.SPACE && job.getJobOperation() == JobOperation.RESTORE) {
            this.permissionsHelper.assertUserHasSystemAdminPermissions();
        } else if (job.getJobScope() == JobScope.SPACE && job.getJobOperation() == JobOperation.BACKUP) {
            this.permissionsHelper.assertUserCanCancelSpaceBackup(publicBackupRestoreSettings.getSpaceKeys());
        } else {
            throw new IllegalStateException(String.format("Unknown job scope %s or operation %s ", job.getJobScope(), job.getJobOperation()));
        }
    }

    private void terminateJob(Long jobId) {
        boolean localActiveProcessFound = this.backupRestoreJobRunner.terminateProcess(jobId);
        if (!localActiveProcessFound) {
            this.eventPublisher.publish((Object)new TerminateBackupRestoreJobRequest(this, jobId));
        }
    }

    public int cancelAllJobsFromQueue() {
        List<JobState> jobStates = Arrays.stream(JobState.values()).filter(JobState::isCancellable).collect(Collectors.toList());
        return this.cancelJobsWithState(jobStates);
    }

    private int cancelJobsWithState(List<JobState> jobStates) {
        BackupRestoreJobsSearchFilter filter = new BackupRestoreJobsSearchFilter.Builder(jobStates).build();
        List<BackupRestoreJob> jobs = this.backupRestoreJobDao.findJobs(filter);
        jobs.sort(Comparator.comparingLong(BackupRestoreJob::getId).reversed());
        int cancelledJobsCount = 0;
        for (BackupRestoreJob job : jobs) {
            if (!this.cancelJob(job, AuthenticatedUserThreadLocal.getUsername())) continue;
            ++cancelledJobsCount;
        }
        return cancelledJobsCount;
    }
}

