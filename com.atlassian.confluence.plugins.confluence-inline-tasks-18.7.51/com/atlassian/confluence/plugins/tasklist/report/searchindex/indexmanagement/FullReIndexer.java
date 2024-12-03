/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.ao.dao.InlineTaskDao;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.InlineTaskSearchIndexAccessor;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.AddSearchDocumentForInlineTaskAction;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.RemoveAllDocumentsAction;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.TaskReportIndexPersistedStateService;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.lucene.InlineTaskSearchDocumentFactory;
import com.atlassian.confluence.plugins.tasklist.service.util.TaskContentUtils;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FullReIndexer {
    private static final Logger log = LoggerFactory.getLogger(FullReIndexer.class);
    final int BATCH_SIZE = Integer.getInteger("confluence.task-report.indexing-batch-size", 1000);
    private final ExecutorService executorForFullIndexing = Executors.newCachedThreadPool();
    private final InlineTaskDao inlineTaskDao;
    private final InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor;
    private final ContentEntityManager contentEntityManager;
    private final InlineTaskSearchDocumentFactory inlineTaskSearchDocumentFactory;
    private final TransactionTemplate txTemplate;
    private final TaskReportIndexPersistedStateService taskReportIndexPersistedStateService;

    public FullReIndexer(InlineTaskDao inlineTaskDao, InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, InlineTaskSearchDocumentFactory inlineTaskSearchDocumentFactory, TransactionTemplate txTemplate, TaskReportIndexPersistedStateService taskReportIndexPersistedStateService) {
        this.inlineTaskDao = inlineTaskDao;
        this.inlineTaskSearchIndexAccessor = inlineTaskSearchIndexAccessor;
        this.contentEntityManager = contentEntityManager;
        this.inlineTaskSearchDocumentFactory = inlineTaskSearchDocumentFactory;
        this.txTemplate = txTemplate;
        this.taskReportIndexPersistedStateService = taskReportIndexPersistedStateService;
    }

    public int reindexAllTasks() {
        log.info("Inline tasks search index: full reindexing started. While the index is being rebuilt, Task Report macros will temporary use database queries. That means macros will be slower than expected.");
        this.taskReportIndexPersistedStateService.markAsNotReady();
        StopWatch globalWatch = StopWatch.createStarted();
        this.removeAllDocuments();
        int processedTasksNumber = this.addAllInlineTasksToSearchIndex();
        globalWatch.stop();
        if (processedTasksNumber > 0) {
            long avgDurationOf1000Tasks = globalWatch.getTime() * 1000L / (long)processedTasksNumber;
            log.info("Full inline tasks reindexing (for Task Report macro) has been finished. Processed {} inline tasks. Duration: {}. In average, 1000 inline tasks were processed in {} ms.", new Object[]{processedTasksNumber, globalWatch, avgDurationOf1000Tasks});
        } else {
            log.info("Full reindexing has been finished. Processed zero inline tasks. Duration: {}.", (Object)globalWatch);
        }
        this.taskReportIndexPersistedStateService.markAsReady();
        return processedTasksNumber;
    }

    private Long indexNextBatchOfInlineTasksInSeparateTransaction(Long latestProcessedGlobalTaskId, int batchSize, AtomicInteger processedTaskCounter, int iterationNumber) throws ExecutionException, InterruptedException {
        if (Thread.interrupted()) {
            throw new IllegalStateException("Unable to complete full indexing of inline tasks, the thread was interrupted");
        }
        return this.executorForFullIndexing.submit(() -> (Long)this.txTemplate.execute(() -> this.indexNextBatchOfInlineTasks(latestProcessedGlobalTaskId, batchSize, processedTaskCounter, iterationNumber))).get();
    }

    private Long indexNextBatchOfInlineTasks(Long latestProcessedGlobalTaskId, int batchSize, AtomicInteger processedTaskCounter, int iterationNumber) {
        AOInlineTask[] tasks;
        log.trace("Starting iteration {} of indexing tasks", (Object)iterationNumber);
        AOInlineTask[] aOInlineTaskArray = tasks = latestProcessedGlobalTaskId == null ? this.inlineTaskDao.getFirstTasksOrderedById(this.BATCH_SIZE) : this.inlineTaskDao.getTasksWithIdGreaterThan(latestProcessedGlobalTaskId, batchSize);
        if (tasks.length == 0) {
            log.debug("No more tasks were found on iteration {}", (Object)iterationNumber);
            return null;
        }
        StopWatch localWatch = StopWatch.createStarted();
        log.trace("{} tasks retrieved from the DB on iteration {}. Indexing them.", (Object)tasks.length, (Object)iterationNumber);
        Set contentIds = Arrays.stream(tasks).map(AOInlineTask::getContentId).collect(Collectors.toSet());
        log.trace("Found {} unique content ids for {} inline tasks on iteration {}", new Object[]{contentIds.size(), tasks.length, iterationNumber});
        List<AddSearchDocumentForInlineTaskAction> actions = this.convertInlineTasksToIndexActions(tasks);
        this.inlineTaskSearchIndexAccessor.withBatchUpdate(() -> actions.forEach(arg_0 -> ((InlineTaskSearchIndexAccessor)this.inlineTaskSearchIndexAccessor).execute(arg_0)));
        processedTaskCounter.addAndGet(tasks.length);
        long lastProcessedGlobalId = tasks[tasks.length - 1].getGlobalId();
        log.trace("Finished iteration {} containing {} inline tasks Last processed global task id: {}. Duration: {}", new Object[]{iterationNumber, tasks.length, lastProcessedGlobalId, localWatch});
        return lastProcessedGlobalId;
    }

    private int addAllInlineTasksToSearchIndex() {
        AtomicInteger processedTasksCount = new AtomicInteger();
        int iterationNumber = 0;
        Long latestProcessedGlobalTaskId = null;
        try {
            while ((latestProcessedGlobalTaskId = this.indexNextBatchOfInlineTasksInSeparateTransaction(latestProcessedGlobalTaskId, this.BATCH_SIZE, processedTasksCount, iterationNumber++)) != null) {
            }
        }
        catch (ExecutionException e) {
            throw new IllegalStateException("Something went wrong with indexing: " + e.getMessage(), e);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException("Unable to complete full indexing of inline tasks, the thread was interrupted");
        }
        return processedTasksCount.get();
    }

    private List<AddSearchDocumentForInlineTaskAction> convertInlineTasksToIndexActions(AOInlineTask[] tasks) {
        return Arrays.stream(tasks).map(task -> {
            try {
                return this.getAddSearchDocumentAction(task.getContentId(), (AOInlineTask)task);
            }
            catch (Exception e) {
                log.warn("Unable to create search document action inline task with global id " + task.getGlobalId() + ". It will be skipped. Error: " + e.getMessage(), (Throwable)e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private AddSearchDocumentForInlineTaskAction getAddSearchDocumentAction(long contentId, AOInlineTask task) {
        log.debug("Creating a document for content id {} and task {}", (Object)contentId, (Object)task);
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(contentId);
        if (contentEntityObject == null) {
            log.warn("Content with id {} for task {} was not found. The task will not be added to the search index (used for task reports).", (Object)contentId, (Object)task);
            return null;
        }
        if (!contentEntityObject.isIndexable()) {
            return null;
        }
        String taskBody = task.getBody();
        if (TaskContentUtils.isBlankContent(taskBody)) {
            log.debug("Task {} has empty body text. The task will not be added to the search index (used for task reports).", (Object)task);
            return null;
        }
        return new AddSearchDocumentForInlineTaskAction(this.inlineTaskSearchDocumentFactory, contentEntityObject, task);
    }

    private void removeAllDocuments() {
        log.debug("Removing all documents in the inline task report macro search index.");
        this.inlineTaskSearchIndexAccessor.execute(new RemoveAllDocumentsAction());
        log.debug("All documents in the search index have been removed.");
    }
}

