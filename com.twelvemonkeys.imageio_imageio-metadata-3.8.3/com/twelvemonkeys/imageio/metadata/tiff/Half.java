/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.tiff;

public final class Half
extends Number
implements Comparable<Half> {
    public static final int SIZE = 16;
    private final short shortBits;
    private final transient float floatValue;

    public Half(short s) {
        this.shortBits = s;
        this.floatValue = Half.shortBitsToFloat(s);
    }

    @Override
    public int intValue() {
        return (int)this.floatValue;
    }

    @Override
    public long longValue() {
        return (long)this.floatValue;
    }

    @Override
    public float floatValue() {
        return this.floatValue;
    }

    @Override
    public double doubleValue() {
        return this.floatValue;
    }

    public int hashCode() {
        return this.shortBits;
    }

    public boolean equals(Object object) {
        return object instanceof Half && ((Half)object).shortBits == this.shortBits;
    }

    @Override
    public int compareTo(Half half) {
        return Float.compare(this.floatValue, half.floatValue);
    }

    public String toString() {
        return Float.toString(this.floatValue);
    }

    public static Half valueOf(String string) throws NumberFormatException {
        return new Half(Half.parseHalf(string));
    }

    public static short parseHalf(String string) throws NumberFormatException {
        return Half.floatToShortBits(Float.parseFloat(string));
    }

    public static float shortBitsToFloat(short s) {
        int n = s & 0x3FF;
        int n2 = s & 0x7C00;
        if (n2 == 31744) {
            n2 = 261120;
        } else if (n2 != 0) {
            if (n == 0 && (n2 += 114688) > 115712) {
                return Float.intBitsToFloat((s & 0x8000) << 16 | n2 << 13 | 0x3FF);
            }
        } else if (n != 0) {
            n2 = 115712;
            do {
                n2 -= 1024;
            } while (((n <<= 1) & 0x400) == 0);
            n &= 0x3FF;
        }
        return Float.intBitsToFloat((s & 0x8000) << 16 | (n2 | n) << 13);
    }

    public static short floatToShortBits(float f) {
        return (short)Half.floatTo16Bits(f);
    }

    private static int floatTo16Bits(float f) {
        int n = Float.floatToIntBits(f);
        int n2 = n >>> 16 & 0x8000;
        int n3 = (n & Integer.MAX_VALUE) + 4096;
        if (n3 >= 1199570944) {
            if ((n & Integer.MAX_VALUE) >= 1199570944) {
                if (n3 < 2139095040) {
                    return n2 | 0x7C00;
                }
                return n2 | 0x7C00 | (n & 0x7FFFFF) >>> 13;
            }
            return n2 | 0x7BFF;
        }
        if (n3 >= 0x38800000) {
            return n2 | n3 - 0x38000000 >>> 13;
        }
        if (n3 < 0x33000000) {
            return n2;
        }
        n3 = (n & Integer.MAX_VALUE) >>> 23;
        return n2 | (n & 0x7FFFFF | 0x800000) + (0x800000 >>> n3 - 102) >>> 126 - n3;
    }

    private Object readResolve() {
        return new Half(this.shortBits);
    }
}

