/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.map.listener.MapPartitionLostListener;
import java.util.EventListener;

public class MapPartitionLostListenerConfigReadOnly
extends MapPartitionLostListenerConfig {
    public MapPartitionLostListenerConfigReadOnly(MapPartitionLostListenerConfig config) {
        super(config);
    }

    @Override
    public MapPartitionLostListener getImplementation() {
        return (MapPartitionLostListener)this.implementation;
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
    public MapPartitionLostListenerConfig setImplementation(MapPartitionLostListener implementation) {
        throw new UnsupportedOperationException("this config is read-only");
    }
}

