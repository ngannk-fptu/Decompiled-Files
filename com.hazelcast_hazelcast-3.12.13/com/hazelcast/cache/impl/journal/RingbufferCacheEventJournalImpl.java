/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.journal.CacheEventJournal;
import com.hazelcast.cache.impl.journal.InternalEventJournalCacheEvent;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
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

public class RingbufferCacheEventJournalImpl
implements CacheEventJournal {
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;

    public RingbufferCacheEventJournalImpl(NodeEngine engine) {
        this.nodeEngine = (NodeEngineImpl)engine;
        this.logger = this.nodeEngine.getLogger(RingbufferCacheEventJournalImpl.class);
    }

    @Override
    public void writeUpdateEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object oldValue, Object newValue) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, CacheEventType.UPDATED, key, oldValue, newValue);
    }

    @Override
    public void writeCreatedEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, CacheEventType.CREATED, key, null, value);
    }

    @Override
    public void writeRemoveEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, CacheEventType.REMOVED, key, value, null);
    }

    @Override
    public void writeEvictEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, CacheEventType.EVICTED, key, value, null);
    }

    @Override
    public void writeExpiredEvent(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, Data key, Object value) {
        this.addToEventRingbuffer(journalConfig, namespace, partitionId, CacheEventType.EXPIRED, key, value, null);
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
    public <T> long readMany(ObjectNamespace namespace, int partitionId, long beginSequence, ReadResultSetImpl<InternalEventJournalCacheEvent, T> resultSet) {
        return this.getRingbufferOrFail(namespace, partitionId).readMany(beginSequence, resultSet);
    }

    @Override
    public void cleanup(ObjectNamespace namespace, int partitionId) {
        this.getRingbufferOrFail(namespace, partitionId).cleanup();
    }

    @Override
    public boolean hasEventJournal(ObjectNamespace namespace) {
        return this.getEventJournalConfig(namespace) != null;
    }

    @Override
    public EventJournalConfig getEventJournalConfig(ObjectNamespace namespace) {
        String name = namespace.getObjectName();
        CacheConfig cacheConfig = this.getCacheService().getCacheConfig(name);
        if (cacheConfig == null) {
            throw new CacheNotExistsException("Cache " + name + " is already destroyed or not created yet, on " + this.nodeEngine.getLocalMember());
        }
        String cacheSimpleName = cacheConfig.getName();
        EventJournalConfig config = this.nodeEngine.getConfig().findCacheEventJournalConfig(cacheSimpleName);
        if (config == null || !config.isEnabled()) {
            return null;
        }
        return config;
    }

    @Override
    public RingbufferConfig toRingbufferConfig(EventJournalConfig config, ObjectNamespace namespace) {
        CacheConfig cacheConfig = this.getCacheService().getCacheConfig(namespace.getObjectName());
        if (cacheConfig == null) {
            throw new CacheNotExistsException("Cache " + namespace.getObjectName() + " is already destroyed or not created yet, on " + this.nodeEngine.getLocalMember());
        }
        int partitionCount = this.nodeEngine.getPartitionService().getPartitionCount();
        return new RingbufferConfig().setAsyncBackupCount(cacheConfig.getAsyncBackupCount()).setBackupCount(cacheConfig.getBackupCount()).setInMemoryFormat(InMemoryFormat.OBJECT).setCapacity(config.getCapacity() / partitionCount).setTimeToLiveSeconds(config.getTimeToLiveSeconds());
    }

    private void addToEventRingbuffer(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId, CacheEventType eventType, Data key, Object oldValue, Object newValue) {
        if (journalConfig == null || !journalConfig.isEnabled()) {
            return;
        }
        RingbufferContainer<InternalEventJournalCacheEvent, Object> eventContainer = this.getRingbufferOrNull(journalConfig, namespace, partitionId);
        if (eventContainer == null) {
            return;
        }
        InternalEventJournalCacheEvent event = new InternalEventJournalCacheEvent(this.toData(key), this.toData(newValue), this.toData(oldValue), eventType.getType());
        eventContainer.add(event);
        this.getOperationParker().unpark(eventContainer);
    }

    protected Data toData(Object val) {
        return this.getSerializationService().toData(val, DataType.HEAP);
    }

    private RingbufferContainer<InternalEventJournalCacheEvent, Object> getRingbufferOrFail(ObjectNamespace namespace, int partitionId) {
        RingbufferService ringbufferService = this.getRingbufferService();
        RingbufferContainer<InternalEventJournalCacheEvent, Object> container = ringbufferService.getContainerOrNull(partitionId, namespace);
        if (container != null) {
            return container;
        }
        EventJournalConfig config = this.getEventJournalConfig(namespace);
        if (config == null) {
            throw new IllegalStateException(String.format("There is no event journal configured for cache %s or the journal is disabled", namespace.getObjectName()));
        }
        return this.getOrCreateRingbufferContainer(namespace, partitionId, config);
    }

    private RingbufferContainer<InternalEventJournalCacheEvent, Object> getRingbufferOrNull(EventJournalConfig journalConfig, ObjectNamespace namespace, int partitionId) {
        RingbufferService ringbufferService = this.getRingbufferService();
        RingbufferContainer<InternalEventJournalCacheEvent, Object> container = ringbufferService.getContainerOrNull(partitionId, namespace);
        if (container != null) {
            return container;
        }
        return journalConfig != null ? this.getOrCreateRingbufferContainer(namespace, partitionId, journalConfig) : null;
    }

    private RingbufferContainer<InternalEventJournalCacheEvent, Object> getOrCreateRingbufferContainer(ObjectNamespace namespace, int partitionId, EventJournalConfig config) {
        RingbufferConfig ringbufferConfig = this.toRingbufferConfig(config, namespace);
        return this.getRingbufferService().getOrCreateContainer(partitionId, namespace, ringbufferConfig);
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

    private CacheService getCacheService() {
        return (CacheService)this.nodeEngine.getService("hz:impl:cacheService");
    }
}

