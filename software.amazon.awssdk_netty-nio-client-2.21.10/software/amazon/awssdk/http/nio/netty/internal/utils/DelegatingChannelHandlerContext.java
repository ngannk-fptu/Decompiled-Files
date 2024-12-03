/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelProgressivePromise
 *  io.netty.channel.ChannelPromise
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.EventExecutor
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public abstract class DelegatingChannelHandlerContext
implements ChannelHandlerContext {
    private final ChannelHandlerContext delegate;

    public DelegatingChannelHandlerContext(ChannelHandlerContext delegate) {
        this.delegate = delegate;
    }

    public Channel channel() {
        return this.delegate.channel();
    }

    public EventExecutor executor() {
        return this.delegate.executor();
    }

    public String name() {
        return this.delegate.name();
    }

    public ChannelHandler handler() {
        return this.delegate.handler();
    }

    public boolean isRemoved() {
        return this.delegate.isRemoved();
    }

    public ChannelHandlerContext fireChannelRegistered() {
        return this.delegate.fireChannelRegistered();
    }

    public ChannelHandlerContext fireChannelUnregistered() {
        return this.delegate.fireChannelUnregistered();
    }

    public ChannelHandlerContext fireChannelActive() {
        return this.delegate.fireChannelActive();
    }

    public ChannelHandlerContext fireChannelInactive() {
        return this.delegate.fireChannelInactive();
    }

    public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
        return this.delegate.fireExceptionCaught(cause);
    }

    public ChannelHandlerContext fireUserEventTriggered(Object evt) {
        return this.delegate.fireUserEventTriggered(evt);
    }

    public ChannelHandlerContext fireChannelRead(Object msg) {
        return this.delegate.fireChannelRead(msg);
    }

    public ChannelHandlerContext fireChannelReadComplete() {
        return this.delegate.fireChannelReadComplete();
    }

    public ChannelHandlerContext fireChannelWritabilityChanged() {
        return this.delegate.fireChannelWritabilityChanged();
    }

    public ChannelFuture bind(SocketAddress localAddress) {
        return this.delegate.bind(localAddress);
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.delegate.connect(remoteAddress);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.delegate.connect(remoteAddress, localAddress);
    }

    public ChannelFuture disconnect() {
        return this.delegate.disconnect();
    }

    public ChannelFuture close() {
        return this.delegate.close();
    }

    public ChannelFuture deregister() {
        return this.delegate.deregister();
    }

    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.delegate.bind(localAddress, promise);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.delegate.connect(remoteAddress, promise);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.delegate.connect(remoteAddress, localAddress, promise);
    }

    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.delegate.disconnect(promise);
    }

    public ChannelFuture close(ChannelPromise promise) {
        return this.delegate.close(promise);
    }

    public ChannelFuture deregister(ChannelPromise promise) {
        return this.delegate.deregister(promise);
    }

    public ChannelHandlerContext read() {
        return this.delegate.read();
    }

    public ChannelFuture write(Object msg) {
        return this.delegate.write(msg);
    }

    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.delegate.write(msg, promise);
    }

    public ChannelHandlerContext flush() {
        return this.delegate.flush();
    }

    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.delegate.writeAndFlush(msg, promise);
    }

    public ChannelFuture writeAndFlush(Object msg) {
        return this.delegate.writeAndFlush(msg);
    }

    public ChannelPromise newPromise() {
        return this.delegate.newPromise();
    }

    public ChannelProgressivePromise newProgressivePromise() {
        return this.delegate.newProgressivePromise();
    }

    public ChannelFuture newSucceededFuture() {
        return this.delegate.newSucceededFuture();
    }

    public ChannelFuture newFailedFuture(Throwable cause) {
        return this.delegate.newFailedFuture(cause);
    }

    public ChannelPromise voidPromise() {
        return this.delegate.voidPromise();
    }

    public ChannelPipeline pipeline() {
        return this.delegate.pipeline();
    }

    public ByteBufAllocator alloc() {
        return this.delegate.alloc();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.delegate.attr(key);
    }

    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.delegate.hasAttr(key);
    }
}

