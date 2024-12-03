/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.support.AbstractCollectionDataAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;

public class CollectionTransactionAccess
extends AbstractCollectionDataAccess {
    public CollectionTransactionAccess(DomainDataRegion region, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, CollectionDataCachingConfig config) {
        super(region, keysFactory, storageAccess, config);
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.TRANSACTIONAL;
    }
}

