/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.journal.MapEventJournal;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStoreMutationObserver;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;

public class EventJournalWriterRecordStoreMutationObserver
implements RecordStoreMutationObserver {
    private final MapEventJournal eventJournal;
    private final int partitionId;
    private final EventJournalConfig eventJournalConfig;
    private final ObjectNamespace objectNamespace;

    public EventJournalWriterRecordStoreMutationObserver(MapEventJournal eventJournal, MapContainer mapContainer, int partitionId) {
        this.eventJournal = eventJournal;
        this.partitionId = partitionId;
        this.eventJournalConfig = mapContainer.getEventJournalConfig();
        this.objectNamespace = mapContainer.getObjectNamespace();
    }

    @Override
    public void onClear() {
    }

    public void onPutRecord(Data key, Record record) {
        this.eventJournal.writeAddEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, record.getKey(), record.getValue());
    }

    public void onReplicationPutRecord(Data key, Record record) {
    }

    public void onUpdateRecord(Data key, Record record, Object newValue) {
        this.eventJournal.writeUpdateEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, record.getKey(), record.getValue(), newValue);
    }

    public void onRemoveRecord(Data key, Record record) {
        this.eventJournal.writeRemoveEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, record.getKey(), record.getValue());
    }

    public void onEvictRecord(Data key, Record record) {
        this.eventJournal.writeEvictEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, record.getKey(), record.getValue());
    }

    public void onLoadRecord(Data key, Record record) {
        this.eventJournal.writeLoadEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, record.getKey(), record.getValue());
    }

    @Override
    public void onDestroy(boolean internal) {
        if (!internal) {
            this.eventJournal.destroy(this.objectNamespace, this.partitionId);
        }
    }

    @Override
    public void onReset() {
    }
}

