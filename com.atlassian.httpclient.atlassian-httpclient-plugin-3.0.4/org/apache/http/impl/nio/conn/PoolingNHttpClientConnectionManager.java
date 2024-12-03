/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.nio.conn.CPool;
import org.apache.http.impl.nio.conn.CPoolEntry;
import org.apache.http.impl.nio.conn.CPoolProxy;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.pool.SocketAddressResolver;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading=ThreadingBehavior.SAFE)
public class PoolingNHttpClientConnectionManager
implements NHttpClientConnectionManager,
ConnPoolControl<HttpRoute> {
    private final Log log = LogFactory.getLog(this.getClass());
    static final String IOSESSION_FACTORY_REGISTRY = "http.ioSession-factory-registry";
    private final ConnectingIOReactor ioReactor;
    private final ConfigData configData;
    private final CPool pool;
    private final Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry;

    private static Registry<SchemeIOSessionStrategy> getDefaultRegistry() {
        return RegistryBuilder.create().register("http", NoopIOSessionStrategy.INSTANCE).register("https", (NoopIOSessionStrategy)((Object)SSLIOSessionStrategy.getDefaultStrategy())).build();
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor) {
        this(ioReactor, PoolingNHttpClientConnectionManager.getDefaultRegistry());
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry) {
        this(ioReactor, null, ioSessionFactoryRegistry, (DnsResolver)null);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, DnsResolver dnsResolver) {
        this(ioReactor, connFactory, PoolingNHttpClientConnectionManager.getDefaultRegistry(), dnsResolver);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, SocketAddressResolver<HttpRoute> socketAddressResolver) {
        this(ioReactor, connFactory, PoolingNHttpClientConnectionManager.getDefaultRegistry(), socketAddressResolver);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory) {
        this(ioReactor, connFactory, PoolingNHttpClientConnectionManager.getDefaultRegistry(), (DnsResolver)null);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry) {
        this(ioReactor, connFactory, ioSessionFactoryRegistry, (DnsResolver)null);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry, DnsResolver dnsResolver) {
        this(ioReactor, connFactory, ioSessionFactoryRegistry, null, dnsResolver, -1L, TimeUnit.MILLISECONDS);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry, SocketAddressResolver<HttpRoute> socketAddressResolver) {
        this(ioReactor, connFactory, ioSessionFactoryRegistry, socketAddressResolver, -1L, TimeUnit.MILLISECONDS);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry, SchemePortResolver schemePortResolver, DnsResolver dnsResolver, long timeToLive, TimeUnit timeUnit) {
        this(ioReactor, connFactory, ioSessionFactoryRegistry, new InternalAddressResolver(schemePortResolver, dnsResolver), timeToLive, timeUnit);
    }

    public PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry, SocketAddressResolver<HttpRoute> socketAddressResolver, long timeToLive, TimeUnit timeUnit) {
        Args.notNull(ioReactor, "I/O reactor");
        Args.notNull(ioSessionFactoryRegistry, "I/O session factory registry");
        Args.notNull(socketAddressResolver, "Socket address resolver");
        this.ioReactor = ioReactor;
        this.configData = new ConfigData();
        this.pool = new CPool(ioReactor, new InternalConnectionFactory(this.configData, connFactory), socketAddressResolver, 2, 20, timeToLive, timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
        this.ioSessionFactoryRegistry = ioSessionFactoryRegistry;
    }

    PoolingNHttpClientConnectionManager(ConnectingIOReactor ioReactor, CPool pool, Registry<SchemeIOSessionStrategy> ioSessionFactoryRegistry) {
        this.ioReactor = ioReactor;
        this.configData = new ConfigData();
        this.pool = pool;
        this.ioSessionFactoryRegistry = ioSessionFactoryRegistry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            this.shutdown();
        }
        finally {
            super.finalize();
        }
    }

    @Override
    public void execute(IOEventDispatch eventDispatch) throws IOException {
        this.ioReactor.execute(eventDispatch);
    }

    public void shutdown(long waitMs) throws IOException {
        this.log.debug("Connection manager is shutting down");
        this.pool.shutdown(waitMs);
        this.log.debug("Connection manager shut down");
    }

    @Override
    public void shutdown() throws IOException {
        this.log.debug("Connection manager is shutting down");
        this.pool.shutdown(2000L);
        this.log.debug("Connection manager shut down");
    }

    private String format(HttpRoute route, Object state) {
        StringBuilder buf = new StringBuilder();
        buf.append("[route: ").append(route).append("]");
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }

    private String formatStats(HttpRoute route) {
        StringBuilder buf = new StringBuilder();
        PoolStats totals = this.pool.getTotalStats();
        PoolStats stats = this.pool.getStats(route);
        buf.append("[total kept alive: ").append(totals.getAvailable()).append("; ");
        buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
        buf.append(" of ").append(stats.getMax()).append("; ");
        buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
        buf.append(" of ").append(totals.getMax()).append("]");
        return buf.toString();
    }

    private String format(CPoolEntry entry) {
        StringBuilder buf = new StringBuilder();
        buf.append("[id: ").append(entry.getId()).append("]");
        buf.append("[route: ").append(entry.getRoute()).append("]");
        Object state = entry.getState();
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }

    @Override
    public Future<NHttpClientConnection> requestConnection(HttpRoute route, Object state, long connectTimeout, long leaseTimeout, TimeUnit timeUnit, FutureCallback<NHttpClientConnection> callback) {
        Args.notNull(route, "HTTP route");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Connection request: " + this.format(route, state) + this.formatStats(route));
        }
        final BasicFuture<NHttpClientConnection> resultFuture = new BasicFuture<NHttpClientConnection>(callback);
        HttpHost host = route.getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
        SchemeIOSessionStrategy sf = this.ioSessionFactoryRegistry.lookup(host.getSchemeName());
        if (sf == null) {
            resultFuture.failed(new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported"));
            return resultFuture;
        }
        final Future<CPoolEntry> leaseFuture = this.pool.lease(route, state, connectTimeout, leaseTimeout, timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS, new FutureCallback<CPoolEntry>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void completed(CPoolEntry entry) {
                NHttpClientConnection managedConn;
                Asserts.check(entry.getConnection() != null, "Pool entry with no connection");
                if (PoolingNHttpClientConnectionManager.this.log.isDebugEnabled()) {
                    PoolingNHttpClientConnectionManager.this.log.debug("Connection leased: " + PoolingNHttpClientConnectionManager.this.format(entry) + PoolingNHttpClientConnectionManager.this.formatStats((HttpRoute)entry.getRoute()));
                }
                NHttpClientConnection nHttpClientConnection = managedConn = CPoolProxy.newProxy(entry);
                synchronized (nHttpClientConnection) {
                    if (!resultFuture.completed(managedConn)) {
                        PoolingNHttpClientConnectionManager.this.pool.release(entry, true);
                    }
                }
            }

            @Override
            public void failed(Exception ex) {
                if (PoolingNHttpClientConnectionManager.this.log.isDebugEnabled()) {
                    PoolingNHttpClientConnectionManager.this.log.debug("Connection request failed", ex);
                }
                resultFuture.failed(ex);
            }

            @Override
            public void cancelled() {
                PoolingNHttpClientConnectionManager.this.log.debug("Connection request cancelled");
                resultFuture.cancel(true);
            }
        });
        return new Future<NHttpClientConnection>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    leaseFuture.cancel(mayInterruptIfRunning);
                }
                finally {
                    return resultFuture.cancel(mayInterruptIfRunning);
                }
            }

            @Override
            public boolean isCancelled() {
                return resultFuture.isCancelled();
            }

            @Override
            public boolean isDone() {
                return resultFuture.isDone();
            }

            @Override
            public NHttpClientConnection get() throws InterruptedException, ExecutionException {
                return (NHttpClientConnection)resultFuture.get();
            }

            @Override
            public NHttpClientConnection get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return (NHttpClientConnection)resultFuture.get(timeout, unit);
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void releaseConnection(NHttpClientConnection managedConn, Object state, long keepalive, TimeUnit timeUnit) {
        Args.notNull(managedConn, "Managed connection");
        NHttpClientConnection nHttpClientConnection = managedConn;
        synchronized (nHttpClientConnection) {
            NHttpClientConnection conn;
            CPoolEntry entry;
            block9: {
                entry = CPoolProxy.detach(managedConn);
                if (entry == null) {
                    return;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Releasing connection: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute()));
                }
                conn = (NHttpClientConnection)entry.getConnection();
                try {
                    if (!conn.isOpen()) break block9;
                    entry.setState(state);
                    entry.updateExpiry(keepalive, timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
                    if (!this.log.isDebugEnabled()) break block9;
                    String s = keepalive > 0L ? "for " + (double)keepalive / 1000.0 + " seconds" : "indefinitely";
                    this.log.debug("Connection " + this.format(entry) + " can be kept alive " + s);
                }
                catch (Throwable throwable) {
                    this.pool.release(entry, conn.isOpen() && entry.isRouteComplete());
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Connection released: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute()));
                    }
                    throw throwable;
                }
            }
            this.pool.release(entry, conn.isOpen() && entry.isRouteComplete());
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection released: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute()));
            }
        }
    }

    private Lookup<SchemeIOSessionStrategy> getIOSessionFactoryRegistry(HttpContext context) {
        Registry<SchemeIOSessionStrategy> reg = (Registry<SchemeIOSessionStrategy>)context.getAttribute(IOSESSION_FACTORY_REGISTRY);
        if (reg == null) {
            reg = this.ioSessionFactoryRegistry;
        }
        return reg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void startRoute(NHttpClientConnection managedConn, HttpRoute route, HttpContext context) throws IOException {
        Args.notNull(managedConn, "Managed connection");
        Args.notNull(route, "HTTP route");
        HttpHost host = route.getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
        Lookup<SchemeIOSessionStrategy> reg = this.getIOSessionFactoryRegistry(context);
        SchemeIOSessionStrategy sf = reg.lookup(host.getSchemeName());
        if (sf == null) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
        }
        if (sf.isLayeringRequired()) {
            NHttpClientConnection nHttpClientConnection = managedConn;
            synchronized (nHttpClientConnection) {
                CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
                ManagedNHttpClientConnection conn = (ManagedNHttpClientConnection)entry.getConnection();
                IOSession ioSession = conn.getIOSession();
                IOSession currentSession = sf.upgrade(host, ioSession);
                conn.bind(currentSession);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void upgrade(NHttpClientConnection managedConn, HttpRoute route, HttpContext context) throws IOException {
        Args.notNull(managedConn, "Managed connection");
        Args.notNull(route, "HTTP route");
        HttpHost host = route.getTargetHost();
        Lookup<SchemeIOSessionStrategy> reg = this.getIOSessionFactoryRegistry(context);
        SchemeIOSessionStrategy sf = reg.lookup(host.getSchemeName());
        if (sf == null) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
        }
        if (!sf.isLayeringRequired()) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol does not support connection upgrade");
        }
        NHttpClientConnection nHttpClientConnection = managedConn;
        synchronized (nHttpClientConnection) {
            CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
            ManagedNHttpClientConnection conn = (ManagedNHttpClientConnection)entry.getConnection();
            IOSession currentSession = sf.upgrade(host, conn.getIOSession());
            conn.bind(currentSession);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void routeComplete(NHttpClientConnection managedConn, HttpRoute route, HttpContext context) {
        Args.notNull(managedConn, "Managed connection");
        Args.notNull(route, "HTTP route");
        NHttpClientConnection nHttpClientConnection = managedConn;
        synchronized (nHttpClientConnection) {
            CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
            entry.markRouteComplete();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isRouteComplete(NHttpClientConnection managedConn) {
        Args.notNull(managedConn, "Managed connection");
        NHttpClientConnection nHttpClientConnection = managedConn;
        synchronized (nHttpClientConnection) {
            CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
            return entry.isRouteComplete();
        }
    }

    @Override
    public void closeIdleConnections(long idleTimeout, TimeUnit timeUnit) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Closing connections idle longer than " + idleTimeout + " " + (Object)((Object)timeUnit));
        }
        this.pool.closeIdle(idleTimeout, timeUnit);
    }

    @Override
    public void closeExpiredConnections() {
        this.log.debug("Closing expired connections");
        this.pool.closeExpired();
    }

    public void validatePendingRequests() {
        this.log.debug("Validating pending requests");
        this.pool.validatePendingRequests();
    }

    @Override
    public int getMaxTotal() {
        return this.pool.getMaxTotal();
    }

    @Override
    public void setMaxTotal(int max) {
        this.pool.setMaxTotal(max);
    }

    @Override
    public int getDefaultMaxPerRoute() {
        return this.pool.getDefaultMaxPerRoute();
    }

    @Override
    public void setDefaultMaxPerRoute(int max) {
        this.pool.setDefaultMaxPerRoute(max);
    }

    @Override
    public int getMaxPerRoute(HttpRoute route) {
        return this.pool.getMaxPerRoute(route);
    }

    @Override
    public void setMaxPerRoute(HttpRoute route, int max) {
        this.pool.setMaxPerRoute(route, max);
    }

    @Override
    public PoolStats getTotalStats() {
        return this.pool.getTotalStats();
    }

    @Override
    public PoolStats getStats(HttpRoute route) {
        return this.pool.getStats(route);
    }

    public Set<HttpRoute> getRoutes() {
        return this.pool.getRoutes();
    }

    public ConnectionConfig getDefaultConnectionConfig() {
        return this.configData.getDefaultConnectionConfig();
    }

    public void setDefaultConnectionConfig(ConnectionConfig defaultConnectionConfig) {
        this.configData.setDefaultConnectionConfig(defaultConnectionConfig);
    }

    public ConnectionConfig getConnectionConfig(HttpHost host) {
        return this.configData.getConnectionConfig(host);
    }

    public void setConnectionConfig(HttpHost host, ConnectionConfig connectionConfig) {
        this.configData.setConnectionConfig(host, connectionConfig);
    }

    static class InternalAddressResolver
    implements SocketAddressResolver<HttpRoute> {
        private final SchemePortResolver schemePortResolver;
        private final DnsResolver dnsResolver;

        public InternalAddressResolver(SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
            this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
            this.dnsResolver = dnsResolver != null ? dnsResolver : SystemDefaultDnsResolver.INSTANCE;
        }

        @Override
        public SocketAddress resolveLocalAddress(HttpRoute route) throws IOException {
            return route.getLocalAddress() != null ? new InetSocketAddress(route.getLocalAddress(), 0) : null;
        }

        @Override
        public SocketAddress resolveRemoteAddress(HttpRoute route) throws IOException {
            HttpHost host = route.getProxyHost() != null ? route.getProxyHost() : route.getTargetHost();
            int port = this.schemePortResolver.resolve(host);
            InetAddress[] addresses = this.dnsResolver.resolve(host.getHostName());
            return new InetSocketAddress(addresses[0], port);
        }
    }

    static class InternalConnectionFactory
    implements NIOConnFactory<HttpRoute, ManagedNHttpClientConnection> {
        private final ConfigData configData;
        private final NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory;

        InternalConnectionFactory(ConfigData configData, NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory) {
            this.configData = configData != null ? configData : new ConfigData();
            this.connFactory = connFactory != null ? connFactory : ManagedNHttpClientConnectionFactory.INSTANCE;
        }

        @Override
        public ManagedNHttpClientConnection create(HttpRoute route, IOSession ioSession) throws IOException {
            ConnectionConfig config = null;
            if (route.getProxyHost() != null) {
                config = this.configData.getConnectionConfig(route.getProxyHost());
            }
            if (config == null) {
                config = this.configData.getConnectionConfig(route.getTargetHost());
            }
            if (config == null) {
                config = this.configData.getDefaultConnectionConfig();
            }
            if (config == null) {
                config = ConnectionConfig.DEFAULT;
            }
            ManagedNHttpClientConnection conn = this.connFactory.create(ioSession, config);
            ioSession.setAttribute("http.connection", conn);
            return conn;
        }
    }

    static class ConfigData {
        private final Map<HttpHost, ConnectionConfig> connectionConfigMap = new ConcurrentHashMap<HttpHost, ConnectionConfig>();
        private volatile ConnectionConfig defaultConnectionConfig;

        ConfigData() {
        }

        public ConnectionConfig getDefaultConnectionConfig() {
            return this.defaultConnectionConfig;
        }

        public void setDefaultConnectionConfig(ConnectionConfig defaultConnectionConfig) {
            this.defaultConnectionConfig = defaultConnectionConfig;
        }

        public ConnectionConfig getConnectionConfig(HttpHost host) {
            return this.connectionConfigMap.get(host);
        }

        public void setConnectionConfig(HttpHost host, ConnectionConfig connectionConfig) {
            this.connectionConfigMap.put(host, connectionConfig);
        }
    }
}

