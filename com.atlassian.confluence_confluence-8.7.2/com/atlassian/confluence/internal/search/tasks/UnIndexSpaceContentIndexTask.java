/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.internal.search.tasks.SpaceKeyHandle;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.HandleAware;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@LuceneIndependent
@Internal
public class UnIndexSpaceContentIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private static final JournalEntryType journalEntryType = JournalEntryType.UNINDEX_SPACE;
    private final Handle handle;
    private final String spaceKey;

    public UnIndexSpaceContentIndexTask(String spaceKey) {
        this.handle = new SpaceKeyHandle(spaceKey);
        this.spaceKey = spaceKey;
    }

    @Override
    public String getDescription() {
        return "index.task.unindex.space.content";
    }

    @Override
    public void perform(SearchIndexWriter writer) throws IOException {
        writer.delete(new TermQuery("spacekey", this.spaceKey));
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
        if (!(o instanceof UnIndexSpaceContentIndexTask)) {
            return false;
        }
        UnIndexSpaceContentIndexTask that = (UnIndexSpaceContentIndexTask)o;
        return this.handle.equals(that.handle);
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)UnIndexSpaceContentIndexTask.class.getName()).append((Object)this.handle).toHashCode();
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

