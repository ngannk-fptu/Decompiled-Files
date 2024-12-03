/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.access;

import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface CachedDomainDataAccess {
    public DomainDataRegion getRegion();

    public AccessType getAccessType();

    public Object get(SharedSessionContractImplementor var1, Object var2);

    public boolean putFromLoad(SharedSessionContractImplementor var1, Object var2, Object var3, Object var4);

    public boolean putFromLoad(SharedSessionContractImplementor var1, Object var2, Object var3, Object var4, boolean var5);

    public SoftLock lockItem(SharedSessionContractImplementor var1, Object var2, Object var3);

    public void unlockItem(SharedSessionContractImplementor var1, Object var2, SoftLock var3);

    public void remove(SharedSessionContractImplementor var1, Object var2);

    public void removeAll(SharedSessionContractImplementor var1);

    public boolean contains(Object var1);

    public SoftLock lockRegion();

    public void unlockRegion(SoftLock var1);

    public void evict(Object var1);

    public void evictAll();
}

