/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapPartitionLostListenerConfigReadOnly;
import com.hazelcast.map.listener.MapPartitionLostListener;

public class MapPartitionLostListenerConfig
extends ListenerConfig {
    private MapPartitionLostListenerConfigReadOnly readOnly;

    public MapPartitionLostListenerConfig() {
    }

    public MapPartitionLostListenerConfig(String className) {
        super(className);
    }

    public MapPartitionLostListenerConfig(MapPartitionLostListener implementation) {
        super(implementation);
    }

    public MapPartitionLostListenerConfig(MapPartitionLostListenerConfig config) {
        this.implementation = config.getImplementation();
        this.className = config.getClassName();
    }

    @Override
    public MapPartitionLostListenerConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MapPartitionLostListenerConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public MapPartitionLostListener getImplementation() {
        return (MapPartitionLostListener)this.implementation;
    }

    public MapPartitionLostListenerConfig setImplementation(MapPartitionLostListener implementation) {
        super.setImplementation(implementation);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MapPartitionLostListenerConfig that = (MapPartitionLostListenerConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        return !(this.implementation == null ? that.implementation != null : !this.implementation.equals(that.implementation));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        return result;
    }

    @Override
    public int getId() {
        return 15;
    }
}

