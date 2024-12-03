/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.Serializable;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.FloatRadixConstants;
import org.apfloat.spi.DataStorage;

public class FloatBaseMath
implements Serializable {
    private static final long serialVersionUID = -2321698097908304307L;
    private int radix;

    public FloatBaseMath(int radix) {
        this.radix = radix;
    }

    public float baseAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        boolean sameDst = src1 == dst || src2 == dst;
        float base = FloatRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            double result = (double)(src1 == null ? 0.0f : src1.getFloat()) + (double)carry + (double)(src2 == null ? 0.0f : src2.getFloat());
            carry = result >= (double)base ? 1 : 0;
            dst.setFloat((float)(result -= (double)(result >= (double)base ? base : 0.0f)));
            if (src1 != null) {
                src1.next();
            }
            if (src2 != null) {
                src2.next();
            }
            if (sameDst) continue;
            dst.next();
        }
        return carry;
    }

    public float baseSubtract(DataStorage.Iterator src1, DataStorage.Iterator src2, float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        assert (src2 != dst);
        float base = FloatRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            float result = (src1 == null ? 0.0f : src1.getFloat()) - carry - (src2 == null ? 0.0f : src2.getFloat());
            carry = result < 0.0f ? 1 : 0;
            dst.setFloat(result += result < 0.0f ? base : 0.0f);
            if (src1 != null && src1 != dst) {
                src1.next();
            }
            if (src2 != null) {
                src2.next();
            }
            dst.next();
        }
        return carry;
    }

    public float baseMultiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, float src3, float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != src2);
        assert (src1 != dst);
        double base = FloatRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            double tmp = (double)src1.getFloat() * (double)src3 + (double)(src2 == null ? 0.0f : src2.getFloat()) + (double)carry;
            carry = (int)(tmp / base);
            dst.setFloat((float)(tmp - (double)carry * base));
            src1.next();
            if (src2 != null && src2 != dst) {
                src2.next();
            }
            dst.next();
        }
        return carry;
    }

    public float baseDivide(DataStorage.Iterator src1, float src2, float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != dst);
        double base = FloatRadixConstants.BASE[this.radix];
        double divisor = src2;
        for (long i = 0L; i < size; ++i) {
            double tmp = (double)carry * base + (double)(src1 == null ? 0.0f : src1.getFloat());
            float result = (int)(tmp / divisor);
            carry = (float)(tmp - (double)result * divisor);
            dst.setFloat(result);
            if (src1 != null) {
                src1.next();
            }
            dst.next();
        }
        return carry;
    }
}

