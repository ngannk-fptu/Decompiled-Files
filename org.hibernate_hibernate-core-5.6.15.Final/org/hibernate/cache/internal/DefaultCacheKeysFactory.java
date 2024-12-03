/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import org.hibernate.cache.internal.CacheKeyImplementation;
import org.hibernate.cache.internal.NaturalIdCacheKey;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public class DefaultCacheKeysFactory
implements CacheKeysFactory {
    public static final String SHORT_NAME = "default";
    public static final DefaultCacheKeysFactory INSTANCE = new DefaultCacheKeysFactory();

    public static Object staticCreateCollectionKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return new CacheKeyImplementation(id, persister.getKeyType(), persister.getRole(), tenantIdentifier, factory);
    }

    public static Object staticCreateEntityKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return new CacheKeyImplementation(id, persister.getIdentifierType(), persister.getRootEntityName(), tenantIdentifier, factory);
    }

    public static Object staticCreateNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return new NaturalIdCacheKey(naturalIdValues, persister.getPropertyTypes(), persister.getNaturalIdentifierProperties(), persister.getRootEntityName(), session);
    }

    public static Object staticGetEntityId(Object cacheKey) {
        return ((CacheKeyImplementation)cacheKey).getId();
    }

    public static Object staticGetCollectionId(Object cacheKey) {
        return ((CacheKeyImplementation)cacheKey).getId();
    }

    public static Object[] staticGetNaturalIdValues(Object cacheKey) {
        return ((NaturalIdCacheKey)cacheKey).getNaturalIdValues();
    }

    @Override
    public Object createCollectionKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateCollectionKey(id, persister, factory, tenantIdentifier);
    }

    @Override
    public Object createEntityKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateEntityKey(id, persister, factory, tenantIdentifier);
    }

    @Override
    public Object createNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return DefaultCacheKeysFactory.staticCreateNaturalIdKey(naturalIdValues, persister, session);
    }

    @Override
    public Object getEntityId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetEntityId(cacheKey);
    }

    @Override
    public Object getCollectionId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetCollectionId(cacheKey);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetNaturalIdValues(cacheKey);
    }
}

