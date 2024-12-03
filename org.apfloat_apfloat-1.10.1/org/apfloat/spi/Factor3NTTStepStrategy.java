/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface Factor3NTTStepStrategy {
    public void transformColumns(DataStorage var1, DataStorage var2, DataStorage var3, long var4, long var6, long var8, long var10, boolean var12, int var13) throws ApfloatRuntimeException;

    public long getMaxTransformLength();
}

