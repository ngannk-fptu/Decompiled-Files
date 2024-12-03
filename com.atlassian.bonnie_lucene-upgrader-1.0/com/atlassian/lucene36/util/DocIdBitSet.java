/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import java.util.BitSet;

public class DocIdBitSet
extends DocIdSet {
    private BitSet bitSet;

    public DocIdBitSet(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public DocIdSetIterator iterator() {
        return new DocIdBitSetIterator(this.bitSet);
    }

    public boolean isCacheable() {
        return true;
    }

    public BitSet getBitSet() {
        return this.bitSet;
    }

    private static class DocIdBitSetIterator
    extends DocIdSetIterator {
        private int docId;
        private BitSet bitSet;

        DocIdBitSetIterator(BitSet bitSet) {
            this.bitSet = bitSet;
            this.docId = -1;
        }

        public int docID() {
            return this.docId;
        }

        public int nextDoc() {
            int d = this.bitSet.nextSetBit(this.docId + 1);
            this.docId = d == -1 ? Integer.MAX_VALUE : d;
            return this.docId;
        }

        public int advance(int target) {
            int d = this.bitSet.nextSetBit(target);
            this.docId = d == -1 ? Integer.MAX_VALUE : d;
            return this.docId;
        }
    }
}

