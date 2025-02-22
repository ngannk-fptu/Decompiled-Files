/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.internal.nearcache.impl.invalidation.AbstractBaseNearCacheInvalidationListener;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.map.impl.nearcache.invalidation.InvalidationListener;

abstract class AbstractMapClientNearCacheInvalidationListener
extends AbstractBaseNearCacheInvalidationListener
implements InvalidationListener,
ListenerMessageTask {
    private final ClientEndpoint endpoint;

    AbstractMapClientNearCacheInvalidationListener(ClientEndpoint endpoint, String localMemberUuid, long correlationId) {
        super(localMemberUuid, correlationId);
        this.endpoint = endpoint;
    }

    @Override
    public void onInvalidate(Invalidation invalidation) {
        if (!this.endpoint.isAlive()) {
            return;
        }
        this.sendInvalidation(invalidation);
    }
}

