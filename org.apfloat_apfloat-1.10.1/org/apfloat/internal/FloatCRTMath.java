/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.FloatBaseMath;
import org.apfloat.internal.FloatRadixConstants;

public class FloatCRTMath
extends FloatBaseMath {
    private static final long serialVersionUID = 2778445457339436642L;
    private static final double INVERSE_MAX_POWER_OF_TWO_BASE = 5.9604644775390625E-8;
    private double base;

    public FloatCRTMath(int radix) {
        super(radix);
        this.base = FloatRadixConstants.BASE[radix];
    }

    public final void multiply(float[] src, float factor, float[] dst) {
        double tmp = (double)src[1] * (double)factor;
        float carry = (int)(tmp * 5.9604644775390625E-8);
        dst[2] = (float)(tmp - (double)(carry * 1.6777216E7f));
        tmp = (double)src[0] * (double)factor + (double)carry;
        carry = (int)(tmp * 5.9604644775390625E-8);
        dst[1] = (float)(tmp - (double)(carry * 1.6777216E7f));
        dst[0] = carry;
    }

    public final float compare(float[] src1, float[] src2) {
        float result = src1[0] - src2[0];
        if (result != 0.0f) {
            return result;
        }
        result = src1[1] - src2[1];
        if (result != 0.0f) {
            return result;
        }
        return src1[2] - src2[2];
    }

    public final float add(float[] src, float[] srcDst) {
        double result = (double)srcDst[2] + (double)src[2];
        float carry = result >= 1.6777216E7 ? 1 : 0;
        result = result >= 1.6777216E7 ? result - 1.6777216E7 : result;
        srcDst[2] = (float)result;
        result = (double)srcDst[1] + (double)src[1] + (double)carry;
        carry = result >= 1.6777216E7 ? 1 : 0;
        result = result >= 1.6777216E7 ? result - 1.6777216E7 : result;
        srcDst[1] = (float)result;
        result = (double)srcDst[0] + (double)src[0] + (double)carry;
        carry = result >= 1.6777216E7 ? 1 : 0;
        result = result >= 1.6777216E7 ? result - 1.6777216E7 : result;
        srcDst[0] = (float)result;
        return carry;
    }

    public final void subtract(float[] src, float[] srcDst) {
        float result = srcDst[2] - src[2];
        float carry = result < 0.0f ? 1 : 0;
        srcDst[2] = result = result < 0.0f ? result + 1.6777216E7f : result;
        result = srcDst[1] - src[1] - carry;
        carry = result < 0.0f ? 1 : 0;
        srcDst[1] = result = result < 0.0f ? result + 1.6777216E7f : result;
        result = srcDst[0] - src[0] - carry;
        srcDst[0] = result = result < 0.0f ? result + 1.6777216E7f : result;
    }

    public final float divide(float[] srcDst) {
        double tmp = (double)srcDst[0] * 1.6777216E7 + (double)srcDst[1];
        float result = (int)(tmp / this.base);
        float carry = (float)(tmp - (double)result * this.base);
        srcDst[0] = 0.0f;
        srcDst[1] = result;
        tmp = (double)carry * 1.6777216E7 + (double)srcDst[2];
        result = (int)(tmp / this.base);
        carry = (float)(tmp - (double)result * this.base);
        srcDst[2] = result;
        return carry;
    }
}

