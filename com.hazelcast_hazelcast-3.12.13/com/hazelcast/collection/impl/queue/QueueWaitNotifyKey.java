/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.spi.AbstractWaitNotifyKey;

public class QueueWaitNotifyKey
extends AbstractWaitNotifyKey {
    private final String type;

    public QueueWaitNotifyKey(String name, String type) {
        super("hz:impl:queueService", name);
        this.type = type;
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
        QueueWaitNotifyKey that = (QueueWaitNotifyKey)o;
        return this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }
}

