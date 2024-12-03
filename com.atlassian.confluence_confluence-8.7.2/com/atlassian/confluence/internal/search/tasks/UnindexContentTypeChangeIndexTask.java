/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.io.IOException;
import java.util.Optional;

@LuceneIndependent
@Internal
public class UnindexContentTypeChangeIndexTask
implements ConfluenceIndexTask {
    private static final JournalEntryType journalEntryType = JournalEntryType.UNINDEX_CONTENT_TYPE_CHANGE;
    private final String contentType;

    public UnindexContentTypeChangeIndexTask(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.contentType);
    }

    @Override
    public String getDescription() {
        return String.format("index.task.unindex.contentType.change.%s", this.contentType);
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        writer.delete(new TermQuery("type", this.contentType));
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CHANGE;
    }
}

