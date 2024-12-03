/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.monitor.impl.LocalReplicatedMapStatsImpl;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEvictionProcessor;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.InternalReplicatedMapStorage;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.EntryTaskSchedulerFactory;
import com.hazelcast.util.scheduler.ScheduleType;
import com.hazelcast.util.scheduler.ScheduledEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractBaseReplicatedRecordStore<K, V>
implements ReplicatedRecordStore {
    protected int partitionId;
    protected final String name;
    protected final NodeEngine nodeEngine;
    protected final EventService eventService;
    protected final IPartitionService partitionService;
    protected final ReplicatedMapConfig replicatedMapConfig;
    protected final SerializationService serializationService;
    protected final ReplicatedMapService replicatedMapService;
    protected final AtomicReference<InternalReplicatedMapStorage<K, V>> storageRef;
    protected final AtomicBoolean isLoaded = new AtomicBoolean(false);
    private final EntryTaskScheduler<Object, Object> ttlEvictionScheduler;

    protected AbstractBaseReplicatedRecordStore(String name, ReplicatedMapService replicatedMapService, int partitionId) {
        this.name = name;
        this.partitionId = partitionId;
        this.nodeEngine = replicatedMapService.getNodeEngine();
        this.serializationService = this.nodeEngine.getSerializationService();
        this.partitionService = this.nodeEngine.getPartitionService();
        this.eventService = this.nodeEngine.getEventService();
        this.replicatedMapService = replicatedMapService;
        this.replicatedMapConfig = replicatedMapService.getReplicatedMapConfig(name);
        this.storageRef = new AtomicReference();
        this.storageRef.set(new InternalReplicatedMapStorage());
        this.ttlEvictionScheduler = EntryTaskSchedulerFactory.newScheduler(this.nodeEngine.getExecutionService().getGlobalTaskScheduler(), new ReplicatedMapEvictionProcessor(this, this.nodeEngine, partitionId), ScheduleType.POSTPONE);
    }

    @Override
    public InternalReplicatedMapStorage<K, V> getStorage() {
        return this.storageRef.get();
    }

    public AtomicReference<InternalReplicatedMapStorage<K, V>> getStorageRef() {
        return this.storageRef;
    }

    public EntryTaskScheduler getTtlEvictionScheduler() {
        return this.ttlEvictionScheduler;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public LocalReplicatedMapStatsImpl getStats() {
        return this.replicatedMapService.getLocalMapStatsImpl(this.name);
    }

    @Override
    public void destroy() {
        InternalReplicatedMapStorage storage = this.storageRef.getAndSet(new InternalReplicatedMapStorage());
        if (storage != null) {
            storage.clear();
        }
        this.ttlEvictionScheduler.cancelAll();
    }

    protected InternalReplicatedMapStorage<K, V> clearInternal() {
        InternalReplicatedMapStorage<K, V> storage = this.getStorage();
        storage.clear();
        this.getStats().incrementOtherOperations();
        this.ttlEvictionScheduler.cancelAll();
        return storage;
    }

    @Override
    public long getVersion() {
        return this.storageRef.get().getVersion();
    }

    @Override
    public boolean isStale(long version) {
        return this.storageRef.get().isStale(version);
    }

    public Set<ReplicatedRecord> getRecords() {
        return new HashSet<ReplicatedRecord>(this.storageRef.get().values());
    }

    @Override
    public ScheduledEntry<Object, Object> cancelTtlEntry(Object key) {
        return this.ttlEvictionScheduler.cancel(key);
    }

    @Override
    public boolean scheduleTtlEntry(long delayMillis, Object key, Object value) {
        return this.ttlEvictionScheduler.schedule(delayMillis, key, value);
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded.get();
    }

    @Override
    public void setLoaded(boolean loaded) {
        this.isLoaded.set(loaded);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractBaseReplicatedRecordStore that = (AbstractBaseReplicatedRecordStore)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return this.storageRef.get().equals(that.storageRef.get());
    }

    public int hashCode() {
        int result = this.storageRef.get().hashCode();
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }
}

