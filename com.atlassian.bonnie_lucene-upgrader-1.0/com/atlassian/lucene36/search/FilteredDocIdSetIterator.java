/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.DocIdSetIterator;
import java.io.IOException;

public abstract class FilteredDocIdSetIterator
extends DocIdSetIterator {
    protected DocIdSetIterator _innerIter;
    private int doc;

    public FilteredDocIdSetIterator(DocIdSetIterator innerIter) {
        if (innerIter == null) {
            throw new IllegalArgumentException("null iterator");
        }
        this._innerIter = innerIter;
        this.doc = -1;
    }

    protected abstract boolean match(int var1) throws IOException;

    public int docID() {
        return this.doc;
    }

    public int nextDoc() throws IOException {
        while ((this.doc = this._innerIter.nextDoc()) != Integer.MAX_VALUE) {
            if (!this.match(this.doc)) continue;
            return this.doc;
        }
        return this.doc;
    }

    public int advance(int target) throws IOException {
        this.doc = this._innerIter.advance(target);
        if (this.doc != Integer.MAX_VALUE) {
            if (this.match(this.doc)) {
                return this.doc;
            }
            while ((this.doc = this._innerIter.nextDoc()) != Integer.MAX_VALUE) {
                if (!this.match(this.doc)) continue;
                return this.doc;
            }
            return this.doc;
        }
        return this.doc;
    }
}

