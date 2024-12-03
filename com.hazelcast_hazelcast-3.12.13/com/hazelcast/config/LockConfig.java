/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class LockConfig
implements IdentifiedDataSerializable,
NamedConfig {
    private String name;
    private String quorumName;

    public LockConfig() {
    }

    public LockConfig(String name) {
        this.name = Preconditions.checkNotNull(name, "name can't be null");
    }

    public LockConfig(LockConfig config) {
        Preconditions.checkNotNull(config, "config can't be null");
        this.name = config.name;
        this.quorumName = config.quorumName;
    }

    public LockConfig(String name, LockConfig config) {
        this(config);
        this.name = Preconditions.checkNotNull(name, "name can't be null");
    }

    @Override
    public LockConfig setName(String name) {
        this.name = Preconditions.checkHasText(name, "name must contain text");
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public LockConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public LockConfig getAsReadOnly() {
        return new LockConfigReadOnly(this);
    }

    public String toString() {
        return "LockConfig{name='" + this.name + '\'' + ", quorumName='" + this.quorumName + '\'' + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 27;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.quorumName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.quorumName = in.readUTF();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LockConfig)) {
            return false;
        }
        LockConfig that = (LockConfig)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return this.quorumName != null ? this.quorumName.equals(that.quorumName) : that.quorumName == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        return result;
    }

    private static class LockConfigReadOnly
    extends LockConfig {
        LockConfigReadOnly(LockConfig config) {
            super(config);
        }

        @Override
        public LockConfig setName(String name) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public LockConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This config is read-only");
        }
    }
}

