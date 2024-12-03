/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;

public class ResetJournalStateUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "9009";
    private final JournalStateStore journalStateStore;
    private final JournalStateStore bandanaJournalStateStore;
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final IndexManager indexManager;

    public ResetJournalStateUpgradeTask(JournalStateStore journalStateStore, JournalStateStore bandanaJournalStateStore, BackupRestoreJobDao backupRestoreJobDao, IndexManager indexManager) {
        this.journalStateStore = journalStateStore;
        this.bandanaJournalStateStore = bandanaJournalStateStore;
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.indexManager = indexManager;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public void doUpgrade() throws Exception {
        BackupRestoreJobsSearchFilter filter = new BackupRestoreJobsSearchFilter.Builder(JobState.FINISHED).setJobScope(JobScope.SITE).setJobOperation(JobOperation.RESTORE).build();
        if (this.backupRestoreJobDao.findJobs(filter).isEmpty()) {
            return;
        }
        log.info("ResetJournalStateUpgradeTask started. Reindexing whole content.");
        this.journalStateStore.resetAllJournalStates();
        this.bandanaJournalStateStore.resetAllJournalStates();
        this.indexManager.reIndex();
    }
}

