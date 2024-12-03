/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.IntCarryCRTStepStrategy;
import org.apfloat.internal.StepCarryCRTStrategy;
import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.CarryCRTStrategy;

public class IntCarryCRTBuilder
implements CarryCRTBuilder<int[]> {
    @Override
    public CarryCRTStrategy createCarryCRT(int radix) {
        return new StepCarryCRTStrategy(radix);
    }

    @Override
    public CarryCRTStepStrategy<int[]> createCarryCRTSteps(int radix) {
        return new IntCarryCRTStepStrategy(radix);
    }
}

