/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WanConsumerConfig
implements IdentifiedDataSerializable,
Versioned {
    public static final boolean DEFAULT_PERSIST_WAN_REPLICATED_DATA = false;
    private boolean persistWanReplicatedData = false;
    private String className;
    private Object implementation;
    private Map<String, Comparable> properties = new HashMap<String, Comparable>();

    public Map<String, Comparable> getProperties() {
        return this.properties;
    }

    public WanConsumerConfig setProperties(Map<String, Comparable> properties) {
        this.properties = properties;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public WanConsumerConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Object getImplementation() {
        return this.implementation;
    }

    public WanConsumerConfig setImplementation(Object implementation) {
        this.implementation = implementation;
        return this;
    }

    public boolean isPersistWanReplicatedData() {
        return this.persistWanReplicatedData;
    }

    public WanConsumerConfig setPersistWanReplicatedData(boolean persistWanReplicatedData) {
        this.persistWanReplicatedData = persistWanReplicatedData;
        return this;
    }

    public String toString() {
        return "WanConsumerConfig{properties=" + this.properties + ", className='" + this.className + '\'' + ", implementation=" + this.implementation + ", persistWanReplicatedData=" + this.persistWanReplicatedData + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int size = this.properties.size();
        out.writeInt(size);
        for (Map.Entry<String, Comparable> entry : this.properties.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeUTF(this.className);
        out.writeObject(this.implementation);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeBoolean(this.persistWanReplicatedData);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.properties.put(in.readUTF(), (Comparable)in.readObject());
        }
        this.className = in.readUTF();
        this.implementation = in.readObject();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.persistWanReplicatedData = in.readBoolean();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WanConsumerConfig that = (WanConsumerConfig)o;
        if (this.persistWanReplicatedData != that.persistWanReplicatedData) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        return this.properties != null ? this.properties.equals(that.properties) : that.properties == null;
    }

    public int hashCode() {
        int result = this.persistWanReplicatedData ? 1 : 0;
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        return result;
    }
}

