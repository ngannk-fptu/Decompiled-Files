/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.math.BigInteger;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.FloatCRTMath;
import org.apfloat.internal.FloatModConstants;
import org.apfloat.internal.FloatModMath;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.DataStorage;

public class FloatCarryCRTStepStrategy
extends FloatCRTMath
implements CarryCRTStepStrategy<float[]> {
    private static final long serialVersionUID = 3192182234524626533L;
    private static final FloatModMath MATH_MOD_0 = new FloatModMath();
    private static final FloatModMath MATH_MOD_1 = new FloatModMath();
    private static final FloatModMath MATH_MOD_2 = new FloatModMath();
    private static final float T0;
    private static final float T1;
    private static final float T2;
    private static final float[] M01;
    private static final float[] M02;
    private static final float[] M12;
    private static final float[] M012;

    public FloatCarryCRTStepStrategy(int radix) {
        super(radix);
    }

    @Override
    public float[] crt(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, DataStorage dataStorage, long size, long resultSize, long offset, long length) throws ApfloatRuntimeException {
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
            float[] results;
            float[] carryResult = new float[3];
            float[] sum = new float[3];
            float[] tmp = new float[3];
            for (long i = 0L; i < length; ++i) {
                float y0 = MATH_MOD_0.modMultiply(T0, src0.getFloat());
                float y1 = MATH_MOD_1.modMultiply(T1, src1.getFloat());
                float y2 = MATH_MOD_2.modMultiply(T2, src2.getFloat());
                this.multiply(M12, y0, sum);
                this.multiply(M02, y1, tmp);
                if (this.add(tmp, sum) != 0.0f || this.compare(sum, M012) >= 0.0f) {
                    this.subtract(M012, sum);
                }
                this.multiply(M01, y2, tmp);
                if (this.add(tmp, sum) != 0.0f || this.compare(sum, M012) >= 0.0f) {
                    this.subtract(M012, sum);
                }
                this.add(sum, carryResult);
                float result = this.divide(carryResult);
                if (i >= skipSize) {
                    dst.setFloat(result);
                    dst.next();
                }
                src0.next();
                src1.next();
                src2.next();
            }
            float result0 = this.divide(carryResult);
            float result1 = carryResult[2];
            assert (carryResult[0] == 0.0f);
            assert (carryResult[1] == 0.0f);
            if (subResultSize == length - skipSize + 1L) {
                dst.setFloat(result0);
                result0 = result1;
                assert (result1 == 0.0f);
            }
            float[] fArray = results = new float[]{result1, result0};
            return fArray;
        }
    }

    @Override
    public float[] carry(DataStorage dataStorage, long size, long resultSize, long offset, long length, float[] results, float[] previousResults) throws ApfloatRuntimeException {
        long skipSize = offset == 0L ? size - resultSize + 1L : 0L;
        long lastSize = offset + length == size ? 1 : 0;
        long nonLastSize = 1L - lastSize;
        long subResultSize = length - skipSize + lastSize;
        long subResultStart = size - offset - length + nonLastSize + subResultSize;
        long subResultEnd = subResultStart - subResultSize;
        DataStorage.Iterator src = FloatCarryCRTStepStrategy.arrayIterator(previousResults);
        try (DataStorage.Iterator dst = FloatCarryCRTStepStrategy.compositeIterator(dataStorage.iterator(3, subResultStart, subResultEnd), subResultSize, FloatCarryCRTStepStrategy.arrayIterator(results));){
            float carry = this.baseAdd(dst, src, 0.0f, dst, previousResults.length);
            carry = this.baseCarry(dst, carry, subResultSize);
            assert (carry == 0.0f);
        }
        return results;
    }

    private float baseCarry(DataStorage.Iterator srcDst, float carry, long size) throws ApfloatRuntimeException {
        for (long i = 0L; i < size && carry > 0.0f; ++i) {
            carry = this.baseAdd(srcDst, null, carry, srcDst, 1L);
        }
        return carry;
    }

    private static DataStorage.Iterator arrayIterator(final float[] data) {
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
            public float getFloat() {
                assert (this.position >= 0);
                return data[this.position];
            }

            @Override
            public void setFloat(float value) {
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
            public float getFloat() throws ApfloatRuntimeException {
                return (this.position < size ? iterator1 : iterator2).getFloat();
            }

            @Override
            public void setFloat(float value) throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).setFloat(value);
            }

            @Override
            public void close() throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).close();
            }
        };
    }

    static {
        MATH_MOD_0.setModulus(FloatModConstants.MODULUS[0]);
        MATH_MOD_1.setModulus(FloatModConstants.MODULUS[1]);
        MATH_MOD_2.setModulus(FloatModConstants.MODULUS[2]);
        BigInteger base = BigInteger.valueOf(Math.abs(0x1000000L));
        BigInteger m0 = BigInteger.valueOf((long)FloatModConstants.MODULUS[0]);
        BigInteger m1 = BigInteger.valueOf((long)FloatModConstants.MODULUS[1]);
        BigInteger m2 = BigInteger.valueOf((long)FloatModConstants.MODULUS[2]);
        BigInteger m01 = m0.multiply(m1);
        BigInteger m02 = m0.multiply(m2);
        BigInteger m12 = m1.multiply(m2);
        T0 = m12.modInverse(m0).floatValue();
        T1 = m02.modInverse(m1).floatValue();
        T2 = m01.modInverse(m2).floatValue();
        M01 = new float[2];
        M02 = new float[2];
        M12 = new float[2];
        M012 = new float[3];
        BigInteger[] qr = m01.divideAndRemainder(base);
        FloatCarryCRTStepStrategy.M01[0] = qr[0].floatValue();
        FloatCarryCRTStepStrategy.M01[1] = qr[1].floatValue();
        qr = m02.divideAndRemainder(base);
        FloatCarryCRTStepStrategy.M02[0] = qr[0].floatValue();
        FloatCarryCRTStepStrategy.M02[1] = qr[1].floatValue();
        qr = m12.divideAndRemainder(base);
        FloatCarryCRTStepStrategy.M12[0] = qr[0].floatValue();
        FloatCarryCRTStepStrategy.M12[1] = qr[1].floatValue();
        qr = m0.multiply(m12).divideAndRemainder(base);
        FloatCarryCRTStepStrategy.M012[2] = qr[1].floatValue();
        qr = qr[0].divideAndRemainder(base);
        FloatCarryCRTStepStrategy.M012[0] = qr[0].floatValue();
        FloatCarryCRTStepStrategy.M012[1] = qr[1].floatValue();
    }
}

