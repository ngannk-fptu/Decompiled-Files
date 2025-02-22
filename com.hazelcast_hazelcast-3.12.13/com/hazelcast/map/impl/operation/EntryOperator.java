/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.ToHeapDataConverter;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.LazyMapEntry;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.LockAwareLazyMapEntry;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.FalsePredicate;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Clock;
import com.hazelcast.wan.impl.CallerProvenance;
import java.util.Map;

public final class EntryOperator {
    private final boolean shouldClone;
    private final boolean backup;
    private final boolean readOnly;
    private final boolean wanReplicationEnabled;
    private final boolean hasEventRegistration;
    private final int partitionId;
    private final long startTimeNanos = System.nanoTime();
    private final String mapName;
    private final RecordStore recordStore;
    private final InternalSerializationService ss;
    private final MapContainer mapContainer;
    private final MapEventPublisher mapEventPublisher;
    private final LocalMapStatsImpl stats;
    private final IPartitionService partitionService;
    private final Predicate predicate;
    private final MapServiceContext mapServiceContext;
    private final MapOperation mapOperation;
    private final Address callerAddress;
    private final InMemoryFormat inMemoryFormat;
    private EntryProcessor entryProcessor;
    private EntryBackupProcessor backupProcessor;
    private Data dataKey;
    private Object oldValue;
    private Object newValue;
    private EntryEventType eventType;
    private Data result;
    private boolean didMatchPredicate;

    private EntryOperator(MapOperation mapOperation, Object processor, Predicate predicate, boolean collectWanEvents) {
        this.backup = mapOperation instanceof BackupOperation;
        this.setProcessor(processor);
        this.mapOperation = mapOperation;
        this.predicate = predicate;
        this.recordStore = mapOperation.recordStore;
        this.readOnly = this.entryProcessor instanceof ReadOnly;
        this.mapContainer = this.recordStore.getMapContainer();
        this.inMemoryFormat = this.mapContainer.getMapConfig().getInMemoryFormat();
        this.mapName = this.mapContainer.getName();
        this.wanReplicationEnabled = this.mapContainer.isWanReplicationEnabled();
        this.shouldClone = this.mapContainer.shouldCloneOnEntryProcessing(mapOperation.getPartitionId());
        this.mapServiceContext = this.mapContainer.getMapServiceContext();
        LocalMapStatsProvider localMapStatsProvider = this.mapServiceContext.getLocalMapStatsProvider();
        this.stats = localMapStatsProvider.getLocalMapStatsImpl(this.mapName);
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        this.ss = (InternalSerializationService)nodeEngine.getSerializationService();
        this.partitionService = nodeEngine.getPartitionService();
        EventService eventService = nodeEngine.getEventService();
        this.hasEventRegistration = eventService.hasEventRegistration("hz:impl:mapService", this.mapName);
        this.mapEventPublisher = this.mapServiceContext.getMapEventPublisher();
        this.partitionId = this.recordStore.getPartitionId();
        this.callerAddress = mapOperation.getCallerAddress();
    }

    private void setProcessor(Object processor) {
        if (this.backup) {
            this.backupProcessor = (EntryBackupProcessor)processor;
            this.entryProcessor = null;
        } else {
            this.entryProcessor = (EntryProcessor)processor;
            this.backupProcessor = null;
        }
    }

    public static EntryOperator operator(MapOperation mapOperation) {
        return new EntryOperator(mapOperation, null, null, false);
    }

    public static EntryOperator operator(MapOperation mapOperation, Object processor) {
        return new EntryOperator(mapOperation, processor, null, false);
    }

    public static EntryOperator operator(MapOperation mapOperation, Object processor, Predicate predicate) {
        return new EntryOperator(mapOperation, processor, predicate, false);
    }

    public EntryOperator init(Data dataKey, Object oldValue, Object newValue, Data result, EntryEventType eventType) {
        this.dataKey = dataKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.eventType = eventType;
        this.result = result;
        this.didMatchPredicate = true;
        return this;
    }

    public EntryOperator operateOnKey(Data dataKey) {
        this.init(dataKey, null, null, null, null);
        if (this.belongsAnotherPartition(dataKey)) {
            return this;
        }
        this.oldValue = this.recordStore.get(dataKey, this.backup, this.callerAddress, false);
        if (this.predicate != null && this.oldValue == null) {
            return this;
        }
        Boolean locked = this.recordStore.isLocked(dataKey);
        return this.operateOnKeyValueInternal(dataKey, this.clonedOrRawOldValue(), locked);
    }

    public EntryOperator operateOnKeyValue(Data dataKey, Object oldValue) {
        return this.operateOnKeyValueInternal(dataKey, oldValue, null);
    }

    private EntryOperator operateOnKeyValueInternal(Data dataKey, Object oldValue, Boolean locked) {
        this.init(dataKey, oldValue, null, null, null);
        Map.Entry entry = this.createMapEntry(dataKey, oldValue, locked);
        if (this.outOfPredicateScope(entry)) {
            this.didMatchPredicate = false;
            return this;
        }
        this.process(entry);
        this.findModificationType(entry);
        this.newValue = entry.getValue();
        if (this.readOnly && this.entryWasModified()) {
            this.throwModificationInReadOnlyException();
        }
        return this;
    }

    private boolean entryWasModified() {
        return this.eventType != null;
    }

    public EntryEventType getEventType() {
        return this.eventType;
    }

    public Object getNewValue() {
        return this.newValue;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Data getResult() {
        return this.result;
    }

    public EntryOperator doPostOperateOps() {
        if (!this.didMatchPredicate) {
            return this;
        }
        if (this.eventType == null) {
            this.onTouched();
            return this;
        }
        switch (this.eventType) {
            case UPDATED: {
                this.onTouched();
                this.onAddedOrUpdated();
                break;
            }
            case ADDED: {
                this.onAddedOrUpdated();
                break;
            }
            case REMOVED: {
                this.onRemove();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unexpected event found:" + (Object)((Object)this.eventType));
            }
        }
        if (this.wanReplicationEnabled) {
            this.publishWanReplicationEvent();
        }
        if (!this.backup) {
            if (this.hasEventRegistration) {
                this.publishEntryEvent();
            }
            this.mapOperation.invalidateNearCache(this.dataKey);
        }
        this.mapOperation.evict(this.dataKey);
        return this;
    }

    private Object clonedOrRawOldValue() {
        return this.shouldClone ? this.ss.toObject(this.ss.toData(this.oldValue)) : this.oldValue;
    }

    private boolean belongsAnotherPartition(Data key) {
        return this.partitionService.getPartitionId(key) != this.partitionId;
    }

    private boolean outOfPredicateScope(Map.Entry entry) {
        assert (entry instanceof QueryableEntry);
        if (this.predicate == null || this.predicate == TruePredicate.INSTANCE) {
            return false;
        }
        return this.predicate == FalsePredicate.INSTANCE || !this.predicate.apply(entry);
    }

    private Map.Entry createMapEntry(Data key, Object value, Boolean locked) {
        return new LockAwareLazyMapEntry(key, value, this.ss, this.mapContainer.getExtractors(), locked);
    }

    private void findModificationType(Map.Entry entry) {
        LazyMapEntry lazyMapEntry = (LazyMapEntry)entry;
        if (!lazyMapEntry.isModified() || this.oldValue == null && lazyMapEntry.hasNullValue()) {
            this.eventType = null;
            return;
        }
        if (lazyMapEntry.hasNullValue()) {
            this.eventType = EntryEventType.REMOVED;
            return;
        }
        this.eventType = this.oldValue == null ? EntryEventType.ADDED : EntryEventType.UPDATED;
    }

    private void onTouched() {
        Object record = this.recordStore.getRecord(this.dataKey);
        if (record != null) {
            this.recordStore.accessRecord((Record)record, Clock.currentTimeMillis());
        }
    }

    private void onAddedOrUpdated() {
        if (this.backup) {
            this.recordStore.putBackup(this.dataKey, this.newValue, CallerProvenance.NOT_WAN);
        } else {
            this.recordStore.setWithUncountedAccess(this.dataKey, this.newValue, -1L, -1L);
            if (this.mapOperation.isPostProcessing(this.recordStore)) {
                Object record = this.recordStore.getRecord(this.dataKey);
                this.newValue = record == null ? null : record.getValue();
            }
            this.mapServiceContext.interceptAfterPut(this.mapName, this.newValue);
            this.stats.incrementPutLatencyNanos(EntryOperator.getLatencyNanos(this.startTimeNanos));
        }
    }

    private void onRemove() {
        if (this.backup) {
            this.recordStore.removeBackup(this.dataKey, CallerProvenance.NOT_WAN);
        } else {
            this.recordStore.delete(this.dataKey, CallerProvenance.NOT_WAN);
            this.mapServiceContext.interceptAfterRemove(this.mapName, this.oldValue);
            this.stats.incrementRemoveLatencyNanos(EntryOperator.getLatencyNanos(this.startTimeNanos));
        }
    }

    private static long getLatencyNanos(long beginTimeNanos) {
        return System.nanoTime() - beginTimeNanos;
    }

    private void process(Map.Entry entry) {
        if (this.backup) {
            this.backupProcessor.processBackup(entry);
            return;
        }
        this.result = this.ss.toData(this.entryProcessor.process(entry));
    }

    private void throwModificationInReadOnlyException() {
        throw new UnsupportedOperationException("Entry Processor " + this.entryProcessor.getClass().getName() + " marked as ReadOnly tried to modify map " + this.mapName + ". This is not supported. Remove the ReadOnly marker from the Entry Processor or do not modify the entry in the process method.");
    }

    private void publishWanReplicationEvent() {
        assert (this.entryWasModified());
        if (this.eventType == EntryEventType.REMOVED) {
            this.mapOperation.publishWanRemove(this.dataKey);
        } else {
            this.mapOperation.publishWanUpdate(this.dataKey, this.newValue);
        }
    }

    private void publishEntryEvent() {
        Object oldValue = this.getOrNullOldValue();
        this.mapEventPublisher.publishEvent(this.callerAddress, this.mapName, this.eventType, ToHeapDataConverter.toHeapData(this.dataKey), oldValue, this.newValue);
    }

    private Object getOrNullOldValue() {
        if (this.inMemoryFormat == InMemoryFormat.OBJECT && this.eventType != EntryEventType.REMOVED) {
            return null;
        }
        return this.oldValue;
    }
}

