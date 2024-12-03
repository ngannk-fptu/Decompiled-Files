/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.FilteredDocIdSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.OpenBitSet;

public abstract class FieldCacheDocIdSet
extends DocIdSet {
    protected final int maxDoc;
    protected final Bits acceptDocs;

    public FieldCacheDocIdSet(int maxDoc, Bits acceptDocs) {
        this.maxDoc = maxDoc;
        this.acceptDocs = acceptDocs;
    }

    protected abstract boolean matchDoc(int var1);

    @Override
    public final boolean isCacheable() {
        return true;
    }

    @Override
    public final Bits bits() {
        return this.acceptDocs == null ? new Bits(){

            @Override
            public boolean get(int docid) {
                return FieldCacheDocIdSet.this.matchDoc(docid);
            }

            @Override
            public int length() {
                return FieldCacheDocIdSet.this.maxDoc;
            }
        } : new Bits(){

            @Override
            public boolean get(int docid) {
                return FieldCacheDocIdSet.this.matchDoc(docid) && FieldCacheDocIdSet.this.acceptDocs.get(docid);
            }

            @Override
            public int length() {
                return FieldCacheDocIdSet.this.maxDoc;
            }
        };
    }

    @Override
    public final DocIdSetIterator iterator() throws IOException {
        if (this.acceptDocs == null) {
            return new DocIdSetIterator(){
                private int doc = -1;

                @Override
                public int docID() {
                    return this.doc;
                }

                @Override
                public int nextDoc() {
                    do {
                        ++this.doc;
                        if (this.doc < FieldCacheDocIdSet.this.maxDoc) continue;
                        this.doc = Integer.MAX_VALUE;
                        return Integer.MAX_VALUE;
                    } while (!FieldCacheDocIdSet.this.matchDoc(this.doc));
                    return this.doc;
                }

                @Override
                public int advance(int target) {
                    this.doc = target;
                    while (this.doc < FieldCacheDocIdSet.this.maxDoc) {
                        if (FieldCacheDocIdSet.this.matchDoc(this.doc)) {
                            return this.doc;
                        }
                        ++this.doc;
                    }
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }

                @Override
                public long cost() {
                    return FieldCacheDocIdSet.this.maxDoc;
                }
            };
        }
        if (this.acceptDocs instanceof FixedBitSet || this.acceptDocs instanceof OpenBitSet) {
            return new FilteredDocIdSetIterator(((DocIdSet)((Object)this.acceptDocs)).iterator()){

                @Override
                protected boolean match(int doc) {
                    return FieldCacheDocIdSet.this.matchDoc(doc);
                }
            };
        }
        return new DocIdSetIterator(){
            private int doc = -1;

            @Override
            public int docID() {
                return this.doc;
            }

            @Override
            public int nextDoc() {
                do {
                    ++this.doc;
                    if (this.doc < FieldCacheDocIdSet.this.maxDoc) continue;
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                } while (!FieldCacheDocIdSet.this.matchDoc(this.doc) || !FieldCacheDocIdSet.this.acceptDocs.get(this.doc));
                return this.doc;
            }

            @Override
            public int advance(int target) {
                this.doc = target;
                while (this.doc < FieldCacheDocIdSet.this.maxDoc) {
                    if (FieldCacheDocIdSet.this.matchDoc(this.doc) && FieldCacheDocIdSet.this.acceptDocs.get(this.doc)) {
                        return this.doc;
                    }
                    ++this.doc;
                }
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }

            @Override
            public long cost() {
                return FieldCacheDocIdSet.this.maxDoc;
            }
        };
    }
}

