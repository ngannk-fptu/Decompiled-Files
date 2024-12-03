/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ExtendedBindMessage
implements IdentifiedDataSerializable {
    private byte schemaVersion;
    private Map<ProtocolType, Collection<Address>> localAddresses;
    private Address targetAddress;
    private boolean reply;

    public ExtendedBindMessage() {
    }

    public ExtendedBindMessage(byte schemaVersion, Map<ProtocolType, Collection<Address>> localAddresses, Address targetAddress, boolean reply) {
        this.schemaVersion = schemaVersion;
        this.localAddresses = new EnumMap<ProtocolType, Collection<Address>>(localAddresses);
        this.targetAddress = targetAddress;
        this.reply = reply;
    }

    byte getSchemaVersion() {
        return this.schemaVersion;
    }

    public Map<ProtocolType, Collection<Address>> getLocalAddresses() {
        return this.localAddresses;
    }

    public Address getTargetAddress() {
        return this.targetAddress;
    }

    public boolean isReply() {
        return this.reply;
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 44;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByte(this.schemaVersion);
        out.writeObject(this.targetAddress);
        out.writeBoolean(this.reply);
        int size = this.localAddresses == null ? 0 : this.localAddresses.size();
        out.writeInt(size);
        if (size == 0) {
            return;
        }
        for (Map.Entry<ProtocolType, Collection<Address>> addressEntry : this.localAddresses.entrySet()) {
            out.writeInt(addressEntry.getKey().ordinal());
            SerializationUtil.writeCollection(addressEntry.getValue(), out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.schemaVersion = in.readByte();
        this.targetAddress = (Address)in.readObject();
        this.reply = in.readBoolean();
        int size = in.readInt();
        if (size == 0) {
            this.localAddresses = Collections.emptyMap();
            return;
        }
        EnumMap<ProtocolType, Collection<Address>> addressesPerProtocolType = new EnumMap<ProtocolType, Collection<Address>>(ProtocolType.class);
        for (int i = 0; i < size; ++i) {
            ProtocolType protocolType = ProtocolType.valueOf(in.readInt());
            Collection addresses = SerializationUtil.readCollection(in);
            addressesPerProtocolType.put(protocolType, addresses);
        }
        this.localAddresses = addressesPerProtocolType;
    }

    public String toString() {
        return "ExtendedBindMessage{schemaVersion=" + this.schemaVersion + ", localAddresses=" + this.localAddresses + ", targetAddress=" + this.targetAddress + ", reply=" + this.reply + '}';
    }
}

