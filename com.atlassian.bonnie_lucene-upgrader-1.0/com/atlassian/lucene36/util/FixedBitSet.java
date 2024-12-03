/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.util.BitUtil;
import com.atlassian.lucene36.util.Bits;
import com.atlassian.lucene36.util.OpenBitSetIterator;
import java.io.IOException;
import java.util.Arrays;

public final class FixedBitSet
extends DocIdSet
implements Bits {
    private final long[] bits;
    private int numBits;

    public static int bits2words(int numBits) {
        int numLong = numBits >>> 6;
        if ((numBits & 0x3F) != 0) {
            ++numLong;
        }
        return numLong;
    }

    public FixedBitSet(int numBits) {
        this.numBits = numBits;
        this.bits = new long[FixedBitSet.bits2words(numBits)];
    }

    public FixedBitSet(FixedBitSet other) {
        this.bits = new long[other.bits.length];
        System.arraycopy(other.bits, 0, this.bits, 0, this.bits.length);
        this.numBits = other.numBits;
    }

    public DocIdSetIterator iterator() {
        return new OpenBitSetIterator(this.bits, this.bits.length);
    }

    public int length() {
        return this.numBits;
    }

    public boolean isCacheable() {
        return true;
    }

    public long[] getBits() {
        return this.bits;
    }

    public int cardinality() {
        return (int)BitUtil.pop_array(this.bits, 0, this.bits.length);
    }

    public boolean get(int index) {
        assert (index >= 0 && index < this.numBits) : "index=" + index;
        int i = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        return (this.bits[i] & bitmask) != 0L;
    }

    public void set(int index) {
        assert (index >= 0 && index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
    }

    public boolean getAndSet(int index) {
        assert (index >= 0 && index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        boolean val = (this.bits[wordNum] & bitmask) != 0L;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
        return val;
    }

    public void clear(int index) {
        assert (index >= 0 && index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] & (bitmask ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public boolean getAndClear(int index) {
        assert (index >= 0 && index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        boolean val = (this.bits[wordNum] & bitmask) != 0L;
        int n = wordNum;
        this.bits[n] = this.bits[n] & (bitmask ^ 0xFFFFFFFFFFFFFFFFL);
        return val;
    }

    public int nextSetBit(int index) {
        assert (index >= 0 && index < this.numBits);
        int i = index >> 6;
        int subIndex = index & 0x3F;
        long word = this.bits[i] >> subIndex;
        if (word != 0L) {
            return (i << 6) + subIndex + BitUtil.ntz(word);
        }
        while (++i < this.bits.length) {
            word = this.bits[i];
            if (word == 0L) continue;
            return (i << 6) + BitUtil.ntz(word);
        }
        return -1;
    }

    public int prevSetBit(int index) {
        assert (index >= 0 && index < this.numBits) : "index=" + index + " numBits=" + this.numBits;
        int i = index >> 6;
        int subIndex = index & 0x3F;
        long word = this.bits[i] << 63 - subIndex;
        if (word != 0L) {
            return (i << 6) + subIndex - Long.numberOfLeadingZeros(word);
        }
        while (--i >= 0) {
            word = this.bits[i];
            if (word == 0L) continue;
            return (i << 6) + 63 - Long.numberOfLeadingZeros(word);
        }
        return -1;
    }

    public void or(DocIdSetIterator iter) throws IOException {
        if (iter instanceof OpenBitSetIterator && iter.docID() == -1) {
            OpenBitSetIterator obs = (OpenBitSetIterator)iter;
            this.or(obs.arr, obs.words);
            obs.advance(this.numBits);
        } else {
            int doc;
            while ((doc = iter.nextDoc()) < this.numBits) {
                this.set(doc);
            }
        }
    }

    public void or(FixedBitSet other) {
        this.or(other.bits, other.bits.length);
    }

    private void or(long[] otherArr, int otherLen) {
        long[] thisArr = this.bits;
        int pos = Math.min(thisArr.length, otherLen);
        while (--pos >= 0) {
            int n = pos;
            thisArr[n] = thisArr[n] | otherArr[pos];
        }
    }

    public void and(DocIdSetIterator iter) throws IOException {
        if (iter instanceof OpenBitSetIterator && iter.docID() == -1) {
            OpenBitSetIterator obs = (OpenBitSetIterator)iter;
            this.and(obs.arr, obs.words);
            obs.advance(this.numBits);
        } else {
            int disiDoc;
            if (this.numBits == 0) {
                return;
            }
            int bitSetDoc = this.nextSetBit(0);
            while (bitSetDoc != -1 && (disiDoc = iter.advance(bitSetDoc)) < this.numBits) {
                this.clear(bitSetDoc, disiDoc);
                bitSetDoc = ++disiDoc < this.numBits ? this.nextSetBit(disiDoc) : -1;
            }
            if (bitSetDoc != -1) {
                this.clear(bitSetDoc, this.numBits);
            }
        }
    }

    public void and(FixedBitSet other) {
        this.and(other.bits, other.bits.length);
    }

    private void and(long[] otherArr, int otherLen) {
        long[] thisArr = this.bits;
        int pos = Math.min(thisArr.length, otherLen);
        while (--pos >= 0) {
            int n = pos;
            thisArr[n] = thisArr[n] & otherArr[pos];
        }
        if (thisArr.length > otherLen) {
            Arrays.fill(thisArr, otherLen, thisArr.length, 0L);
        }
    }

    public void andNot(DocIdSetIterator iter) throws IOException {
        if (iter instanceof OpenBitSetIterator && iter.docID() == -1) {
            OpenBitSetIterator obs = (OpenBitSetIterator)iter;
            this.andNot(obs.arr, obs.words);
            obs.advance(this.numBits);
        } else {
            int doc;
            while ((doc = iter.nextDoc()) < this.numBits) {
                this.clear(doc);
            }
        }
    }

    public void andNot(FixedBitSet other) {
        this.andNot(other.bits, other.bits.length);
    }

    private void andNot(long[] otherArr, int otherLen) {
        long[] thisArr = this.bits;
        int pos = Math.min(thisArr.length, otherLen);
        while (--pos >= 0) {
            int n = pos;
            thisArr[n] = thisArr[n] & (otherArr[pos] ^ 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public void flip(int startIndex, int endIndex) {
        assert (startIndex >= 0 && startIndex < this.numBits);
        assert (endIndex >= 0 && endIndex <= this.numBits);
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = startIndex >> 6;
        int endWord = endIndex - 1 >> 6;
        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;
        if (startWord == endWord) {
            int n = startWord;
            this.bits[n] = this.bits[n] ^ startmask & endmask;
            return;
        }
        int n = startWord;
        this.bits[n] = this.bits[n] ^ startmask;
        for (int i = startWord + 1; i < endWord; ++i) {
            this.bits[i] = this.bits[i] ^ 0xFFFFFFFFFFFFFFFFL;
        }
        int n2 = endWord;
        this.bits[n2] = this.bits[n2] ^ endmask;
    }

    public void set(int startIndex, int endIndex) {
        assert (startIndex >= 0 && startIndex < this.numBits);
        assert (endIndex >= 0 && endIndex <= this.numBits);
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = startIndex >> 6;
        int endWord = endIndex - 1 >> 6;
        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;
        if (startWord == endWord) {
            int n = startWord;
            this.bits[n] = this.bits[n] | startmask & endmask;
            return;
        }
        int n = startWord;
        this.bits[n] = this.bits[n] | startmask;
        Arrays.fill(this.bits, startWord + 1, endWord, -1L);
        int n2 = endWord;
        this.bits[n2] = this.bits[n2] | endmask;
    }

    public void clear(int startIndex, int endIndex) {
        assert (startIndex >= 0 && startIndex < this.numBits);
        assert (endIndex >= 0 && endIndex <= this.numBits);
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = startIndex >> 6;
        int endWord = endIndex - 1 >> 6;
        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;
        startmask ^= 0xFFFFFFFFFFFFFFFFL;
        endmask ^= 0xFFFFFFFFFFFFFFFFL;
        if (startWord == endWord) {
            int n = startWord;
            this.bits[n] = this.bits[n] & (startmask | endmask);
            return;
        }
        int n = startWord;
        this.bits[n] = this.bits[n] & startmask;
        Arrays.fill(this.bits, startWord + 1, endWord, 0L);
        int n2 = endWord;
        this.bits[n2] = this.bits[n2] & endmask;
    }

    public Object clone() {
        return new FixedBitSet(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FixedBitSet)) {
            return false;
        }
        FixedBitSet other = (FixedBitSet)o;
        if (this.numBits != other.length()) {
            return false;
        }
        return Arrays.equals(this.bits, other.bits);
    }

    public int hashCode() {
        long h = 0L;
        int i = this.bits.length;
        while (--i >= 0) {
            h ^= this.bits[i];
            h = h << 1 | h >>> 63;
        }
        return (int)(h >> 32 ^ h) + -1737092556;
    }
}

