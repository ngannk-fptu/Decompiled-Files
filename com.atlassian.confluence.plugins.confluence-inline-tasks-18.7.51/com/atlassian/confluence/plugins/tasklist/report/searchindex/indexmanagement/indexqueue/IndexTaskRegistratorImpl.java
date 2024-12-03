/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexTaskRegistrator;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task.InlineTaskIndexTaskType;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IndexTaskRegistratorImpl
implements IndexTaskRegistrator {
    private static final Logger log = LoggerFactory.getLogger(IndexTaskRegistratorImpl.class);
    private final JournalService journalService;
    static final JournalIdentifier JOURNAL_ID = new JournalIdentifier("task_report_index");

    public IndexTaskRegistratorImpl(JournalService journalService) {
        this.journalService = (JournalService)Preconditions.checkNotNull((Object)journalService);
    }

    @Override
    public void requestToReindexAllInlineTasks() {
        this.enqueue(InlineTaskIndexTaskType.REINDEX_ALL);
    }

    @Override
    public void requestToAddAllInlineTasks() {
        this.enqueue(InlineTaskIndexTaskType.REINDEX_ALL);
    }

    @Override
    public void requestToReindexAllInlineTasksOnPage(long contentId) {
        this.enqueue(InlineTaskIndexTaskType.REINDEX_INLINE_TASKS_FROM_PAGE, contentId);
    }

    @Override
    public void requestToReindexAllInlineTasksOnPageIncludingAllDescendants(long contentId) {
        this.enqueue(InlineTaskIndexTaskType.REINDEX_INLINE_TASKS_FROM_PAGE_INCLUDING_CHILDREN, contentId);
    }

    @Override
    public void requestToReindexInlineTask(long taskId) {
        this.enqueue(InlineTaskIndexTaskType.REINDEX_INLINE_TASK, taskId);
    }

    @Override
    public void requestToRemoveTask(long globalTaskId) {
        this.enqueue(InlineTaskIndexTaskType.REMOVE_INLINE_TASK, globalTaskId);
    }

    @Override
    public void requestToReindexTask(long globalTaskId) {
        log.debug("Received request to re-index inline task with global id {} in the search index", (Object)globalTaskId);
        this.enqueue(InlineTaskIndexTaskType.REINDEX_INLINE_TASK, globalTaskId);
    }

    @Override
    public void requestToRemoveAllTasksOnThePage(long contentId) {
        this.enqueue(InlineTaskIndexTaskType.REMOVE_ALL_INLINE_TASKS_FROM_PAGE, contentId);
    }

    private void enqueue(InlineTaskIndexTaskType indexTaskType) {
        log.debug("Enqueue request received without a message. indexTaskType: {}.", (Object)indexTaskType);
        this.journalService.enqueue(new JournalEntry(JOURNAL_ID, indexTaskType.name(), ""));
    }

    private void enqueue(InlineTaskIndexTaskType indexTaskType, Long id) {
        log.debug("Enqueue request received. indexTaskType: {}, message (id): {}", (Object)indexTaskType, (Object)id);
        this.journalService.enqueue(new JournalEntry(JOURNAL_ID, indexTaskType.name(), Long.toString(id)));
    }
}

