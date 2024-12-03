/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.AbstractDataStorageBuilder;
import org.apfloat.internal.LongDiskDataStorage;
import org.apfloat.internal.LongMemoryDataStorage;
import org.apfloat.spi.DataStorage;

public class LongDataStorageBuilder
extends AbstractDataStorageBuilder {
    @Override
    protected long getMaxCachedSize() {
        return 0x3FFFFFFF8L;
    }

    @Override
    protected DataStorage createCachedDataStorage() throws ApfloatRuntimeException {
        return new LongMemoryDataStorage();
    }

    @Override
    protected DataStorage createNonCachedDataStorage() throws ApfloatRuntimeException {
        return new LongDiskDataStorage();
    }

    @Override
    protected boolean isCached(DataStorage dataStorage) throws ApfloatRuntimeException {
        return dataStorage instanceof LongMemoryDataStorage;
    }
}

