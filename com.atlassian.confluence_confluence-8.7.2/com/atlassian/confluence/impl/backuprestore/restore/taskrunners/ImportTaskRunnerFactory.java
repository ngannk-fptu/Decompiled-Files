/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.impl.backuprestore.restore.HiLoGeneratorInitialiserOnSiteRestore;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.ImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PostImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PreImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.SiteImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.SpaceImportTaskRunner;

public class ImportTaskRunnerFactory {
    private final PreImportTaskRunner preImportTaskRunner;
    private final PostImportTaskRunner postImportTaskRunner;

    public ImportTaskRunnerFactory(PreImportTaskRunner preImportTaskRunner, PostImportTaskRunner postImportTaskRunner) {
        this.preImportTaskRunner = preImportTaskRunner;
        this.postImportTaskRunner = postImportTaskRunner;
    }

    public ImportTaskRunner createImportTaskRunner(BackupRestoreJob job, BackupRestoreSettings settings, HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore) {
        return JobScope.SITE.equals((Object)job.getJobScope()) ? new SiteImportTaskRunner(this.preImportTaskRunner, this.postImportTaskRunner, job, settings, hiLoGeneratorInitialiserOnSiteRestore) : new SpaceImportTaskRunner(this.preImportTaskRunner, this.postImportTaskRunner);
    }
}

