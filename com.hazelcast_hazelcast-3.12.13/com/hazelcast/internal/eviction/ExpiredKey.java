/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.nio.serialization.Data;

public final class ExpiredKey {
    private final Data key;
    private final long creationTime;

    public ExpiredKey(Data key, long creationTime) {
        this.key = key;
        this.creationTime = creationTime;
    }

    public Data getKey() {
        return this.key;
    }

    public long getCreationTime() {
        return this.creationTime;
    }
}

