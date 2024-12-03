/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.HandleAware;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class RebuildChangeDocumentsIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private static final Logger log = LoggerFactory.getLogger(RebuildChangeDocumentsIndexTask.class);
    private static final JournalEntryType journalEntryType = JournalEntryType.REBUILD_CHANGE_DOCUMENTS;
    private final Handle handle;
    private final Searchable searchable;
    private final ContentEntityObjectDao contentEntityObjectDao;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final HibernateSessionManager5 hibernateSessionManager;
    private final SessionFactory sessionFactory;
    @VisibleForTesting
    int LAST_EDITED_VERSION_BATCH_SIZE = 100;

    public RebuildChangeDocumentsIndexTask(Searchable searchable, ContentEntityObjectDao contentEntityObjectDao, IndexTaskFactoryInternal indexTaskFactory, HibernateSessionManager5 hibernateSessionManager, SessionFactory sessionFactory) {
        this.hibernateSessionManager = hibernateSessionManager;
        this.sessionFactory = sessionFactory;
        if (searchable == null) {
            throw new IllegalArgumentException("searchable cannot be null.");
        }
        if (!ChangeDocumentIndexPolicy.shouldIndex(searchable)) {
            throw new IllegalArgumentException("searchable is not supported. Received: " + searchable);
        }
        this.searchable = searchable;
        this.handle = new HibernateHandle(searchable);
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.indexTaskFactory = indexTaskFactory;
    }

    @Override
    public Handle getHandle() {
        return this.handle;
    }

    @Override
    public String getDescription() {
        return "index.task.rebuild.change";
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        this.indexTaskFactory.createDeleteChangeDocumentsIndexTask(this.searchable).perform(writer);
        if (!(this.searchable instanceof ContentEntityObject)) {
            throw new UnsupportedOperationException(this.searchable + " not supported");
        }
        List<ContentEntityObject> searchables = this.contentEntityObjectDao.getLastEditedVersionsOf((ContentEntityObject)this.searchable);
        this.hibernateSessionManager.executeThenClearSessionWithoutCommitOrFlush(searchables, this.LAST_EDITED_VERSION_BATCH_SIZE, searchables.size(), ceo -> {
            if (ChangeDocumentIndexPolicy.shouldIndex((Searchable)ceo)) {
                try {
                    this.indexTaskFactory.createAddChangeDocumentTask((Searchable)ceo).perform(writer);
                }
                catch (IOException e) {
                    log.warn("Error when indexing change document for document with id: {} and last edited version id of: {}.", (Object)this.searchable.getId(), (Object)((Searchable)ceo).getId());
                }
            }
            return null;
        });
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.handle.toString());
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.handle).append((Object)RebuildChangeDocumentsIndexTask.class.getName()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RebuildChangeDocumentsIndexTask)) {
            return false;
        }
        RebuildChangeDocumentsIndexTask that = (RebuildChangeDocumentsIndexTask)obj;
        return new EqualsBuilder().append((Object)this.handle, (Object)that.handle).isEquals();
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CHANGE;
    }
}

