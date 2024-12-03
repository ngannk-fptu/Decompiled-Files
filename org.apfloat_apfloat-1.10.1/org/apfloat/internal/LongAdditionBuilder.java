/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.LongAdditionStrategy;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

public class LongAdditionBuilder
implements AdditionBuilder<Long> {
    @Override
    public AdditionStrategy<Long> createAddition(int radix) {
        LongAdditionStrategy additionStrategy = new LongAdditionStrategy(radix);
        return additionStrategy;
    }
}

