/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.LongCarryCRTStepStrategy;
import org.apfloat.internal.StepCarryCRTStrategy;
import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.CarryCRTStrategy;

public class LongCarryCRTBuilder
implements CarryCRTBuilder<long[]> {
    @Override
    public CarryCRTStrategy createCarryCRT(int radix) {
        return new StepCarryCRTStrategy(radix);
    }

    @Override
    public CarryCRTStepStrategy<long[]> createCarryCRTSteps(int radix) {
        return new LongCarryCRTStepStrategy(radix);
    }
}

