/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.CarryCRTStrategy;

public interface CarryCRTBuilder<T> {
    public CarryCRTStrategy createCarryCRT(int var1);

    public CarryCRTStepStrategy<T> createCarryCRTSteps(int var1);
}

