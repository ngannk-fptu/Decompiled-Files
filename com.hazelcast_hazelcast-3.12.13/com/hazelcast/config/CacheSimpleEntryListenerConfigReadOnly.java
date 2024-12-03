/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheSimpleEntryListenerConfig;

public class CacheSimpleEntryListenerConfigReadOnly
extends CacheSimpleEntryListenerConfig {
    public CacheSimpleEntryListenerConfigReadOnly(CacheSimpleEntryListenerConfig listenerConfig) {
        super(listenerConfig);
    }

    @Override
    public void setSynchronous(boolean synchronous) {
        super.setSynchronous(synchronous);
    }

    @Override
    public void setOldValueRequired(boolean oldValueRequired) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setCacheEntryEventFilterFactory(String cacheEntryEventFilterFactory) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setCacheEntryListenerFactory(String cacheEntryListenerFactory) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

