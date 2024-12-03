/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.EntryCostEstimator;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.iterator.MapKeysWithCursor;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;
import java.util.Iterator;

public interface Storage<K, R> {
    public void put(K var1, R var2);

    public void updateRecordValue(K var1, R var2, Object var3);

    public R get(K var1);

    public R getIfSameKey(K var1);

    public void removeRecord(R var1);

    public boolean containsKey(K var1);

    public Collection<R> values();

    public Iterator<R> mutationTolerantIterator();

    public int size();

    public boolean isEmpty();

    public void clear(boolean var1);

    public void destroy(boolean var1);

    public EntryCostEstimator getEntryCostEstimator();

    public void setEntryCostEstimator(EntryCostEstimator var1);

    public void disposeDeferredBlocks();

    public Iterable<EntryView> getRandomSamples(int var1);

    public MapKeysWithCursor fetchKeys(int var1, int var2);

    public MapEntriesWithCursor fetchEntries(int var1, int var2, SerializationService var3);

    public Record extractRecordFromLazy(EntryView var1);
}

