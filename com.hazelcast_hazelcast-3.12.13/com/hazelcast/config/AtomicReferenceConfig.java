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

public class AtomicReferenceConfig
extends AbstractBasicConfig<AtomicReferenceConfig> {
    private transient AtomicReferenceConfigReadOnly readOnly;

    AtomicReferenceConfig() {
    }

    public AtomicReferenceConfig(String name) {
        super(name);
    }

    public AtomicReferenceConfig(AtomicReferenceConfig config) {
        super(config);
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.AtomicReferenceMergeTypes.class;
    }

    @Override
    public int getId() {
        return 50;
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
        if (!(o instanceof AtomicReferenceConfig)) {
            return false;
        }
        AtomicReferenceConfig that = (AtomicReferenceConfig)o;
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
    public AtomicReferenceConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new AtomicReferenceConfigReadOnly(this);
        }
        return this.readOnly;
    }

    static class AtomicReferenceConfigReadOnly
    extends AtomicReferenceConfig {
        AtomicReferenceConfigReadOnly(AtomicReferenceConfig config) {
            super(config);
        }

        @Override
        public AtomicReferenceConfig setName(String name) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }

        @Override
        public AtomicReferenceConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }

        @Override
        public AtomicReferenceConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
            throw new UnsupportedOperationException("This is a read-only config!");
        }
    }
}

