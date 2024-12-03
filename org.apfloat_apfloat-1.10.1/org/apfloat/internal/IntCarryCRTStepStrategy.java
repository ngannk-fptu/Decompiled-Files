/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.math.BigInteger;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.IntCRTMath;
import org.apfloat.internal.IntModConstants;
import org.apfloat.internal.IntModMath;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.DataStorage;

public class IntCarryCRTStepStrategy
extends IntCRTMath
implements CarryCRTStepStrategy<int[]> {
    private static final long serialVersionUID = 7666237487091579201L;
    private static final IntModMath MATH_MOD_0 = new IntModMath();
    private static final IntModMath MATH_MOD_1 = new IntModMath();
    private static final IntModMath MATH_MOD_2 = new IntModMath();
    private static final int T0;
    private static final int T1;
    private static final int T2;
    private static final int[] M01;
    private static final int[] M02;
    private static final int[] M12;
    private static final int[] M012;

    public IntCarryCRTStepStrategy(int radix) {
        super(radix);
    }

    @Override
    public int[] crt(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, DataStorage dataStorage, long size, long resultSize, long offset, long length) throws ApfloatRuntimeException {
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
            int[] results;
            int[] carryResult = new int[3];
            int[] sum = new int[3];
            int[] tmp = new int[3];
            for (long i = 0L; i < length; ++i) {
                int y0 = MATH_MOD_0.modMultiply(T0, src0.getInt());
                int y1 = MATH_MOD_1.modMultiply(T1, src1.getInt());
                int y2 = MATH_MOD_2.modMultiply(T2, src2.getInt());
                this.multiply(M12, y0, sum);
                this.multiply(M02, y1, tmp);
                if (this.add(tmp, sum) != 0 || this.compare(sum, M012) >= 0) {
                    this.subtract(M012, sum);
                }
                this.multiply(M01, y2, tmp);
                if (this.add(tmp, sum) != 0 || this.compare(sum, M012) >= 0) {
                    this.subtract(M012, sum);
                }
                this.add(sum, carryResult);
                int result = this.divide(carryResult);
                if (i >= skipSize) {
                    dst.setInt(result);
                    dst.next();
                }
                src0.next();
                src1.next();
                src2.next();
            }
            int result0 = this.divide(carryResult);
            int result1 = carryResult[2];
            assert (carryResult[0] == 0);
            assert (carryResult[1] == 0);
            if (subResultSize == length - skipSize + 1L) {
                dst.setInt(result0);
                result0 = result1;
                assert (result1 == 0);
            }
            int[] nArray = results = new int[]{result1, result0};
            return nArray;
        }
    }

    @Override
    public int[] carry(DataStorage dataStorage, long size, long resultSize, long offset, long length, int[] results, int[] previousResults) throws ApfloatRuntimeException {
        long skipSize = offset == 0L ? size - resultSize + 1L : 0L;
        long lastSize = offset + length == size ? 1 : 0;
        long nonLastSize = 1L - lastSize;
        long subResultSize = length - skipSize + lastSize;
        long subResultStart = size - offset - length + nonLastSize + subResultSize;
        long subResultEnd = subResultStart - subResultSize;
        DataStorage.Iterator src = IntCarryCRTStepStrategy.arrayIterator(previousResults);
        try (DataStorage.Iterator dst = IntCarryCRTStepStrategy.compositeIterator(dataStorage.iterator(3, subResultStart, subResultEnd), subResultSize, IntCarryCRTStepStrategy.arrayIterator(results));){
            int carry = this.baseAdd(dst, src, 0, dst, previousResults.length);
            carry = this.baseCarry(dst, carry, subResultSize);
            assert (carry == 0);
        }
        return results;
    }

    private int baseCarry(DataStorage.Iterator srcDst, int carry, long size) throws ApfloatRuntimeException {
        for (long i = 0L; i < size && carry > 0; ++i) {
            carry = this.baseAdd(srcDst, null, carry, srcDst, 1L);
        }
        return carry;
    }

    private static DataStorage.Iterator arrayIterator(final int[] data) {
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
            public int getInt() {
                assert (this.position >= 0);
                return data[this.position];
            }

            @Override
            public void setInt(int value) {
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
            public int getInt() throws ApfloatRuntimeException {
                return (this.position < size ? iterator1 : iterator2).getInt();
            }

            @Override
            public void setInt(int value) throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).setInt(value);
            }

            @Override
            public void close() throws ApfloatRuntimeException {
                (this.position < size ? iterator1 : iterator2).close();
            }
        };
    }

    static {
        MATH_MOD_0.setModulus(IntModConstants.MODULUS[0]);
        MATH_MOD_1.setModulus(IntModConstants.MODULUS[1]);
        MATH_MOD_2.setModulus(IntModConstants.MODULUS[2]);
        BigInteger base = BigInteger.valueOf(Math.abs(Integer.MIN_VALUE));
        BigInteger m0 = BigInteger.valueOf(IntModConstants.MODULUS[0]);
        BigInteger m1 = BigInteger.valueOf(IntModConstants.MODULUS[1]);
        BigInteger m2 = BigInteger.valueOf(IntModConstants.MODULUS[2]);
        BigInteger m01 = m0.multiply(m1);
        BigInteger m02 = m0.multiply(m2);
        BigInteger m12 = m1.multiply(m2);
        T0 = m12.modInverse(m0).intValue();
        T1 = m02.modInverse(m1).intValue();
        T2 = m01.modInverse(m2).intValue();
        M01 = new int[2];
        M02 = new int[2];
        M12 = new int[2];
        M012 = new int[3];
        BigInteger[] qr = m01.divideAndRemainder(base);
        IntCarryCRTStepStrategy.M01[0] = qr[0].intValue();
        IntCarryCRTStepStrategy.M01[1] = qr[1].intValue();
        qr = m02.divideAndRemainder(base);
        IntCarryCRTStepStrategy.M02[0] = qr[0].intValue();
        IntCarryCRTStepStrategy.M02[1] = qr[1].intValue();
        qr = m12.divideAndRemainder(base);
        IntCarryCRTStepStrategy.M12[0] = qr[0].intValue();
        IntCarryCRTStepStrategy.M12[1] = qr[1].intValue();
        qr = m0.multiply(m12).divideAndRemainder(base);
        IntCarryCRTStepStrategy.M012[2] = qr[1].intValue();
        qr = qr[0].divideAndRemainder(base);
        IntCarryCRTStepStrategy.M012[0] = qr[0].intValue();
        IntCarryCRTStepStrategy.M012[1] = qr[1].intValue();
    }
}

