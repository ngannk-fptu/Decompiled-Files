/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.IntBaseMath;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.DataStorage;

public class IntAdditionStrategy
extends IntBaseMath
implements AdditionStrategy<Integer> {
    private static final long serialVersionUID = -6156689494629604331L;

    public IntAdditionStrategy(int radix) {
        super(radix);
    }

    @Override
    public Integer add(DataStorage.Iterator src1, DataStorage.Iterator src2, Integer carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseAdd(src1, src2, carry, dst, size);
    }

    @Override
    public Integer subtract(DataStorage.Iterator src1, DataStorage.Iterator src2, Integer carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseSubtract(src1, src2, carry, dst, size);
    }

    @Override
    public Integer multiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, Integer src3, Integer carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseMultiplyAdd(src1, src2, src3, carry, dst, size);
    }

    @Override
    public Integer divide(DataStorage.Iterator src1, Integer src2, Integer carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseDivide(src1, src2, carry, dst, size);
    }

    @Override
    public Integer zero() {
        return 0;
    }
}

