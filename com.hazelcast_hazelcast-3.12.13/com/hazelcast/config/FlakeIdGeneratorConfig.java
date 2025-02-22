/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.FlakeIdGeneratorConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Arrays;

public class FlakeIdGeneratorConfig
implements IdentifiedDataSerializable {
    public static final int DEFAULT_PREFETCH_COUNT = 100;
    public static final long DEFAULT_PREFETCH_VALIDITY_MILLIS = 600000L;
    public static final int MAXIMUM_PREFETCH_COUNT = 100000;
    private String name;
    private int prefetchCount = 100;
    private long prefetchValidityMillis = 600000L;
    private long idOffset;
    private long nodeIdOffset;
    private boolean statisticsEnabled = true;
    private transient FlakeIdGeneratorConfigReadOnly readOnly;

    FlakeIdGeneratorConfig() {
    }

    public FlakeIdGeneratorConfig(String name) {
        this.name = name;
    }

    public FlakeIdGeneratorConfig(FlakeIdGeneratorConfig other) {
        this.name = other.name;
        this.prefetchCount = other.prefetchCount;
        this.prefetchValidityMillis = other.prefetchValidityMillis;
        this.idOffset = other.idOffset;
        this.nodeIdOffset = other.nodeIdOffset;
        this.statisticsEnabled = other.statisticsEnabled;
    }

    public FlakeIdGeneratorConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new FlakeIdGeneratorConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrefetchCount() {
        return this.prefetchCount;
    }

    public FlakeIdGeneratorConfig setPrefetchCount(int prefetchCount) {
        Preconditions.checkTrue(prefetchCount > 0 && prefetchCount <= 100000, "prefetch-count must be 1..100000, not " + prefetchCount);
        this.prefetchCount = prefetchCount;
        return this;
    }

    public long getPrefetchValidityMillis() {
        return this.prefetchValidityMillis;
    }

    public FlakeIdGeneratorConfig setPrefetchValidityMillis(long prefetchValidityMs) {
        this.prefetchValidityMillis = prefetchValidityMs;
        return this;
    }

    public long getIdOffset() {
        return this.idOffset;
    }

    public FlakeIdGeneratorConfig setIdOffset(long idOffset) {
        this.idOffset = idOffset;
        return this;
    }

    public long getNodeIdOffset() {
        return this.nodeIdOffset;
    }

    public FlakeIdGeneratorConfig setNodeIdOffset(long nodeIdOffset) {
        Preconditions.checkNotNegative(nodeIdOffset, "node id offset must be non-negative");
        this.nodeIdOffset = nodeIdOffset;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public FlakeIdGeneratorConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlakeIdGeneratorConfig that = (FlakeIdGeneratorConfig)o;
        return this.prefetchCount == that.prefetchCount && this.prefetchValidityMillis == that.prefetchValidityMillis && this.idOffset == that.idOffset && this.nodeIdOffset == that.nodeIdOffset && (this.name != null ? this.name.equals(that.name) : that.name == null) && this.statisticsEnabled == that.statisticsEnabled;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.name, this.prefetchCount, this.prefetchValidityMillis, this.idOffset, this.statisticsEnabled});
    }

    public String toString() {
        return "FlakeIdGeneratorConfig{name='" + this.name + '\'' + ", prefetchCount=" + this.prefetchCount + ", prefetchValidityMillis=" + this.prefetchValidityMillis + ", idOffset=" + this.idOffset + ", nodeIdOffset=" + this.nodeIdOffset + ", statisticsEnabled=" + this.statisticsEnabled + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 48;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.prefetchCount);
        out.writeLong(this.prefetchValidityMillis);
        out.writeLong(this.idOffset);
        out.writeLong(this.nodeIdOffset);
        out.writeBoolean(this.statisticsEnabled);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.prefetchCount = in.readInt();
        this.prefetchValidityMillis = in.readLong();
        this.idOffset = in.readLong();
        this.nodeIdOffset = in.readLong();
        this.statisticsEnabled = in.readBoolean();
    }
}

