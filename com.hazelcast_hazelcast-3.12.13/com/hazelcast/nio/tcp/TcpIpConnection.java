/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionLifecycleListener;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.tcp.TcpIpConnectionErrorHandler;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import java.io.EOFException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class TcpIpConnection
implements Connection {
    private final Channel channel;
    private final TcpIpEndpointManager endpointManager;
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final AtomicBoolean binding = new AtomicBoolean();
    private final ILogger logger;
    private final int connectionId;
    private final IOService ioService;
    private Address endPoint;
    private TcpIpConnectionErrorHandler errorHandler;
    private volatile ConnectionType type = ConnectionType.NONE;
    private volatile ConnectionLifecycleListener lifecycleListener;
    private volatile Throwable closeCause;
    private volatile String closeReason;

    public TcpIpConnection(TcpIpEndpointManager endpointManager, ConnectionLifecycleListener lifecycleListener, int connectionId, Channel channel) {
        this.connectionId = connectionId;
        this.endpointManager = endpointManager;
        this.lifecycleListener = lifecycleListener;
        this.ioService = endpointManager.getNetworkingService().getIoService();
        this.logger = this.ioService.getLoggingService().getLogger(TcpIpConnection.class);
        this.channel = channel;
        channel.attributeMap().put(TcpIpConnection.class, this);
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ConnectionType getType() {
        return this.type;
    }

    @Override
    public void setType(ConnectionType type) {
        if (this.type != ConnectionType.NONE) {
            return;
        }
        this.type = type;
        if (type == ConnectionType.MEMBER) {
            this.logger.info("Initialized new cluster connection between " + this.channel.localSocketAddress() + " and " + this.channel.remoteSocketAddress());
        }
    }

    @Probe
    private int getConnectionType() {
        ConnectionType t = this.type;
        return t == null ? -1 : t.ordinal();
    }

    @Override
    public TcpIpEndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    @Override
    public InetAddress getInetAddress() {
        return this.channel.socket().getInetAddress();
    }

    @Override
    public int getPort() {
        return this.channel.socket().getPort();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress)this.channel.remoteSocketAddress();
    }

    @Override
    public boolean isAlive() {
        return this.alive.get();
    }

    @Override
    public long lastWriteTimeMillis() {
        return this.channel.lastWriteTimeMillis();
    }

    @Override
    public long lastReadTimeMillis() {
        return this.channel.lastReadTimeMillis();
    }

    @Override
    public Address getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(Address endPoint) {
        this.endPoint = endPoint;
    }

    public void setErrorHandler(TcpIpConnectionErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public int getConnectionId() {
        return this.connectionId;
    }

    @Override
    public boolean isClient() {
        ConnectionType t = this.type;
        return t != null && t != ConnectionType.NONE && t.isClient();
    }

    @Override
    public boolean write(OutboundFrame frame) {
        if (this.channel.write(frame)) {
            return true;
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Connection is closed, won't write packet -> " + frame);
        }
        return false;
    }

    public int hashCode() {
        return TcpIpConnection.hash(this.channel.isClientMode(), this.connectionId);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TcpIpConnection other = (TcpIpConnection)obj;
        return this.channel.isClientMode() == other.channel.isClientMode() && this.connectionId == other.connectionId && TcpIpConnection.equals(this.endPoint, other.endPoint);
    }

    @Override
    public void close(String reason, Throwable cause) {
        if (!this.alive.compareAndSet(true, false)) {
            return;
        }
        this.closeCause = cause;
        this.closeReason = reason;
        this.logClose();
        try {
            this.channel.close();
        }
        catch (Exception e) {
            this.logger.warning(e);
        }
        this.lifecycleListener.onConnectionClose(this, null, false);
        this.ioService.onDisconnect(this.endPoint, cause);
        if (cause != null && this.errorHandler != null) {
            this.errorHandler.onError(cause);
        }
    }

    public boolean setBinding() {
        return this.binding.compareAndSet(false, true);
    }

    private void logClose() {
        Level logLevel = this.resolveLogLevelOnClose();
        if (!this.logger.isLoggable(logLevel)) {
            return;
        }
        String message = this.toString() + " closed. Reason: ";
        message = this.closeReason != null ? message + this.closeReason : (this.closeCause != null ? message + this.closeCause.getClass().getName() + "[" + this.closeCause.getMessage() + "]" : message + "Socket explicitly closed");
        if (Level.FINEST.equals(logLevel)) {
            this.logger.log(logLevel, message, this.closeCause);
        } else if (this.closeCause == null || this.closeCause instanceof EOFException || this.closeCause instanceof CancelledKeyException) {
            this.logger.log(logLevel, message);
        } else {
            this.logger.log(logLevel, message, this.closeCause);
        }
    }

    private Level resolveLogLevelOnClose() {
        if (!this.ioService.isActive()) {
            return Level.FINEST;
        }
        if (this.closeCause == null || this.closeCause instanceof EOFException || this.closeCause instanceof CancelledKeyException) {
            if (this.type == ConnectionType.REST_CLIENT || this.type == ConnectionType.MEMCACHE_CLIENT) {
                return Level.FINE;
            }
            return Level.INFO;
        }
        return Level.WARNING;
    }

    @Override
    public Throwable getCloseCause() {
        return this.closeCause;
    }

    @Override
    public String getCloseReason() {
        if (this.closeReason == null) {
            return this.closeCause == null ? null : this.closeCause.getMessage();
        }
        return this.closeReason;
    }

    public String toString() {
        return "Connection[id=" + this.connectionId + ", " + this.channel.localSocketAddress() + "->" + this.channel.remoteSocketAddress() + ", qualifier=" + this.endpointManager.getEndpointQualifier() + ", endpoint=" + this.endPoint + ", alive=" + this.alive + ", type=" + (Object)((Object)this.type) + "]";
    }

    private static int hash(Object ... values) {
        return Arrays.hashCode(values);
    }

    private static boolean equals(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }
}

