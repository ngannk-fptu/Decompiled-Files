/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameEncoder
extends MessageToMessageEncoder<WebSocketFrame>
implements WebSocketFrameEncoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private static final int GATHERING_WRITE_THRESHOLD = 1024;
    private final boolean maskPayload;

    public WebSocket08FrameEncoder(boolean maskPayload) {
        this.maskPayload = maskPayload;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        int opcode;
        ByteBuf data = msg.content();
        if (msg instanceof TextWebSocketFrame) {
            opcode = 1;
        } else if (msg instanceof PingWebSocketFrame) {
            opcode = 9;
        } else if (msg instanceof PongWebSocketFrame) {
            opcode = 10;
        } else if (msg instanceof CloseWebSocketFrame) {
            opcode = 8;
        } else if (msg instanceof BinaryWebSocketFrame) {
            opcode = 2;
        } else if (msg instanceof ContinuationWebSocketFrame) {
            opcode = 0;
        } else {
            throw new UnsupportedOperationException("Cannot encode frame of type: " + msg.getClass().getName());
        }
        int length = data.readableBytes();
        if (logger.isTraceEnabled()) {
            logger.trace("Encoding WebSocket Frame opCode={} length={}", (Object)((byte)opcode), (Object)length);
        }
        int b0 = 0;
        if (msg.isFinalFragment()) {
            b0 |= 0x80;
        }
        b0 |= msg.rsv() % 8 << 4;
        b0 |= opcode % 128;
        if (opcode == 9 && length > 125) {
            throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + length);
        }
        boolean release = true;
        ReferenceCounted buf = null;
        try {
            int size;
            int maskLength;
            int n = maskLength = this.maskPayload ? 4 : 0;
            if (length <= 125) {
                size = 2 + maskLength + length;
                buf = ctx.alloc().buffer(size);
                ((ByteBuf)buf).writeByte(b0);
                byte b = this.maskPayload ? (byte)(0x80 | (byte)length) : (byte)length;
                ((ByteBuf)buf).writeByte(b);
            } else if (length <= 65535) {
                size = 4 + maskLength;
                if (this.maskPayload || length <= 1024) {
                    size += length;
                }
                buf = ctx.alloc().buffer(size);
                ((ByteBuf)buf).writeByte(b0);
                ((ByteBuf)buf).writeByte(this.maskPayload ? 254 : 126);
                ((ByteBuf)buf).writeByte(length >>> 8 & 0xFF);
                ((ByteBuf)buf).writeByte(length & 0xFF);
            } else {
                size = 10 + maskLength;
                if (this.maskPayload) {
                    size += length;
                }
                buf = ctx.alloc().buffer(size);
                ((ByteBuf)buf).writeByte(b0);
                ((ByteBuf)buf).writeByte(this.maskPayload ? 255 : 127);
                ((ByteBuf)buf).writeLong(length);
            }
            if (this.maskPayload) {
                int mask = PlatformDependent.threadLocalRandom().nextInt(Integer.MAX_VALUE);
                ((ByteBuf)buf).writeInt(mask);
                if (data.isReadable()) {
                    ByteOrder srcOrder = data.order();
                    ByteOrder dstOrder = ((ByteBuf)buf).order();
                    int i = data.readerIndex();
                    int end = data.writerIndex();
                    if (srcOrder == dstOrder) {
                        long longMask = (long)mask & 0xFFFFFFFFL;
                        longMask |= longMask << 32;
                        if (srcOrder == ByteOrder.LITTLE_ENDIAN) {
                            longMask = Long.reverseBytes(longMask);
                        }
                        int lim = end - 7;
                        while (i < lim) {
                            ((ByteBuf)buf).writeLong(data.getLong(i) ^ longMask);
                            i += 8;
                        }
                        if (i < end - 3) {
                            ((ByteBuf)buf).writeInt(data.getInt(i) ^ (int)longMask);
                            i += 4;
                        }
                    }
                    int maskOffset = 0;
                    while (i < end) {
                        byte byteData = data.getByte(i);
                        ((ByteBuf)buf).writeByte(byteData ^ WebSocketUtil.byteAtIndex(mask, maskOffset++ & 3));
                        ++i;
                    }
                }
                out.add(buf);
            } else if (((ByteBuf)buf).writableBytes() >= data.readableBytes()) {
                ((ByteBuf)buf).writeBytes(data);
                out.add(buf);
            } else {
                out.add(buf);
                out.add(data.retain());
            }
            release = false;
        }
        finally {
            if (release && buf != null) {
                buf.release();
            }
        }
    }
}

