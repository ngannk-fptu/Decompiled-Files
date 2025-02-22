/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.impl.journal.InternalEventJournalMapEvent;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import com.hazelcast.spi.serialization.SerializationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

@SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"}, justification="equality is checked by serialized data in superclass, not deserialized instances in this class")
public class DeserializingEventJournalMapEvent<K, V>
extends InternalEventJournalMapEvent
implements EventJournalMapEvent<K, V>,
HazelcastInstanceAware {
    private SerializationService serializationService;
    private K objectKey;
    private V objectNewValue;
    private V objectOldValue;

    public DeserializingEventJournalMapEvent() {
    }

    public DeserializingEventJournalMapEvent(SerializationService serializationService, InternalEventJournalMapEvent je) {
        super(je.getDataKey(), je.getDataNewValue(), je.getDataOldValue(), je.getEventType());
        this.serializationService = serializationService;
    }

    @Override
    public int getId() {
        return 143;
    }

    @Override
    public K getKey() {
        if (this.objectKey == null && this.dataKey != null) {
            this.objectKey = this.serializationService.toObject(this.dataKey);
        }
        return this.objectKey;
    }

    @Override
    public V getNewValue() {
        if (this.objectNewValue == null && this.dataNewValue != null) {
            this.objectNewValue = this.serializationService.toObject(this.dataNewValue);
        }
        return this.objectNewValue;
    }

    @Override
    public V getOldValue() {
        if (this.objectOldValue == null && this.dataOldValue != null) {
            this.objectOldValue = this.serializationService.toObject(this.dataOldValue);
        }
        return this.objectOldValue;
    }

    @Override
    public EntryEventType getType() {
        return EntryEventType.getByType(this.eventType);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.eventType);
        out.writeData(this.toData(this.dataKey, this.objectKey));
        out.writeData(this.toData(this.dataNewValue, this.objectNewValue));
        out.writeData(this.toData(this.dataOldValue, this.objectOldValue));
    }

    private Data toData(Data data, Object o) {
        return o != null ? this.serializationService.toData(o) : data;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.serializationService = ((SerializationServiceSupport)((Object)hazelcastInstance)).getSerializationService();
    }
}

