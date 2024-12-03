/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractConvolutionBuilder;
import org.apfloat.internal.IntKaratsubaConvolutionStrategy;
import org.apfloat.internal.IntMediumConvolutionStrategy;
import org.apfloat.internal.IntShortConvolutionStrategy;
import org.apfloat.internal.ParallelThreeNTTConvolutionStrategy;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;

public class IntConvolutionBuilder
extends AbstractConvolutionBuilder {
    @Override
    protected int getKaratsubaCutoffPoint() {
        return 15;
    }

    @Override
    protected float getKaratsubaCostFactor() {
        return 4.8f;
    }

    @Override
    protected float getNTTCostFactor() {
        return 4.1f;
    }

    @Override
    protected ConvolutionStrategy createShortConvolutionStrategy(int radix) {
        return new IntShortConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createMediumConvolutionStrategy(int radix) {
        return new IntMediumConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix) {
        return new IntKaratsubaConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy) {
        return new ParallelThreeNTTConvolutionStrategy(radix, nttStrategy);
    }
}

