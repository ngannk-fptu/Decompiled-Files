/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobDetails
 *  com.atlassian.confluence.api.model.backuprestore.JobFilter
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.confluence.api.model.backuprestore.JobStatistics
 *  com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings
 *  com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings
 *  com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings
 *  com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobDetails;
import com.atlassian.confluence.api.model.backuprestore.JobFilter;
import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.api.model.backuprestore.JobStatistics;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreFilesystemManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class BackupRestoreJobConverter {
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;

    public BackupRestoreJobConverter(BackupRestoreFilesystemManager backupRestoreFilesystemManager) {
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
    }

    public SpaceBackupJobDetails convertToSpaceBackupJobDetails(BackupRestoreJob backupRestoreJob, BackupRestoreSettings backupRestoreSettings, BackupRestoreJobResult backupRestoreJobResult) {
        this.validateJobScope(JobScope.SPACE, backupRestoreJob);
        this.validateJobOperation(JobOperation.BACKUP, backupRestoreJob);
        SpaceBackupJobDetails jobDetails = new SpaceBackupJobDetails();
        this.setCommonDetails(backupRestoreJob, (JobDetails)jobDetails);
        jobDetails.setJobSettings(this.convertToSpaceBackupSettings(backupRestoreSettings));
        jobDetails.setJobStatistics(this.convertToJobStatistics(backupRestoreJobResult));
        return jobDetails;
    }

    public SpaceBackupSettings convertToSpaceBackupSettings(BackupRestoreSettings backupRestoreSettings) {
        SpaceBackupSettings settings = new SpaceBackupSettings();
        settings.setSpaceKeys(backupRestoreSettings.getSpaceKeys());
        settings.setKeepPermanently(backupRestoreSettings.isKeepPermanently());
        settings.setFileNamePrefix(backupRestoreSettings.getFileNamePrefix());
        return settings;
    }

    public BackupRestoreSettings convertFromSpaceBackupSettings(SpaceBackupSettings spaceBackupSettings) {
        BackupRestoreSettings.Builder builder = new BackupRestoreSettings.Builder(JobOperation.BACKUP, JobScope.SPACE);
        spaceBackupSettings.getSpaceKeys().forEach(builder::addSpaceKey);
        builder.keepPermanently(spaceBackupSettings.isKeepPermanently());
        String fileNamePrefix = spaceBackupSettings.getFileNamePrefix();
        builder.setFilenamePrefix(fileNamePrefix);
        fileNamePrefix = StringUtils.isBlank((CharSequence)fileNamePrefix) ? BackupRestoreFilesystemManager.SPACE_BACKUP_FILENAME_PREFIX : fileNamePrefix;
        builder.setFilename(this.backupRestoreFilesystemManager.generateSpaceBackupFileName(fileNamePrefix, spaceBackupSettings.getSpaceKeys(), LocalDateTime::now));
        return builder.build();
    }

    public SiteBackupJobDetails convertToSiteBackupJobDetails(BackupRestoreJob backupRestoreJob, BackupRestoreSettings siteBackupSettings, BackupRestoreJobResult backupRestoreJobResult) {
        this.validateJobScope(JobScope.SITE, backupRestoreJob);
        this.validateJobOperation(JobOperation.BACKUP, backupRestoreJob);
        SiteBackupJobDetails jobDetails = new SiteBackupJobDetails();
        this.setCommonDetails(backupRestoreJob, (JobDetails)jobDetails);
        jobDetails.setJobSettings(this.convertToSiteBackupSettings(siteBackupSettings));
        jobDetails.setJobStatistics(this.convertToJobStatistics(backupRestoreJobResult));
        return jobDetails;
    }

    public SiteBackupSettings convertToSiteBackupSettings(BackupRestoreSettings backupRestoreSettings) {
        SiteBackupSettings settings = new SiteBackupSettings();
        settings.setKeepPermanently(backupRestoreSettings.isKeepPermanently());
        settings.setSkipAttachments(backupRestoreSettings.isSkipAttachments());
        settings.setFileNamePrefix(backupRestoreSettings.getFileNamePrefix());
        return settings;
    }

    public BackupRestoreSettings convertFromSiteBackupSettings(SiteBackupSettings siteBackupSettings) {
        BackupRestoreSettings.Builder builder = new BackupRestoreSettings.Builder(JobOperation.BACKUP, JobScope.SITE);
        builder.skipAttachments(siteBackupSettings.isSkipAttachments());
        builder.keepPermanently(siteBackupSettings.isKeepPermanently());
        String fileNamePrefix = siteBackupSettings.getFileNamePrefix();
        builder.setFilenamePrefix(fileNamePrefix);
        fileNamePrefix = StringUtils.isBlank((CharSequence)fileNamePrefix) ? BackupRestoreFilesystemManager.SITE_BACKUP_FILENAME_PREFIX : fileNamePrefix;
        builder.setFilename(this.backupRestoreFilesystemManager.generateSiteBackupFileName(fileNamePrefix, LocalDateTime::now));
        return builder.build();
    }

    public SpaceRestoreJobDetails convertToSpaceRestoreJobDetails(BackupRestoreJob backupRestoreJob, BackupRestoreSettings backupRestoreSettings, BackupRestoreJobResult backupRestoreJobResult) {
        this.validateJobScope(JobScope.SPACE, backupRestoreJob);
        this.validateJobOperation(JobOperation.RESTORE, backupRestoreJob);
        SpaceRestoreJobDetails jobDetails = new SpaceRestoreJobDetails();
        this.setCommonDetails(backupRestoreJob, (JobDetails)jobDetails);
        jobDetails.setJobSettings(this.convertToSpaceRestoreSettings(backupRestoreSettings));
        jobDetails.setJobStatistics(this.convertToJobStatistics(backupRestoreJobResult));
        return jobDetails;
    }

    public SpaceRestoreSettings convertToSpaceRestoreSettings(BackupRestoreSettings backupRestoreSettings) {
        SpaceRestoreSettings settings = new SpaceRestoreSettings();
        settings.setFileName(backupRestoreSettings.getFileName());
        settings.setSkipReindex(backupRestoreSettings.getSkipReindex());
        return settings;
    }

    public BackupRestoreSettings convertFromSpaceRestoreSettings(SpaceRestoreSettings spaceRestoreSettings) {
        BackupRestoreSettings.Builder builder = new BackupRestoreSettings.Builder(JobOperation.RESTORE, JobScope.SPACE);
        builder.setFilename(spaceRestoreSettings.getFileName());
        builder.setSkipReindex(spaceRestoreSettings.getSkipReindex());
        builder.setWorkingDir(this.backupRestoreFilesystemManager.getRestoreWorkingDir(JobScope.SPACE));
        return builder.build();
    }

    public SiteRestoreJobDetails convertToSiteRestoreJobDetails(BackupRestoreJob backupRestoreJob, BackupRestoreSettings backupRestoreSettings, BackupRestoreJobResult backupRestoreJobResult) {
        this.validateJobScope(JobScope.SITE, backupRestoreJob);
        this.validateJobOperation(JobOperation.RESTORE, backupRestoreJob);
        SiteRestoreJobDetails jobDetails = new SiteRestoreJobDetails();
        this.setCommonDetails(backupRestoreJob, (JobDetails)jobDetails);
        jobDetails.setJobSettings(this.convertToSiteRestoreSettings(backupRestoreSettings));
        jobDetails.setJobStatistics(this.convertToJobStatistics(backupRestoreJobResult));
        return jobDetails;
    }

    public SiteRestoreSettings convertToSiteRestoreSettings(BackupRestoreSettings backupRestoreSettings) {
        SiteRestoreSettings settings = new SiteRestoreSettings();
        settings.setFileName(backupRestoreSettings.getFileName());
        settings.setSkipReindex(backupRestoreSettings.getSkipReindex());
        return settings;
    }

    public BackupRestoreSettings convertFromSiteRestoreSettings(SiteRestoreSettings siteRestoreSettings) {
        BackupRestoreSettings.Builder builder = new BackupRestoreSettings.Builder(JobOperation.RESTORE, JobScope.SITE);
        builder.setFilename(siteRestoreSettings.getFileName());
        builder.setSkipReindex(siteRestoreSettings.getSkipReindex());
        builder.setWorkingDir(this.backupRestoreFilesystemManager.getRestoreWorkingDir(JobScope.SITE));
        return builder.build();
    }

    public JobDetails convertToJobDetails(BackupRestoreJob backupRestoreJob) {
        JobDetails jobDetails = new JobDetails();
        this.setCommonDetails(backupRestoreJob, jobDetails);
        return jobDetails;
    }

    public JobDetails convertToFullJobDetails(BackupRestoreJob backupRestoreJob, BackupRestoreSettings settings, BackupRestoreJobResult backupRestoreJobResult) {
        if (backupRestoreJob.getJobScope() == JobScope.SITE) {
            return backupRestoreJob.getJobOperation() == JobOperation.BACKUP ? this.convertToSiteBackupJobDetails(backupRestoreJob, settings, backupRestoreJobResult) : this.convertToSiteRestoreJobDetails(backupRestoreJob, settings, backupRestoreJobResult);
        }
        return backupRestoreJob.getJobOperation() == JobOperation.BACKUP ? this.convertToSpaceBackupJobDetails(backupRestoreJob, settings, backupRestoreJobResult) : this.convertToSpaceRestoreJobDetails(backupRestoreJob, settings, backupRestoreJobResult);
    }

    public BackupRestoreJobsSearchFilter convertToSearchFilter(JobFilter jobFilter) {
        Set jobStates = jobFilter.getJobStates().isEmpty() ? (Collection)Arrays.stream(JobState.values()).collect(Collectors.toSet()) : jobFilter.getJobStates();
        BackupRestoreJobsSearchFilter.Builder builder = new BackupRestoreJobsSearchFilter.Builder(jobStates);
        builder.byOwner(jobFilter.getOwner());
        builder.bySpaceKey(jobFilter.getSpaceKey());
        builder.setJobScope(jobFilter.getJobScope());
        builder.setJobOperation(jobFilter.getJobOperation());
        builder.dateRange(jobFilter.getFromDate(), jobFilter.getToDate());
        builder.setLimit(jobFilter.getLimit());
        return builder.build();
    }

    private void setCommonDetails(BackupRestoreJob backupRestoreJob, JobDetails jobDetails) {
        jobDetails.setId(backupRestoreJob.getId());
        jobDetails.setJobScope(backupRestoreJob.getJobScope());
        jobDetails.setJobOperation(backupRestoreJob.getJobOperation());
        jobDetails.setJobState(backupRestoreJob.getJobState());
        jobDetails.setOwner(backupRestoreJob.getOwner());
        jobDetails.setCancelledBy(backupRestoreJob.getWhoCancelledTheJob());
        jobDetails.setErrorMessage(backupRestoreJob.getErrorMessage());
        jobDetails.setFileName(backupRestoreJob.getFileName());
        jobDetails.setSpaceKeys(backupRestoreJob.getSpaceKeys());
        jobDetails.setCreateTime(backupRestoreJob.getCreateTime());
        jobDetails.setStartProcessingTime(backupRestoreJob.getStartProcessingTime());
        jobDetails.setFinishProcessingTime(backupRestoreJob.getFinishProcessingTime());
        jobDetails.setFileDeleteTime(backupRestoreJob.getFileDeleteTime());
        jobDetails.setFileExists(backupRestoreJob.isFileExists());
        jobDetails.setCancelTime(backupRestoreJob.getCancelTime());
    }

    public JobStatistics convertToJobStatistics(BackupRestoreJobResult backupRestoreJobResult) {
        if (backupRestoreJobResult != null) {
            JobStatistics jobStatistics = new JobStatistics();
            jobStatistics.setTotalObjectsCount(Long.valueOf(backupRestoreJobResult.getTotalObjectsCount()));
            jobStatistics.setProcessedObjectsCount(Long.valueOf(backupRestoreJobResult.getProcessedObjectsCount()));
            jobStatistics.setPersistedObjectsCount(Long.valueOf(backupRestoreJobResult.getPersistedObjectsCount()));
            jobStatistics.setSkippedObjectsCount(Long.valueOf(backupRestoreJobResult.getSkippedObjectsCount()));
            jobStatistics.setReusedObjectsCount(Long.valueOf(backupRestoreJobResult.getReusedObjectsCount()));
            return jobStatistics;
        }
        return null;
    }

    private void validateJobOperation(JobOperation expected, BackupRestoreJob job) {
        if (expected != job.getJobOperation()) {
            throw new IllegalStateException(String.format("Received unsupported job (jobId %s). Expected job operation %s, but received %s", job.getId(), expected, job.getJobOperation()));
        }
    }

    private void validateJobScope(JobScope expected, BackupRestoreJob job) {
        if (expected != job.getJobScope()) {
            throw new IllegalStateException(String.format("Received unsupported job (jobId %s). Expected job scope %s, but received %s", job.getId(), expected, job.getJobScope()));
        }
    }
}

