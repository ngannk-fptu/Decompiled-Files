/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Attachable
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.Pool
 *  org.eclipse.jetty.util.Pool$Entry
 *  org.eclipse.jetty.util.Pool$StrategyType
 *  org.eclipse.jetty.util.Promise
 *  org.eclipse.jetty.util.Promise$Completable
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.thread.Sweeper$Sweepable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.ConnectionPool;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.thread.Sweeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public abstract class AbstractConnectionPool
extends ContainerLifeCycle
implements ConnectionPool,
Dumpable,
Sweeper.Sweepable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnectionPool.class);
    private final AtomicInteger pending = new AtomicInteger();
    private final HttpDestination destination;
    private final Callback requester;
    private final Pool<Connection> pool;
    private boolean maximizeConnections;
    private volatile long maxDurationNanos = 0L;

    protected AbstractConnectionPool(HttpDestination destination, int maxConnections, boolean cache, Callback requester) {
        this(destination, Pool.StrategyType.FIRST, maxConnections, cache, requester);
    }

    protected AbstractConnectionPool(HttpDestination destination, Pool.StrategyType strategy, int maxConnections, boolean cache, Callback requester) {
        this(destination, (Pool<Connection>)new Pool(strategy, maxConnections, cache), requester);
    }

    protected AbstractConnectionPool(HttpDestination destination, Pool<Connection> pool, Callback requester) {
        this.destination = destination;
        this.requester = requester;
        this.pool = pool;
        pool.setMaxMultiplex(1);
        this.addBean(pool);
    }

    protected void doStop() throws Exception {
        this.pool.close();
    }

    @Override
    public CompletableFuture<Void> preCreateConnections(int connectionCount) {
        Pool.Entry entry;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Pre-creating connections {}/{}", (Object)connectionCount, (Object)this.getMaxConnectionCount());
        }
        ArrayList<FutureConnection> futures = new ArrayList<FutureConnection>();
        for (int i = 0; i < connectionCount && (entry = this.pool.reserve()) != null; ++i) {
            this.pending.incrementAndGet();
            FutureConnection future = new FutureConnection(entry);
            futures.add(future);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pre-creating connection {}/{} at {}", new Object[]{futures.size(), this.getMaxConnectionCount(), entry});
            }
            this.destination.newConnection((Promise<Connection>)future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @ManagedAttribute(value="The maximum duration in milliseconds a connection can be used for before it gets closed")
    public long getMaxDuration() {
        return TimeUnit.NANOSECONDS.toMillis(this.maxDurationNanos);
    }

    public void setMaxDuration(long maxDurationInMs) {
        this.maxDurationNanos = TimeUnit.MILLISECONDS.toNanos(maxDurationInMs);
    }

    protected int getMaxMultiplex() {
        return this.pool.getMaxMultiplex();
    }

    protected void setMaxMultiplex(int maxMultiplex) {
        this.pool.setMaxMultiplex(maxMultiplex);
    }

    protected int getMaxUsageCount() {
        return this.pool.getMaxUsageCount();
    }

    protected void setMaxUsageCount(int maxUsageCount) {
        this.pool.setMaxUsageCount(maxUsageCount);
    }

    @ManagedAttribute(value="The number of active connections", readonly=true)
    public int getActiveConnectionCount() {
        return this.pool.getInUseCount();
    }

    @ManagedAttribute(value="The number of idle connections", readonly=true)
    public int getIdleConnectionCount() {
        return this.pool.getIdleCount();
    }

    @ManagedAttribute(value="The max number of connections", readonly=true)
    public int getMaxConnectionCount() {
        return this.pool.getMaxEntries();
    }

    @ManagedAttribute(value="The number of connections", readonly=true)
    public int getConnectionCount() {
        return this.pool.size();
    }

    @ManagedAttribute(value="The number of pending connections", readonly=true)
    public int getPendingConnectionCount() {
        return this.pending.get();
    }

    @Override
    public boolean isEmpty() {
        return this.pool.size() == 0;
    }

    @Override
    @ManagedAttribute(value="Whether this pool is closed")
    public boolean isClosed() {
        return this.pool.isClosed();
    }

    @ManagedAttribute(value="Whether the pool tries to maximize the number of connections used")
    public boolean isMaximizeConnections() {
        return this.maximizeConnections;
    }

    public void setMaximizeConnections(boolean maximizeConnections) {
        this.maximizeConnections = maximizeConnections;
    }

    @Override
    public Connection acquire(boolean create) {
        Connection connection;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Acquiring create={} on {}", (Object)create, (Object)this);
        }
        if ((connection = this.activate()) == null) {
            this.tryCreate(create);
            connection = this.activate();
        }
        return connection;
    }

    protected void tryCreate(boolean create) {
        int pending;
        int connectionCount = this.getConnectionCount();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Try creating connection {}/{} with {} pending", new Object[]{connectionCount, this.getMaxConnectionCount(), this.getPendingConnectionCount()});
        }
        int multiplexed = this.getMaxMultiplex();
        do {
            boolean tryCreate;
            pending = this.pending.get();
            int supply = pending * multiplexed;
            int demand = this.destination.getQueuedRequestCount() + (create ? 1 : 0);
            boolean bl = tryCreate = this.isMaximizeConnections() || supply < demand;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Try creating({}) connection, pending/demand/supply: {}/{}/{}, result={}", new Object[]{create, pending, demand, supply, tryCreate});
            }
            if (tryCreate) continue;
            return;
        } while (!this.pending.compareAndSet(pending, pending + 1));
        Pool.Entry entry = this.pool.reserve();
        if (entry == null) {
            this.pending.decrementAndGet();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Not creating connection as pool {} is full, pending: {}", this.pool, (Object)this.pending);
            }
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating connection {}/{} at {}", new Object[]{connectionCount, this.getMaxConnectionCount(), entry});
        }
        FutureConnection future = new FutureConnection(entry);
        this.destination.newConnection((Promise<Connection>)future);
    }

    @Override
    public boolean accept(Connection connection) {
        if (!(connection instanceof Attachable)) {
            throw new IllegalArgumentException("Invalid connection object: " + connection);
        }
        Pool.Entry entry = this.pool.reserve();
        if (entry == null) {
            return false;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("onCreating {} {}", (Object)entry, (Object)connection);
        }
        Attachable attachable = (Attachable)connection;
        attachable.setAttachment((Object)new EntryHolder(entry));
        this.onCreated(connection);
        entry.enable((Object)connection, false);
        this.idle(connection, false);
        return true;
    }

    protected void proceed() {
        this.requester.succeeded();
    }

    protected Connection activate() {
        Pool.Entry entry;
        while ((entry = this.pool.acquire()) != null) {
            EntryHolder holder;
            Connection connection = (Connection)entry.getPooled();
            long maxDurationNanos = this.maxDurationNanos;
            if (maxDurationNanos > 0L && (holder = (EntryHolder)((Attachable)connection).getAttachment()).isExpired(maxDurationNanos)) {
                boolean canClose = this.remove(connection);
                if (canClose) {
                    IO.close((Closeable)connection);
                }
                if (!LOG.isDebugEnabled()) continue;
                LOG.debug("Connection removed{} due to expiration {} {}", new Object[]{canClose ? " and closed" : "", entry, this.pool});
                continue;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Activated {} {}", (Object)entry, this.pool);
            }
            this.acquired(connection);
            return connection;
        }
        return null;
    }

    @Override
    public boolean isActive(Connection connection) {
        if (!(connection instanceof Attachable)) {
            throw new IllegalArgumentException("Invalid connection object: " + connection);
        }
        Attachable attachable = (Attachable)connection;
        EntryHolder holder = (EntryHolder)attachable.getAttachment();
        if (holder == null) {
            return false;
        }
        return !holder.entry.isIdle();
    }

    @Override
    public boolean release(Connection connection) {
        if (!this.deactivate(connection)) {
            return false;
        }
        this.released(connection);
        return this.idle(connection, this.isClosed());
    }

    protected boolean deactivate(Connection connection) {
        if (!(connection instanceof Attachable)) {
            throw new IllegalArgumentException("Invalid connection object: " + connection);
        }
        Attachable attachable = (Attachable)connection;
        EntryHolder holder = (EntryHolder)attachable.getAttachment();
        if (holder == null) {
            return true;
        }
        long maxDurationNanos = this.maxDurationNanos;
        if (maxDurationNanos > 0L && holder.isExpired(maxDurationNanos)) {
            return !this.remove(connection);
        }
        boolean reusable = this.pool.release(holder.entry);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Released ({}) {} {}", new Object[]{reusable, holder.entry, this.pool});
        }
        if (reusable) {
            return true;
        }
        return !this.remove(connection);
    }

    @Override
    public boolean remove(Connection connection) {
        if (!(connection instanceof Attachable)) {
            throw new IllegalArgumentException("Invalid connection object: " + connection);
        }
        Attachable attachable = (Attachable)connection;
        EntryHolder holder = (EntryHolder)attachable.getAttachment();
        if (holder == null) {
            return false;
        }
        boolean removed = this.pool.remove(holder.entry);
        if (removed) {
            attachable.setAttachment(null);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removed ({}) {} {}", new Object[]{removed, holder.entry, this.pool});
        }
        if (removed) {
            this.released(connection);
            this.removed(connection);
        }
        return removed;
    }

    @Deprecated
    protected boolean remove(Connection connection, boolean force) {
        return this.remove(connection);
    }

    protected void onCreated(Connection connection) {
    }

    protected boolean idle(Connection connection, boolean close) {
        return !close;
    }

    protected void acquired(Connection connection) {
    }

    protected void released(Connection connection) {
    }

    protected void removed(Connection connection) {
    }

    Queue<Connection> getIdleConnections() {
        return this.pool.values().stream().filter(Pool.Entry::isIdle).filter(entry -> !entry.isClosed()).map(Pool.Entry::getPooled).collect(Collectors.toCollection(ArrayDeque::new));
    }

    Collection<Connection> getActiveConnections() {
        return this.pool.values().stream().filter(entry -> !entry.isIdle()).filter(entry -> !entry.isClosed()).map(Pool.Entry::getPooled).collect(Collectors.toList());
    }

    @Override
    public void close() {
        block4: {
            try {
                for (Pool.Entry entry : this.pool.values()) {
                    while (entry.isInUse()) {
                        if (!entry.release()) continue;
                        this.released((Connection)entry.getPooled());
                        break;
                    }
                    if (!entry.remove()) continue;
                    this.removed((Connection)entry.getPooled());
                }
            }
            catch (Throwable x) {
                if (!LOG.isDebugEnabled()) break block4;
                LOG.debug("Detected concurrent modification while forcibly releasing the pooled connections", x);
            }
        }
        this.pool.close();
    }

    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[0]);
    }

    public boolean sweep() {
        this.pool.values().stream().map(Pool.Entry::getPooled).filter(connection -> connection instanceof Sweeper.Sweepable).forEach(connection -> {
            if (((Sweeper.Sweepable)connection).sweep()) {
                boolean removed = this.remove((Connection)connection);
                LOG.warn("Connection swept: {}{}{} from active connections{}{}", new Object[]{connection, System.lineSeparator(), removed ? "Removed" : "Not removed", System.lineSeparator(), this.dump()});
            }
        });
        return false;
    }

    public String toString() {
        return String.format("%s@%x[s=%s,c=%d/%d/%d,a=%d,i=%d,q=%d,p=%s]", this.getClass().getSimpleName(), this.hashCode(), this.getState(), this.getPendingConnectionCount(), this.getConnectionCount(), this.getMaxConnectionCount(), this.getActiveConnectionCount(), this.getIdleConnectionCount(), this.destination.getQueuedRequestCount(), this.pool);
    }

    private class FutureConnection
    extends Promise.Completable<Connection> {
        private final Pool.Entry reserved;

        public FutureConnection(Pool.Entry reserved) {
            this.reserved = reserved;
        }

        public void succeeded(Connection connection) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Connection creation succeeded {}: {}", (Object)this.reserved, (Object)connection);
            }
            if (connection instanceof Attachable) {
                ((Attachable)connection).setAttachment((Object)new EntryHolder(this.reserved));
                AbstractConnectionPool.this.onCreated(connection);
                AbstractConnectionPool.this.pending.decrementAndGet();
                this.reserved.enable((Object)connection, false);
                AbstractConnectionPool.this.idle(connection, false);
                this.complete(null);
                AbstractConnectionPool.this.proceed();
            } else {
                this.failed(new IllegalArgumentException("Invalid connection object: " + connection));
            }
        }

        public void failed(Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Connection creation failed {}", (Object)this.reserved, (Object)x);
            }
            AbstractConnectionPool.this.pending.decrementAndGet();
            this.reserved.remove();
            this.completeExceptionally(x);
            AbstractConnectionPool.this.requester.failed(x);
        }
    }

    private static class EntryHolder {
        private final Pool.Entry entry;
        private final long creationNanoTime = NanoTime.now();

        private EntryHolder(Pool.Entry entry) {
            this.entry = Objects.requireNonNull(entry);
        }

        private boolean isExpired(long timeoutNanos) {
            return NanoTime.since((long)this.creationNanoTime) >= timeoutNanos;
        }
    }
}

