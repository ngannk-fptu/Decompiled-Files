/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;

public class ReplicatedEntryEventFilter
implements EventFilter {
    protected Data key;

    public ReplicatedEntryEventFilter(Data key) {
        this.key = key;
    }

    @Override
    public boolean eval(Object arg) {
        return this.key == null || this.key.equals(arg);
    }
}

