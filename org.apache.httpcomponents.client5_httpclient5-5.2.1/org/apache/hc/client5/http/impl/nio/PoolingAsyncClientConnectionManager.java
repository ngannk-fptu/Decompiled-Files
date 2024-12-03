/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.BasicFuture
 *  org.apache.hc.core5.concurrent.CallbackContribution
 *  org.apache.hc.core5.concurrent.ComplexFuture
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.command.RequestExecutionCommand
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http2.HttpVersionPolicy
 *  org.apache.hc.core5.http2.nio.AsyncPingHandler
 *  org.apache.hc.core5.http2.nio.command.PingCommand
 *  org.apache.hc.core5.http2.nio.support.BasicPingHandler
 *  org.apache.hc.core5.http2.ssl.ApplicationProtocol
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
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.ConnectionInitiator
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Deadline
 *  org.apache.hc.core5.util.Identifiable
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.nio;

import java.net.InetSocketAddress;
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
import org.apache.hc.client5.http.impl.nio.DefaultAsyncClientConnectionOperator;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.client5.http.nio.AsyncClientConnectionOperator;
import org.apache.hc.client5.http.nio.AsyncConnectionEndpoint;
import org.apache.hc.client5.http.nio.ManagedAsyncClientConnection;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.CallbackContribution;
import org.apache.hc.core5.concurrent.ComplexFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.RequestExecutionCommand;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.nio.AsyncPingHandler;
import org.apache.hc.core5.http2.nio.command.PingCommand;
import org.apache.hc.core5.http2.nio.support.BasicPingHandler;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
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
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Deadline;
import org.apache.hc.core5.util.Identifiable;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class PoolingAsyncClientConnectionManager
implements AsyncClientConnectionManager,
ConnPoolControl<HttpRoute> {
    private static final Logger LOG = LoggerFactory.getLogger(PoolingAsyncClientConnectionManager.class);
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 25;
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
    private final ManagedConnPool<HttpRoute, ManagedAsyncClientConnection> pool;
    private final AsyncClientConnectionOperator connectionOperator;
    private final AtomicBoolean closed;
    private volatile Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver;
    private volatile Resolver<HttpHost, TlsConfig> tlsConfigResolver;
    private static final PrefixedIncrementingId INCREMENTING_ID = new PrefixedIncrementingId("ep-");

    public PoolingAsyncClientConnectionManager() {
        this((Lookup<TlsStrategy>)RegistryBuilder.create().register(URIScheme.HTTPS.getId(), (Object)DefaultClientTlsStrategy.getDefault()).build());
    }

    public PoolingAsyncClientConnectionManager(Lookup<TlsStrategy> tlsStrategyLookup) {
        this(tlsStrategyLookup, PoolConcurrencyPolicy.STRICT, TimeValue.NEG_ONE_MILLISECOND);
    }

    public PoolingAsyncClientConnectionManager(Lookup<TlsStrategy> tlsStrategyLookup, PoolConcurrencyPolicy poolConcurrencyPolicy, TimeValue timeToLive) {
        this(tlsStrategyLookup, poolConcurrencyPolicy, PoolReusePolicy.LIFO, timeToLive);
    }

    public PoolingAsyncClientConnectionManager(Lookup<TlsStrategy> tlsStrategyLookup, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive) {
        this(tlsStrategyLookup, poolConcurrencyPolicy, poolReusePolicy, timeToLive, null, null);
    }

    public PoolingAsyncClientConnectionManager(Lookup<TlsStrategy> tlsStrategyLookup, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
        this(new DefaultAsyncClientConnectionOperator(tlsStrategyLookup, schemePortResolver, dnsResolver), poolConcurrencyPolicy, poolReusePolicy, timeToLive);
    }

    @Internal
    protected PoolingAsyncClientConnectionManager(AsyncClientConnectionOperator connectionOperator, PoolConcurrencyPolicy poolConcurrencyPolicy, PoolReusePolicy poolReusePolicy, TimeValue timeToLive) {
        this.connectionOperator = (AsyncClientConnectionOperator)Args.notNull((Object)connectionOperator, (String)"Connection operator");
        switch (poolConcurrencyPolicy != null ? poolConcurrencyPolicy : PoolConcurrencyPolicy.STRICT) {
            case STRICT: {
                this.pool = new StrictConnPool<HttpRoute, ManagedAsyncClientConnection>(5, 25, timeToLive, poolReusePolicy, null){

                    public void closeExpired() {
                        this.enumAvailable(e -> PoolingAsyncClientConnectionManager.this.closeIfExpired((PoolEntry<HttpRoute, ManagedAsyncClientConnection>)e));
                    }
                };
                break;
            }
            case LAX: {
                this.pool = new LaxConnPool<HttpRoute, ManagedAsyncClientConnection>(5, timeToLive, poolReusePolicy, null){

                    public void closeExpired() {
                        this.enumAvailable(e -> PoolingAsyncClientConnectionManager.this.closeIfExpired((PoolEntry<HttpRoute, ManagedAsyncClientConnection>)e));
                    }
                };
                break;
            }
            default: {
                throw new IllegalArgumentException("Unexpected PoolConcurrencyPolicy value: " + poolConcurrencyPolicy);
            }
        }
        this.closed = new AtomicBoolean(false);
    }

    @Internal
    protected PoolingAsyncClientConnectionManager(ManagedConnPool<HttpRoute, ManagedAsyncClientConnection> pool, AsyncClientConnectionOperator connectionOperator) {
        this.connectionOperator = (AsyncClientConnectionOperator)Args.notNull((Object)connectionOperator, (String)"Connection operator");
        this.pool = (ManagedConnPool)Args.notNull(pool, (String)"Connection pool");
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

    private InternalConnectionEndpoint cast(AsyncConnectionEndpoint endpoint) {
        if (endpoint instanceof InternalConnectionEndpoint) {
            return (InternalConnectionEndpoint)endpoint;
        }
        throw new IllegalStateException("Unexpected endpoint class: " + endpoint.getClass());
    }

    private ConnectionConfig resolveConnectionConfig(HttpRoute route) {
        Resolver<HttpRoute, ConnectionConfig> resolver = this.connectionConfigResolver;
        ConnectionConfig connectionConfig = resolver != null ? (ConnectionConfig)resolver.resolve((Object)route) : null;
        return connectionConfig != null ? connectionConfig : ConnectionConfig.DEFAULT;
    }

    private TlsConfig resolveTlsConfig(HttpHost host, Object attachment) {
        if (attachment instanceof TlsConfig) {
            return (TlsConfig)attachment;
        }
        Resolver<HttpHost, TlsConfig> resolver = this.tlsConfigResolver;
        TlsConfig tlsConfig = resolver != null ? (TlsConfig)resolver.resolve((Object)host) : null;
        return tlsConfig != null ? tlsConfig : TlsConfig.DEFAULT;
    }

    @Override
    public Future<AsyncConnectionEndpoint> lease(final String id, final HttpRoute route, final Object state, final Timeout requestTimeout, final FutureCallback<AsyncConnectionEndpoint> callback) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} endpoint lease request ({}) {}", new Object[]{id, requestTimeout, ConnPoolSupport.formatStats(route, state, this.pool)});
        }
        return new Future<AsyncConnectionEndpoint>(){
            final ConnectionConfig connectionConfig;
            final BasicFuture<AsyncConnectionEndpoint> resultFuture;
            final Future<PoolEntry<HttpRoute, ManagedAsyncClientConnection>> leaseFuture;
            {
                this.connectionConfig = PoolingAsyncClientConnectionManager.this.resolveConnectionConfig(route);
                this.resultFuture = new BasicFuture(callback);
                this.leaseFuture = PoolingAsyncClientConnectionManager.this.pool.lease((Object)route, state, requestTimeout, (FutureCallback)new FutureCallback<PoolEntry<HttpRoute, ManagedAsyncClientConnection>>(){

                    public void completed(PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry) {
                        Deadline deadline;
                        TimeValue timeToLive;
                        if (poolEntry.hasConnection() && TimeValue.isNonNegative((TimeValue)(timeToLive = connectionConfig.getTimeToLive())) && (deadline = Deadline.calculate((long)poolEntry.getCreated(), (TimeValue)timeToLive)).isExpired()) {
                            poolEntry.discardConnection(CloseMode.GRACEFUL);
                        }
                        if (poolEntry.hasConnection()) {
                            Deadline deadline2;
                            ManagedAsyncClientConnection connection = (ManagedAsyncClientConnection)poolEntry.getConnection();
                            TimeValue timeValue = connectionConfig.getValidateAfterInactivity();
                            if (connection.isOpen() && TimeValue.isNonNegative((TimeValue)timeValue) && (deadline2 = Deadline.calculate((long)poolEntry.getUpdated(), (TimeValue)timeValue)).isExpired()) {
                                ProtocolVersion protocolVersion = connection.getProtocolVersion();
                                if (protocolVersion != null && protocolVersion.greaterEquals((ProtocolVersion)HttpVersion.HTTP_2_0)) {
                                    connection.submitCommand((Command)new PingCommand((AsyncPingHandler)new BasicPingHandler(result -> {
                                        if (result == null || !result.booleanValue()) {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("{} connection {} is stale", (Object)id, (Object)ConnPoolSupport.getId(connection));
                                            }
                                            poolEntry.discardConnection(CloseMode.GRACEFUL);
                                        }
                                        this.leaseCompleted(poolEntry);
                                    })), Command.Priority.IMMEDIATE);
                                    return;
                                }
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("{} connection {} is closed", (Object)id, (Object)ConnPoolSupport.getId(connection));
                                }
                                poolEntry.discardConnection(CloseMode.IMMEDIATE);
                            }
                        }
                        this.leaseCompleted(poolEntry);
                    }

                    void leaseCompleted(PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry) {
                        ManagedAsyncClientConnection connection = (ManagedAsyncClientConnection)poolEntry.getConnection();
                        if (connection != null) {
                            connection.activate();
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} endpoint leased {}", (Object)id, (Object)ConnPoolSupport.formatStats(route, state, (ConnPoolControl<HttpRoute>)PoolingAsyncClientConnectionManager.this.pool));
                        }
                        InternalConnectionEndpoint endpoint = new InternalConnectionEndpoint(poolEntry);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} acquired {}", (Object)id, (Object)ConnPoolSupport.getId(endpoint));
                        }
                        resultFuture.completed((Object)endpoint);
                    }

                    public void failed(Exception ex) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} endpoint lease failed", (Object)id);
                        }
                        resultFuture.failed(ex);
                    }

                    public void cancelled() {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} endpoint lease cancelled", (Object)id);
                        }
                        resultFuture.cancel();
                    }
                });
            }

            @Override
            public AsyncConnectionEndpoint get() throws InterruptedException, ExecutionException {
                return (AsyncConnectionEndpoint)this.resultFuture.get();
            }

            @Override
            public AsyncConnectionEndpoint get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return (AsyncConnectionEndpoint)this.resultFuture.get(timeout, unit);
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return this.leaseFuture.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isDone() {
                return this.resultFuture.isDone();
            }

            @Override
            public boolean isCancelled() {
                return this.resultFuture.isCancelled();
            }
        };
    }

    @Override
    public void release(AsyncConnectionEndpoint endpoint, Object state, TimeValue keepAlive) {
        ManagedAsyncClientConnection connection;
        Args.notNull((Object)endpoint, (String)"Managed endpoint");
        Args.notNull((Object)keepAlive, (String)"Keep-alive time");
        PoolEntry<HttpRoute, ManagedAsyncClientConnection> entry = this.cast(endpoint).detach();
        if (entry == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} releasing endpoint", (Object)ConnPoolSupport.getId(endpoint));
        }
        boolean reusable = (connection = (ManagedAsyncClientConnection)entry.getConnection()) != null && connection.isOpen();
        try {
            if (reusable) {
                entry.updateState(state);
                entry.updateExpiry(keepAlive);
                connection.passivate();
                if (LOG.isDebugEnabled()) {
                    String s = TimeValue.isPositive((TimeValue)keepAlive) ? "for " + keepAlive : "indefinitely";
                    LOG.debug("{} connection {} can be kept alive {}", new Object[]{ConnPoolSupport.getId(endpoint), ConnPoolSupport.getId(connection), s});
                }
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
    public Future<AsyncConnectionEndpoint> connect(final AsyncConnectionEndpoint endpoint, ConnectionInitiator connectionInitiator, Timeout timeout, Object attachment, HttpContext context, FutureCallback<AsyncConnectionEndpoint> callback) {
        Timeout connectTimeout;
        Args.notNull((Object)endpoint, (String)"Endpoint");
        Args.notNull((Object)connectionInitiator, (String)"Connection initiator");
        final InternalConnectionEndpoint internalEndpoint = this.cast(endpoint);
        final ComplexFuture resultFuture = new ComplexFuture(callback);
        if (internalEndpoint.isConnected()) {
            resultFuture.completed((Object)endpoint);
            return resultFuture;
        }
        final PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry = internalEndpoint.getPoolEntry();
        HttpRoute route = (HttpRoute)poolEntry.getRoute();
        HttpHost host = route.getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
        InetSocketAddress localAddress = route.getLocalSocketAddress();
        final ConnectionConfig connectionConfig = this.resolveConnectionConfig(route);
        TlsConfig tlsConfig = this.resolveTlsConfig(host, attachment);
        Timeout timeout2 = connectTimeout = timeout != null ? timeout : connectionConfig.getConnectTimeout();
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} connecting endpoint to {} ({})", new Object[]{ConnPoolSupport.getId(endpoint), host, connectTimeout});
        }
        Future<ManagedAsyncClientConnection> connectFuture = this.connectionOperator.connect(connectionInitiator, host, localAddress, connectTimeout, route.isTunnelled() ? TlsConfig.copy(tlsConfig).setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1).build() : tlsConfig, context, new FutureCallback<ManagedAsyncClientConnection>(){

            public void completed(ManagedAsyncClientConnection connection) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} connected {}", (Object)ConnPoolSupport.getId(endpoint), (Object)ConnPoolSupport.getId(connection));
                    }
                    ProtocolVersion protocolVersion = connection.getProtocolVersion();
                    Timeout socketTimeout = connectionConfig.getSocketTimeout();
                    if (socketTimeout != null) {
                        connection.setSocketTimeout(socketTimeout);
                    }
                    poolEntry.assignConnection((ModalCloseable)connection);
                    resultFuture.completed((Object)internalEndpoint);
                }
                catch (RuntimeException ex) {
                    resultFuture.failed((Exception)ex);
                }
            }

            public void failed(Exception ex) {
                resultFuture.failed(ex);
            }

            public void cancelled() {
                resultFuture.cancel();
            }
        });
        resultFuture.setDependency(connectFuture);
        return resultFuture;
    }

    @Override
    public void upgrade(final AsyncConnectionEndpoint endpoint, Object attachment, HttpContext context, final FutureCallback<AsyncConnectionEndpoint> callback) {
        Args.notNull((Object)endpoint, (String)"Managed endpoint");
        final InternalConnectionEndpoint internalEndpoint = this.cast(endpoint);
        PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry = internalEndpoint.getValidatedPoolEntry();
        HttpRoute route = (HttpRoute)poolEntry.getRoute();
        HttpHost host = route.getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
        TlsConfig tlsConfig = this.resolveTlsConfig(host, attachment);
        this.connectionOperator.upgrade((ManagedAsyncClientConnection)poolEntry.getConnection(), route.getTargetHost(), attachment != null ? attachment : tlsConfig, context, (FutureCallback<ManagedAsyncClientConnection>)new CallbackContribution<ManagedAsyncClientConnection>(callback){

            public void completed(ManagedAsyncClientConnection connection) {
                TlsDetails tlsDetails;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} upgraded {}", (Object)ConnPoolSupport.getId(internalEndpoint), (Object)ConnPoolSupport.getId(connection));
                }
                if ((tlsDetails = connection.getTlsDetails()) != null && ApplicationProtocol.HTTP_2.id.equals(tlsDetails.getApplicationProtocol())) {
                    connection.switchProtocol(ApplicationProtocol.HTTP_2.id, (FutureCallback<ProtocolIOSession>)new CallbackContribution<ProtocolIOSession>(callback){

                        public void completed(ProtocolIOSession protocolIOSession) {
                            if (callback != null) {
                                callback.completed((Object)endpoint);
                            }
                        }
                    });
                } else if (callback != null) {
                    callback.completed((Object)endpoint);
                }
            }
        });
    }

    @Override
    public void upgrade(AsyncConnectionEndpoint endpoint, Object attachment, HttpContext context) {
        this.upgrade(endpoint, attachment, context, null);
    }

    public Set<HttpRoute> getRoutes() {
        return this.pool.getRoutes();
    }

    public void setMaxTotal(int max) {
        this.pool.setMaxTotal(max);
    }

    public int getMaxTotal() {
        return this.pool.getMaxTotal();
    }

    public void setDefaultMaxPerRoute(int max) {
        this.pool.setDefaultMaxPerRoute(max);
    }

    public int getDefaultMaxPerRoute() {
        return this.pool.getDefaultMaxPerRoute();
    }

    public void setMaxPerRoute(HttpRoute route, int max) {
        this.pool.setMaxPerRoute((Object)route, max);
    }

    public int getMaxPerRoute(HttpRoute route) {
        return this.pool.getMaxPerRoute((Object)route);
    }

    public void closeIdle(TimeValue idletime) {
        this.pool.closeIdle(idletime);
    }

    public void closeExpired() {
        this.pool.closeExpired();
    }

    public PoolStats getTotalStats() {
        return this.pool.getTotalStats();
    }

    public PoolStats getStats(HttpRoute route) {
        return this.pool.getStats((Object)route);
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

    void closeIfExpired(PoolEntry<HttpRoute, ManagedAsyncClientConnection> entry) {
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
    public TimeValue getValidateAfterInactivity() {
        return ConnectionConfig.DEFAULT.getValidateAfterInactivity();
    }

    @Deprecated
    public void setValidateAfterInactivity(TimeValue validateAfterInactivity) {
        this.setDefaultConnectionConfig(ConnectionConfig.custom().setValidateAfterInactivity(validateAfterInactivity).build());
    }

    boolean isClosed() {
        return this.closed.get();
    }

    class InternalConnectionEndpoint
    extends AsyncConnectionEndpoint
    implements Identifiable {
        private final AtomicReference<PoolEntry<HttpRoute, ManagedAsyncClientConnection>> poolEntryRef;
        private final String id;

        InternalConnectionEndpoint(PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry) {
            this.poolEntryRef = new AtomicReference<PoolEntry<HttpRoute, ManagedAsyncClientConnection>>(poolEntry);
            this.id = INCREMENTING_ID.getNextId();
        }

        public String getId() {
            return this.id;
        }

        PoolEntry<HttpRoute, ManagedAsyncClientConnection> getPoolEntry() {
            PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry = this.poolEntryRef.get();
            if (poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            return poolEntry;
        }

        PoolEntry<HttpRoute, ManagedAsyncClientConnection> getValidatedPoolEntry() {
            PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry = this.getPoolEntry();
            if (poolEntry.getConnection() == null) {
                throw new ConnectionShutdownException();
            }
            return poolEntry;
        }

        PoolEntry<HttpRoute, ManagedAsyncClientConnection> detach() {
            return this.poolEntryRef.getAndSet(null);
        }

        public void close(CloseMode closeMode) {
            PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry = this.poolEntryRef.get();
            if (poolEntry != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} close {}", (Object)this.id, (Object)closeMode);
                }
                poolEntry.discardConnection(closeMode);
            }
        }

        @Override
        public boolean isConnected() {
            PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry = this.poolEntryRef.get();
            if (poolEntry == null) {
                return false;
            }
            ManagedAsyncClientConnection connection = (ManagedAsyncClientConnection)poolEntry.getConnection();
            if (connection == null) {
                return false;
            }
            if (!connection.isOpen()) {
                poolEntry.discardConnection(CloseMode.IMMEDIATE);
                return false;
            }
            return true;
        }

        @Override
        public void setSocketTimeout(Timeout timeout) {
            ((ManagedAsyncClientConnection)this.getValidatedPoolEntry().getConnection()).setSocketTimeout(timeout);
        }

        @Override
        public void execute(String exchangeId, AsyncClientExchangeHandler exchangeHandler, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context) {
            ManagedAsyncClientConnection connection = (ManagedAsyncClientConnection)this.getValidatedPoolEntry().getConnection();
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} executing exchange {} over {}", new Object[]{this.id, exchangeId, ConnPoolSupport.getId(connection)});
            }
            context.setProtocolVersion(connection.getProtocolVersion());
            connection.submitCommand((Command)new RequestExecutionCommand(exchangeHandler, pushHandlerFactory, context), Command.Priority.NORMAL);
        }
    }
}

