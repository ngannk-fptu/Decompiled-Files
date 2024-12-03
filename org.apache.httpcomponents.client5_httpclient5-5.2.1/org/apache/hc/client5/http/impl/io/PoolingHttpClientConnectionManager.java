/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.Registry
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.impl.io.HttpRequestExecutor
 *  org.apache.hc.core5.http.io.HttpClientConnection
 *  org.apache.hc.core5.http.io.HttpConnectionFactory
 *  org.apache.hc.core5.http.io.SocketConfig
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.ModalCloseable
 *  org.apache.hc.core5.pool.ConnPoolControl
 *  org.apache.hc.core5.pool.LaxConnPool
 *  org.apache.hc.core5.pool.ManagedConnPool
 *  org.apache.hc.core5.pool.PoolConcurrencyPolicy
 *  org.apache.hc.core5.pool.PoolEntry
 *  org.apache.hc.core5.pool.PoolReusePolicy
 *  org.apache.hc.core5.pool.PoolStats
 *  org.apache.hc.core5.pool.StrictConnPool
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Asserts
 *  org.apache.hc.core5.util.Deadline
 *  org.apache.hc.core5.util.Identifiable
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.io;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.ConnPoolSupport;
import org.apache.hc.client5.http.impl.ConnectionShutdownException;
import org.apache.hc.client5.http.impl.PrefixedIncrementingId;
import org.apache.hc.client5.http.impl.io.DefaultHttpClientConnectionOperator;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.io.ConnectionEndpoint;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionOperator;
import org.apache.hc.client5.http.io.LeaseRequest;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.pool.ConnPoolControl;
import org.apache.hc.core5.pool.LaxConnPool;
import org.apache.hc.core5.pool.ManagedConnPool;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolEntry;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.pool.StrictConnPool;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.Deadline;
import org.apache.hc.core5.util.Identifiable;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class PoolingHttpClientConnectionManager
implements HttpClientConnectionManager,
ConnPoolControl<HttpRoute> {
    private static final Logger LOG = LoggerFactory.getLogger(PoolingHttpClientConnectionManager.class);
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 25;
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
    private final HttpClientConnectionOperator connectionOperator;
    private final ManagedConnPool<HttpRoute, ManagedHttpClientConnection> pool;
    private final HttpConnectionFactory<ManagedHttpClientConnection> connFactory;
    private final AtomicBoolean closed;
    private volatile Resolver<HttpRoute, SocketConfig> socketConfigResolver;
    private volatile Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver;
    private volatile Resolver<HttpHost, TlsConfig> tlsConfigResolver;
    private static final PrefixedIncrementingId INCREMENTING_ID = new PrefixedIncrementingId("ep-");

    public PoolingHttpClientConnectionManager() {
        this((Registry<ConnectionSocketFactory>)RegistryBuilder.create().register(URIScheme.HTTP.id, (Object)PlainConnectionSocketFactory.getSocketFactory()).register(URIScheme.HTTPS.id, (Object)SSLConnectionSocketFactory.getSocketFactory()).build());
    }

    public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry) {
        this(socketFactoryRegistry, null);
    }

    public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<ManagedHttpClientConnection> connFactory) {
        this(socketFactoryRegistry, PoolConcurrencyPolicy.STRICT, TimeValue.NEG_ONE_MILLISECOND, connFactory);
    }

    public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, PoolConcurrencyPolicy poolConcurrencyPolicy, TimeValue timeToLive, HttpConnectionFactory<ManagedHttpClientConnection> connFactory) {
        this(socketFactoryRegistry, poolConcurrencyPolicy, PoolReusePolicy.LIFO, timeToLive, connFactory);
    }

    public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive) {
        this(socketFactoryRegistry, poolConcurrencyPolicy, poolReusePolicy, timeToLive, null);
    }

    public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive, HttpConnectionFactory<ManagedHttpClientConnection> connFactory) {
        this(socketFactoryRegistry, poolConcurrencyPolicy, poolReusePolicy, timeToLive, null, null, connFactory);
    }

    public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive, SchemePortResolver schemePortResolver, DnsResolver dnsResolver, HttpConnectionFactory<ManagedHttpClientConnection> connFactory) {
        this(new DefaultHttpClientConnectionOperator((Lookup<ConnectionSocketFactory>)socketFactoryRegistry, schemePortResolver, dnsResolver), poolConcurrencyPolicy, poolReusePolicy, timeToLive, connFactory);
    }

    @Internal
    protected PoolingHttpClientConnectionManager(HttpClientConnectionOperator httpClientConnectionOperator, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive, HttpConnectionFactory<ManagedHttpClientConnection> connFactory) {
        this.connectionOperator = (HttpClientConnectionOperator)Args.notNull((Object)httpClientConnectionOperator, (String)"Connection operator");
        switch (poolConcurrencyPolicy != null ? poolConcurrencyPolicy : PoolConcurrencyPolicy.STRICT) {
            case STRICT: {
                this.pool = new StrictConnPool<HttpRoute, ManagedHttpClientConnection>(5, 25, timeToLive, poolReusePolicy, null){

                    public void closeExpired() {
                        this.enumAvailable(e -> PoolingHttpClientConnectionManager.this.closeIfExpired((PoolEntry<HttpRoute, ManagedHttpClientConnection>)e));
                    }
                };
                break;
            }
            case LAX: {
                this.pool = new LaxConnPool<HttpRoute, ManagedHttpClientConnection>(5, timeToLive, poolReusePolicy, null){

                    public void closeExpired() {
                        this.enumAvailable(e -> PoolingHttpClientConnectionManager.this.closeIfExpired((PoolEntry<HttpRoute, ManagedHttpClientConnection>)e));
                    }
                };
                break;
            }
            default: {
                throw new IllegalArgumentException("Unexpected PoolConcurrencyPolicy value: " + poolConcurrencyPolicy);
            }
        }
        this.connFactory = connFactory != null ? connFactory : ManagedHttpClientConnectionFactory.INSTANCE;
        this.closed = new AtomicBoolean(false);
    }

    @Internal
    protected PoolingHttpClientConnectionManager(HttpClientConnectionOperator httpClientConnectionOperator, ManagedConnPool<HttpRoute, ManagedHttpClientConnection> pool, HttpConnectionFactory<ManagedHttpClientConnection> connFactory) {
        this.connectionOperator = (HttpClientConnectionOperator)Args.notNull((Object)httpClientConnectionOperator, (String)"Connection operator");
        this.pool = (ManagedConnPool)Args.notNull(pool, (String)"Connection pool");
        this.connFactory = connFactory != null ? connFactory : ManagedHttpClientConnectionFactory.INSTANCE;
        this.closed = new AtomicBoolean(false);
    }

    public void close() {
        this.close(CloseMode.GRACEFUL);
    }

    public void close(CloseMode closeMode) {
        if (this.closed.compareAndSet(false, true)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Shutdown connection pool {}", (Object)closeMode);
            }
            this.pool.close(closeMode);
            LOG.debug("Connection pool shut down");
        }
    }

    private InternalConnectionEndpoint cast(ConnectionEndpoint endpoint) {
        if (endpoint instanceof InternalConnectionEndpoint) {
            return (InternalConnectionEndpoint)endpoint;
        }
        throw new IllegalStateException("Unexpected endpoint class: " + endpoint.getClass());
    }

    private SocketConfig resolveSocketConfig(HttpRoute route) {
        Resolver<HttpRoute, SocketConfig> resolver = this.socketConfigResolver;
        SocketConfig socketConfig = resolver != null ? (SocketConfig)resolver.resolve((Object)route) : null;
        return socketConfig != null ? socketConfig : SocketConfig.DEFAULT;
    }

    private ConnectionConfig resolveConnectionConfig(HttpRoute route) {
        Resolver<HttpRoute, ConnectionConfig> resolver = this.connectionConfigResolver;
        ConnectionConfig connectionConfig = resolver != null ? (ConnectionConfig)resolver.resolve((Object)route) : null;
        return connectionConfig != null ? connectionConfig : ConnectionConfig.DEFAULT;
    }

    private TlsConfig resolveTlsConfig(HttpHost host) {
        Resolver<HttpHost, TlsConfig> resolver = this.tlsConfigResolver;
        TlsConfig tlsConfig = resolver != null ? (TlsConfig)resolver.resolve((Object)host) : null;
        return tlsConfig != null ? tlsConfig : TlsConfig.DEFAULT;
    }

    private TimeValue resolveValidateAfterInactivity(ConnectionConfig connectionConfig) {
        TimeValue timeValue = connectionConfig.getValidateAfterInactivity();
        return timeValue != null ? timeValue : TimeValue.ofSeconds((long)2L);
    }

    public LeaseRequest lease(String id, HttpRoute route, Object state) {
        return this.lease(id, route, Timeout.DISABLED, state);
    }

    @Override
    public LeaseRequest lease(final String id, final HttpRoute route, Timeout requestTimeout, final Object state) {
        Args.notNull((Object)route, (String)"HTTP route");
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} endpoint lease request ({}) {}", new Object[]{id, requestTimeout, ConnPoolSupport.formatStats(route, state, this.pool)});
        }
        final Future leaseFuture = this.pool.lease((Object)route, state, requestTimeout, null);
        return new LeaseRequest(){
            private volatile ConnectionEndpoint endpoint;

            @Override
            public synchronized ConnectionEndpoint get(Timeout timeout) throws InterruptedException, ExecutionException, TimeoutException {
                PoolEntry poolEntry;
                Args.notNull((Object)timeout, (String)"Operation timeout");
                if (this.endpoint != null) {
                    return this.endpoint;
                }
                try {
                    poolEntry = (PoolEntry)leaseFuture.get(timeout.getDuration(), timeout.getTimeUnit());
                }
                catch (TimeoutException ex) {
                    leaseFuture.cancel(true);
                    throw ex;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} endpoint leased {}", (Object)id, (Object)ConnPoolSupport.formatStats(route, state, (ConnPoolControl<HttpRoute>)PoolingHttpClientConnectionManager.this.pool));
                }
                ConnectionConfig connectionConfig = PoolingHttpClientConnectionManager.this.resolveConnectionConfig(route);
                try {
                    ManagedHttpClientConnection conn;
                    TimeValue timeValue;
                    Deadline deadline;
                    TimeValue timeToLive;
                    if (poolEntry.hasConnection() && TimeValue.isNonNegative((TimeValue)(timeToLive = connectionConfig.getTimeToLive())) && (deadline = Deadline.calculate((long)poolEntry.getCreated(), (TimeValue)timeToLive)).isExpired()) {
                        poolEntry.discardConnection(CloseMode.GRACEFUL);
                    }
                    if (poolEntry.hasConnection() && TimeValue.isNonNegative((TimeValue)(timeValue = PoolingHttpClientConnectionManager.this.resolveValidateAfterInactivity(connectionConfig))) && (deadline = Deadline.calculate((long)poolEntry.getUpdated(), (TimeValue)timeValue)).isExpired()) {
                        boolean stale;
                        ManagedHttpClientConnection conn2 = (ManagedHttpClientConnection)poolEntry.getConnection();
                        try {
                            stale = conn2.isStale();
                        }
                        catch (IOException ignore) {
                            stale = true;
                        }
                        if (stale) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{} connection {} is stale", (Object)id, (Object)ConnPoolSupport.getId(conn2));
                            }
                            poolEntry.discardConnection(CloseMode.IMMEDIATE);
                        }
                    }
                    if ((conn = (ManagedHttpClientConnection)poolEntry.getConnection()) != null) {
                        conn.activate();
                    } else {
                        poolEntry.assignConnection((ModalCloseable)PoolingHttpClientConnectionManager.this.connFactory.createConnection(null));
                    }
                    this.endpoint = new InternalConnectionEndpoint((PoolEntry<HttpRoute, ManagedHttpClientConnection>)poolEntry);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} acquired {}", (Object)id, (Object)ConnPoolSupport.getId(this.endpoint));
                    }
                    return this.endpoint;
                }
                catch (Exception ex) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} endpoint lease failed", (Object)id);
                    }
                    PoolingHttpClientConnectionManager.this.pool.release(poolEntry, false);
                    throw new ExecutionException(ex.getMessage(), ex);
                }
            }

            public boolean cancel() {
                return leaseFuture.cancel(true);
            }
        };
    }

    @Override
    public void release(ConnectionEndpoint endpoint, Object state, TimeValue keepAlive) {
        ManagedHttpClientConnection conn;
        Args.notNull((Object)endpoint, (String)"Managed endpoint");
        PoolEntry<HttpRoute, ManagedHttpClientConnection> entry = this.cast(endpoint).detach();
        if (entry == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} releasing endpoint", (Object)ConnPoolSupport.getId(endpoint));
        }
        if ((conn = (ManagedHttpClientConnection)entry.getConnection()) != null && keepAlive == null) {
            conn.close(CloseMode.GRACEFUL);
        }
        boolean reusable = conn != null && conn.isOpen() && conn.isConsistent();
        try {
            if (reusable) {
                entry.updateState(state);
                entry.updateExpiry(keepAlive);
                conn.passivate();
                if (LOG.isDebugEnabled()) {
                    String s = TimeValue.isPositive((TimeValue)keepAlive) ? "for " + keepAlive : "indefinitely";
                    LOG.debug("{} connection {} can be kept alive {}", new Object[]{ConnPoolSupport.getId(endpoint), ConnPoolSupport.getId(conn), s});
                }
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("{} connection is not kept alive", (Object)ConnPoolSupport.getId(endpoint));
            }
        }
        catch (RuntimeException ex) {
            reusable = false;
            throw ex;
        }
        finally {
            this.pool.release(entry, reusable);
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} connection released {}", (Object)ConnPoolSupport.getId(endpoint), (Object)ConnPoolSupport.formatStats((HttpRoute)entry.getRoute(), entry.getState(), this.pool));
            }
        }
    }

    @Override
    public void connect(ConnectionEndpoint endpoint, TimeValue timeout, HttpContext context) throws IOException {
        Timeout socketTimeout;
        Timeout connectTimeout;
        HttpRoute route;
        Args.notNull((Object)endpoint, (String)"Managed endpoint");
        InternalConnectionEndpoint internalEndpoint = this.cast(endpoint);
        if (internalEndpoint.isConnected()) {
            return;
        }
        PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = internalEndpoint.getPoolEntry();
        if (!poolEntry.hasConnection()) {
            poolEntry.assignConnection((ModalCloseable)this.connFactory.createConnection(null));
        }
        HttpHost host = (route = (HttpRoute)poolEntry.getRoute()).getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
        SocketConfig socketConfig = this.resolveSocketConfig(route);
        ConnectionConfig connectionConfig = this.resolveConnectionConfig(route);
        TlsConfig tlsConfig = this.resolveTlsConfig(host);
        Timeout timeout2 = connectTimeout = timeout != null ? Timeout.of((long)timeout.getDuration(), (TimeUnit)timeout.getTimeUnit()) : connectionConfig.getConnectTimeout();
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} connecting endpoint to {} ({})", new Object[]{ConnPoolSupport.getId(endpoint), host, connectTimeout});
        }
        ManagedHttpClientConnection conn = (ManagedHttpClientConnection)poolEntry.getConnection();
        this.connectionOperator.connect(conn, host, route.getLocalSocketAddress(), connectTimeout, socketConfig, tlsConfig, context);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} connected {}", (Object)ConnPoolSupport.getId(endpoint), (Object)ConnPoolSupport.getId(conn));
        }
        if ((socketTimeout = connectionConfig.getSocketTimeout()) != null) {
            conn.setSocketTimeout(socketTimeout);
        }
    }

    @Override
    public void upgrade(ConnectionEndpoint endpoint, HttpContext context) throws IOException {
        Args.notNull((Object)endpoint, (String)"Managed endpoint");
        InternalConnectionEndpoint internalEndpoint = this.cast(endpoint);
        PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = internalEndpoint.getValidatedPoolEntry();
        HttpRoute route = (HttpRoute)poolEntry.getRoute();
        HttpHost host = route.getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
        TlsConfig tlsConfig = this.resolveTlsConfig(host);
        this.connectionOperator.upgrade((ManagedHttpClientConnection)poolEntry.getConnection(), route.getTargetHost(), tlsConfig, context);
    }

    public void closeIdle(TimeValue idleTime) {
        Args.notNull((Object)idleTime, (String)"Idle time");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing connections idle longer than {}", (Object)idleTime);
        }
        this.pool.closeIdle(idleTime);
    }

    public void closeExpired() {
        LOG.debug("Closing expired connections");
        this.pool.closeExpired();
    }

    public Set<HttpRoute> getRoutes() {
        return this.pool.getRoutes();
    }

    public int getMaxTotal() {
        return this.pool.getMaxTotal();
    }

    public void setMaxTotal(int max) {
        this.pool.setMaxTotal(max);
    }

    public int getDefaultMaxPerRoute() {
        return this.pool.getDefaultMaxPerRoute();
    }

    public void setDefaultMaxPerRoute(int max) {
        this.pool.setDefaultMaxPerRoute(max);
    }

    public int getMaxPerRoute(HttpRoute route) {
        return this.pool.getMaxPerRoute((Object)route);
    }

    public void setMaxPerRoute(HttpRoute route, int max) {
        this.pool.setMaxPerRoute((Object)route, max);
    }

    public PoolStats getTotalStats() {
        return this.pool.getTotalStats();
    }

    public PoolStats getStats(HttpRoute route) {
        return this.pool.getStats((Object)route);
    }

    public void setDefaultSocketConfig(SocketConfig config) {
        this.socketConfigResolver = route -> config;
    }

    public void setSocketConfigResolver(Resolver<HttpRoute, SocketConfig> socketConfigResolver) {
        this.socketConfigResolver = socketConfigResolver;
    }

    public void setDefaultConnectionConfig(ConnectionConfig config) {
        this.connectionConfigResolver = route -> config;
    }

    public void setConnectionConfigResolver(Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver) {
        this.connectionConfigResolver = connectionConfigResolver;
    }

    public void setDefaultTlsConfig(TlsConfig config) {
        this.tlsConfigResolver = host -> config;
    }

    public void setTlsConfigResolver(Resolver<HttpHost, TlsConfig> tlsConfigResolver) {
        this.tlsConfigResolver = tlsConfigResolver;
    }

    void closeIfExpired(PoolEntry<HttpRoute, ManagedHttpClientConnection> entry) {
        long now = System.currentTimeMillis();
        if (entry.getExpiryDeadline().isBefore(now)) {
            entry.discardConnection(CloseMode.GRACEFUL);
        } else {
            ConnectionConfig connectionConfig = this.resolveConnectionConfig((HttpRoute)entry.getRoute());
            TimeValue timeToLive = connectionConfig.getTimeToLive();
            if (timeToLive != null && Deadline.calculate((long)entry.getCreated(), (TimeValue)timeToLive).isBefore(now)) {
                entry.discardConnection(CloseMode.GRACEFUL);
            }
        }
    }

    @Deprecated
    public SocketConfig getDefaultSocketConfig() {
        return SocketConfig.DEFAULT;
    }

    @Deprecated
    public TimeValue getValidateAfterInactivity() {
        return ConnectionConfig.DEFAULT.getValidateAfterInactivity();
    }

    @Deprecated
    public void setValidateAfterInactivity(TimeValue validateAfterInactivity) {
        this.setDefaultConnectionConfig(ConnectionConfig.custom().setValidateAfterInactivity(validateAfterInactivity).build());
    }

    class InternalConnectionEndpoint
    extends ConnectionEndpoint
    implements Identifiable {
        private final AtomicReference<PoolEntry<HttpRoute, ManagedHttpClientConnection>> poolEntryRef;
        private final String id;

        InternalConnectionEndpoint(PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry) {
            this.poolEntryRef = new AtomicReference<PoolEntry<HttpRoute, ManagedHttpClientConnection>>(poolEntry);
            this.id = INCREMENTING_ID.getNextId();
        }

        public String getId() {
            return this.id;
        }

        PoolEntry<HttpRoute, ManagedHttpClientConnection> getPoolEntry() {
            PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = this.poolEntryRef.get();
            if (poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            return poolEntry;
        }

        PoolEntry<HttpRoute, ManagedHttpClientConnection> getValidatedPoolEntry() {
            PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = this.getPoolEntry();
            ManagedHttpClientConnection connection = (ManagedHttpClientConnection)poolEntry.getConnection();
            Asserts.check((connection != null && connection.isOpen() ? 1 : 0) != 0, (String)"Endpoint is not connected");
            return poolEntry;
        }

        PoolEntry<HttpRoute, ManagedHttpClientConnection> detach() {
            return this.poolEntryRef.getAndSet(null);
        }

        public void close(CloseMode closeMode) {
            PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = this.poolEntryRef.get();
            if (poolEntry != null) {
                poolEntry.discardConnection(closeMode);
            }
        }

        public void close() throws IOException {
            PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = this.poolEntryRef.get();
            if (poolEntry != null) {
                poolEntry.discardConnection(CloseMode.GRACEFUL);
            }
        }

        @Override
        public boolean isConnected() {
            PoolEntry<HttpRoute, ManagedHttpClientConnection> poolEntry = this.getPoolEntry();
            ManagedHttpClientConnection connection = (ManagedHttpClientConnection)poolEntry.getConnection();
            return connection != null && connection.isOpen();
        }

        @Override
        public void setSocketTimeout(Timeout timeout) {
            ((ManagedHttpClientConnection)this.getValidatedPoolEntry().getConnection()).setSocketTimeout(timeout);
        }

        @Override
        public ClassicHttpResponse execute(String exchangeId, ClassicHttpRequest request, HttpRequestExecutor requestExecutor, HttpContext context) throws IOException, HttpException {
            Args.notNull((Object)request, (String)"HTTP request");
            Args.notNull((Object)requestExecutor, (String)"Request executor");
            ManagedHttpClientConnection connection = (ManagedHttpClientConnection)this.getValidatedPoolEntry().getConnection();
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} executing exchange {} over {}", new Object[]{this.id, exchangeId, ConnPoolSupport.getId(connection)});
            }
            return requestExecutor.execute(request, (HttpClientConnection)connection, context);
        }
    }
}

