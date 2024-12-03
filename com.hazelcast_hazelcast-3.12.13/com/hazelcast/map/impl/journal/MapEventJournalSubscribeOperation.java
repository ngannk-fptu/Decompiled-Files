/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.map.impl.journal.MapEventJournal;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class MapEventJournalSubscribeOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private EventJournalInitialSubscriberState response;
    private ObjectNamespace namespace;

    public MapEventJournalSubscribeOperation() {
    }

    public MapEventJournalSubscribeOperation(String name) {
        super(name);
    }

    @Override
    public void beforeRun() throws Exception {
        super.beforeRun();
        this.namespace = this.getServiceNamespace();
        if (!this.mapServiceContext.getEventJournal().hasEventJournal(this.namespace)) {
            throw new UnsupportedOperationException("Cannot subscribe to event journal because it is either not configured or disabled for map '" + this.name + '\'');
        }
    }

    @Override
    public void run() {
        MapEventJournal eventJournal = this.mapServiceContext.getEventJournal();
        long newestSequence = eventJournal.newestSequence(this.namespace, this.getPartitionId());
        long oldestSequence = eventJournal.oldestSequence(this.namespace, this.getPartitionId());
        this.response = new EventJournalInitialSubscriberState(oldestSequence, newestSequence);
    }

    @Override
    public EventJournalInitialSubscriberState getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 141;
    }
}

