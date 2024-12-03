/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.Preconditions;
import com.hazelcast.version.MemberVersion;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PrivateApi
public abstract class AbstractMember
implements Member,
Versioned {
    protected final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    protected Address address;
    protected Map<EndpointQualifier, Address> addressMap;
    protected String uuid;
    protected boolean liteMember;
    protected MemberVersion version;

    protected AbstractMember() {
    }

    protected AbstractMember(Map<EndpointQualifier, Address> addresses, MemberVersion version, String uuid, Map<String, Object> attributes, boolean liteMember) {
        this.address = addresses.get(EndpointQualifier.MEMBER);
        this.addressMap = addresses;
        assert (this.address != null) : "Address is required!";
        this.version = version;
        String string = this.uuid = uuid != null ? uuid : "<" + this.address.toString() + ">";
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
        this.liteMember = liteMember;
    }

    protected AbstractMember(AbstractMember member) {
        this.address = member.address;
        this.addressMap = member.addressMap;
        this.version = member.version;
        this.uuid = member.uuid;
        this.attributes.putAll(member.attributes);
        this.liteMember = member.liteMember;
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public Map<EndpointQualifier, Address> getAddressMap() {
        return this.addressMap;
    }

    public int getPort() {
        return this.address.getPort();
    }

    public InetAddress getInetAddress() {
        try {
            return this.address.getInetAddress();
        }
        catch (UnknownHostException e) {
            if (this.getLogger() != null) {
                this.getLogger().warning(e);
            }
            return null;
        }
    }

    protected abstract ILogger getLogger();

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return this.getSocketAddress();
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return this.getSocketAddress(EndpointQualifier.MEMBER);
    }

    @Override
    public InetSocketAddress getSocketAddress(EndpointQualifier qualifier) {
        Address addr = this.addressMap.get(qualifier);
        if (addr == null && !qualifier.getType().equals((Object)ProtocolType.MEMBER)) {
            addr = this.addressMap.get(EndpointQualifier.MEMBER);
        }
        Preconditions.checkNotNull(addr);
        try {
            return addr.getInetSocketAddress();
        }
        catch (UnknownHostException e) {
            if (this.getLogger() != null) {
                this.getLogger().warning(e);
            }
            return null;
        }
    }

    void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public boolean isLiteMember() {
        return this.liteMember;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public void updateAttribute(MemberAttributeOperationType operationType, String key, Object value) {
        switch (operationType) {
            case PUT: {
                this.attributes.put(key, value);
                break;
            }
            case REMOVE: {
                this.attributes.remove(key);
                break;
            }
            default: {
                throw new IllegalArgumentException("Not a known OperationType " + (Object)((Object)operationType));
            }
        }
    }

    protected Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    public MemberVersion getVersion() {
        return this.version;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.address = new Address();
        this.address.readData(in);
        this.uuid = in.readUTF();
        this.liteMember = in.readBoolean();
        this.version = (MemberVersion)in.readObject();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            String key = in.readUTF();
            Object value = IOUtil.readAttributeValue(in);
            this.attributes.put(key, value);
        }
        this.addressMap = this.readAddressMap(in);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        this.address.writeData(out);
        out.writeUTF(this.uuid);
        out.writeBoolean(this.liteMember);
        out.writeObject(this.version);
        HashMap<String, Object> attributes = new HashMap<String, Object>(this.attributes);
        out.writeInt(attributes.size());
        for (Map.Entry entry : attributes.entrySet()) {
            out.writeUTF((String)entry.getKey());
            IOUtil.writeAttributeValue(entry.getValue(), out);
        }
        this.writeAddressMap(out);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Member [");
        sb.append(this.address.getHost());
        sb.append("]");
        sb.append(":");
        sb.append(this.address.getPort());
        sb.append(" - ").append(this.uuid);
        if (this.localMember()) {
            sb.append(" this");
        }
        if (this.isLiteMember()) {
            sb.append(" lite");
        }
        return sb.toString();
    }

    public int hashCode() {
        int result = this.address.hashCode();
        result = 31 * result + this.uuid.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Member)) {
            return false;
        }
        Member that = (Member)obj;
        return this.address.equals(that.getAddress()) && this.uuid.equals(that.getUuid());
    }

    private void writeAddressMap(ObjectDataOutput out) throws IOException {
        if (out.getVersion().isUnknownOrLessThan(Versions.V3_12)) {
            return;
        }
        SerializationUtil.writeNullableMap(this.addressMap, out);
    }

    private Map<EndpointQualifier, Address> readAddressMap(ObjectDataInput in) throws IOException {
        if (in.getVersion().isUnknownOrLessThan(Versions.V3_12)) {
            return null;
        }
        return SerializationUtil.readNullableMap(in);
    }
}

