/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.BitSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;

public class DocIdBitSet
extends DocIdSet
implements Bits {
    private final BitSet bitSet;

    public DocIdBitSet(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    @Override
    public DocIdSetIterator iterator() {
        return new DocIdBitSetIterator(this.bitSet);
    }

    @Override
    public Bits bits() {
        return this;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    public BitSet getBitSet() {
        return this.bitSet;
    }

    @Override
    public boolean get(int index) {
        return this.bitSet.get(index);
    }

    @Override
    public int length() {
        return this.bitSet.size();
    }

    private static class DocIdBitSetIterator
    extends DocIdSetIterator {
        private int docId;
        private BitSet bitSet;

        DocIdBitSetIterator(BitSet bitSet) {
            this.bitSet = bitSet;
            this.docId = -1;
        }

        @Override
        public int docID() {
            return this.docId;
        }

        @Override
        public int nextDoc() {
            int d = this.bitSet.nextSetBit(this.docId + 1);
            this.docId = d == -1 ? Integer.MAX_VALUE : d;
            return this.docId;
        }

        @Override
        public int advance(int target) {
            int d = this.bitSet.nextSetBit(target);
            this.docId = d == -1 ? Integer.MAX_VALUE : d;
            return this.docId;
        }

        @Override
        public long cost() {
            return this.bitSet.length();
        }
    }
}

