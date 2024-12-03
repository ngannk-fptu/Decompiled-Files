/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.Serializable;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.IntRadixConstants;
import org.apfloat.spi.DataStorage;

public class IntBaseMath
implements Serializable {
    private static final long serialVersionUID = 2173589976837534455L;
    private int radix;

    public IntBaseMath(int radix) {
        this.radix = radix;
    }

    public int baseAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, int carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        boolean sameDst = src1 == dst || src2 == dst;
        int base = IntRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            int result = (src1 == null ? 0 : src1.getInt()) + carry + (src2 == null ? 0 : src2.getInt());
            carry = result >= base | result < 0 ? 1 : 0;
            dst.setInt(result -= result >= base | result < 0 ? base : 0);
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

    public int baseSubtract(DataStorage.Iterator src1, DataStorage.Iterator src2, int carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        assert (src2 != dst);
        int base = IntRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            int result = (src1 == null ? 0 : src1.getInt()) - carry - (src2 == null ? 0 : src2.getInt());
            carry = result < 0 ? 1 : 0;
            dst.setInt(result += result < 0 ? base : 0);
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

    public int baseMultiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, int src3, int carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != src2);
        assert (src1 != dst);
        int base = IntRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            long tmp = (long)src1.getInt() * (long)src3 + (long)(src2 == null ? 0 : src2.getInt()) + (long)carry;
            carry = (int)(tmp / (long)base);
            dst.setInt((int)tmp - carry * base);
            src1.next();
            if (src2 != null && src2 != dst) {
                src2.next();
            }
            dst.next();
        }
        return carry;
    }

    public int baseDivide(DataStorage.Iterator src1, int src2, int carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != dst);
        int base = IntRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            long tmp = (long)carry * (long)base + (long)(src1 == null ? 0 : src1.getInt());
            int result = (int)(tmp / (long)src2);
            carry = (int)tmp - result * src2;
            dst.setInt(result);
            if (src1 != null) {
                src1.next();
            }
            dst.next();
        }
        return carry;
    }
}

