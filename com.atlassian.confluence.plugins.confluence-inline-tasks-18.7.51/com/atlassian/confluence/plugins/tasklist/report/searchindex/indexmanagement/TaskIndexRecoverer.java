/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.index.IndexRecoverer
 *  org.apache.commons.lang3.time.StopWatch
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement;

import com.atlassian.confluence.api.model.index.IndexRecoverer;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.InlineTaskSearchIndexAccessor;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.TaskReportIndexPersistedStateService;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexQueueProcessor;
import java.io.File;
import org.apache.commons.lang3.time.StopWatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskIndexRecoverer
implements IndexRecoverer {
    private static final Logger log = LoggerFactory.getLogger(TaskIndexRecoverer.class);
    private final IndexQueueProcessor indexQueueProcessor;
    private final InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor;
    private final TaskReportIndexPersistedStateService taskReportIndexPersistedStateService;

    public TaskIndexRecoverer(IndexQueueProcessor indexQueueProcessor, InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor, TaskReportIndexPersistedStateService taskReportIndexPersistedStateService) {
        this.indexQueueProcessor = indexQueueProcessor;
        this.inlineTaskSearchIndexAccessor = inlineTaskSearchIndexAccessor;
        this.taskReportIndexPersistedStateService = taskReportIndexPersistedStateService;
    }

    public void snapshot(@NonNull File file) {
        log.debug("Snapshot has been requested");
        this.inlineTaskSearchIndexAccessor.snapshot(file);
    }

    public void reset(@NonNull Runnable runnable) {
        StopWatch watch = StopWatch.createStarted();
        log.debug("Index reset was called. It is going to be restored from the snapshot.");
        this.taskReportIndexPersistedStateService.markAsNotReady();
        this.inlineTaskSearchIndexAccessor.reset(runnable);
        this.taskReportIndexPersistedStateService.markAsReady();
        log.info("Index successfully restored from snapshot. Duration: {}", (Object)watch);
    }

    public void reindex() {
        log.info("Full reindex on current node was requested. Index will be marked as NOT ready immediately.");
        this.taskReportIndexPersistedStateService.markAsNotReady();
        this.indexQueueProcessor.requestFullReindexOnCurrentNode();
    }
}

