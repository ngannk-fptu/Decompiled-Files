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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
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
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class AddChangeDocumentIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private static final Logger log = LoggerFactory.getLogger(AddChangeDocumentIndexTask.class);
    private final Handle handle;
    private final Searchable searchable;
    private final AtlassianDocumentBuilder<Searchable> changeDocumentBuilder;
    private final ChangeDocumentIdBuilder changeDocumentIdBuilder;
    private static final JournalEntryType journalEntryType = JournalEntryType.ADD_CHANGE_DOCUMENT;

    public AddChangeDocumentIndexTask(Searchable searchable, AtlassianDocumentBuilder<Searchable> changeDocumentBuilder) {
        if (searchable == null) {
            throw new IllegalArgumentException("searchable cannot be null.");
        }
        this.searchable = searchable;
        this.handle = new HibernateHandle(searchable);
        this.changeDocumentBuilder = changeDocumentBuilder;
        this.changeDocumentIdBuilder = new ChangeDocumentIdBuilder();
    }

    @Override
    public Handle getHandle() {
        return this.handle;
    }

    @Override
    public String getDescription() {
        return "index.task.add.change";
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        log.debug("Re-indexing {}", (Object)this.searchable);
        ChangeDocumentIndexPolicy.PolicyCheckResult policyCheckResult = ChangeDocumentIndexPolicy.buildFor(this.searchable);
        if (policyCheckResult.failed()) {
            log.debug("searchable: {} is not supported: {}", (Object)this.searchable, (Object)policyCheckResult.getErrorMessage());
            return;
        }
        if (this.searchable instanceof Versioned) {
            writer.delete(new TermQuery(LuceneChangeExtractor.Mappings.CHANGE_DOCUMENT_AND_AUTHOR_ID.getName(), this.changeDocumentIdBuilder.getChangeDocumentAndAuthorId(this.searchable)));
        }
        writer.add(this.changeDocumentBuilder.build(this.searchable));
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.handle.toString());
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.handle).append((Object)AddChangeDocumentIndexTask.class.getName()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AddChangeDocumentIndexTask)) {
            return false;
        }
        AddChangeDocumentIndexTask that = (AddChangeDocumentIndexTask)obj;
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

