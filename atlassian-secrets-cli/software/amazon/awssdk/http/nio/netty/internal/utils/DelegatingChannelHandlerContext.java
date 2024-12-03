/*
 * Decompiled with CFR 0.152.
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

    @Override
    public Channel channel() {
        return this.delegate.channel();
    }

    @Override
    public EventExecutor executor() {
        return this.delegate.executor();
    }

    @Override
    public String name() {
        return this.delegate.name();
    }

    @Override
    public ChannelHandler handler() {
        return this.delegate.handler();
    }

    @Override
    public boolean isRemoved() {
        return this.delegate.isRemoved();
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        return this.delegate.fireChannelRegistered();
    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        return this.delegate.fireChannelUnregistered();
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        return this.delegate.fireChannelActive();
    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        return this.delegate.fireChannelInactive();
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
        return this.delegate.fireExceptionCaught(cause);
    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object evt) {
        return this.delegate.fireUserEventTriggered(evt);
    }

    @Override
    public ChannelHandlerContext fireChannelRead(Object msg) {
        return this.delegate.fireChannelRead(msg);
    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        return this.delegate.fireChannelReadComplete();
    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        return this.delegate.fireChannelWritabilityChanged();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.delegate.bind(localAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.delegate.connect(remoteAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.delegate.connect(remoteAddress, localAddress);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.delegate.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.delegate.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.delegate.deregister();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.delegate.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.delegate.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.delegate.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.delegate.disconnect(promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return this.delegate.close(promise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return this.delegate.deregister(promise);
    }

    @Override
    public ChannelHandlerContext read() {
        return this.delegate.read();
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.delegate.write(msg);
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.delegate.write(msg, promise);
    }

    @Override
    public ChannelHandlerContext flush() {
        return this.delegate.flush();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.delegate.writeAndFlush(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.delegate.writeAndFlush(msg);
    }

    @Override
    public ChannelPromise newPromise() {
        return this.delegate.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.delegate.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.delegate.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return this.delegate.newFailedFuture(cause);
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.delegate.voidPromise();
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.delegate.pipeline();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.delegate.alloc();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.delegate.attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.delegate.hasAttr(key);
    }
}

