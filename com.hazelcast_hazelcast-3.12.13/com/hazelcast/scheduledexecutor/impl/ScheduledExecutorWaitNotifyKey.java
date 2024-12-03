/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.spi.AbstractWaitNotifyKey;

public class ScheduledExecutorWaitNotifyKey
extends AbstractWaitNotifyKey {
    private final String urn;

    public ScheduledExecutorWaitNotifyKey(String objectName, String urn) {
        super("hz:impl:scheduledExecutorService", objectName);
        this.urn = urn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledExecutorWaitNotifyKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ScheduledExecutorWaitNotifyKey that = (ScheduledExecutorWaitNotifyKey)o;
        return this.urn.equals(that.urn);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.urn.hashCode();
        return result;
    }
}

