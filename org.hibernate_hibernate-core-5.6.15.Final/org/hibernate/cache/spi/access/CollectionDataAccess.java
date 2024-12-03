/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.access;

import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public interface CollectionDataAccess
extends CachedDomainDataAccess {
    public Object generateCacheKey(Object var1, CollectionPersister var2, SessionFactoryImplementor var3, String var4);

    public Object getCacheKeyId(Object var1);
}

