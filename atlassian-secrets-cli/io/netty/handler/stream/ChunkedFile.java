/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkedFile
implements ChunkedInput<ByteBuf> {
    private final RandomAccessFile file;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedFile(File file) throws IOException {
        this(file, 8192);
    }

    public ChunkedFile(File file, int chunkSize) throws IOException {
        this(new RandomAccessFile(file, "r"), chunkSize);
    }

    public ChunkedFile(RandomAccessFile file) throws IOException {
        this(file, 8192);
    }

    public ChunkedFile(RandomAccessFile file, int chunkSize) throws IOException {
        this(file, 0L, file.length(), chunkSize);
    }

    public ChunkedFile(RandomAccessFile file, long offset, long length, int chunkSize) throws IOException {
        ObjectUtil.checkNotNull(file, "file");
        ObjectUtil.checkPositiveOrZero(offset, "offset");
        ObjectUtil.checkPositiveOrZero(length, "length");
        ObjectUtil.checkPositive(chunkSize, "chunkSize");
        this.file = file;
        this.offset = this.startOffset = offset;
        this.endOffset = offset + length;
        this.chunkSize = chunkSize;
        file.seek(offset);
    }

    public long startOffset() {
        return this.startOffset;
    }

    public long endOffset() {
        return this.endOffset;
    }

    public long currentOffset() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return this.offset >= this.endOffset || !this.file.getChannel().isOpen();
    }

    @Override
    public void close() throws Exception {
        this.file.close();
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
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int)Math.min((long)this.chunkSize, this.endOffset - offset);
        ByteBuf buf = allocator.heapBuffer(chunkSize);
        boolean release = true;
        try {
            this.file.readFully(buf.array(), buf.arrayOffset(), chunkSize);
            buf.writerIndex(chunkSize);
            this.offset = offset + (long)chunkSize;
            release = false;
            ByteBuf byteBuf = buf;
            return byteBuf;
        }
        finally {
            if (release) {
                buf.release();
            }
        }
    }

    @Override
    public long length() {
        return this.endOffset - this.startOffset;
    }

    @Override
    public long progress() {
        return this.offset - this.startOffset;
    }
}

