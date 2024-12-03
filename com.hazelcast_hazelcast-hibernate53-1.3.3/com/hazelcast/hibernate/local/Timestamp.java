/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.IdentifiedDataSerializable
 */
package com.hazelcast.hibernate.local;

import com.hazelcast.hibernate.serialization.HibernateDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.UUID;

public class Timestamp
implements IdentifiedDataSerializable {
    private Object key;
    private long timestamp;
    private UUID senderId;

    public Timestamp() {
    }

    public Timestamp(Object key, long timestamp, UUID senderId) {
        this.key = key;
        this.timestamp = timestamp;
        this.senderId = senderId;
    }

    public Object getKey() {
        return this.key;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public UUID getSenderId() {
        return this.senderId;
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.key);
        out.writeLong(this.timestamp);
        out.writeUTF(this.senderId.toString());
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readObject();
        this.timestamp = in.readLong();
        this.senderId = UUID.fromString(in.readUTF());
    }

    public int getFactoryId() {
        return HibernateDataSerializerHook.F_ID;
    }

    public int getId() {
        return 6;
    }

    public String toString() {
        return "Timestamp{ key=" + this.key + ", timestamp=" + this.timestamp + ", senderId=" + this.senderId + '}';
    }
}

