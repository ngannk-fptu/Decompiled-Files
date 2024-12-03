/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;

public final class LockWaitNotifyKey
implements WaitNotifyKey {
    private final ObjectNamespace namespace;
    private final Data key;

    public LockWaitNotifyKey(ObjectNamespace namespace, Data key) {
        this.namespace = namespace;
        this.key = key;
    }

    @Override
    public String getServiceName() {
        return this.namespace.getServiceName();
    }

    @Override
    public String getObjectName() {
        return this.namespace.getObjectName();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LockWaitNotifyKey that = (LockWaitNotifyKey)o;
        if (!this.key.equals(that.key)) {
            return false;
        }
        return this.namespace.equals(that.namespace);
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.key.hashCode();
        return result;
    }

    public String toString() {
        return "LockWaitNotifyKey{namespace=" + this.namespace + ", key=" + this.key + '}';
    }
}

