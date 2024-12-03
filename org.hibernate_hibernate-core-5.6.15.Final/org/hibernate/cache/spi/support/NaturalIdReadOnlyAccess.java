/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractNaturalIdDataAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class NaturalIdReadOnlyAccess
extends AbstractNaturalIdDataAccess {
    public NaturalIdReadOnlyAccess(DomainDataRegion region, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, NaturalIdDataCachingConfig config) {
        super(region, keysFactory, storageAccess, config);
        if (config.isMutable()) {
            SecondLevelCacheLogger.INSTANCE.readOnlyCachingMutableNaturalId(config.getNavigableRole());
        }
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.READ_ONLY;
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
        this.evict(key);
    }
}

