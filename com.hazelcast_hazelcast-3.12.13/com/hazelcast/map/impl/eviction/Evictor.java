/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.eviction;

import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.serialization.Data;

public interface Evictor {
    public static final Evictor NULL_EVICTOR = new Evictor(){

        @Override
        public void evict(RecordStore recordStore, Data excludedKey) {
        }

        @Override
        public boolean checkEvictable(RecordStore recordStore) {
            return false;
        }

        public String toString() {
            return "Null Evictor implementation";
        }
    };
    public static final String SYSTEM_PROPERTY_SAMPLE_COUNT = "hazelcast.map.eviction.sample.count";
    public static final int DEFAULT_SAMPLE_COUNT = 15;
    public static final int SAMPLE_COUNT = Integer.getInteger("hazelcast.map.eviction.sample.count", 15);

    public void evict(RecordStore var1, Data var2);

    public boolean checkEvictable(RecordStore var1);
}

