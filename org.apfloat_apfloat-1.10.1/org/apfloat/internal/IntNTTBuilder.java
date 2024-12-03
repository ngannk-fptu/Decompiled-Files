/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractNTTBuilder;
import org.apfloat.internal.IntFactor3NTTStepStrategy;
import org.apfloat.internal.IntNTTConvolutionStepStrategy;
import org.apfloat.internal.IntNTTStepStrategy;
import org.apfloat.internal.IntTableFNTStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;

public class IntNTTBuilder
extends AbstractNTTBuilder {
    @Override
    public NTTStepStrategy createNTTSteps() {
        return new IntNTTStepStrategy();
    }

    @Override
    public NTTConvolutionStepStrategy createNTTConvolutionSteps() {
        return new IntNTTConvolutionStepStrategy();
    }

    @Override
    public Factor3NTTStepStrategy createFactor3NTTSteps() {
        return new IntFactor3NTTStepStrategy();
    }

    @Override
    protected NTTStrategy createSimpleFNTStrategy(long size) {
        return new IntTableFNTStrategy();
    }
}

