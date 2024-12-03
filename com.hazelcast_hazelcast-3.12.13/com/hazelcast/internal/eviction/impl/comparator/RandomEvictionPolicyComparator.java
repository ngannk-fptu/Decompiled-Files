/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction.impl.comparator;

import com.hazelcast.internal.eviction.EvictableEntryView;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.SerializableByConvention;

@SerializableByConvention
public class RandomEvictionPolicyComparator
extends EvictionPolicyComparator {
    @Override
    public int compare(EvictableEntryView e1, EvictableEntryView e2) {
        return 0;
    }
}

