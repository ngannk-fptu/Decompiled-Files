/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface ConvolutionStrategy {
    public DataStorage convolute(DataStorage var1, DataStorage var2, long var3) throws ApfloatRuntimeException;
}

