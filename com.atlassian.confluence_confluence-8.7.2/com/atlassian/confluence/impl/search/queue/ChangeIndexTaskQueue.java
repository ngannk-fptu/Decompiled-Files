/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.queue;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.queue.JournalIndexTaskQueue;
import com.atlassian.confluence.internal.search.tasks.NoOpIndexTask;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.IndexFlushRequester;
import com.atlassian.confluence.search.queue.JournalEntryType;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeIndexTaskQueue
extends JournalIndexTaskQueue {
    private static final Logger log = LoggerFactory.getLogger(ChangeIndexTaskQueue.class);

    public ChangeIndexTaskQueue(JournalService journalService, IndexTaskFactoryInternal indexTaskFactory, AnyTypeDao anyTypeDao, IndexFlushRequester indexFlushRequester, JournalIdentifier journalIdentifier) {
        super(journalService, indexTaskFactory, anyTypeDao, indexFlushRequester, journalIdentifier);
    }

    @Override
    protected ConfluenceIndexTask toTask(JournalEntry entry) {
        Optional<JournalEntryType> type = JournalEntryType.optionalFromId(entry.getType());
        if (!type.isPresent()) {
            log.error("Unsupported content index queue entry: {}", (Object)entry);
            return NoOpIndexTask.getChangeInstance();
        }
        switch (JournalEntryType.valueOf(entry.getType())) {
            case ADD_CHANGE_DOCUMENT: {
                return this.getTaskIfShouldIndex(entry, this.indexTaskFactory::createAddChangeDocumentTask);
            }
            case DELETE_CHANGE_DOCUMENTS: {
                return this.indexTaskFactory.createDeleteChangeDocumentsIndexTask(entry.getMessage());
            }
            case REBUILD_CHANGE_DOCUMENTS: {
                return this.getTaskIfShouldIndex(entry, this.indexTaskFactory::createRebuildChangeDocumentsIndexTask);
            }
            case UNINDEX_SPACE_CHANGE: {
                return this.indexTaskFactory.createUnIndexSpaceChangeIndexTask(entry.getMessage());
            }
            case REINDEX_ALL_USERS_CHANGE: {
                return this.indexTaskFactory.createReindexAllUsersChangeTask();
            }
            case REINDEX_ALL_BLOGS_CHANGE: {
                return this.indexTaskFactory.createReindexAllBlogsChangeTask();
            }
            case REINDEX_USERS_IN_GROUP_CHANGE: {
                return this.indexTaskFactory.createReindexUsersInGroupChangeTask(entry.getMessage());
            }
            case REINDEX_ALL_SPACES_CHANGE: {
                return this.indexTaskFactory.createReindexAllSpacesChangeTask();
            }
            case UNINDEX_CONTENT_TYPE_CHANGE: {
                return this.indexTaskFactory.createUnindexContentTypeChangeTask(entry.getMessage());
            }
        }
        log.error("Unsupported content index queue entry: {}", (Object)entry);
        return NoOpIndexTask.getChangeInstance();
    }

    @VisibleForTesting
    ConfluenceIndexTask getTaskIfShouldIndex(JournalEntry entry, Function<Searchable, ConfluenceIndexTask> taskFinder) {
        Searchable searchable = this.getSearchableFromEntry(entry);
        if (ChangeDocumentIndexPolicy.shouldIndex(searchable)) {
            return taskFinder.apply(searchable);
        }
        return NoOpIndexTask.getChangeInstance();
    }
}

