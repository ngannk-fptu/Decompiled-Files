/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventDataBuilder;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@BinaryInterface
public class BatchEventData
implements Sequenced,
EventData {
    private String source;
    private Collection<QueryCacheEventData> events;
    private transient int partitionId;

    public BatchEventData() {
    }

    public BatchEventData(Collection<QueryCacheEventData> events, String source, int partitionId) {
        this.events = Preconditions.checkNotNull(events, "events cannot be null");
        this.source = Preconditions.checkNotNull(source, "source cannot be null");
        this.partitionId = Preconditions.checkNotNegative(partitionId, "partitionId cannot be negative");
    }

    public void add(QueryCacheEventData entry) {
        this.events.add(entry);
    }

    public Collection<QueryCacheEventData> getEvents() {
        return this.events;
    }

    public boolean isEmpty() {
        return this.events.isEmpty();
    }

    public int size() {
        return this.events.size();
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public String getMapName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Address getCaller() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEventType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSequence() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSequence(long sequence) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        Collection<QueryCacheEventData> events = this.events;
        out.writeUTF(this.source);
        out.writeInt(events.size());
        for (QueryCacheEventData eventData : events) {
            eventData.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.source = in.readUTF();
        int size = in.readInt();
        if (size > 0) {
            this.events = new ArrayList<QueryCacheEventData>(size);
        }
        Collection<QueryCacheEventData> events = this.events;
        for (int i = 0; i < size; ++i) {
            QueryCacheEventData eventData = QueryCacheEventDataBuilder.newQueryCacheEventDataBuilder(true).build();
            eventData.readData(in);
            events.add(eventData);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BatchEventData)) {
            return false;
        }
        BatchEventData that = (BatchEventData)o;
        return this.events != null ? this.events.equals(that.events) : that.events == null;
    }

    public int hashCode() {
        return this.events != null ? this.events.hashCode() : 0;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BatchEventData{");
        for (QueryCacheEventData event : this.events) {
            stringBuilder.append(event);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}

