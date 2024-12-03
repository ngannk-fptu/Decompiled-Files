/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;

public final class Http2DataChunkedInput
implements ChunkedInput<Http2DataFrame> {
    private final ChunkedInput<ByteBuf> input;
    private final Http2FrameStream stream;
    private boolean endStreamSent;

    public Http2DataChunkedInput(ChunkedInput<ByteBuf> input, Http2FrameStream stream) {
        this.input = ObjectUtil.checkNotNull(input, "input");
        this.stream = ObjectUtil.checkNotNull(stream, "stream");
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.input.isEndOfInput()) {
            return this.endStreamSent;
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        this.input.close();
    }

    @Override
    @Deprecated
    public Http2DataFrame readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk(ctx.alloc());
    }

    @Override
    public Http2DataFrame readChunk(ByteBufAllocator allocator) throws Exception {
        if (this.endStreamSent) {
            return null;
        }
        if (this.input.isEndOfInput()) {
            this.endStreamSent = true;
            return new DefaultHttp2DataFrame(true).stream(this.stream);
        }
        ByteBuf buf = this.input.readChunk(allocator);
        if (buf == null) {
            return null;
        }
        DefaultHttp2DataFrame dataFrame = new DefaultHttp2DataFrame(buf, this.input.isEndOfInput()).stream(this.stream);
        if (dataFrame.isEndStream()) {
            this.endStreamSent = true;
        }
        return dataFrame;
    }

    @Override
    public long length() {
        return this.input.length();
    }

    @Override
    public long progress() {
        return this.input.progress();
    }
}

