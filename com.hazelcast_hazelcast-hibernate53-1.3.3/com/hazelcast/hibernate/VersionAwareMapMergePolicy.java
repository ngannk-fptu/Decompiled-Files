/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.EntryView
 *  com.hazelcast.map.merge.MapMergePolicy
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  org.hibernate.cache.spi.entry.CacheEntry
 */
package com.hazelcast.hibernate;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import org.hibernate.cache.spi.entry.CacheEntry;

public class VersionAwareMapMergePolicy
implements MapMergePolicy {
    public Object merge(String mapName, EntryView mergingEntry, EntryView existingEntry) {
        Object existingValue = existingEntry != null ? existingEntry.getValue() : null;
        Object mergingValue = mergingEntry.getValue();
        if (existingValue != null && existingValue instanceof CacheEntry && mergingValue != null && mergingValue instanceof CacheEntry) {
            CacheEntry existingCacheEntry = (CacheEntry)existingValue;
            CacheEntry mergingCacheEntry = (CacheEntry)mergingValue;
            Object mergingVersionObject = mergingCacheEntry.getVersion();
            Object existingVersionObject = existingCacheEntry.getVersion();
            if (mergingVersionObject != null && existingVersionObject != null && mergingVersionObject instanceof Comparable && existingVersionObject instanceof Comparable) {
                Comparable mergingVersion = (Comparable)mergingVersionObject;
                Comparable existingVersion = (Comparable)existingVersionObject;
                if (mergingVersion.compareTo(existingVersion) > 0) {
                    return mergingValue;
                }
                return existingValue;
            }
        }
        return mergingValue;
    }

    public void writeData(ObjectDataOutput out) throws IOException {
    }

    public void readData(ObjectDataInput in) throws IOException {
    }
}

