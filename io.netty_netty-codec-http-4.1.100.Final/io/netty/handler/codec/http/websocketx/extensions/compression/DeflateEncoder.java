/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.CompositeByteBuf
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.embedded.EmbeddedChannel
 *  io.netty.handler.codec.CodecException
 *  io.netty.handler.codec.compression.ZlibCodecFactory
 *  io.netty.handler.codec.compression.ZlibWrapper
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateEncoder
extends WebSocketExtensionEncoder {
    private final int compressionLevel;
    private final int windowSize;
    private final boolean noContext;
    private final WebSocketExtensionFilter extensionEncoderFilter;
    private EmbeddedChannel encoder;

    DeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
        this.compressionLevel = compressionLevel;
        this.windowSize = windowSize;
        this.noContext = noContext;
        this.extensionEncoderFilter = (WebSocketExtensionFilter)ObjectUtil.checkNotNull((Object)extensionEncoderFilter, (String)"extensionEncoderFilter");
    }

    protected WebSocketExtensionFilter extensionEncoderFilter() {
        return this.extensionEncoderFilter;
    }

    protected abstract int rsv(WebSocketFrame var1);

    protected abstract boolean removeFrameTail(WebSocketFrame var1);

    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        WebSocketFrame outMsg;
        ByteBuf compressedContent;
        if (msg.content().isReadable()) {
            compressedContent = this.compressContent(ctx, msg);
        } else if (msg.isFinalFragment()) {
            compressedContent = PerMessageDeflateDecoder.EMPTY_DEFLATE_BLOCK.duplicate();
        } else {
            throw new CodecException("cannot compress content buffer");
        }
        if (msg instanceof TextWebSocketFrame) {
            outMsg = new TextWebSocketFrame(msg.isFinalFragment(), this.rsv(msg), compressedContent);
        } else if (msg instanceof BinaryWebSocketFrame) {
            outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), this.rsv(msg), compressedContent);
        } else if (msg instanceof ContinuationWebSocketFrame) {
            outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), this.rsv(msg), compressedContent);
        } else {
            throw new CodecException("unexpected frame type: " + ((Object)((Object)msg)).getClass().getName());
        }
        out.add((Object)outMsg);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved(ctx);
    }

    private ByteBuf compressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
        CompositeByteBuf compressedContent;
        ByteBuf partCompressedContent;
        if (this.encoder == null) {
            this.encoder = new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder((ZlibWrapper)ZlibWrapper.NONE, (int)this.compressionLevel, (int)this.windowSize, (int)8)});
        }
        this.encoder.writeOutbound(new Object[]{msg.content().retain()});
        CompositeByteBuf fullCompressedContent = ctx.alloc().compositeBuffer();
        while ((partCompressedContent = (ByteBuf)this.encoder.readOutbound()) != null) {
            if (!partCompressedContent.isReadable()) {
                partCompressedContent.release();
                continue;
            }
            fullCompressedContent.addComponent(true, partCompressedContent);
        }
        if (fullCompressedContent.numComponents() <= 0) {
            fullCompressedContent.release();
            throw new CodecException("cannot read compressed buffer");
        }
        if (msg.isFinalFragment() && this.noContext) {
            this.cleanup();
        }
        if (this.removeFrameTail(msg)) {
            int realLength = fullCompressedContent.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.readableBytes();
            compressedContent = fullCompressedContent.slice(0, realLength);
        } else {
            compressedContent = fullCompressedContent;
        }
        return compressedContent;
    }

    private void cleanup() {
        if (this.encoder != null) {
            this.encoder.finishAndReleaseAll();
            this.encoder = null;
        }
    }
}

