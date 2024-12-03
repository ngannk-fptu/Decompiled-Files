/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.ExpiryMarker;
import com.hazelcast.hibernate.serialization.HibernateDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.Comparator;

public class Value
extends Expirable {
    private long timestamp;
    private Object value;

    public Value() {
    }

    public Value(Object version, long timestamp, Object value) {
        super(version);
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public boolean isReplaceableBy(long txTimestamp, Object newVersion, Comparator versionComparator) {
        return this.version == null ? this.timestamp <= txTimestamp : versionComparator.compare(this.version, newVersion) < 0;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Object getValue(long txTimestamp) {
        return this.timestamp <= txTimestamp ? this.value : null;
    }

    @Override
    public boolean matches(ExpiryMarker lock) {
        return false;
    }

    @Override
    public ExpiryMarker markForExpiration(long timeout, String nextMarkerId) {
        return new ExpiryMarker(this.version, timeout, nextMarkerId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.timestamp = in.readLong();
        this.value = in.readObject();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeLong(this.timestamp);
        out.writeObject(this.value);
    }

    public int getFactoryId() {
        return HibernateDataSerializerHook.F_ID;
    }

    public int getId() {
        return 0;
    }
}

