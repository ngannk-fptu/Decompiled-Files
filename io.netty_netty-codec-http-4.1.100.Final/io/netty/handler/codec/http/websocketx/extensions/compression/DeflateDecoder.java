/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.CompositeByteBuf
 *  io.netty.buffer.Unpooled
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
import io.netty.buffer.Unpooled;
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
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateDecoder
extends WebSocketExtensionDecoder {
    static final ByteBuf FRAME_TAIL = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.wrappedBuffer((byte[])new byte[]{0, 0, -1, -1})).asReadOnly();
    static final ByteBuf EMPTY_DEFLATE_BLOCK = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.wrappedBuffer((byte[])new byte[]{0})).asReadOnly();
    private final boolean noContext;
    private final WebSocketExtensionFilter extensionDecoderFilter;
    private EmbeddedChannel decoder;

    DeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
        this.noContext = noContext;
        this.extensionDecoderFilter = (WebSocketExtensionFilter)ObjectUtil.checkNotNull((Object)extensionDecoderFilter, (String)"extensionDecoderFilter");
    }

    protected WebSocketExtensionFilter extensionDecoderFilter() {
        return this.extensionDecoderFilter;
    }

    protected abstract boolean appendFrameTail(WebSocketFrame var1);

    protected abstract int newRsv(WebSocketFrame var1);

    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        WebSocketFrame outMsg;
        ByteBuf decompressedContent = this.decompressContent(ctx, msg);
        if (msg instanceof TextWebSocketFrame) {
            outMsg = new TextWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
        } else if (msg instanceof BinaryWebSocketFrame) {
            outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
        } else if (msg instanceof ContinuationWebSocketFrame) {
            outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
        } else {
            throw new CodecException("unexpected frame type: " + ((Object)((Object)msg)).getClass().getName());
        }
        out.add((Object)outMsg);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.channelInactive(ctx);
    }

    private ByteBuf decompressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
        ByteBuf partUncompressedContent;
        if (this.decoder == null) {
            if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
                throw new CodecException("unexpected initial frame type: " + ((Object)((Object)msg)).getClass().getName());
            }
            this.decoder = new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder((ZlibWrapper)ZlibWrapper.NONE)});
        }
        boolean readable = msg.content().isReadable();
        boolean emptyDeflateBlock = EMPTY_DEFLATE_BLOCK.equals((Object)msg.content());
        this.decoder.writeInbound(new Object[]{msg.content().retain()});
        if (this.appendFrameTail(msg)) {
            this.decoder.writeInbound(new Object[]{FRAME_TAIL.duplicate()});
        }
        CompositeByteBuf compositeDecompressedContent = ctx.alloc().compositeBuffer();
        while ((partUncompressedContent = (ByteBuf)this.decoder.readInbound()) != null) {
            if (!partUncompressedContent.isReadable()) {
                partUncompressedContent.release();
                continue;
            }
            compositeDecompressedContent.addComponent(true, partUncompressedContent);
        }
        if (!emptyDeflateBlock && readable && compositeDecompressedContent.numComponents() <= 0 && !(msg instanceof ContinuationWebSocketFrame)) {
            compositeDecompressedContent.release();
            throw new CodecException("cannot read uncompressed buffer");
        }
        if (msg.isFinalFragment() && this.noContext) {
            this.cleanup();
        }
        return compositeDecompressedContent;
    }

    private void cleanup() {
        if (this.decoder != null) {
            this.decoder.finishAndReleaseAll();
            this.decoder = null;
        }
    }
}

