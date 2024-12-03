/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;

public abstract class AbstractDataStorageBuilder
implements DataStorageBuilder {
    protected AbstractDataStorageBuilder() {
    }

    @Override
    public DataStorage createDataStorage(long size) throws ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        if (size <= ctx.getMemoryThreshold() && size <= this.getMaxCachedSize()) {
            return this.createCachedDataStorage();
        }
        return this.createNonCachedDataStorage();
    }

    @Override
    public DataStorage createCachedDataStorage(long size) throws ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        if (size <= ctx.getMaxMemoryBlockSize() && size <= this.getMaxCachedSize()) {
            return this.createCachedDataStorage();
        }
        return this.createNonCachedDataStorage();
    }

    @Override
    public DataStorage createDataStorage(DataStorage dataStorage) throws ApfloatRuntimeException {
        ApfloatContext ctx;
        long size;
        if (this.isCached(dataStorage) && (size = dataStorage.getSize()) > (ctx = ApfloatContext.getContext()).getMemoryThreshold()) {
            DataStorage tmp = this.createNonCachedDataStorage();
            tmp.copyFrom(dataStorage);
            dataStorage = tmp;
        }
        return dataStorage;
    }

    protected abstract long getMaxCachedSize();

    protected abstract DataStorage createCachedDataStorage() throws ApfloatRuntimeException;

    protected abstract DataStorage createNonCachedDataStorage() throws ApfloatRuntimeException;

    protected abstract boolean isCached(DataStorage var1) throws ApfloatRuntimeException;
}

