/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.compression.Zstd;
import io.netty.util.internal.ObjectUtil;

public class ZstdOptions
implements CompressionOptions {
    private final int blockSize;
    private final int compressionLevel;
    private final int maxEncodeSize;
    static final ZstdOptions DEFAULT = new ZstdOptions(3, 65536, 0x2000000);

    ZstdOptions(int compressionLevel, int blockSize, int maxEncodeSize) {
        if (!Zstd.isAvailable()) {
            throw new IllegalStateException("zstd-jni is not available", Zstd.cause());
        }
        this.compressionLevel = ObjectUtil.checkInRange((int)compressionLevel, (int)0, (int)22, (String)"compressionLevel");
        this.blockSize = ObjectUtil.checkPositive((int)blockSize, (String)"blockSize");
        this.maxEncodeSize = ObjectUtil.checkPositive((int)maxEncodeSize, (String)"maxEncodeSize");
    }

    public int compressionLevel() {
        return this.compressionLevel;
    }

    public int blockSize() {
        return this.blockSize;
    }

    public int maxEncodeSize() {
        return this.maxEncodeSize;
    }
}

