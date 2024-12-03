/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.IdentifiedDataSerializable
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.serialization.ExpiryMarker;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Comparator;

public abstract class Expirable
implements IdentifiedDataSerializable {
    protected Object version;

    protected Expirable() {
    }

    protected Expirable(Object version) {
        this.version = version;
    }

    public abstract boolean isReplaceableBy(long var1, Object var3, Comparator var4);

    public abstract Object getValue();

    public abstract Object getValue(long var1);

    public Object getVersion() {
        return this.version;
    }

    public abstract boolean matches(ExpiryMarker var1);

    public abstract ExpiryMarker markForExpiration(long var1, String var3);

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.version);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.version = in.readObject();
    }
}

