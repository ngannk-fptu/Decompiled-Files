/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface NTTConvolutionStepStrategy {
    public void multiplyInPlace(DataStorage var1, DataStorage var2, int var3) throws ApfloatRuntimeException;

    public void squareInPlace(DataStorage var1, int var2) throws ApfloatRuntimeException;
}

