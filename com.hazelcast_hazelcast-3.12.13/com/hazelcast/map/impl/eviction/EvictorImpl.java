/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.eviction;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.map.impl.eviction.EvictionChecker;
import com.hazelcast.map.impl.eviction.Evictor;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.LazyEntryViewFromRecord;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.impl.recordstore.Storage;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;

public class EvictorImpl
implements Evictor {
    protected final EvictionChecker evictionChecker;
    protected final IPartitionService partitionService;
    protected final MapEvictionPolicy mapEvictionPolicy;
    private final int batchSize;

    public EvictorImpl(MapEvictionPolicy mapEvictionPolicy, EvictionChecker evictionChecker, IPartitionService partitionService, int batchSize) {
        this.evictionChecker = Preconditions.checkNotNull(evictionChecker);
        this.partitionService = Preconditions.checkNotNull(partitionService);
        this.mapEvictionPolicy = Preconditions.checkNotNull(mapEvictionPolicy);
        this.batchSize = batchSize;
    }

    @Override
    public void evict(RecordStore recordStore, Data excludedKey) {
        ThreadUtil.assertRunningOnPartitionThread();
        for (int i = 0; i < this.batchSize; ++i) {
            EntryView evictableEntry = this.selectEvictableEntry(recordStore, excludedKey);
            if (evictableEntry == null) {
                return;
            }
            this.evictEntry(recordStore, evictableEntry);
        }
    }

    private EntryView selectEvictableEntry(RecordStore recordStore, Data excludedKey) {
        Iterable<EntryView> samples = this.getSamples(recordStore);
        EntryView excluded = null;
        EntryView selected = null;
        for (EntryView candidate : samples) {
            if (excludedKey != null && excluded == null && this.getDataKey(candidate).equals(excludedKey)) {
                excluded = candidate;
                continue;
            }
            if (selected == null) {
                selected = candidate;
                continue;
            }
            if (this.mapEvictionPolicy.compare(candidate, selected) >= 0) continue;
            selected = candidate;
        }
        return selected == null ? excluded : selected;
    }

    private Data getDataKey(EntryView candidate) {
        return this.getRecordFromEntryView(candidate).getKey();
    }

    private void evictEntry(RecordStore recordStore, EntryView selectedEntry) {
        Record record = this.getRecordFromEntryView(selectedEntry);
        Data key = record.getKey();
        if (recordStore.isLocked(record.getKey())) {
            return;
        }
        boolean backup = this.isBackup(recordStore);
        recordStore.evict(key, backup);
        if (!backup) {
            recordStore.doPostEvictionOperations(record);
        }
    }

    @Override
    public boolean checkEvictable(RecordStore recordStore) {
        ThreadUtil.assertRunningOnPartitionThread();
        return this.evictionChecker.checkEvictable(recordStore);
    }

    protected Record getRecordFromEntryView(EntryView selectedEntry) {
        return ((LazyEntryViewFromRecord)selectedEntry).getRecord();
    }

    protected boolean isBackup(RecordStore recordStore) {
        int partitionId = recordStore.getPartitionId();
        IPartition partition = this.partitionService.getPartition(partitionId, false);
        return !partition.isLocal();
    }

    protected Iterable<EntryView> getSamples(RecordStore recordStore) {
        Storage storage = recordStore.getStorage();
        return storage.getRandomSamples(SAMPLE_COUNT);
    }

    protected static long getNow() {
        return Clock.currentTimeMillis();
    }

    public String toString() {
        return "EvictorImpl{, mapEvictionPolicy=" + this.mapEvictionPolicy + ", batchSize=" + this.batchSize + '}';
    }
}

