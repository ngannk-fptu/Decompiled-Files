/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;

public final class SearcherManager
extends ReferenceManager<IndexSearcher> {
    private final SearcherFactory searcherFactory;

    public SearcherManager(IndexWriter writer, boolean applyAllDeletes, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = SearcherManager.getSearcher(searcherFactory, DirectoryReader.open(writer, applyAllDeletes));
    }

    public SearcherManager(Directory dir, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = SearcherManager.getSearcher(searcherFactory, DirectoryReader.open(dir));
    }

    @Override
    protected void decRef(IndexSearcher reference) throws IOException {
        reference.getIndexReader().decRef();
    }

    @Override
    protected IndexSearcher refreshIfNeeded(IndexSearcher referenceToRefresh) throws IOException {
        IndexReader r = referenceToRefresh.getIndexReader();
        assert (r instanceof DirectoryReader) : "searcher's IndexReader should be a DirectoryReader, but got " + r;
        DirectoryReader newReader = DirectoryReader.openIfChanged((DirectoryReader)r);
        if (newReader == null) {
            return null;
        }
        return SearcherManager.getSearcher(this.searcherFactory, newReader);
    }

    @Override
    protected boolean tryIncRef(IndexSearcher reference) {
        return reference.getIndexReader().tryIncRef();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isSearcherCurrent() throws IOException {
        IndexSearcher searcher = (IndexSearcher)this.acquire();
        try {
            IndexReader r = searcher.getIndexReader();
            assert (r instanceof DirectoryReader) : "searcher's IndexReader should be a DirectoryReader, but got " + r;
            boolean bl = ((DirectoryReader)r).isCurrent();
            return bl;
        }
        finally {
            this.release(searcher);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static IndexSearcher getSearcher(SearcherFactory searcherFactory, IndexReader reader) throws IOException {
        IndexSearcher searcher;
        boolean success = false;
        try {
            searcher = searcherFactory.newSearcher(reader);
            if (searcher.getIndexReader() != reader) {
                throw new IllegalStateException("SearcherFactory must wrap exactly the provided reader (got " + searcher.getIndexReader() + " but expected " + reader + ")");
            }
            success = true;
        }
        finally {
            if (!success) {
                reader.decRef();
            }
        }
        return searcher;
    }
}

