/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractNTTBuilder;
import org.apfloat.internal.DoubleFactor3NTTStepStrategy;
import org.apfloat.internal.DoubleNTTConvolutionStepStrategy;
import org.apfloat.internal.DoubleNTTStepStrategy;
import org.apfloat.internal.DoubleTableFNTStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;

public class DoubleNTTBuilder
extends AbstractNTTBuilder {
    @Override
    public NTTStepStrategy createNTTSteps() {
        return new DoubleNTTStepStrategy();
    }

    @Override
    public NTTConvolutionStepStrategy createNTTConvolutionSteps() {
        return new DoubleNTTConvolutionStepStrategy();
    }

    @Override
    public Factor3NTTStepStrategy createFactor3NTTSteps() {
        return new DoubleFactor3NTTStepStrategy();
    }

    @Override
    protected NTTStrategy createSimpleFNTStrategy(long size) {
        return new DoubleTableFNTStrategy();
    }
}

