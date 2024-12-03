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
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.ImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PostImportTaskRunner;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PreImportTaskRunner;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceImportTaskRunner
implements ImportTaskRunner {
    private static final Logger log = LoggerFactory.getLogger(SpaceImportTaskRunner.class);
    private final PreImportTaskRunner preImportTaskRunner;
    private final PostImportTaskRunner postImportTaskRunner;

    public SpaceImportTaskRunner(PreImportTaskRunner preImportTaskRunner, PostImportTaskRunner postImportTaskRunner) {
        this.preImportTaskRunner = preImportTaskRunner;
        this.postImportTaskRunner = postImportTaskRunner;
    }

    @Override
    public void runPreImportTasks() {
    }

    @Override
    public void runPostImportTasks(BackupRestoreJob job, BackupRestoreSettings settings, BackupProperties backupProperties) {
        String restoredFileName = settings.getFileName();
        StopWatch stopWatch = StopWatch.createStarted();
        this.postImportTaskRunner.evictSpacesFromCache(backupProperties.getSpaceKeys());
        this.postImportTaskRunner.runCacheFlushingPostImportTask(restoredFileName);
        this.postImportTaskRunner.runClearReIndexJobPostImportTask();
        this.postImportTaskRunner.runTrashDatePostImportTaskForSpaces(backupProperties.getSpaceKeys());
        log.debug("Post import tasks duration: {}", (Object)stopWatch);
    }

    @Override
    public void close() {
    }
}

