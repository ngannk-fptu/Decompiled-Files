/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractConvolutionBuilder;
import org.apfloat.internal.DoubleKaratsubaConvolutionStrategy;
import org.apfloat.internal.DoubleMediumConvolutionStrategy;
import org.apfloat.internal.DoubleShortConvolutionStrategy;
import org.apfloat.internal.ParallelThreeNTTConvolutionStrategy;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;

public class DoubleConvolutionBuilder
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
        return 6.2f;
    }

    @Override
    protected ConvolutionStrategy createShortConvolutionStrategy(int radix) {
        return new DoubleShortConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createMediumConvolutionStrategy(int radix) {
        return new DoubleMediumConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix) {
        return new DoubleKaratsubaConvolutionStrategy(radix);
    }

    @Override
    protected ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy) {
        return new ParallelThreeNTTConvolutionStrategy(radix, nttStrategy);
    }
}

