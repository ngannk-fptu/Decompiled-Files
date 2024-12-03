/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.internal.Factor3NTTStrategy;
import org.apfloat.internal.SixStepFNTStrategy;
import org.apfloat.internal.TwoPassFNTStrategy;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.NTTBuilder;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

public abstract class AbstractNTTBuilder
implements NTTBuilder {
    protected AbstractNTTBuilder() {
    }

    @Override
    public NTTStrategy createNTT(long size) {
        long power2size;
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        int cacheSize = ctx.getCacheL1Size() / builderFactory.getElementSize();
        long maxMemoryBlockSize = ctx.getMaxMemoryBlockSize() / (long)builderFactory.getElementSize();
        boolean useFactor3 = false;
        if ((size = Util.round23up(size)) != (power2size = size & -size)) {
            useFactor3 = true;
        }
        NTTStrategy nttStrategy = power2size <= (long)(cacheSize / 2) ? this.createSimpleFNTStrategy(power2size) : (power2size <= maxMemoryBlockSize && power2size <= Integer.MAX_VALUE ? this.createSixStepFNTStrategy(power2size) : this.createTwoPassFNTStrategy(power2size));
        if (useFactor3) {
            nttStrategy = this.createFactor3NTTStrategy(size, nttStrategy);
        }
        return nttStrategy;
    }

    protected abstract NTTStrategy createSimpleFNTStrategy(long var1);

    protected NTTStrategy createSixStepFNTStrategy(long size) {
        return new SixStepFNTStrategy();
    }

    protected NTTStrategy createTwoPassFNTStrategy(long size) {
        return new TwoPassFNTStrategy();
    }

    protected NTTStrategy createFactor3NTTStrategy(long size, NTTStrategy nttStrategy) {
        return new Factor3NTTStrategy(nttStrategy);
    }
}

