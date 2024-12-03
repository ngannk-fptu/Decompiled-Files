/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.thread;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.util.AtomicBiInteger;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.PrivilegedThreadFactory;
import org.eclipse.jetty.util.thread.ReservedThreadExecutor;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.ThreadPoolBudget;
import org.eclipse.jetty.util.thread.TryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="A thread pool")
public class QueuedThreadPool
extends ContainerLifeCycle
implements ThreadFactory,
ThreadPool.SizedThreadPool,
Dumpable,
TryExecutor,
VirtualThreads.Configurable {
    private static final Logger LOG = LoggerFactory.getLogger(QueuedThreadPool.class);
    private static final Runnable NOOP = () -> {};
    private final AtomicBiInteger _counts = new AtomicBiInteger(Integer.MIN_VALUE, 0);
    private final AtomicLong _evictThreshold = new AtomicLong();
    private final Set<Thread> _threads = ConcurrentHashMap.newKeySet();
    private final AutoLock.WithCondition _joinLock = new AutoLock.WithCondition();
    private final BlockingQueue<Runnable> _jobs;
    private final ThreadGroup _threadGroup;
    private final ThreadFactory _threadFactory;
    private String _name = "qtp" + this.hashCode();
    private int _idleTimeout;
    private int _maxThreads;
    private int _minThreads;
    private int _reservedThreads = -1;
    private TryExecutor _tryExecutor = TryExecutor.NO_TRY;
    private int _priority = 5;
    private boolean _daemon = false;
    private boolean _detailedDump = false;
    private int _lowThreadsThreshold = 1;
    private ThreadPoolBudget _budget;
    private long _stopTimeout;
    private Executor _virtualThreadsExecutor;
    private int _maxEvictCount = 1;
    private final Runnable _runnable = new Runner();

    public QueuedThreadPool() {
        this(200);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads) {
        this(maxThreads, Math.min(8, maxThreads));
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads) {
        this(maxThreads, minThreads, 60000);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads, @Name(value="queue") BlockingQueue<Runnable> queue) {
        this(maxThreads, minThreads, 60000, -1, queue, null);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads, @Name(value="idleTimeout") int idleTimeout) {
        this(maxThreads, minThreads, idleTimeout, null);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads, @Name(value="idleTimeout") int idleTimeout, @Name(value="queue") BlockingQueue<Runnable> queue) {
        this(maxThreads, minThreads, idleTimeout, queue, null);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads, @Name(value="idleTimeout") int idleTimeout, @Name(value="queue") BlockingQueue<Runnable> queue, @Name(value="threadGroup") ThreadGroup threadGroup) {
        this(maxThreads, minThreads, idleTimeout, -1, queue, threadGroup);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads, @Name(value="idleTimeout") int idleTimeout, @Name(value="reservedThreads") int reservedThreads, @Name(value="queue") BlockingQueue<Runnable> queue, @Name(value="threadGroup") ThreadGroup threadGroup) {
        this(maxThreads, minThreads, idleTimeout, reservedThreads, queue, threadGroup, null);
    }

    public QueuedThreadPool(@Name(value="maxThreads") int maxThreads, @Name(value="minThreads") int minThreads, @Name(value="idleTimeout") int idleTimeout, @Name(value="reservedThreads") int reservedThreads, @Name(value="queue") BlockingQueue<Runnable> queue, @Name(value="threadGroup") ThreadGroup threadGroup, @Name(value="threadFactory") ThreadFactory threadFactory) {
        if (maxThreads < minThreads) {
            throw new IllegalArgumentException("max threads (" + maxThreads + ") less than min threads (" + minThreads + ")");
        }
        this.setMinThreads(minThreads);
        this.setMaxThreads(maxThreads);
        this.setIdleTimeout(idleTimeout);
        this.setStopTimeout(5000L);
        this.setReservedThreads(reservedThreads);
        if (queue == null) {
            int capacity = Math.max(this._minThreads, 8) * 1024;
            queue = new BlockingArrayQueue<Runnable>(capacity, capacity);
        }
        this._jobs = queue;
        this._threadGroup = threadGroup;
        this.setThreadPoolBudget(new ThreadPoolBudget(this));
        this._threadFactory = threadFactory == null ? this : threadFactory;
    }

    @Override
    public ThreadPoolBudget getThreadPoolBudget() {
        return this._budget;
    }

    public void setThreadPoolBudget(ThreadPoolBudget budget) {
        if (budget != null && budget.getSizedThreadPool() != this) {
            throw new IllegalArgumentException();
        }
        this.updateBean(this._budget, budget);
        this._budget = budget;
    }

    public void setStopTimeout(long stopTimeout) {
        this._stopTimeout = stopTimeout;
    }

    public long getStopTimeout() {
        return this._stopTimeout;
    }

    @Override
    protected void doStart() throws Exception {
        if (this._reservedThreads == 0) {
            this._tryExecutor = NO_TRY;
        } else {
            ReservedThreadExecutor reserved = new ReservedThreadExecutor(this, this._reservedThreads);
            reserved.setIdleTimeout(this._idleTimeout, TimeUnit.MILLISECONDS);
            this._tryExecutor = reserved;
        }
        this.addBean(this._tryExecutor);
        this._evictThreshold.set(NanoTime.now());
        super.doStart();
        this._counts.set(0, 0);
        this.ensureThreads();
    }

    @Override
    protected void doStop() throws Exception {
        Runnable job;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Stopping {}", (Object)this);
        }
        super.doStop();
        this.removeBean(this._tryExecutor);
        this._tryExecutor = TryExecutor.NO_TRY;
        int threads = this._counts.getAndSetHi(Integer.MIN_VALUE);
        long timeout = this.getStopTimeout();
        BlockingQueue<Runnable> jobs = this.getQueue();
        if (timeout > 0L) {
            for (int i = 0; i < threads && jobs.offer(NOOP); ++i) {
            }
            this.joinThreads(NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L);
            for (Thread thread : this._threads) {
                if (thread == Thread.currentThread()) continue;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Interrupting {}", (Object)thread);
                }
                thread.interrupt();
            }
            this.joinThreads(NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L);
            Thread.yield();
            for (Thread unstopped : this._threads) {
                if (unstopped == Thread.currentThread()) continue;
                String stack = "";
                if (LOG.isDebugEnabled()) {
                    StringBuilder dmp = new StringBuilder();
                    for (StackTraceElement element : unstopped.getStackTrace()) {
                        dmp.append(System.lineSeparator()).append("\tat ").append(element);
                    }
                    stack = dmp.toString();
                }
                LOG.warn("Couldn't stop {}{}", (Object)unstopped, (Object)stack);
            }
        }
        while ((job = (Runnable)this._jobs.poll()) != null) {
            if (job instanceof Closeable) {
                try {
                    ((Closeable)((Object)job)).close();
                }
                catch (Throwable t) {
                    LOG.warn("Unable to close job: {}", (Object)job, (Object)t);
                }
                continue;
            }
            if (job == NOOP) continue;
            LOG.warn("Stopped without executing or closing {}", (Object)job);
        }
        if (this._budget != null) {
            this._budget.reset();
        }
        try (AutoLock.WithCondition l = this._joinLock.lock();){
            l.signalAll();
        }
    }

    private void joinThreads(long stopByNanos) {
        block2: while (true) {
            for (Thread thread : this._threads) {
                if (thread == Thread.currentThread()) continue;
                long canWait = NanoTime.millisUntil(stopByNanos);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Waiting for {} for {}", (Object)thread, (Object)canWait);
                }
                if (canWait <= 0L) {
                    return;
                }
                try {
                    thread.join(canWait);
                }
                catch (InterruptedException e) {
                    continue block2;
                }
            }
            break;
        }
    }

    @ManagedAttribute(value="maximum time a thread may be idle in ms")
    public int getIdleTimeout() {
        return this._idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this._idleTimeout = idleTimeout;
        ReservedThreadExecutor reserved = this.getBean(ReservedThreadExecutor.class);
        if (reserved != null) {
            reserved.setIdleTimeout(idleTimeout, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    @ManagedAttribute(value="maximum number of threads in the pool")
    public int getMaxThreads() {
        return this._maxThreads;
    }

    @Override
    public void setMaxThreads(int maxThreads) {
        if (this._budget != null) {
            this._budget.check(maxThreads);
        }
        this._maxThreads = maxThreads;
        if (this._minThreads > this._maxThreads) {
            this._minThreads = this._maxThreads;
        }
    }

    @Override
    @ManagedAttribute(value="minimum number of threads in the pool")
    public int getMinThreads() {
        return this._minThreads;
    }

    @Override
    public void setMinThreads(int minThreads) {
        this._minThreads = minThreads;
        if (this._minThreads > this._maxThreads) {
            this._maxThreads = this._minThreads;
        }
        if (this.isStarted()) {
            this.ensureThreads();
        }
    }

    @ManagedAttribute(value="number of configured reserved threads or -1 for heuristic")
    public int getReservedThreads() {
        return this._reservedThreads;
    }

    public void setReservedThreads(int reservedThreads) {
        if (this.isRunning()) {
            throw new IllegalStateException(this.getState());
        }
        this._reservedThreads = reservedThreads;
    }

    @ManagedAttribute(value="name of the thread pool")
    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        if (this.isRunning()) {
            throw new IllegalStateException(this.getState());
        }
        this._name = name;
    }

    @ManagedAttribute(value="priority of threads in the pool")
    public int getThreadsPriority() {
        return this._priority;
    }

    public void setThreadsPriority(int priority) {
        this._priority = priority;
    }

    @ManagedAttribute(value="thread pool uses daemon threads")
    public boolean isDaemon() {
        return this._daemon;
    }

    public void setDaemon(boolean daemon) {
        this._daemon = daemon;
    }

    @ManagedAttribute(value="reports additional details in the dump")
    public boolean isDetailedDump() {
        return this._detailedDump;
    }

    public void setDetailedDump(boolean detailedDump) {
        this._detailedDump = detailedDump;
    }

    @ManagedAttribute(value="threshold at which the pool is low on threads")
    public int getLowThreadsThreshold() {
        return this._lowThreadsThreshold;
    }

    public void setLowThreadsThreshold(int lowThreadsThreshold) {
        this._lowThreadsThreshold = lowThreadsThreshold;
    }

    @Override
    public Executor getVirtualThreadsExecutor() {
        return this._virtualThreadsExecutor;
    }

    @Override
    public void setVirtualThreadsExecutor(Executor executor) {
        try {
            VirtualThreads.Configurable.super.setVirtualThreadsExecutor(executor);
            this._virtualThreadsExecutor = executor;
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
    }

    public void setMaxEvictCount(int evictCount) {
        if (evictCount < 1) {
            throw new IllegalArgumentException("Invalid evict count " + evictCount);
        }
        this._maxEvictCount = evictCount;
    }

    @ManagedAttribute(value="maximum number of idle threads to evict in one idle timeout period")
    public int getMaxEvictCount() {
        return this._maxEvictCount;
    }

    @ManagedAttribute(value="size of the job queue")
    public int getQueueSize() {
        int idle = this._counts.getLo();
        return Math.max(0, -idle);
    }

    @ManagedAttribute(value="maximum number (capacity) of reserved threads")
    public int getMaxReservedThreads() {
        TryExecutor tryExecutor = this._tryExecutor;
        if (tryExecutor instanceof ReservedThreadExecutor) {
            ReservedThreadExecutor reservedThreadExecutor = (ReservedThreadExecutor)tryExecutor;
            return reservedThreadExecutor.getCapacity();
        }
        return 0;
    }

    @ManagedAttribute(value="number of available reserved threads")
    public int getAvailableReservedThreads() {
        TryExecutor tryExecutor = this._tryExecutor;
        if (tryExecutor instanceof ReservedThreadExecutor) {
            ReservedThreadExecutor reservedThreadExecutor = (ReservedThreadExecutor)tryExecutor;
            return reservedThreadExecutor.getAvailable();
        }
        return 0;
    }

    @Override
    @ManagedAttribute(value="number of threads in the pool")
    public int getThreads() {
        int threads = this._counts.getHi();
        return Math.max(0, threads);
    }

    @ManagedAttribute(value="number of threads ready to execute transient jobs")
    public int getReadyThreads() {
        return this.getIdleThreads() + this.getAvailableReservedThreads();
    }

    @ManagedAttribute(value="number of threads used by internal components")
    public int getLeasedThreads() {
        return this.getMaxLeasedThreads() - this.getMaxReservedThreads();
    }

    @ManagedAttribute(value="maximum number of threads leased to internal components")
    public int getMaxLeasedThreads() {
        ThreadPoolBudget budget = this._budget;
        return budget == null ? 0 : budget.getLeasedThreads();
    }

    @Override
    @ManagedAttribute(value="number of idle threads but not reserved")
    public int getIdleThreads() {
        int idle = this._counts.getLo();
        return Math.max(0, idle);
    }

    @ManagedAttribute(value="number of threads executing internal and transient jobs")
    public int getBusyThreads() {
        return this.getThreads() - this.getReadyThreads();
    }

    @ManagedAttribute(value="number of threads executing transient jobs")
    public int getUtilizedThreads() {
        return this.getThreads() - this.getLeasedThreads() - this.getReadyThreads();
    }

    @ManagedAttribute(value="maximum number of threads available to run transient jobs")
    public int getMaxAvailableThreads() {
        return this.getMaxThreads() - this.getLeasedThreads();
    }

    @ManagedAttribute(value="utilization rate of threads executing transient jobs")
    public double getUtilizationRate() {
        return (double)this.getUtilizedThreads() / (double)this.getMaxAvailableThreads();
    }

    @Override
    @ManagedAttribute(value="thread pool is low on threads", readonly=true)
    public boolean isLowOnThreads() {
        return this.getMaxThreads() - this.getThreads() + this.getReadyThreads() - this.getQueueSize() <= this.getLowThreadsThreshold();
    }

    @Override
    public void execute(Runnable job) {
        int idle;
        int startThread;
        int threads;
        long counts;
        do {
            if ((threads = AtomicBiInteger.getHi(counts = this._counts.get())) != Integer.MIN_VALUE) continue;
            throw new RejectedExecutionException(job.toString());
        } while (!this._counts.compareAndSet(counts, threads + (startThread = (idle = AtomicBiInteger.getLo(counts)) <= 0 && threads < this._maxThreads ? 1 : 0), idle + startThread - 1));
        if (!this._jobs.offer(job)) {
            if (this.addCounts(-startThread, 1 - startThread)) {
                LOG.warn("{} rejected {}", (Object)this, (Object)job);
            }
            throw new RejectedExecutionException(job.toString());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("queue {} startThread={}", (Object)job, (Object)startThread);
        }
        while (startThread-- > 0) {
            this.startThread();
        }
    }

    @Override
    public boolean tryExecute(Runnable task) {
        TryExecutor tryExecutor = this._tryExecutor;
        return tryExecutor != null && tryExecutor.tryExecute(task);
    }

    @Override
    public void join() throws InterruptedException {
        try (AutoLock.WithCondition l = this._joinLock.lock();){
            while (this.isRunning()) {
                l.await();
            }
        }
        while (this.isStopping()) {
            Thread.sleep(1L);
        }
    }

    private void ensureThreads() {
        long counts;
        int threads;
        while ((threads = AtomicBiInteger.getHi(counts = this._counts.get())) != Integer.MIN_VALUE) {
            int idle = AtomicBiInteger.getLo(counts);
            if (threads >= this._minThreads && (idle >= 0 || threads >= this._maxThreads)) break;
            if (!this._counts.compareAndSet(counts, threads + 1, idle + 1)) continue;
            this.startThread();
        }
    }

    protected void startThread() {
        boolean started = false;
        try {
            Thread thread = this._threadFactory.newThread(this._runnable);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Starting {}", (Object)thread);
            }
            this._threads.add(thread);
            this._evictThreshold.set(NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(this._idleTimeout));
            thread.start();
            started = true;
        }
        finally {
            if (!started) {
                this.addCounts(-1, -1);
            }
        }
    }

    private boolean addCounts(int deltaThreads, int deltaIdle) {
        while (true) {
            long update;
            long encoded = this._counts.get();
            int threads = AtomicBiInteger.getHi(encoded);
            int idle = AtomicBiInteger.getLo(encoded);
            if (threads == Integer.MIN_VALUE) {
                update = AtomicBiInteger.encode(threads, idle + deltaIdle);
                if (!this._counts.compareAndSet(encoded, update)) continue;
                return false;
            }
            update = AtomicBiInteger.encode(threads + deltaThreads, idle + deltaIdle);
            if (this._counts.compareAndSet(encoded, update)) break;
        }
        return true;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return PrivilegedThreadFactory.newThread(() -> {
            Thread thread = new Thread(this._threadGroup, runnable);
            thread.setDaemon(this.isDaemon());
            thread.setPriority(this.getThreadsPriority());
            thread.setName(this._name + "-" + thread.getId());
            thread.setContextClassLoader(this.getClass().getClassLoader());
            return thread;
        });
    }

    protected void removeThread(Thread thread) {
        this._threads.remove(thread);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        ArrayList<Object> threads = new ArrayList<Object>(this.getMaxThreads());
        for (Thread thread : this._threads) {
            StackTraceElement[] trace = thread.getStackTrace();
            String stackTag = this.getCompressedStackTag(trace);
            String baseThreadInfo = String.format("%s %s tid=%d prio=%d", new Object[]{thread.getName(), thread.getState(), thread.getId(), thread.getPriority()});
            if (!StringUtil.isBlank(stackTag)) {
                threads.add(baseThreadInfo + " " + stackTag);
                continue;
            }
            if (this.isDetailedDump()) {
                threads.add((o, i) -> Dumpable.dumpObjects(o, i, baseThreadInfo, trace));
                continue;
            }
            threads.add(baseThreadInfo + " @ " + (trace.length > 0 ? trace[0].toString() : "???"));
        }
        DumpableCollection threadsDump = new DumpableCollection("threads", threads);
        if (this.isDetailedDump()) {
            this.dumpObjects(out, indent, threadsDump, new DumpableCollection("jobs", new ArrayList<Runnable>(this.getQueue())));
        } else {
            this.dumpObjects(out, indent, threadsDump);
        }
    }

    private String getCompressedStackTag(StackTraceElement[] trace) {
        for (StackTraceElement t : trace) {
            if ("idleJobPoll".equals(t.getMethodName()) && t.getClassName().equals(Runner.class.getName())) {
                return "IDLE";
            }
            if ("reservedWait".equals(t.getMethodName()) && t.getClassName().endsWith("ReservedThread")) {
                return "RESERVED";
            }
            if ("select".equals(t.getMethodName()) && t.getClassName().endsWith("SelectorProducer")) {
                return "SELECTING";
            }
            if (!"accept".equals(t.getMethodName()) || !t.getClassName().contains("ServerConnector")) continue;
            return "ACCEPTING";
        }
        return "";
    }

    protected void runJob(Runnable job) {
        job.run();
    }

    protected boolean evict() {
        long idleTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(this.getIdleTimeout());
        int threads = this.getThreads();
        int minThreads = this.getMinThreads();
        for (int threadsToEvict = threads - minThreads; threadsToEvict > 0; --threadsToEvict) {
            long evictThreshold;
            long threshold;
            long now = NanoTime.now();
            long evictPeriod = idleTimeoutNanos / (long)this.getMaxEvictCount();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Evict check, period={}ms {}", (Object)TimeUnit.NANOSECONDS.toMillis(evictPeriod), (Object)this);
            }
            if (NanoTime.elapsed(threshold = (evictThreshold = this._evictThreshold.get()), now) > idleTimeoutNanos) {
                threshold = now - idleTimeoutNanos;
            }
            if (NanoTime.isBefore(now, threshold += evictPeriod)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Evict skipped, threshold={}ms in the future {}", (Object)NanoTime.millisElapsed(now, threshold), (Object)this);
                }
                return false;
            }
            if (!this._evictThreshold.compareAndSet(evictThreshold, threshold)) continue;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Evicted, threshold={}ms in the past {}", (Object)NanoTime.millisElapsed(threshold, now), (Object)this);
            }
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Evict skipped, no excess threads {}", (Object)this);
        }
        return false;
    }

    protected BlockingQueue<Runnable> getQueue() {
        return this._jobs;
    }

    @ManagedOperation(value="interrupts a pool thread")
    public boolean interruptThread(@Name(value="id") long id) {
        for (Thread thread : this._threads) {
            if (thread.getId() != id) continue;
            thread.interrupt();
            return true;
        }
        return false;
    }

    @ManagedOperation(value="dumps a pool thread stack")
    public String dumpThread(@Name(value="id") long id) {
        for (Thread thread : this._threads) {
            if (thread.getId() != id) continue;
            StringBuilder buf = new StringBuilder();
            buf.append(thread.getId()).append(" ").append(thread.getName()).append(" ");
            buf.append((Object)thread.getState()).append(":").append(System.lineSeparator());
            for (StackTraceElement element : thread.getStackTrace()) {
                buf.append("  at ").append(element.toString()).append(System.lineSeparator());
            }
            return buf.toString();
        }
        return null;
    }

    @Override
    public String toString() {
        long count = this._counts.get();
        int threads = Math.max(0, AtomicBiInteger.getHi(count));
        int idle = Math.max(0, AtomicBiInteger.getLo(count));
        int queue = this.getQueueSize();
        return String.format("%s[%s]@%x{%s,%d<=%d<=%d,i=%d,r=%d,t=%dms,q=%d}[%s]", this.getClass().getSimpleName(), this._name, this.hashCode(), this.getState(), this.getMinThreads(), threads, this.getMaxThreads(), idle, this.getReservedThreads(), NanoTime.millisUntil(this._evictThreshold.get()), queue, this._tryExecutor);
    }

    private class Runner
    implements Runnable {
        private Runner() {
        }

        private Runnable idleJobPoll(long idleTimeoutNanos) throws InterruptedException {
            if (idleTimeoutNanos <= 0L) {
                return QueuedThreadPool.this._jobs.take();
            }
            return QueuedThreadPool.this._jobs.poll(idleTimeoutNanos, TimeUnit.NANOSECONDS);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Runner started for {}", (Object)QueuedThreadPool.this);
            }
            boolean idle = true;
            try {
                while (QueuedThreadPool.this._counts.getHi() != Integer.MIN_VALUE) {
                    try {
                        long idleTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(QueuedThreadPool.this.getIdleTimeout());
                        Runnable job = this.idleJobPoll(idleTimeoutNanos);
                        while (job != null) {
                            idle = false;
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("run {} in {}", (Object)job, (Object)QueuedThreadPool.this);
                            }
                            this.doRunJob(job);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("ran {} in {}", (Object)job, (Object)QueuedThreadPool.this);
                            }
                            if (!QueuedThreadPool.this.addCounts(0, 1)) break;
                            idle = true;
                            job = (Runnable)QueuedThreadPool.this._jobs.poll();
                        }
                        if (!QueuedThreadPool.this.evict()) continue;
                        break;
                    }
                    catch (InterruptedException e) {
                        LOG.trace("IGNORED", (Throwable)e);
                    }
                }
            }
            catch (Throwable throwable) {
                Thread thread = Thread.currentThread();
                QueuedThreadPool.this.removeThread(thread);
                QueuedThreadPool.this.addCounts(-1, idle ? -1 : 0);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} exited for {}", (Object)thread, (Object)QueuedThreadPool.this);
                }
                QueuedThreadPool.this.ensureThreads();
                throw throwable;
            }
            Thread thread = Thread.currentThread();
            QueuedThreadPool.this.removeThread(thread);
            QueuedThreadPool.this.addCounts(-1, idle ? -1 : 0);
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} exited for {}", (Object)thread, (Object)QueuedThreadPool.this);
            }
            QueuedThreadPool.this.ensureThreads();
        }

        private void doRunJob(Runnable job) {
            try {
                QueuedThreadPool.this.runJob(job);
            }
            catch (Throwable e) {
                LOG.warn("Job failed", e);
            }
            finally {
                Thread.interrupted();
            }
        }
    }
}

