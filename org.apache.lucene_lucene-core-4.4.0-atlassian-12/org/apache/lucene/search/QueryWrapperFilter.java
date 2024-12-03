/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;

public class QueryWrapperFilter
extends Filter {
    private final Query query;

    public QueryWrapperFilter(Query query) {
        if (query == null) {
            throw new NullPointerException("Query may not be null");
        }
        this.query = query;
    }

    public final Query getQuery() {
        return this.query;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, final Bits acceptDocs) throws IOException {
        final AtomicReaderContext privateContext = context.reader().getContext();
        final Weight weight = new IndexSearcher(privateContext).createNormalizedWeight(this.query);
        return new DocIdSet(){

            @Override
            public DocIdSetIterator iterator() throws IOException {
                return weight.scorer(privateContext, true, false, acceptDocs);
            }

            @Override
            public boolean isCacheable() {
                return false;
            }
        };
    }

    public String toString() {
        return "QueryWrapperFilter(" + this.query + ")";
    }

    public boolean equals(Object o) {
        if (!(o instanceof QueryWrapperFilter)) {
            return false;
        }
        return this.query.equals(((QueryWrapperFilter)o).query);
    }

    public int hashCode() {
        return this.query.hashCode() ^ 0x923F64B9;
    }
}

