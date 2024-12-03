/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.cache.spi.AbstractCacheTransactionSynchronization;
import org.hibernate.cache.spi.RegionFactory;

public class StandardCacheTransactionSynchronization
extends AbstractCacheTransactionSynchronization {
    public StandardCacheTransactionSynchronization(RegionFactory regionFactory) {
        super(regionFactory);
    }
}

