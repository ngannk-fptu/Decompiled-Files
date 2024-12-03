/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import java.io.IOException;

public abstract class FieldCacheDocIdSet
extends DocIdSet {
    protected final IndexReader reader;

    public FieldCacheDocIdSet(IndexReader reader) {
        this.reader = reader;
    }

    protected abstract boolean matchDoc(int var1);

    public final boolean isCacheable() {
        return !this.reader.hasDeletions();
    }

    public final DocIdSetIterator iterator() throws IOException {
        if (!this.reader.hasDeletions()) {
            final int maxDoc = this.reader.maxDoc();
            return new DocIdSetIterator(){
                private int doc = -1;

                public int docID() {
                    return this.doc;
                }

                public int nextDoc() {
                    do {
                        ++this.doc;
                        if (this.doc < maxDoc) continue;
                        this.doc = Integer.MAX_VALUE;
                        return Integer.MAX_VALUE;
                    } while (!FieldCacheDocIdSet.this.matchDoc(this.doc));
                    return this.doc;
                }

                public int advance(int target) {
                    this.doc = target;
                    while (this.doc < maxDoc) {
                        if (FieldCacheDocIdSet.this.matchDoc(this.doc)) {
                            return this.doc;
                        }
                        ++this.doc;
                    }
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
            };
        }
        final TermDocs termDocs = this.reader.termDocs(null);
        return new DocIdSetIterator(){
            private int doc = -1;

            public int docID() {
                return this.doc;
            }

            public int nextDoc() throws IOException {
                do {
                    if (termDocs.next()) continue;
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                } while (!FieldCacheDocIdSet.this.matchDoc(this.doc = termDocs.doc()));
                return this.doc;
            }

            public int advance(int target) throws IOException {
                if (!termDocs.skipTo(target)) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                while (!FieldCacheDocIdSet.this.matchDoc(this.doc = termDocs.doc())) {
                    if (termDocs.next()) continue;
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                return this.doc;
            }
        };
    }
}

