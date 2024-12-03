/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import org.apfloat.internal.LongBaseMath;
import org.apfloat.internal.LongRadixConstants;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LongCRTMath
extends LongBaseMath {
    private static final long serialVersionUID = 7400961005627736773L;
    private static final long BASE_MASK = 0x1FFFFFFFFFFFFFFL;
    private static final long[] INVERSE_BASE = new long[37];
    private long base;
    private double inverseBase;
    private transient long inverseBaseLong;

    public LongCRTMath(int n) {
        super(n);
        this.base = LongRadixConstants.BASE[n];
        this.inverseBase = 1.0 / (double)LongRadixConstants.BASE[n];
        this.inverseBaseLong = INVERSE_BASE[n];
    }

    public final void multiply(long[] lArray, long l, long[] lArray2) {
        long l2 = lArray[1] * l;
        long l3 = Math.multiplyHigh(lArray[1], l) << 7 | l2 >>> 57;
        lArray2[2] = l2 & 0x1FFFFFFFFFFFFFFL;
        l2 = lArray[0] * l;
        long l4 = (l2 & 0x1FFFFFFFFFFFFFFL) + l3;
        l3 = (Math.multiplyHigh(lArray[0], l) << 7) + (l2 >>> 57) + (l4 >>> 57);
        lArray2[1] = l4 & 0x1FFFFFFFFFFFFFFL;
        lArray2[0] = l3;
    }

    public final long compare(long[] lArray, long[] lArray2) {
        long l = lArray[0] - lArray2[0];
        if (l != 0L) {
            return l;
        }
        l = lArray[1] - lArray2[1];
        if (l != 0L) {
            return l;
        }
        return lArray[2] - lArray2[2];
    }

    public final long add(long[] lArray, long[] lArray2) {
        long l = lArray2[2] + lArray[2];
        long l2 = l >= 0x200000000000000L ? 1 : 0;
        lArray2[2] = l = l >= 0x200000000000000L ? l - 0x200000000000000L : l;
        l = lArray2[1] + lArray[1] + l2;
        l2 = l >= 0x200000000000000L ? 1 : 0;
        lArray2[1] = l = l >= 0x200000000000000L ? l - 0x200000000000000L : l;
        l = lArray2[0] + lArray[0] + l2;
        l2 = l >= 0x200000000000000L ? 1 : 0;
        lArray2[0] = l = l >= 0x200000000000000L ? l - 0x200000000000000L : l;
        return l2;
    }

    public final void subtract(long[] lArray, long[] lArray2) {
        long l = lArray2[2] - lArray[2];
        long l2 = l < 0L ? 1 : 0;
        lArray2[2] = l = l < 0L ? l + 0x200000000000000L : l;
        l = lArray2[1] - lArray[1] - l2;
        l2 = l < 0L ? 1 : 0;
        lArray2[1] = l = l < 0L ? l + 0x200000000000000L : l;
        l = lArray2[0] - lArray[0] - l2;
        lArray2[0] = l = l < 0L ? l + 0x200000000000000L : l;
    }

    public final long divide(long[] lArray) {
        long l = lArray[0] << 7 | lArray[1] >> 57;
        long l2 = Math.multiplyHigh(l, this.inverseBaseLong);
        long l3 = (lArray[0] << 57 | lArray[1]) - l2 * this.base;
        l = Math.multiplyHigh(l3, this.inverseBaseLong) >> 50;
        l2 += l;
        int n = (l3 -= l * this.base) >= this.base ? 1 : 0;
        long l4 = l3 >= this.base ? this.base : 0L;
        lArray[0] = 0L;
        lArray[1] = l2 += (long)n;
        l = (l3 -= l4) << 7 | lArray[2] >> 57;
        l2 = Math.multiplyHigh(l, this.inverseBaseLong) + (l < 0L ? this.inverseBaseLong : 0L);
        l3 = (l3 << 57 | lArray[2]) - l2 * this.base;
        l = Math.multiplyHigh(l3, this.inverseBaseLong) >> 50;
        l2 += l;
        int n2 = (l3 -= l * this.base) >= this.base ? 1 : 0;
        long l5 = l3 >= this.base ? this.base : 0L;
        lArray[2] = l2 += (long)n2;
        return l3 -= l5;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.inverseBaseLong = INVERSE_BASE[this.radix()];
    }

    static {
        for (int i = 2; i <= 36; ++i) {
            LongCRTMath.INVERSE_BASE[i] = BigInteger.ONE.shiftLeft(114).divide(BigInteger.valueOf(LongRadixConstants.BASE[i])).longValueExact();
        }
    }
}

