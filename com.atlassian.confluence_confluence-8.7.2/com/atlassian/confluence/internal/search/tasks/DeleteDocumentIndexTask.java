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
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.index.v2.AtlassianContentDocumentBuilder;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.HandleAware;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@LuceneIndependent
@Internal
public class DeleteDocumentIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private final Handle handle;
    private static final JournalEntryType journalEntryType = JournalEntryType.DELETE_DOCUMENT;

    public DeleteDocumentIndexTask(String handle) {
        try {
            this.handle = new HibernateHandle(handle);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Handle was invalid: " + handle);
        }
    }

    public DeleteDocumentIndexTask(Searchable target) {
        this.handle = new HibernateHandle(target);
    }

    @Override
    public String getDescription() {
        return "index.task.delete.content";
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        writer.delete(new TermQuery(AtlassianContentDocumentBuilder.FieldMappings.CONTENT_DOCUMENT_ID.getName(), this.handle.toString()));
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.handle.toString());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof DeleteDocumentIndexTask)) {
            return false;
        }
        DeleteDocumentIndexTask that = (DeleteDocumentIndexTask)o;
        return this.handle.equals(that.handle);
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)DeleteDocumentIndexTask.class.getName()).append((Object)this.handle).toHashCode();
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

