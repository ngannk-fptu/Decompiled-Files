/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.IntBaseMath;
import org.apfloat.internal.IntRadixConstants;

public class IntCRTMath
extends IntBaseMath {
    private static final long serialVersionUID = 6698972116690441263L;
    private static final int BASE_MASK = Integer.MAX_VALUE;
    private int base;

    public IntCRTMath(int radix) {
        super(radix);
        this.base = IntRadixConstants.BASE[radix];
    }

    public final void multiply(int[] src, int factor, int[] dst) {
        long tmp = (long)src[1] * (long)factor;
        int carry = (int)(tmp >>> 31);
        dst[2] = (int)tmp & Integer.MAX_VALUE;
        tmp = (long)src[0] * (long)factor + (long)carry;
        carry = (int)(tmp >>> 31);
        dst[1] = (int)tmp & Integer.MAX_VALUE;
        dst[0] = carry;
    }

    public final int compare(int[] src1, int[] src2) {
        int result = src1[0] - src2[0];
        if (result != 0) {
            return result;
        }
        result = src1[1] - src2[1];
        if (result != 0) {
            return result;
        }
        return src1[2] - src2[2];
    }

    public final int add(int[] src, int[] srcDst) {
        int result = srcDst[2] + src[2];
        int carry = result < 0 ? 1 : 0;
        srcDst[2] = result = result < 0 ? result - Integer.MIN_VALUE : result;
        result = srcDst[1] + src[1] + carry;
        carry = result < 0 ? 1 : 0;
        srcDst[1] = result = result < 0 ? result - Integer.MIN_VALUE : result;
        result = srcDst[0] + src[0] + carry;
        carry = result < 0 ? 1 : 0;
        srcDst[0] = result = result < 0 ? result - Integer.MIN_VALUE : result;
        return carry;
    }

    public final void subtract(int[] src, int[] srcDst) {
        int result = srcDst[2] - src[2];
        int carry = result < 0 ? 1 : 0;
        srcDst[2] = result = result < 0 ? result + Integer.MIN_VALUE : result;
        result = srcDst[1] - src[1] - carry;
        carry = result < 0 ? 1 : 0;
        srcDst[1] = result = result < 0 ? result + Integer.MIN_VALUE : result;
        result = srcDst[0] - src[0] - carry;
        srcDst[0] = result = result < 0 ? result + Integer.MIN_VALUE : result;
    }

    public final int divide(int[] srcDst) {
        long tmp = ((long)srcDst[0] << 31) + (long)srcDst[1];
        int result = (int)(tmp / (long)this.base);
        int carry = (int)tmp - result * this.base;
        srcDst[0] = 0;
        srcDst[1] = result;
        tmp = ((long)carry << 31) + (long)srcDst[2];
        result = (int)(tmp / (long)this.base);
        carry = (int)tmp - result * this.base;
        srcDst[2] = result;
        return carry;
    }
}

