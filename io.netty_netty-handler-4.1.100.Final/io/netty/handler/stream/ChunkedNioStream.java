/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChunkedNioStream
implements ChunkedInput<ByteBuf> {
    private final ReadableByteChannel in;
    private final int chunkSize;
    private long offset;
    private final ByteBuffer byteBuffer;

    public ChunkedNioStream(ReadableByteChannel in) {
        this(in, 8192);
    }

    public ChunkedNioStream(ReadableByteChannel in, int chunkSize) {
        this.in = (ReadableByteChannel)ObjectUtil.checkNotNull((Object)in, (String)"in");
        this.chunkSize = ObjectUtil.checkPositive((int)chunkSize, (String)"chunkSize");
        this.byteBuffer = ByteBuffer.allocate(chunkSize);
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.byteBuffer.position() > 0) {
            return false;
        }
        if (this.in.isOpen()) {
            int b = this.in.read(this.byteBuffer);
            if (b < 0) {
                return true;
            }
            this.offset += (long)b;
            return false;
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    @Override
    @Deprecated
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk(ctx.alloc());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        int localReadBytes;
        if (this.isEndOfInput()) {
            return null;
        }
        int readBytes = this.byteBuffer.position();
        while ((localReadBytes = this.in.read(this.byteBuffer)) >= 0) {
            this.offset += (long)localReadBytes;
            if ((readBytes += localReadBytes) != this.chunkSize) continue;
            break;
        }
        this.byteBuffer.flip();
        boolean release = true;
        ByteBuf buffer = allocator.buffer(this.byteBuffer.remaining());
        try {
            buffer.writeBytes(this.byteBuffer);
            this.byteBuffer.clear();
            release = false;
            ByteBuf byteBuf = buffer;
            return byteBuf;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }

    @Override
    public long length() {
        return -1L;
    }

    @Override
    public long progress() {
        return this.offset;
    }
}

