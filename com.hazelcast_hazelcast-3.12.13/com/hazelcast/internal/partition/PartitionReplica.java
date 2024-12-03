/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.core.Member;
import com.hazelcast.internal.partition.impl.PartitionDataSerializerHook;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public final class PartitionReplica
implements IdentifiedDataSerializable {
    public static final String UNKNOWN_UID = "<unknown-uuid>";
    private Address address;
    private String uuid;

    public PartitionReplica() {
    }

    public PartitionReplica(Address address, String uuid) {
        assert (address != null);
        assert (uuid != null);
        this.address = address;
        this.uuid = uuid;
    }

    public Address address() {
        return this.address;
    }

    public String uuid() {
        return this.uuid;
    }

    public boolean isIdentical(Member member) {
        return this.address.equals(member.getAddress()) && this.uuid.equals(member.getUuid());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartitionReplica)) {
            return false;
        }
        PartitionReplica replica = (PartitionReplica)o;
        if (!this.address.equals(replica.address)) {
            return false;
        }
        return this.uuid.equals(replica.uuid);
    }

    public int hashCode() {
        int result = this.address.hashCode();
        result = 31 * result + this.uuid.hashCode();
        return result;
    }

    public String toString() {
        return "[" + this.address.getHost() + "]:" + this.address.getPort() + " - " + this.uuid;
    }

    @Override
    public int getFactoryId() {
        return PartitionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 21;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.address);
        out.writeUTF(this.uuid);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.address = (Address)in.readObject();
        this.uuid = in.readUTF();
    }

    public static PartitionReplica from(Member member) {
        return new PartitionReplica(member.getAddress(), member.getUuid());
    }

    public static PartitionReplica[] from(Member[] members) {
        PartitionReplica[] replicas = new PartitionReplica[members.length];
        for (int i = 0; i < members.length; ++i) {
            replicas[i] = PartitionReplica.from(members[i]);
        }
        return replicas;
    }
}

