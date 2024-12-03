/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;

public abstract class DocIdSetIterator {
    public static final int NO_MORE_DOCS = Integer.MAX_VALUE;

    public static final DocIdSetIterator empty() {
        return new DocIdSetIterator(){
            boolean exhausted = false;

            @Override
            public int advance(int target) {
                assert (!this.exhausted);
                assert (target >= 0);
                this.exhausted = true;
                return Integer.MAX_VALUE;
            }

            @Override
            public int docID() {
                return this.exhausted ? Integer.MAX_VALUE : -1;
            }

            @Override
            public int nextDoc() {
                assert (!this.exhausted);
                this.exhausted = true;
                return Integer.MAX_VALUE;
            }

            @Override
            public long cost() {
                return 0L;
            }
        };
    }

    public abstract int docID();

    public abstract int nextDoc() throws IOException;

    public abstract int advance(int var1) throws IOException;

    protected final int slowAdvance(int target) throws IOException {
        int doc;
        assert (this.docID() == Integer.MAX_VALUE || this.docID() < target);
        while ((doc = this.nextDoc()) < target) {
        }
        return doc;
    }

    public abstract long cost();
}

