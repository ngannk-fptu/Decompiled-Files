/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.thread;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.AtomicBiInteger;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.ThreadPoolBudget;
import org.eclipse.jetty.util.thread.TryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="A pool for reserved threads")
public class ReservedThreadExecutor
extends AbstractLifeCycle
implements TryExecutor,
Dumpable {
    private static final Logger LOG = LoggerFactory.getLogger(ReservedThreadExecutor.class);
    private static final long DEFAULT_IDLE_TIMEOUT = TimeUnit.MINUTES.toNanos(1L);
    private static final Runnable STOP = new Runnable(){

        @Override
        public void run() {
        }

        public String toString() {
            return "STOP";
        }
    };
    private final Executor _executor;
    private final int _capacity;
    private final Set<ReservedThread> _threads = ConcurrentHashMap.newKeySet();
    private final SynchronousQueue<Runnable> _queue = new SynchronousQueue(false);
    private final AtomicBiInteger _count = new AtomicBiInteger();
    private final AtomicLong _lastEmptyNanoTime = new AtomicLong(NanoTime.now());
    private ThreadPoolBudget.Lease _lease;
    private long _idleTimeNanos = DEFAULT_IDLE_TIMEOUT;

    public ReservedThreadExecutor(Executor executor, int capacity) {
        this._executor = executor;
        this._capacity = ReservedThreadExecutor.reservedThreads(executor, capacity);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}", (Object)this);
        }
    }

    private static int reservedThreads(Executor executor, int capacity) {
        if (capacity >= 0) {
            return capacity;
        }
        if (VirtualThreads.isUseVirtualThreads(executor)) {
            return 0;
        }
        int cpus = ProcessorUtils.availableProcessors();
        if (executor instanceof ThreadPool.SizedThreadPool) {
            int threads = ((ThreadPool.SizedThreadPool)executor).getMaxThreads();
            return Math.max(1, Math.min(cpus, threads / 10));
        }
        return cpus;
    }

    public Executor getExecutor() {
        return this._executor;
    }

    @ManagedAttribute(value="max number of reserved threads", readonly=true)
    public int getCapacity() {
        return this._capacity;
    }

    @ManagedAttribute(value="available reserved threads", readonly=true)
    public int getAvailable() {
        return this._count.getLo();
    }

    @ManagedAttribute(value="pending reserved threads", readonly=true)
    public int getPending() {
        return this._count.getHi();
    }

    @ManagedAttribute(value="idle timeout in ms", readonly=true)
    public long getIdleTimeoutMs() {
        return TimeUnit.NANOSECONDS.toMillis(this._idleTimeNanos);
    }

    public void setIdleTimeout(long idleTime, TimeUnit idleTimeUnit) {
        if (this.isRunning()) {
            throw new IllegalStateException();
        }
        this._idleTimeNanos = idleTime <= 0L || idleTimeUnit == null ? DEFAULT_IDLE_TIMEOUT : idleTimeUnit.toNanos(idleTime);
    }

    @Override
    public void doStart() throws Exception {
        this._lease = ThreadPoolBudget.leaseFrom(this.getExecutor(), this, this._capacity);
        this._count.set(0, 0);
        super.doStart();
    }

    @Override
    public void doStop() throws Exception {
        if (this._lease != null) {
            this._lease.close();
        }
        super.doStop();
        int size = this._count.getAndSetLo(-1);
        for (int i = 0; i < size; ++i) {
            Thread.yield();
            this._queue.offer(STOP);
        }
        this._threads.stream().filter(rec$ -> ((ReservedThread)rec$).isReserved()).map(t -> t._thread).filter(Objects::nonNull).forEach(Thread::interrupt);
        this._threads.clear();
        this._count.getAndSetHi(0);
    }

    @Override
    public void execute(Runnable task) throws RejectedExecutionException {
        this._executor.execute(task);
    }

    @Override
    public boolean tryExecute(Runnable task) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} tryExecute {}", (Object)this, (Object)task);
        }
        if (task == null) {
            return false;
        }
        boolean offered = this._queue.offer(task);
        int size = this._count.getLo();
        while (offered && size > 0 && !this._count.compareAndSetLo(size--, size)) {
            size = this._count.getLo();
        }
        if (size == 0 && task != STOP) {
            this.startReservedThread();
        }
        return offered;
    }

    private void startReservedThread() {
        block5: {
            int size;
            int pending;
            long count;
            do {
                count = this._count.get();
                pending = AtomicBiInteger.getHi(count);
                size = AtomicBiInteger.getLo(count);
                if (size < 0 || pending + size >= this._capacity) {
                    return;
                }
                if (size != 0) continue;
                this._lastEmptyNanoTime.set(NanoTime.now());
            } while (!this._count.compareAndSet(count, pending + 1, size));
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} startReservedThread p={}", (Object)this, (Object)(pending + 1));
            }
            try {
                ReservedThread thread = new ReservedThread();
                this._threads.add(thread);
                this._executor.execute(thread);
            }
            catch (Throwable e) {
                this._count.add(-1, 0);
                if (!LOG.isDebugEnabled()) break block5;
                LOG.debug("ignored", e);
            }
        }
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObjects(out, indent, this, new DumpableCollection("threads", this._threads.stream().filter(rec$ -> ((ReservedThread)rec$).isReserved()).collect(Collectors.toList())));
    }

    @Override
    public String toString() {
        return String.format("%s@%x{reserved=%d/%d,pending=%d}", this.getClass().getSimpleName(), this.hashCode(), this._count.getLo(), this._capacity, this._count.getHi());
    }

    private class ReservedThread
    implements Runnable {
        private volatile State _state = State.PENDING;
        private volatile Thread _thread;

        private ReservedThread() {
        }

        private boolean isReserved() {
            return this._state == State.RESERVED;
        }

        private Runnable reservedWait() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} waiting {}", (Object)this, (Object)ReservedThreadExecutor.this);
            }
            while (ReservedThreadExecutor.this._count.getLo() >= 0) {
                try {
                    Runnable task = ReservedThreadExecutor.this._queue.poll(ReservedThreadExecutor.this._idleTimeNanos, TimeUnit.NANOSECONDS);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} task={} {}", new Object[]{this, task, ReservedThreadExecutor.this});
                    }
                    if (task != null) {
                        return task;
                    }
                    int size = ReservedThreadExecutor.this._count.getLo();
                    while (size > 0 && !ReservedThreadExecutor.this._count.compareAndSetLo(size--, size)) {
                        size = ReservedThreadExecutor.this._count.getLo();
                    }
                    this._state = size >= 0 ? State.IDLE : State.STOPPED;
                    return STOP;
                }
                catch (InterruptedException e) {
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug("ignored", (Throwable)e);
                }
            }
            this._state = State.STOPPED;
            return STOP;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         */
        @Override
        public void run() {
            block17: {
                this._thread = Thread.currentThread();
                while (true) lbl-1000:
                // 5 sources

                {
                    count = ReservedThreadExecutor.this._count.get();
                    pending = AtomicBiInteger.getHi(count) - (this._state == State.PENDING ? 1 : 0);
                    size = AtomicBiInteger.getLo(count);
                    if (size < 0 || size >= ReservedThreadExecutor.this._capacity) {
                        next = State.STOPPED;
                    } else {
                        now = NanoTime.now();
                        lastEmpty = ReservedThreadExecutor.this._lastEmptyNanoTime.get();
                        if (size > 0 && ReservedThreadExecutor.this._idleTimeNanos < NanoTime.elapsed(lastEmpty, now) && ReservedThreadExecutor.this._lastEmptyNanoTime.compareAndSet(lastEmpty, now)) {
                            next = State.IDLE;
                        } else {
                            next = State.RESERVED;
                            ++size;
                        }
                    }
                    if (!ReservedThreadExecutor.this._count.compareAndSet(count, pending, size)) continue;
                    if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
                        ReservedThreadExecutor.LOG.debug("{} was={} next={} size={}+{} capacity={}", new Object[]{this, this._state, next, pending, size, ReservedThreadExecutor.this._capacity});
                    }
                    this._state = next;
                    if (next != State.RESERVED) {
                        break block17;
                    }
                    task = this.reservedWait();
                    if (task == ReservedThreadExecutor.STOP) {
                        break block17;
                    }
                    try {
                        this._state = State.RUNNING;
                        task.run();
                    }
                    catch (Throwable e) {
                        ReservedThreadExecutor.LOG.warn("Unable to run task", e);
                    }
                    finally {
                        Thread.interrupted();
                        continue;
                    }
                    break;
                }
                ** GOTO lbl-1000
                finally {
                    if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
                        ReservedThreadExecutor.LOG.debug("{} exited {}", (Object)this, (Object)ReservedThreadExecutor.this);
                    }
                    ReservedThreadExecutor.this._threads.remove(this);
                    this._thread = null;
                }
            }
        }

        public String toString() {
            return String.format("%s@%x{%s,thread=%s}", new Object[]{this.getClass().getSimpleName(), this.hashCode(), this._state, this._thread});
        }
    }

    private static enum State {
        PENDING,
        RESERVED,
        RUNNING,
        IDLE,
        STOPPED;

    }
}

