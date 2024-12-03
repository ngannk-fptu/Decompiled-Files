/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.JavaVersion
 *  org.eclipse.jetty.util.Promise
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 *  org.eclipse.jetty.util.ssl.SslContextFactory$Client
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.io.SelectorManager;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.JavaVersion;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ClientConnector
extends ContainerLifeCycle {
    public static final String CLIENT_CONNECTOR_CONTEXT_KEY = "org.eclipse.jetty.client.connector";
    public static final String REMOTE_SOCKET_ADDRESS_CONTEXT_KEY = "org.eclipse.jetty.client.connector.remoteSocketAddress";
    public static final String CLIENT_CONNECTION_FACTORY_CONTEXT_KEY = "org.eclipse.jetty.client.connector.clientConnectionFactory";
    public static final String CONNECTION_PROMISE_CONTEXT_KEY = "org.eclipse.jetty.client.connector.connectionPromise";
    public static final String APPLICATION_PROTOCOLS_CONTEXT_KEY = "org.eclipse.jetty.client.connector.applicationProtocols";
    private static final Logger LOG = LoggerFactory.getLogger(ClientConnector.class);
    private final Configurator configurator;
    private Executor executor;
    private Scheduler scheduler;
    private ByteBufferPool byteBufferPool;
    private SslContextFactory.Client sslContextFactory;
    private SelectorManager selectorManager;
    private int selectors = 1;
    private boolean connectBlocking;
    private Duration connectTimeout = Duration.ofSeconds(5L);
    private Duration idleTimeout = Duration.ofSeconds(30L);
    private SocketAddress bindAddress;
    private boolean tcpNoDelay = true;
    private boolean reuseAddress = true;
    private boolean reusePort;
    private int receiveBufferSize = -1;
    private int sendBufferSize = -1;

    public static ClientConnector forUnixDomain(Path path) {
        return new ClientConnector(Configurator.forUnixDomain(path));
    }

    public ClientConnector() {
        this(new Configurator());
    }

    public ClientConnector(Configurator configurator) {
        this.configurator = Objects.requireNonNull(configurator);
        this.addBean((Object)configurator);
        configurator.addBean((Object)this, false);
    }

    public boolean isIntrinsicallySecure(SocketAddress address) {
        return this.configurator.isIntrinsicallySecure(this, address);
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public void setExecutor(Executor executor) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.updateBean(this.executor, executor);
        this.executor = executor;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.updateBean(this.scheduler, scheduler);
        this.scheduler = scheduler;
    }

    public ByteBufferPool getByteBufferPool() {
        return this.byteBufferPool;
    }

    public void setByteBufferPool(ByteBufferPool byteBufferPool) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.updateBean(this.byteBufferPool, byteBufferPool);
        this.byteBufferPool = byteBufferPool;
    }

    public SslContextFactory.Client getSslContextFactory() {
        return this.sslContextFactory;
    }

    public void setSslContextFactory(SslContextFactory.Client sslContextFactory) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.updateBean(this.sslContextFactory, sslContextFactory);
        this.sslContextFactory = sslContextFactory;
    }

    @ManagedAttribute(value="The number of NIO selectors")
    public int getSelectors() {
        return this.selectors;
    }

    public void setSelectors(int selectors) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.selectors = selectors;
    }

    @ManagedAttribute(value="Whether connect operations are performed in blocking mode")
    public boolean isConnectBlocking() {
        return this.connectBlocking;
    }

    public void setConnectBlocking(boolean connectBlocking) {
        this.connectBlocking = connectBlocking;
    }

    @ManagedAttribute(value="The timeout of connect operations")
    public Duration getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        if (this.selectorManager != null) {
            this.selectorManager.setConnectTimeout(connectTimeout.toMillis());
        }
    }

    @ManagedAttribute(value="The duration for which a connection can be idle")
    public Duration getIdleTimeout() {
        return this.idleTimeout;
    }

    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @ManagedAttribute(value="The socket address to bind sockets to before the connect operation")
    public SocketAddress getBindAddress() {
        return this.bindAddress;
    }

    public void setBindAddress(SocketAddress bindAddress) {
        this.bindAddress = bindAddress;
    }

    @ManagedAttribute(value="Whether small TCP packets are sent without delay")
    public boolean isTCPNoDelay() {
        return this.tcpNoDelay;
    }

    public void setTCPNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    @ManagedAttribute(value="Whether rebinding is allowed with sockets in tear-down states")
    public boolean getReuseAddress() {
        return this.reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    @ManagedAttribute(value="Whether binding to same host and port is allowed")
    public boolean isReusePort() {
        return this.reusePort;
    }

    public void setReusePort(boolean reusePort) {
        this.reusePort = reusePort;
    }

    @ManagedAttribute(value="The receive buffer size in bytes")
    public int getReceiveBufferSize() {
        return this.receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    @ManagedAttribute(value="The send buffer size in bytes")
    public int getSendBufferSize() {
        return this.sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    protected void doStart() throws Exception {
        if (this.executor == null) {
            QueuedThreadPool clientThreads = new QueuedThreadPool();
            clientThreads.setName(String.format("client-pool@%x", ((Object)((Object)this)).hashCode()));
            this.setExecutor((Executor)clientThreads);
        }
        if (this.scheduler == null) {
            this.setScheduler((Scheduler)new ScheduledExecutorScheduler(String.format("client-scheduler@%x", ((Object)((Object)this)).hashCode()), false));
        }
        if (this.byteBufferPool == null) {
            this.setByteBufferPool(new MappedByteBufferPool());
        }
        if (this.sslContextFactory == null) {
            this.setSslContextFactory(this.newSslContextFactory());
        }
        this.selectorManager = this.newSelectorManager();
        this.selectorManager.setConnectTimeout(this.getConnectTimeout().toMillis());
        this.addBean((Object)this.selectorManager);
        super.doStart();
    }

    protected void doStop() throws Exception {
        super.doStop();
        this.removeBean((Object)this.selectorManager);
    }

    protected SslContextFactory.Client newSslContextFactory() {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client(false);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
        return sslContextFactory;
    }

    protected SelectorManager newSelectorManager() {
        return new ClientSelectorManager(this.getExecutor(), this.getScheduler(), this.getSelectors());
    }

    public void connect(SocketAddress address, Map<String, Object> context) {
        SelectableChannel channel = null;
        try {
            if (context == null) {
                context = new ConcurrentHashMap<String, Object>();
            }
            context.put(CLIENT_CONNECTOR_CONTEXT_KEY, (Object)this);
            context.putIfAbsent(REMOTE_SOCKET_ADDRESS_CONTEXT_KEY, address);
            Configurator.ChannelWithAddress channelWithAddress = this.configurator.newChannelWithAddress(this, address, context);
            channel = channelWithAddress.getSelectableChannel();
            address = channelWithAddress.getSocketAddress();
            this.configure(channel);
            SocketAddress bindAddress = this.getBindAddress();
            if (bindAddress != null && channel instanceof NetworkChannel) {
                this.bind((NetworkChannel)((Object)channel), bindAddress);
            }
            boolean connected = true;
            if (channel instanceof SocketChannel) {
                boolean blocking;
                SocketChannel socketChannel = (SocketChannel)channel;
                boolean bl = blocking = this.isConnectBlocking() && address instanceof InetSocketAddress;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Connecting {} to {}", (Object)(blocking ? "blocking" : "non-blocking"), (Object)address);
                }
                if (blocking) {
                    socketChannel.socket().connect(address, (int)this.getConnectTimeout().toMillis());
                    socketChannel.configureBlocking(false);
                } else {
                    socketChannel.configureBlocking(false);
                    connected = socketChannel.connect(address);
                }
            } else {
                channel.configureBlocking(false);
            }
            if (connected) {
                this.selectorManager.accept(channel, context);
            } else {
                this.selectorManager.connect(channel, context);
            }
        }
        catch (Throwable x) {
            if (x.getClass() == SocketException.class) {
                x = new SocketException("Could not connect to " + address).initCause(x);
            }
            IO.close(channel);
            this.connectFailed(x, context);
        }
    }

    public void accept(SelectableChannel selectable, Map<String, Object> context) {
        block4: {
            try {
                SocketChannel channel = (SocketChannel)selectable;
                context.put(CLIENT_CONNECTOR_CONTEXT_KEY, (Object)this);
                if (!channel.isConnected()) {
                    throw new IllegalStateException("SocketChannel must be connected");
                }
                this.configure(channel);
                channel.configureBlocking(false);
                this.selectorManager.accept(channel, context);
            }
            catch (Throwable failure) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Could not accept {}", (Object)selectable);
                }
                IO.close((Closeable)selectable);
                Promise promise = (Promise)context.get(CONNECTION_PROMISE_CONTEXT_KEY);
                if (promise == null) break block4;
                promise.failed(failure);
            }
        }
    }

    private void bind(NetworkChannel channel, SocketAddress bindAddress) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Binding {} to {}", (Object)channel, (Object)bindAddress);
        }
        channel.bind(bindAddress);
    }

    protected void configure(SelectableChannel selectable) throws IOException {
        if (selectable instanceof NetworkChannel) {
            int sendBufferSize;
            NetworkChannel channel = (NetworkChannel)((Object)selectable);
            this.setSocketOption(channel, StandardSocketOptions.TCP_NODELAY, this.isTCPNoDelay());
            this.setSocketOption(channel, StandardSocketOptions.SO_REUSEADDR, this.getReuseAddress());
            this.setSocketOption(channel, StandardSocketOptions.SO_REUSEPORT, this.isReusePort());
            int receiveBufferSize = this.getReceiveBufferSize();
            if (receiveBufferSize >= 0) {
                this.setSocketOption(channel, StandardSocketOptions.SO_RCVBUF, receiveBufferSize);
            }
            if ((sendBufferSize = this.getSendBufferSize()) >= 0) {
                this.setSocketOption(channel, StandardSocketOptions.SO_SNDBUF, sendBufferSize);
            }
        }
    }

    private <T> void setSocketOption(NetworkChannel channel, SocketOption<T> option, T value) {
        block2: {
            try {
                channel.setOption(option, value);
            }
            catch (Throwable x) {
                if (!LOG.isTraceEnabled()) break block2;
                LOG.trace("Could not configure {} to {} on {}", new Object[]{option, value, channel, x});
            }
        }
    }

    protected EndPoint newEndPoint(SelectableChannel selectable, ManagedSelector selector, SelectionKey selectionKey) {
        Map context = (Map)selectionKey.attachment();
        SocketAddress address = (SocketAddress)context.get(REMOTE_SOCKET_ADDRESS_CONTEXT_KEY);
        return this.configurator.newEndPoint(this, address, selectable, selector, selectionKey);
    }

    protected Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
        SocketAddress address = (SocketAddress)context.get(REMOTE_SOCKET_ADDRESS_CONTEXT_KEY);
        return this.configurator.newConnection(this, address, endPoint, context);
    }

    protected void connectFailed(Throwable failure, Map<String, Object> context) {
        Promise promise;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Could not connect to {}", context.get(REMOTE_SOCKET_ADDRESS_CONTEXT_KEY));
        }
        if ((promise = (Promise)context.get(CONNECTION_PROMISE_CONTEXT_KEY)) != null) {
            promise.failed(failure);
        }
    }

    public static class Configurator
    extends ContainerLifeCycle {
        public boolean isIntrinsicallySecure(ClientConnector clientConnector, SocketAddress address) {
            return false;
        }

        public ChannelWithAddress newChannelWithAddress(ClientConnector clientConnector, SocketAddress address, Map<String, Object> context) throws IOException {
            return new ChannelWithAddress(SocketChannel.open(), address);
        }

        public EndPoint newEndPoint(ClientConnector clientConnector, SocketAddress address, SelectableChannel selectable, ManagedSelector selector, SelectionKey selectionKey) {
            return new SocketChannelEndPoint((SocketChannel)selectable, selector, selectionKey, clientConnector.getScheduler());
        }

        public Connection newConnection(ClientConnector clientConnector, SocketAddress address, EndPoint endPoint, Map<String, Object> context) throws IOException {
            ClientConnectionFactory factory = (ClientConnectionFactory)context.get(ClientConnector.CLIENT_CONNECTION_FACTORY_CONTEXT_KEY);
            return factory.newConnection(endPoint, context);
        }

        private static Configurator forUnixDomain(final Path path) {
            return new Configurator(){

                @Override
                public ChannelWithAddress newChannelWithAddress(ClientConnector clientConnector, SocketAddress address, Map<String, Object> context) {
                    try {
                        ProtocolFamily family = Enum.valueOf(StandardProtocolFamily.class, "UNIX");
                        SocketChannel socketChannel = (SocketChannel)SocketChannel.class.getMethod("open", ProtocolFamily.class).invoke(null, family);
                        Class<?> addressClass = Class.forName("java.net.UnixDomainSocketAddress");
                        SocketAddress socketAddress = (SocketAddress)addressClass.getMethod("of", Path.class).invoke(null, path);
                        return new ChannelWithAddress(socketChannel, socketAddress);
                    }
                    catch (Throwable x) {
                        String message = "Unix-Domain SocketChannels are available starting from Java 16, your Java version is: " + JavaVersion.VERSION;
                        throw new UnsupportedOperationException(message, x);
                    }
                }
            };
        }

        public static class ChannelWithAddress {
            private final SelectableChannel channel;
            private final SocketAddress address;

            public ChannelWithAddress(SelectableChannel channel, SocketAddress address) {
                this.channel = channel;
                this.address = address;
            }

            public SelectableChannel getSelectableChannel() {
                return this.channel;
            }

            public SocketAddress getSocketAddress() {
                return this.address;
            }
        }
    }

    protected class ClientSelectorManager
    extends SelectorManager {
        public ClientSelectorManager(Executor executor, Scheduler scheduler, int selectors) {
            super(executor, scheduler, selectors);
        }

        @Override
        protected EndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) {
            EndPoint endPoint = ClientConnector.this.newEndPoint(channel, selector, selectionKey);
            endPoint.setIdleTimeout(ClientConnector.this.getIdleTimeout().toMillis());
            return endPoint;
        }

        @Override
        public Connection newConnection(SelectableChannel channel, EndPoint endPoint, Object attachment) throws IOException {
            Map context = (Map)attachment;
            return ClientConnector.this.newConnection(endPoint, context);
        }

        @Override
        public void connectionOpened(Connection connection, Object context) {
            super.connectionOpened(connection, context);
            Map contextMap = (Map)context;
            Promise promise = (Promise)contextMap.get(ClientConnector.CONNECTION_PROMISE_CONTEXT_KEY);
            if (promise != null) {
                promise.succeeded((Object)connection);
            }
        }

        @Override
        protected void connectionFailed(SelectableChannel channel, Throwable failure, Object attachment) {
            Map context = (Map)attachment;
            ClientConnector.this.connectFailed(failure, context);
        }
    }
}

