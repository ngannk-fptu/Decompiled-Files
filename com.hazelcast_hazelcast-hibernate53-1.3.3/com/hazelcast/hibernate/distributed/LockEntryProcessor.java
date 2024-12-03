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

public class LockEntryProcessor
extends AbstractRegionCacheEntryProcessor {
    private String nextMarkerId;
    private long timeout;
    private Object version;

    public LockEntryProcessor() {
    }

    public LockEntryProcessor(String nextMarkerId, long timeout, Object version) {
        this.nextMarkerId = nextMarkerId;
        this.timeout = timeout;
        this.version = version;
    }

    public Expirable process(Map.Entry<Object, Expirable> entry) {
        Expirable expirable = entry.getValue();
        expirable = expirable == null ? new ExpiryMarker(this.version, this.timeout, this.nextMarkerId) : expirable.markForExpiration(this.timeout, this.nextMarkerId);
        entry.setValue(expirable);
        return expirable;
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.nextMarkerId);
        out.writeLong(this.timeout);
        out.writeObject(this.version);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.nextMarkerId = in.readUTF();
        this.timeout = in.readLong();
        this.version = in.readObject();
    }

    public int getId() {
        return 2;
    }
}

