/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.mapstore.AbstractMapDataStore;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntries;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.operation.NotifyMapFlushOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class WriteBehindStore
extends AbstractMapDataStore<Data, Object> {
    private static final DelayedEntry TRANSIENT = DelayedEntries.emptyDelayedEntry();
    private final AtomicLong sequence = new AtomicLong(0L);
    private final Queue<Sequence> flushSequences = new ConcurrentLinkedQueue<Sequence>();
    private final boolean coalesce;
    private final ConcurrentMap<Data, DelayedEntry> stagingArea = new ConcurrentHashMap<Data, DelayedEntry>();
    private final OperationService operationService;
    private final InMemoryFormat inMemoryFormat;
    private final NodeEngine nodeEngine;
    private final String mapName;
    private final int partitionId;
    private WriteBehindProcessor writeBehindProcessor;
    private WriteBehindQueue<DelayedEntry> writeBehindQueue;

    public WriteBehindStore(MapStoreContext mapStoreContext, int partitionId, InternalSerializationService serializationService) {
        super(mapStoreContext.getMapStoreWrapper(), serializationService);
        MapStoreConfig mapStoreConfig = mapStoreContext.getMapStoreConfig();
        this.partitionId = partitionId;
        this.inMemoryFormat = WriteBehindStore.getInMemoryFormat(mapStoreContext);
        this.coalesce = mapStoreConfig.isWriteCoalescing();
        this.mapName = mapStoreContext.getMapName();
        this.nodeEngine = mapStoreContext.getMapServiceContext().getNodeEngine();
        this.operationService = this.nodeEngine.getOperationService();
    }

    @Override
    public Object add(Data key, Object value, long now) {
        if (InMemoryFormat.NATIVE == this.inMemoryFormat) {
            value = this.toHeapData(value);
            key = this.toHeapData(key);
        }
        if (!this.coalesce && InMemoryFormat.OBJECT == this.inMemoryFormat) {
            value = this.toHeapData(value);
        }
        DelayedEntry<Data, Object> delayedEntry = DelayedEntries.createDefault(key, value, now, this.partitionId);
        this.add(delayedEntry);
        return value;
    }

    public void add(DelayedEntry<Data, Object> delayedEntry) {
        this.writeBehindQueue.addLast(delayedEntry);
        this.stagingArea.put(delayedEntry.getKey(), delayedEntry);
        delayedEntry.setSequence(this.sequence.incrementAndGet());
    }

    @Override
    public void addTransient(Data key, long now) {
        if (InMemoryFormat.NATIVE == this.inMemoryFormat) {
            key = this.toHeapData(key);
        }
        this.stagingArea.put(key, TRANSIENT);
    }

    @Override
    public Object addBackup(Data key, Object value, long time) {
        return this.add(key, value, time);
    }

    @Override
    public void remove(Data key, long now) {
        if (InMemoryFormat.NATIVE == this.inMemoryFormat) {
            key = this.toHeapData(key);
        }
        DelayedEntry<Data, Object> delayedEntry = DelayedEntries.createWithoutValue(key, now, this.partitionId);
        this.add(delayedEntry);
    }

    @Override
    public void removeBackup(Data key, long time) {
        this.remove(key, time);
    }

    @Override
    public void reset() {
        this.writeBehindQueue.clear();
        this.stagingArea.clear();
        this.sequence.set(0L);
        this.flushSequences.clear();
    }

    @Override
    public Object load(Data key) {
        DelayedEntry delayedEntry = this.getFromStagingArea(key);
        if (delayedEntry == null) {
            return this.getStore().load(this.toObject(key));
        }
        return this.toObject(delayedEntry.getValue());
    }

    @Override
    public Map loadAll(Collection keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Data, Object> map = MapUtil.createHashMap(keys.size());
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Data dataKey = this.toHeapData(key);
            DelayedEntry delayedEntry = this.getFromStagingArea(dataKey);
            if (delayedEntry == null) continue;
            Object value = delayedEntry.getValue();
            if (value != null) {
                map.put(dataKey, this.toObject(value));
            }
            iterator.remove();
        }
        map.putAll(super.loadAll(keys));
        return map;
    }

    @Override
    public boolean loadable(Data key) {
        if (InMemoryFormat.NATIVE == this.inMemoryFormat) {
            key = this.toHeapData(key);
        }
        return !this.writeBehindQueue.contains(DelayedEntries.createDefault(key, null, -1L, -1));
    }

    @Override
    public int notFinishedOperationsCount() {
        return this.writeBehindQueue.size();
    }

    @Override
    public Object flush(Data key, Object value, boolean backup) {
        DelayedEntry delayedEntry;
        if (InMemoryFormat.NATIVE == this.inMemoryFormat) {
            key = this.toHeapData(key);
            value = this.toHeapData(value);
        }
        if ((delayedEntry = (DelayedEntry)this.stagingArea.get(key)) == TRANSIENT) {
            this.stagingArea.remove(key);
            return null;
        }
        if (this.writeBehindQueue.size() == 0 || !this.writeBehindQueue.contains(DelayedEntries.createWithoutValue(key))) {
            return null;
        }
        this.addAndGetSequence(false);
        return value;
    }

    @Override
    public long softFlush() {
        int size = this.writeBehindQueue.size();
        if (size == 0) {
            return 0L;
        }
        return this.addAndGetSequence(true);
    }

    private long addAndGetSequence(boolean fullFlush) {
        Sequence sequence = new Sequence(this.sequence.get(), fullFlush);
        this.flushSequences.add(sequence);
        return sequence.getSequence();
    }

    @Override
    public void hardFlush() {
        if (this.writeBehindQueue.size() == 0) {
            return;
        }
        this.writeBehindProcessor.flush(this.writeBehindQueue);
    }

    public WriteBehindQueue<DelayedEntry> getWriteBehindQueue() {
        return this.writeBehindQueue;
    }

    public void setWriteBehindQueue(WriteBehindQueue<DelayedEntry> writeBehindQueue) {
        this.writeBehindQueue = writeBehindQueue;
    }

    public void setWriteBehindProcessor(WriteBehindProcessor writeBehindProcessor) {
        this.writeBehindProcessor = writeBehindProcessor;
    }

    public void setSequence(long newSequence) {
        this.sequence.set(newSequence);
    }

    public void notifyFlush() {
        long nextSequenceNumber = this.sequence.get() + 1L;
        DelayedEntry firstEntry = this.writeBehindQueue.peek();
        if (firstEntry == null) {
            if (!this.flushSequences.isEmpty()) {
                this.findAwaitingFlushesAndSendNotification(nextSequenceNumber);
            }
        } else {
            this.findAwaitingFlushesAndSendNotification(firstEntry.getSequence());
        }
    }

    private void findAwaitingFlushesAndSendNotification(long lastSequenceInQueue) {
        int maxIterationCount = 100;
        Iterator iterator = this.flushSequences.iterator();
        int iterationCount = 0;
        while (iterator.hasNext()) {
            Sequence flushSequence = (Sequence)iterator.next();
            if (flushSequence.getSequence() < lastSequenceInQueue) {
                iterator.remove();
                this.executeNotifyOperation(flushSequence);
            }
            if (++iterationCount != 100) continue;
            break;
        }
    }

    private void executeNotifyOperation(Sequence flushSequence) {
        if (!flushSequence.isFullFlush() || !this.nodeEngine.getPartitionService().isPartitionOwner(this.partitionId)) {
            return;
        }
        NotifyMapFlushOperation operation = new NotifyMapFlushOperation(this.mapName, flushSequence.getSequence());
        operation.setServiceName("hz:impl:mapService").setNodeEngine(this.nodeEngine).setPartitionId(this.partitionId).setCallerUuid(this.nodeEngine.getLocalMember().getUuid()).setOperationResponseHandler(OperationResponseHandlerFactory.createEmptyResponseHandler());
        this.operationService.execute(operation);
    }

    protected void removeFromStagingArea(DelayedEntry delayedEntry) {
        if (delayedEntry == null) {
            return;
        }
        Data key = (Data)delayedEntry.getKey();
        this.stagingArea.remove(key, delayedEntry);
    }

    private DelayedEntry getFromStagingArea(Data key) {
        DelayedEntry delayedEntry = (DelayedEntry)this.stagingArea.get(key);
        if (delayedEntry == null || delayedEntry == TRANSIENT) {
            return null;
        }
        return delayedEntry;
    }

    public Queue<Sequence> getFlushSequences() {
        return this.flushSequences;
    }

    public long getSequenceToFlush() {
        int maxIterationCount = 100;
        Iterator iterator = this.flushSequences.iterator();
        long sequenceNumber = 0L;
        int iterationCount = 0;
        while (iterator.hasNext()) {
            Sequence sequence = (Sequence)iterator.next();
            sequenceNumber = sequence.getSequence();
            if (++iterationCount != 100) continue;
            break;
        }
        return sequenceNumber;
    }

    public void setFlushSequences(Queue<Sequence> flushSequences) {
        this.flushSequences.addAll(flushSequences);
    }

    private static InMemoryFormat getInMemoryFormat(MapStoreContext mapStoreContext) {
        MapServiceContext mapServiceContext = mapStoreContext.getMapServiceContext();
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        Config config = nodeEngine.getConfig();
        String mapName = mapStoreContext.getMapName();
        MapConfig mapConfig = config.findMapConfig(mapName);
        return mapConfig.getInMemoryFormat();
    }

    public static class Sequence {
        private final long sequence;
        private final boolean fullFlush;

        public Sequence(long sequence, boolean fullFlush) {
            this.sequence = sequence;
            this.fullFlush = fullFlush;
        }

        public long getSequence() {
            return this.sequence;
        }

        public boolean isFullFlush() {
            return this.fullFlush;
        }
    }
}

