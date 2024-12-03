/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.LongBaseMath;
import org.apfloat.internal.LongRadixConstants;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LongCRTMath
extends LongBaseMath {
    private static final long serialVersionUID = 7400961005627736773L;
    private static final long BASE_MASK = 0x1FFFFFFFFFFFFFFL;
    private static final double INVERSE_MAX_POWER_OF_TWO_BASE = 6.938893903907228E-18;
    private long base;
    private double inverseBase;

    public LongCRTMath(int radix) {
        super(radix);
        this.base = LongRadixConstants.BASE[radix];
        this.inverseBase = 1.0 / (double)LongRadixConstants.BASE[radix];
    }

    public final void multiply(long[] src, long factor, long[] dst) {
        long tmp = src[1] * factor;
        long carry = (long)((double)src[1] * (double)factor * 6.938893903907228E-18);
        carry += tmp - (carry << 57) >> 57;
        dst[2] = tmp & 0x1FFFFFFFFFFFFFFL;
        tmp = src[0] * factor + carry;
        carry = (long)(((double)src[0] * (double)factor + (double)carry) * 6.938893903907228E-18);
        carry += tmp - (carry << 57) >> 57;
        dst[1] = tmp & 0x1FFFFFFFFFFFFFFL;
        dst[0] = carry;
    }

    public final long compare(long[] src1, long[] src2) {
        long result = src1[0] - src2[0];
        if (result != 0L) {
            return result;
        }
        result = src1[1] - src2[1];
        if (result != 0L) {
            return result;
        }
        return src1[2] - src2[2];
    }

    public final long add(long[] src, long[] srcDst) {
        long result = srcDst[2] + src[2];
        long carry = result >= 0x200000000000000L ? 1 : 0;
        srcDst[2] = result = result >= 0x200000000000000L ? result - 0x200000000000000L : result;
        result = srcDst[1] + src[1] + carry;
        carry = result >= 0x200000000000000L ? 1 : 0;
        srcDst[1] = result = result >= 0x200000000000000L ? result - 0x200000000000000L : result;
        result = srcDst[0] + src[0] + carry;
        carry = result >= 0x200000000000000L ? 1 : 0;
        srcDst[0] = result = result >= 0x200000000000000L ? result - 0x200000000000000L : result;
        return carry;
    }

    public final void subtract(long[] src, long[] srcDst) {
        long result = srcDst[2] - src[2];
        long carry = result < 0L ? 1 : 0;
        srcDst[2] = result = result < 0L ? result + 0x200000000000000L : result;
        result = srcDst[1] - src[1] - carry;
        carry = result < 0L ? 1 : 0;
        srcDst[1] = result = result < 0L ? result + 0x200000000000000L : result;
        result = srcDst[0] - src[0] - carry;
        srcDst[0] = result = result < 0L ? result + 0x200000000000000L : result;
    }

    public final long divide(long[] srcDst) {
        long tmp = (srcDst[0] << 57) + srcDst[1];
        long result = (long)(((double)srcDst[0] * 1.44115188075855872E17 + (double)srcDst[1]) * this.inverseBase);
        long carry = tmp - result * this.base;
        int tmp2 = (int)((double)carry * this.inverseBase);
        result += (long)tmp2;
        if ((carry -= (long)tmp2 * this.base) >= this.base) {
            carry -= this.base;
            ++result;
        }
        if (carry >= this.base) {
            carry -= this.base;
            ++result;
        }
        if (carry < 0L) {
            carry += this.base;
            --result;
        }
        if (carry < 0L) {
            carry += this.base;
        }
        srcDst[0] = 0L;
        srcDst[1] = --result;
        tmp = (carry << 57) + srcDst[2];
        result = (long)(((double)carry * 1.44115188075855872E17 + (double)srcDst[2]) * this.inverseBase);
        carry = tmp - result * this.base;
        tmp2 = (int)((double)carry * this.inverseBase);
        result += (long)tmp2;
        if ((carry -= (long)tmp2 * this.base) >= this.base) {
            carry -= this.base;
            ++result;
        }
        if (carry >= this.base) {
            carry -= this.base;
            ++result;
        }
        if (carry < 0L) {
            carry += this.base;
            --result;
        }
        if (carry < 0L) {
            carry += this.base;
        }
        srcDst[2] = --result;
        return carry;
    }
}

