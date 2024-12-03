/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.ArrayUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class IntsRef
implements Comparable<IntsRef>,
Cloneable {
    public static final int[] EMPTY_INTS = new int[0];
    public int[] ints;
    public int offset;
    public int length;

    public IntsRef() {
        this.ints = EMPTY_INTS;
    }

    public IntsRef(int capacity) {
        this.ints = new int[capacity];
    }

    public IntsRef(int[] ints, int offset, int length) {
        assert (ints != null);
        assert (offset >= 0);
        assert (length >= 0);
        assert (ints.length >= offset + length);
        this.ints = ints;
        this.offset = offset;
        this.length = length;
    }

    public IntsRef clone() {
        return new IntsRef(this.ints, this.offset, this.length);
    }

    public int hashCode() {
        int prime = 31;
        int result = 0;
        int end = this.offset + this.length;
        for (int i = this.offset; i < end; ++i) {
            result = 31 * result + this.ints[i];
        }
        return result;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntsRef) {
            return this.intsEquals((IntsRef)other);
        }
        return false;
    }

    public boolean intsEquals(IntsRef other) {
        if (this.length == other.length) {
            int otherUpto = other.offset;
            int[] otherInts = other.ints;
            int end = this.offset + this.length;
            int upto = this.offset;
            while (upto < end) {
                if (this.ints[upto] != otherInts[otherUpto]) {
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
    public int compareTo(IntsRef other) {
        if (this == other) {
            return 0;
        }
        int[] aInts = this.ints;
        int aUpto = this.offset;
        int[] bInts = other.ints;
        int bUpto = other.offset;
        int aStop = aUpto + Math.min(this.length, other.length);
        while (aUpto < aStop) {
            int bInt;
            int aInt;
            if ((aInt = aInts[aUpto++]) > (bInt = bInts[bUpto++])) {
                return 1;
            }
            if (aInt >= bInt) continue;
            return -1;
        }
        return this.length - other.length;
    }

    public void copyInts(IntsRef other) {
        if (this.ints.length - this.offset < other.length) {
            this.ints = new int[other.length];
            this.offset = 0;
        }
        System.arraycopy(other.ints, other.offset, this.ints, this.offset, other.length);
        this.length = other.length;
    }

    public void grow(int newLength) {
        assert (this.offset == 0);
        if (this.ints.length < newLength) {
            this.ints = ArrayUtil.grow(this.ints, newLength);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int end = this.offset + this.length;
        for (int i = this.offset; i < end; ++i) {
            if (i > this.offset) {
                sb.append(' ');
            }
            sb.append(Integer.toHexString(this.ints[i]));
        }
        sb.append(']');
        return sb.toString();
    }

    public static IntsRef deepCopyOf(IntsRef other) {
        IntsRef clone = new IntsRef();
        clone.copyInts(other);
        return clone;
    }
}

