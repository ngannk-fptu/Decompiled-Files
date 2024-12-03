/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.IOException;

public class MerkleTreeConfig
implements IdentifiedDataSerializable {
    private static final int MIN_DEPTH = 2;
    private static final int MAX_DEPTH = 27;
    private static final int DEFAULT_DEPTH = 10;
    private boolean enabled = true;
    private int depth = 10;
    private String mapName;

    public MerkleTreeConfig() {
    }

    public MerkleTreeConfig(MerkleTreeConfig config) {
        Preconditions.checkNotNull(config, "config can't be null");
        this.enabled = config.enabled;
        this.mapName = config.mapName;
        this.depth = config.depth;
    }

    public String toString() {
        return "MerkleTreeConfig{enabled=" + this.enabled + ", depth=" + this.depth + ", mapName='" + this.mapName + '\'' + '}';
    }

    MerkleTreeConfig getAsReadOnly() {
        return new MerkleTreeConfigReadOnly(this);
    }

    public int getDepth() {
        return this.depth;
    }

    public MerkleTreeConfig setDepth(int depth) {
        if (depth < 2 || depth > 27) {
            throw new IllegalArgumentException("Merkle tree depth " + depth + " is outside of the allowed range " + 2 + "-" + 27 + ". ");
        }
        this.depth = depth;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public MerkleTreeConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getMapName() {
        return this.mapName;
    }

    public MerkleTreeConfig setMapName(String mapName) {
        if (StringUtil.isNullOrEmpty(mapName)) {
            throw new IllegalArgumentException("Merkle tree map name must not be empty.");
        }
        this.mapName = mapName;
        return this;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 54;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeBoolean(this.enabled);
        out.writeInt(this.depth);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        this.enabled = in.readBoolean();
        this.depth = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MerkleTreeConfig that = (MerkleTreeConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.depth != that.depth) {
            return false;
        }
        return this.mapName != null ? this.mapName.equals(that.mapName) : that.mapName == null;
    }

    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + this.depth;
        result = 31 * result + (this.mapName != null ? this.mapName.hashCode() : 0);
        return result;
    }

    static class MerkleTreeConfigReadOnly
    extends MerkleTreeConfig {
        MerkleTreeConfigReadOnly(MerkleTreeConfig config) {
            super(config);
        }

        @Override
        public MerkleTreeConfig setDepth(int depth) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public MerkleTreeConfig setEnabled(boolean enabled) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public MerkleTreeConfig setMapName(String mapName) {
            throw new UnsupportedOperationException("This config is read-only");
        }
    }
}

