/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.AbstractServerChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannelRecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalChannelRegistry;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalServerChannel
extends AbstractServerChannel {
    private final ChannelConfig config = new DefaultChannelConfig(this, new ServerChannelRecvByteBufAllocator()){};
    private final Queue<Object> inboundBuffer = new ArrayDeque<Object>();
    private final Runnable shutdownHook = new Runnable(){

        @Override
        public void run() {
            LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
        }
    };
    private volatile int state;
    private volatile LocalAddress localAddress;
    private volatile boolean acceptInProgress;

    public LocalServerChannel() {
        this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public LocalAddress localAddress() {
        return (LocalAddress)super.localAddress();
    }

    @Override
    public LocalAddress remoteAddress() {
        return (LocalAddress)super.remoteAddress();
    }

    @Override
    public boolean isOpen() {
        return this.state < 2;
    }

    @Override
    public boolean isActive() {
        return this.state == 1;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof SingleThreadEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.localAddress;
    }

    @Override
    protected void doRegister() throws Exception {
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).addShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
        this.state = 1;
    }

    @Override
    protected void doClose() throws Exception {
        if (this.state <= 1) {
            if (this.localAddress != null) {
                LocalChannelRegistry.unregister(this.localAddress);
                this.localAddress = null;
            }
            this.state = 2;
        }
    }

    @Override
    protected void doDeregister() throws Exception {
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).removeShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (this.acceptInProgress) {
            return;
        }
        Queue<Object> inboundBuffer = this.inboundBuffer;
        if (inboundBuffer.isEmpty()) {
            this.acceptInProgress = true;
            return;
        }
        this.readInbound();
    }

    LocalChannel serve(LocalChannel peer) {
        final LocalChannel child = this.newLocalChannel(peer);
        if (this.eventLoop().inEventLoop()) {
            this.serve0(child);
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    LocalServerChannel.this.serve0(child);
                }
            });
        }
        return child;
    }

    private void readInbound() {
        Object m;
        RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
        handle.reset(this.config());
        ChannelPipeline pipeline = this.pipeline();
        while ((m = this.inboundBuffer.poll()) != null) {
            pipeline.fireChannelRead(m);
            if (handle.continueReading()) continue;
        }
        handle.readComplete();
        pipeline.fireChannelReadComplete();
    }

    protected LocalChannel newLocalChannel(LocalChannel peer) {
        return new LocalChannel(this, peer);
    }

    private void serve0(LocalChannel child) {
        this.inboundBuffer.add(child);
        if (this.acceptInProgress) {
            this.acceptInProgress = false;
            this.readInbound();
        }
    }
}

