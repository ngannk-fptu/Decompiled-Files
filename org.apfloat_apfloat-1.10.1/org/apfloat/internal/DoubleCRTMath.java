/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.DoubleBaseMath;
import org.apfloat.internal.DoubleRadixConstants;

public class DoubleCRTMath
extends DoubleBaseMath {
    private static final long serialVersionUID = -8414531999881223922L;
    private static final long BASE_MASK = 0x7FFFFFFFFFFFFL;
    private static final double INVERSE_2_64 = 5.421010862427522E-20;
    private long base;
    private double inverseBase;

    public DoubleCRTMath(int radix) {
        super(radix);
        this.base = (long)DoubleRadixConstants.BASE[radix];
        this.inverseBase = 1.0 / DoubleRadixConstants.BASE[radix];
    }

    public final void multiply(double[] src, double factor, double[] dst) {
        long tmp = (long)src[1] * (long)factor;
        long carry = (long)((src[1] * factor + (double)(tmp & Long.MIN_VALUE)) * 5.421010862427522E-20);
        carry = carry << 13 | tmp >>> 51;
        dst[2] = tmp & 0x7FFFFFFFFFFFFL;
        tmp = (long)src[0] * (long)factor + carry;
        carry = (long)((src[0] * factor + (double)carry + (double)(tmp & Long.MIN_VALUE)) * 5.421010862427522E-20);
        carry = carry << 13 | tmp >>> 51;
        dst[1] = tmp & 0x7FFFFFFFFFFFFL;
        dst[0] = carry;
    }

    public final double compare(double[] src1, double[] src2) {
        double result = src1[0] - src2[0];
        if (result != 0.0) {
            return result;
        }
        result = src1[1] - src2[1];
        if (result != 0.0) {
            return result;
        }
        return src1[2] - src2[2];
    }

    public final double add(double[] src, double[] srcDst) {
        double result = srcDst[2] + src[2];
        double carry = result >= 2.251799813685248E15 ? 1 : 0;
        srcDst[2] = result = result >= 2.251799813685248E15 ? result - 2.251799813685248E15 : result;
        result = srcDst[1] + src[1] + carry;
        carry = result >= 2.251799813685248E15 ? 1 : 0;
        srcDst[1] = result = result >= 2.251799813685248E15 ? result - 2.251799813685248E15 : result;
        result = srcDst[0] + src[0] + carry;
        carry = result >= 2.251799813685248E15 ? 1 : 0;
        srcDst[0] = result = result >= 2.251799813685248E15 ? result - 2.251799813685248E15 : result;
        return carry;
    }

    public final void subtract(double[] src, double[] srcDst) {
        double result = srcDst[2] - src[2];
        double carry = result < 0.0 ? 1 : 0;
        srcDst[2] = result = result < 0.0 ? result + 2.251799813685248E15 : result;
        result = srcDst[1] - src[1] - carry;
        carry = result < 0.0 ? 1 : 0;
        srcDst[1] = result = result < 0.0 ? result + 2.251799813685248E15 : result;
        result = srcDst[0] - src[0] - carry;
        srcDst[0] = result = result < 0.0 ? result + 2.251799813685248E15 : result;
    }

    public final double divide(double[] srcDst) {
        long tmp = ((long)srcDst[0] << 51) + (long)srcDst[1];
        long result = (long)((srcDst[0] * 2.251799813685248E15 + srcDst[1]) * this.inverseBase);
        long carry = tmp - result * this.base;
        if (carry >= this.base) {
            carry -= this.base;
            ++result;
        }
        if (carry < 0L) {
            carry += this.base;
            --result;
        }
        srcDst[0] = 0.0;
        srcDst[1] = result;
        tmp = (carry << 51) + (long)srcDst[2];
        result = (long)(((double)carry * 2.251799813685248E15 + srcDst[2]) * this.inverseBase);
        if ((carry = tmp - result * this.base) >= this.base) {
            carry -= this.base;
            ++result;
        }
        if (carry < 0L) {
            carry += this.base;
            --result;
        }
        srcDst[2] = result;
        return carry;
    }
}

