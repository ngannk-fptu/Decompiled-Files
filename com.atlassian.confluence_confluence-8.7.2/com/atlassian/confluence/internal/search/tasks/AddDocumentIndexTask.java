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
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.HandleAware;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class AddDocumentIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private static final Logger log = LoggerFactory.getLogger(AddDocumentIndexTask.class);
    private static final JournalEntryType journalEntryType = JournalEntryType.ADD_DOCUMENT;
    private final AtlassianDocumentBuilder<Searchable> documentBuilder;
    private final Searchable searchable;
    private final HibernateHandle handle;

    public AddDocumentIndexTask(Searchable searchable, AtlassianDocumentBuilder<Searchable> documentBuilder) {
        if (searchable == null) {
            throw new IllegalArgumentException("searchable cannot be null.");
        }
        if (!searchable.isIndexable()) {
            throw new IllegalArgumentException("Item is not indexable.");
        }
        this.searchable = searchable;
        this.documentBuilder = documentBuilder;
        this.handle = new HibernateHandle(searchable);
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        if (this.searchable.isIndexable()) {
            log.debug("perform was skipped because document is not indexable");
        }
        try {
            writer.add(this.documentBuilder.build(this.searchable));
        }
        catch (Exception e) {
            log.warn("Error getting document from searchable", (Throwable)e);
        }
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.handle.toString());
    }

    @Override
    public String getDescription() {
        return "index.task.add";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof AddDocumentIndexTask)) {
            return false;
        }
        AddDocumentIndexTask that = (AddDocumentIndexTask)o;
        return this.handle.equals((Object)that.handle);
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)AddDocumentIndexTask.class.getName()).append((Object)this.handle).toHashCode();
    }

    @Override
    public Handle getHandle() {
        return this.handle;
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CONTENT;
    }
}

