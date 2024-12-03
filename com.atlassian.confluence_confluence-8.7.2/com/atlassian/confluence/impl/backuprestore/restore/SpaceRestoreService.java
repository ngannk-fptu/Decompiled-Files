/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.RestoreJobValidator;
import com.atlassian.confluence.impl.backuprestore.restore.RestoreService;

public class SpaceRestoreService {
    public static final int RESTORE_THREADS_NUMBER = Integer.getInteger("confluence.xmlrestore.space.number-of-threads", 16);
    private final RestoreService restoreService;
    private final RestoreJobValidator restoreJobValidator;

    public SpaceRestoreService(RestoreService restoreService, RestoreJobValidator restoreJobValidator) {
        this.restoreService = restoreService;
        this.restoreJobValidator = restoreJobValidator;
    }

    public void doRestoreSynchronously(BackupRestoreJob job, BackupRestoreSettings settings) throws BackupRestoreException, InterruptedException {
        this.restoreJobValidator.validateSpaceRestoreJob(job, settings);
        this.restoreService.doRestore(job, settings, RESTORE_THREADS_NUMBER);
    }
}

