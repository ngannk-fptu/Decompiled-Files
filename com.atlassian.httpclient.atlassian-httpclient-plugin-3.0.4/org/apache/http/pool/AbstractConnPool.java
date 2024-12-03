/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.pool;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.pool.ConnFactory;
import org.apache.http.pool.ConnPool;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolEntry;
import org.apache.http.pool.PoolEntryCallback;
import org.apache.http.pool.PoolStats;
import org.apache.http.pool.RouteSpecificPool;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public abstract class AbstractConnPool<T, C, E extends PoolEntry<T, C>>
implements ConnPool<T, E>,
ConnPoolControl<T> {
    private final Lock lock;
    private final Condition condition;
    private final ConnFactory<T, C> connFactory;
    private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
    private final Set<E> leased;
    private final LinkedList<E> available;
    private final LinkedList<Future<E>> pending;
    private final Map<T, Integer> maxPerRoute;
    private volatile boolean isShutDown;
    private volatile int defaultMaxPerRoute;
    private volatile int maxTotal;
    private volatile int validateAfterInactivity;

    public AbstractConnPool(ConnFactory<T, C> connFactory, int defaultMaxPerRoute, int maxTotal) {
        this.connFactory = Args.notNull(connFactory, "Connection factory");
        this.defaultMaxPerRoute = Args.positive(defaultMaxPerRoute, "Max per route value");
        this.maxTotal = Args.positive(maxTotal, "Max total value");
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.routeToPool = new HashMap<T, RouteSpecificPool<T, C, E>>();
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.pending = new LinkedList();
        this.maxPerRoute = new HashMap<T, Integer>();
    }

    protected abstract E createEntry(T var1, C var2);

    protected void onLease(E entry) {
    }

    protected void onRelease(E entry) {
    }

    protected void onReuse(E entry) {
    }

    protected boolean validate(E entry) {
        return true;
    }

    public boolean isShutdown() {
        return this.isShutDown;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() throws IOException {
        if (this.isShutDown) {
            return;
        }
        this.isShutDown = true;
        this.lock.lock();
        try {
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
            this.available.clear();
        }
        finally {
            this.lock.unlock();
        }
    }

    private RouteSpecificPool<T, C, E> getPool(final T route) {
        RouteSpecificPool pool = this.routeToPool.get(route);
        if (pool == null) {
            pool = new RouteSpecificPool<T, C, E>(route){

                @Override
                protected E createEntry(C conn) {
                    return AbstractConnPool.this.createEntry(route, conn);
                }
            };
            this.routeToPool.put(route, pool);
        }
        return pool;
    }

    private static Exception operationAborted() {
        return new CancellationException("Operation aborted");
    }

    @Override
    public Future<E> lease(final T route, final Object state, final FutureCallback<E> callback) {
        Args.notNull(route, "Route");
        Asserts.check(!this.isShutDown, "Connection pool shut down");
        return new Future<E>(){
            private final AtomicBoolean cancelled = new AtomicBoolean(false);
            private final AtomicBoolean done = new AtomicBoolean(false);
            private final AtomicReference<E> entryRef = new AtomicReference<Object>(null);

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (this.done.compareAndSet(false, true)) {
                    this.cancelled.set(true);
                    AbstractConnPool.this.lock.lock();
                    try {
                        AbstractConnPool.this.condition.signalAll();
                    }
                    finally {
                        AbstractConnPool.this.lock.unlock();
                    }
                    if (callback != null) {
                        callback.cancelled();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean isCancelled() {
                return this.cancelled.get();
            }

            @Override
            public boolean isDone() {
                return this.done.get();
            }

            @Override
            public E get() throws InterruptedException, ExecutionException {
                try {
                    return this.get(0L, TimeUnit.MILLISECONDS);
                }
                catch (TimeoutException ex) {
                    throw new ExecutionException(ex);
                }
            }

            /*
             * Exception decompiling
             */
            @Override
            public E get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                /*
                 * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                 * 
                 * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[UNCONDITIONALDOLOOP]], but top level block is 1[TRYBLOCK]
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                 *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                 *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
                 *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
                 *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
                 *     at org.benf.cfr.reader.Main.main(Main.java:54)
                 */
                throw new IllegalStateException("Decompilation failed");
            }
        };
    }

    public Future<E> lease(T route, Object state) {
        return this.lease(route, state, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private E getPoolEntryBlocking(T route, Object state, long timeout, TimeUnit timeUnit, Future<E> future) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Date deadline = null;
        if (timeout > 0L) {
            deadline = new Date(System.currentTimeMillis() + timeUnit.toMillis(timeout));
        }
        this.lock.lock();
        try {
            boolean success;
            do {
                int totalUsed;
                int freeCapacity;
                E entry;
                Asserts.check(!this.isShutDown, "Connection pool shut down");
                if (future.isCancelled()) {
                    throw new ExecutionException(AbstractConnPool.operationAborted());
                }
                RouteSpecificPool<T, C, E> pool = this.getPool(route);
                while ((entry = pool.getFree(state)) != null) {
                    if (((PoolEntry)entry).isExpired(System.currentTimeMillis())) {
                        ((PoolEntry)entry).close();
                    }
                    if (!((PoolEntry)entry).isClosed()) break;
                    this.available.remove(entry);
                    pool.free(entry, false);
                }
                if (entry != null) {
                    this.available.remove(entry);
                    this.leased.add(entry);
                    this.onReuse(entry);
                    E e = entry;
                    return e;
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
                if (pool.getAllocatedCount() < maxPerRoute && (freeCapacity = Math.max(this.maxTotal - (totalUsed = this.leased.size()), 0)) > 0) {
                    int totalAvailable = this.available.size();
                    if (totalAvailable > freeCapacity - 1) {
                        PoolEntry lastUsed = (PoolEntry)this.available.removeLast();
                        lastUsed.close();
                        RouteSpecificPool otherpool = this.getPool(lastUsed.getRoute());
                        otherpool.remove(lastUsed);
                    }
                    C conn = this.connFactory.create(route);
                    entry = pool.add(conn);
                    this.leased.add(entry);
                    E e = entry;
                    return e;
                }
                success = false;
                try {
                    pool.queue(future);
                    this.pending.add(future);
                    if (deadline != null) {
                        success = this.condition.awaitUntil(deadline);
                    } else {
                        this.condition.await();
                        success = true;
                    }
                    if (future.isCancelled()) {
                        throw new ExecutionException(AbstractConnPool.operationAborted());
                    }
                }
                finally {
                    pool.unqueue(future);
                    this.pending.remove(future);
                }
            } while (success || deadline == null || deadline.getTime() > System.currentTimeMillis());
            throw new TimeoutException("Timeout waiting for connection");
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void release(E entry, boolean reusable) {
        this.lock.lock();
        try {
            if (this.leased.remove(entry)) {
                RouteSpecificPool pool = this.getPool(((PoolEntry)entry).getRoute());
                pool.free(entry, reusable);
                if (reusable && !this.isShutDown) {
                    this.available.addFirst(entry);
                } else {
                    ((PoolEntry)entry).close();
                }
                this.onRelease(entry);
                Future<E> future = pool.nextPending();
                if (future != null) {
                    this.pending.remove(future);
                } else {
                    future = this.pending.poll();
                }
                if (future != null) {
                    this.condition.signalAll();
                }
            }
        }
        finally {
            this.lock.unlock();
        }
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
        Args.positive(max, "Max per route value");
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
            PoolStats poolStats = new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), this.getMax(route));
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
        }
        finally {
            this.lock.unlock();
        }
    }

    private void purgePoolMap() {
        Iterator<Map.Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<T, RouteSpecificPool<T, C, E>> entry = it.next();
            RouteSpecificPool<T, C, E> pool = entry.getValue();
            if (pool.getPendingCount() + pool.getAllocatedCount() != 0) continue;
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

    public int getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    public void setValidateAfterInactivity(int ms) {
        this.validateAfterInactivity = ms;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        this.lock.lock();
        try {
            StringBuilder buffer = new StringBuilder();
            buffer.append("[leased: ");
            buffer.append(this.leased);
            buffer.append("][available: ");
            buffer.append(this.available);
            buffer.append("][pending: ");
            buffer.append(this.pending);
            buffer.append("]");
            String string = buffer.toString();
            return string;
        }
        finally {
            this.lock.unlock();
        }
    }

    static /* synthetic */ Exception access$200() {
        return AbstractConnPool.operationAborted();
    }

    static /* synthetic */ PoolEntry access$300(AbstractConnPool x0, Object x1, Object x2, long x3, TimeUnit x4, Future x5) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        return x0.getPoolEntryBlocking(x1, x2, x3, x4, x5);
    }

    static /* synthetic */ int access$400(AbstractConnPool x0) {
        return x0.validateAfterInactivity;
    }
}

