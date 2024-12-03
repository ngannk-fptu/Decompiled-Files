/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore;

import com.hazelcast.spi.AbstractWaitNotifyKey;
import com.hazelcast.util.Preconditions;

public class SemaphoreWaitNotifyKey
extends AbstractWaitNotifyKey {
    private final String type;

    public SemaphoreWaitNotifyKey(String name, String type) {
        super("hz:impl:semaphoreService", name);
        this.type = Preconditions.isNotNull(type, "type");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SemaphoreWaitNotifyKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SemaphoreWaitNotifyKey that = (SemaphoreWaitNotifyKey)o;
        return this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }
}

