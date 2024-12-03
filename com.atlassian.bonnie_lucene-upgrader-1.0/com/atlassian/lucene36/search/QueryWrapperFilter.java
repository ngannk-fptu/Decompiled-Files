/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.IndexSearcher;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;

public class QueryWrapperFilter
extends Filter {
    private Query query;

    public QueryWrapperFilter(Query query) {
        this.query = query;
    }

    public DocIdSet getDocIdSet(final IndexReader reader) throws IOException {
        final Weight weight = new IndexSearcher(reader).createNormalizedWeight(this.query);
        return new DocIdSet(){

            public DocIdSetIterator iterator() throws IOException {
                return weight.scorer(reader, true, false);
            }

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

