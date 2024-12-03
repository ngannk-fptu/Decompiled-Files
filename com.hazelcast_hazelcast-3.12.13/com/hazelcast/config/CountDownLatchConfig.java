/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class CountDownLatchConfig
implements IdentifiedDataSerializable,
Versioned,
NamedConfig {
    private transient CountDownLatchConfigReadOnly readOnly;
    private String name;
    private String quorumName;

    public CountDownLatchConfig() {
    }

    public CountDownLatchConfig(String name) {
        this.setName(name);
    }

    public CountDownLatchConfig(CountDownLatchConfig config) {
        Preconditions.isNotNull(config, "config");
        this.name = config.getName();
        this.quorumName = config.getQuorumName();
    }

    public CountDownLatchConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new CountDownLatchConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CountDownLatchConfig setName(String name) {
        this.name = Preconditions.checkHasText(name, "name must contain text");
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public CountDownLatchConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String toString() {
        return "CountDownLatchConfig{name='" + this.name + '\'' + ", quorumName=" + this.quorumName + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 52;
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
        if (!(o instanceof CountDownLatchConfig)) {
            return false;
        }
        CountDownLatchConfig that = (CountDownLatchConfig)o;
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.name != null ? this.name.equals(that.name) : that.name == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        return result;
    }

    static class CountDownLatchConfigReadOnly
    extends CountDownLatchConfig {
        CountDownLatchConfigReadOnly(CountDownLatchConfig config) {
            super(config);
        }

        @Override
        public CountDownLatchConfig setName(String name) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }

        @Override
        public CountDownLatchConfig setQuorumName(String name) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }
    }
}

