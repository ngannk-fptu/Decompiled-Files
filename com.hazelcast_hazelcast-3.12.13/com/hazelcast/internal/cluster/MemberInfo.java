/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster;

import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.VersionAware;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.MapUtil;
import com.hazelcast.version.MemberVersion;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemberInfo
implements IdentifiedDataSerializable,
Versioned {
    private Address address;
    private String uuid;
    private boolean liteMember;
    private MemberVersion version;
    private Map<String, Object> attributes;
    private int memberListJoinVersion = -1;
    private Map<EndpointQualifier, Address> addressMap;

    public MemberInfo() {
    }

    public MemberInfo(Address address, String uuid, Map<String, Object> attributes, MemberVersion version) {
        this(address, uuid, attributes, false, version, -1, Collections.emptyMap());
    }

    public MemberInfo(Address address, String uuid, Map<String, Object> attributes, boolean liteMember, MemberVersion version, Map<EndpointQualifier, Address> addressMap) {
        this(address, uuid, attributes, liteMember, version, -1, addressMap);
    }

    public MemberInfo(Address address, String uuid, Map<String, Object> attributes, boolean liteMember, MemberVersion version, int memberListJoinVersion, Map<EndpointQualifier, Address> addressMap) {
        this.address = address;
        this.uuid = uuid;
        this.attributes = attributes == null || attributes.isEmpty() ? Collections.emptyMap() : new HashMap<String, Object>(attributes);
        this.liteMember = liteMember;
        this.version = version;
        this.memberListJoinVersion = memberListJoinVersion;
        this.addressMap = addressMap;
    }

    public MemberInfo(MemberImpl member) {
        this(member.getAddress(), member.getUuid(), member.getAttributes(), member.isLiteMember(), member.getVersion(), member.getMemberListJoinVersion(), member.getAddressMap());
    }

    public Address getAddress() {
        return this.address;
    }

    public MemberVersion getVersion() {
        return this.version;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public boolean isLiteMember() {
        return this.liteMember;
    }

    public int getMemberListJoinVersion() {
        return this.memberListJoinVersion;
    }

    public Map<EndpointQualifier, Address> getAddressMap() {
        return this.addressMap;
    }

    public MemberImpl toMember() {
        return new MemberImpl.Builder(Collections.singletonMap(EndpointQualifier.MEMBER, this.address)).version(this.version).uuid(this.uuid).attributes(this.attributes).liteMember(this.liteMember).memberListJoinVersion(this.memberListJoinVersion).build();
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.address = new Address();
        this.address.readData(in);
        if (in.readBoolean()) {
            this.uuid = in.readUTF();
        }
        this.liteMember = in.readBoolean();
        int size = in.readInt();
        if (size > 0) {
            this.attributes = MapUtil.createHashMap(size);
        }
        for (int i = 0; i < size; ++i) {
            String key = in.readUTF();
            Object value = in.readObject();
            this.attributes.put(key, value);
        }
        this.version = (MemberVersion)in.readObject();
        if (this.mustReadMemberListJoinVersion(in)) {
            this.memberListJoinVersion = in.readInt();
        }
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.addressMap = SerializationUtil.readMap(in);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        this.address.writeData(out);
        boolean hasUuid = this.uuid != null;
        out.writeBoolean(hasUuid);
        if (hasUuid) {
            out.writeUTF(this.uuid);
        }
        out.writeBoolean(this.liteMember);
        out.writeInt(this.attributes == null ? 0 : this.attributes.size());
        if (this.attributes != null) {
            for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeObject(entry.getValue());
            }
        }
        out.writeObject(this.version);
        out.writeInt(this.memberListJoinVersion);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            SerializationUtil.writeMap(this.addressMap, out);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.address == null ? 0 : this.address.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MemberInfo other = (MemberInfo)obj;
        return !(this.address == null ? other.address != null : !this.address.equals(other.address));
    }

    public String toString() {
        return "MemberInfo{address=" + this.address + ", uuid=" + this.uuid + ", liteMember=" + this.liteMember + ", memberListJoinVersion=" + this.memberListJoinVersion + '}';
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 28;
    }

    private boolean mustReadMemberListJoinVersion(VersionAware versionAware) {
        return !BuildInfoProvider.getBuildInfo().isEnterprise() || versionAware.getVersion().isGreaterOrEqual(Versions.V3_10);
    }
}

