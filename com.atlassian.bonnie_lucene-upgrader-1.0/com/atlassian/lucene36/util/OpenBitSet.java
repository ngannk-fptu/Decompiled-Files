/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.BitUtil;
import com.atlassian.lucene36.util.Bits;
import com.atlassian.lucene36.util.OpenBitSetIterator;
import java.io.Serializable;
import java.util.Arrays;

public class OpenBitSet
extends DocIdSet
implements Cloneable,
Serializable,
Bits {
    protected long[] bits;
    protected int wlen;
    private long numBits;

    public OpenBitSet(long numBits) {
        this.numBits = numBits;
        this.bits = new long[OpenBitSet.bits2words(numBits)];
        this.wlen = this.bits.length;
    }

    public OpenBitSet() {
        this(64L);
    }

    public OpenBitSet(long[] bits, int numWords) {
        this.bits = bits;
        this.wlen = numWords;
        this.numBits = this.wlen * 64;
    }

    public DocIdSetIterator iterator() {
        return new OpenBitSetIterator(this.bits, this.wlen);
    }

    public boolean isCacheable() {
        return true;
    }

    public long capacity() {
        return this.bits.length << 6;
    }

    public long size() {
        return this.capacity();
    }

    public int length() {
        return this.bits.length << 6;
    }

    public boolean isEmpty() {
        return this.cardinality() == 0L;
    }

    public long[] getBits() {
        return this.bits;
    }

    public void setBits(long[] bits) {
        this.bits = bits;
    }

    public int getNumWords() {
        return this.wlen;
    }

    public void setNumWords(int nWords) {
        this.wlen = nWords;
    }

    public boolean get(int index) {
        int i = index >> 6;
        if (i >= this.bits.length) {
            return false;
        }
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        return (this.bits[i] & bitmask) != 0L;
    }

    public boolean fastGet(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int i = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        return (this.bits[i] & bitmask) != 0L;
    }

    public boolean get(long index) {
        int i = (int)(index >> 6);
        if (i >= this.bits.length) {
            return false;
        }
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        return (this.bits[i] & bitmask) != 0L;
    }

    public boolean fastGet(long index) {
        assert (index >= 0L && index < this.numBits);
        int i = (int)(index >> 6);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        return (this.bits[i] & bitmask) != 0L;
    }

    public int getBit(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int i = index >> 6;
        int bit = index & 0x3F;
        return (int)(this.bits[i] >>> bit) & 1;
    }

    public void set(long index) {
        int wordNum = this.expandingWordNum(index);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
    }

    public void fastSet(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
    }

    public void fastSet(long index) {
        assert (index >= 0L && index < this.numBits);
        int wordNum = (int)(index >> 6);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
    }

    public void set(long startIndex, long endIndex) {
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = (int)(startIndex >> 6);
        int endWord = this.expandingWordNum(endIndex - 1L);
        long startmask = -1L << (int)startIndex;
        long endmask = -1L >>> (int)(-endIndex);
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

    protected int expandingWordNum(long index) {
        int wordNum = (int)(index >> 6);
        if (wordNum >= this.wlen) {
            this.ensureCapacity(index + 1L);
            this.wlen = wordNum + 1;
        }
        assert ((this.numBits = Math.max(this.numBits, index + 1L)) >= 0L);
        return wordNum;
    }

    public void fastClear(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] & (bitmask ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public void fastClear(long index) {
        assert (index >= 0L && index < this.numBits);
        int wordNum = (int)(index >> 6);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] & (bitmask ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public void clear(long index) {
        int wordNum = (int)(index >> 6);
        if (wordNum >= this.wlen) {
            return;
        }
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] & (bitmask ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public void clear(int startIndex, int endIndex) {
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = startIndex >> 6;
        if (startWord >= this.wlen) {
            return;
        }
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
        int middle = Math.min(this.wlen, endWord);
        Arrays.fill(this.bits, startWord + 1, middle, 0L);
        if (endWord < this.wlen) {
            int n2 = endWord;
            this.bits[n2] = this.bits[n2] & endmask;
        }
    }

    public void clear(long startIndex, long endIndex) {
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = (int)(startIndex >> 6);
        if (startWord >= this.wlen) {
            return;
        }
        int endWord = (int)(endIndex - 1L >> 6);
        long startmask = -1L << (int)startIndex;
        long endmask = -1L >>> (int)(-endIndex);
        startmask ^= 0xFFFFFFFFFFFFFFFFL;
        endmask ^= 0xFFFFFFFFFFFFFFFFL;
        if (startWord == endWord) {
            int n = startWord;
            this.bits[n] = this.bits[n] & (startmask | endmask);
            return;
        }
        int n = startWord;
        this.bits[n] = this.bits[n] & startmask;
        int middle = Math.min(this.wlen, endWord);
        Arrays.fill(this.bits, startWord + 1, middle, 0L);
        if (endWord < this.wlen) {
            int n2 = endWord;
            this.bits[n2] = this.bits[n2] & endmask;
        }
    }

    public boolean getAndSet(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        boolean val = (this.bits[wordNum] & bitmask) != 0L;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
        return val;
    }

    public boolean getAndSet(long index) {
        assert (index >= 0L && index < this.numBits);
        int wordNum = (int)(index >> 6);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        boolean val = (this.bits[wordNum] & bitmask) != 0L;
        int n = wordNum;
        this.bits[n] = this.bits[n] | bitmask;
        return val;
    }

    public void fastFlip(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] ^ bitmask;
    }

    public void fastFlip(long index) {
        assert (index >= 0L && index < this.numBits);
        int wordNum = (int)(index >> 6);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] ^ bitmask;
    }

    public void flip(long index) {
        int wordNum = this.expandingWordNum(index);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] ^ bitmask;
    }

    public boolean flipAndGet(int index) {
        assert (index >= 0 && (long)index < this.numBits);
        int wordNum = index >> 6;
        int bit = index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] ^ bitmask;
        return (this.bits[wordNum] & bitmask) != 0L;
    }

    public boolean flipAndGet(long index) {
        assert (index >= 0L && index < this.numBits);
        int wordNum = (int)(index >> 6);
        int bit = (int)index & 0x3F;
        long bitmask = 1L << bit;
        int n = wordNum;
        this.bits[n] = this.bits[n] ^ bitmask;
        return (this.bits[wordNum] & bitmask) != 0L;
    }

    public void flip(long startIndex, long endIndex) {
        if (endIndex <= startIndex) {
            return;
        }
        int startWord = (int)(startIndex >> 6);
        int endWord = this.expandingWordNum(endIndex - 1L);
        long startmask = -1L << (int)startIndex;
        long endmask = -1L >>> (int)(-endIndex);
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

    public long cardinality() {
        return BitUtil.pop_array(this.bits, 0, this.wlen);
    }

    public static long intersectionCount(OpenBitSet a, OpenBitSet b) {
        return BitUtil.pop_intersect(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
    }

    public static long unionCount(OpenBitSet a, OpenBitSet b) {
        long tot = BitUtil.pop_union(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
        if (a.wlen < b.wlen) {
            tot += BitUtil.pop_array(b.bits, a.wlen, b.wlen - a.wlen);
        } else if (a.wlen > b.wlen) {
            tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
        }
        return tot;
    }

    public static long andNotCount(OpenBitSet a, OpenBitSet b) {
        long tot = BitUtil.pop_andnot(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
        if (a.wlen > b.wlen) {
            tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
        }
        return tot;
    }

    public static long xorCount(OpenBitSet a, OpenBitSet b) {
        long tot = BitUtil.pop_xor(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
        if (a.wlen < b.wlen) {
            tot += BitUtil.pop_array(b.bits, a.wlen, b.wlen - a.wlen);
        } else if (a.wlen > b.wlen) {
            tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
        }
        return tot;
    }

    public int nextSetBit(int index) {
        int i = index >> 6;
        if (i >= this.wlen) {
            return -1;
        }
        int subIndex = index & 0x3F;
        long word = this.bits[i] >> subIndex;
        if (word != 0L) {
            return (i << 6) + subIndex + BitUtil.ntz(word);
        }
        while (++i < this.wlen) {
            word = this.bits[i];
            if (word == 0L) continue;
            return (i << 6) + BitUtil.ntz(word);
        }
        return -1;
    }

    public long nextSetBit(long index) {
        int i = (int)(index >>> 6);
        if (i >= this.wlen) {
            return -1L;
        }
        int subIndex = (int)index & 0x3F;
        long word = this.bits[i] >>> subIndex;
        if (word != 0L) {
            return ((long)i << 6) + (long)(subIndex + BitUtil.ntz(word));
        }
        while (++i < this.wlen) {
            word = this.bits[i];
            if (word == 0L) continue;
            return ((long)i << 6) + (long)BitUtil.ntz(word);
        }
        return -1L;
    }

    public int prevSetBit(int index) {
        long word;
        int subIndex;
        int i = index >> 6;
        if (i >= this.wlen) {
            i = this.wlen - 1;
            if (i < 0) {
                return -1;
            }
            subIndex = 63;
            word = this.bits[i];
        } else {
            if (i < 0) {
                return -1;
            }
            subIndex = index & 0x3F;
            word = this.bits[i] << 63 - subIndex;
        }
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

    public long prevSetBit(long index) {
        long word;
        int subIndex;
        int i = (int)(index >> 6);
        if (i >= this.wlen) {
            i = this.wlen - 1;
            if (i < 0) {
                return -1L;
            }
            subIndex = 63;
            word = this.bits[i];
        } else {
            if (i < 0) {
                return -1L;
            }
            subIndex = (int)index & 0x3F;
            word = this.bits[i] << 63 - subIndex;
        }
        if (word != 0L) {
            return ((long)i << 6) + (long)subIndex - (long)Long.numberOfLeadingZeros(word);
        }
        while (--i >= 0) {
            word = this.bits[i];
            if (word == 0L) continue;
            return ((long)i << 6) + 63L - (long)Long.numberOfLeadingZeros(word);
        }
        return -1L;
    }

    public Object clone() {
        try {
            OpenBitSet obs = (OpenBitSet)super.clone();
            obs.bits = (long[])obs.bits.clone();
            return obs;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void intersect(OpenBitSet other) {
        int newLen = Math.min(this.wlen, other.wlen);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        int pos = newLen;
        while (--pos >= 0) {
            int n = pos;
            thisArr[n] = thisArr[n] & otherArr[pos];
        }
        if (this.wlen > newLen) {
            Arrays.fill(this.bits, newLen, this.wlen, 0L);
        }
        this.wlen = newLen;
    }

    public void union(OpenBitSet other) {
        int newLen = Math.max(this.wlen, other.wlen);
        this.ensureCapacityWords(newLen);
        assert ((this.numBits = Math.max(other.numBits, this.numBits)) >= 0L);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        int pos = Math.min(this.wlen, other.wlen);
        while (--pos >= 0) {
            int n = pos;
            thisArr[n] = thisArr[n] | otherArr[pos];
        }
        if (this.wlen < newLen) {
            System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen - this.wlen);
        }
        this.wlen = newLen;
    }

    public void remove(OpenBitSet other) {
        int idx = Math.min(this.wlen, other.wlen);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        while (--idx >= 0) {
            int n = idx;
            thisArr[n] = thisArr[n] & (otherArr[idx] ^ 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public void xor(OpenBitSet other) {
        int newLen = Math.max(this.wlen, other.wlen);
        this.ensureCapacityWords(newLen);
        assert ((this.numBits = Math.max(other.numBits, this.numBits)) >= 0L);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        int pos = Math.min(this.wlen, other.wlen);
        while (--pos >= 0) {
            int n = pos;
            thisArr[n] = thisArr[n] ^ otherArr[pos];
        }
        if (this.wlen < newLen) {
            System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen - this.wlen);
        }
        this.wlen = newLen;
    }

    public void and(OpenBitSet other) {
        this.intersect(other);
    }

    public void or(OpenBitSet other) {
        this.union(other);
    }

    public void andNot(OpenBitSet other) {
        this.remove(other);
    }

    public boolean intersects(OpenBitSet other) {
        int pos = Math.min(this.wlen, other.wlen);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        while (--pos >= 0) {
            if ((thisArr[pos] & otherArr[pos]) == 0L) continue;
            return true;
        }
        return false;
    }

    public void ensureCapacityWords(int numWords) {
        if (this.bits.length < numWords) {
            this.bits = ArrayUtil.grow(this.bits, numWords);
        }
    }

    public void ensureCapacity(long numBits) {
        this.ensureCapacityWords(OpenBitSet.bits2words(numBits));
    }

    public void trimTrailingZeros() {
        int idx;
        for (idx = this.wlen - 1; idx >= 0 && this.bits[idx] == 0L; --idx) {
        }
        this.wlen = idx + 1;
    }

    public static int bits2words(long numBits) {
        return (int)((numBits - 1L >>> 6) + 1L);
    }

    public boolean equals(Object o) {
        int i;
        OpenBitSet a;
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpenBitSet)) {
            return false;
        }
        OpenBitSet b = (OpenBitSet)o;
        if (b.wlen > this.wlen) {
            a = b;
            b = this;
        } else {
            a = this;
        }
        for (i = a.wlen - 1; i >= b.wlen; --i) {
            if (a.bits[i] == 0L) continue;
            return false;
        }
        for (i = b.wlen - 1; i >= 0; --i) {
            if (a.bits[i] == b.bits[i]) continue;
            return false;
        }
        return true;
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

