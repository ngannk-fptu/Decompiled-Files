/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.embedded.EmbeddedChannel
 *  io.netty.handler.codec.compression.Brotli
 *  io.netty.handler.codec.compression.BrotliDecoder
 *  io.netty.handler.codec.compression.SnappyFrameDecoder
 *  io.netty.handler.codec.compression.ZlibCodecFactory
 *  io.netty.handler.codec.compression.ZlibWrapper
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.BrotliDecoder;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpContentDecoder;
import io.netty.handler.codec.http.HttpHeaderValues;

public class HttpContentDecompressor
extends HttpContentDecoder {
    private final boolean strict;

    public HttpContentDecompressor() {
        this(false);
    }

    public HttpContentDecompressor(boolean strict) {
        this.strict = strict;
    }

    @Override
    protected EmbeddedChannel newContentDecoder(String contentEncoding) throws Exception {
        if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase((CharSequence)contentEncoding) || HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase((CharSequence)contentEncoding)) {
            return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder((ZlibWrapper)ZlibWrapper.GZIP)});
        }
        if (HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase((CharSequence)contentEncoding) || HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase((CharSequence)contentEncoding)) {
            ZlibWrapper wrapper = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
            return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder((ZlibWrapper)wrapper)});
        }
        if (Brotli.isAvailable() && HttpHeaderValues.BR.contentEqualsIgnoreCase((CharSequence)contentEncoding)) {
            return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{new BrotliDecoder()});
        }
        if (HttpHeaderValues.SNAPPY.contentEqualsIgnoreCase((CharSequence)contentEncoding)) {
            return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{new SnappyFrameDecoder()});
        }
        return null;
    }
}

