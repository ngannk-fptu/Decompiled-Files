/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongMediumConvolutionStrategy;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;

public class LongKaratsubaConvolutionStrategy
extends LongMediumConvolutionStrategy {
    public static final int CUTOFF_POINT = 15;
    private static final long serialVersionUID = -4812398042499004749L;

    public LongKaratsubaConvolutionStrategy(int radix) {
        super(radix);
    }

    @Override
    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize) throws ApfloatRuntimeException {
        DataStorage longStorage;
        DataStorage shortStorage;
        if (Math.min(x.getSize(), y.getSize()) <= 15L) {
            return super.convolute(x, y, resultSize);
        }
        if (x.getSize() > y.getSize()) {
            shortStorage = y;
            longStorage = x;
        } else {
            shortStorage = x;
            longStorage = y;
        }
        long shortSize = shortStorage.getSize();
        long longSize = longStorage.getSize();
        long size = shortSize + longSize;
        long halfSize = longSize + 1L >> 1;
        long x1size = longSize - halfSize;
        long x2size = halfSize;
        long y1size = shortSize - halfSize;
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 8L);
        resultStorage.setSize(size);
        if (y1size <= 0L) {
            long xSize;
            DataStorage.Iterator dst = resultStorage.iterator(2, size, 0L);
            DataStorage.Iterator src1 = null;
            long carry = 0L;
            long i = longSize;
            do {
                xSize = Math.min(i, shortSize);
                x = longStorage.subsequence(i - xSize, xSize);
                y = shortStorage;
                DataStorage a = this.convolute(x, y, xSize + shortSize);
                assert (a.getSize() == xSize + shortSize);
                DataStorage.Iterator src2 = a.iterator(1, xSize + shortSize, 0L);
                carry = this.baseAdd(src1, src2, carry, dst, shortSize);
                src1 = src2;
            } while ((i -= shortSize) > 0L);
            carry = this.baseAdd(src1, null, carry, dst, xSize);
            assert (carry == 0L);
        } else {
            DataStorage x1 = longStorage.subsequence(0L, x1size);
            DataStorage x2 = longStorage.subsequence(x1size, x2size);
            DataStorage y1 = shortStorage.subsequence(0L, y1size);
            DataStorage y2 = shortStorage.subsequence(y1size, halfSize);
            DataStorage a = this.add(x1, x2);
            DataStorage b = this.add(y1, y2);
            DataStorage c = this.convolute(a, b, a.getSize() + b.getSize());
            a = this.convolute(x1, y1, x1size + y1size);
            b = this.convolute(x2, y2, 2L * halfSize);
            this.subtract(c, a);
            this.subtract(c, b);
            long cSize = c.getSize();
            long c1size = cSize - halfSize;
            if (c1size > x1size + y1size) {
                long zeros = c1size - x1size - y1size;
                assert (this.isZero(c, 0L));
                assert (zeros == 1L || this.isZero(c, 1L));
                assert (zeros <= 2L);
                c1size -= zeros;
                c = c.subsequence(zeros, cSize -= zeros);
            }
            assert (a.getSize() == x1size + y1size);
            assert (b.getSize() == 2L * halfSize);
            assert (cSize >= 2L * halfSize && cSize <= 2L * halfSize + 2L);
            assert (c1size <= x1size + y1size);
            DataStorage.Iterator src1 = a.iterator(1, x1size + y1size, 0L);
            DataStorage.Iterator src2 = b.iterator(1, 2L * halfSize, 0L);
            DataStorage.Iterator src3 = c.iterator(1, cSize, 0L);
            DataStorage.Iterator dst = resultStorage.iterator(2, size, 0L);
            long carry = 0L;
            carry = this.baseAdd(src2, null, carry, dst, halfSize);
            carry = this.baseAdd(src2, src3, carry, dst, halfSize);
            carry = this.baseAdd(src1, src3, carry, dst, c1size);
            carry = this.baseAdd(src1, null, carry, dst, x1size + y1size - c1size);
            assert (carry == 0L);
        }
        return resultStorage;
    }

    private DataStorage add(DataStorage x1, DataStorage x2) {
        long x1size = x1.getSize();
        long x2size = x2.getSize();
        assert (x1size <= x2size);
        long size = x2size + 1L;
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 8L);
        resultStorage.setSize(size);
        DataStorage.Iterator src1 = x1.iterator(1, x1size, 0L);
        DataStorage.Iterator src2 = x2.iterator(1, x2size, 0L);
        DataStorage.Iterator dst = resultStorage.iterator(2, size, 0L);
        long carry = 0L;
        carry = this.baseAdd(src1, src2, carry, dst, x1size);
        carry = this.baseAdd(src2, null, carry, dst, x2size - x1size);
        this.baseAdd(null, null, carry, dst, 1L);
        if (carry == 0L) {
            resultStorage = resultStorage.subsequence(1L, size - 1L);
        }
        return resultStorage;
    }

    private void subtract(DataStorage x1, DataStorage x2) {
        long x1size = x1.getSize();
        long x2size = x2.getSize();
        assert (x1size >= x2size);
        DataStorage.Iterator src1 = x1.iterator(3, x1size, 0L);
        DataStorage.Iterator src2 = x2.iterator(1, x2size, 0L);
        DataStorage.Iterator dst = src1;
        long carry = 0L;
        carry = this.baseSubtract(src1, src2, carry, dst, x2size);
        carry = this.baseSubtract(src1, null, carry, dst, x1size - x2size);
        assert (carry == 0L);
    }

    private boolean isZero(DataStorage x, long index) {
        DataStorage.Iterator i = x.iterator(1, index, index + 1L);
        long data = i.getLong();
        i.next();
        return data == 0L;
    }
}

