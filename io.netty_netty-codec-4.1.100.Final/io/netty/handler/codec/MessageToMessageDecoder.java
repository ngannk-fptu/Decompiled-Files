/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.internal.TypeParameterMatcher
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I>
extends ChannelInboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageDecoder() {
        this.matcher = TypeParameterMatcher.find((Object)((Object)this), MessageToMessageDecoder.class, (String)"I");
    }

    protected MessageToMessageDecoder(Class<? extends I> inboundMessageType) {
        this.matcher = TypeParameterMatcher.get(inboundMessageType);
    }

    public boolean acceptInboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        block16: {
            CodecOutputList out = CodecOutputList.newInstance();
            try {
                if (this.acceptInboundMessage(msg)) {
                    Object cast = msg;
                    try {
                        this.decode(ctx, cast, out);
                        break block16;
                    }
                    finally {
                        ReferenceCountUtil.release((Object)cast);
                    }
                }
                out.add(msg);
            }
            catch (DecoderException e) {
                throw e;
            }
            catch (Exception e) {
                throw new DecoderException(e);
            }
            finally {
                try {
                    int size = out.size();
                    for (int i = 0; i < size; ++i) {
                        ctx.fireChannelRead(out.getUnsafe(i));
                    }
                }
                finally {
                    out.recycle();
                }
            }
        }
    }

    protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

