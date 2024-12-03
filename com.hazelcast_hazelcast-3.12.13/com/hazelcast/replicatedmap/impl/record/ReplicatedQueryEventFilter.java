/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.replicatedmap.impl.record.ReplicatedEntryEventFilter;
import java.util.Map;

public class ReplicatedQueryEventFilter
extends ReplicatedEntryEventFilter {
    private Predicate predicate;

    public ReplicatedQueryEventFilter(Data key, Predicate predicate) {
        super(key);
        this.predicate = predicate;
    }

    @Override
    public boolean eval(Object arg) {
        QueryableEntry entry = (QueryableEntry)arg;
        Data keyData = entry.getKeyData();
        return (this.key == null || this.key.equals(keyData)) && this.predicate.apply((Map.Entry)arg);
    }
}

