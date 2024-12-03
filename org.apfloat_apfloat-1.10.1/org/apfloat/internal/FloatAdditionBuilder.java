/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.FloatAdditionStrategy;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

public class FloatAdditionBuilder
implements AdditionBuilder<Float> {
    @Override
    public AdditionStrategy<Float> createAddition(int radix) {
        FloatAdditionStrategy additionStrategy = new FloatAdditionStrategy(radix);
        return additionStrategy;
    }
}

