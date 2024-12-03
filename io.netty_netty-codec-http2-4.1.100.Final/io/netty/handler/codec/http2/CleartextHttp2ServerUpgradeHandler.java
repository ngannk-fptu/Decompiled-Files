/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.http.HttpServerCodec
 *  io.netty.handler.codec.http.HttpServerUpgradeHandler
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public final class CleartextHttp2ServerUpgradeHandler
extends ByteToMessageDecoder {
    private static final ByteBuf CONNECTION_PREFACE = Unpooled.unreleasableBuffer((ByteBuf)Http2CodecUtil.connectionPrefaceBuf()).asReadOnly();
    private final HttpServerCodec httpServerCodec;
    private final HttpServerUpgradeHandler httpServerUpgradeHandler;
    private final ChannelHandler http2ServerHandler;

    public CleartextHttp2ServerUpgradeHandler(HttpServerCodec httpServerCodec, HttpServerUpgradeHandler httpServerUpgradeHandler, ChannelHandler http2ServerHandler) {
        this.httpServerCodec = (HttpServerCodec)ObjectUtil.checkNotNull((Object)httpServerCodec, (String)"httpServerCodec");
        this.httpServerUpgradeHandler = (HttpServerUpgradeHandler)ObjectUtil.checkNotNull((Object)httpServerUpgradeHandler, (String)"httpServerUpgradeHandler");
        this.http2ServerHandler = (ChannelHandler)ObjectUtil.checkNotNull((Object)http2ServerHandler, (String)"http2ServerHandler");
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addAfter(ctx.name(), null, (ChannelHandler)this.httpServerUpgradeHandler).addAfter(ctx.name(), null, (ChannelHandler)this.httpServerCodec);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int prefaceLength = CONNECTION_PREFACE.readableBytes();
        int bytesRead = Math.min(in.readableBytes(), prefaceLength);
        if (!ByteBufUtil.equals((ByteBuf)CONNECTION_PREFACE, (int)CONNECTION_PREFACE.readerIndex(), (ByteBuf)in, (int)in.readerIndex(), (int)bytesRead)) {
            ctx.pipeline().remove((ChannelHandler)this);
        } else if (bytesRead == prefaceLength) {
            ctx.pipeline().remove((ChannelHandler)this.httpServerCodec).remove((ChannelHandler)this.httpServerUpgradeHandler);
            ctx.pipeline().addAfter(ctx.name(), null, this.http2ServerHandler);
            ctx.pipeline().remove((ChannelHandler)this);
            ctx.fireUserEventTriggered((Object)PriorKnowledgeUpgradeEvent.INSTANCE);
        }
    }

    public static final class PriorKnowledgeUpgradeEvent {
        private static final PriorKnowledgeUpgradeEvent INSTANCE = new PriorKnowledgeUpgradeEvent();

        private PriorKnowledgeUpgradeEvent() {
        }
    }
}

