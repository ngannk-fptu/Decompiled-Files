/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.FileInfo
 *  com.atlassian.confluence.api.model.backuprestore.JobDetails
 *  com.atlassian.confluence.api.model.backuprestore.JobFilter
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings
 *  com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings
 *  com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings
 *  com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings
 *  com.atlassian.confluence.api.service.backuprestore.BackupRestoreService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.FileInfo;
import com.atlassian.confluence.api.model.backuprestore.JobDetails;
import com.atlassian.confluence.api.model.backuprestore.JobFilter;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings;
import com.atlassian.confluence.api.service.backuprestore.BackupRestoreService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.atlassian.confluence.backuprestore.BackupRestoreManager;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.NotPermittedException;
import com.atlassian.confluence.backuprestore.exception.TheSameSpaceBackupRestoreJobAlreadyInProgressException;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreFilesystemManager;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreJobConverter;
import com.atlassian.confluence.impl.backuprestore.analytics.BackupRestoreAddJobAnalyticsEvent;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBackupRestoreService
implements BackupRestoreService {
    private static final Logger log = LoggerFactory.getLogger(DefaultBackupRestoreService.class);
    private final BackupRestoreManager manager;
    private final BackupRestoreJobConverter jobConverter;
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;
    private final EventPublisher eventPublisher;

    public DefaultBackupRestoreService(BackupRestoreManager manager, BackupRestoreJobConverter jobConverter, BackupRestoreFilesystemManager backupRestoreFilesystemManager, EventPublisher eventPublisher) {
        this.manager = manager;
        this.jobConverter = jobConverter;
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
        this.eventPublisher = eventPublisher;
    }

    public SpaceBackupJobDetails createSpaceBackupJob(SpaceBackupSettings settings) {
        this.validate(settings);
        try {
            BackupRestoreJob job = this.manager.startSpaceBackup(this.jobConverter.convertFromSpaceBackupSettings(settings));
            this.eventPublisher.publish((Object)new BackupRestoreAddJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), null));
            BackupRestoreSettings internalSettings = this.manager.getSettingsById(job.getId());
            Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(job.getId());
            return this.jobConverter.convertToSpaceBackupJobDetails(job, internalSettings, backupRestoreJobResultOptional.orElse(null));
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (TheSameSpaceBackupRestoreJobAlreadyInProgressException e) {
            throw new ConflictException(e.getMessage(), (Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), (Throwable)e);
        }
    }

    public SiteBackupJobDetails createSiteBackupJob(SiteBackupSettings settings) {
        this.validate(settings);
        try {
            BackupRestoreJob job = this.manager.startSiteBackup(this.jobConverter.convertFromSiteBackupSettings(settings));
            this.eventPublisher.publish((Object)new BackupRestoreAddJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), null));
            BackupRestoreSettings internalSettings = this.manager.getSettingsById(job.getId());
            Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(job.getId());
            return this.jobConverter.convertToSiteBackupJobDetails(job, internalSettings, backupRestoreJobResultOptional.orElse(null));
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), (Throwable)e);
        }
    }

    public SpaceRestoreJobDetails createSpaceRestoreJob(SpaceRestoreSettings settings) {
        this.validate(settings);
        try {
            BackupRestoreJob job = this.manager.startSpaceRestore(this.jobConverter.convertFromSpaceRestoreSettings(settings));
            this.eventPublisher.publish((Object)new BackupRestoreAddJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), false));
            BackupRestoreSettings internalSettings = this.manager.getSettingsById(job.getId());
            Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(job.getId());
            return this.jobConverter.convertToSpaceRestoreJobDetails(job, internalSettings, backupRestoreJobResultOptional.orElse(null));
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), (Throwable)e);
        }
    }

    public SiteRestoreJobDetails createSiteRestoreJob(SiteRestoreSettings settings) {
        this.validate(settings);
        try {
            BackupRestoreJob job = this.manager.startSiteRestore(this.jobConverter.convertFromSiteRestoreSettings(settings));
            this.eventPublisher.publish((Object)new BackupRestoreAddJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), false));
            BackupRestoreSettings internalSettings = this.manager.getSettingsById(job.getId());
            Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(job.getId());
            return this.jobConverter.convertToSiteRestoreJobDetails(job, internalSettings, backupRestoreJobResultOptional.orElse(null));
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), (Throwable)e);
        }
    }

    public SpaceRestoreJobDetails createSpaceRestoreJob(SpaceRestoreSettings settings, InputStream inputStream) {
        this.validate(settings);
        this.validateFileName(settings.getFileName());
        try {
            this.manager.assertUserHasSystemAdminPermissions();
            FilesystemPath filePath = this.tryWriteFilesLocally(JobScope.SPACE, settings.getFileName(), inputStream);
            settings.setFileName(filePath.asJavaFile().getName());
            this.backupRestoreFilesystemManager.validateZipFile(filePath.asJavaFile());
            BackupRestoreJob job = this.manager.startSpaceRestore(this.jobConverter.convertFromSpaceRestoreSettings(settings));
            this.eventPublisher.publish((Object)new BackupRestoreAddJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), true));
            BackupRestoreSettings internalSettings = this.manager.getSettingsById(job.getId());
            Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(job.getId());
            return this.jobConverter.convertToSpaceRestoreJobDetails(job, internalSettings, backupRestoreJobResultOptional.orElse(null));
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), (Throwable)e);
        }
    }

    public SiteRestoreJobDetails createSiteRestoreJob(SiteRestoreSettings settings, InputStream inputStream) {
        this.validate(settings);
        this.validateFileName(settings.getFileName());
        try {
            this.manager.assertUserHasSystemAdminPermissions();
            FilesystemPath filePath = this.tryWriteFilesLocally(JobScope.SITE, settings.getFileName(), inputStream);
            settings.setFileName(filePath.asJavaFile().getName());
            this.backupRestoreFilesystemManager.validateZipFile(filePath.asJavaFile());
            BackupRestoreJob job = this.manager.startSiteRestore(this.jobConverter.convertFromSiteRestoreSettings(settings));
            this.eventPublisher.publish((Object)new BackupRestoreAddJobAnalyticsEvent(job.getId(), job.getJobScope(), job.getJobOperation(), true));
            BackupRestoreSettings internalSettings = this.manager.getSettingsById(job.getId());
            Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(job.getId());
            return this.jobConverter.convertToSiteRestoreJobDetails(job, internalSettings, backupRestoreJobResultOptional.orElse(null));
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), (Throwable)e);
        }
    }

    public JobDetails getJob(long jobId) {
        Optional<Object> job;
        try {
            job = this.manager.getJob(jobId);
        }
        catch (NotPermittedException e) {
            job = Optional.empty();
        }
        if (job.isEmpty()) {
            throw new NotFoundException(String.format("Job with id %s doesn't exist or you don't have permissions to view it", jobId));
        }
        Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(jobId);
        return this.jobConverter.convertToFullJobDetails((BackupRestoreJob)job.get(), this.manager.getSettingsById(jobId), backupRestoreJobResultOptional.orElse(null));
    }

    public List<JobDetails> findJobs(JobFilter filter) {
        Collection<BackupRestoreJob> jobs = this.manager.findJobs(this.jobConverter.convertToSearchFilter(filter));
        return jobs.stream().map(this.jobConverter::convertToJobDetails).collect(Collectors.toList());
    }

    public JobDetails cancelJob(long jobId) {
        Optional<Object> job;
        try {
            job = this.manager.cancelJob(jobId);
        }
        catch (NotPermittedException e) {
            job = Optional.empty();
        }
        if (job.isEmpty()) {
            throw new NotFoundException(String.format("Job with id %s doesn't exist or you don't have permissions to cancel it", jobId));
        }
        Optional<BackupRestoreJobResult> backupRestoreJobResultOptional = this.manager.getStatisticsById(jobId);
        return this.jobConverter.convertToFullJobDetails((BackupRestoreJob)job.get(), this.manager.getSettingsById(jobId), backupRestoreJobResultOptional.orElse(null));
    }

    public List<FileInfo> getFiles(JobScope jobScope) {
        try {
            this.manager.assertUserHasSystemAdminPermissions();
            return this.backupRestoreFilesystemManager.getFiles(jobScope);
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
    }

    public int cancelAllQueuedJobs() {
        try {
            return this.manager.cancelAllJobsFromQueue();
        }
        catch (NotPermittedException e) {
            throw new PermissionException((Throwable)e);
        }
    }

    public File getBackupFile(Long jobId) throws NotFoundException {
        try {
            Optional<BackupRestoreJob> job = this.manager.getJob(jobId);
            if (job.isEmpty() || job.get().getJobState() != JobState.FINISHED) {
                throw new NotFoundException();
            }
            return this.backupRestoreFilesystemManager.getFile(job.get().getFileName(), job.get().getJobScope());
        }
        catch (NotPermittedException | FileNotFoundException e) {
            log.debug("Exception while accessing backup file", (Throwable)e);
            throw new NotFoundException();
        }
    }

    private void validate(SpaceBackupSettings settings) {
        if (settings == null || settings.getSpaceKeys().isEmpty()) {
            throw new BadRequestException("At least one spaceId should be provided for space backup");
        }
    }

    private void validate(SiteBackupSettings settings) {
        if (settings == null) {
            throw new BadRequestException("Invalid site backup settings provided");
        }
    }

    private void validate(SpaceRestoreSettings settings) {
        if (settings == null || StringUtils.isEmpty((CharSequence)settings.getFileName())) {
            throw new BadRequestException("File for space restore is not provided");
        }
    }

    private void validate(SiteRestoreSettings settings) {
        if (settings == null || StringUtils.isEmpty((CharSequence)settings.getFileName())) {
            throw new BadRequestException("File for site restore is not provided");
        }
    }

    private FilesystemPath tryWriteFilesLocally(JobScope jobScope, String fileName, InputStream inputStream) {
        try {
            return this.backupRestoreFilesystemManager.writeFileLocally(jobScope, fileName, () -> inputStream);
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot write file locally to disk", e);
        }
    }

    @VisibleForTesting
    void validateFileName(String fileName) {
        if (!this.backupRestoreFilesystemManager.isValidFilename(fileName)) {
            throw new BadRequestException("Invalid filename. The file should be a valid zip file with a .zip extension.The filename cannot include any of the following characters: /?<>\\:*| . The max length of filename is 255 characters.");
        }
    }
}

