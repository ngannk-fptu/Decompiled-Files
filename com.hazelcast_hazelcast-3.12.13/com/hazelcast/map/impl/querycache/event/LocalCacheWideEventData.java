/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.IOException;

@BinaryInterface
public class LocalCacheWideEventData
implements EventData {
    private final String source;
    private final int eventType;
    private final int numberOfEntriesAffected;

    public LocalCacheWideEventData(String source, int eventType, int numberOfEntriesAffected) {
        this.source = source;
        this.eventType = eventType;
        this.numberOfEntriesAffected = numberOfEntriesAffected;
    }

    public int getNumberOfEntriesAffected() {
        return this.numberOfEntriesAffected;
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
        return this.eventType;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "LocalCacheWideEventData{eventType=" + this.eventType + ", source='" + this.source + '\'' + ", numberOfEntriesAffected=" + this.numberOfEntriesAffected + '}';
    }
}

