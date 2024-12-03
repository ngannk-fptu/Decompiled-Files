/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.backuprestore;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.backuprestore.FileInfo;
import com.atlassian.confluence.api.model.backuprestore.JobDetails;
import com.atlassian.confluence.api.model.backuprestore.JobFilter;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import java.io.File;
import java.io.InputStream;
import java.util.List;

@ExperimentalApi
public interface BackupRestoreService {
    public SpaceBackupJobDetails createSpaceBackupJob(SpaceBackupSettings var1);

    public SiteBackupJobDetails createSiteBackupJob(SiteBackupSettings var1);

    public SpaceRestoreJobDetails createSpaceRestoreJob(SpaceRestoreSettings var1);

    public SiteRestoreJobDetails createSiteRestoreJob(SiteRestoreSettings var1);

    public SpaceRestoreJobDetails createSpaceRestoreJob(SpaceRestoreSettings var1, InputStream var2);

    public SiteRestoreJobDetails createSiteRestoreJob(SiteRestoreSettings var1, InputStream var2);

    public JobDetails getJob(long var1);

    public List<JobDetails> findJobs(JobFilter var1);

    public JobDetails cancelJob(long var1);

    public List<FileInfo> getFiles(JobScope var1);

    public int cancelAllQueuedJobs();

    public File getBackupFile(Long var1) throws NotFoundException;
}

