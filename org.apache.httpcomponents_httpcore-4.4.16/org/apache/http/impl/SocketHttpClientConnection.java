/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.apache.http.HttpInetConnection;
import org.apache.http.impl.AbstractHttpClientConnection;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public class SocketHttpClientConnection
extends AbstractHttpClientConnection
implements HttpInetConnection {
    private volatile boolean open;
    private volatile Socket socket = null;

    protected void assertNotOpen() {
        Asserts.check(!this.open, "Connection is already open");
    }

    @Override
    protected void assertOpen() {
        Asserts.check(this.open, "Connection is not open");
    }

    protected SessionInputBuffer createSessionInputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
        return new SocketInputBuffer(socket, bufferSize, params);
    }

    protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
        return new SocketOutputBuffer(socket, bufferSize, params);
    }

    protected void bind(Socket socket, HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        Args.notNull(params, "HTTP parameters");
        this.socket = socket;
        int bufferSize = params.getIntParameter("http.socket.buffer-size", -1);
        this.init(this.createSessionInputBuffer(socket, bufferSize, params), this.createSessionOutputBuffer(socket, bufferSize, params), params);
        this.open = true;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    protected Socket getSocket() {
        return this.socket;
    }

    @Override
    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }

    @Override
    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }

    @Override
    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }

    @Override
    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            }
            catch (SocketException socketException) {
                // empty catch block
            }
        }
    }

    @Override
    public int getSocketTimeout() {
        if (this.socket != null) {
            try {
                return this.socket.getSoTimeout();
            }
            catch (SocketException socketException) {
                // empty catch block
            }
        }
        return -1;
    }

    @Override
    public void shutdown() throws IOException {
        this.open = false;
        Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (!this.open) {
            return;
        }
        this.open = false;
        Socket sock = this.socket;
        try {
            this.doFlush();
            try {
                try {
                    sock.shutdownOutput();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                try {
                    sock.shutdownInput();
                }
                catch (IOException iOException) {
                }
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
        }
        finally {
            sock.close();
        }
    }

    private static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress)socketAddress;
            buffer.append(addr.getAddress() != null ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
        } else {
            buffer.append(socketAddress);
        }
    }

    public String toString() {
        if (this.socket != null) {
            StringBuilder buffer = new StringBuilder();
            SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
            SocketAddress localAddress = this.socket.getLocalSocketAddress();
            if (remoteAddress != null && localAddress != null) {
                SocketHttpClientConnection.formatAddress(buffer, localAddress);
                buffer.append("<->");
                SocketHttpClientConnection.formatAddress(buffer, remoteAddress);
            }
            return buffer.toString();
        }
        return super.toString();
    }
}

