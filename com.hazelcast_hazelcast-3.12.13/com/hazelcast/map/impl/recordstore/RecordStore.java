/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.EntryView;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.iterator.MapKeysWithCursor;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordFactory;
import com.hazelcast.map.impl.recordstore.Storage;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.monitor.LocalRecordStoreStats;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.wan.impl.CallerProvenance;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface RecordStore<R extends Record> {
    public static final long DEFAULT_TTL = -1L;
    public static final long DEFAULT_MAX_IDLE = -1L;

    public LocalRecordStoreStats getLocalRecordStoreStats();

    public void accessRecord(Record var1, long var2);

    public String getName();

    public Object set(Data var1, Object var2, long var3, long var5);

    public Object put(Data var1, Object var2, long var3, long var5);

    public Object putIfAbsent(Data var1, Object var2, long var3, long var5, Address var7);

    public R putBackup(Data var1, Object var2, CallerProvenance var3);

    public R putBackup(Data var1, Object var2, long var3, long var5, boolean var7, CallerProvenance var8);

    public boolean setWithUncountedAccess(Data var1, Object var2, long var3, long var5);

    public Object remove(Data var1, CallerProvenance var2);

    public boolean delete(Data var1, CallerProvenance var2);

    public boolean remove(Data var1, Object var2);

    public boolean setTtl(Data var1, long var2);

    public void removeBackup(Data var1, CallerProvenance var2);

    public Object get(Data var1, boolean var2, Address var3, boolean var4);

    public Object get(Data var1, boolean var2, Address var3);

    public Data readBackupData(Data var1);

    public MapEntries getAll(Set<Data> var1, Address var2);

    public boolean existInMemory(Data var1);

    public boolean containsKey(Data var1, Address var2);

    public int getLockedEntryCount();

    public Object replace(Data var1, Object var2);

    public boolean replace(Data var1, Object var2, Object var3);

    public Object putTransient(Data var1, Object var2, long var3, long var5);

    public Object putFromLoad(Data var1, Object var2, Address var3);

    public Object putFromLoadBackup(Data var1, Object var2);

    public boolean merge(SplitBrainMergeTypes.MapMergeTypes var1, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> var2);

    public boolean merge(SplitBrainMergeTypes.MapMergeTypes var1, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> var2, CallerProvenance var3);

    public boolean merge(Data var1, EntryView var2, MapMergePolicy var3);

    public boolean merge(Data var1, EntryView var2, MapMergePolicy var3, CallerProvenance var4);

    public R getRecord(Data var1);

    public void putRecord(Data var1, R var2);

    public Iterator<Record> iterator();

    public Iterator<Record> iterator(long var1, boolean var3);

    public MapKeysWithCursor fetchKeys(int var1, int var2);

    public MapEntriesWithCursor fetchEntries(int var1, int var2);

    public Iterator<Record> loadAwareIterator(long var1, boolean var3);

    public int size();

    public boolean txnLock(Data var1, String var2, long var3, long var5, long var7, boolean var9);

    public boolean extendLock(Data var1, String var2, long var3, long var5);

    public boolean localLock(Data var1, String var2, long var3, long var5, long var7);

    public boolean lock(Data var1, String var2, long var3, long var5, long var7);

    public boolean isLockedBy(Data var1, String var2, long var3);

    public boolean unlock(Data var1, String var2, long var3, long var5);

    public boolean isLocked(Data var1);

    public boolean isTransactionallyLocked(Data var1);

    public boolean canAcquireLock(Data var1, String var2, long var3);

    public String getLockOwnerInfo(Data var1);

    public boolean containsValue(Object var1);

    public Object evict(Data var1, boolean var2);

    public int evictAll(boolean var1);

    public MapContainer getMapContainer();

    public long softFlush();

    public boolean forceUnlock(Data var1);

    public long getOwnedEntryCost();

    public boolean isEmpty();

    public void evictExpiredEntries(int var1, boolean var2);

    public boolean isExpirable();

    public boolean isExpired(R var1, long var2, boolean var4);

    public void doPostEvictionOperations(Record var1);

    public MapDataStore<Data, Object> getMapDataStore();

    public InvalidationQueue<ExpiredKey> getExpiredKeysQueue();

    public int getPartitionId();

    public R getRecordOrNull(Data var1);

    public void evictEntries(Data var1);

    public boolean shouldEvict();

    public Storage createStorage(RecordFactory<R> var1, InMemoryFormat var2);

    public Record createRecord(Data var1, Object var2, long var3, long var5, long var7);

    public Record loadRecordOrNull(Data var1, boolean var2, Address var3);

    public void disposeDeferredBlocks();

    public void init();

    public Storage getStorage();

    public void sampleAndForceRemoveEntries(int var1);

    public void startLoading();

    public void setPreMigrationLoadedStatus(boolean var1);

    public boolean isKeyLoadFinished();

    public boolean isLoaded();

    public void checkIfLoaded() throws RetryableHazelcastException;

    public void loadAll(boolean var1);

    public void maybeDoInitialLoad();

    public void loadAllFromStore(List<Data> var1, boolean var2);

    public void updateLoadStatus(boolean var1, Throwable var2);

    public boolean hasQueryCache();

    public void clearPartition(boolean var1, boolean var2);

    public int clear();

    public void reset();

    public void destroy();
}

