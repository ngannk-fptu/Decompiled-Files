/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.math.BigInteger;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongCRTMath;
import org.apfloat.internal.LongModConstants;
import org.apfloat.internal.LongModMath;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.DataStorage;

public class LongCarryCRTStepStrategy
extends LongCRTMath
implements CarryCRTStepStrategy<long[]> {
    private static final long serialVersionUID = -1851512769800204475L;
    private static final LongModMath MATH_MOD_0 = new LongModMath();
    private static final LongModMath MATH_MOD_1 = new LongModMath();
    private static final LongModMath MATH_MOD_2 = new LongModMath();
    private static final long T0;
    private static final long T1;
    private static final long T2;
    private static final long[] M01;
    private static final long[] M02;
    private static final long[] M12;
    private static final long[] M012;

    public LongCarryCRTStepStrategy(int radix) {
        super(radix);
    }

    @Override
    public long[] crt(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, DataStorage dataStorage, long size, long resultSize, long offset, long length) throws ApfloatRuntimeException {
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
            long[] results;
            long[] carryResult = new long[3];
            long[] sum = new long[3];
            long[] tmp = new long[3];
            for (long i = 0L; i < length; ++i) {
                long y0 = MATH_MOD_0.modMultiply(T0, src0.getLong());
                long y1 = MATH_MOD_1.modMultiply(T1, src1.getLong());
                long y2 = MATH_MOD_2.modMultiply(T2, src2.getLong());
                this.multiply(M12, y0, sum);
                this.multiply(M02, y1, tmp);
                if (this.add(tmp, sum) != 0L || this.compare(sum, M012) >= 0L) {
                    this.subtract(M012, sum);
                }
                this.multiply(M01, y2, tmp);
                if (this.add(tmp, sum) != 0L || this.compare(sum, M012) >= 0L) {
                    this.subtract(M012, sum);
                }
                this.add(sum, carryResult);
                long result = this.divide(carryResult);
                if (i >= skipSize) {
                    dst.setLong(result);
                    dst.next();
                }
                src0.next();
                src1.next();
                src2.next();
            }
            long result0 = this.divide(carryResult);
            long result1 = carryResult[2];
            assert (carryResult[0] == 0L);
            assert (carryResult[1] == 0L);
            if (subResultSize == length - skipSize + 1L) {
                dst.setLong(result0);
                result0 = result1;
                assert (result1 == 0L);
            }
            long[] lArray = results = new long[]{result1, result0};
            return lArray;
        }
    }

    @Override
    public long[] carry(DataStorage dataStorage, long size, long resultSize, long offset, long length, long[] results, long[] previousResults) throws ApfloatRuntimeException {
        long skipSize = offset == 0L ? size - resultSize + 1L : 0L;
        long lastSize = offset + length == size ? 1 : 0;
        long nonLastSize = 1L - lastSize;
        long subResultSize = length - skipSize + lastSize;
        long subResultStart = size - offset - length + nonLastSize + subResultSize;
        long subResultEnd = subResultStart - subResultSize;
        DataStorage.Iterator src = LongCarryCRTStepStrategy.arrayIterator(previousResults);
        try (DataStorage.Iterator dst = LongCarryCRTStepStrategy.compositeIterator(dataStorage.iterator(3, subResultStart, subResultEnd), subResultSize, LongCarryCRTStepStrategy.arrayIterator(results));){
            long carry = this.baseAdd(dst, src, 0L, dst, previousResults.length);
            carry = this.baseCarry(dst, carry, subResultSize);
            assert (carry == 0L);
        }
        return results;
    }

    private long baseCarry(DataStorage.Iterator srcDst, long carry, long size) throws ApfloatRuntimeException {
        for (long i = 0L; i < size && carry > 0L; ++i) {
            carry = this.baseAdd(srcDst, null, carry, srcDst, 1L);
        }
        return carry;
    }

    private static DataStorage.Iterator arrayIterator(final long[] data) {
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
            public long getLong() {
                assert (this.position >= 0);
                return data[this.position];
            }

            @Override
            public void setLong(long value) {
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
            public long getLong() throws ApfloatRuntimeException {
                return (this.position < size ? iterator1 : iterator2).getLong();
            }

            @Override
            public void setLong(long value) throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).setLong(value);
            }

            @Override
            public void close() throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).close();
            }
        };
    }

    static {
        MATH_MOD_0.setModulus(LongModConstants.MODULUS[0]);
        MATH_MOD_1.setModulus(LongModConstants.MODULUS[1]);
        MATH_MOD_2.setModulus(LongModConstants.MODULUS[2]);
        BigInteger base = BigInteger.valueOf(Math.abs(0x200000000000000L));
        BigInteger m0 = BigInteger.valueOf(LongModConstants.MODULUS[0]);
        BigInteger m1 = BigInteger.valueOf(LongModConstants.MODULUS[1]);
        BigInteger m2 = BigInteger.valueOf(LongModConstants.MODULUS[2]);
        BigInteger m01 = m0.multiply(m1);
        BigInteger m02 = m0.multiply(m2);
        BigInteger m12 = m1.multiply(m2);
        T0 = m12.modInverse(m0).longValue();
        T1 = m02.modInverse(m1).longValue();
        T2 = m01.modInverse(m2).longValue();
        M01 = new long[2];
        M02 = new long[2];
        M12 = new long[2];
        M012 = new long[3];
        BigInteger[] qr = m01.divideAndRemainder(base);
        LongCarryCRTStepStrategy.M01[0] = qr[0].longValue();
        LongCarryCRTStepStrategy.M01[1] = qr[1].longValue();
        qr = m02.divideAndRemainder(base);
        LongCarryCRTStepStrategy.M02[0] = qr[0].longValue();
        LongCarryCRTStepStrategy.M02[1] = qr[1].longValue();
        qr = m12.divideAndRemainder(base);
        LongCarryCRTStepStrategy.M12[0] = qr[0].longValue();
        LongCarryCRTStepStrategy.M12[1] = qr[1].longValue();
        qr = m0.multiply(m12).divideAndRemainder(base);
        LongCarryCRTStepStrategy.M012[2] = qr[1].longValue();
        qr = qr[0].divideAndRemainder(base);
        LongCarryCRTStepStrategy.M012[0] = qr[0].longValue();
        LongCarryCRTStepStrategy.M012[1] = qr[1].longValue();
    }
}

