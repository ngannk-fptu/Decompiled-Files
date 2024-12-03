/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.util.internal.ObjectUtil;

public class DeflateOptions
implements CompressionOptions {
    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;
    static final DeflateOptions DEFAULT = new DeflateOptions(6, 15, 8);

    DeflateOptions(int compressionLevel, int windowBits, int memLevel) {
        this.compressionLevel = ObjectUtil.checkInRange((int)compressionLevel, (int)0, (int)9, (String)"compressionLevel");
        this.windowBits = ObjectUtil.checkInRange((int)windowBits, (int)9, (int)15, (String)"windowBits");
        this.memLevel = ObjectUtil.checkInRange((int)memLevel, (int)1, (int)9, (String)"memLevel");
    }

    public int compressionLevel() {
        return this.compressionLevel;
    }

    public int windowBits() {
        return this.windowBits;
    }

    public int memLevel() {
        return this.memLevel;
    }
}

