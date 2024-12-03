/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface CarryCRTStrategy {
    public DataStorage carryCRT(DataStorage var1, DataStorage var2, DataStorage var3, long var4) throws ApfloatRuntimeException;
}

