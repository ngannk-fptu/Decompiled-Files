/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractConvolutionBuilder;
import org.apfloat.internal.FloatKaratsubaConvolutionStrategy;
import org.apfloat.internal.FloatMediumConvolutionStrategy;
import org.apfloat.internal.FloatShortConvolutionStrategy;
import org.apfloat.internal.ParallelThreeNTTConvolutionStrategy;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;

public class FloatConvolutionBuilder
extends AbstractConvolutionBuilder {
    @Override
    protected int getKaratsubaCutoffPoint() {
        return 15;
    }

    @Override
    protected float getKaratsubaCostFactor() {
        return 6.1f;
    }

    @Override
    protected float getNTTCostFactor() {
        return 7.4f;
    }

    @Override
    protected ConvolutionStrategy createShortConvolutionStrategy(int radix) {
        return new FloatShortConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createMediumConvolutionStrategy(int radix) {
        return new FloatMediumConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix) {
        return new FloatKaratsubaConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy) {
        return new ParallelThreeNTTConvolutionStrategy(radix, nttStrategy);
    }
}

