/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction.impl.comparator;

import com.hazelcast.internal.eviction.EvictableEntryView;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.SerializableByConvention;

@SerializableByConvention
public class LFUEvictionPolicyComparator
extends EvictionPolicyComparator {
    @Override
    public int compare(EvictableEntryView e1, EvictableEntryView e2) {
        long hits1 = e1.getAccessHit();
        long hits2 = e2.getAccessHit();
        if (hits2 < hits1) {
            return 1;
        }
        if (hits1 < hits2) {
            return -1;
        }
        long creationTime1 = e1.getCreationTime();
        long creationTime2 = e2.getCreationTime();
        if (creationTime2 < creationTime1) {
            return 1;
        }
        if (creationTime2 > creationTime1) {
            return -1;
        }
        return 0;
    }
}

