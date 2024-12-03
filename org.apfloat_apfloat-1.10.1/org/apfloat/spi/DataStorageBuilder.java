/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface DataStorageBuilder {
    public DataStorage createDataStorage(long var1) throws ApfloatRuntimeException;

    public DataStorage createCachedDataStorage(long var1) throws ApfloatRuntimeException;

    public DataStorage createDataStorage(DataStorage var1) throws ApfloatRuntimeException;
}

