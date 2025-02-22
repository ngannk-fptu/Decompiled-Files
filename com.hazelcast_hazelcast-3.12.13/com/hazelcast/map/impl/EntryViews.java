/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.LazyEntryView;
import com.hazelcast.map.impl.NullEntryView;
import com.hazelcast.map.impl.SimpleEntryView;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public final class EntryViews {
    private EntryViews() {
    }

    public static <K, V> EntryView<K, V> createNullEntryView(K key) {
        return new NullEntryView(key);
    }

    public static <K, V> EntryView<K, V> createSimpleEntryView() {
        return new SimpleEntryView();
    }

    public static <K, V> EntryView<K, V> createSimpleEntryView(K key, V value, Record record) {
        return new SimpleEntryView<K, V>(key, value).withCost(record.getCost()).withVersion(record.getVersion()).withHits(record.getHits()).withLastAccessTime(record.getLastAccessTime()).withLastUpdateTime(record.getLastUpdateTime()).withTtl(record.getTtl()).withMaxIdle(record.getMaxIdle()).withCreationTime(record.getCreationTime()).withExpirationTime(record.getExpirationTime()).withLastStoredTime(record.getLastStoredTime());
    }

    public static EntryView<Data, Data> toSimpleEntryView(Record<Data> record) {
        return new SimpleEntryView<Data, Data>(record.getKey(), record.getValue()).withCost(record.getCost()).withVersion(record.getVersion()).withHits(record.getHits()).withLastAccessTime(record.getLastAccessTime()).withLastUpdateTime(record.getLastUpdateTime()).withTtl(record.getTtl()).withMaxIdle(record.getMaxIdle()).withCreationTime(record.getCreationTime()).withExpirationTime(record.getExpirationTime()).withLastStoredTime(record.getLastStoredTime());
    }

    public static <K, V> EntryView<K, V> createLazyEntryView(K key, V value, Record record, SerializationService serializationService, MapMergePolicy mergePolicy) {
        return new LazyEntryView<K, V>(key, value, serializationService, mergePolicy).setCost(record.getCost()).setVersion(record.getVersion()).setHits(record.getHits()).setLastAccessTime(record.getLastAccessTime()).setLastUpdateTime(record.getLastUpdateTime()).setTtl(record.getTtl()).setMaxIdle(record.getMaxIdle()).setCreationTime(record.getCreationTime()).setExpirationTime(record.getExpirationTime()).setLastStoredTime(record.getLastStoredTime());
    }

    public static <K, V> EntryView<K, V> toLazyEntryView(EntryView<K, V> entryView, SerializationService serializationService, MapMergePolicy mergePolicy) {
        return new LazyEntryView<K, V>(entryView.getKey(), entryView.getValue(), serializationService, mergePolicy).setCost(entryView.getCost()).setVersion(entryView.getVersion()).setLastAccessTime(entryView.getLastAccessTime()).setLastUpdateTime(entryView.getLastUpdateTime()).setTtl(entryView.getTtl()).setMaxIdle(entryView.getMaxIdle()).setCreationTime(entryView.getCreationTime()).setHits(entryView.getHits()).setExpirationTime(entryView.getExpirationTime()).setLastStoredTime(entryView.getLastStoredTime());
    }
}

