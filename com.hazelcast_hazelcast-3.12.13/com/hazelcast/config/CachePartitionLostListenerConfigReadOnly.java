/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.util.EventListener;

@BinaryInterface
public class CachePartitionLostListenerConfigReadOnly
extends CachePartitionLostListenerConfig {
    public CachePartitionLostListenerConfigReadOnly(CachePartitionLostListenerConfig config) {
        super(config);
    }

    @Override
    public CachePartitionLostListener getImplementation() {
        return (CachePartitionLostListener)this.implementation;
    }

    @Override
    public ListenerConfig setClassName(String className) {
        throw new UnsupportedOperationException("this config is read-only");
    }

    @Override
    public ListenerConfig setImplementation(EventListener implementation) {
        throw new UnsupportedOperationException("this config is read-only");
    }

    @Override
    public CachePartitionLostListenerConfig setImplementation(CachePartitionLostListener implementation) {
        throw new UnsupportedOperationException("this config is read-only");
    }
}

