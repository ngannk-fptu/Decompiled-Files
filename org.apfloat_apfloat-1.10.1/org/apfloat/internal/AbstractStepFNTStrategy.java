/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.Parallelizable;
import org.apfloat.internal.TransformLengthExceededException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

public abstract class AbstractStepFNTStrategy
implements NTTStrategy,
Parallelizable {
    protected NTTStepStrategy stepStrategy;

    protected AbstractStepFNTStrategy() {
        ApfloatContext ctx = ApfloatContext.getContext();
        this.stepStrategy = ctx.getBuilderFactory().getNTTBuilder().createNTTSteps();
    }

    @Override
    public void transform(DataStorage dataStorage, int modulus) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        if (length > this.stepStrategy.getMaxTransformLength()) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + length + " > " + this.stepStrategy.getMaxTransformLength());
        }
        if (length < 2L) {
            return;
        }
        assert (length == (length & -length));
        int logLength = Util.log2down(length);
        int n1 = logLength >> 1;
        int n2 = logLength - n1;
        n1 = 1 << n1;
        n2 = 1 << n2;
        this.transform(dataStorage, n1, n2, length, modulus);
    }

    @Override
    public void inverseTransform(DataStorage dataStorage, int modulus, long totalTransformLength) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        if (Math.max(length, totalTransformLength) > this.stepStrategy.getMaxTransformLength()) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + Math.max(length, totalTransformLength) + " > " + this.stepStrategy.getMaxTransformLength());
        }
        if (length < 2L) {
            return;
        }
        assert (length == (length & -length));
        int logLength = Util.log2down(length);
        int n1 = logLength >> 1;
        int n2 = logLength - n1;
        n1 = 1 << n1;
        n2 = 1 << n2;
        this.inverseTransform(dataStorage, n1, n2, length, totalTransformLength, modulus);
    }

    @Override
    public long getTransformLength(long size) {
        return Util.round2up(size);
    }

    protected abstract void transform(DataStorage var1, int var2, int var3, long var4, int var6) throws ApfloatRuntimeException;

    protected abstract void inverseTransform(DataStorage var1, int var2, int var3, long var4, long var6, int var8) throws ApfloatRuntimeException;
}

