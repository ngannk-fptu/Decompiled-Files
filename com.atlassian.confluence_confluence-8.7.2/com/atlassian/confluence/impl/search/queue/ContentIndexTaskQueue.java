/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.queue;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.queue.JournalIndexTaskQueue;
import com.atlassian.confluence.internal.search.tasks.NoOpIndexTask;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.IndexFlushRequester;
import com.atlassian.confluence.search.queue.JournalEntryType;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentIndexTaskQueue
extends JournalIndexTaskQueue {
    private static final Logger log = LoggerFactory.getLogger(ContentIndexTaskQueue.class);

    public ContentIndexTaskQueue(JournalService journalService, IndexTaskFactoryInternal indexTaskFactory, AnyTypeDao anyTypeDao, IndexFlushRequester indexFlushRequester, JournalIdentifier journalIdentifier) {
        super(journalService, indexTaskFactory, anyTypeDao, indexFlushRequester, journalIdentifier);
    }

    @Override
    protected ConfluenceIndexTask toTask(JournalEntry entry) {
        Optional<JournalEntryType> type = JournalEntryType.optionalFromId(entry.getType());
        if (!type.isPresent()) {
            log.error("Unsupported content index queue entry: {}", (Object)entry);
            return NoOpIndexTask.getContentInstance();
        }
        switch (type.get()) {
            case ADD_DOCUMENT: {
                return this.indexTaskFactory.createAddDocumentTask(this.getSearchableFromEntry(entry));
            }
            case DELETE_DOCUMENT: {
                return this.indexTaskFactory.createDeleteDocumentTask(entry.getMessage());
            }
            case UPDATE_DOCUMENT: {
                return this.indexTaskFactory.createUpdateDocumentTask(this.getSearchableFromEntry(entry));
            }
            case UPDATE_DOCUMENT_EXCLUDING_DEPENDENTS: {
                return this.indexTaskFactory.createUpdateDocumentTask(this.getSearchableFromEntry(entry), false);
            }
            case UNINDEX_SPACE: {
                return this.indexTaskFactory.createUnIndexSpaceContentIndexTask(entry.getMessage());
            }
            case REINDEX_ALL_USERS: {
                return this.indexTaskFactory.createReindexAllUsersContentTask();
            }
            case REINDEX_USERS_IN_GROUP: {
                return this.indexTaskFactory.createReindexUsersInGroupContentTask(entry.getMessage());
            }
            case REINDEX_ALL_SPACES: {
                return this.indexTaskFactory.createReindexAllSpacesContentTask();
            }
            case UNINDEX_CONTENT_TYPE: {
                return this.indexTaskFactory.createUnindexContentTypeContentTask(entry.getMessage());
            }
            case REINDEX_ALL_BLOGS: {
                return this.indexTaskFactory.createReindexAllBlogsContentTask();
            }
            case INDEX_DRAFTS: {
                return this.indexTaskFactory.createIndexDraftsTask();
            }
        }
        log.error("Unsupported content index queue entry: {}", (Object)entry);
        return NoOpIndexTask.getContentInstance();
    }
}

