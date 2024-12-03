/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.AbstractNTTBuilder;
import org.apfloat.internal.LongFactor3NTTStepStrategy;
import org.apfloat.internal.LongNTTConvolutionStepStrategy;
import org.apfloat.internal.LongNTTStepStrategy;
import org.apfloat.internal.LongTableFNTStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;

public class LongNTTBuilder
extends AbstractNTTBuilder {
    @Override
    public NTTStepStrategy createNTTSteps() {
        return new LongNTTStepStrategy();
    }

    @Override
    public NTTConvolutionStepStrategy createNTTConvolutionSteps() {
        return new LongNTTConvolutionStepStrategy();
    }

    @Override
    public Factor3NTTStepStrategy createFactor3NTTSteps() {
        return new LongFactor3NTTStepStrategy();
    }

    @Override
    protected NTTStrategy createSimpleFNTStrategy(long size) {
        return new LongTableFNTStrategy();
    }
}

