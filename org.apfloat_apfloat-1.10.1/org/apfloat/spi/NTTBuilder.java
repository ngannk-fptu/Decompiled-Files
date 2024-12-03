/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.NTTStrategy;

public interface NTTBuilder {
    public NTTStrategy createNTT(long var1);

    public NTTStepStrategy createNTTSteps();

    public NTTConvolutionStepStrategy createNTTConvolutionSteps();

    public Factor3NTTStepStrategy createFactor3NTTSteps();
}

