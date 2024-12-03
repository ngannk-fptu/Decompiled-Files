/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CacheEventSet
implements IdentifiedDataSerializable {
    private CacheEventType eventType;
    private Set<CacheEventData> events;
    private int completionId;

    public CacheEventSet() {
    }

    public CacheEventSet(CacheEventType eventType, Set<CacheEventData> events) {
        this.eventType = eventType;
        this.events = events;
    }

    public CacheEventSet(CacheEventType eventType, Set<CacheEventData> events, int completionId) {
        this.eventType = eventType;
        this.events = events;
        this.completionId = completionId;
    }

    public CacheEventSet(CacheEventType eventType, int completionId) {
        this.eventType = eventType;
        this.completionId = completionId;
    }

    public Set<CacheEventData> getEvents() {
        return this.events;
    }

    public CacheEventType getEventType() {
        return this.eventType;
    }

    public int getCompletionId() {
        return this.completionId;
    }

    public void addEventData(CacheEventData cacheEventData) {
        if (this.events == null) {
            this.events = new HashSet<CacheEventData>();
        }
        this.events.add(cacheEventData);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.eventType.getType());
        out.writeInt(this.completionId);
        out.writeInt(this.events.size());
        for (CacheEventData ced : this.events) {
            out.writeObject(ced);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.eventType = CacheEventType.getByType(in.readInt());
        this.completionId = in.readInt();
        int size = in.readInt();
        this.events = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            CacheEventData ced = (CacheEventData)in.readObject();
            this.events.add(ced);
        }
    }

    @Override
    public int getId() {
        return 32;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }
}

