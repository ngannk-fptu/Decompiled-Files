/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.spi.AdditionStrategy;

public interface AdditionBuilder<T> {
    public AdditionStrategy<T> createAddition(int var1);
}

