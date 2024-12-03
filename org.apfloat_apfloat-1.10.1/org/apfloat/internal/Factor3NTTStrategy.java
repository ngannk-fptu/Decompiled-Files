/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.Parallelizable;
import org.apfloat.internal.TransformLengthExceededException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

public class Factor3NTTStrategy
implements NTTStrategy,
Parallelizable {
    protected Factor3NTTStepStrategy stepStrategy;
    private NTTStrategy factor2Strategy;

    public Factor3NTTStrategy(NTTStrategy factor2Strategy) {
        this.factor2Strategy = factor2Strategy;
        ApfloatContext ctx = ApfloatContext.getContext();
        this.stepStrategy = ctx.getBuilderFactory().getNTTBuilder().createFactor3NTTSteps();
    }

    @Override
    public void transform(DataStorage dataStorage, int modulus) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        long power2length = length & -length;
        if (length > this.stepStrategy.getMaxTransformLength()) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + length + " > " + this.stepStrategy.getMaxTransformLength());
        }
        if (length == power2length) {
            this.factor2Strategy.transform(dataStorage, modulus);
        } else {
            assert (length == 3L * power2length);
            DataStorage dataStorage0 = dataStorage.subsequence(0L, power2length);
            DataStorage dataStorage1 = dataStorage.subsequence(power2length, power2length);
            DataStorage dataStorage2 = dataStorage.subsequence(2L * power2length, power2length);
            this.stepStrategy.transformColumns(dataStorage0, dataStorage1, dataStorage2, 0L, power2length, power2length, length, false, modulus);
            this.factor2Strategy.transform(dataStorage0, modulus);
            this.factor2Strategy.transform(dataStorage1, modulus);
            this.factor2Strategy.transform(dataStorage2, modulus);
        }
    }

    @Override
    public void inverseTransform(DataStorage dataStorage, int modulus, long totalTransformLength) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        long power2length = length & -length;
        if (Math.max(length, totalTransformLength) > this.stepStrategy.getMaxTransformLength()) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + Math.max(length, totalTransformLength) + " > " + this.stepStrategy.getMaxTransformLength());
        }
        if (length == power2length) {
            this.factor2Strategy.inverseTransform(dataStorage, modulus, totalTransformLength);
        } else {
            assert (length == 3L * power2length);
            DataStorage dataStorage0 = dataStorage.subsequence(0L, power2length);
            DataStorage dataStorage1 = dataStorage.subsequence(power2length, power2length);
            DataStorage dataStorage2 = dataStorage.subsequence(2L * power2length, power2length);
            this.factor2Strategy.inverseTransform(dataStorage0, modulus, totalTransformLength);
            this.factor2Strategy.inverseTransform(dataStorage1, modulus, totalTransformLength);
            this.factor2Strategy.inverseTransform(dataStorage2, modulus, totalTransformLength);
            this.stepStrategy.transformColumns(dataStorage0, dataStorage1, dataStorage2, 0L, power2length, power2length, length, true, modulus);
        }
    }

    @Override
    public long getTransformLength(long size) {
        return Util.round23up(size);
    }
}

