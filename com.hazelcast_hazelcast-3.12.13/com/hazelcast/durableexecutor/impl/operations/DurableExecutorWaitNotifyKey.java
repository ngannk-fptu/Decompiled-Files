/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.spi.AbstractWaitNotifyKey;

public class DurableExecutorWaitNotifyKey
extends AbstractWaitNotifyKey {
    private final long uniqueId;

    DurableExecutorWaitNotifyKey(String objectName, long uniqueId) {
        super("hz:impl:durableExecutorService", objectName);
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DurableExecutorWaitNotifyKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DurableExecutorWaitNotifyKey that = (DurableExecutorWaitNotifyKey)o;
        return this.uniqueId == that.uniqueId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int)(this.uniqueId ^ this.uniqueId >>> 32);
        return result;
    }
}

