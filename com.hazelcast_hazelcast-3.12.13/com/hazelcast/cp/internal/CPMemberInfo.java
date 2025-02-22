/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.core.Member;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class CPMemberInfo
implements CPMember,
Serializable,
IdentifiedDataSerializable {
    private static final long serialVersionUID = 5628148969327743953L;
    private transient UUID uuid;
    private transient String uuidString;
    private transient Address address;

    public CPMemberInfo() {
    }

    public CPMemberInfo(UUID uuid, Address address) {
        this.uuid = uuid;
        this.uuidString = uuid.toString();
        this.address = address;
    }

    public CPMemberInfo(Member member) {
        this(UUID.fromString(member.getUuid()), member.getAddress());
    }

    @Override
    public String getUuid() {
        return this.uuidString;
    }

    @Override
    public SocketAddress getSocketAddress() {
        try {
            return this.address.getInetSocketAddress();
        }
        catch (UnknownHostException e) {
            return null;
        }
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        UUIDSerializationUtil.writeUUID(out, this.uuid);
        out.writeUTF(this.address.getHost());
        out.writeInt(this.address.getPort());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.uuid = UUIDSerializationUtil.readUUID(in);
        this.uuidString = this.uuid.toString();
        String host = in.readUTF();
        int port = in.readInt();
        this.address = new Address(host, port);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        UUIDSerializationUtil.writeUUID(out, this.uuid);
        out.writeObject(this.address);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.uuid = UUIDSerializationUtil.readUUID(in);
        this.uuidString = this.uuid.toString();
        this.address = (Address)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 31;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CPMemberInfo)) {
            return false;
        }
        CPMemberInfo that = (CPMemberInfo)o;
        if (this.uuid != null ? !this.uuid.equals(that.uuid) : that.uuid != null) {
            return false;
        }
        return this.address != null ? this.address.equals(that.address) : that.address == null;
    }

    public int hashCode() {
        int result = this.uuid != null ? this.uuid.hashCode() : 0;
        result = 31 * result + (this.address != null ? this.address.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "CPMember{uuid=" + this.uuidString + ", address=" + this.address + '}';
    }
}

