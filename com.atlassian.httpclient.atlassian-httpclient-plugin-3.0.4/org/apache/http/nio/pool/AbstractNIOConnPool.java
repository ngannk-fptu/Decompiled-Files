/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.pool;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.pool.LeaseRequest;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.pool.RouteSpecificPool;
import org.apache.http.nio.pool.SocketAddressResolver;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.apache.http.pool.ConnPool;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolEntry;
import org.apache.http.pool.PoolEntryCallback;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public abstract class AbstractNIOConnPool<T, C, E extends PoolEntry<T, C>>
implements ConnPool<T, E>,
ConnPoolControl<T> {
    private final ConnectingIOReactor ioReactor;
    private final NIOConnFactory<T, C> connFactory;
    private final SocketAddressResolver<T> addressResolver;
    private final SessionRequestCallback sessionRequestCallback;
    private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
    private final LinkedList<LeaseRequest<T, C, E>> leasingRequests;
    private final Set<SessionRequest> pending;
    private final Set<E> leased;
    private final LinkedList<E> available;
    private final ConcurrentLinkedQueue<LeaseRequest<T, C, E>> completedRequests;
    private final Map<T, Integer> maxPerRoute;
    private final Lock lock;
    private final AtomicBoolean isShutDown;
    private volatile int defaultMaxPerRoute;
    private volatile int maxTotal;

    @Deprecated
    public AbstractNIOConnPool(ConnectingIOReactor ioReactor, NIOConnFactory<T, C> connFactory, int defaultMaxPerRoute, int maxTotal) {
        Args.notNull(ioReactor, "I/O reactor");
        Args.notNull(connFactory, "Connection factory");
        Args.positive(defaultMaxPerRoute, "Max per route value");
        Args.positive(maxTotal, "Max total value");
        this.ioReactor = ioReactor;
        this.connFactory = connFactory;
        this.addressResolver = new SocketAddressResolver<T>(){

            @Override
            public SocketAddress resolveLocalAddress(T route) throws IOException {
                return AbstractNIOConnPool.this.resolveLocalAddress(route);
            }

            @Override
            public SocketAddress resolveRemoteAddress(T route) throws IOException {
                return AbstractNIOConnPool.this.resolveRemoteAddress(route);
            }
        };
        this.sessionRequestCallback = new InternalSessionRequestCallback();
        this.routeToPool = new HashMap<T, RouteSpecificPool<T, C, E>>();
        this.leasingRequests = new LinkedList();
        this.pending = new HashSet<SessionRequest>();
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.maxPerRoute = new HashMap<T, Integer>();
        this.completedRequests = new ConcurrentLinkedQueue();
        this.lock = new ReentrantLock();
        this.isShutDown = new AtomicBoolean(false);
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotal = maxTotal;
    }

    public AbstractNIOConnPool(ConnectingIOReactor ioReactor, NIOConnFactory<T, C> connFactory, SocketAddressResolver<T> addressResolver, int defaultMaxPerRoute, int maxTotal) {
        Args.notNull(ioReactor, "I/O reactor");
        Args.notNull(connFactory, "Connection factory");
        Args.notNull(addressResolver, "Address resolver");
        Args.positive(defaultMaxPerRoute, "Max per route value");
        Args.positive(maxTotal, "Max total value");
        this.ioReactor = ioReactor;
        this.connFactory = connFactory;
        this.addressResolver = addressResolver;
        this.sessionRequestCallback = new InternalSessionRequestCallback();
        this.routeToPool = new HashMap<T, RouteSpecificPool<T, C, E>>();
        this.leasingRequests = new LinkedList();
        this.pending = new HashSet<SessionRequest>();
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.completedRequests = new ConcurrentLinkedQueue();
        this.maxPerRoute = new HashMap<T, Integer>();
        this.lock = new ReentrantLock();
        this.isShutDown = new AtomicBoolean(false);
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotal = maxTotal;
    }

    @Deprecated
    protected SocketAddress resolveRemoteAddress(T route) {
        return null;
    }

    @Deprecated
    protected SocketAddress resolveLocalAddress(T route) {
        return null;
    }

    protected abstract E createEntry(T var1, C var2);

    protected void onLease(E entry) {
    }

    protected void onRelease(E entry) {
    }

    protected void onReuse(E entry) {
    }

    public boolean isShutdown() {
        return this.isShutDown.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown(long waitMs) throws IOException {
        if (this.isShutDown.compareAndSet(false, true)) {
            this.fireCallbacks();
            this.lock.lock();
            try {
                for (SessionRequest sessionRequest : this.pending) {
                    sessionRequest.cancel();
                }
                for (PoolEntry poolEntry : this.available) {
                    poolEntry.close();
                }
                for (PoolEntry poolEntry : this.leased) {
                    poolEntry.close();
                }
                for (RouteSpecificPool routeSpecificPool : this.routeToPool.values()) {
                    routeSpecificPool.shutdown();
                }
                this.routeToPool.clear();
                this.leased.clear();
                this.pending.clear();
                this.available.clear();
                this.leasingRequests.clear();
                this.ioReactor.shutdown(waitMs);
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    private RouteSpecificPool<T, C, E> getPool(T route) {
        RouteSpecificPool pool = this.routeToPool.get(route);
        if (pool == null) {
            pool = new RouteSpecificPool<T, C, E>(route){

                @Override
                protected E createEntry(T route, C conn) {
                    return AbstractNIOConnPool.this.createEntry(route, conn);
                }
            };
            this.routeToPool.put(route, pool);
        }
        return pool;
    }

    public Future<E> lease(T route, Object state, long connectTimeout, TimeUnit timeUnit, FutureCallback<E> callback) {
        return this.lease(route, state, connectTimeout, connectTimeout, timeUnit, callback);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Future<E> lease(T route, Object state, long connectTimeout, long leaseTimeout, TimeUnit timeUnit, FutureCallback<E> callback) {
        Args.notNull(route, "Route");
        Args.notNull(timeUnit, "Time unit");
        Asserts.check(!this.isShutDown.get(), "Connection pool shut down");
        final BasicFuture<E> future = new BasicFuture<E>(callback);
        final LeaseRequest leaseRequest = new LeaseRequest(route, state, connectTimeout >= 0L ? timeUnit.toMillis(connectTimeout) : -1L, leaseTimeout > 0L ? timeUnit.toMillis(leaseTimeout) : 0L, future);
        this.lock.lock();
        try {
            boolean completed = this.processPendingRequest(leaseRequest);
            if (!leaseRequest.isDone() && !completed) {
                this.leasingRequests.add(leaseRequest);
            }
            if (leaseRequest.isDone()) {
                this.completedRequests.add(leaseRequest);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
        return new Future<E>(){

            @Override
            public E get() throws InterruptedException, ExecutionException {
                return (PoolEntry)future.get();
            }

            @Override
            public E get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return (PoolEntry)future.get(timeout, unit);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    leaseRequest.cancel();
                }
                finally {
                    return future.cancel(mayInterruptIfRunning);
                }
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }
        };
    }

    @Override
    public Future<E> lease(T route, Object state, FutureCallback<E> callback) {
        return this.lease(route, state, -1L, TimeUnit.MICROSECONDS, callback);
    }

    public Future<E> lease(T route, Object state) {
        return this.lease(route, state, -1L, TimeUnit.MICROSECONDS, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void release(E entry, boolean reusable) {
        if (entry == null) {
            return;
        }
        if (this.isShutDown.get()) {
            return;
        }
        this.lock.lock();
        try {
            if (this.leased.remove(entry)) {
                RouteSpecificPool pool = this.getPool(((PoolEntry)entry).getRoute());
                pool.free(entry, reusable);
                if (reusable) {
                    this.available.addFirst(entry);
                    this.onRelease(entry);
                } else {
                    ((PoolEntry)entry).close();
                }
                this.processNextPendingRequest();
            }
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
    }

    private void processPendingRequests() {
        ListIterator it = this.leasingRequests.listIterator();
        while (it.hasNext()) {
            LeaseRequest request = (LeaseRequest)it.next();
            BasicFuture future = request.getFuture();
            if (future.isCancelled()) {
                it.remove();
                continue;
            }
            boolean completed = this.processPendingRequest(request);
            if (request.isDone() || completed) {
                it.remove();
            }
            if (!request.isDone()) continue;
            this.completedRequests.add(request);
        }
    }

    private void processNextPendingRequest() {
        ListIterator it = this.leasingRequests.listIterator();
        while (it.hasNext()) {
            LeaseRequest request = (LeaseRequest)it.next();
            BasicFuture future = request.getFuture();
            if (future.isCancelled()) {
                it.remove();
                continue;
            }
            boolean completed = this.processPendingRequest(request);
            if (request.isDone() || completed) {
                it.remove();
            }
            if (request.isDone()) {
                this.completedRequests.add(request);
            }
            if (!completed) continue;
            return;
        }
    }

    private boolean processPendingRequest(LeaseRequest<T, C, E> request) {
        E entry;
        T route = request.getRoute();
        Object state = request.getState();
        long deadline = request.getDeadline();
        long now = System.currentTimeMillis();
        if (now > deadline) {
            request.failed(new TimeoutException("Connection lease request time out"));
            return false;
        }
        RouteSpecificPool<T, C, E> pool = this.getPool(route);
        while ((entry = pool.getFree(state)) != null && (((PoolEntry)entry).isClosed() || ((PoolEntry)entry).isExpired(System.currentTimeMillis()))) {
            ((PoolEntry)entry).close();
            this.available.remove(entry);
            pool.free(entry, false);
        }
        if (entry != null) {
            this.available.remove(entry);
            this.leased.add(entry);
            request.completed(entry);
            this.onReuse(entry);
            this.onLease(entry);
            return true;
        }
        int maxPerRoute = this.getMax(route);
        int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
        if (excess > 0) {
            E lastUsed;
            for (int i = 0; i < excess && (lastUsed = pool.getLastUsed()) != null; ++i) {
                ((PoolEntry)lastUsed).close();
                this.available.remove(lastUsed);
                pool.remove(lastUsed);
            }
        }
        if (pool.getAllocatedCount() < maxPerRoute) {
            SocketAddress localAddress;
            SocketAddress remoteAddress;
            int totalUsed = this.pending.size() + this.leased.size();
            int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
            if (freeCapacity == 0) {
                return false;
            }
            int totalAvailable = this.available.size();
            if (totalAvailable > freeCapacity - 1) {
                PoolEntry lastUsed = (PoolEntry)this.available.removeLast();
                lastUsed.close();
                RouteSpecificPool otherpool = this.getPool(lastUsed.getRoute());
                otherpool.remove(lastUsed);
            }
            try {
                remoteAddress = this.addressResolver.resolveRemoteAddress(route);
                localAddress = this.addressResolver.resolveLocalAddress(route);
            }
            catch (IOException ex) {
                request.failed(ex);
                return false;
            }
            SessionRequest sessionRequest = this.ioReactor.connect(remoteAddress, localAddress, route, this.sessionRequestCallback);
            request.attachSessionRequest(sessionRequest);
            long connectTimeout = request.getConnectTimeout();
            if (connectTimeout >= 0L) {
                sessionRequest.setConnectTimeout(connectTimeout < Integer.MAX_VALUE ? (int)connectTimeout : Integer.MAX_VALUE);
            }
            this.pending.add(sessionRequest);
            pool.addPending(sessionRequest, request.getFuture());
            return true;
        }
        return false;
    }

    private void fireCallbacks() {
        LeaseRequest<T, C, E> request;
        while ((request = this.completedRequests.poll()) != null) {
            BasicFuture<E> future = request.getFuture();
            Exception ex = request.getException();
            E result = request.getResult();
            boolean successfullyCompleted = false;
            if (ex != null) {
                future.failed(ex);
            } else if (result != null) {
                if (future.completed(result)) {
                    successfullyCompleted = true;
                }
            } else {
                future.cancel();
            }
            if (successfullyCompleted) continue;
            this.release(result, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void validatePendingRequests() {
        this.lock.lock();
        try {
            long now = System.currentTimeMillis();
            ListIterator it = this.leasingRequests.listIterator();
            while (it.hasNext()) {
                LeaseRequest request = (LeaseRequest)it.next();
                BasicFuture future = request.getFuture();
                if (future.isCancelled() && !request.isDone()) {
                    it.remove();
                    continue;
                }
                long deadline = request.getDeadline();
                if (now > deadline) {
                    request.failed(new TimeoutException("Connection lease request time out"));
                }
                if (!request.isDone()) continue;
                it.remove();
                this.completedRequests.add(request);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void requestCompleted(SessionRequest request) {
        if (this.isShutDown.get()) {
            IOSession session = request.getSession();
            if (session != null) {
                session.close();
            }
            return;
        }
        Object route = request.getAttachment();
        this.lock.lock();
        try {
            this.pending.remove(request);
            RouteSpecificPool<Object, C, E> pool = this.getPool(route);
            IOSession session = request.getSession();
            try {
                C conn = this.connFactory.create(route, session);
                E entry = pool.createEntry(request, conn);
                if (pool.completed(request, entry)) {
                    this.leased.add(entry);
                    this.onLease(entry);
                } else {
                    this.available.add(entry);
                    if (this.ioReactor.getStatus().compareTo(IOReactorStatus.ACTIVE) <= 0) {
                        this.processNextPendingRequest();
                    }
                }
            }
            catch (IOException ex) {
                pool.failed(request, ex);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void requestCancelled(SessionRequest request) {
        if (this.isShutDown.get()) {
            return;
        }
        Object route = request.getAttachment();
        this.lock.lock();
        try {
            this.pending.remove(request);
            RouteSpecificPool<Object, C, E> pool = this.getPool(route);
            pool.cancelled(request);
            if (this.ioReactor.getStatus().compareTo(IOReactorStatus.ACTIVE) <= 0) {
                this.processNextPendingRequest();
            }
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void requestFailed(SessionRequest request) {
        if (this.isShutDown.get()) {
            return;
        }
        Object route = request.getAttachment();
        this.lock.lock();
        try {
            this.pending.remove(request);
            RouteSpecificPool<Object, C, E> pool = this.getPool(route);
            pool.failed(request, request.getException());
            this.processNextPendingRequest();
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void requestTimeout(SessionRequest request) {
        if (this.isShutDown.get()) {
            return;
        }
        Object route = request.getAttachment();
        this.lock.lock();
        try {
            this.pending.remove(request);
            RouteSpecificPool<Object, C, E> pool = this.getPool(route);
            pool.timeout(request);
            this.processNextPendingRequest();
        }
        finally {
            this.lock.unlock();
        }
        this.fireCallbacks();
    }

    private int getMax(T route) {
        Integer v = this.maxPerRoute.get(route);
        return v != null ? v : this.defaultMaxPerRoute;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaxTotal(int max) {
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.maxTotal = max;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getMaxTotal() {
        this.lock.lock();
        try {
            int n = this.maxTotal;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDefaultMaxPerRoute(int max) {
        Args.positive(max, "Max value");
        this.lock.lock();
        try {
            this.defaultMaxPerRoute = max;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getDefaultMaxPerRoute() {
        this.lock.lock();
        try {
            int n = this.defaultMaxPerRoute;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaxPerRoute(T route, int max) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            if (max > -1) {
                this.maxPerRoute.put(route, max);
            } else {
                this.maxPerRoute.remove(route);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getMaxPerRoute(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            int n = this.getMax(route);
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PoolStats getTotalStats() {
        this.lock.lock();
        try {
            PoolStats poolStats = new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
            return poolStats;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PoolStats getStats(T route) {
        Args.notNull(route, "Route");
        this.lock.lock();
        try {
            RouteSpecificPool<T, C, E> pool = this.getPool(route);
            int pendingCount = 0;
            for (LeaseRequest leaseRequest : this.leasingRequests) {
                if (!LangUtils.equals(route, leaseRequest.getRoute())) continue;
                ++pendingCount;
            }
            PoolStats poolStats = new PoolStats(pool.getLeasedCount(), pendingCount + pool.getPendingCount(), pool.getAvailableCount(), this.getMax(route));
            return poolStats;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<T> getRoutes() {
        this.lock.lock();
        try {
            HashSet<T> hashSet = new HashSet<T>(this.routeToPool.keySet());
            return hashSet;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void enumAvailable(PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            Iterator it = this.available.iterator();
            while (it.hasNext()) {
                PoolEntry entry = (PoolEntry)it.next();
                callback.process(entry);
                if (!entry.isClosed()) continue;
                RouteSpecificPool pool = this.getPool(entry.getRoute());
                pool.remove(entry);
                it.remove();
            }
            this.processPendingRequests();
            this.purgePoolMap();
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void enumLeased(PoolEntryCallback<T, C> callback) {
        this.lock.lock();
        try {
            for (PoolEntry entry : this.leased) {
                callback.process(entry);
            }
            this.processPendingRequests();
        }
        finally {
            this.lock.unlock();
        }
    }

    @Deprecated
    protected void enumEntries(Iterator<E> it, PoolEntryCallback<T, C> callback) {
        while (it.hasNext()) {
            PoolEntry entry = (PoolEntry)it.next();
            callback.process(entry);
        }
        this.processPendingRequests();
    }

    private void purgePoolMap() {
        Iterator<Map.Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<T, RouteSpecificPool<T, C, E>> entry = it.next();
            RouteSpecificPool<T, C, E> pool = entry.getValue();
            if (pool.getAllocatedCount() != 0) continue;
            it.remove();
        }
    }

    public void closeIdle(long idletime, TimeUnit timeUnit) {
        Args.notNull(timeUnit, "Time unit");
        long time = timeUnit.toMillis(idletime);
        if (time < 0L) {
            time = 0L;
        }
        final long deadline = System.currentTimeMillis() - time;
        this.enumAvailable(new PoolEntryCallback<T, C>(){

            @Override
            public void process(PoolEntry<T, C> entry) {
                if (entry.getUpdated() <= deadline) {
                    entry.close();
                }
            }
        });
    }

    public void closeExpired() {
        final long now = System.currentTimeMillis();
        this.enumAvailable(new PoolEntryCallback<T, C>(){

            @Override
            public void process(PoolEntry<T, C> entry) {
                if (entry.isExpired(now)) {
                    entry.close();
                }
            }
        });
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[leased: ");
        buffer.append(this.leased);
        buffer.append("][available: ");
        buffer.append(this.available);
        buffer.append("][pending: ");
        buffer.append(this.pending);
        buffer.append("]");
        return buffer.toString();
    }

    class InternalSessionRequestCallback
    implements SessionRequestCallback {
        InternalSessionRequestCallback() {
        }

        @Override
        public void completed(SessionRequest request) {
            AbstractNIOConnPool.this.requestCompleted(request);
        }

        @Override
        public void cancelled(SessionRequest request) {
            AbstractNIOConnPool.this.requestCancelled(request);
        }

        @Override
        public void failed(SessionRequest request) {
            AbstractNIOConnPool.this.requestFailed(request);
        }

        @Override
        public void timeout(SessionRequest request) {
            AbstractNIOConnPool.this.requestTimeout(request);
        }
    }
}

