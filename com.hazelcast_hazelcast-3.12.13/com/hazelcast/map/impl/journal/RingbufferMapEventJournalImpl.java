/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.journal.InternalEventJournalMapEvent;
import com.hazelcast.map.impl.journal.MapEventJournal;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataType;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.ringbuffer.impl.RingbufferWaitNotifyKey;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationparker.OperationParker;

public class RingbufferMapEventJournalImpl
implements MapEventJournal {
    private final NodeEngineImpl nodeEngine;
    private final MapServiceContext mapServiceContext;
    private final ILogger logger;

    public RingbufferMapEventJournalImpl(NodeEngine engine, MapServiceContext mapServiceContext) {
        this.nodeEngine = (NodeEngineImpl)engine;
        this.mapServiceContext = mapServiceContext;
        this.logger = this.nodeEngine.getLogger(RingbufferMapEventJournalImpl.class);
    }

    @Override
    public void writeUpdateEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object oldValue, Object newValue) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, EntryEventType.UPDATED, key, oldValue, newValue);
    }

    @Override
    public void writeAddEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, EntryEventType.ADDED, key, null, value);
    }

    @Override
    public void writeRemoveEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, EntryEventType.REMOVED, key, value, null);
    }

    @Override
    public void writeEvictEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, EntryEventType.EVICTED, key, value, null);
    }

    @Override
    public void writeLoadEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, EntryEventType.LOADED, key, null, value);
    }

    @Override
    public long newestSequence(ObjectNamespace namespace, int partitionId) {
        return this.getRingbufferOrFail(namespace, partitionId).tailSequence();
    }

    @Override
    public long oldestSequence(ObjectNamespace namespace, int partitionId) {
        return this.getRingbufferOrFail(namespace, partitionId).headSequence();
    }

    @Override
    public boolean isPersistenceEnabled(ObjectNamespace namespace, int partitionId) {
        return this.getRingbufferOrFail(namespace, partitionId).getStore().isEnabled();
    }

    @Override
    public void destroy(ObjectNamespace namespace, int partitionId) {
        RingbufferService service;
        try {
            service = this.getRingbufferService();
        }
        catch (Exception e) {
            if (this.nodeEngine.isRunning()) {
                this.logger.fine("Could not retrieve ringbuffer service to destroy event journal " + namespace, e);
            }
            return;
        }
        service.destroyContainer(partitionId, namespace);
    }

    @Override
    public void isAvailableOrNextSequence(ObjectNamespace namespace, int partitionId, long sequence) {
        this.getRingbufferOrFail(namespace, partitionId).checkBlockableReadSequence(sequence);
    }

    @Override
    public boolean isNextAvailableSequence(ObjectNamespace namespace, int partitionId, long sequence) {
        return this.getRingbufferOrFail(namespace, partitionId).shouldWait(sequence);
    }

    @Override
    public WaitNotifyKey getWaitNotifyKey(ObjectNamespace namespace, int partitionId) {
        return new RingbufferWaitNotifyKey(namespace, partitionId);
    }

    @Override
    public <T> long readMany(ObjectNamespace namespace, int partitionId, long beginSequence, ReadResultSetImpl<InternalEventJournalMapEvent, T> resultSet) {
        return this.getRingbufferOrFail(namespace, partitionId).readMany(beginSequence, resultSet);
    }

    @Override
    public void cleanup(ObjectNamespace namespace, int partitionId) {
        this.getRingbufferOrFail(namespace, partitionId).cleanup();
    }

    @Override
    public boolean hasEventJournal(ObjectNamespace namespace) {
        EventJournalConfig config = this.getEventJournalConfig(namespace);
        return config != null && config.isEnabled();
    }

    @Override
    public EventJournalConfig getEventJournalConfig(ObjectNamespace namespace) {
        return this.nodeEngine.getConfig().findMapEventJournalConfig(namespace.getObjectName());
    }

    @Override
    public RingbufferConfig toRingbufferConfig(EventJournalConfig config, ObjectNamespace namespace) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(namespace.getObjectName());
        int partitionCount = this.nodeEngine.getPartitionService().getPartitionCount();
        return new RingbufferConfig().setAsyncBackupCount(mapContainer.getAsyncBackupCount()).setBackupCount(mapContainer.getBackupCount()).setInMemoryFormat(InMemoryFormat.OBJECT).setCapacity(config.getCapacity() / partitionCount).setTimeToLiveSeconds(config.getTimeToLiveSeconds());
    }

    private void addToEventRingbuffer(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, EntryEventType eventType, Data key, Object oldValue, Object newValue) {
        if (journalConfig == null || !journalConfig.isEnabled()) {
            return;
        }
        RingbufferContainer<InternalEventJournalMapEvent, Object> eventContainer = this.getRingbufferOrNull(namespace, partitionId);
        if (eventContainer == null) {
            return;
        }
        InternalEventJournalMapEvent event = new InternalEventJournalMapEvent(this.toData(key), this.toData(newValue), this.toData(oldValue), eventType.getType());
        eventContainer.add(event);
        this.getOperationParker().unpark(eventContainer);
    }

    private Data toData(Object val) {
        return this.getSerializationService().toData(val, DataType.HEAP);
    }

    private RingbufferContainer<InternalEventJournalMapEvent, Object> getRingbufferOrFail(ObjectNamespace namespace, int partitionId) {
        RingbufferContainer<InternalEventJournalMapEvent, Object> ringbuffer = this.getRingbufferOrNull(namespace, partitionId);
        if (ringbuffer == null) {
            throw new IllegalStateException("There is no event journal configured for map with name: " + namespace.getObjectName());
        }
        return ringbuffer;
    }

    private RingbufferContainer<InternalEventJournalMapEvent, Object> getRingbufferOrNull(ObjectNamespace namespace, int partitionId) {
        RingbufferService service = this.getRingbufferService();
        RingbufferContainer<InternalEventJournalMapEvent, Object> container = service.getContainerOrNull(partitionId, namespace);
        if (container != null) {
            return container;
        }
        EventJournalConfig config = this.getEventJournalConfig(namespace);
        if (config == null || !config.isEnabled()) {
            return null;
        }
        RingbufferConfig ringbufferConfig = this.toRingbufferConfig(config, namespace);
        return service.getOrCreateContainer(partitionId, namespace, ringbufferConfig);
    }

    private RingbufferService getRingbufferService() {
        return (RingbufferService)this.nodeEngine.getService("hz:impl:ringbufferService");
    }

    private OperationParker getOperationParker() {
        return this.nodeEngine.getOperationParker();
    }

    private InternalSerializationService getSerializationService() {
        return (InternalSerializationService)this.nodeEngine.getSerializationService();
    }
}

