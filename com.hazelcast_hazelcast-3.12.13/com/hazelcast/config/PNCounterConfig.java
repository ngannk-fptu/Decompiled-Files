/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class PNCounterConfig
implements IdentifiedDataSerializable,
NamedConfig {
    public static final int DEFAULT_REPLICA_COUNT = Integer.MAX_VALUE;
    public static final boolean DEFAULT_STATISTICS_ENABLED = true;
    private String name = "default";
    private int replicaCount = Integer.MAX_VALUE;
    private String quorumName;
    private boolean statisticsEnabled = true;
    private transient PNCounterConfigReadOnly readOnly;

    public PNCounterConfig() {
    }

    public PNCounterConfig(String name, int replicaCount, String quorumName, boolean statisticsEnabled) {
        this.name = name;
        this.replicaCount = replicaCount;
        this.quorumName = quorumName;
        this.statisticsEnabled = statisticsEnabled;
    }

    public PNCounterConfig(String name) {
        this.name = name;
    }

    public PNCounterConfig(PNCounterConfig config) {
        this(config.getName(), config.getReplicaCount(), config.getQuorumName(), config.isStatisticsEnabled());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PNCounterConfig setName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public PNCounterConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public int getReplicaCount() {
        return this.replicaCount;
    }

    public PNCounterConfig setReplicaCount(int replicaCount) {
        if (replicaCount < 1) {
            throw new ConfigurationException("Replica count must be greater or equal to 1");
        }
        this.replicaCount = replicaCount;
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public PNCounterConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    PNCounterConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new PNCounterConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 53;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.replicaCount);
        out.writeBoolean(this.statisticsEnabled);
        out.writeUTF(this.quorumName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.replicaCount = in.readInt();
        this.statisticsEnabled = in.readBoolean();
        this.quorumName = in.readUTF();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PNCounterConfig that = (PNCounterConfig)o;
        if (this.replicaCount != that.replicaCount) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        return this.quorumName != null ? this.quorumName.equals(that.quorumName) : that.quorumName == null;
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.replicaCount;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        return result;
    }

    static class PNCounterConfigReadOnly
    extends PNCounterConfig {
        PNCounterConfigReadOnly(PNCounterConfig config) {
            super(config);
        }

        @Override
        public PNCounterConfig setName(String name) {
            throw new UnsupportedOperationException("This config is read-only PN counter: " + this.getName());
        }

        @Override
        public PNCounterConfig setReplicaCount(int replicaCount) {
            throw new UnsupportedOperationException("This config is read-only PN counter: " + this.getName());
        }

        @Override
        public PNCounterConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This config is read-only PN counter: " + this.getName());
        }

        @Override
        public PNCounterConfig setStatisticsEnabled(boolean statisticsEnabled) {
            throw new UnsupportedOperationException("This config is read-only PN counter: " + this.getName());
        }
    }
}

