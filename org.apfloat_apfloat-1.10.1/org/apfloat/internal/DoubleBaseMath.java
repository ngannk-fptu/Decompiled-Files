/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.Serializable;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DoubleRadixConstants;
import org.apfloat.spi.DataStorage;

public class DoubleBaseMath
implements Serializable {
    private static final long serialVersionUID = 4560898425815362356L;
    private int radix;
    private double inverseBase;

    public DoubleBaseMath(int radix) {
        this.radix = radix;
        this.inverseBase = 1.0 / DoubleRadixConstants.BASE[radix];
    }

    public double baseAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        boolean sameDst = src1 == dst || src2 == dst;
        double base = DoubleRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            double result = (src1 == null ? 0.0 : src1.getDouble()) + carry + (src2 == null ? 0.0 : src2.getDouble());
            carry = result >= base ? 1 : 0;
            dst.setDouble(result -= result >= base ? base : 0.0);
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

    public double baseSubtract(DataStorage.Iterator src1, DataStorage.Iterator src2, double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 == null || src1 != src2);
        assert (src2 != dst);
        double base = DoubleRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            double result = (src1 == null ? 0.0 : src1.getDouble()) - carry - (src2 == null ? 0.0 : src2.getDouble());
            carry = result < 0.0 ? 1 : 0;
            dst.setDouble(result += result < 0.0 ? base : 0.0);
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

    public double baseMultiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, double src3, double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != src2);
        assert (src1 != dst);
        double base = DoubleRadixConstants.BASE[this.radix];
        for (long i = 0L; i < size; ++i) {
            double a = src1.getDouble();
            double b = src3;
            long tmp = (long)a * (long)b + (long)(carry += src2 == null ? 0.0 : src2.getDouble());
            carry = (long)((a * b + carry) * this.inverseBase);
            double result = tmp - (long)carry * (long)base;
            carry += (double)(result >= base ? 1 : 0);
            carry -= (double)((result -= result >= base ? base : 0.0) < 0.0 ? 1 : 0);
            dst.setDouble(result += result < 0.0 ? base : 0.0);
            src1.next();
            if (src2 != null && src2 != dst) {
                src2.next();
            }
            dst.next();
        }
        return carry;
    }

    public double baseDivide(DataStorage.Iterator src1, double src2, double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        assert (src1 != dst);
        double base = DoubleRadixConstants.BASE[this.radix];
        double inverseDivisor = 1.0 / src2;
        for (long i = 0L; i < size; ++i) {
            double a = src1 == null ? 0.0 : src1.getDouble();
            long tmp = (long)carry * (long)base + (long)a;
            double result = (long)((carry * base + a) * inverseDivisor);
            carry = tmp - (long)result * (long)src2;
            result += (double)(carry >= src2 ? 1 : 0);
            boolean bl = (carry -= carry >= src2 ? src2 : 0.0) < 0.0;
            carry += carry < 0.0 ? src2 : 0.0;
            dst.setDouble(result -= (double)bl);
            if (src1 != null) {
                src1.next();
            }
            dst.next();
        }
        return carry;
    }
}

