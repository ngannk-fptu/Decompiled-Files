/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.okhttp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;

public class DelegatingSocketFactory
extends SocketFactory {
    private final SocketFactory delegate;

    public DelegatingSocketFactory(SocketFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Socket createSocket() throws IOException {
        Socket socket = this.delegate.createSocket();
        return this.configureSocket(socket);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket socket = this.delegate.createSocket(host, port);
        return this.configureSocket(socket);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket socket = this.delegate.createSocket(host, port, localAddress, localPort);
        return this.configureSocket(socket);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        Socket socket = this.delegate.createSocket(host, port);
        return this.configureSocket(socket);
    }

    @Override
    public Socket createSocket(InetAddress host, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket socket = this.delegate.createSocket(host, port, localAddress, localPort);
        return this.configureSocket(socket);
    }

    protected Socket configureSocket(Socket socket) {
        return socket;
    }
}

