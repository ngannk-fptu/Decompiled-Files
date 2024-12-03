/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.DoubleAdditionStrategy;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

public class DoubleAdditionBuilder
implements AdditionBuilder<Double> {
    @Override
    public AdditionStrategy<Double> createAddition(int radix) {
        DoubleAdditionStrategy additionStrategy = new DoubleAdditionStrategy(radix);
        return additionStrategy;
    }
}

