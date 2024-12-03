/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.apache.internal.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.apache.internal.net.DelegateSocket;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class SdkSocket
extends DelegateSocket {
    private static final Logger log = Logger.loggerFor(SdkSocket.class);

    public SdkSocket(Socket sock) {
        super(sock);
        log.debug(() -> "created: " + this.endpoint());
    }

    private String endpoint() {
        return this.sock.getInetAddress() + ":" + this.sock.getPort();
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        log.trace(() -> "connecting to: " + endpoint);
        this.sock.connect(endpoint);
        log.debug(() -> "connected to: " + endpoint);
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        log.trace(() -> "connecting to: " + endpoint);
        this.sock.connect(endpoint, timeout);
        log.debug(() -> "connected to: " + endpoint);
    }

    @Override
    public void close() throws IOException {
        log.debug(() -> "closing " + this.endpoint());
        this.sock.close();
    }

    @Override
    public void shutdownInput() throws IOException {
        log.debug(() -> "shutting down input of " + this.endpoint());
        this.sock.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        log.debug(() -> "shutting down output of " + this.endpoint());
        this.sock.shutdownOutput();
    }
}

