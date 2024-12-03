/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.journal.CacheEventJournal;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.AbstractNamedOperation;

public class CacheEventJournalSubscribeOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
IdentifiedDataSerializable,
ReadonlyOperation {
    private EventJournalInitialSubscriberState response;
    private ObjectNamespace namespace;

    public CacheEventJournalSubscribeOperation() {
    }

    public CacheEventJournalSubscribeOperation(String name) {
        super(name);
    }

    @Override
    public void beforeRun() throws Exception {
        super.beforeRun();
        this.namespace = CacheService.getObjectNamespace(this.name);
        CacheService service = (CacheService)this.getService();
        if (!service.getEventJournal().hasEventJournal(this.namespace)) {
            throw new UnsupportedOperationException("Cannot subscribe to event journal because it is either not configured or disabled for cache " + this.name);
        }
    }

    @Override
    public void run() {
        CacheService service = (CacheService)this.getService();
        CacheEventJournal eventJournal = service.getEventJournal();
        long newestSequence = eventJournal.newestSequence(this.namespace, this.getPartitionId());
        long oldestSequence = eventJournal.oldestSequence(this.namespace, this.getPartitionId());
        this.response = new EventJournalInitialSubscriberState(oldestSequence, newestSequence);
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 56;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public EventJournalInitialSubscriberState getResponse() {
        return this.response;
    }
}

