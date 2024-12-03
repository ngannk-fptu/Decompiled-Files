/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongBaseMath;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.DataStorage;

public class LongAdditionStrategy
extends LongBaseMath
implements AdditionStrategy<Long> {
    private static final long serialVersionUID = 4128390142053847289L;

    public LongAdditionStrategy(int radix) {
        super(radix);
    }

    @Override
    public Long add(DataStorage.Iterator src1, DataStorage.Iterator src2, Long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseAdd(src1, src2, carry, dst, size);
    }

    @Override
    public Long subtract(DataStorage.Iterator src1, DataStorage.Iterator src2, Long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseSubtract(src1, src2, carry, dst, size);
    }

    @Override
    public Long multiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, Long src3, Long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseMultiplyAdd(src1, src2, src3, carry, dst, size);
    }

    @Override
    public Long divide(DataStorage.Iterator src1, Long src2, Long carry, DataStorage.Iterator dst, long size) throws ApfloatRuntimeException {
        return this.baseDivide(src1, src2, carry, dst, size);
    }

    @Override
    public Long zero() {
        return 0L;
    }
}

