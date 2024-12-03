/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.util.internal.TypeParameterMatcher
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN>
extends ChannelDuplexHandler {
    private final MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<Object>(){

        @Override
        public boolean acceptOutboundMessage(Object msg) throws Exception {
            return MessageToMessageCodec.this.acceptOutboundMessage(msg);
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            MessageToMessageCodec.this.encode(ctx, msg, out);
        }
    };
    private final MessageToMessageDecoder<Object> decoder = new MessageToMessageDecoder<Object>(){

        @Override
        public boolean acceptInboundMessage(Object msg) throws Exception {
            return MessageToMessageCodec.this.acceptInboundMessage(msg);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            MessageToMessageCodec.this.decode(ctx, msg, out);
        }
    };
    private final TypeParameterMatcher inboundMsgMatcher;
    private final TypeParameterMatcher outboundMsgMatcher;

    protected MessageToMessageCodec() {
        this.inboundMsgMatcher = TypeParameterMatcher.find((Object)((Object)this), MessageToMessageCodec.class, (String)"INBOUND_IN");
        this.outboundMsgMatcher = TypeParameterMatcher.find((Object)((Object)this), MessageToMessageCodec.class, (String)"OUTBOUND_IN");
    }

    protected MessageToMessageCodec(Class<? extends INBOUND_IN> inboundMessageType, Class<? extends OUTBOUND_IN> outboundMessageType) {
        this.inboundMsgMatcher = TypeParameterMatcher.get(inboundMessageType);
        this.outboundMsgMatcher = TypeParameterMatcher.get(outboundMessageType);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.decoder.channelRead(ctx, msg);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.encoder.write(ctx, msg, promise);
    }

    public boolean acceptInboundMessage(Object msg) throws Exception {
        return this.inboundMsgMatcher.match(msg);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.outboundMsgMatcher.match(msg);
    }

    protected abstract void encode(ChannelHandlerContext var1, OUTBOUND_IN var2, List<Object> var3) throws Exception;

    protected abstract void decode(ChannelHandlerContext var1, INBOUND_IN var2, List<Object> var3) throws Exception;
}

