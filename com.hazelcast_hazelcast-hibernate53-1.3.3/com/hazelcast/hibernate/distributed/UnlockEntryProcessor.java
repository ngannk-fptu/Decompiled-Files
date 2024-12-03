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
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.Map;

public class UnlockEntryProcessor
extends AbstractRegionCacheEntryProcessor {
    private ExpiryMarker lock;
    private String nextMarkerId;
    private long timestamp;

    public UnlockEntryProcessor() {
    }

    public UnlockEntryProcessor(ExpiryMarker lock, String nextMarkerId, long timestamp) {
        this.lock = lock;
        this.nextMarkerId = nextMarkerId;
        this.timestamp = timestamp;
    }

    public Void process(Map.Entry<Object, Expirable> entry) {
        Expirable expirable = entry.getValue();
        if (expirable != null) {
            if (expirable.matches(this.lock)) {
                expirable = ((ExpiryMarker)expirable).expire(this.timestamp);
            } else if (expirable.getValue() != null) {
                expirable = new ExpiryMarker(null, this.timestamp, this.nextMarkerId).expire(this.timestamp);
            } else {
                return null;
            }
            entry.setValue(expirable);
        }
        return null;
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject((Object)this.lock);
        out.writeUTF(this.nextMarkerId);
        out.writeLong(this.timestamp);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.lock = (ExpiryMarker)in.readObject();
        this.nextMarkerId = in.readUTF();
        this.timestamp = in.readLong();
    }

    public int getId() {
        return 3;
    }
}

