/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.search.ConvertibleToJournalEntry;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;

public interface ConfluenceIndexTask
extends IndexTask,
ConvertibleToJournalEntry {
    public SearchIndex getSearchIndex();

    public void perform(SearchIndexWriter var1) throws IOException;
}

