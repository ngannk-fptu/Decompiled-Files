/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;

public interface StaleReadDetector {
    public static final StaleReadDetector ALWAYS_FRESH = new StaleReadDetector(){

        @Override
        public boolean isStaleRead(Object key, NearCacheRecord record) {
            return false;
        }

        @Override
        public int getPartitionId(Object key) {
            return 0;
        }

        @Override
        public MetaDataContainer getMetaDataContainer(int partitionId) {
            return null;
        }

        public String toString() {
            return "ALWAYS_FRESH";
        }
    };

    public boolean isStaleRead(Object var1, NearCacheRecord var2);

    public int getPartitionId(Object var1);

    public MetaDataContainer getMetaDataContainer(int var1);
}

