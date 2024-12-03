/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.CallbackContribution
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http2.nio.pool.H2ConnPool
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.ModalCloseable
 *  org.apache.hc.core5.reactor.ConnectionInitiator
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.client5.http.impl.async;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.core5.concurrent.CallbackContribution;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.nio.pool.H2ConnPool;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

class InternalH2ConnPool
implements ModalCloseable {
    private final H2ConnPool connPool;
    private volatile Resolver<HttpHost, ConnectionConfig> connectionConfigResolver;

    InternalH2ConnPool(ConnectionInitiator connectionInitiator, Resolver<HttpHost, InetSocketAddress> addressResolver, TlsStrategy tlsStrategy) {
        this.connPool = new H2ConnPool(connectionInitiator, addressResolver, tlsStrategy);
    }

    public void close(CloseMode closeMode) {
        this.connPool.close(closeMode);
    }

    public void close() {
        this.connPool.close();
    }

    private ConnectionConfig resolveConnectionConfig(HttpHost httpHost) {
        Resolver<HttpHost, ConnectionConfig> resolver = this.connectionConfigResolver;
        ConnectionConfig connectionConfig = resolver != null ? (ConnectionConfig)resolver.resolve((Object)httpHost) : null;
        return connectionConfig != null ? connectionConfig : ConnectionConfig.DEFAULT;
    }

    public Future<IOSession> getSession(HttpHost endpoint, Timeout connectTimeout, final FutureCallback<IOSession> callback) {
        final ConnectionConfig connectionConfig = this.resolveConnectionConfig(endpoint);
        return this.connPool.getSession((Object)endpoint, connectTimeout != null ? connectTimeout : connectionConfig.getConnectTimeout(), (FutureCallback)new CallbackContribution<IOSession>(callback){

            public void completed(IOSession ioSession) {
                Timeout socketTimeout = connectionConfig.getSocketTimeout();
                if (socketTimeout != null) {
                    ioSession.setSocketTimeout(socketTimeout);
                }
                callback.completed((Object)ioSession);
            }
        });
    }

    public void closeIdle(TimeValue idleTime) {
        this.connPool.closeIdle(idleTime);
    }

    public void setConnectionConfigResolver(Resolver<HttpHost, ConnectionConfig> connectionConfigResolver) {
        this.connectionConfigResolver = connectionConfigResolver;
    }
}

