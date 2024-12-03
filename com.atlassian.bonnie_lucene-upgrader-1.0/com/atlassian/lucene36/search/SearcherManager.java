/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.search.IndexSearcher;
import com.atlassian.lucene36.search.ReferenceManager;
import com.atlassian.lucene36.search.SearcherFactory;
import com.atlassian.lucene36.store.Directory;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SearcherManager
extends ReferenceManager<IndexSearcher> {
    private final SearcherFactory searcherFactory;

    public SearcherManager(IndexWriter writer, boolean applyAllDeletes, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = SearcherManager.getSearcher(searcherFactory, IndexReader.open(writer, applyAllDeletes));
    }

    public SearcherManager(Directory dir, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = SearcherManager.getSearcher(searcherFactory, IndexReader.open(dir));
    }

    @Override
    protected void decRef(IndexSearcher reference) throws IOException {
        reference.getIndexReader().decRef();
    }

    @Override
    protected IndexSearcher refreshIfNeeded(IndexSearcher referenceToRefresh) throws IOException {
        IndexReader newReader = IndexReader.openIfChanged(referenceToRefresh.getIndexReader());
        if (newReader == null) {
            return null;
        }
        return SearcherManager.getSearcher(this.searcherFactory, newReader);
    }

    @Override
    protected boolean tryIncRef(IndexSearcher reference) {
        return reference.getIndexReader().tryIncRef();
    }

    @Deprecated
    public boolean maybeReopen() throws IOException {
        return this.maybeRefresh();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isSearcherCurrent() throws IOException {
        boolean bl;
        IndexSearcher searcher = (IndexSearcher)this.acquire();
        try {
            bl = searcher.getIndexReader().isCurrent();
            Object var4_3 = null;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            this.release(searcher);
            throw throwable;
        }
        this.release(searcher);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static IndexSearcher getSearcher(SearcherFactory searcherFactory, IndexReader reader) throws IOException {
        boolean success = false;
        try {
            IndexSearcher searcher = searcherFactory.newSearcher(reader);
            if (searcher.getIndexReader() == reader) return searcher;
            throw new IllegalStateException("SearcherFactory must wrap exactly the provided reader (got " + searcher.getIndexReader() + " but expected " + reader + ")");
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (success) throw throwable;
            reader.decRef();
            throw throwable;
        }
    }
}

