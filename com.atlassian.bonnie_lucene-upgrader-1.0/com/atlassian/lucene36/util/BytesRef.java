/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BytesRef
implements Comparable<BytesRef>,
Cloneable {
    public static final byte[] EMPTY_BYTES = new byte[0];
    public byte[] bytes;
    public int offset;
    public int length;
    private static final Comparator<BytesRef> utf8SortedAsUnicodeSortOrder = new UTF8SortedAsUnicodeComparator();
    private static final Comparator<BytesRef> utf8SortedAsUTF16SortOrder = new UTF8SortedAsUTF16Comparator();

    public BytesRef() {
        this(EMPTY_BYTES);
    }

    public BytesRef(byte[] bytes, int offset, int length) {
        assert (bytes != null);
        assert (offset >= 0);
        assert (length >= 0);
        assert (bytes.length >= offset + length);
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public BytesRef(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    public BytesRef(int capacity) {
        this.bytes = new byte[capacity];
    }

    public BytesRef(CharSequence text) {
        this();
        this.copyChars(text);
    }

    public void copyChars(CharSequence text) {
        assert (this.offset == 0);
        UnicodeUtil.UTF16toUTF8(text, 0, text.length(), this);
    }

    public void copyChars(char[] text, int offset, int length) {
        UnicodeUtil.UTF16toUTF8(text, offset, length, this);
    }

    public boolean bytesEquals(BytesRef other) {
        assert (other != null);
        if (this.length == other.length) {
            int otherUpto = other.offset;
            byte[] otherBytes = other.bytes;
            int end = this.offset + this.length;
            int upto = this.offset;
            while (upto < end) {
                if (this.bytes[upto] != otherBytes[otherUpto]) {
                    return false;
                }
                ++upto;
                ++otherUpto;
            }
            return true;
        }
        return false;
    }

    public BytesRef clone() {
        return new BytesRef(this.bytes, this.offset, this.length);
    }

    private boolean sliceEquals(BytesRef other, int pos) {
        if (pos < 0 || this.length - pos < other.length) {
            return false;
        }
        int i = this.offset + pos;
        int j = other.offset;
        int k = other.offset + other.length;
        while (j < k) {
            if (this.bytes[i++] == other.bytes[j++]) continue;
            return false;
        }
        return true;
    }

    public boolean startsWith(BytesRef other) {
        return this.sliceEquals(other, 0);
    }

    public boolean endsWith(BytesRef other) {
        return this.sliceEquals(other, this.length - other.length);
    }

    public int hashCode() {
        int hash = 0;
        int end = this.offset + this.length;
        for (int i = this.offset; i < end; ++i) {
            hash = 31 * hash + this.bytes[i];
        }
        return hash;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BytesRef) {
            return this.bytesEquals((BytesRef)other);
        }
        return false;
    }

    public String utf8ToString() {
        try {
            return new String(this.bytes, this.offset, this.length, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
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
            sb.append(Integer.toHexString(this.bytes[i] & 0xFF));
        }
        sb.append(']');
        return sb.toString();
    }

    public void copyBytes(BytesRef other) {
        if (this.bytes.length - this.offset < other.length) {
            this.bytes = new byte[other.length];
            this.offset = 0;
        }
        System.arraycopy(other.bytes, other.offset, this.bytes, this.offset, other.length);
        this.length = other.length;
    }

    public void append(BytesRef other) {
        int newLen = this.length + other.length;
        if (this.bytes.length - this.offset < newLen) {
            byte[] newBytes = new byte[newLen];
            System.arraycopy(this.bytes, this.offset, newBytes, 0, this.length);
            this.offset = 0;
            this.bytes = newBytes;
        }
        System.arraycopy(other.bytes, other.offset, this.bytes, this.length + this.offset, other.length);
        this.length = newLen;
    }

    public void grow(int newLength) {
        assert (this.offset == 0);
        this.bytes = ArrayUtil.grow(this.bytes, newLength);
    }

    @Override
    public int compareTo(BytesRef other) {
        return utf8SortedAsUnicodeSortOrder.compare(this, other);
    }

    public static Comparator<BytesRef> getUTF8SortedAsUnicodeComparator() {
        return utf8SortedAsUnicodeSortOrder;
    }

    public static Comparator<BytesRef> getUTF8SortedAsUTF16Comparator() {
        return utf8SortedAsUTF16SortOrder;
    }

    public static BytesRef deepCopyOf(BytesRef other) {
        BytesRef copy = new BytesRef();
        copy.copyBytes(other);
        return copy;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class UTF8SortedAsUTF16Comparator
    implements Comparator<BytesRef> {
        private UTF8SortedAsUTF16Comparator() {
        }

        @Override
        public int compare(BytesRef a, BytesRef b) {
            byte[] aBytes = a.bytes;
            int aUpto = a.offset;
            byte[] bBytes = b.bytes;
            int bUpto = b.offset;
            int aStop = a.length < b.length ? aUpto + a.length : aUpto + b.length;
            while (aUpto < aStop) {
                int bByte;
                int aByte;
                if ((aByte = aBytes[aUpto++] & 0xFF) == (bByte = bBytes[bUpto++] & 0xFF)) continue;
                if (aByte >= 238 && bByte >= 238) {
                    if ((aByte & 0xFE) == 238) {
                        aByte += 14;
                    }
                    if ((bByte & 0xFE) == 238) {
                        bByte += 14;
                    }
                }
                return aByte - bByte;
            }
            return a.length - b.length;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class UTF8SortedAsUnicodeComparator
    implements Comparator<BytesRef> {
        private UTF8SortedAsUnicodeComparator() {
        }

        @Override
        public int compare(BytesRef a, BytesRef b) {
            byte[] aBytes = a.bytes;
            int aUpto = a.offset;
            byte[] bBytes = b.bytes;
            int bUpto = b.offset;
            int aStop = aUpto + Math.min(a.length, b.length);
            while (aUpto < aStop) {
                int bByte;
                int aByte;
                int diff;
                if ((diff = (aByte = aBytes[aUpto++] & 0xFF) - (bByte = bBytes[bUpto++] & 0xFF)) == 0) continue;
                return diff;
            }
            return a.length - b.length;
        }
    }
}

