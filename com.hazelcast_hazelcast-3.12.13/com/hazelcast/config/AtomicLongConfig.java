/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractBasicConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.io.IOException;

public class AtomicLongConfig
extends AbstractBasicConfig<AtomicLongConfig> {
    private transient AtomicLongConfigReadOnly readOnly;

    AtomicLongConfig() {
    }

    public AtomicLongConfig(String name) {
        super(name);
    }

    public AtomicLongConfig(AtomicLongConfig config) {
        super(config);
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.AtomicLongMergeTypes.class;
    }

    @Override
    public int getId() {
        return 49;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AtomicLongConfig)) {
            return false;
        }
        AtomicLongConfig that = (AtomicLongConfig)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.mergePolicyConfig.equals(that.mergePolicyConfig)) {
            return false;
        }
        return this.quorumName != null ? this.quorumName.equals(that.quorumName) : that.quorumName == null;
    }

    public final int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.mergePolicyConfig.hashCode();
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        return result;
    }

    @Override
    public AtomicLongConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new AtomicLongConfigReadOnly(this);
        }
        return this.readOnly;
    }

    static class AtomicLongConfigReadOnly
    extends AtomicLongConfig {
        AtomicLongConfigReadOnly(AtomicLongConfig config) {
            super(config);
        }

        @Override
        public AtomicLongConfig setName(String name) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }

        @Override
        public AtomicLongConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }

        @Override
        public AtomicLongConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }
    }
}

