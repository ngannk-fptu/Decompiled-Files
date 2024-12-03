/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.HandleAware;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class UpdateDocumentIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private static final Logger log = LoggerFactory.getLogger(UpdateDocumentIndexTask.class);
    private final JournalEntryType journalEntryType;
    private final Searchable searchable;
    private final Handle handle;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final boolean includeDependents;

    public UpdateDocumentIndexTask(Searchable searchable, IndexTaskFactoryInternal indexTaskFactory) {
        this(searchable, indexTaskFactory, true);
    }

    public UpdateDocumentIndexTask(Searchable searchable, IndexTaskFactoryInternal indexTaskFactory, boolean includeDependents) {
        if (searchable == null) {
            throw new IllegalArgumentException("searchable cannot be null.");
        }
        if (!searchable.isIndexable()) {
            throw new IllegalArgumentException("The following item is not indexable: " + searchable);
        }
        if (indexTaskFactory == null) {
            throw new IllegalArgumentException("indexTaskFactory cannot be null.");
        }
        this.searchable = searchable;
        this.indexTaskFactory = indexTaskFactory;
        this.handle = new HibernateHandle(searchable);
        this.includeDependents = includeDependents;
        this.journalEntryType = includeDependents ? JournalEntryType.UPDATE_DOCUMENT : JournalEntryType.UPDATE_DOCUMENT_EXCLUDING_DEPENDENTS;
    }

    @Override
    public String getDescription() {
        return "index.task.update";
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        log.debug("Deleting document with handle: {}", (Object)this.handle.toString());
        this.indexTaskFactory.createDeleteDocumentTask(this.handle.toString()).perform(writer);
        log.debug("Adding document for searchable: {}", (Object)this.searchable);
        this.indexTaskFactory.createAddDocumentTask(this.searchable).perform(writer);
        if (this.includeDependents) {
            Collection dependants = this.searchable.getSearchableDependants();
            log.debug("Re-indexing {} dependants", (Object)dependants.size());
            for (Object s : dependants) {
                Searchable sble;
                if (!(s instanceof Searchable) || !(sble = (Searchable)s).isIndexable()) continue;
                log.trace("Re-indexing {}", (Object)sble);
                this.indexTaskFactory.createDeleteDocumentTask(sble).perform(writer);
                this.indexTaskFactory.createAddDocumentTask(sble).perform(writer);
            }
        }
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, this.journalEntryType, this.handle.toString());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof UpdateDocumentIndexTask)) {
            return false;
        }
        UpdateDocumentIndexTask that = (UpdateDocumentIndexTask)o;
        return this.handle.equals(that.handle) && this.includeDependents == that.includeDependents;
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)UpdateDocumentIndexTask.class.getName()).append((Object)this.handle).append(this.includeDependents).toHashCode();
    }

    @Override
    public Handle getHandle() {
        return this.handle;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("handle", (Object)this.handle).toString();
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CONTENT;
    }
}

