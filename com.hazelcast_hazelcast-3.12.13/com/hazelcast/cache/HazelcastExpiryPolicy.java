/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.Duration
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;

@BinaryInterface
public class HazelcastExpiryPolicy
implements ExpiryPolicy,
IdentifiedDataSerializable,
Serializable {
    private Duration create;
    private Duration access;
    private Duration update;

    public HazelcastExpiryPolicy() {
    }

    public HazelcastExpiryPolicy(long createMillis, long accessMillis, long updateMillis) {
        this(new Duration(TimeUnit.MILLISECONDS, createMillis), new Duration(TimeUnit.MILLISECONDS, accessMillis), new Duration(TimeUnit.MILLISECONDS, updateMillis));
    }

    public HazelcastExpiryPolicy(long createDurationAmount, long accessDurationAmount, long updateDurationAmount, TimeUnit timeUnit) {
        this(new Duration(timeUnit, createDurationAmount), new Duration(timeUnit, accessDurationAmount), new Duration(timeUnit, updateDurationAmount));
    }

    public HazelcastExpiryPolicy(ExpiryPolicy expiryPolicy) {
        if (expiryPolicy != null) {
            this.create = expiryPolicy.getExpiryForCreation();
            this.access = expiryPolicy.getExpiryForAccess();
            this.update = expiryPolicy.getExpiryForUpdate();
        }
    }

    public HazelcastExpiryPolicy(Duration create, Duration access, Duration update) {
        this.create = create;
        this.access = access;
        this.update = update;
    }

    public Duration getExpiryForCreation() {
        return this.create;
    }

    public Duration getExpiryForAccess() {
        return this.access;
    }

    public Duration getExpiryForUpdate() {
        return this.update;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 21;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        this.writeDuration(out, this.create);
        this.writeDuration(out, this.access);
        this.writeDuration(out, this.update);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.create = this.readDuration(in);
        this.access = this.readDuration(in);
        this.update = this.readDuration(in);
    }

    private void writeDuration(ObjectDataOutput out, Duration duration) throws IOException {
        if (duration != null) {
            out.writeLong(duration.getDurationAmount());
            out.writeInt(duration.getTimeUnit().ordinal());
        }
    }

    private Duration readDuration(ObjectDataInput in) throws IOException {
        long da = in.readLong();
        if (da > -1L) {
            TimeUnit tu = TimeUnit.values()[in.readInt()];
            return new Duration(tu, da);
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HazelcastExpiryPolicy that = (HazelcastExpiryPolicy)o;
        if (this.create != null ? !this.create.equals((Object)that.create) : that.create != null) {
            return false;
        }
        if (this.access != null ? !this.access.equals((Object)that.access) : that.access != null) {
            return false;
        }
        return !(this.update != null ? !this.update.equals((Object)that.update) : that.update != null);
    }

    public int hashCode() {
        int result = this.create != null ? this.create.hashCode() : 0;
        result = 31 * result + (this.access != null ? this.access.hashCode() : 0);
        result = 31 * result + (this.update != null ? this.update.hashCode() : 0);
        return result;
    }
}

