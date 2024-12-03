/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.util.Bits
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;

public class EmptyDocIdSet
extends DocIdSet {
    public DocIdSetIterator iterator() {
        return new DocIdSetIterator(){
            boolean exhausted = false;

            public int advance(int target) {
                assert (!this.exhausted);
                assert (target >= 0);
                this.exhausted = true;
                return Integer.MAX_VALUE;
            }

            public int docID() {
                return this.exhausted ? Integer.MAX_VALUE : -1;
            }

            public int nextDoc() {
                assert (!this.exhausted);
                this.exhausted = true;
                return Integer.MAX_VALUE;
            }

            public long cost() {
                return 0L;
            }
        };
    }

    public boolean isCacheable() {
        return true;
    }

    public Bits bits() {
        return null;
    }
}

