/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.Collection;

public class SplitBrainJoinMessage
extends JoinMessage
implements Versioned {
    private Version clusterVersion;
    private int memberListVersion;

    public SplitBrainJoinMessage() {
    }

    public SplitBrainJoinMessage(byte packetVersion, int buildNumber, MemberVersion version, Address address, String uuid, boolean liteMember, ConfigCheck configCheck, Collection<Address> memberAddresses, int dataMemberCount, Version clusterVersion, int memberListVersion) {
        super(packetVersion, buildNumber, version, address, uuid, liteMember, configCheck, memberAddresses, dataMemberCount);
        this.clusterVersion = clusterVersion;
        this.memberListVersion = memberListVersion;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.clusterVersion = (Version)in.readObject();
        this.memberListVersion = in.readInt();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.clusterVersion);
        out.writeInt(this.memberListVersion);
    }

    @Override
    public String toString() {
        return "SplitBrainJoinMessage{packetVersion=" + this.packetVersion + ", buildNumber=" + this.buildNumber + ", memberVersion=" + this.memberVersion + ", clusterVersion=" + this.clusterVersion + ", address=" + this.address + ", uuid='" + this.uuid + '\'' + ", liteMember=" + this.liteMember + ", memberCount=" + this.getMemberCount() + ", dataMemberCount=" + this.dataMemberCount + ", memberListVersion=" + this.memberListVersion + '}';
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 34;
    }

    public Version getClusterVersion() {
        return this.clusterVersion;
    }

    public int getMemberListVersion() {
        return this.memberListVersion;
    }

    public static enum SplitBrainMergeCheckResult {
        CANNOT_MERGE,
        LOCAL_NODE_SHOULD_MERGE,
        REMOTE_NODE_SHOULD_MERGE;

    }
}

