/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.config.CachePartitionLostListenerConfigReadOnly;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@BinaryInterface
public class CachePartitionLostListenerConfig
extends ListenerConfig
implements Serializable {
    private CachePartitionLostListenerConfigReadOnly readOnly;

    public CachePartitionLostListenerConfig() {
    }

    public CachePartitionLostListenerConfig(String className) {
        super(className);
    }

    public CachePartitionLostListenerConfig(CachePartitionLostListener implementation) {
        super(implementation);
    }

    public CachePartitionLostListenerConfig(CachePartitionLostListenerConfig config) {
        this.implementation = config.getImplementation();
        this.className = config.getClassName();
    }

    @Override
    public CachePartitionLostListenerConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new CachePartitionLostListenerConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public CachePartitionLostListener getImplementation() {
        return (CachePartitionLostListener)this.implementation;
    }

    public CachePartitionLostListenerConfig setImplementation(CachePartitionLostListener implementation) {
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
        CachePartitionLostListenerConfig that = (CachePartitionLostListenerConfig)o;
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
        return 46;
    }
}

