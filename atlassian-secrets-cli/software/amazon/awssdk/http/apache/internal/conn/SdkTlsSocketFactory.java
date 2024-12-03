/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.ssl.SSLConnectionSocketFactory
 */
package software.amazon.awssdk.http.apache.internal.conn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.apache.internal.net.SdkSocket;
import software.amazon.awssdk.http.apache.internal.net.SdkSslSocket;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class SdkTlsSocketFactory
extends SSLConnectionSocketFactory {
    private static final Logger log = Logger.loggerFor(SdkTlsSocketFactory.class);
    private final SSLContext sslContext;

    public SdkTlsSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        super(sslContext, hostnameVerifier);
        if (sslContext == null) {
            throw new IllegalArgumentException("sslContext must not be null. Use SSLContext.getDefault() if you are unsure.");
        }
        this.sslContext = sslContext;
    }

    protected final void prepareSocket(SSLSocket socket) {
        log.debug(() -> String.format("socket.getSupportedProtocols(): %s, socket.getEnabledProtocols(): %s", Arrays.toString(socket.getSupportedProtocols()), Arrays.toString(socket.getEnabledProtocols())));
    }

    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        log.trace(() -> String.format("Connecting to %s:%s", remoteAddress.getAddress(), remoteAddress.getPort()));
        Socket connectedSocket = super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
        if (connectedSocket instanceof SSLSocket) {
            return new SdkSslSocket((SSLSocket)connectedSocket);
        }
        return new SdkSocket(connectedSocket);
    }
}

