/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.util.Preconditions;

public class RingbufferWaitNotifyKey
implements WaitNotifyKey {
    private final ObjectNamespace namespace;
    private final int partitionId;

    public RingbufferWaitNotifyKey(ObjectNamespace namespace, int partitionId) {
        Preconditions.checkNotNull(namespace);
        this.namespace = namespace;
        this.partitionId = partitionId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RingbufferWaitNotifyKey that = (RingbufferWaitNotifyKey)o;
        return this.partitionId == that.partitionId && this.namespace.equals(that.namespace);
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.partitionId;
        return result;
    }

    public String toString() {
        return "RingbufferWaitNotifyKey{namespace=" + this.namespace + ", partitionId=" + this.partitionId + '}';
    }

    @Override
    public String getServiceName() {
        return this.namespace.getServiceName();
    }

    @Override
    public String getObjectName() {
        return this.namespace.getObjectName();
    }
}

