/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface CarryCRTStepStrategy<T> {
    public T crt(DataStorage var1, DataStorage var2, DataStorage var3, DataStorage var4, long var5, long var7, long var9, long var11) throws ApfloatRuntimeException;

    public T carry(DataStorage var1, long var2, long var4, long var6, long var8, T var10, T var11) throws ApfloatRuntimeException;
}

