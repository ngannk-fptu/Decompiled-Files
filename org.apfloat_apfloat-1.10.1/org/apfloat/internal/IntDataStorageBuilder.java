/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.AbstractDataStorageBuilder;
import org.apfloat.internal.IntDiskDataStorage;
import org.apfloat.internal.IntMemoryDataStorage;
import org.apfloat.spi.DataStorage;

public class IntDataStorageBuilder
extends AbstractDataStorageBuilder {
    @Override
    protected long getMaxCachedSize() {
        return 0x1FFFFFFFCL;
    }

    @Override
    protected DataStorage createCachedDataStorage() throws ApfloatRuntimeException {
        return new IntMemoryDataStorage();
    }

    @Override
    protected DataStorage createNonCachedDataStorage() throws ApfloatRuntimeException {
        return new IntDiskDataStorage();
    }

    @Override
    protected boolean isCached(DataStorage dataStorage) throws ApfloatRuntimeException {
        return dataStorage instanceof IntMemoryDataStorage;
    }
}

