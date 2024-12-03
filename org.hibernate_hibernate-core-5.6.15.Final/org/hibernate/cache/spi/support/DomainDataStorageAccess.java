/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface DomainDataStorageAccess
extends StorageAccess {
    default public void putFromLoad(Object key, Object value, SharedSessionContractImplementor session) {
        this.putIntoCache(key, value, session);
    }
}

