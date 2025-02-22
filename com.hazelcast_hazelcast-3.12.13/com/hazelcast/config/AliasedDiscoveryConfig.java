/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AliasedDiscoveryConfig<T extends AliasedDiscoveryConfig<T>>
implements IdentifiedDataSerializable {
    private static final String USE_PUBLIC_IP_PROPERTY = "use-public-ip";
    private static final String ENABLED_PROPERTY = "enabled";
    private final String tag;
    private boolean enabled;
    private boolean usePublicIp;
    private final Map<String, String> properties;

    protected AliasedDiscoveryConfig(String tag) {
        this.tag = tag;
        this.properties = new HashMap<String, String>();
    }

    public AliasedDiscoveryConfig(AliasedDiscoveryConfig aliasedDiscoveryConfig) {
        this.tag = aliasedDiscoveryConfig.tag;
        this.enabled = aliasedDiscoveryConfig.enabled;
        this.usePublicIp = aliasedDiscoveryConfig.usePublicIp;
        this.properties = new HashMap<String, String>();
        this.properties.putAll(aliasedDiscoveryConfig.properties);
    }

    public T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (T)this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public T setProperty(String name, String value) {
        if (USE_PUBLIC_IP_PROPERTY.equals(name)) {
            this.usePublicIp = Boolean.parseBoolean(value);
        } else if (ENABLED_PROPERTY.equals(name)) {
            this.enabled = Boolean.parseBoolean(value);
        } else {
            this.properties.put(name, value);
        }
        return (T)this;
    }

    public String getProperty(String name) {
        return this.properties.get(name);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public T setUsePublicIp(boolean usePublicIp) {
        this.usePublicIp = usePublicIp;
        return (T)this;
    }

    public boolean isUsePublicIp() {
        return this.usePublicIp;
    }

    public String getTag() {
        return this.tag;
    }

    public String toString() {
        return "AliasedDiscoveryConfig{tag='" + this.tag + '\'' + ", enabled=" + this.enabled + ", usePublicIp=" + this.usePublicIp + ", properties=" + this.properties + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.enabled);
        out.writeBoolean(this.usePublicIp);
        out.writeInt(this.properties.size());
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.enabled = in.readBoolean();
        this.usePublicIp = in.readBoolean();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.properties.put(in.readUTF(), in.readUTF());
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AliasedDiscoveryConfig that = (AliasedDiscoveryConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.usePublicIp != that.usePublicIp) {
            return false;
        }
        if (!this.tag.equals(that.tag)) {
            return false;
        }
        return this.properties.equals(that.properties);
    }

    public int hashCode() {
        int result = this.tag.hashCode();
        result = 31 * result + (this.enabled ? 1 : 0);
        result = 31 * result + (this.usePublicIp ? 1 : 0);
        result = 31 * result + this.properties.hashCode();
        return result;
    }
}

