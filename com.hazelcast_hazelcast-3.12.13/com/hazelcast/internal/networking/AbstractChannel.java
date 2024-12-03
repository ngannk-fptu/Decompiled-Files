/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelCloseListener;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class AbstractChannel
implements Channel {
    private static final int FALSE = 0;
    private static final int TRUE = 1;
    private static final AtomicIntegerFieldUpdater<AbstractChannel> CLOSED = AtomicIntegerFieldUpdater.newUpdater(AbstractChannel.class, "closed");
    private static final AtomicReferenceFieldUpdater<AbstractChannel, SocketAddress> LOCAL_ADDRESS = AtomicReferenceFieldUpdater.newUpdater(AbstractChannel.class, SocketAddress.class, "localAddress");
    private static final AtomicReferenceFieldUpdater<AbstractChannel, SocketAddress> REMOTE_ADDRESS = AtomicReferenceFieldUpdater.newUpdater(AbstractChannel.class, SocketAddress.class, "remoteAddress");
    protected final SocketChannel socketChannel;
    protected final ILogger logger;
    private final ConcurrentMap<?, ?> attributeMap = new ConcurrentHashMap();
    private final Set<ChannelCloseListener> closeListeners = Collections.newSetFromMap(new ConcurrentHashMap());
    private final boolean clientMode;
    private volatile SocketAddress remoteAddress;
    private volatile SocketAddress localAddress;
    private volatile int closed = 0;

    public AbstractChannel(SocketChannel socketChannel, boolean clientMode) {
        this.socketChannel = socketChannel;
        this.clientMode = clientMode;
        this.logger = Logger.getLogger(this.getClass());
    }

    @Override
    public boolean isClientMode() {
        return this.clientMode;
    }

    @Override
    public ConcurrentMap attributeMap() {
        return this.attributeMap;
    }

    @Override
    public Socket socket() {
        return this.socketChannel.socket();
    }

    public SocketChannel socketChannel() {
        return this.socketChannel;
    }

    @Override
    public SocketAddress remoteSocketAddress() {
        Socket socket;
        if (this.remoteAddress == null && (socket = this.socket()) != null) {
            REMOTE_ADDRESS.compareAndSet(this, null, socket.getRemoteSocketAddress());
        }
        return this.remoteAddress;
    }

    @Override
    public SocketAddress localSocketAddress() {
        Socket socket;
        if (this.localAddress == null && (socket = this.socket()) != null) {
            LOCAL_ADDRESS.compareAndSet(this, null, this.socket().getLocalSocketAddress());
        }
        return this.localAddress;
    }

    @Override
    public void connect(InetSocketAddress address, int timeoutMillis) throws IOException {
        try {
            Preconditions.checkNotNull(address, "address");
            Preconditions.checkNotNegative(timeoutMillis, "timeoutMillis can't be negative");
            this.socketChannel.configureBlocking(true);
            try {
                if (timeoutMillis > 0) {
                    this.socketChannel.socket().connect(address, timeoutMillis);
                } else {
                    this.socketChannel.connect(address);
                }
            }
            catch (SocketException ex) {
                SocketException newEx = new SocketException(ex.getMessage() + " to address " + address);
                newEx.setStackTrace(ex.getStackTrace());
                throw newEx;
            }
            this.onConnect();
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Successfully connected to: " + address + " using socket " + this.socketChannel.socket());
            }
        }
        catch (RuntimeException e) {
            IOUtil.closeResource(this);
            throw e;
        }
        catch (IOException e) {
            IOUtil.closeResource(this);
            throw e;
        }
    }

    protected void onConnect() {
    }

    @Override
    public boolean isClosed() {
        return this.closed == 1;
    }

    @Override
    public void close() throws IOException {
        if (!CLOSED.compareAndSet(this, 0, 1)) {
            return;
        }
        this.close0();
    }

    protected void close0() throws IOException {
    }

    @Override
    public void addCloseListener(ChannelCloseListener listener) {
        this.closeListeners.add(Preconditions.checkNotNull(listener, "listener"));
    }

    protected final void notifyCloseListeners() {
        for (ChannelCloseListener closeListener : this.closeListeners) {
            try {
                closeListener.onClose(this);
            }
            catch (Exception e) {
                this.logger.severe(String.format("Failed to process closeListener [%s] on channel [%s]", closeListener, this), e);
            }
        }
    }
}

