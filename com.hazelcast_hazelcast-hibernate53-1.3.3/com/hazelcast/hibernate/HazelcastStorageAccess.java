/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.spi.access.SoftLock
 *  org.hibernate.cache.spi.support.DomainDataStorageAccess
 */
package com.hazelcast.hibernate;

import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;

public interface HazelcastStorageAccess
extends DomainDataStorageAccess {
    public void afterUpdate(Object var1, Object var2, Object var3);

    public void unlockItem(Object var1, SoftLock var2);
}

