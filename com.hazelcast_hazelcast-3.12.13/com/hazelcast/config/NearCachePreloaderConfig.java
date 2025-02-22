/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.io.Serializable;

public class NearCachePreloaderConfig
implements IdentifiedDataSerializable,
Serializable {
    public static final int DEFAULT_STORE_INITIAL_DELAY_SECONDS = 600;
    public static final int DEFAULT_STORE_INTERVAL_SECONDS = 600;
    private boolean enabled;
    private String directory = "";
    private int storeInitialDelaySeconds = 600;
    private int storeIntervalSeconds = 600;
    private NearCachePreloaderConfig readOnly;

    public NearCachePreloaderConfig() {
    }

    public NearCachePreloaderConfig(NearCachePreloaderConfig nearCachePreloaderConfig) {
        this(nearCachePreloaderConfig.enabled, nearCachePreloaderConfig.directory);
        this.storeInitialDelaySeconds = nearCachePreloaderConfig.storeInitialDelaySeconds;
        this.storeIntervalSeconds = nearCachePreloaderConfig.storeIntervalSeconds;
    }

    public NearCachePreloaderConfig(String directory) {
        this(true, directory);
    }

    public NearCachePreloaderConfig(boolean enabled, String directory) {
        this.enabled = enabled;
        this.directory = Preconditions.checkNotNull(directory, "directory cannot be null!");
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public NearCachePreloaderConfig setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
        return this;
    }

    public NearCachePreloaderConfig setDirectory(String directory) {
        this.directory = Preconditions.checkNotNull(directory, "directory cannot be null!");
        return this;
    }

    public String getDirectory() {
        return this.directory;
    }

    public int getStoreInitialDelaySeconds() {
        return this.storeInitialDelaySeconds;
    }

    public NearCachePreloaderConfig setStoreInitialDelaySeconds(int storeInitialDelaySeconds) {
        this.storeInitialDelaySeconds = Preconditions.checkPositive(storeInitialDelaySeconds, "storeInitialDelaySeconds must be a positive number!");
        return this;
    }

    public int getStoreIntervalSeconds() {
        return this.storeIntervalSeconds;
    }

    public NearCachePreloaderConfig setStoreIntervalSeconds(int storeIntervalSeconds) {
        this.storeIntervalSeconds = Preconditions.checkPositive(storeIntervalSeconds, "storeIntervalSeconds must be a positive number!");
        return this;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.enabled);
        out.writeUTF(this.directory);
        out.writeInt(this.storeInitialDelaySeconds);
        out.writeInt(this.storeIntervalSeconds);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.enabled = in.readBoolean();
        this.directory = in.readUTF();
        this.storeInitialDelaySeconds = in.readInt();
        this.storeIntervalSeconds = in.readInt();
    }

    public String toString() {
        return "NearCachePreloaderConfig{enabled=" + this.enabled + ", directory=" + this.directory + ", storeInitialDelaySeconds=" + this.storeInitialDelaySeconds + ", storeIntervalSeconds=" + this.storeIntervalSeconds + '}';
    }

    NearCachePreloaderConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new NearCachePreloaderConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NearCachePreloaderConfig that = (NearCachePreloaderConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.storeInitialDelaySeconds != that.storeInitialDelaySeconds) {
            return false;
        }
        if (this.storeIntervalSeconds != that.storeIntervalSeconds) {
            return false;
        }
        return this.directory != null ? this.directory.equals(that.directory) : that.directory == null;
    }

    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.directory != null ? this.directory.hashCode() : 0);
        result = 31 * result + this.storeInitialDelaySeconds;
        result = 31 * result + this.storeIntervalSeconds;
        return result;
    }

    @PrivateApi
    private static class NearCachePreloaderConfigReadOnly
    extends NearCachePreloaderConfig {
        public NearCachePreloaderConfigReadOnly() {
        }

        NearCachePreloaderConfigReadOnly(NearCachePreloaderConfig nearCachePreloaderConfig) {
            super(nearCachePreloaderConfig);
        }

        @Override
        public NearCachePreloaderConfig setEnabled(boolean isEnabled) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NearCachePreloaderConfig setDirectory(String directory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NearCachePreloaderConfig setStoreInitialDelaySeconds(int storeInitialDelaySeconds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NearCachePreloaderConfig setStoreIntervalSeconds(int storeIntervalSeconds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getId() {
            throw new UnsupportedOperationException("NearCachePreloaderConfigReadOnly is not serializable");
        }
    }
}

