/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void close(CloseMode closeMode) {
        this.connPool.close(closeMode);
    }

    @Override
    public void close() {
        this.connPool.close();
    }

    private ConnectionConfig resolveConnectionConfig(HttpHost httpHost) {
        Resolver<HttpHost, ConnectionConfig> resolver = this.connectionConfigResolver;
        ConnectionConfig connectionConfig = resolver != null ? resolver.resolve(httpHost) : null;
        return connectionConfig != null ? connectionConfig : ConnectionConfig.DEFAULT;
    }

    public Future<IOSession> getSession(HttpHost endpoint, Timeout connectTimeout, final FutureCallback<IOSession> callback) {
        final ConnectionConfig connectionConfig = this.resolveConnectionConfig(endpoint);
        return this.connPool.getSession(endpoint, connectTimeout != null ? connectTimeout : connectionConfig.getConnectTimeout(), (FutureCallback<IOSession>)new CallbackContribution<IOSession>(callback){

            @Override
            public void completed(IOSession ioSession) {
                Timeout socketTimeout = connectionConfig.getSocketTimeout();
                if (socketTimeout != null) {
                    ioSession.setSocketTimeout(socketTimeout);
                }
                callback.completed(ioSession);
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

