/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import org.hibernate.cache.internal.NaturalIdCacheKey;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public class SimpleCacheKeysFactory
implements CacheKeysFactory {
    public static final String SHORT_NAME = "simple";
    public static CacheKeysFactory INSTANCE = new SimpleCacheKeysFactory();

    @Override
    public Object createCollectionKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return id;
    }

    @Override
    public Object createEntityKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return id;
    }

    @Override
    public Object createNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return new NaturalIdCacheKey(naturalIdValues, persister.getPropertyTypes(), persister.getNaturalIdentifierProperties(), null, session);
    }

    @Override
    public Object getEntityId(Object cacheKey) {
        return cacheKey;
    }

    @Override
    public Object getCollectionId(Object cacheKey) {
        return cacheKey;
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return ((NaturalIdCacheKey)cacheKey).getNaturalIdValues();
    }
}

