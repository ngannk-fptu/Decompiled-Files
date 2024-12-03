/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.DoubleCarryCRTStepStrategy;
import org.apfloat.internal.StepCarryCRTStrategy;
import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.CarryCRTStrategy;

public class DoubleCarryCRTBuilder
implements CarryCRTBuilder<double[]> {
    @Override
    public CarryCRTStrategy createCarryCRT(int radix) {
        return new StepCarryCRTStrategy(radix);
    }

    @Override
    public CarryCRTStepStrategy<double[]> createCarryCRTSteps(int radix) {
        return new DoubleCarryCRTStepStrategy(radix);
    }
}

