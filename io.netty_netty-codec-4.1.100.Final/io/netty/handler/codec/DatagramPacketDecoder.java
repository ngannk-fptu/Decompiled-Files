/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.socket.DatagramPacket
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DatagramPacketDecoder
extends MessageToMessageDecoder<DatagramPacket> {
    private final MessageToMessageDecoder<ByteBuf> decoder;

    public DatagramPacketDecoder(MessageToMessageDecoder<ByteBuf> decoder) {
        this.decoder = (MessageToMessageDecoder)((Object)ObjectUtil.checkNotNull(decoder, (String)"decoder"));
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (msg instanceof DatagramPacket) {
            return this.decoder.acceptInboundMessage(((DatagramPacket)msg).content());
        }
        return false;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        this.decoder.decode(ctx, (ByteBuf)msg.content(), out);
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelRegistered(ctx);
    }

    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelUnregistered(ctx);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelActive(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelInactive(ctx);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelReadComplete(ctx);
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        this.decoder.userEventTriggered(ctx, evt);
    }

    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelWritabilityChanged(ctx);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.decoder.exceptionCaught(ctx, cause);
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.decoder.handlerAdded(ctx);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.decoder.handlerRemoved(ctx);
    }

    public boolean isSharable() {
        return this.decoder.isSharable();
    }
}

