/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.internal.nearcache.impl.invalidation.AbstractBaseNearCacheInvalidationListener;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.NotifiableEventListener;

abstract class AbstractCacheClientNearCacheInvalidationListener
extends AbstractBaseNearCacheInvalidationListener
implements CacheEventListener,
NotifiableEventListener<CacheService> {
    private final ClientEndpoint endpoint;
    private final CacheContext cacheContext;

    AbstractCacheClientNearCacheInvalidationListener(ClientEndpoint endpoint, CacheContext cacheContext, String localMemberUuid, long correlationId) {
        super(localMemberUuid, correlationId);
        this.endpoint = endpoint;
        this.cacheContext = cacheContext;
    }

    @Override
    public void handleEvent(Object eventObject) {
        if (!this.endpoint.isAlive() || !(eventObject instanceof Invalidation)) {
            return;
        }
        this.sendInvalidation((Invalidation)eventObject);
    }

    @Override
    public void onRegister(CacheService cacheService, String serviceName, String topic, EventRegistration registration) {
        this.cacheContext.increaseInvalidationListenerCount();
    }

    @Override
    public void onDeregister(CacheService cacheService, String serviceName, String topic, EventRegistration registration) {
        this.cacheContext.decreaseInvalidationListenerCount();
    }
}

