/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.memory.MemorySize;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.util.Preconditions;

public class NativeMemoryConfig {
    public static final int DEFAULT_MIN_BLOCK_SIZE = 16;
    public static final int DEFAULT_PAGE_SIZE = 0x400000;
    public static final float DEFAULT_METADATA_SPACE_PERCENTAGE = 12.5f;
    public static final int MIN_INITIAL_MEMORY_SIZE = 512;
    public static final int INITIAL_MEMORY_SIZE = 512;
    private boolean enabled;
    private MemorySize size = new MemorySize(512L, MemoryUnit.MEGABYTES);
    private MemoryAllocatorType allocatorType = MemoryAllocatorType.POOLED;
    private int minBlockSize = 16;
    private int pageSize = 0x400000;
    private float metadataSpacePercentage = 12.5f;

    public NativeMemoryConfig() {
    }

    public NativeMemoryConfig(NativeMemoryConfig nativeMemoryConfig) {
        this.enabled = nativeMemoryConfig.enabled;
        this.size = nativeMemoryConfig.size;
        this.allocatorType = nativeMemoryConfig.allocatorType;
        this.minBlockSize = nativeMemoryConfig.minBlockSize;
        this.pageSize = nativeMemoryConfig.pageSize;
        this.metadataSpacePercentage = nativeMemoryConfig.metadataSpacePercentage;
    }

    public MemorySize getSize() {
        return this.size;
    }

    public NativeMemoryConfig setSize(MemorySize size) {
        this.size = Preconditions.isNotNull(size, "size");
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public NativeMemoryConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public MemoryAllocatorType getAllocatorType() {
        return this.allocatorType;
    }

    public NativeMemoryConfig setAllocatorType(MemoryAllocatorType allocatorType) {
        this.allocatorType = allocatorType;
        return this;
    }

    public int getMinBlockSize() {
        return this.minBlockSize;
    }

    public NativeMemoryConfig setMinBlockSize(int minBlockSize) {
        this.minBlockSize = Preconditions.checkPositive(minBlockSize, "Minimum block size should be positive");
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public NativeMemoryConfig setPageSize(int pageSize) {
        this.pageSize = Preconditions.checkPositive(pageSize, "Page size should be positive");
        return this;
    }

    public float getMetadataSpacePercentage() {
        return this.metadataSpacePercentage;
    }

    public NativeMemoryConfig setMetadataSpacePercentage(float metadataSpacePercentage) {
        this.metadataSpacePercentage = metadataSpacePercentage;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof NativeMemoryConfig)) {
            return false;
        }
        NativeMemoryConfig that = (NativeMemoryConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.minBlockSize != that.minBlockSize) {
            return false;
        }
        if (this.pageSize != that.pageSize) {
            return false;
        }
        if (Float.compare(that.metadataSpacePercentage, this.metadataSpacePercentage) != 0) {
            return false;
        }
        if (this.size != null ? !this.size.equals(that.size) : that.size != null) {
            return false;
        }
        return this.allocatorType == that.allocatorType;
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.size != null ? this.size.hashCode() : 0);
        result = 31 * result + (this.allocatorType != null ? this.allocatorType.hashCode() : 0);
        result = 31 * result + this.minBlockSize;
        result = 31 * result + this.pageSize;
        result = 31 * result + (this.metadataSpacePercentage != 0.0f ? Float.floatToIntBits(this.metadataSpacePercentage) : 0);
        return result;
    }

    public String toString() {
        return "NativeMemoryConfig{enabled=" + this.enabled + ", size=" + this.size + ", allocatorType=" + (Object)((Object)this.allocatorType) + ", minBlockSize=" + this.minBlockSize + ", pageSize=" + this.pageSize + ", metadataSpacePercentage=" + this.metadataSpacePercentage + '}';
    }

    public static enum MemoryAllocatorType {
        STANDARD,
        POOLED;

    }
}

