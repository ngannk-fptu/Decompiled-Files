/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections.impl;

import groovyjarjarantlr.CharFormatter;
import groovyjarjarantlr.collections.impl.IntRange;
import groovyjarjarantlr.collections.impl.Vector;

public class BitSet
implements Cloneable {
    protected static final int BITS = 64;
    protected static final int NIBBLE = 4;
    protected static final int LOG_BITS = 6;
    protected static final int MOD_MASK = 63;
    protected long[] bits;

    public BitSet() {
        this(64);
    }

    public BitSet(long[] lArray) {
        this.bits = lArray;
    }

    public BitSet(int n) {
        this.bits = new long[(n - 1 >> 6) + 1];
    }

    public void add(int n) {
        int n2 = BitSet.wordNumber(n);
        if (n2 >= this.bits.length) {
            this.growToInclude(n);
        }
        int n3 = n2;
        this.bits[n3] = this.bits[n3] | BitSet.bitMask(n);
    }

    public BitSet and(BitSet bitSet) {
        BitSet bitSet2 = (BitSet)this.clone();
        bitSet2.andInPlace(bitSet);
        return bitSet2;
    }

    public void andInPlace(BitSet bitSet) {
        int n;
        int n2 = Math.min(this.bits.length, bitSet.bits.length);
        for (n = n2 - 1; n >= 0; --n) {
            int n3 = n;
            this.bits[n3] = this.bits[n3] & bitSet.bits[n];
        }
        for (n = n2; n < this.bits.length; ++n) {
            this.bits[n] = 0L;
        }
    }

    private static final long bitMask(int n) {
        int n2 = n & 0x3F;
        return 1L << n2;
    }

    public void clear() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            this.bits[i] = 0L;
        }
    }

    public void clear(int n) {
        int n2 = BitSet.wordNumber(n);
        if (n2 >= this.bits.length) {
            this.growToInclude(n);
        }
        int n3 = n2;
        this.bits[n3] = this.bits[n3] & (BitSet.bitMask(n) ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public Object clone() {
        BitSet bitSet;
        try {
            bitSet = (BitSet)super.clone();
            bitSet.bits = new long[this.bits.length];
            System.arraycopy(this.bits, 0, bitSet.bits, 0, this.bits.length);
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
        return bitSet;
    }

    public int degree() {
        int n = 0;
        for (int i = this.bits.length - 1; i >= 0; --i) {
            long l = this.bits[i];
            if (l == 0L) continue;
            for (int j = 63; j >= 0; --j) {
                if ((l & 1L << j) == 0L) continue;
                ++n;
            }
        }
        return n;
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof BitSet) {
            int n;
            BitSet bitSet = (BitSet)object;
            int n2 = n = Math.min(this.bits.length, bitSet.bits.length);
            while (n2-- > 0) {
                if (this.bits[n2] == bitSet.bits[n2]) continue;
                return false;
            }
            if (this.bits.length > n) {
                n2 = this.bits.length;
                while (n2-- > n) {
                    if (this.bits[n2] == 0L) continue;
                    return false;
                }
            } else if (bitSet.bits.length > n) {
                n2 = bitSet.bits.length;
                while (n2-- > n) {
                    if (bitSet.bits[n2] == 0L) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Vector getRanges(int[] nArray) {
        if (nArray.length == 0) {
            return null;
        }
        int n = nArray[0];
        int n2 = nArray[nArray.length - 1];
        if (nArray.length <= 2) {
            return null;
        }
        Vector vector = new Vector(5);
        for (int i = 0; i < nArray.length - 2; ++i) {
            int n3 = nArray.length - 1;
            for (int j = i + 1; j < nArray.length; ++j) {
                if (nArray[j] == nArray[j - 1] + 1) continue;
                n3 = j - 1;
                break;
            }
            if (n3 - i <= 2) continue;
            vector.appendElement(new IntRange(nArray[i], nArray[n3]));
        }
        return vector;
    }

    public void growToInclude(int n) {
        int n2 = Math.max(this.bits.length << 1, this.numWordsToHold(n));
        long[] lArray = new long[n2];
        System.arraycopy(this.bits, 0, lArray, 0, this.bits.length);
        this.bits = lArray;
    }

    public boolean member(int n) {
        int n2 = BitSet.wordNumber(n);
        if (n2 >= this.bits.length) {
            return false;
        }
        return (this.bits[n2] & BitSet.bitMask(n)) != 0L;
    }

    public boolean nil() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            if (this.bits[i] == 0L) continue;
            return false;
        }
        return true;
    }

    public BitSet not() {
        BitSet bitSet = (BitSet)this.clone();
        bitSet.notInPlace();
        return bitSet;
    }

    public void notInPlace() {
        for (int i = this.bits.length - 1; i >= 0; --i) {
            this.bits[i] = this.bits[i] ^ 0xFFFFFFFFFFFFFFFFL;
        }
    }

    public void notInPlace(int n) {
        this.notInPlace(0, n);
    }

    public void notInPlace(int n, int n2) {
        this.growToInclude(n2);
        for (int i = n; i <= n2; ++i) {
            int n3;
            int n4 = n3 = BitSet.wordNumber(i);
            this.bits[n4] = this.bits[n4] ^ BitSet.bitMask(i);
        }
    }

    private final int numWordsToHold(int n) {
        return (n >> 6) + 1;
    }

    public static BitSet of(int n) {
        BitSet bitSet = new BitSet(n + 1);
        bitSet.add(n);
        return bitSet;
    }

    public BitSet or(BitSet bitSet) {
        BitSet bitSet2 = (BitSet)this.clone();
        bitSet2.orInPlace(bitSet);
        return bitSet2;
    }

    public void orInPlace(BitSet bitSet) {
        if (bitSet.bits.length > this.bits.length) {
            this.setSize(bitSet.bits.length);
        }
        int n = Math.min(this.bits.length, bitSet.bits.length);
        for (int i = n - 1; i >= 0; --i) {
            int n2 = i;
            this.bits[n2] = this.bits[n2] | bitSet.bits[i];
        }
    }

    public void remove(int n) {
        int n2 = BitSet.wordNumber(n);
        if (n2 >= this.bits.length) {
            this.growToInclude(n);
        }
        int n3 = n2;
        this.bits[n3] = this.bits[n3] & (BitSet.bitMask(n) ^ 0xFFFFFFFFFFFFFFFFL);
    }

    private void setSize(int n) {
        long[] lArray = new long[n];
        int n2 = Math.min(n, this.bits.length);
        System.arraycopy(this.bits, 0, lArray, 0, n2);
        this.bits = lArray;
    }

    public int size() {
        return this.bits.length << 6;
    }

    public int lengthInLongWords() {
        return this.bits.length;
    }

    public boolean subset(BitSet bitSet) {
        if (bitSet == null || !(bitSet instanceof BitSet)) {
            return false;
        }
        return this.and(bitSet).equals(this);
    }

    public void subtractInPlace(BitSet bitSet) {
        if (bitSet == null) {
            return;
        }
        for (int i = 0; i < this.bits.length && i < bitSet.bits.length; ++i) {
            int n = i;
            this.bits[n] = this.bits[n] & (bitSet.bits[i] ^ 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public int[] toArray() {
        int[] nArray = new int[this.degree()];
        int n = 0;
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (!this.member(i)) continue;
            nArray[n++] = i;
        }
        return nArray;
    }

    public long[] toPackedArray() {
        return this.bits;
    }

    public String toString() {
        return this.toString(",");
    }

    public String toString(String string) {
        String string2 = "";
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (!this.member(i)) continue;
            if (string2.length() > 0) {
                string2 = string2 + string;
            }
            string2 = string2 + i;
        }
        return string2;
    }

    public String toString(String string, CharFormatter charFormatter) {
        String string2 = "";
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (!this.member(i)) continue;
            if (string2.length() > 0) {
                string2 = string2 + string;
            }
            string2 = string2 + charFormatter.literalChar(i);
        }
        return string2;
    }

    public String toString(String string, Vector vector) {
        if (vector == null) {
            return this.toString(string);
        }
        String string2 = "";
        for (int i = 0; i < this.bits.length << 6; ++i) {
            if (!this.member(i)) continue;
            if (string2.length() > 0) {
                string2 = string2 + string;
            }
            string2 = i >= vector.size() ? string2 + "<bad element " + i + ">" : (vector.elementAt(i) == null ? string2 + "<" + i + ">" : string2 + (String)vector.elementAt(i));
        }
        return string2;
    }

    public String toStringOfHalfWords() {
        String string = new String();
        for (int i = 0; i < this.bits.length; ++i) {
            if (i != 0) {
                string = string + ", ";
            }
            long l = this.bits[i];
            string = string + (l &= 0xFFFFFFFFL) + "UL";
            string = string + ", ";
            l = this.bits[i] >>> 32;
            string = string + (l &= 0xFFFFFFFFL) + "UL";
        }
        return string;
    }

    public String toStringOfWords() {
        String string = new String();
        for (int i = 0; i < this.bits.length; ++i) {
            if (i != 0) {
                string = string + ", ";
            }
            string = string + this.bits[i] + "L";
        }
        return string;
    }

    public String toStringWithRanges(String string, CharFormatter charFormatter) {
        String string2 = "";
        int[] nArray = this.toArray();
        if (nArray.length == 0) {
            return "";
        }
        for (int i = 0; i < nArray.length; ++i) {
            int n = 0;
            int n2 = i + 1;
            while (n2 < nArray.length && nArray[n2] == nArray[n2 - 1] + 1) {
                n = n2++;
            }
            if (string2.length() > 0) {
                string2 = string2 + string;
            }
            if (n - i >= 2) {
                string2 = string2 + charFormatter.literalChar(nArray[i]);
                string2 = string2 + "..";
                string2 = string2 + charFormatter.literalChar(nArray[n]);
                i = n;
                continue;
            }
            string2 = string2 + charFormatter.literalChar(nArray[i]);
        }
        return string2;
    }

    private static final int wordNumber(int n) {
        return n >> 6;
    }
}

