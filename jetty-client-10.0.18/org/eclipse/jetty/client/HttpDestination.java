/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.ClientConnectionFactory$Decorator
 *  org.eclipse.jetty.io.CyclicTimeouts
 *  org.eclipse.jetty.util.BlockingArrayQueue
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.HostPort
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.Promise
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.component.DumpableCollection
 *  org.eclipse.jetty.util.component.LifeCycle
 *  org.eclipse.jetty.util.ssl.SslContextFactory$Client
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.Sweeper
 *  org.eclipse.jetty.util.thread.Sweeper$Sweepable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.ConnectionPool;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.IConnection;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.RequestNotifier;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.SendFailure;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.Destination;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.CyclicTimeouts;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Sweeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public abstract class HttpDestination
extends ContainerLifeCycle
implements Destination,
Closeable,
Callback,
Dumpable,
Sweeper.Sweepable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpDestination.class);
    private final HttpClient client;
    private final Origin origin;
    private final Queue<HttpExchange> exchanges;
    private final RequestNotifier requestNotifier;
    private final ResponseNotifier responseNotifier;
    private final ProxyConfiguration.Proxy proxy;
    private final ClientConnectionFactory connectionFactory;
    private final HttpField hostField;
    private final RequestTimeouts requestTimeouts;
    private final AutoLock staleLock = new AutoLock();
    private ConnectionPool connectionPool;
    private boolean stale;
    private long activeNanoTime;

    public HttpDestination(HttpClient client, Origin origin, boolean intrinsicallySecure) {
        Object tag;
        this.client = client;
        this.origin = origin;
        this.exchanges = this.newExchangeQueue(client);
        this.requestNotifier = new RequestNotifier(client);
        this.responseNotifier = new ResponseNotifier();
        this.requestTimeouts = new RequestTimeouts(client.getScheduler());
        Object host = HostPort.normalizeHost((String)this.getHost());
        if (!client.isDefaultPort(this.getScheme(), this.getPort())) {
            host = (String)host + ":" + this.getPort();
        }
        this.hostField = new HttpField(HttpHeader.HOST, (String)host);
        ProxyConfiguration proxyConfig = client.getProxyConfiguration();
        this.proxy = proxyConfig.match(origin);
        HttpClientTransport connectionFactory = client.getTransport();
        if (this.proxy != null) {
            connectionFactory = this.proxy.newClientConnectionFactory(connectionFactory);
            if (!intrinsicallySecure && this.proxy.isSecure()) {
                connectionFactory = this.newSslClientConnectionFactory(this.proxy.getSslContextFactory(), connectionFactory);
            }
        } else if (!intrinsicallySecure && this.isSecure()) {
            connectionFactory = this.newSslClientConnectionFactory(null, connectionFactory);
        }
        if ((tag = origin.getTag()) instanceof ClientConnectionFactory.Decorator) {
            connectionFactory = ((ClientConnectionFactory.Decorator)tag).apply((ClientConnectionFactory)connectionFactory);
        }
        this.connectionFactory = connectionFactory;
    }

    public void accept(Connection connection) {
        this.connectionPool.accept(connection);
    }

    public boolean stale() {
        try (AutoLock l = this.staleLock.lock();){
            boolean stale = this.stale;
            if (!stale) {
                this.activeNanoTime = NanoTime.now();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Stale check done with result {} on {}", (Object)stale, (Object)this);
            }
            boolean bl = stale;
            return bl;
        }
    }

    public boolean sweep() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sweep check in progress on {}", (Object)this);
        }
        boolean remove = false;
        try (AutoLock l = this.staleLock.lock();){
            boolean stale;
            boolean bl = stale = this.exchanges.isEmpty() && this.connectionPool.isEmpty();
            if (!stale) {
                this.activeNanoTime = NanoTime.now();
            } else if (NanoTime.millisSince((long)this.activeNanoTime) >= this.getHttpClient().getDestinationIdleTimeout()) {
                this.stale = true;
                remove = true;
            }
        }
        if (remove) {
            this.getHttpClient().removeDestination(this);
            LifeCycle.stop((Object)this);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sweep check done with result {} on {}", (Object)remove, (Object)this);
        }
        return remove;
    }

    protected void doStart() throws Exception {
        Sweeper destinationSweeper;
        this.connectionPool = this.newConnectionPool(this.client);
        this.addBean(this.connectionPool, true);
        super.doStart();
        Sweeper connectionPoolSweeper = (Sweeper)this.client.getBean(Sweeper.class);
        if (connectionPoolSweeper != null && this.connectionPool instanceof Sweeper.Sweepable) {
            connectionPoolSweeper.offer((Sweeper.Sweepable)this.connectionPool);
        }
        if ((destinationSweeper = this.getHttpClient().getDestinationSweeper()) != null) {
            destinationSweeper.offer((Sweeper.Sweepable)this);
        }
    }

    protected void doStop() throws Exception {
        Sweeper connectionPoolSweeper;
        Sweeper destinationSweeper = this.getHttpClient().getDestinationSweeper();
        if (destinationSweeper != null) {
            destinationSweeper.remove((Sweeper.Sweepable)this);
        }
        if ((connectionPoolSweeper = (Sweeper)this.client.getBean(Sweeper.class)) != null && this.connectionPool instanceof Sweeper.Sweepable) {
            connectionPoolSweeper.remove((Sweeper.Sweepable)this.connectionPool);
        }
        super.doStop();
        this.removeBean(this.connectionPool);
    }

    protected ConnectionPool newConnectionPool(HttpClient client) {
        return client.getTransport().getConnectionPoolFactory().newConnectionPool(this);
    }

    protected Queue<HttpExchange> newExchangeQueue(HttpClient client) {
        int maxCapacity = client.getMaxRequestsQueuedPerDestination();
        if (maxCapacity > 32) {
            return new BlockingArrayQueue(32, 32, maxCapacity);
        }
        return new BlockingArrayQueue(maxCapacity);
    }

    protected ClientConnectionFactory newSslClientConnectionFactory(SslContextFactory.Client sslContextFactory, ClientConnectionFactory connectionFactory) {
        return this.client.newSslClientConnectionFactory(sslContextFactory, connectionFactory);
    }

    public boolean isSecure() {
        return HttpClient.isSchemeSecure(this.getScheme());
    }

    public HttpClient getHttpClient() {
        return this.client;
    }

    public Origin getOrigin() {
        return this.origin;
    }

    public Queue<HttpExchange> getHttpExchanges() {
        return this.exchanges;
    }

    public RequestNotifier getRequestNotifier() {
        return this.requestNotifier;
    }

    public ResponseNotifier getResponseNotifier() {
        return this.responseNotifier;
    }

    public ProxyConfiguration.Proxy getProxy() {
        return this.proxy;
    }

    public ClientConnectionFactory getClientConnectionFactory() {
        return this.connectionFactory;
    }

    @Override
    @ManagedAttribute(value="The destination scheme", readonly=true)
    public String getScheme() {
        return this.getOrigin().getScheme();
    }

    @Override
    @ManagedAttribute(value="The destination host", readonly=true)
    public String getHost() {
        return this.getOrigin().getAddress().getHost();
    }

    @Override
    @ManagedAttribute(value="The destination port", readonly=true)
    public int getPort() {
        return this.getOrigin().getAddress().getPort();
    }

    @ManagedAttribute(value="The number of queued requests", readonly=true)
    public int getQueuedRequestCount() {
        return this.exchanges.size();
    }

    public Origin.Address getConnectAddress() {
        return this.proxy == null ? this.getOrigin().getAddress() : this.proxy.getAddress();
    }

    public HttpField getHostField() {
        return this.hostField;
    }

    @ManagedAttribute(value="The connection pool", readonly=true)
    public ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    public void succeeded() {
        this.send(false);
    }

    public void failed(Throwable x) {
        this.abort(x);
    }

    public void send(Request request, Response.CompleteListener listener) {
        ((HttpRequest)request).sendAsync(this, listener);
    }

    protected void send(HttpRequest request, List<Response.ResponseListener> listeners) {
        this.send(new HttpExchange(this, request, listeners));
    }

    public void send(HttpExchange exchange) {
        HttpRequest request = exchange.getRequest();
        if (this.client.isRunning()) {
            if (this.enqueue(this.exchanges, exchange)) {
                request.sent();
                this.requestTimeouts.schedule(exchange);
                if (!this.client.isRunning() && this.exchanges.remove(exchange)) {
                    request.abort(new RejectedExecutionException(this.client + " is stopping"));
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Queued {} for {}", (Object)request, (Object)this);
                    }
                    this.requestNotifier.notifyQueued(request);
                    this.send();
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Max queue size {} exceeded by {} for {}", new Object[]{this.client.getMaxRequestsQueuedPerDestination(), request, this});
                }
                request.abort(new RejectedExecutionException("Max requests queued per destination " + this.client.getMaxRequestsQueuedPerDestination() + " exceeded for " + this));
            }
        } else {
            request.abort(new RejectedExecutionException(this.client + " is stopped"));
        }
    }

    protected boolean enqueue(Queue<HttpExchange> queue, HttpExchange exchange) {
        return queue.offer(exchange);
    }

    public void send() {
        this.send(true);
    }

    private void send(boolean create) {
        if (!this.getHttpExchanges().isEmpty()) {
            this.process(create);
        }
    }

    private void process(boolean create) {
        boolean proceed;
        Connection connection;
        while ((connection = this.connectionPool.acquire(create)) != null && (proceed = this.process(connection))) {
            create = false;
        }
    }

    private boolean process(Connection connection) {
        HttpClient client = this.getHttpClient();
        HttpExchange exchange = this.getHttpExchanges().poll();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Processing exchange {} on {} of {}", new Object[]{exchange, connection, this});
        }
        if (exchange == null) {
            if (!this.connectionPool.release(connection)) {
                connection.close();
            }
            if (!client.isRunning()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} is stopping", (Object)client);
                }
                connection.close();
            }
            return false;
        }
        HttpRequest request = exchange.getRequest();
        Throwable cause = request.getAbortCause();
        if (cause != null) {
            boolean released;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Aborted before processing {}: {}", (Object)exchange, (Object)cause);
            }
            if (!(released = this.connectionPool.release(connection))) {
                connection.close();
            }
            exchange.abort(cause);
            return this.getQueuedRequestCount() > 0;
        }
        SendFailure failure = this.send((IConnection)connection, exchange);
        if (failure == null) {
            return this.getQueuedRequestCount() > 0;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Send failed {} for {}", (Object)failure, (Object)exchange);
        }
        if (failure.retry) {
            this.send(exchange);
            return false;
        }
        request.abort(failure.failure);
        return this.getQueuedRequestCount() > 0;
    }

    protected SendFailure send(IConnection connection, HttpExchange exchange) {
        return connection.send(exchange);
    }

    @Override
    public void newConnection(Promise<Connection> promise) {
        this.createConnection(promise);
    }

    protected void createConnection(Promise<Connection> promise) {
        this.client.newConnection(this, promise);
    }

    public boolean remove(HttpExchange exchange) {
        return this.exchanges.remove(exchange);
    }

    @Override
    public void close() {
        this.abort(new AsynchronousCloseException());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Closed {}", (Object)this);
        }
        this.connectionPool.close();
        this.requestTimeouts.destroy();
    }

    public void release(Connection connection) {
        HttpClient client;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Released {}", (Object)connection);
        }
        if ((client = this.getHttpClient()).isRunning()) {
            if (this.connectionPool.isActive(connection)) {
                if (this.connectionPool.release(connection)) {
                    this.send(false);
                } else {
                    connection.close();
                    this.send(true);
                }
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("Released explicit {}", (Object)connection);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} is stopped", (Object)client);
            }
            connection.close();
        }
    }

    public boolean remove(Connection connection) {
        boolean removed = this.connectionPool.remove(connection);
        if (removed) {
            this.send(true);
        }
        return removed;
    }

    public void abort(Throwable cause) {
        for (HttpExchange exchange : new ArrayList<HttpExchange>(this.exchanges)) {
            exchange.getRequest().abort(cause);
        }
    }

    public void dump(Appendable out, String indent) throws IOException {
        this.dumpObjects(out, indent, new Object[]{new DumpableCollection("exchanges", this.exchanges)});
    }

    public String asString() {
        return this.getOrigin().asString();
    }

    @ManagedAttribute(value="For how long this destination has been idle in ms")
    public long getIdle() {
        if (this.getHttpClient().getDestinationIdleTimeout() <= 0L) {
            return -1L;
        }
        try (AutoLock l = this.staleLock.lock();){
            long l2 = NanoTime.millisSince((long)this.activeNanoTime);
            return l2;
        }
    }

    @ManagedAttribute(value="Whether this destinations is stale")
    public boolean isStale() {
        try (AutoLock l = this.staleLock.lock();){
            boolean bl = this.stale;
            return bl;
        }
    }

    public String toString() {
        return String.format("%s[%s]@%x%s,state=%s,queue=%d,pool=%s,stale=%b,idle=%d", HttpDestination.class.getSimpleName(), this.getOrigin(), this.hashCode(), this.proxy == null ? "" : "(via " + this.proxy + ")", this.getState(), this.getQueuedRequestCount(), this.getConnectionPool(), this.isStale(), this.getIdle());
    }

    private class RequestTimeouts
    extends CyclicTimeouts<HttpExchange> {
        private RequestTimeouts(Scheduler scheduler) {
            super(scheduler);
        }

        protected Iterator<HttpExchange> iterator() {
            return HttpDestination.this.exchanges.iterator();
        }

        protected boolean onExpired(HttpExchange exchange) {
            HttpRequest request = exchange.getRequest();
            request.abort(new TimeoutException("Total timeout " + request.getConversation().getTimeout() + " ms elapsed"));
            return false;
        }
    }

    @FunctionalInterface
    public static interface Multiplexed {
        public void setMaxRequestsPerConnection(int var1);
    }
}

