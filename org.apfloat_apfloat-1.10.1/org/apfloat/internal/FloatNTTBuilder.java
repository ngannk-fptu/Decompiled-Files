/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractNTTBuilder;
import org.apfloat.internal.FloatFactor3NTTStepStrategy;
import org.apfloat.internal.FloatNTTConvolutionStepStrategy;
import org.apfloat.internal.FloatNTTStepStrategy;
import org.apfloat.internal.FloatTableFNTStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;

public class FloatNTTBuilder
extends AbstractNTTBuilder {
    @Override
    public NTTStepStrategy createNTTSteps() {
        return new FloatNTTStepStrategy();
    }

    @Override
    public NTTConvolutionStepStrategy createNTTConvolutionSteps() {
        return new FloatNTTConvolutionStepStrategy();
    }

    @Override
    public Factor3NTTStepStrategy createFactor3NTTSteps() {
        return new FloatFactor3NTTStepStrategy();
    }

    @Override
    protected NTTStrategy createSimpleFNTStrategy(long size) {
        return new FloatTableFNTStrategy();
    }
}

