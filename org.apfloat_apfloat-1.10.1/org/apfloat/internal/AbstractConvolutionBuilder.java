/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTBuilder;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

public abstract class AbstractConvolutionBuilder
implements ConvolutionBuilder {
    private static final double LOG2_3 = Math.log(3.0) / Math.log(2.0);

    protected AbstractConvolutionBuilder() {
    }

    @Override
    public ConvolutionStrategy createConvolution(int radix, long size1, long size2, long resultSize) {
        float nttCost;
        long minSize = Math.min(size1, size2);
        long maxSize = Math.max(size1, size2);
        long totalSize = size1 + size2;
        if (minSize == 1L) {
            return this.createShortConvolutionStrategy(radix);
        }
        if (minSize <= (long)this.getKaratsubaCutoffPoint()) {
            return this.createMediumConvolutionStrategy(radix);
        }
        float mediumCost = (float)minSize * (float)maxSize;
        float karatsubaCost = this.getKaratsubaCostFactor() * (float)Math.pow(minSize, LOG2_3) * (float)maxSize / (float)minSize;
        if (mediumCost <= Math.min(karatsubaCost, nttCost = this.getNTTCostFactor() * (float)totalSize * (float)Util.log2down(totalSize))) {
            return this.createMediumConvolutionStrategy(radix);
        }
        if (karatsubaCost <= nttCost) {
            return this.createKaratsubaConvolutionStrategy(radix);
        }
        ApfloatContext ctx = ApfloatContext.getContext();
        NTTBuilder nttBuilder = ctx.getBuilderFactory().getNTTBuilder();
        NTTStrategy nttStrategy = nttBuilder.createNTT(totalSize);
        return this.createThreeNTTConvolutionStrategy(radix, nttStrategy);
    }

    protected abstract int getKaratsubaCutoffPoint();

    protected abstract float getKaratsubaCostFactor();

    protected abstract float getNTTCostFactor();

    protected abstract ConvolutionStrategy createShortConvolutionStrategy(int var1);

    protected abstract ConvolutionStrategy createMediumConvolutionStrategy(int var1);

    protected abstract ConvolutionStrategy createKaratsubaConvolutionStrategy(int var1);

    protected abstract ConvolutionStrategy createThreeNTTConvolutionStrategy(int var1, NTTStrategy var2);
}

