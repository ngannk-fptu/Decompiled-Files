/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.ao.dao.InlineTaskDao;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.InlineTaskSearchIndexAccessor;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.AddSearchDocumentForInlineTaskAction;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.DeleteContentIdAction;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.DeletePageWithDescendantsAction;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.DeleteTasksAction;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.SearchIndexActionWithNumberOfIds;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.FullReIndexer;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.TaskReportIndexPersistedStateService;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexQueueProcessor;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task.InlineTaskIndexTaskType;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task.TaskLevel;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.lucene.InlineTaskSearchDocumentFactory;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class IndexQueueProcessorImpl
implements IndexQueueProcessor {
    private static final Logger log = LoggerFactory.getLogger(IndexQueueProcessorImpl.class);
    private static final String SELF_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-inline-tasks";
    static final JournalIdentifier JOURNAL_ID = new JournalIdentifier("task_report_index");
    public static final int BATCH_SIZE = 1000;
    private final JournalService journalService;
    private final InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor;
    private final FullReIndexer fullReIndexer;
    private final InlineTaskSearchDocumentFactory inlineTaskSearchDocumentFactory;
    private final InlineTaskDao inlineTaskDao;
    private final EventPublisher eventPublisher;
    private final ContentEntityManager contentEntityManager;
    private final TaskReportIndexPersistedStateService taskReportIndexPersistedStateService;
    private final AtomicBoolean pluginIsUpAndRunning = new AtomicBoolean();
    private final AtomicBoolean fullReindexRequiredOnCurrentNode = new AtomicBoolean();

    public IndexQueueProcessorImpl(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport JournalService journalService, @ComponentImport EventPublisher eventPublisher, InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor, FullReIndexer fullReIndexer, InlineTaskSearchDocumentFactory inlineTaskSearchDocumentFactory, InlineTaskDao inlineTaskDao, TaskReportIndexPersistedStateService taskReportIndexPersistedStateService) {
        this.contentEntityManager = contentEntityManager;
        this.journalService = (JournalService)Preconditions.checkNotNull((Object)journalService);
        this.eventPublisher = eventPublisher;
        this.inlineTaskSearchIndexAccessor = inlineTaskSearchIndexAccessor;
        this.fullReIndexer = fullReIndexer;
        this.inlineTaskDao = inlineTaskDao;
        this.inlineTaskSearchDocumentFactory = inlineTaskSearchDocumentFactory;
        this.taskReportIndexPersistedStateService = taskReportIndexPersistedStateService;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (!SELF_PLUGIN_KEY.equals(event.getPlugin().getKey())) {
            return;
        }
        log.debug("Inline Task Plugin has been enabled. Task indexing queue processing is allowed.");
        this.pluginIsUpAndRunning.set(true);
    }

    private synchronized int processJournalEntries() {
        if (this.fullReindexRequiredOnCurrentNode.get()) {
            this.fullReindexRequiredOnCurrentNode.set(false);
            return this.fullReIndexer.reindexAllTasks();
        }
        if (!this.taskReportIndexPersistedStateService.isIndexReady()) {
            log.debug("Index is not ready. Either broken or still being recovered. Journal records will not be processed.");
            return 0;
        }
        AtomicInteger processedEntries = new AtomicInteger();
        this.journalService.processNewEntries(JOURNAL_ID, 1000, entries -> {
            ImmutableList entriesAsList = ImmutableList.copyOf((Iterable)entries);
            boolean fullReindexRequired = entriesAsList.stream().anyMatch(entry -> InlineTaskIndexTaskType.valueOf(entry.getType()).equals((Object)InlineTaskIndexTaskType.REINDEX_ALL));
            try {
                if (fullReindexRequired) {
                    return EntryProcessorResult.success((Object)this.fullReIndexer.reindexAllTasks());
                }
                List<SearchIndexActionWithNumberOfIds> searchIndexActions = this.getSearchIndexActionsForAllIndexingLevels((List<JournalEntry>)entriesAsList);
                this.inlineTaskSearchIndexAccessor.withBatchUpdate(() -> searchIndexActions.forEach(arg_0 -> ((InlineTaskSearchIndexAccessor)this.inlineTaskSearchIndexAccessor).execute(arg_0)));
                processedEntries.addAndGet(searchIndexActions.stream().map(SearchIndexActionWithNumberOfIds::getNumberOfIds).reduce(0, Integer::sum));
                return EntryProcessorResult.success((Object)entriesAsList.size());
            }
            catch (Exception e) {
                log.warn("Failed to process the next batch of task", (Throwable)e);
                return EntryProcessorResult.failure((Object)0, (long)((JournalEntry)entriesAsList.get(entriesAsList.size() - 1)).getId());
            }
        });
        return processedEntries.get();
    }

    private List<SearchIndexActionWithNumberOfIds> getSearchIndexActionsForAllIndexingLevels(List<JournalEntry> entriesAsList) {
        Map<String, List<JournalEntry>> mapsOfEntriesByType = entriesAsList.stream().collect(Collectors.groupingBy(JournalEntry::getType));
        ArrayList<SearchIndexActionWithNumberOfIds> allSearchIndexActions = new ArrayList<SearchIndexActionWithNumberOfIds>();
        mapsOfEntriesByType.forEach((key, value) -> {
            InlineTaskIndexTaskType taskType = InlineTaskIndexTaskType.valueOf(key);
            allSearchIndexActions.addAll(this.getActionsForParticularTaskType(taskType, (List<JournalEntry>)value));
        });
        return allSearchIndexActions;
    }

    private List<SearchIndexActionWithNumberOfIds> getActionsForParticularTaskType(InlineTaskIndexTaskType taskType, List<JournalEntry> journalEntries) {
        Set<Long> idsToReindex = this.extractIdsFromJournalRecords(journalEntries);
        ArrayList<SearchIndexActionWithNumberOfIds> allIndexActions = new ArrayList<SearchIndexActionWithNumberOfIds>();
        allIndexActions.add(this.createDeleteAction(taskType.taskLevel, idsToReindex));
        if (taskType.modificationType.isAdd) {
            idsToReindex.forEach(id -> allIndexActions.addAll(this.createActionForAddingInlineTasksToTheSearchIndex(taskType.taskLevel, (Long)id)));
        }
        return allIndexActions;
    }

    private Set<Long> extractIdsFromJournalRecords(List<JournalEntry> journalEntries) {
        return journalEntries.stream().map(entry -> {
            try {
                return Long.parseLong(entry.getMessage());
            }
            catch (Exception e) {
                log.error("unable to parse message (long value) for journal with id " + entry.getId());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private SearchIndexActionWithNumberOfIds createDeleteAction(TaskLevel taskLevel, Set<Long> ids) {
        switch (taskLevel) {
            case TASK: {
                return new DeleteTasksAction(ids);
            }
            case ANCESTOR: {
                return new DeletePageWithDescendantsAction(ids);
            }
            case PAGE: {
                return new DeleteContentIdAction(ids);
            }
        }
        throw new IllegalArgumentException("Unexpected task level for removal: " + taskLevel.name());
    }

    @Override
    public synchronized int flushQueue() {
        if (!this.pluginIsUpAndRunning.get()) {
            log.debug("Queue will not be processed because the plugin is not ready yet.");
            return 0;
        }
        log.trace("Flushing task index queue...");
        StopWatch stopWatch = StopWatch.createStarted();
        AtomicInteger numberOfProcessedEntries = new AtomicInteger();
        numberOfProcessedEntries.set(this.processJournalEntries());
        log.trace("Inline task index job flushed {} tasks. Duration: {}", (Object)numberOfProcessedEntries.get(), (Object)stopWatch);
        return numberOfProcessedEntries.get();
    }

    @Override
    public void requestFullReindexOnCurrentNode() {
        this.fullReindexRequiredOnCurrentNode.set(true);
    }

    private Collection<? extends SearchIndexActionWithNumberOfIds> createActionForAddingInlineTasksToTheSearchIndex(TaskLevel taskLevel, Long id) {
        switch (taskLevel) {
            case TASK: {
                return this.createActionForAddingSingleTask(id);
            }
            case PAGE: {
                return this.createActionsForAddingTasksFromPage(id);
            }
            case ANCESTOR: {
                return this.createActionForAddingTasksFromPageAndAllItsDescendants(id);
            }
        }
        throw new IllegalArgumentException("Unexpected task level for adding: " + taskLevel.name());
    }

    private Collection<? extends SearchIndexActionWithNumberOfIds> createActionForAddingTasksFromPageAndAllItsDescendants(Long ancestorId) {
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(ancestorId.longValue());
        if (contentEntityObject == null) {
            log.warn("Unable to find an ancestor page with id {}. Nothing will be indexed", (Object)ancestorId);
            return Collections.emptyList();
        }
        LinkedList<ContentEntityObject> pagesToProcess = new LinkedList<ContentEntityObject>();
        pagesToProcess.add(contentEntityObject);
        ArrayList<? extends SearchIndexActionWithNumberOfIds> actions = new ArrayList<SearchIndexActionWithNumberOfIds>();
        while (!pagesToProcess.isEmpty()) {
            ContentEntityObject contentEntityObjectToProcess = (ContentEntityObject)pagesToProcess.pollLast();
            actions.addAll(this.createActionsForAddingTasksFromPage(contentEntityObjectToProcess));
            if (!(contentEntityObjectToProcess instanceof Page)) continue;
            Page pageToProcess = (Page)contentEntityObjectToProcess;
            pagesToProcess.addAll(pageToProcess.getChildren());
        }
        return actions;
    }

    private Collection<? extends SearchIndexActionWithNumberOfIds> createActionForAddingSingleTask(Long globalTaskId) {
        AOInlineTask aoInlineTask = this.inlineTaskDao.get(globalTaskId);
        if (aoInlineTask == null) {
            log.warn("Unable to find task with id {}. Nothing will be indexed", (Object)globalTaskId);
            return Collections.emptyList();
        }
        long contentId = aoInlineTask.getContentId();
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(contentId);
        if (contentEntityObject == null) {
            log.warn("Unable to find page with id {}. Nothing will be indexed", (Object)contentId);
            return Collections.emptyList();
        }
        return Collections.singleton(new AddSearchDocumentForInlineTaskAction(this.inlineTaskSearchDocumentFactory, contentEntityObject, aoInlineTask));
    }

    private Collection<? extends SearchIndexActionWithNumberOfIds> createActionsForAddingTasksFromPage(Long contentId) {
        Collection<AOInlineTask> tasks = this.inlineTaskDao.getByContentId(contentId);
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(contentId.longValue());
        if (contentEntityObject == null) {
            log.warn("Unable to find page with id {}. Nothing will be indexed", (Object)contentId);
            return Collections.emptyList();
        }
        return tasks.stream().map(aoInlineTask -> new AddSearchDocumentForInlineTaskAction(this.inlineTaskSearchDocumentFactory, contentEntityObject, (AOInlineTask)aoInlineTask)).collect(Collectors.toList());
    }

    private Collection<? extends SearchIndexActionWithNumberOfIds> createActionsForAddingTasksFromPage(ContentEntityObject contentEntityObject) {
        Collection<AOInlineTask> tasks = this.inlineTaskDao.getByContentId(contentEntityObject.getId());
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks.stream().map(aoInlineTask -> new AddSearchDocumentForInlineTaskAction(this.inlineTaskSearchDocumentFactory, contentEntityObject, (AOInlineTask)aoInlineTask)).collect(Collectors.toList());
    }
}

