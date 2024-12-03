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
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;
import java.util.Optional;

@LuceneIndependent
@Internal
public class NoOpIndexTask
implements ConfluenceIndexTask {
    private static final NoOpIndexTask contentInstance = new NoOpIndexTask(SearchIndex.CONTENT);
    private static final NoOpIndexTask changeInstance = new NoOpIndexTask(SearchIndex.CHANGE);
    private final SearchIndex searchIndex;

    public static NoOpIndexTask getContentInstance() {
        return contentInstance;
    }

    public static NoOpIndexTask getChangeInstance() {
        return changeInstance;
    }

    @Deprecated
    public static NoOpIndexTask getInstance() {
        return NoOpIndexTask.getContentInstance();
    }

    private NoOpIndexTask(SearchIndex searchIndex) {
        this.searchIndex = searchIndex;
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "index.task.no.op";
    }

    @Override
    public SearchIndex getSearchIndex() {
        return this.searchIndex;
    }
}

