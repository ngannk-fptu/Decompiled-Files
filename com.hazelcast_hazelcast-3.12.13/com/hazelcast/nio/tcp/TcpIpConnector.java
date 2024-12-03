/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.internal.networking.Channel;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.tcp.BindRequest;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import com.hazelcast.util.AddressUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

class TcpIpConnector {
    private static final int DEFAULT_IPV6_SOCKET_CONNECT_TIMEOUT_SECONDS = 3;
    private static final int MILLIS_PER_SECOND = 1000;
    private final TcpIpEndpointManager endpointManager;
    private final ILogger logger;
    private final IOService ioService;
    private final int outboundPortCount;
    private final LinkedList<Integer> outboundPorts = new LinkedList();

    TcpIpConnector(TcpIpEndpointManager endpointManager) {
        this.endpointManager = endpointManager;
        this.ioService = endpointManager.getNetworkingService().getIoService();
        this.logger = this.ioService.getLoggingService().getLogger(this.getClass());
        Collection<Integer> ports = this.ioService.getOutboundPorts(endpointManager.getEndpointQualifier());
        this.outboundPortCount = ports.size();
        this.outboundPorts.addAll(ports);
    }

    void asyncConnect(Address address, boolean silent) {
        this.ioService.shouldConnectTo(address);
        this.ioService.executeAsync(new ConnectTask(address, silent));
    }

    private boolean useAnyOutboundPort() {
        return this.outboundPortCount == 0;
    }

    private int getOutboundPortCount() {
        return this.outboundPortCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int acquireOutboundPort() {
        if (this.useAnyOutboundPort()) {
            return 0;
        }
        LinkedList<Integer> linkedList = this.outboundPorts;
        synchronized (linkedList) {
            Integer port = this.outboundPorts.removeFirst();
            this.outboundPorts.addLast(port);
            return port;
        }
    }

    private final class ConnectTask
    implements Runnable {
        private final Address address;
        private final boolean silent;

        ConnectTask(Address address, boolean silent) {
            this.address = address;
            this.silent = silent;
        }

        @Override
        public void run() {
            if (!TcpIpConnector.this.endpointManager.getNetworkingService().isLive()) {
                if (TcpIpConnector.this.logger.isFinestEnabled()) {
                    TcpIpConnector.this.logger.finest("ConnectionManager is not live, connection attempt to " + this.address + " is cancelled!");
                }
                return;
            }
            if (TcpIpConnector.this.logger.isFinestEnabled()) {
                TcpIpConnector.this.logger.finest("Starting to connect to " + this.address);
            }
            try {
                Address thisAddress = TcpIpConnector.this.ioService.getThisAddress();
                if (this.address.isIPv4()) {
                    this.tryToConnect(this.address.getInetSocketAddress(), TcpIpConnector.this.ioService.getSocketConnectTimeoutSeconds(TcpIpConnector.this.endpointManager.getEndpointQualifier()) * 1000);
                } else if (thisAddress.isIPv6() && thisAddress.getScopeId() != null) {
                    Inet6Address inetAddress = AddressUtil.getInetAddressFor((Inet6Address)this.address.getInetAddress(), thisAddress.getScopeId());
                    this.tryToConnect(new InetSocketAddress(inetAddress, this.address.getPort()), TcpIpConnector.this.ioService.getSocketConnectTimeoutSeconds(TcpIpConnector.this.endpointManager.getEndpointQualifier()) * 1000);
                } else {
                    this.tryConnectToIPv6();
                }
            }
            catch (Throwable e) {
                TcpIpConnector.this.logger.finest(e);
                TcpIpConnector.this.endpointManager.failedConnection(this.address, e, this.silent);
            }
        }

        private void tryConnectToIPv6() throws Exception {
            Level level;
            Collection<Inet6Address> possibleInetAddresses = AddressUtil.getPossibleInetAddressesFor((Inet6Address)this.address.getInetAddress());
            Level level2 = level = this.silent ? Level.FINEST : Level.INFO;
            if (TcpIpConnector.this.logger.isLoggable(level)) {
                TcpIpConnector.this.logger.log(level, "Trying to connect possible IPv6 addresses: " + possibleInetAddresses);
            }
            boolean connected = false;
            Exception error = null;
            int configuredTimeoutMillis = TcpIpConnector.this.ioService.getSocketConnectTimeoutSeconds(TcpIpConnector.this.endpointManager.getEndpointQualifier()) * 1000;
            int timeoutMillis = configuredTimeoutMillis > 0 && configuredTimeoutMillis < Integer.MAX_VALUE ? configuredTimeoutMillis : 3000;
            for (Inet6Address inetAddress : possibleInetAddresses) {
                try {
                    this.tryToConnect(new InetSocketAddress(inetAddress, this.address.getPort()), timeoutMillis);
                    connected = true;
                    break;
                }
                catch (Exception e) {
                    error = e;
                }
            }
            if (!connected && error != null) {
                throw error;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void tryToConnect(InetSocketAddress socketAddress, int timeout) throws Exception {
            SocketChannel socketChannel = SocketChannel.open();
            TcpIpConnection connection = null;
            Channel channel = TcpIpConnector.this.endpointManager.newChannel(socketChannel, true);
            try {
                Level level;
                if (TcpIpConnector.this.ioService.isSocketBind()) {
                    this.bindSocket(socketChannel);
                }
                Level level2 = level = this.silent ? Level.FINEST : Level.INFO;
                if (TcpIpConnector.this.logger.isLoggable(level)) {
                    TcpIpConnector.this.logger.log(level, "Connecting to " + socketAddress + ", timeout: " + timeout + ", bind-any: " + TcpIpConnector.this.ioService.isSocketBindAny());
                }
                try {
                    channel.connect(socketAddress, timeout);
                    TcpIpConnector.this.ioService.interceptSocket(TcpIpConnector.this.endpointManager.getEndpointQualifier(), socketChannel.socket(), false);
                    connection = TcpIpConnector.this.endpointManager.newConnection(channel, this.address);
                    BindRequest request = new BindRequest(TcpIpConnector.this.logger, TcpIpConnector.this.ioService, connection, this.address, true);
                    request.send();
                }
                catch (NullPointerException e) {
                    this.closeConnection(connection, e);
                    this.closeSocket(socketChannel);
                    TcpIpConnector.this.logger.log(level, "Could not connect to: " + socketAddress + ". Reason: " + e.getClass().getSimpleName() + "[" + e.getMessage() + "]");
                    TcpIpConnector.this.logger.log(Level.INFO, "Add this stacktrace to https://github.com/hazelcast/hazelcast-enterprise/issues/2104 please!", e);
                    throw e;
                }
                catch (Exception e) {
                    this.closeConnection(connection, e);
                    this.closeSocket(socketChannel);
                    TcpIpConnector.this.logger.log(level, "Could not connect to: " + socketAddress + ". Reason: " + e.getClass().getSimpleName() + "[" + e.getMessage() + "]");
                    throw e;
                }
            }
            finally {
                TcpIpConnector.this.endpointManager.removeAcceptedChannel(channel);
            }
        }

        private void bindSocket(SocketChannel socketChannel) throws IOException {
            InetAddress inetAddress = this.getInetAddress();
            Socket socket = socketChannel.socket();
            if (!TcpIpConnector.this.useAnyOutboundPort()) {
                IOException ex = null;
                int retryCount = TcpIpConnector.this.getOutboundPortCount() * 2;
                for (int i = 0; i < retryCount; ++i) {
                    int port = TcpIpConnector.this.acquireOutboundPort();
                    InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
                    try {
                        socket.bind(socketAddress);
                        return;
                    }
                    catch (IOException e) {
                        ex = e;
                        TcpIpConnector.this.logger.finest("Could not bind port[ " + port + "]: " + e.getMessage());
                        continue;
                    }
                }
                throw ex;
            }
            InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, 0);
            socket.bind(socketAddress);
        }

        private InetAddress getInetAddress() throws UnknownHostException {
            return TcpIpConnector.this.ioService.isSocketBindAny() ? null : TcpIpConnector.this.ioService.getThisAddress().getInetAddress();
        }

        private void closeConnection(Connection connection, Throwable t) {
            if (connection != null) {
                connection.close(null, t);
            }
        }

        private void closeSocket(SocketChannel socketChannel) {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                }
                catch (IOException e) {
                    TcpIpConnector.this.logger.finest("Closing socket channel failed", e);
                }
            }
        }
    }
}

