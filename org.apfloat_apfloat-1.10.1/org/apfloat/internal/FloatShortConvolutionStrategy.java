/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.FloatBaseMath;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;

public class FloatShortConvolutionStrategy
extends FloatBaseMath
implements ConvolutionStrategy {
    private static final long serialVersionUID = 3839614758362699756L;

    public FloatShortConvolutionStrategy(int radix) {
        super(radix);
    }

    @Override
    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize) throws ApfloatRuntimeException {
        float factor;
        DataStorage longStorage;
        DataStorage shortStorage;
        if (x.getSize() > 1L) {
            shortStorage = y;
            longStorage = x;
        } else {
            shortStorage = x;
            longStorage = y;
        }
        assert (shortStorage.getSize() == 1L);
        long size = longStorage.getSize() + 1L;
        try (ArrayAccess arrayAccess = shortStorage.getArray(1, 0L, 1);){
            factor = arrayAccess.getFloatData()[arrayAccess.getOffset()];
        }
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 4L);
        resultStorage.setSize(size);
        DataStorage.Iterator src = longStorage.iterator(1, size - 1L, 0L);
        try (DataStorage.Iterator dst = resultStorage.iterator(2, size, 0L);){
            float carry = this.baseMultiplyAdd(src, null, factor, 0.0f, dst, size - 1L);
            dst.setFloat(carry);
        }
        return resultStorage;
    }
}

