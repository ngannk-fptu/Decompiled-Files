/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.FilteredDocIdSetIterator;
import java.io.IOException;

public abstract class FilteredDocIdSet
extends DocIdSet {
    private final DocIdSet _innerSet;

    public FilteredDocIdSet(DocIdSet innerSet) {
        this._innerSet = innerSet;
    }

    public boolean isCacheable() {
        return this._innerSet.isCacheable();
    }

    protected abstract boolean match(int var1) throws IOException;

    public DocIdSetIterator iterator() throws IOException {
        DocIdSetIterator iterator = this._innerSet.iterator();
        if (iterator == null) {
            return null;
        }
        return new FilteredDocIdSetIterator(iterator){

            protected boolean match(int docid) throws IOException {
                return FilteredDocIdSet.this.match(docid);
            }
        };
    }
}

