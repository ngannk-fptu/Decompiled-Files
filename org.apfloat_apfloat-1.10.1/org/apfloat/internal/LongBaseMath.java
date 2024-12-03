/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.Serializable;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongRadixConstants;
import org.apfloat.spi.DataStorage;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LongBaseMath
implements Serializable {
    private static final long serialVersionUID = -6469225916787810664L;
    private int radix;
    private double inverseBase;

    public LongBaseMath(int radix) {
        this.radix = radix;
        this.inverseBase = 1.0 / (double)LongRadixConstants.BASE[radix];
    }

    public long baseAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        boolean sameDst = src1 == dst || src2 == dst;
        long base = LongRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            long result = (src1 == null ? 0L : src1.getLong()) + carry + (src2 == null ? 0L : src2.getLong());
            carry = result >= base ? 1 : 0;
            dst.setLong(result -= result >= base ? base : 0L);
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

    public long baseSubtract(DataStorage.Iterator src1, DataStorage.Iterator src2, long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        assert (src2 != dst);
        long base = LongRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            long result = (src1 == null ? 0L : src1.getLong()) - carry - (src2 == null ? 0L : src2.getLong());
            carry = result < 0L ? 1 : 0;
            dst.setLong(result += result < 0L ? base : 0L);
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

    public long baseMultiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, long src3, long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != src2);
        assert (src1 != dst);
        long base = LongRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            long a = src1.getLong();
            long b = src3;
            long tmp = a * b + (carry += src2 == null ? 0L : src2.getLong());
            carry = (long)(((double)a * (double)b + (double)carry) * this.inverseBase);
            int tmp2 = (int)((double)(tmp -= carry * base) * this.inverseBase);
            carry += (long)tmp2;
            carry += (long)((tmp -= (long)tmp2 * base) >= base ? 1 : 0);
            carry += (long)((tmp -= tmp >= base ? base : 0L) >= base ? 1 : 0);
            carry -= (long)((tmp -= tmp >= base ? base : 0L) < 0L ? 1 : 0);
            carry -= (long)((tmp += tmp < 0L ? base : 0L) < 0L ? 1 : 0);
            dst.setLong(tmp += tmp < 0L ? base : 0L);
            src1.next();
            if (src2 != null && src2 != dst) {
                src2.next();
            }
            dst.next();
        }
        return carry;
    }

    public long baseDivide(DataStorage.Iterator src1, long src2, long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != dst);
        long base = LongRadixConstants.BASE[this.radix];
        double inverseDivisor = 1.0 / (double)src2;
        for (long i = 0L; i < size; ++i) {
            long a = src1 == null ? 0L : src1.getLong();
            long tmp = carry * base + a;
            long result = (long)(((double)carry * (double)base + (double)a) * inverseDivisor);
            carry = tmp - result * src2;
            int tmp2 = (int)((double)carry * inverseDivisor);
            result += (long)tmp2;
            result += (long)((carry -= (long)tmp2 * src2) >= src2 ? 1 : 0);
            result += (long)((carry -= carry >= src2 ? src2 : 0L) >= src2 ? 1 : 0);
            result -= (long)((carry -= carry >= src2 ? src2 : 0L) < 0L ? 1 : 0);
            int n = (carry += carry < 0L ? src2 : 0L) < 0L ? 1 : 0;
            carry += carry < 0L ? src2 : 0L;
            dst.setLong(result -= (long)n);
            if (src1 != null) {
                src1.next();
            }
            dst.next();
        }
        return carry;
    }
}

