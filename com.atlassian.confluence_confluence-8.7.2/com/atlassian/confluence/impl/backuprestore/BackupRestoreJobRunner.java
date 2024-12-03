/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.SiteBackupService;
import com.atlassian.confluence.impl.backuprestore.backup.SpaceBackupService;
import com.atlassian.confluence.impl.backuprestore.restore.SiteRestoreService;
import com.atlassian.confluence.impl.backuprestore.restore.SpaceRestoreService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackupRestoreJobRunner {
    private final SiteBackupService siteBackupService;
    private final SpaceBackupService spaceBackupService;
    private final SiteRestoreService siteRestoreService;
    private final SpaceRestoreService spaceRestoreService;
    private final Map<Long, Thread> activeJobs = new ConcurrentHashMap<Long, Thread>();

    public BackupRestoreJobRunner(SiteBackupService siteBackupService, SpaceBackupService spaceBackupService, SiteRestoreService siteRestoreService, SpaceRestoreService spaceRestoreService) {
        this.siteBackupService = siteBackupService;
        this.spaceBackupService = spaceBackupService;
        this.siteRestoreService = siteRestoreService;
        this.spaceRestoreService = spaceRestoreService;
    }

    void runJobSynchronously(BackupRestoreJob job, BackupRestoreSettings settings) throws InterruptedException, BackupRestoreException {
        this.activeJobs.put(job.getId(), Thread.currentThread());
        try {
            switch (job.getJobOperation()) {
                case BACKUP: {
                    switch (job.getJobScope()) {
                        case SITE: {
                            this.siteBackupService.doBackupSynchronously(job, settings);
                            return;
                        }
                        case SPACE: {
                            this.spaceBackupService.doBackupSynchronously(job, settings);
                            return;
                        }
                    }
                    throw new IllegalArgumentException("Undefined job scope: " + job.getJobScope());
                }
                case RESTORE: {
                    switch (job.getJobScope()) {
                        case SITE: {
                            this.siteRestoreService.doRestoreSynchronously(job, settings);
                            return;
                        }
                        case SPACE: {
                            this.spaceRestoreService.doRestoreSynchronously(job, settings);
                            return;
                        }
                    }
                    throw new IllegalArgumentException("Undefined job scope: " + job.getJobScope());
                }
            }
            throw new IllegalArgumentException("Undefined job operation: " + job.getJobOperation());
        }
        finally {
            this.activeJobs.remove(job.getId());
        }
    }

    boolean terminateProcess(long jobId) {
        Thread thread = this.activeJobs.get(jobId);
        if (thread == null) {
            return false;
        }
        thread.interrupt();
        return true;
    }
}

