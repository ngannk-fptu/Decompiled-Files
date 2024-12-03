/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 */
package com.hazelcast.hibernate.distributed;

import com.hazelcast.hibernate.distributed.AbstractRegionCacheEntryProcessor;
import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.ExpiryMarker;
import com.hazelcast.hibernate.serialization.Value;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.Map;

public class UpdateEntryProcessor
extends AbstractRegionCacheEntryProcessor {
    private ExpiryMarker lock;
    private Object newValue;
    private Object newVersion;
    private String nextMarkerId;
    private long timestamp;

    public UpdateEntryProcessor() {
    }

    public UpdateEntryProcessor(ExpiryMarker lock, Object newValue, Object newVersion, String nextMarkerId, long timestamp) {
        this.lock = lock;
        this.nextMarkerId = nextMarkerId;
        this.newValue = newValue;
        this.newVersion = newVersion;
        this.timestamp = timestamp;
    }

    public Boolean process(Map.Entry<Object, Expirable> entry) {
        boolean updated;
        Expirable expirable = entry.getValue();
        if (expirable == null) {
            expirable = new Value(this.newVersion, this.timestamp, this.newValue);
            updated = true;
        } else if (expirable.matches(this.lock)) {
            ExpiryMarker marker = (ExpiryMarker)expirable;
            if (marker.isConcurrent()) {
                expirable = marker.expire(this.timestamp);
                updated = false;
            } else {
                expirable = new Value(this.newVersion, this.timestamp, this.newValue);
                updated = true;
            }
        } else {
            if (expirable.getValue() == null) {
                return false;
            }
            expirable = new ExpiryMarker(this.newVersion, this.timestamp, this.nextMarkerId).expire(this.timestamp);
            updated = false;
        }
        entry.setValue(expirable);
        return updated;
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject((Object)this.lock);
        out.writeObject(this.newValue);
        out.writeObject(this.newVersion);
        out.writeUTF(this.nextMarkerId);
        out.writeLong(this.timestamp);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.lock = (ExpiryMarker)in.readObject();
        this.newValue = in.readObject();
        this.newVersion = in.readObject();
        this.nextMarkerId = in.readUTF();
        this.timestamp = in.readLong();
    }

    public int getId() {
        return 4;
    }
}

