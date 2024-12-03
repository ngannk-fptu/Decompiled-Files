/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DoubleBaseMath;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.DataStorage;

public class DoubleAdditionStrategy
extends DoubleBaseMath
implements AdditionStrategy<Double> {
    private static final long serialVersionUID = 6863520700151824670L;

    public DoubleAdditionStrategy(int radix) {
        super(radix);
    }

    @Override
    public Double add(DataStorage.Iterator src1, DataStorage.Iterator src2, Double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseAdd(src1, src2, carry, dst, size);
    }

    @Override
    public Double subtract(DataStorage.Iterator src1, DataStorage.Iterator src2, Double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseSubtract(src1, src2, carry, dst, size);
    }

    @Override
    public Double multiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, Double src3, Double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseMultiplyAdd(src1, src2, src3, carry, dst, size);
    }

    @Override
    public Double divide(DataStorage.Iterator src1, Double src2, Double carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseDivide(src1, src2, carry, dst, size);
    }

    @Override
    public Double zero() {
        return 0.0;
    }
}

