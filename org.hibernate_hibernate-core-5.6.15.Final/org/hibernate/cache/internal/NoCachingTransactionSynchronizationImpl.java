/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import org.hibernate.cache.spi.AbstractCacheTransactionSynchronization;
import org.hibernate.cache.spi.RegionFactory;

public class NoCachingTransactionSynchronizationImpl
extends AbstractCacheTransactionSynchronization {
    public NoCachingTransactionSynchronizationImpl(RegionFactory regionFactory) {
        super(regionFactory);
    }

    @Override
    public long getCachingTimestamp() {
        throw new UnsupportedOperationException("Method not supported when 2LC is not enabled");
    }
}

