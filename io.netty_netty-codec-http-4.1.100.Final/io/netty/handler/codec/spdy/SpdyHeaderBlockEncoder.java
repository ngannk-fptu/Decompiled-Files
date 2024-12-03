/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.util.internal.PlatformDependent
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.spdy.SpdyHeaderBlockJZlibEncoder;
import io.netty.handler.codec.spdy.SpdyHeaderBlockZlibEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.internal.PlatformDependent;

public abstract class SpdyHeaderBlockEncoder {
    static SpdyHeaderBlockEncoder newInstance(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
        if (PlatformDependent.javaVersion() >= 7) {
            return new SpdyHeaderBlockZlibEncoder(version, compressionLevel);
        }
        return new SpdyHeaderBlockJZlibEncoder(version, compressionLevel, windowBits, memLevel);
    }

    abstract ByteBuf encode(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception;

    abstract void end();
}

