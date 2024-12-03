/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.support.AbstractNaturalIdDataAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;

public class NaturalIdTransactionalAccess
extends AbstractNaturalIdDataAccess {
    public NaturalIdTransactionalAccess(DomainDataRegion region, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, NaturalIdDataCachingConfig config) {
        super(region, keysFactory, storageAccess, config);
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.TRANSACTIONAL;
    }
}

