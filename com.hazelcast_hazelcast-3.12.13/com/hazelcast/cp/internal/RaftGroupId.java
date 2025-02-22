/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class RaftGroupId
implements CPGroupId,
IdentifiedDataSerializable,
Serializable {
    private static final long serialVersionUID = -2381010126931378167L;
    private String name;
    private long seed;
    private long commitIndex;

    public RaftGroupId() {
    }

    public RaftGroupId(String name, long seed, long commitIndex) {
        assert (name != null);
        this.name = name;
        this.seed = seed;
        this.commitIndex = commitIndex;
    }

    @Override
    public String name() {
        return this.name;
    }

    public long seed() {
        return this.seed;
    }

    @Override
    public long id() {
        return this.commitIndex;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.seed);
        out.writeLong(this.commitIndex);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.seed = in.readLong();
        this.commitIndex = in.readLong();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this.name);
        out.writeLong(this.seed);
        out.writeLong(this.commitIndex);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.name = in.readUTF();
        this.seed = in.readLong();
        this.commitIndex = in.readLong();
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RaftGroupId that = (RaftGroupId)o;
        if (this.seed != that.seed) {
            return false;
        }
        if (this.commitIndex != that.commitIndex) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + (int)(this.seed ^ this.seed >>> 32);
        result = 31 * result + (int)(this.commitIndex ^ this.commitIndex >>> 32);
        return result;
    }

    public String toString() {
        return "CPGroupId{name='" + this.name + '\'' + ", seed=" + this.seed + ", commitIndex=" + this.commitIndex + '}';
    }
}

