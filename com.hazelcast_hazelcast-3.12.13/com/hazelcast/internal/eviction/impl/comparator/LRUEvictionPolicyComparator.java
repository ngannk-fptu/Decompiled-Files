/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction.impl.comparator;

import com.hazelcast.internal.eviction.EvictableEntryView;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.SerializableByConvention;

@SerializableByConvention
public class LRUEvictionPolicyComparator
extends EvictionPolicyComparator {
    @Override
    public int compare(EvictableEntryView e1, EvictableEntryView e2) {
        long accessTime1 = e1.getLastAccessTime();
        long accessTime2 = e2.getLastAccessTime();
        if (accessTime2 < accessTime1) {
            return 1;
        }
        if (accessTime1 < accessTime2) {
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

