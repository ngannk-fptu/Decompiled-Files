/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.search.ConfluenceIndexer;

public class IndexingBatchTask<T extends Searchable>
implements BatchTask<T> {
    private final BatchTask<T> delegate;
    private final ConfluenceIndexer indexer;

    public IndexingBatchTask(BatchTask<T> delegate, ConfluenceIndexer indexer) {
        this.delegate = delegate;
        this.indexer = indexer;
    }

    @Override
    public boolean apply(T item, int index, int batchSize) throws Exception {
        boolean result = this.delegate.apply(item, index, batchSize);
        this.indexer.reIndex((Searchable)item);
        return result;
    }
}

