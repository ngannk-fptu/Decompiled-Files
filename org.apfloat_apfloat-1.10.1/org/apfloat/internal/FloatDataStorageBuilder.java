/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.AbstractDataStorageBuilder;
import org.apfloat.internal.FloatDiskDataStorage;
import org.apfloat.internal.FloatMemoryDataStorage;
import org.apfloat.spi.DataStorage;

public class FloatDataStorageBuilder
extends AbstractDataStorageBuilder {
    @Override
    protected long getMaxCachedSize() {
        return 0x1FFFFFFFCL;
    }

    @Override
    protected DataStorage createCachedDataStorage() throws ApfloatRuntimeException {
        return new FloatMemoryDataStorage();
    }

    @Override
    protected DataStorage createNonCachedDataStorage() throws ApfloatRuntimeException {
        return new FloatDiskDataStorage();
    }

    @Override
    protected boolean isCached(DataStorage dataStorage) throws ApfloatRuntimeException {
        return dataStorage instanceof FloatMemoryDataStorage;
    }
}

