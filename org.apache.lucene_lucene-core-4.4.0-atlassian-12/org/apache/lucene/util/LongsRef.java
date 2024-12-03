/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import org.apache.lucene.util.ArrayUtil;

public final class LongsRef
implements Comparable<LongsRef>,
Cloneable {
    public static final long[] EMPTY_LONGS = new long[0];
    public long[] longs;
    public int offset;
    public int length;

    public LongsRef() {
        this.longs = EMPTY_LONGS;
    }

    public LongsRef(int capacity) {
        this.longs = new long[capacity];
    }

    public LongsRef(long[] longs, int offset, int length) {
        this.longs = longs;
        this.offset = offset;
        this.length = length;
        assert (this.isValid());
    }

    public LongsRef clone() {
        return new LongsRef(this.longs, this.offset, this.length);
    }

    public int hashCode() {
        int prime = 31;
        int result = 0;
        long end = this.offset + this.length;
        int i = this.offset;
        while ((long)i < end) {
            result = 31 * result + (int)(this.longs[i] ^ this.longs[i] >>> 32);
            ++i;
        }
        return result;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongsRef) {
            return this.longsEquals((LongsRef)other);
        }
        return false;
    }

    public boolean longsEquals(LongsRef other) {
        if (this.length == other.length) {
            int otherUpto = other.offset;
            long[] otherInts = other.longs;
            long end = this.offset + this.length;
            int upto = this.offset;
            while ((long)upto < end) {
                if (this.longs[upto] != otherInts[otherUpto]) {
                    return false;
                }
                ++upto;
                ++otherUpto;
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(LongsRef other) {
        if (this == other) {
            return 0;
        }
        long[] aInts = this.longs;
        int aUpto = this.offset;
        long[] bInts = other.longs;
        int bUpto = other.offset;
        long aStop = aUpto + Math.min(this.length, other.length);
        while ((long)aUpto < aStop) {
            long bInt;
            long aInt;
            if ((aInt = aInts[aUpto++]) > (bInt = bInts[bUpto++])) {
                return 1;
            }
            if (aInt >= bInt) continue;
            return -1;
        }
        return this.length - other.length;
    }

    public void copyLongs(LongsRef other) {
        if (this.longs.length - this.offset < other.length) {
            this.longs = new long[other.length];
            this.offset = 0;
        }
        System.arraycopy(other.longs, other.offset, this.longs, this.offset, other.length);
        this.length = other.length;
    }

    public void grow(int newLength) {
        assert (this.offset == 0);
        if (this.longs.length < newLength) {
            this.longs = ArrayUtil.grow(this.longs, newLength);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        long end = this.offset + this.length;
        int i = this.offset;
        while ((long)i < end) {
            if (i > this.offset) {
                sb.append(' ');
            }
            sb.append(Long.toHexString(this.longs[i]));
            ++i;
        }
        sb.append(']');
        return sb.toString();
    }

    public static LongsRef deepCopyOf(LongsRef other) {
        LongsRef clone = new LongsRef();
        clone.copyLongs(other);
        return clone;
    }

    public boolean isValid() {
        if (this.longs == null) {
            throw new IllegalStateException("longs is null");
        }
        if (this.length < 0) {
            throw new IllegalStateException("length is negative: " + this.length);
        }
        if (this.length > this.longs.length) {
            throw new IllegalStateException("length is out of bounds: " + this.length + ",longs.length=" + this.longs.length);
        }
        if (this.offset < 0) {
            throw new IllegalStateException("offset is negative: " + this.offset);
        }
        if (this.offset > this.longs.length) {
            throw new IllegalStateException("offset out of bounds: " + this.offset + ",longs.length=" + this.longs.length);
        }
        if (this.offset + this.length < 0) {
            throw new IllegalStateException("offset+length is negative: offset=" + this.offset + ",length=" + this.length);
        }
        if (this.offset + this.length > this.longs.length) {
            throw new IllegalStateException("offset+length out of bounds: offset=" + this.offset + ",length=" + this.length + ",longs.length=" + this.longs.length);
        }
        return true;
    }
}

