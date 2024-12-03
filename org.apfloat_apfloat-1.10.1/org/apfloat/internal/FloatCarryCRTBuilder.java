/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.FloatCarryCRTStepStrategy;
import org.apfloat.internal.StepCarryCRTStrategy;
import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.CarryCRTStrategy;

public class FloatCarryCRTBuilder
implements CarryCRTBuilder<float[]> {
    @Override
    public CarryCRTStrategy createCarryCRT(int radix) {
        return new StepCarryCRTStrategy(radix);
    }

    @Override
    public CarryCRTStepStrategy<float[]> createCarryCRTSteps(int radix) {
        return new FloatCarryCRTStepStrategy(radix);
    }
}

