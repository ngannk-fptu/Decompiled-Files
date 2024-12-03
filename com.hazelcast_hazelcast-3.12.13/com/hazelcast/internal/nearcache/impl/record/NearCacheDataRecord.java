/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.record;

import com.hazelcast.internal.nearcache.impl.record.AbstractNearCacheRecord;
import com.hazelcast.nio.serialization.Data;

public class NearCacheDataRecord
extends AbstractNearCacheRecord<Data> {
    public NearCacheDataRecord(Data value, long creationTime, long expiryTime) {
        super(value, creationTime, expiryTime);
        this.value = value;
    }

    @Override
    public String toString() {
        return "NearCacheDataRecord{" + super.toString() + '}';
    }
}

