/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.math.BigInteger;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DoubleCRTMath;
import org.apfloat.internal.DoubleModConstants;
import org.apfloat.internal.DoubleModMath;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.DataStorage;

public class DoubleCarryCRTStepStrategy
extends DoubleCRTMath
implements CarryCRTStepStrategy<double[]> {
    private static final long serialVersionUID = 2974874464027705533L;
    private static final DoubleModMath MATH_MOD_0 = new DoubleModMath();
    private static final DoubleModMath MATH_MOD_1 = new DoubleModMath();
    private static final DoubleModMath MATH_MOD_2 = new DoubleModMath();
    private static final double T0;
    private static final double T1;
    private static final double T2;
    private static final double[] M01;
    private static final double[] M02;
    private static final double[] M12;
    private static final double[] M012;

    public DoubleCarryCRTStepStrategy(int radix) {
        super(radix);
    }

    @Override
    public double[] crt(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, DataStorage dataStorage, long size, long resultSize, long offset, long length) throws ApfloatRuntimeException {
        long skipSize = offset == 0L ? size - resultSize + 1L : 0L;
        long lastSize = offset + length == size ? 1 : 0;
        long nonLastSize = 1L - lastSize;
        long subResultSize = length - skipSize + lastSize;
        long subStart = size - offset;
        long subEnd = subStart - length;
        long subResultStart = size - offset - length + nonLastSize + subResultSize;
        long subResultEnd = subResultStart - subResultSize;
        DataStorage.Iterator src0 = resultMod0.iterator(1, subStart, subEnd);
        DataStorage.Iterator src1 = resultMod1.iterator(1, subStart, subEnd);
        DataStorage.Iterator src2 = resultMod2.iterator(1, subStart, subEnd);
        try (DataStorage.Iterator dst = dataStorage.iterator(2, subResultStart, subResultEnd);){
            double[] results;
            double[] carryResult = new double[3];
            double[] sum = new double[3];
            double[] tmp = new double[3];
            for (long i = 0L; i < length; ++i) {
                double y0 = MATH_MOD_0.modMultiply(T0, src0.getDouble());
                double y1 = MATH_MOD_1.modMultiply(T1, src1.getDouble());
                double y2 = MATH_MOD_2.modMultiply(T2, src2.getDouble());
                this.multiply(M12, y0, sum);
                this.multiply(M02, y1, tmp);
                if (this.add(tmp, sum) != 0.0 || this.compare(sum, M012) >= 0.0) {
                    this.subtract(M012, sum);
                }
                this.multiply(M01, y2, tmp);
                if (this.add(tmp, sum) != 0.0 || this.compare(sum, M012) >= 0.0) {
                    this.subtract(M012, sum);
                }
                this.add(sum, carryResult);
                double result = this.divide(carryResult);
                if (i >= skipSize) {
                    dst.setDouble(result);
                    dst.next();
                }
                src0.next();
                src1.next();
                src2.next();
            }
            double result0 = this.divide(carryResult);
            double result1 = carryResult[2];
            assert (carryResult[0] == 0.0);
            assert (carryResult[1] == 0.0);
            if (subResultSize == length - skipSize + 1L) {
                dst.setDouble(result0);
                result0 = result1;
                assert (result1 == 0.0);
            }
            double[] dArray = results = new double[]{result1, result0};
            return dArray;
        }
    }

    @Override
    public double[] carry(DataStorage dataStorage, long size, long resultSize, long offset, long length, double[] results, double[] previousResults) throws ApfloatRuntimeException {
        long skipSize = offset == 0L ? size - resultSize + 1L : 0L;
        long lastSize = offset + length == size ? 1 : 0;
        long nonLastSize = 1L - lastSize;
        long subResultSize = length - skipSize + lastSize;
        long subResultStart = size - offset - length + nonLastSize + subResultSize;
        long subResultEnd = subResultStart - subResultSize;
        DataStorage.Iterator src = DoubleCarryCRTStepStrategy.arrayIterator(previousResults);
        try (DataStorage.Iterator dst = DoubleCarryCRTStepStrategy.compositeIterator(dataStorage.iterator(3, subResultStart, subResultEnd), subResultSize, DoubleCarryCRTStepStrategy.arrayIterator(results));){
            double carry = this.baseAdd(dst, src, 0.0, dst, previousResults.length);
            carry = this.baseCarry(dst, carry, subResultSize);
            assert (carry == 0.0);
        }
        return results;
    }

    private double baseCarry(DataStorage.Iterator srcDst, double carry, long size) throws ApfloatRuntimeException {
        for (long i = 0L; i < size && carry > 0.0; ++i) {
            carry = this.baseAdd(srcDst, null, carry, srcDst, 1L);
        }
        return carry;
    }

    private static DataStorage.Iterator arrayIterator(final double[] data) {
        return new DataStorage.Iterator(){
            private static final long serialVersionUID = 1L;
            private int position;
            {
                this.position = data.length - 1;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public void next() {
                --this.position;
            }

            @Override
            public double getDouble() {
                assert (this.position >= 0);
                return data[this.position];
            }

            @Override
            public void setDouble(double value) {
                assert (this.position >= 0);
                data[this.position] = value;
            }
        };
    }

    private static DataStorage.Iterator compositeIterator(final DataStorage.Iterator iterator1, final long size, final DataStorage.Iterator iterator2) {
        return new DataStorage.Iterator(){
            private static final long serialVersionUID = 1L;
            private long position;

            @Override
            public boolean hasNext() {
                return this.position < size ? iterator1.hasNext() : iterator2.hasNext();
            }

            @Override
            public void next() throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).next();
                ++this.position;
            }

            @Override
            public double getDouble() throws ApfloatRuntimeException {
                return (this.position < size ? iterator1 : iterator2).getDouble();
            }

            @Override
            public void setDouble(double value) throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).setDouble(value);
            }

            @Override
            public void close() throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).close();
            }
        };
    }

    static {
        MATH_MOD_0.setModulus(DoubleModConstants.MODULUS[0]);
        MATH_MOD_1.setModulus(DoubleModConstants.MODULUS[1]);
        MATH_MOD_2.setModulus(DoubleModConstants.MODULUS[2]);
        BigInteger base = BigInteger.valueOf(Math.abs(0x8000000000000L));
        BigInteger m0 = BigInteger.valueOf((long)DoubleModConstants.MODULUS[0]);
        BigInteger m1 = BigInteger.valueOf((long)DoubleModConstants.MODULUS[1]);
        BigInteger m2 = BigInteger.valueOf((long)DoubleModConstants.MODULUS[2]);
        BigInteger m01 = m0.multiply(m1);
        BigInteger m02 = m0.multiply(m2);
        BigInteger m12 = m1.multiply(m2);
        T0 = m12.modInverse(m0).doubleValue();
        T1 = m02.modInverse(m1).doubleValue();
        T2 = m01.modInverse(m2).doubleValue();
        M01 = new double[2];
        M02 = new double[2];
        M12 = new double[2];
        M012 = new double[3];
        BigInteger[] qr = m01.divideAndRemainder(base);
        DoubleCarryCRTStepStrategy.M01[0] = qr[0].doubleValue();
        DoubleCarryCRTStepStrategy.M01[1] = qr[1].doubleValue();
        qr = m02.divideAndRemainder(base);
        DoubleCarryCRTStepStrategy.M02[0] = qr[0].doubleValue();
        DoubleCarryCRTStepStrategy.M02[1] = qr[1].doubleValue();
        qr = m12.divideAndRemainder(base);
        DoubleCarryCRTStepStrategy.M12[0] = qr[0].doubleValue();
        DoubleCarryCRTStepStrategy.M12[1] = qr[1].doubleValue();
        qr = m0.multiply(m12).divideAndRemainder(base);
        DoubleCarryCRTStepStrategy.M012[2] = qr[1].doubleValue();
        qr = qr[0].divideAndRemainder(base);
        DoubleCarryCRTStepStrategy.M012[0] = qr[0].doubleValue();
        DoubleCarryCRTStepStrategy.M012[1] = qr[1].doubleValue();
    }
}

