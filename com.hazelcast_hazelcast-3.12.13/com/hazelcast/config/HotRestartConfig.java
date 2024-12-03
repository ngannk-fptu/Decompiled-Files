/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class HotRestartConfig
implements IdentifiedDataSerializable {
    private boolean enabled;
    private boolean fsync;

    public HotRestartConfig() {
    }

    public HotRestartConfig(HotRestartConfig hotRestartConfig) {
        this.enabled = hotRestartConfig.enabled;
        this.fsync = hotRestartConfig.fsync;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public HotRestartConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isFsync() {
        return this.fsync;
    }

    public HotRestartConfig setFsync(boolean fsync) {
        this.fsync = fsync;
        return this;
    }

    public String toString() {
        return "HotRestartConfig{enabled=" + this.enabled + ", fsync=" + this.fsync + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 21;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.enabled);
        out.writeBoolean(this.fsync);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.enabled = in.readBoolean();
        this.fsync = in.readBoolean();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HotRestartConfig)) {
            return false;
        }
        HotRestartConfig that = (HotRestartConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        return this.fsync == that.fsync;
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.fsync ? 1 : 0);
        return result;
    }
}

