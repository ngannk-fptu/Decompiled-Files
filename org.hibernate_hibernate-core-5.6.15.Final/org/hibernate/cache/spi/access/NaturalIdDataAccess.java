/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.access;

import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public interface NaturalIdDataAccess
extends CachedDomainDataAccess {
    public Object generateCacheKey(Object[] var1, EntityPersister var2, SharedSessionContractImplementor var3);

    public Object[] getNaturalIdValues(Object var1);

    public boolean insert(SharedSessionContractImplementor var1, Object var2, Object var3);

    public boolean afterInsert(SharedSessionContractImplementor var1, Object var2, Object var3);

    public boolean update(SharedSessionContractImplementor var1, Object var2, Object var3);

    public boolean afterUpdate(SharedSessionContractImplementor var1, Object var2, Object var3, SoftLock var4);
}

