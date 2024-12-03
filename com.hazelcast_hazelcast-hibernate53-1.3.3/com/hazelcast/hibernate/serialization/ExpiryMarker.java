/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.HibernateDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;

public class ExpiryMarker
extends Expirable
implements Serializable {
    private static final long NOT_COMPLETELY_EXPIRED = -1L;
    private boolean concurrent;
    private long expiredTimestamp;
    private String markerId;
    private int multiplicity;
    private long timeout;

    public ExpiryMarker() {
    }

    public ExpiryMarker(Object version, long timeout, String markerId) {
        this(version, false, -1L, markerId, 1, timeout);
    }

    private ExpiryMarker(Object version, boolean concurrent, long expiredTimestamp, String markerId, int multiplicity, long timeout) {
        super(version);
        this.concurrent = concurrent;
        this.expiredTimestamp = expiredTimestamp;
        this.markerId = markerId;
        this.multiplicity = multiplicity;
        this.timeout = timeout;
    }

    @Override
    public boolean isReplaceableBy(long txTimestamp, Object newVersion, Comparator versionComparator) {
        if (txTimestamp > this.timeout) {
            return true;
        }
        if (this.multiplicity > 0) {
            return false;
        }
        if (this.version == null) {
            return this.expiredTimestamp != -1L && txTimestamp > this.expiredTimestamp;
        }
        return versionComparator.compare(this.version, newVersion) < 0;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Object getValue(long txTimestamp) {
        return null;
    }

    @Override
    public boolean matches(ExpiryMarker lock) {
        return this.markerId.equals(lock.markerId);
    }

    public boolean isConcurrent() {
        return this.concurrent;
    }

    @Override
    public ExpiryMarker markForExpiration(long timeout, String nextMarkerId) {
        return new ExpiryMarker(this.version, true, -1L, this.markerId, this.multiplicity + 1, timeout);
    }

    public ExpiryMarker expire(long timestamp) {
        int newMultiplicity = this.multiplicity - 1;
        long newExpiredTimestamp = newMultiplicity == 0 ? timestamp : this.expiredTimestamp;
        return new ExpiryMarker(this.version, this.concurrent, newExpiredTimestamp, this.markerId, newMultiplicity, this.timeout);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeBoolean(this.concurrent);
        out.writeUTF(this.markerId);
        out.writeInt(this.multiplicity);
        out.writeLong(this.timeout);
        out.writeLong(this.expiredTimestamp);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.concurrent = in.readBoolean();
        this.markerId = in.readUTF();
        this.multiplicity = in.readInt();
        this.timeout = in.readLong();
        this.expiredTimestamp = in.readLong();
    }

    public int getFactoryId() {
        return HibernateDataSerializerHook.F_ID;
    }

    public int getId() {
        return 1;
    }
}

