/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class MembersViewMetadata
implements IdentifiedDataSerializable {
    private Address memberAddress;
    private String memberUuid;
    private Address masterAddress;
    private int memberListVersion;

    public MembersViewMetadata() {
    }

    public MembersViewMetadata(Address memberAddress, String memberUuid, Address masterAddress, int memberListVersion) {
        this.memberAddress = memberAddress;
        this.memberUuid = memberUuid;
        this.masterAddress = masterAddress;
        this.memberListVersion = memberListVersion;
    }

    public Address getMemberAddress() {
        return this.memberAddress;
    }

    public String getMemberUuid() {
        return this.memberUuid;
    }

    public Address getMasterAddress() {
        return this.masterAddress;
    }

    public int getMemberListVersion() {
        return this.memberListVersion;
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 40;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.memberAddress);
        out.writeUTF(this.memberUuid);
        out.writeObject(this.masterAddress);
        out.writeInt(this.memberListVersion);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.memberAddress = (Address)in.readObject();
        this.memberUuid = in.readUTF();
        this.masterAddress = (Address)in.readObject();
        this.memberListVersion = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MembersViewMetadata that = (MembersViewMetadata)o;
        if (this.memberListVersion != that.memberListVersion) {
            return false;
        }
        if (!this.memberAddress.equals(that.memberAddress)) {
            return false;
        }
        if (!this.memberUuid.equals(that.memberUuid)) {
            return false;
        }
        return this.masterAddress.equals(that.masterAddress);
    }

    public int hashCode() {
        int result = this.memberAddress.hashCode();
        result = 31 * result + this.memberUuid.hashCode();
        result = 31 * result + this.masterAddress.hashCode();
        result = 31 * result + this.memberListVersion;
        return result;
    }

    public String toString() {
        return "MembersViewMetadata{address=" + this.memberAddress + ", memberUuid='" + this.memberUuid + '\'' + ", masterAddress=" + this.masterAddress + ", memberListVersion=" + this.memberListVersion + '}';
    }
}

