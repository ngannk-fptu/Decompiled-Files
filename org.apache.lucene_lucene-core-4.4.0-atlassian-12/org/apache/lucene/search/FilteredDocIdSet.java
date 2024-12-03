/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.FilteredDocIdSetIterator;
import org.apache.lucene.util.Bits;

public abstract class FilteredDocIdSet
extends DocIdSet {
    private final DocIdSet _innerSet;

    public FilteredDocIdSet(DocIdSet innerSet) {
        this._innerSet = innerSet;
    }

    @Override
    public boolean isCacheable() {
        return this._innerSet.isCacheable();
    }

    @Override
    public Bits bits() throws IOException {
        final Bits bits = this._innerSet.bits();
        return bits == null ? null : new Bits(){

            @Override
            public boolean get(int docid) {
                return bits.get(docid) && FilteredDocIdSet.this.match(docid);
            }

            @Override
            public int length() {
                return bits.length();
            }
        };
    }

    protected abstract boolean match(int var1);

    @Override
    public DocIdSetIterator iterator() throws IOException {
        DocIdSetIterator iterator = this._innerSet.iterator();
        if (iterator == null) {
            return null;
        }
        return new FilteredDocIdSetIterator(iterator){

            @Override
            protected boolean match(int docid) {
                return FilteredDocIdSet.this.match(docid);
            }
        };
    }
}

