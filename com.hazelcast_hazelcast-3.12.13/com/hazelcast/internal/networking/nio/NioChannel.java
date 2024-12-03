/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.networking.AbstractChannel;
import com.hazelcast.internal.networking.ChannelInitializer;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.internal.networking.nio.NioChannelOptions;
import com.hazelcast.internal.networking.nio.NioInboundPipeline;
import com.hazelcast.internal.networking.nio.NioOutboundPipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public final class NioChannel
extends AbstractChannel {
    NioInboundPipeline inboundPipeline;
    NioOutboundPipeline outboundPipeline;
    private final Executor closeListenerExecutor;
    private final MetricsRegistry metricsRegistry;
    private final ChannelInitializer channelInitializer;
    private final NioChannelOptions config;

    public NioChannel(SocketChannel socketChannel, boolean clientMode, ChannelInitializer channelInitializer, MetricsRegistry metricsRegistry, Executor closeListenerExecutor) {
        super(socketChannel, clientMode);
        this.channelInitializer = channelInitializer;
        this.metricsRegistry = metricsRegistry;
        this.closeListenerExecutor = closeListenerExecutor;
        this.config = new NioChannelOptions(socketChannel.socket());
    }

    @Override
    public NioChannelOptions options() {
        return this.config;
    }

    public void init(NioInboundPipeline inboundPipeline, NioOutboundPipeline outboundPipeline) {
        this.inboundPipeline = inboundPipeline;
        this.outboundPipeline = outboundPipeline;
    }

    @Override
    public NioOutboundPipeline outboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public NioInboundPipeline inboundPipeline() {
        return this.inboundPipeline;
    }

    @Override
    public boolean write(OutboundFrame frame) {
        if (this.isClosed()) {
            return false;
        }
        this.outboundPipeline.write(frame);
        return true;
    }

    @Override
    protected void onConnect() {
        String metricsId = this.localSocketAddress() + "->" + this.remoteSocketAddress();
        this.metricsRegistry.scanAndRegister(this.outboundPipeline, "tcp.connection[" + metricsId + "].out");
        this.metricsRegistry.scanAndRegister(this.inboundPipeline, "tcp.connection[" + metricsId + "].in");
    }

    @Override
    public long lastReadTimeMillis() {
        return this.inboundPipeline.lastReadTimeMillis();
    }

    @Override
    public long lastWriteTimeMillis() {
        return this.outboundPipeline.lastWriteTimeMillis();
    }

    @Override
    public void start() {
        try {
            this.socketChannel.configureBlocking(false);
            this.channelInitializer.initChannel(this);
        }
        catch (Exception e) {
            throw new HazelcastException("Failed to start " + this, e);
        }
        this.inboundPipeline.start();
        this.outboundPipeline.start();
    }

    @Override
    protected void close0() {
        block6: {
            this.outboundPipeline.drainWriteQueues();
            try {
                this.socketChannel.close();
            }
            catch (IOException e) {
                if (!this.logger.isFineEnabled()) break block6;
                this.logger.fine("Failed to close " + this, e);
            }
        }
        if (Thread.currentThread() instanceof NioThread) {
            try {
                this.closeListenerExecutor.execute(new NotifyCloseListenersTask());
            }
            catch (RejectedExecutionException e) {
                this.logger.fine(e);
            }
        } else {
            this.notifyCloseListeners();
        }
    }

    @Override
    public long bytesRead() {
        return this.inboundPipeline.bytesRead();
    }

    @Override
    public long bytesWritten() {
        return this.outboundPipeline.bytesWritten();
    }

    public String toString() {
        return "NioChannel{" + this.localSocketAddress() + "->" + this.remoteSocketAddress() + '}';
    }

    private String getPort(SocketAddress socketAddress) {
        return socketAddress == null ? "*missing*" : Integer.toString(((InetSocketAddress)socketAddress).getPort());
    }

    private class NotifyCloseListenersTask
    implements Runnable {
        private NotifyCloseListenersTask() {
        }

        @Override
        public void run() {
            try {
                NioChannel.this.notifyCloseListeners();
            }
            catch (Exception e) {
                NioChannel.this.logger.warning(e.getMessage(), e);
            }
        }
    }
}

