/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface ConnectionSocketFactory {
    public Socket createSocket(HttpContext var1) throws IOException;

    public Socket connectSocket(TimeValue var1, Socket var2, HttpHost var3, InetSocketAddress var4, InetSocketAddress var5, HttpContext var6) throws IOException;

    default public Socket connectSocket(Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, Timeout connectTimeout, Object attachment, HttpContext context) throws IOException {
        return this.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
    }
}

