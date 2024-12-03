/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Comparator;
import org.apache.lucene.util.ArrayUtil;

public final class CharsRef
implements Comparable<CharsRef>,
CharSequence,
Cloneable {
    public static final char[] EMPTY_CHARS = new char[0];
    public char[] chars;
    public int offset;
    public int length;
    @Deprecated
    private static final Comparator<CharsRef> utf16SortedAsUTF8SortOrder = new UTF16SortedAsUTF8Comparator();

    public CharsRef() {
        this(EMPTY_CHARS, 0, 0);
    }

    public CharsRef(int capacity) {
        this.chars = new char[capacity];
    }

    public CharsRef(char[] chars, int offset, int length) {
        this.chars = chars;
        this.offset = offset;
        this.length = length;
        assert (this.isValid());
    }

    public CharsRef(String string) {
        this.chars = string.toCharArray();
        this.offset = 0;
        this.length = this.chars.length;
    }

    public CharsRef clone() {
        return new CharsRef(this.chars, this.offset, this.length);
    }

    public int hashCode() {
        int prime = 31;
        int result = 0;
        int end = this.offset + this.length;
        for (int i = this.offset; i < end; ++i) {
            result = 31 * result + this.chars[i];
        }
        return result;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharsRef) {
            return this.charsEquals((CharsRef)other);
        }
        return false;
    }

    public boolean charsEquals(CharsRef other) {
        if (this.length == other.length) {
            int otherUpto = other.offset;
            char[] otherChars = other.chars;
            int end = this.offset + this.length;
            int upto = this.offset;
            while (upto < end) {
                if (this.chars[upto] != otherChars[otherUpto]) {
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
    public int compareTo(CharsRef other) {
        if (this == other) {
            return 0;
        }
        char[] aChars = this.chars;
        int aUpto = this.offset;
        char[] bChars = other.chars;
        int bUpto = other.offset;
        int aStop = aUpto + Math.min(this.length, other.length);
        while (aUpto < aStop) {
            char bInt;
            char aInt;
            if ((aInt = aChars[aUpto++]) > (bInt = bChars[bUpto++])) {
                return 1;
            }
            if (aInt >= bInt) continue;
            return -1;
        }
        return this.length - other.length;
    }

    public void copyChars(CharsRef other) {
        this.copyChars(other.chars, other.offset, other.length);
    }

    public void grow(int newLength) {
        assert (this.offset == 0);
        if (this.chars.length < newLength) {
            this.chars = ArrayUtil.grow(this.chars, newLength);
        }
    }

    public void copyChars(char[] otherChars, int otherOffset, int otherLength) {
        if (this.chars.length - this.offset < otherLength) {
            this.chars = new char[otherLength];
            this.offset = 0;
        }
        System.arraycopy(otherChars, otherOffset, this.chars, this.offset, otherLength);
        this.length = otherLength;
    }

    public void append(char[] otherChars, int otherOffset, int otherLength) {
        int newLen = this.length + otherLength;
        if (this.chars.length - this.offset < newLen) {
            char[] newChars = new char[newLen];
            System.arraycopy(this.chars, this.offset, newChars, 0, this.length);
            this.offset = 0;
            this.chars = newChars;
        }
        System.arraycopy(otherChars, otherOffset, this.chars, this.length + this.offset, otherLength);
        this.length = newLen;
    }

    @Override
    public String toString() {
        return new String(this.chars, this.offset, this.length);
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.chars[this.offset + index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end > this.length || start > end) {
            throw new IndexOutOfBoundsException();
        }
        return new CharsRef(this.chars, this.offset + start, end - start);
    }

    @Deprecated
    public static Comparator<CharsRef> getUTF16SortedAsUTF8Comparator() {
        return utf16SortedAsUTF8SortOrder;
    }

    public static CharsRef deepCopyOf(CharsRef other) {
        CharsRef clone = new CharsRef();
        clone.copyChars(other);
        return clone;
    }

    public boolean isValid() {
        if (this.chars == null) {
            throw new IllegalStateException("chars is null");
        }
        if (this.length < 0) {
            throw new IllegalStateException("length is negative: " + this.length);
        }
        if (this.length > this.chars.length) {
            throw new IllegalStateException("length is out of bounds: " + this.length + ",chars.length=" + this.chars.length);
        }
        if (this.offset < 0) {
            throw new IllegalStateException("offset is negative: " + this.offset);
        }
        if (this.offset > this.chars.length) {
            throw new IllegalStateException("offset out of bounds: " + this.offset + ",chars.length=" + this.chars.length);
        }
        if (this.offset + this.length < 0) {
            throw new IllegalStateException("offset+length is negative: offset=" + this.offset + ",length=" + this.length);
        }
        if (this.offset + this.length > this.chars.length) {
            throw new IllegalStateException("offset+length out of bounds: offset=" + this.offset + ",length=" + this.length + ",chars.length=" + this.chars.length);
        }
        return true;
    }

    @Deprecated
    private static class UTF16SortedAsUTF8Comparator
    implements Comparator<CharsRef> {
        private UTF16SortedAsUTF8Comparator() {
        }

        @Override
        public int compare(CharsRef a, CharsRef b) {
            if (a == b) {
                return 0;
            }
            char[] aChars = a.chars;
            int aUpto = a.offset;
            char[] bChars = b.chars;
            int bUpto = b.offset;
            int aStop = aUpto + Math.min(a.length, b.length);
            while (aUpto < aStop) {
                char bChar;
                char aChar;
                if ((aChar = aChars[aUpto++]) == (bChar = bChars[bUpto++])) continue;
                if (aChar >= '\ud800' && bChar >= '\ud800') {
                    aChar = aChar >= '\ue000' ? (char)(aChar - 2048) : (char)(aChar + 8192);
                    bChar = bChar >= '\ue000' ? (char)(bChar - 2048) : (char)(bChar + 8192);
                }
                return aChar - bChar;
            }
            return a.length - b.length;
        }
    }
}

