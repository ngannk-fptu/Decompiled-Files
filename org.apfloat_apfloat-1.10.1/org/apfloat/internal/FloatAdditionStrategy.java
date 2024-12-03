/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.FloatBaseMath;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.DataStorage;

public class FloatAdditionStrategy
extends FloatBaseMath
implements AdditionStrategy<Float> {
    private static final long serialVersionUID = -8811571288007744481L;

    public FloatAdditionStrategy(int radix) {
        super(radix);
    }

    @Override
    public Float add(DataStorage.Iterator src1, DataStorage.Iterator src2, Float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return Float.valueOf(this.baseAdd(src1, src2, carry.floatValue(), dst, size));
    }

    @Override
    public Float subtract(DataStorage.Iterator src1, DataStorage.Iterator src2, Float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return Float.valueOf(this.baseSubtract(src1, src2, carry.floatValue(), dst, size));
    }

    @Override
    public Float multiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, Float src3, Float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return Float.valueOf(this.baseMultiplyAdd(src1, src2, src3.floatValue(), carry.floatValue(), dst, size));
    }

    @Override
    public Float divide(DataStorage.Iterator src1, Float src2, Float carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return Float.valueOf(this.baseDivide(src1, src2.floatValue(), carry.floatValue(), dst, size));
    }

    @Override
    public Float zero() {
        return Float.valueOf(0.0f);
    }
}

