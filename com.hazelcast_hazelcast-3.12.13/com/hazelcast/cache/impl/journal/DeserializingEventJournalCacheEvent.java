/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.journal.InternalEventJournalCacheEvent;
import com.hazelcast.cache.journal.EventJournalCacheEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import com.hazelcast.spi.serialization.SerializationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

@SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"}, justification="equality is checked by serialised data in superclass, not deserialised instances in this class")
public class DeserializingEventJournalCacheEvent<K, V>
extends InternalEventJournalCacheEvent
implements EventJournalCacheEvent<K, V>,
HazelcastInstanceAware {
    private SerializationService serializationService;
    private K objectKey;
    private V objectNewValue;
    private V objectOldValue;

    public DeserializingEventJournalCacheEvent() {
    }

    public DeserializingEventJournalCacheEvent(SerializationService serializationService, InternalEventJournalCacheEvent je) {
        super(je.getDataKey(), je.getDataNewValue(), je.getDataOldValue(), je.getEventType());
        this.serializationService = serializationService;
    }

    @Override
    public int getId() {
        return 58;
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
    public CacheEventType getType() {
        return CacheEventType.getByType(this.eventType);
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

