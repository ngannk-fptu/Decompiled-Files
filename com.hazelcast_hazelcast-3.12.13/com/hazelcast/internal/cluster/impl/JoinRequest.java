/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.security.Credentials;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import com.hazelcast.version.MemberVersion;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JoinRequest
extends JoinMessage {
    private Credentials credentials;
    private int tryCount;
    private Map<String, Object> attributes;
    private Set<String> excludedMemberUuids = Collections.emptySet();
    private Map<EndpointQualifier, Address> addresses;

    public JoinRequest() {
    }

    public JoinRequest(byte packetVersion, int buildNumber, MemberVersion version, Address address, String uuid, boolean liteMember, ConfigCheck config, Credentials credentials, Map<String, Object> attributes, Set<String> excludedMemberUuids, Map<EndpointQualifier, Address> addresses) {
        super(packetVersion, buildNumber, version, address, uuid, liteMember, config);
        this.credentials = credentials;
        this.attributes = attributes;
        if (excludedMemberUuids != null) {
            this.excludedMemberUuids = Collections.unmodifiableSet(new HashSet<String>(excludedMemberUuids));
        }
        this.addresses = addresses;
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public int getTryCount() {
        return this.tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Set<String> getExcludedMemberUuids() {
        return this.excludedMemberUuids;
    }

    public MemberInfo toMemberInfo() {
        return new MemberInfo(this.address, this.uuid, this.attributes, this.liteMember, this.memberVersion, this.addresses);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.credentials = (Credentials)in.readObject();
        if (this.credentials != null) {
            this.credentials.setEndpoint(this.getAddress().getHost());
        }
        this.tryCount = in.readInt();
        int size = in.readInt();
        this.attributes = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String key = in.readUTF();
            Object value = in.readObject();
            this.attributes.put(key, value);
        }
        size = in.readInt();
        Set<String> excludedMemberUuids = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            excludedMemberUuids.add(in.readUTF());
        }
        this.excludedMemberUuids = Collections.unmodifiableSet(excludedMemberUuids);
        if (this.memberVersion.asVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.addresses = SerializationUtil.readMap(in);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.credentials);
        out.writeInt(this.tryCount);
        out.writeInt(this.attributes.size());
        for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeInt(this.excludedMemberUuids.size());
        for (String uuid : this.excludedMemberUuids) {
            out.writeUTF(uuid);
        }
        SerializationUtil.writeMap(this.addresses, out);
    }

    @Override
    public String toString() {
        return "JoinRequest{packetVersion=" + this.packetVersion + ", buildNumber=" + this.buildNumber + ", memberVersion=" + this.memberVersion + ", address=" + this.address + ", uuid='" + this.uuid + "', liteMember=" + this.liteMember + ", credentials=" + this.credentials + ", memberCount=" + this.getMemberCount() + ", tryCount=" + this.tryCount + (this.excludedMemberUuids.size() > 0 ? ", excludedMemberUuids=" + this.excludedMemberUuids : "") + '}';
    }

    @Override
    public int getId() {
        return 30;
    }
}

