/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.AddressedEnvelope
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.channel.socket.DatagramPacket
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public class DatagramPacketEncoder<M>
extends MessageToMessageEncoder<AddressedEnvelope<M, InetSocketAddress>> {
    private final MessageToMessageEncoder<? super M> encoder;

    public DatagramPacketEncoder(MessageToMessageEncoder<? super M> encoder) {
        this.encoder = (MessageToMessageEncoder)((Object)ObjectUtil.checkNotNull(encoder, (String)"encoder"));
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (super.acceptOutboundMessage(msg)) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            return this.encoder.acceptOutboundMessage(envelope.content()) && (envelope.sender() instanceof InetSocketAddress || envelope.sender() == null) && envelope.recipient() instanceof InetSocketAddress;
        }
        return false;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<M, InetSocketAddress> msg, List<Object> out) throws Exception {
        assert (out.isEmpty());
        this.encoder.encode(ctx, msg.content(), out);
        if (out.size() != 1) {
            throw new EncoderException(StringUtil.simpleClassName(this.encoder) + " must produce only one message.");
        }
        Object content = out.get(0);
        if (!(content instanceof ByteBuf)) {
            throw new EncoderException(StringUtil.simpleClassName(this.encoder) + " must produce only ByteBuf.");
        }
        out.set(0, new DatagramPacket((ByteBuf)content, (InetSocketAddress)msg.recipient(), (InetSocketAddress)msg.sender()));
    }

    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        this.encoder.bind(ctx, localAddress, promise);
    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        this.encoder.connect(ctx, remoteAddress, localAddress, promise);
    }

    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.encoder.disconnect(ctx, promise);
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.encoder.close(ctx, promise);
    }

    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.encoder.deregister(ctx, promise);
    }

    public void read(ChannelHandlerContext ctx) throws Exception {
        this.encoder.read(ctx);
    }

    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.encoder.flush(ctx);
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.encoder.handlerAdded(ctx);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.encoder.handlerRemoved(ctx);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.encoder.exceptionCaught(ctx, cause);
    }

    public boolean isSharable() {
        return this.encoder.isSharable();
    }
}

