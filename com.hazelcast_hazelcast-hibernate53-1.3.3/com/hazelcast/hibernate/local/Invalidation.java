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

public class Invalidation
implements IdentifiedDataSerializable {
    private Object key;
    private Object version;

    public Invalidation() {
    }

    public Invalidation(Object key, Object version) {
        this.key = key;
        this.version = version;
    }

    public Object getKey() {
        return this.key;
    }

    public Object getVersion() {
        return this.version;
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.key);
        out.writeObject(this.version);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readObject();
        this.version = in.readObject();
    }

    public int getFactoryId() {
        return HibernateDataSerializerHook.F_ID;
    }

    public int getId() {
        return 5;
    }

    public String toString() {
        return "Invalidation{key=" + this.key + ", version=" + this.version + '}';
    }
}

