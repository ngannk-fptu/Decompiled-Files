/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.HiLoGeneratorInitialiserOnSiteRestore;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.ImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PostImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PreImportTaskRunner;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteImportTaskRunner
implements ImportTaskRunner {
    private static final Logger log = LoggerFactory.getLogger(SiteImportTaskRunner.class);
    private final PreImportTaskRunner preImportTaskRunner;
    private final PostImportTaskRunner postImportTaskRunner;
    private final BackupRestoreJob job;
    private final BackupRestoreSettings settings;
    private final HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore;

    public SiteImportTaskRunner(PreImportTaskRunner preImportTaskRunner, PostImportTaskRunner postImportTaskRunner, BackupRestoreJob job, BackupRestoreSettings settings, HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore) {
        this.preImportTaskRunner = preImportTaskRunner;
        this.postImportTaskRunner = postImportTaskRunner;
        this.job = job;
        this.settings = settings;
        this.hiLoGeneratorInitialiserOnSiteRestore = hiLoGeneratorInitialiserOnSiteRestore;
    }

    @Override
    public void runPreImportTasks() throws BackupRestoreException {
        StopWatch stopWatch = StopWatch.createStarted();
        this.preImportTaskRunner.disablePlugins();
        this.preImportTaskRunner.unIndexAll();
        this.preImportTaskRunner.pauseSchedulerAndFlushJobs();
        this.preImportTaskRunner.flushCommitClearSession();
        this.preImportTaskRunner.deleteAllDatabaseContent();
        this.preImportTaskRunner.runDatabaseConstraintsTask();
        this.preImportTaskRunner.restoreSiteJobRecord(this.job, this.settings, this.hiLoGeneratorInitialiserOnSiteRestore);
        log.debug("Pre-import tasks duration: {}", (Object)stopWatch);
    }

    @Override
    public void runPostImportTasks(BackupRestoreJob job, BackupRestoreSettings settings, BackupProperties backupProperties) throws BackupRestoreException {
        String restoredFileName = settings.getFileName();
        StopWatch stopWatch = StopWatch.createStarted();
        this.postImportTaskRunner.runUpgradeTasks(backupProperties);
        this.preImportTaskRunner.flushCaches();
        this.postImportTaskRunner.runCacheFlushingPostImportTask(restoredFileName);
        this.postImportTaskRunner.runClearReIndexJobPostImportTask();
        this.postImportTaskRunner.runTrashDatePostImportTaskForSite();
        this.postImportTaskRunner.runKeyInitPostImportTask();
        log.debug("Post import tasks duration: {}", (Object)stopWatch);
    }

    @Override
    public void close() {
        StopWatch stopWatch = StopWatch.createStarted();
        this.preImportTaskRunner.enablePlugins();
        this.preImportTaskRunner.resumeScheduler();
        log.debug("Cleanup import tasks duration: {}", (Object)stopWatch);
    }
}

