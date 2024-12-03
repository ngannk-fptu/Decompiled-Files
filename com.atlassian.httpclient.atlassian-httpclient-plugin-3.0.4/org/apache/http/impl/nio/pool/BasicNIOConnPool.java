/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.pool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.pool.BasicNIOConnFactory;
import org.apache.http.impl.nio.pool.BasicNIOPoolEntry;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.pool.AbstractNIOConnPool;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.pool.SocketAddressResolver;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class BasicNIOConnPool
extends AbstractNIOConnPool<HttpHost, NHttpClientConnection, BasicNIOPoolEntry> {
    private static final AtomicLong COUNTER = new AtomicLong();
    private final int connectTimeout;

    @Deprecated
    public BasicNIOConnPool(ConnectingIOReactor ioReactor, NIOConnFactory<HttpHost, NHttpClientConnection> connFactory, HttpParams params) {
        super(ioReactor, connFactory, 2, 20);
        Args.notNull(params, "HTTP parameters");
        this.connectTimeout = params.getIntParameter("http.connection.timeout", 0);
    }

    @Deprecated
    public BasicNIOConnPool(ConnectingIOReactor ioReactor, HttpParams params) {
        this(ioReactor, (NIOConnFactory<HttpHost, NHttpClientConnection>)new BasicNIOConnFactory(params), params);
    }

    public BasicNIOConnPool(ConnectingIOReactor ioReactor, NIOConnFactory<HttpHost, NHttpClientConnection> connFactory, int connectTimeout) {
        super(ioReactor, connFactory, new BasicAddressResolver(), 2, 20);
        this.connectTimeout = connectTimeout;
    }

    public BasicNIOConnPool(ConnectingIOReactor ioReactor, int connectTimeout, ConnectionConfig config) {
        this(ioReactor, (NIOConnFactory<HttpHost, NHttpClientConnection>)new BasicNIOConnFactory(config), connectTimeout);
    }

    public BasicNIOConnPool(ConnectingIOReactor ioReactor, ConnectionConfig config) {
        this(ioReactor, (NIOConnFactory<HttpHost, NHttpClientConnection>)new BasicNIOConnFactory(config), 0);
    }

    public BasicNIOConnPool(ConnectingIOReactor ioReactor) {
        this(ioReactor, (NIOConnFactory<HttpHost, NHttpClientConnection>)new BasicNIOConnFactory(ConnectionConfig.DEFAULT), 0);
    }

    @Override
    @Deprecated
    protected SocketAddress resolveRemoteAddress(HttpHost host) {
        return new InetSocketAddress(host.getHostName(), host.getPort());
    }

    @Override
    @Deprecated
    protected SocketAddress resolveLocalAddress(HttpHost host) {
        return null;
    }

    @Override
    protected BasicNIOPoolEntry createEntry(HttpHost host, NHttpClientConnection conn) {
        BasicNIOPoolEntry entry = new BasicNIOPoolEntry(Long.toString(COUNTER.getAndIncrement()), host, conn);
        entry.setSocketTimeout(conn.getSocketTimeout());
        return entry;
    }

    @Override
    public Future<BasicNIOPoolEntry> lease(HttpHost route, Object state, FutureCallback<BasicNIOPoolEntry> callback) {
        return super.lease(route, state, this.connectTimeout, TimeUnit.MILLISECONDS, callback);
    }

    @Override
    public Future<BasicNIOPoolEntry> lease(HttpHost route, Object state) {
        return super.lease(route, state, this.connectTimeout, TimeUnit.MILLISECONDS, null);
    }

    @Override
    protected void onLease(BasicNIOPoolEntry entry) {
        NHttpClientConnection conn = (NHttpClientConnection)entry.getConnection();
        conn.setSocketTimeout(entry.getSocketTimeout());
    }

    @Override
    protected void onRelease(BasicNIOPoolEntry entry) {
        NHttpClientConnection conn = (NHttpClientConnection)entry.getConnection();
        entry.setSocketTimeout(conn.getSocketTimeout());
        conn.setSocketTimeout(0);
    }

    static class BasicAddressResolver
    implements SocketAddressResolver<HttpHost> {
        BasicAddressResolver() {
        }

        @Override
        public SocketAddress resolveLocalAddress(HttpHost host) {
            return null;
        }

        @Override
        public SocketAddress resolveRemoteAddress(HttpHost host) {
            String hostname = host.getHostName();
            int port = host.getPort();
            if (port == -1) {
                if (host.getSchemeName().equalsIgnoreCase("http")) {
                    port = 80;
                } else if (host.getSchemeName().equalsIgnoreCase("https")) {
                    port = 443;
                }
            }
            return new InetSocketAddress(hostname, port);
        }
    }
}

