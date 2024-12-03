/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractConvolutionBuilder;
import org.apfloat.internal.LongKaratsubaConvolutionStrategy;
import org.apfloat.internal.LongMediumConvolutionStrategy;
import org.apfloat.internal.LongShortConvolutionStrategy;
import org.apfloat.internal.ParallelThreeNTTConvolutionStrategy;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;

public class LongConvolutionBuilder
extends AbstractConvolutionBuilder {
    @Override
    protected int getKaratsubaCutoffPoint() {
        return 15;
    }

    @Override
    protected float getKaratsubaCostFactor() {
        return 4.3f;
    }

    @Override
    protected float getNTTCostFactor() {
        return 8.3f;
    }

    @Override
    protected ConvolutionStrategy createShortConvolutionStrategy(int radix) {
        return new LongShortConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createMediumConvolutionStrategy(int radix) {
        return new LongMediumConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix) {
        return new LongKaratsubaConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy) {
        return new ParallelThreeNTTConvolutionStrategy(radix, nttStrategy);
    }
}

