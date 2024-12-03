/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.util.Preconditions;

public abstract class AbstractBasicConfig<T extends AbstractBasicConfig>
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
NamedConfig {
    protected String name;
    protected String quorumName;
    protected MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();

    protected AbstractBasicConfig() {
    }

    protected AbstractBasicConfig(String name) {
        this.name = name;
    }

    protected AbstractBasicConfig(AbstractBasicConfig config) {
        this.name = config.name;
        this.quorumName = config.quorumName;
        this.mergePolicyConfig = config.mergePolicyConfig;
    }

    abstract T getAsReadOnly();

    @Override
    public String getName() {
        return this.name;
    }

    public T setName(String name) {
        this.name = Preconditions.checkNotNull(name, "name cannot be null");
        return (T)this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public T setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null");
        return (T)this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public T setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return (T)this;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{name='" + this.name + '\'' + ", quorumName=" + this.quorumName + ", mergePolicyConfig=" + this.mergePolicyConfig + "}";
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }
}

