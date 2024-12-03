/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.internal.FloatBaseMath;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;

public class FloatMediumConvolutionStrategy
extends FloatBaseMath
implements ConvolutionStrategy {
    private static final long serialVersionUID = -6697305140738370764L;

    public FloatMediumConvolutionStrategy(int radix) {
        super(radix);
    }

    @Override
    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize) throws ApfloatRuntimeException {
        DataStorage longStorage;
        DataStorage shortStorage;
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
        if (shortSize > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Too long shorter number, size = " + shortSize);
        }
        final int bufferSize = (int)shortSize;
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 4L);
        resultStorage.setSize(size);
        DataStorage.Iterator src = longStorage.iterator(1, longSize, 0L);
        DataStorage.Iterator dst = resultStorage.iterator(2, size, 0L);
        DataStorage.Iterator tmpDst = new DataStorage.Iterator(){
            private static final long serialVersionUID = 1L;
            private float[] buffer;
            private int position;
            {
                this.buffer = new float[bufferSize];
                this.position = 0;
            }

            @Override
            public void next() {
                ++this.position;
                this.position = this.position == bufferSize ? 0 : this.position;
            }

            @Override
            public float getFloat() {
                return this.buffer[this.position];
            }

            @Override
            public void setFloat(float value) {
                this.buffer[this.position] = value;
            }
        };
        for (long i = 0L; i < longSize; ++i) {
            DataStorage.Iterator tmpSrc = shortStorage.iterator(1, shortSize, 0L);
            float factor = src.getFloat();
            float carry = this.baseMultiplyAdd(tmpSrc, tmpDst, factor, 0.0f, tmpDst, shortSize);
            float result = tmpDst.getFloat();
            dst.setFloat(result);
            tmpDst.setFloat(carry);
            tmpDst.next();
            src.next();
            dst.next();
        }
        for (int i = 0; i < bufferSize; ++i) {
            float result = tmpDst.getFloat();
            dst.setFloat(result);
            tmpDst.next();
            dst.next();
        }
        return resultStorage;
    }
}

