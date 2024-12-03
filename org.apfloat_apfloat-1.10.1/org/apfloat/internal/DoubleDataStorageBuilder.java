/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.AbstractDataStorageBuilder;
import org.apfloat.internal.DoubleDiskDataStorage;
import org.apfloat.internal.DoubleMemoryDataStorage;
import org.apfloat.spi.DataStorage;

public class DoubleDataStorageBuilder
extends AbstractDataStorageBuilder {
    @Override
    protected long getMaxCachedSize() {
        return 0x3FFFFFFF8L;
    }

    @Override
    protected DataStorage createCachedDataStorage() throws ApfloatRuntimeException {
        return new DoubleMemoryDataStorage();
    }

    @Override
    protected DataStorage createNonCachedDataStorage() throws ApfloatRuntimeException {
        return new DoubleDiskDataStorage();
    }

    @Override
    protected boolean isCached(DataStorage dataStorage) throws ApfloatRuntimeException {
        return dataStorage instanceof DoubleMemoryDataStorage;
    }
}

