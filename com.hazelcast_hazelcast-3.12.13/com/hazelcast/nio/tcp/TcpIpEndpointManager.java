/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.internal.networking.Networking;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionLifecycleListener;
import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.tcp.BindHandler;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpConnectionErrorHandler;
import com.hazelcast.nio.tcp.TcpIpConnector;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.MutableLong;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.executor.StripedRunnable;
import com.hazelcast.util.function.Consumer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TcpIpEndpointManager
implements EndpointManager<TcpIpConnection>,
Consumer<Packet> {
    private static final int RETRY_NUMBER = 5;
    private static final long DELAY_FACTOR = 100L;
    @Probe(name="inProgressCount")
    final Set<Address> connectionsInProgress = Collections.newSetFromMap(new ConcurrentHashMap());
    @Probe(name="count", level=ProbeLevel.MANDATORY)
    final ConcurrentHashMap<Address, TcpIpConnection> connectionsMap = new ConcurrentHashMap(100);
    @Probe(name="activeCount", level=ProbeLevel.MANDATORY)
    final Set<TcpIpConnection> activeConnections = Collections.newSetFromMap(new ConcurrentHashMap());
    private final ILogger logger;
    private final IOService ioService;
    private final EndpointConfig endpointConfig;
    private final EndpointQualifier endpointQualifier;
    private final ChannelInitializerProvider channelInitializerProvider;
    private final NetworkingService networkingService;
    private final TcpIpConnector connector;
    private final BindHandler bindHandler;
    @Probe(name="connectionListenerCount")
    private final Set<ConnectionListener> connectionListeners = new CopyOnWriteArraySet<ConnectionListener>();
    private final ConstructorFunction<Address, TcpIpConnectionErrorHandler> monitorConstructor = new ConstructorFunction<Address, TcpIpConnectionErrorHandler>(){

        @Override
        public TcpIpConnectionErrorHandler createNew(Address endpoint) {
            return new TcpIpConnectionErrorHandler(TcpIpEndpointManager.this, endpoint);
        }
    };
    @Probe(name="monitorCount")
    private final ConcurrentHashMap<Address, TcpIpConnectionErrorHandler> monitors = new ConcurrentHashMap(100);
    private final AtomicInteger connectionIdGen = new AtomicInteger();
    @Probe
    private final MwCounter openedCount = MwCounter.newMwCounter();
    @Probe
    private final MwCounter closedCount = MwCounter.newMwCounter();
    private final BytesTransceivedCounter bytesReceived = new BytesTransceivedCounter(new ChannelBytesSupplier(){

        @Override
        public long get(Channel channel) {
            return channel.bytesRead();
        }
    });
    private final BytesTransceivedCounter bytesSent = new BytesTransceivedCounter(new ChannelBytesSupplier(){

        @Override
        public long get(Channel channel) {
            return channel.bytesWritten();
        }
    });
    @Probe(name="acceptedSocketCount", level=ProbeLevel.MANDATORY)
    private final Set<Channel> acceptedChannels = Collections.newSetFromMap(new ConcurrentHashMap());
    private final EndpointConnectionLifecycleListener connectionLifecycleListener = new EndpointConnectionLifecycleListener();

    TcpIpEndpointManager(NetworkingService networkingService, EndpointConfig endpointConfig, ChannelInitializerProvider channelInitializerProvider, IOService ioService, LoggingService loggingService, MetricsRegistry metricsRegistry, HazelcastProperties properties, Set<ProtocolType> supportedProtocolTypes) {
        this.networkingService = networkingService;
        this.endpointConfig = endpointConfig;
        this.endpointQualifier = endpointConfig != null ? endpointConfig.getQualifier() : null;
        this.channelInitializerProvider = channelInitializerProvider;
        this.ioService = ioService;
        this.logger = loggingService.getLogger(TcpIpEndpointManager.class);
        this.connector = new TcpIpConnector(this);
        boolean spoofingChecks = properties != null && properties.getBoolean(GroupProperty.BIND_SPOOFING_CHECKS);
        this.bindHandler = new BindHandler(this, ioService, this.logger, spoofingChecks, supportedProtocolTypes);
        if (this.endpointQualifier == null) {
            metricsRegistry.scanAndRegister(this, "tcp.connection");
        } else {
            metricsRegistry.scanAndRegister(this, this.endpointQualifier.toMetricsPrefixString() + ".tcp.connection");
        }
    }

    public NetworkingService getNetworkingService() {
        return this.networkingService;
    }

    public EndpointQualifier getEndpointQualifier() {
        return this.endpointQualifier;
    }

    @Override
    public Collection<TcpIpConnection> getActiveConnections() {
        return Collections.unmodifiableSet(this.activeConnections);
    }

    @Override
    public Collection<TcpIpConnection> getConnections() {
        return Collections.unmodifiableCollection(new HashSet<TcpIpConnection>(this.connectionsMap.values()));
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        Preconditions.checkNotNull(listener, "listener can't be null");
        this.connectionListeners.add(listener);
    }

    @Override
    public synchronized void accept(Packet packet) {
        this.bindHandler.process(packet);
    }

    @Override
    public TcpIpConnection getConnection(Address address) {
        return this.connectionsMap.get(address);
    }

    @Override
    public TcpIpConnection getOrConnect(Address address) {
        return this.getOrConnect(address, false);
    }

    @Override
    public TcpIpConnection getOrConnect(Address address, boolean silent) {
        TcpIpConnection connection = this.connectionsMap.get(address);
        if (connection == null && this.networkingService.isLive() && this.connectionsInProgress.add(address)) {
            this.connector.asyncConnect(address, silent);
        }
        return connection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized boolean registerConnection(final Address remoteEndPoint, final TcpIpConnection connection) {
        try {
            if (remoteEndPoint.equals(this.ioService.getThisAddress())) {
                boolean bl = false;
                return bl;
            }
            if (!connection.isAlive()) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest(connection + " to " + remoteEndPoint + " is not registered since connection is not active.");
                }
                boolean bl = false;
                return bl;
            }
            Address currentEndPoint = connection.getEndPoint();
            if (currentEndPoint != null && !currentEndPoint.equals(remoteEndPoint)) {
                throw new IllegalArgumentException(connection + " has already a different endpoint than: " + remoteEndPoint);
            }
            connection.setEndPoint(remoteEndPoint);
            if (!connection.isClient()) {
                TcpIpConnectionErrorHandler connectionMonitor = this.getErrorHandler(remoteEndPoint, true);
                connection.setErrorHandler(connectionMonitor);
            }
            this.connectionsMap.put(remoteEndPoint, connection);
            this.ioService.getEventService().executeEventCallback(new StripedRunnable(){

                @Override
                public void run() {
                    for (ConnectionListener listener : TcpIpEndpointManager.this.connectionListeners) {
                        listener.connectionAdded(connection);
                    }
                }

                @Override
                public int getKey() {
                    return remoteEndPoint.hashCode();
                }
            });
            boolean bl = true;
            return bl;
        }
        finally {
            this.connectionsInProgress.remove(remoteEndPoint);
        }
    }

    private void fireConnectionRemovedEvent(final Connection connection, final Address endPoint) {
        if (this.networkingService.isLive()) {
            this.ioService.getEventService().executeEventCallback(new StripedRunnable(){

                @Override
                public void run() {
                    for (ConnectionListener listener : TcpIpEndpointManager.this.connectionListeners) {
                        listener.connectionRemoved(connection);
                    }
                }

                @Override
                public int getKey() {
                    return endPoint.hashCode();
                }
            });
        }
    }

    public synchronized void reset(boolean cleanListeners) {
        for (Channel channel : this.acceptedChannels) {
            IOUtil.closeResource(channel);
        }
        for (Connection connection : this.connectionsMap.values()) {
            IOUtil.close(connection, "EndpointManager is stopping");
        }
        for (Connection connection : this.activeConnections) {
            IOUtil.close(connection, "EndpointManager is stopping");
        }
        this.acceptedChannels.clear();
        this.connectionsInProgress.clear();
        this.connectionsMap.clear();
        this.monitors.clear();
        this.activeConnections.clear();
        if (cleanListeners) {
            this.connectionListeners.clear();
        }
    }

    @Override
    public boolean transmit(Packet packet, TcpIpConnection connection) {
        Preconditions.checkNotNull(packet, "Packet can't be null");
        if (connection == null) {
            return false;
        }
        return connection.write(packet);
    }

    @Override
    public boolean transmit(Packet packet, Address target) {
        Preconditions.checkNotNull(packet, "Packet can't be null");
        Preconditions.checkNotNull(target, "target can't be null");
        return this.send(packet, target, null);
    }

    long calculateBytesReceived() {
        return this.bytesReceived.calculate();
    }

    long calculateBytesSent() {
        return this.bytesSent.calculate();
    }

    private TcpIpConnectionErrorHandler getErrorHandler(Address endpoint, boolean reset) {
        TcpIpConnectionErrorHandler monitor = ConcurrencyUtil.getOrPutIfAbsent(this.monitors, endpoint, this.monitorConstructor);
        if (reset) {
            monitor.reset();
        }
        return monitor;
    }

    Channel newChannel(SocketChannel socketChannel, boolean clientMode) throws IOException {
        Networking networking = this.getNetworkingService().getNetworking();
        Channel channel = networking.register(this.endpointQualifier, this.channelInitializerProvider, socketChannel, clientMode);
        if (this.endpointConfig != null) {
            IOUtil.setChannelOptions(channel, this.endpointConfig);
        }
        if (this.endpointQualifier != null) {
            channel.attributeMap().put(ProtocolType.class, this.endpointQualifier.getType());
        }
        this.acceptedChannels.add(channel);
        return channel;
    }

    void removeAcceptedChannel(Channel channel) {
        this.acceptedChannels.remove(channel);
    }

    void failedConnection(Address address, Throwable t, boolean silent) {
        this.connectionsInProgress.remove(address);
        this.ioService.onFailedConnection(address);
        if (!silent) {
            this.getErrorHandler(address, false).onError(t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized TcpIpConnection newConnection(Channel channel, Address endpoint) {
        try {
            if (!this.networkingService.isLive()) {
                throw new IllegalStateException("connection manager is not live!");
            }
            TcpIpConnection connection = new TcpIpConnection(this, this.connectionLifecycleListener, this.connectionIdGen.incrementAndGet(), channel);
            connection.setEndPoint(endpoint);
            this.activeConnections.add(connection);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Established socket connection between " + channel.localSocketAddress() + " and " + channel.remoteSocketAddress());
            }
            this.openedCount.inc();
            channel.start();
            TcpIpConnection tcpIpConnection = connection;
            return tcpIpConnection;
        }
        finally {
            this.acceptedChannels.remove(channel);
        }
    }

    private boolean send(Packet packet, Address target, SendTask sendTask) {
        block6: {
            int retries;
            TcpIpConnection connection = this.getConnection(target);
            if (connection != null) {
                return connection.write(packet);
            }
            if (sendTask == null) {
                sendTask = new SendTask(packet, target);
            }
            if ((retries = sendTask.retries) < 5 && this.ioService.isActive()) {
                this.getOrConnect(target, true);
                try {
                    this.networkingService.scheduleDeferred(sendTask, (long)(retries + 1) * 100L, TimeUnit.MILLISECONDS);
                    return true;
                }
                catch (RejectedExecutionException e) {
                    if (this.networkingService.isLive()) {
                        throw e;
                    }
                    if (!this.logger.isFinestEnabled()) break block6;
                    this.logger.finest("Packet send task is rejected. Packet cannot be sent to " + target);
                }
            }
        }
        return false;
    }

    public String toString() {
        return "TcpIpEndpointManager{endpointQualifier=" + this.endpointQualifier + ", connectionsMap=" + this.connectionsMap + '}';
    }

    int getAcceptedChannelsSize() {
        return this.acceptedChannels.size();
    }

    int getConnectionListenersCount() {
        return this.connectionListeners.size();
    }

    private static interface ChannelBytesSupplier {
        public long get(Channel var1);
    }

    private class BytesTransceivedCounter {
        private final ChannelBytesSupplier channelBytesSupplier;
        private final MwCounter bytesTransceivedOnClosed = MwCounter.newMwCounter();
        private final AtomicLong bytesTransceivedLastCalc = new AtomicLong();

        BytesTransceivedCounter(ChannelBytesSupplier channelBytesSupplier) {
            this.channelBytesSupplier = channelBytesSupplier;
        }

        void onConnectionClose(TcpIpConnection connection) {
            this.bytesTransceivedOnClosed.inc(this.channelBytesSupplier.get(connection.getChannel()));
        }

        long calculate() {
            MutableLong total = MutableLong.valueOf(this.bytesTransceivedOnClosed.get());
            for (TcpIpConnection conn : TcpIpEndpointManager.this.activeConnections) {
                total.value += this.channelBytesSupplier.get(conn.getChannel());
            }
            return this.updateToMaxAndGet(total.value);
        }

        private long updateToMaxAndGet(long candidateValue) {
            long next;
            long cur;
            while (!this.bytesTransceivedLastCalc.compareAndSet(cur = this.bytesTransceivedLastCalc.get(), next = Math.max(cur, candidateValue))) {
            }
            return next;
        }
    }

    public final class EndpointConnectionLifecycleListener
    implements ConnectionLifecycleListener<TcpIpConnection> {
        @Override
        public void onConnectionClose(TcpIpConnection connection, Throwable t, boolean silent) {
            TcpIpEndpointManager.this.closedCount.inc();
            TcpIpEndpointManager.this.activeConnections.remove(connection);
            TcpIpEndpointManager.this.bytesReceived.onConnectionClose(connection);
            TcpIpEndpointManager.this.bytesSent.onConnectionClose(connection);
            Address endPoint = connection.getEndPoint();
            if (endPoint != null) {
                TcpIpEndpointManager.this.connectionsInProgress.remove(endPoint);
                TcpIpEndpointManager.this.connectionsMap.remove(endPoint, connection);
                TcpIpEndpointManager.this.fireConnectionRemovedEvent(connection, endPoint);
            }
            if (t != null) {
                TcpIpEndpointManager.this.ioService.onFailedConnection(endPoint);
                if (!silent) {
                    TcpIpEndpointManager.this.getErrorHandler(endPoint, false).onError(t);
                }
            }
        }
    }

    private final class SendTask
    implements Runnable {
        private final Packet packet;
        private final Address target;
        private volatile int retries;

        private SendTask(Packet packet, Address target) {
            this.packet = packet;
            this.target = target;
        }

        @Override
        @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="single-writer, many-reader")
        public void run() {
            ++this.retries;
            if (TcpIpEndpointManager.this.logger.isFinestEnabled()) {
                TcpIpEndpointManager.this.logger.finest("Retrying[" + this.retries + "] packet send operation to: " + this.target);
            }
            TcpIpEndpointManager.this.send(this.packet, this.target, this);
        }
    }
}

