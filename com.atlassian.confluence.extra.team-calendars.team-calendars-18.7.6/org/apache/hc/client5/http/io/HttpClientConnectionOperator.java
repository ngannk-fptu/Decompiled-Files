/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public interface HttpClientConnectionOperator {
    public void connect(ManagedHttpClientConnection var1, HttpHost var2, InetSocketAddress var3, TimeValue var4, SocketConfig var5, HttpContext var6) throws IOException;

    default public void connect(ManagedHttpClientConnection conn, HttpHost host, InetSocketAddress localAddress, Timeout connectTimeout, SocketConfig socketConfig, Object attachment, HttpContext context) throws IOException {
        this.connect(conn, host, localAddress, connectTimeout, socketConfig, context);
    }

    public void upgrade(ManagedHttpClientConnection var1, HttpHost var2, HttpContext var3) throws IOException;

    default public void upgrade(ManagedHttpClientConnection conn, HttpHost host, Object attachment, HttpContext context) throws IOException {
        this.upgrade(conn, host, context);
    }
}

