/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.version.MemberVersion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JoinMessage
implements IdentifiedDataSerializable {
    protected byte packetVersion;
    protected int buildNumber;
    protected MemberVersion memberVersion;
    protected Address address;
    protected String uuid;
    protected boolean liteMember;
    protected ConfigCheck configCheck;
    protected Collection<Address> memberAddresses;
    protected int dataMemberCount;

    public JoinMessage() {
    }

    public JoinMessage(byte packetVersion, int buildNumber, MemberVersion memberVersion, Address address, String uuid, boolean liteMember, ConfigCheck configCheck) {
        this(packetVersion, buildNumber, memberVersion, address, uuid, liteMember, configCheck, Collections.emptySet(), 0);
    }

    public JoinMessage(byte packetVersion, int buildNumber, MemberVersion memberVersion, Address address, String uuid, boolean liteMember, ConfigCheck configCheck, Collection<Address> memberAddresses, int dataMemberCount) {
        this.packetVersion = packetVersion;
        this.buildNumber = buildNumber;
        this.memberVersion = memberVersion;
        this.address = address;
        this.uuid = uuid;
        this.liteMember = liteMember;
        this.configCheck = configCheck;
        this.memberAddresses = memberAddresses;
        this.dataMemberCount = dataMemberCount;
    }

    public byte getPacketVersion() {
        return this.packetVersion;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public MemberVersion getMemberVersion() {
        return this.memberVersion;
    }

    public Address getAddress() {
        return this.address;
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean isLiteMember() {
        return this.liteMember;
    }

    public ConfigCheck getConfigCheck() {
        return this.configCheck;
    }

    public int getMemberCount() {
        return this.memberAddresses != null ? this.memberAddresses.size() : 0;
    }

    public Collection<Address> getMemberAddresses() {
        return this.memberAddresses != null ? this.memberAddresses : Collections.emptySet();
    }

    public int getDataMemberCount() {
        return this.dataMemberCount;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.packetVersion = in.readByte();
        this.buildNumber = in.readInt();
        this.memberVersion = (MemberVersion)in.readObject();
        this.address = new Address();
        this.address.readData(in);
        this.uuid = in.readUTF();
        this.configCheck = new ConfigCheck();
        this.configCheck.readData(in);
        this.liteMember = in.readBoolean();
        int memberCount = in.readInt();
        this.memberAddresses = new ArrayList<Address>(memberCount);
        for (int i = 0; i < memberCount; ++i) {
            Address member = new Address();
            member.readData(in);
            this.memberAddresses.add(member);
        }
        this.dataMemberCount = in.readInt();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByte(this.packetVersion);
        out.writeInt(this.buildNumber);
        out.writeObject(this.memberVersion);
        this.address.writeData(out);
        out.writeUTF(this.uuid);
        this.configCheck.writeData(out);
        out.writeBoolean(this.liteMember);
        int memberCount = this.getMemberCount();
        out.writeInt(memberCount);
        if (memberCount > 0) {
            for (Address member : this.memberAddresses) {
                member.writeData(out);
            }
        }
        out.writeInt(this.dataMemberCount);
    }

    public String toString() {
        return "JoinMessage{packetVersion=" + this.packetVersion + ", buildNumber=" + this.buildNumber + ", memberVersion=" + this.memberVersion + ", address=" + this.address + ", uuid='" + this.uuid + '\'' + ", liteMember=" + this.liteMember + ", memberCount=" + this.getMemberCount() + ", dataMemberCount=" + this.dataMemberCount + '}';
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 29;
    }
}

