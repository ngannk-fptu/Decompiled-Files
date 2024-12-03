/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public final class CacheHelper {
    private CacheHelper() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Serializable fromSharedCache(SharedSessionContractImplementor session, Object cacheKey, CachedDomainDataAccess cacheAccess) {
        SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
        Serializable cachedValue = null;
        eventListenerManager.cacheGetStart();
        try {
            cachedValue = (Serializable)cacheAccess.get(session, cacheKey);
            eventListenerManager.cacheGetEnd(cachedValue != null);
        }
        catch (Throwable throwable) {
            eventListenerManager.cacheGetEnd(cachedValue != null);
            throw throwable;
        }
        return cachedValue;
    }
}

