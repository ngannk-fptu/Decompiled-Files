/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.File;

public final class MemoryUsageSetting {
    private final boolean useMainMemory;
    private final boolean useTempFile;
    private final long maxMainMemoryBytes;
    private final long maxStorageBytes;
    private File tempDir;

    private MemoryUsageSetting(boolean useMainMemory, boolean useTempFile, long maxMainMemoryBytes, long maxStorageBytes) {
        long locMaxStorageBytes;
        boolean locUseMainMemory = !useTempFile || useMainMemory;
        long locMaxMainMemoryBytes = useMainMemory ? maxMainMemoryBytes : -1L;
        long l = locMaxStorageBytes = maxStorageBytes > 0L ? maxStorageBytes : -1L;
        if (locMaxMainMemoryBytes < -1L) {
            locMaxMainMemoryBytes = -1L;
        }
        if (locUseMainMemory && locMaxMainMemoryBytes == 0L) {
            if (useTempFile) {
                locUseMainMemory = false;
            } else {
                locMaxMainMemoryBytes = locMaxStorageBytes;
            }
        }
        if (locUseMainMemory && locMaxStorageBytes > -1L && (locMaxMainMemoryBytes == -1L || locMaxMainMemoryBytes > locMaxStorageBytes)) {
            locMaxStorageBytes = locMaxMainMemoryBytes;
        }
        this.useMainMemory = locUseMainMemory;
        this.useTempFile = useTempFile;
        this.maxMainMemoryBytes = locMaxMainMemoryBytes;
        this.maxStorageBytes = locMaxStorageBytes;
    }

    public static MemoryUsageSetting setupMainMemoryOnly() {
        return MemoryUsageSetting.setupMainMemoryOnly(-1L);
    }

    public static MemoryUsageSetting setupMainMemoryOnly(long maxMainMemoryBytes) {
        return new MemoryUsageSetting(true, false, maxMainMemoryBytes, maxMainMemoryBytes);
    }

    public static MemoryUsageSetting setupTempFileOnly() {
        return MemoryUsageSetting.setupTempFileOnly(-1L);
    }

    public static MemoryUsageSetting setupTempFileOnly(long maxStorageBytes) {
        return new MemoryUsageSetting(false, true, 0L, maxStorageBytes);
    }

    public static MemoryUsageSetting setupMixed(long maxMainMemoryBytes) {
        return MemoryUsageSetting.setupMixed(maxMainMemoryBytes, -1L);
    }

    public static MemoryUsageSetting setupMixed(long maxMainMemoryBytes, long maxStorageBytes) {
        return new MemoryUsageSetting(true, true, maxMainMemoryBytes, maxStorageBytes);
    }

    public MemoryUsageSetting getPartitionedCopy(int parallelUseCount) {
        long newMaxMainMemoryBytes = this.maxMainMemoryBytes <= 0L ? this.maxMainMemoryBytes : this.maxMainMemoryBytes / (long)parallelUseCount;
        long newMaxStorageBytes = this.maxStorageBytes <= 0L ? this.maxStorageBytes : this.maxStorageBytes / (long)parallelUseCount;
        MemoryUsageSetting copy = new MemoryUsageSetting(this.useMainMemory, this.useTempFile, newMaxMainMemoryBytes, newMaxStorageBytes);
        copy.tempDir = this.tempDir;
        return copy;
    }

    public MemoryUsageSetting setTempDir(File tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    public boolean useMainMemory() {
        return this.useMainMemory;
    }

    public boolean useTempFile() {
        return this.useTempFile;
    }

    public boolean isMainMemoryRestricted() {
        return this.maxMainMemoryBytes >= 0L;
    }

    public boolean isStorageRestricted() {
        return this.maxStorageBytes > 0L;
    }

    public long getMaxMainMemoryBytes() {
        return this.maxMainMemoryBytes;
    }

    public long getMaxStorageBytes() {
        return this.maxStorageBytes;
    }

    public File getTempDir() {
        return this.tempDir;
    }

    public String toString() {
        return this.useMainMemory ? (this.useTempFile ? "Mixed mode with max. of " + this.maxMainMemoryBytes + " main memory bytes" + (this.isStorageRestricted() ? " and max. of " + this.maxStorageBytes + " storage bytes" : " and unrestricted scratch file size") : (this.isMainMemoryRestricted() ? "Main memory only with max. of " + this.maxMainMemoryBytes + " bytes" : "Main memory only with no size restriction")) : (this.isStorageRestricted() ? "Scratch file only with max. of " + this.maxStorageBytes + " bytes" : "Scratch file only with no size restriction");
    }
}

