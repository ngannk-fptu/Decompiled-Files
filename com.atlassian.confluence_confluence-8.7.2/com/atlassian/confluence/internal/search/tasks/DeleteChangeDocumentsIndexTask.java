/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.index.lucene.LuceneChangeExtractor;
import com.atlassian.confluence.internal.search.ChangeDocumentIdBuilder;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@LuceneIndependent
@Internal
public class DeleteChangeDocumentsIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private final HibernateHandle handle;
    private final ChangeDocumentIdBuilder changeDocumentIdBuilder;
    private static final JournalEntryType journalEntryType = JournalEntryType.DELETE_CHANGE_DOCUMENTS;

    public DeleteChangeDocumentsIndexTask(Searchable searchable) {
        if (!ChangeDocumentIndexPolicy.shouldUnIndex(searchable)) {
            throw new UnsupportedOperationException("this searchable is not supported: " + searchable);
        }
        if (!((Versioned)searchable).isLatestVersion()) {
            throw new UnsupportedOperationException("Only the latest version of a searchable is supported.");
        }
        this.handle = new HibernateHandle(searchable);
        this.changeDocumentIdBuilder = new ChangeDocumentIdBuilder();
    }

    public DeleteChangeDocumentsIndexTask(String handle) {
        try {
            this.handle = new HibernateHandle(handle);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Handle was invalid: " + handle);
        }
        this.changeDocumentIdBuilder = new ChangeDocumentIdBuilder();
    }

    @Override
    public Handle getHandle() {
        return this.handle;
    }

    @Override
    public String getDescription() {
        return "index.task.delete.changes";
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        writer.delete(new TermQuery(LuceneChangeExtractor.Mappings.CHANGE_DOCUMENT_GROUP_ID.getName(), this.changeDocumentIdBuilder.getGroupId((Handle)this.handle)));
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.handle.toString());
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.handle).append((Object)DeleteChangeDocumentsIndexTask.class.getName()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DeleteChangeDocumentsIndexTask)) {
            return false;
        }
        DeleteChangeDocumentsIndexTask that = (DeleteChangeDocumentsIndexTask)obj;
        return new EqualsBuilder().append((Object)this.handle, (Object)that.handle).isEquals();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("handle", (Object)this.handle).toString();
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CHANGE;
    }
}

