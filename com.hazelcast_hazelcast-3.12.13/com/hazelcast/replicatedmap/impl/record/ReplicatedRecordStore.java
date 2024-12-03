/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.record.InternalReplicatedMapStorage;
import com.hazelcast.replicatedmap.impl.record.RecordMigrationInfo;
import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.scheduler.ScheduledEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface ReplicatedRecordStore {
    public String getName();

    public int getPartitionId();

    public Object remove(Object var1);

    public Object removeWithVersion(Object var1, long var2);

    public void evict(Object var1);

    public Object get(Object var1);

    public Object put(Object var1, Object var2);

    public Object put(Object var1, Object var2, long var3, TimeUnit var5, boolean var6);

    public Object putWithVersion(Object var1, Object var2, long var3, TimeUnit var5, boolean var6, long var7);

    public boolean containsKey(Object var1);

    public boolean containsValue(Object var1);

    public ReplicatedRecord getReplicatedRecord(Object var1);

    public Set keySet(boolean var1);

    public Collection values(boolean var1);

    public Collection values(Comparator var1);

    public Set entrySet(boolean var1);

    public int size();

    public void clear();

    public void clearWithVersion(long var1);

    public void reset();

    public boolean isEmpty();

    public Object unmarshall(Object var1);

    public Object marshall(Object var1);

    public void destroy();

    public long getVersion();

    public boolean isStale(long var1);

    public Iterator<ReplicatedRecord> recordIterator();

    public void putRecords(Collection<RecordMigrationInfo> var1, long var2);

    public InternalReplicatedMapStorage getStorage();

    public ScheduledEntry<Object, Object> cancelTtlEntry(Object var1);

    public boolean scheduleTtlEntry(long var1, Object var3, Object var4);

    public boolean isLoaded();

    public void setLoaded(boolean var1);

    public boolean merge(SplitBrainMergeTypes.ReplicatedMapMergeTypes var1, SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> var2);

    public boolean merge(Object var1, ReplicatedMapEntryView var2, ReplicatedMapMergePolicy var3);
}

