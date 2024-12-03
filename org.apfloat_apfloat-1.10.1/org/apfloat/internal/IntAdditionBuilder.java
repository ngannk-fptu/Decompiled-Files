/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.IntAdditionStrategy;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

public class IntAdditionBuilder
implements AdditionBuilder<Integer> {
    @Override
    public AdditionStrategy<Integer> createAddition(int radix) {
        IntAdditionStrategy additionStrategy = new IntAdditionStrategy(radix);
        return additionStrategy;
    }
}

