/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.record;

import com.hazelcast.internal.nearcache.impl.record.AbstractNearCacheRecord;

public class NearCacheObjectRecord<V>
extends AbstractNearCacheRecord<V> {
    public NearCacheObjectRecord(V value, long creationTime, long expiryTime) {
        super(value, creationTime, expiryTime);
        this.value = value;
    }

    @Override
    public String toString() {
        return "NearCacheObjectRecord{" + super.toString() + '}';
    }
}

