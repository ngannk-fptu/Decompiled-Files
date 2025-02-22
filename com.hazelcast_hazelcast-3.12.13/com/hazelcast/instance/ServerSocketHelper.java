/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.TimeUnit;

final class ServerSocketHelper {
    private static final int SOCKET_TIMEOUT_MILLIS = (int)TimeUnit.SECONDS.toMillis(1L);
    private static final int SOCKET_BACKLOG_LENGTH = 100;

    private ServerSocketHelper() {
    }

    static ServerSocketChannel createServerSocketChannel(ILogger logger, EndpointConfig endpointConfig, InetAddress bindAddress, int port, int portCount, boolean isPortAutoIncrement, boolean isReuseAddress, boolean bindAny) {
        logger.finest("inet reuseAddress:" + isReuseAddress);
        if (port == 0) {
            logger.info("No explicit port is given, system will pick up an ephemeral port.");
        }
        int portTrialCount = port > 0 && isPortAutoIncrement ? portCount : 1;
        try {
            return ServerSocketHelper.tryOpenServerSocketChannel(endpointConfig, bindAddress, port, isReuseAddress, portTrialCount, bindAny, logger);
        }
        catch (IOException e) {
            String message = "Cannot bind to a given address: " + bindAddress + ". Hazelcast cannot start. ";
            message = isPortAutoIncrement ? message + "Config-port: " + port + ", latest-port: " + (port + portTrialCount - 1) : message + "Port [" + port + "] is already in use and auto-increment is disabled.";
            throw new HazelcastException(message, e);
        }
    }

    private static ServerSocketChannel tryOpenServerSocketChannel(EndpointConfig endpointConfig, InetAddress bindAddress, int initialPort, boolean isReuseAddress, int portTrialCount, boolean bindAny, ILogger logger) throws IOException {
        assert (portTrialCount > 0) : "Port trial count must be positive: " + portTrialCount;
        IOException error = null;
        for (int i = 0; i < portTrialCount; ++i) {
            int actualPort = initialPort + i;
            InetSocketAddress socketBindAddress = bindAny ? new InetSocketAddress(actualPort) : new InetSocketAddress(bindAddress, actualPort);
            try {
                return ServerSocketHelper.openServerSocketChannel(endpointConfig, socketBindAddress, isReuseAddress, logger);
            }
            catch (IOException e) {
                error = e;
                continue;
            }
        }
        throw error;
    }

    private static ServerSocketChannel openServerSocketChannel(EndpointConfig endpointConfig, InetSocketAddress socketBindAddress, boolean reuseAddress, ILogger logger) throws IOException {
        ServerSocket serverSocket = null;
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocket = serverSocketChannel.socket();
            serverSocket.setReuseAddress(reuseAddress);
            serverSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
            if (endpointConfig != null) {
                serverSocket.setReceiveBufferSize(endpointConfig.getSocketRcvBufferSizeKb() * 1024);
            }
            logger.fine("Trying to bind inet socket address: " + socketBindAddress);
            serverSocket.bind(socketBindAddress, 100);
            logger.fine("Bind successful to inet socket address: " + serverSocket.getLocalSocketAddress());
            serverSocketChannel.configureBlocking(false);
            return serverSocketChannel;
        }
        catch (IOException e) {
            IOUtil.close(serverSocket);
            IOUtil.closeResource(serverSocketChannel);
            throw e;
        }
    }
}

