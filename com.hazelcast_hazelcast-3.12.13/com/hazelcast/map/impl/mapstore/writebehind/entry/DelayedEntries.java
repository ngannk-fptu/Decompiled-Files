/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind.entry;

import com.hazelcast.map.impl.mapstore.writebehind.entry.AddedDelayedEntry;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DeletedDelayedEntry;
import com.hazelcast.map.impl.mapstore.writebehind.entry.NullValueDelayedEntry;

public final class DelayedEntries {
    private static final DelayedEntry EMPTY_DELAYED_ENTRY = new EmptyDelayedEntry();

    private DelayedEntries() {
    }

    public static <K, V> DelayedEntry<K, V> createDefault(K key, V value, long storeTime, int partitionId) {
        return new AddedDelayedEntry<K, V>(key, value, storeTime, partitionId);
    }

    public static <K, V> DelayedEntry<K, V> createWithoutValue(K key) {
        return new NullValueDelayedEntry(key);
    }

    public static <K, V> DelayedEntry<K, V> createWithoutValue(K key, long storeTime, int partitionId) {
        return new DeletedDelayedEntry(key, storeTime, partitionId);
    }

    public static <K, V> DelayedEntry<K, V> emptyDelayedEntry() {
        return EMPTY_DELAYED_ENTRY;
    }

    private static class EmptyDelayedEntry
    implements DelayedEntry {
        private EmptyDelayedEntry() {
        }

        public Object getKey() {
            return null;
        }

        public Object getValue() {
            return null;
        }

        @Override
        public long getStoreTime() {
            return -1L;
        }

        @Override
        public int getPartitionId() {
            return -1;
        }

        @Override
        public void setStoreTime(long storeTime) {
        }

        @Override
        public void setSequence(long sequence) {
        }

        @Override
        public long getSequence() {
            return -1L;
        }
    }
}

