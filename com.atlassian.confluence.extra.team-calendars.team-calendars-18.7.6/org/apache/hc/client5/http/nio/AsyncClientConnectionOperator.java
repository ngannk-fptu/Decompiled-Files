/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.nio;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.nio.ManagedAsyncClientConnection;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public interface AsyncClientConnectionOperator {
    public Future<ManagedAsyncClientConnection> connect(ConnectionInitiator var1, HttpHost var2, SocketAddress var3, Timeout var4, Object var5, FutureCallback<ManagedAsyncClientConnection> var6);

    default public Future<ManagedAsyncClientConnection> connect(ConnectionInitiator connectionInitiator, HttpHost host, SocketAddress localAddress, Timeout connectTimeout, Object attachment, HttpContext context, FutureCallback<ManagedAsyncClientConnection> callback) {
        return this.connect(connectionInitiator, host, localAddress, connectTimeout, attachment, callback);
    }

    public void upgrade(ManagedAsyncClientConnection var1, HttpHost var2, Object var3);

    default public void upgrade(ManagedAsyncClientConnection conn, HttpHost host, Object attachment, HttpContext context, FutureCallback<ManagedAsyncClientConnection> callback) {
        this.upgrade(conn, host, attachment, context);
        if (callback != null) {
            callback.completed(conn);
        }
    }

    default public void upgrade(ManagedAsyncClientConnection conn, HttpHost host, Object attachment, HttpContext context) {
        this.upgrade(conn, host, attachment);
    }
}

