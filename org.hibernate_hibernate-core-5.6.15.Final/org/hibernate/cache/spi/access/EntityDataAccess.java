/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.access;

import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public interface EntityDataAccess
extends CachedDomainDataAccess {
    public Object generateCacheKey(Object var1, EntityPersister var2, SessionFactoryImplementor var3, String var4);

    public Object getCacheKeyId(Object var1);

    public boolean insert(SharedSessionContractImplementor var1, Object var2, Object var3, Object var4);

    public boolean afterInsert(SharedSessionContractImplementor var1, Object var2, Object var3, Object var4);

    public boolean update(SharedSessionContractImplementor var1, Object var2, Object var3, Object var4, Object var5);

    public boolean afterUpdate(SharedSessionContractImplementor var1, Object var2, Object var3, Object var4, Object var5, SoftLock var6);
}

