/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.util.ArrayUtil;
import java.io.IOException;
import java.util.BitSet;

public class SortedVIntList
extends DocIdSet {
    static final int BITS2VINTLIST_SIZE = 8;
    private int size;
    private byte[] bytes;
    private int lastBytePos;
    private static final int VB1 = 127;
    private static final int BIT_SHIFT = 7;
    private final int MAX_BYTES_PER_INT = 5;

    public SortedVIntList(int ... sortedInts) {
        this(sortedInts, sortedInts.length);
    }

    public SortedVIntList(int[] sortedInts, int inputSize) {
        SortedVIntListBuilder builder = new SortedVIntListBuilder();
        for (int i = 0; i < inputSize; ++i) {
            builder.addInt(sortedInts[i]);
        }
        builder.done();
    }

    public SortedVIntList(BitSet bits) {
        SortedVIntListBuilder builder = new SortedVIntListBuilder();
        int nextInt = bits.nextSetBit(0);
        while (nextInt != -1) {
            builder.addInt(nextInt);
            nextInt = bits.nextSetBit(nextInt + 1);
        }
        builder.done();
    }

    public SortedVIntList(DocIdSetIterator docIdSetIterator) throws IOException {
        int doc;
        SortedVIntListBuilder builder = new SortedVIntListBuilder();
        while ((doc = docIdSetIterator.nextDoc()) != Integer.MAX_VALUE) {
            builder.addInt(doc);
        }
        builder.done();
    }

    private void initBytes() {
        this.size = 0;
        this.bytes = new byte[128];
        this.lastBytePos = 0;
    }

    private void resizeBytes(int newSize) {
        if (newSize != this.bytes.length) {
            byte[] newBytes = new byte[newSize];
            System.arraycopy(this.bytes, 0, newBytes, 0, this.lastBytePos);
            this.bytes = newBytes;
        }
    }

    public int size() {
        return this.size;
    }

    public int getByteSize() {
        return this.bytes.length;
    }

    public boolean isCacheable() {
        return true;
    }

    public DocIdSetIterator iterator() {
        return new DocIdSetIterator(){
            int bytePos = 0;
            int lastInt = 0;
            int doc = -1;

            private void advance() {
                byte b = SortedVIntList.this.bytes[this.bytePos++];
                this.lastInt += b & 0x7F;
                int s = 7;
                while ((b & 0xFFFFFF80) != 0) {
                    b = SortedVIntList.this.bytes[this.bytePos++];
                    this.lastInt += (b & 0x7F) << s;
                    s += 7;
                }
            }

            public int docID() {
                return this.doc;
            }

            public int nextDoc() {
                if (this.bytePos >= SortedVIntList.this.lastBytePos) {
                    this.doc = Integer.MAX_VALUE;
                } else {
                    this.advance();
                    this.doc = this.lastInt;
                }
                return this.doc;
            }

            public int advance(int target) {
                while (this.bytePos < SortedVIntList.this.lastBytePos) {
                    this.advance();
                    if (this.lastInt < target) continue;
                    this.doc = this.lastInt;
                    return this.doc;
                }
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
        };
    }

    private class SortedVIntListBuilder {
        private int lastInt = 0;

        SortedVIntListBuilder() {
            SortedVIntList.this.initBytes();
            this.lastInt = 0;
        }

        void addInt(int nextInt) {
            int diff = nextInt - this.lastInt;
            if (diff < 0) {
                throw new IllegalArgumentException("Input not sorted or first element negative.");
            }
            if (SortedVIntList.this.lastBytePos + 5 > SortedVIntList.this.bytes.length) {
                SortedVIntList.this.resizeBytes(ArrayUtil.oversize(SortedVIntList.this.lastBytePos + 5, 1));
            }
            while ((diff & 0xFFFFFF80) != 0) {
                ((SortedVIntList)SortedVIntList.this).bytes[((SortedVIntList)SortedVIntList.this).lastBytePos++] = (byte)(diff & 0x7F | 0xFFFFFF80);
                diff >>>= 7;
            }
            ((SortedVIntList)SortedVIntList.this).bytes[((SortedVIntList)SortedVIntList.this).lastBytePos++] = (byte)diff;
            SortedVIntList.this.size++;
            this.lastInt = nextInt;
        }

        void done() {
            SortedVIntList.this.resizeBytes(SortedVIntList.this.lastBytePos);
        }
    }
}

