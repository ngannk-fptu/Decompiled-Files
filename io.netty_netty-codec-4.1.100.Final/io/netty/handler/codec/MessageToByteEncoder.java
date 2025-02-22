/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.internal.TypeParameterMatcher
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class MessageToByteEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private final boolean preferDirect;

    protected MessageToByteEncoder() {
        this(true);
    }

    protected MessageToByteEncoder(Class<? extends I> outboundMessageType) {
        this(outboundMessageType, true);
    }

    protected MessageToByteEncoder(boolean preferDirect) {
        this.matcher = TypeParameterMatcher.find((Object)((Object)this), MessageToByteEncoder.class, (String)"I");
        this.preferDirect = preferDirect;
    }

    protected MessageToByteEncoder(Class<? extends I> outboundMessageType, boolean preferDirect) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
        this.preferDirect = preferDirect;
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        block14: {
            ByteBuf buf = null;
            try {
                if (this.acceptOutboundMessage(msg)) {
                    Object cast = msg;
                    buf = this.allocateBuffer(ctx, cast, this.preferDirect);
                    try {
                        this.encode(ctx, cast, buf);
                    }
                    finally {
                        ReferenceCountUtil.release((Object)cast);
                    }
                    if (buf.isReadable()) {
                        ctx.write((Object)buf, promise);
                    } else {
                        buf.release();
                        ctx.write((Object)Unpooled.EMPTY_BUFFER, promise);
                    }
                    buf = null;
                    break block14;
                }
                ctx.write(msg, promise);
            }
            catch (EncoderException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new EncoderException(e);
            }
            finally {
                if (buf != null) {
                    buf.release();
                }
            }
        }
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, I msg, boolean preferDirect) throws Exception {
        if (preferDirect) {
            return ctx.alloc().ioBuffer();
        }
        return ctx.alloc().heapBuffer();
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, ByteBuf var3) throws Exception;

    protected boolean isPreferDirect() {
        return this.preferDirect;
    }
}

