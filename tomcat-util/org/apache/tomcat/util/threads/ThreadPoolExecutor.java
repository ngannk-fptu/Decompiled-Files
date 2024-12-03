/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.StopPooledThreadException;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThread;

public class ThreadPoolExecutor
extends AbstractExecutorService {
    protected static final StringManager sm = StringManager.getManager(ThreadPoolExecutor.class);
    private final AtomicInteger ctl = new AtomicInteger(ThreadPoolExecutor.ctlOf(-536870912, 0));
    private static final int COUNT_BITS = 29;
    private static final int COUNT_MASK = 0x1FFFFFFF;
    private static final int RUNNING = -536870912;
    private static final int SHUTDOWN = 0;
    private static final int STOP = 0x20000000;
    private static final int TIDYING = 0x40000000;
    private static final int TERMINATED = 0x60000000;
    private final BlockingQueue<Runnable> workQueue;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final HashSet<Worker> workers = new HashSet();
    private final Condition termination = this.mainLock.newCondition();
    private int largestPoolSize;
    private long completedTaskCount;
    private final AtomicInteger submittedCount = new AtomicInteger(0);
    private final AtomicLong lastContextStoppedTime = new AtomicLong(0L);
    private final AtomicLong lastTimeThreadKilledItself = new AtomicLong(0L);
    private volatile long threadRenewalDelay = 1000L;
    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut;
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private static final RejectedExecutionHandler defaultHandler = new RejectPolicy();
    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");
    private static final boolean ONLY_ONE = true;

    private static int workerCountOf(int c) {
        return c & 0x1FFFFFFF;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < 0;
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return this.ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return this.ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        this.ctl.addAndGet(-1);
    }

    private void advanceRunState(int targetState) {
        int c;
        while (!ThreadPoolExecutor.runStateAtLeast(c = this.ctl.get(), targetState) && !this.ctl.compareAndSet(c, ThreadPoolExecutor.ctlOf(targetState, ThreadPoolExecutor.workerCountOf(c)))) {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void tryTerminate() {
        int c;
        while (!(ThreadPoolExecutor.isRunning(c = this.ctl.get()) || ThreadPoolExecutor.runStateAtLeast(c, 0x40000000) || ThreadPoolExecutor.runStateLessThan(c, 0x20000000) && !this.workQueue.isEmpty())) {
            if (ThreadPoolExecutor.workerCountOf(c) != 0) {
                this.interruptIdleWorkers(true);
                return;
            }
            ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (!this.ctl.compareAndSet(c, ThreadPoolExecutor.ctlOf(0x40000000, 0))) continue;
                try {
                    this.terminated();
                }
                finally {
                    this.ctl.set(ThreadPoolExecutor.ctlOf(0x60000000, 0));
                    this.termination.signalAll();
                }
                return;
            }
            finally {
                mainLock.unlock();
                continue;
            }
            break;
        }
        return;
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            for (Worker w : this.workers) {
                security.checkAccess(w.thread);
            }
        }
    }

    private void interruptWorkers() {
        for (Worker w : this.workers) {
            w.interruptIfStarted();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void interruptIdleWorkers(boolean onlyOne) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : this.workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    }
                    catch (SecurityException securityException) {
                    }
                    finally {
                        w.unlock();
                    }
                }
                if (!onlyOne) continue;
                break;
            }
        }
        finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers() {
        this.interruptIdleWorkers(false);
    }

    final void reject(Runnable command) {
        this.handler.rejectedExecution(command, this);
    }

    void onShutdown() {
    }

    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = this.workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (!q.remove(r)) continue;
                taskList.add(r);
            }
        }
        return taskList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        boolean workerStarted;
        block15: {
            int c = this.ctl.get();
            block6: while (true) {
                if (ThreadPoolExecutor.runStateAtLeast(c, 0) && (ThreadPoolExecutor.runStateAtLeast(c, 0x20000000) || firstTask != null || this.workQueue.isEmpty())) {
                    return false;
                }
                do {
                    if (ThreadPoolExecutor.workerCountOf(c) >= ((core ? this.corePoolSize : this.maximumPoolSize) & 0x1FFFFFFF)) {
                        return false;
                    }
                    if (this.compareAndIncrementWorkerCount(c)) break block6;
                } while (!ThreadPoolExecutor.runStateAtLeast(c = this.ctl.get(), 0));
            }
            workerStarted = false;
            boolean workerAdded = false;
            Worker w = null;
            try {
                w = new Worker(firstTask);
                Thread t = w.thread;
                if (t == null) break block15;
                ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    int c2 = this.ctl.get();
                    if (ThreadPoolExecutor.isRunning(c2) || ThreadPoolExecutor.runStateLessThan(c2, 0x20000000) && firstTask == null) {
                        if (t.getState() != Thread.State.NEW) {
                            throw new IllegalThreadStateException();
                        }
                        this.workers.add(w);
                        workerAdded = true;
                        int s = this.workers.size();
                        if (s > this.largestPoolSize) {
                            this.largestPoolSize = s;
                        }
                    }
                }
                finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
            finally {
                if (!workerStarted) {
                    this.addWorkerFailed(w);
                }
            }
        }
        return workerStarted;
    }

    private void addWorkerFailed(Worker w) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null) {
                this.workers.remove(w);
            }
            this.decrementWorkerCount();
            this.tryTerminate();
        }
        finally {
            mainLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) {
            this.decrementWorkerCount();
        }
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.completedTaskCount += w.completedTasks;
            this.workers.remove(w);
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
        int c = this.ctl.get();
        if (ThreadPoolExecutor.runStateLessThan(c, 0x20000000)) {
            if (!completedAbruptly) {
                int min;
                int n = min = this.allowCoreThreadTimeOut ? 0 : this.corePoolSize;
                if (min == 0 && !this.workQueue.isEmpty()) {
                    min = 1;
                }
                if (ThreadPoolExecutor.workerCountOf(c) >= min && this.workQueue.isEmpty()) {
                    return;
                }
            }
            this.addWorker(null, false);
        }
    }

    private Runnable getTask() {
        boolean timedOut = false;
        while (true) {
            boolean timed;
            int c;
            if (ThreadPoolExecutor.runStateAtLeast(c = this.ctl.get(), 0) && (ThreadPoolExecutor.runStateAtLeast(c, 0x20000000) || this.workQueue.isEmpty())) {
                this.decrementWorkerCount();
                return null;
            }
            int wc = ThreadPoolExecutor.workerCountOf(c);
            boolean bl = timed = this.allowCoreThreadTimeOut || wc > this.corePoolSize;
            if ((wc > this.maximumPoolSize || timed && timedOut) && (wc > 1 || this.workQueue.isEmpty())) {
                if (!this.compareAndDecrementWorkerCount(c)) continue;
                return null;
            }
            try {
                Runnable r;
                Runnable runnable = r = timed ? this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS) : this.workQueue.take();
                if (r != null) {
                    return r;
                }
                timedOut = true;
                continue;
            }
            catch (InterruptedException retry) {
                timedOut = false;
                continue;
            }
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = this.getTask()) != null) {
                w.lock();
                if ((ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0x20000000) || Thread.interrupted() && ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0x20000000)) && !wt.isInterrupted()) {
                    wt.interrupt();
                }
                try {
                    this.beforeExecute(wt, task);
                    try {
                        task.run();
                        this.afterExecute(task, null);
                    }
                    catch (Throwable ex) {
                        this.afterExecute(task, ex);
                        throw ex;
                    }
                }
                finally {
                    task = null;
                    ++w.completedTasks;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        }
        finally {
            this.processWorkerExit(w, completedAbruptly);
        }
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultHandler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0L) {
            throw new IllegalArgumentException();
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
        this.prestartAllCoreThreads();
    }

    @Override
    public void execute(Runnable command) {
        this.execute(command, 0L, TimeUnit.MILLISECONDS);
    }

    @Deprecated
    public void execute(Runnable command, long timeout, TimeUnit unit) {
        this.submittedCount.incrementAndGet();
        try {
            this.executeInternal(command);
        }
        catch (RejectedExecutionException rx) {
            if (this.getQueue() instanceof TaskQueue) {
                TaskQueue queue = (TaskQueue)this.getQueue();
                try {
                    if (!queue.force(command, timeout, unit)) {
                        this.submittedCount.decrementAndGet();
                        throw new RejectedExecutionException(sm.getString("threadPoolExecutor.queueFull"));
                    }
                }
                catch (InterruptedException x) {
                    this.submittedCount.decrementAndGet();
                    throw new RejectedExecutionException(x);
                }
            }
            this.submittedCount.decrementAndGet();
            throw rx;
        }
    }

    private void executeInternal(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        int c = this.ctl.get();
        if (ThreadPoolExecutor.workerCountOf(c) < this.corePoolSize) {
            if (this.addWorker(command, true)) {
                return;
            }
            c = this.ctl.get();
        }
        if (ThreadPoolExecutor.isRunning(c) && this.workQueue.offer(command)) {
            int recheck = this.ctl.get();
            if (!ThreadPoolExecutor.isRunning(recheck) && this.remove(command)) {
                this.reject(command);
            } else if (ThreadPoolExecutor.workerCountOf(recheck) == 0) {
                this.addWorker(null, false);
            }
        } else if (!this.addWorker(command, false)) {
            this.reject(command);
        }
    }

    @Override
    public void shutdown() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.checkShutdownAccess();
            this.advanceRunState(0);
            this.interruptIdleWorkers();
            this.onShutdown();
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.checkShutdownAccess();
            this.advanceRunState(0x20000000);
            this.interruptWorkers();
            tasks = this.drainQueue();
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0);
    }

    boolean isStopped() {
        return ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0x20000000);
    }

    public boolean isTerminating() {
        int c = this.ctl.get();
        return ThreadPoolExecutor.runStateAtLeast(c, 0) && ThreadPoolExecutor.runStateLessThan(c, 0x60000000);
    }

    @Override
    public boolean isTerminated() {
        return ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0x60000000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            while (ThreadPoolExecutor.runStateLessThan(this.ctl.get(), 0x60000000)) {
                if (nanos <= 0L) {
                    boolean bl = false;
                    return bl;
                }
                nanos = this.termination.awaitNanos(nanos);
            }
            boolean bl = true;
            return bl;
        }
        finally {
            mainLock.unlock();
        }
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }

    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.handler;
    }

    public void setCorePoolSize(int corePoolSize) {
        block3: {
            int delta;
            block2: {
                if (corePoolSize < 0 || this.maximumPoolSize < corePoolSize) {
                    throw new IllegalArgumentException();
                }
                delta = corePoolSize - this.corePoolSize;
                this.corePoolSize = corePoolSize;
                if (ThreadPoolExecutor.workerCountOf(this.ctl.get()) <= corePoolSize) break block2;
                this.interruptIdleWorkers();
                break block3;
            }
            if (delta <= 0) break block3;
            int k = Math.min(delta, this.workQueue.size());
            while (k-- > 0 && this.addWorker(null, true) && !this.workQueue.isEmpty()) {
            }
        }
    }

    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    public boolean prestartCoreThread() {
        return ThreadPoolExecutor.workerCountOf(this.ctl.get()) < this.corePoolSize && this.addWorker(null, true);
    }

    void ensurePrestart() {
        int wc = ThreadPoolExecutor.workerCountOf(this.ctl.get());
        if (wc < this.corePoolSize) {
            this.addWorker(null, true);
        } else if (wc == 0) {
            this.addWorker(null, false);
        }
    }

    public int prestartAllCoreThreads() {
        int n = 0;
        while (this.addWorker(null, true)) {
            ++n;
        }
        return n;
    }

    public boolean allowsCoreThreadTimeOut() {
        return this.allowCoreThreadTimeOut;
    }

    public void allowCoreThreadTimeOut(boolean value) {
        if (value && this.keepAliveTime <= 0L) {
            throw new IllegalArgumentException(sm.getString("threadPoolExecutor.invalidKeepAlive"));
        }
        if (value != this.allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = value;
            if (value) {
                this.interruptIdleWorkers();
            }
        }
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException();
        }
        this.maximumPoolSize = maximumPoolSize;
        if (ThreadPoolExecutor.workerCountOf(this.ctl.get()) > maximumPoolSize) {
            this.interruptIdleWorkers();
        }
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0L) {
            throw new IllegalArgumentException(sm.getString("threadPoolExecutor.invalidKeepAlive"));
        }
        if (time == 0L && this.allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException(sm.getString("threadPoolExecutor.invalidKeepAlive"));
        }
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0L) {
            this.interruptIdleWorkers();
        }
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
    }

    public long getThreadRenewalDelay() {
        return this.threadRenewalDelay;
    }

    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
    }

    public BlockingQueue<Runnable> getQueue() {
        return this.workQueue;
    }

    public boolean remove(Runnable task) {
        boolean removed = this.workQueue.remove(task);
        this.tryTerminate();
        return removed;
    }

    public void purge() {
        BlockingQueue<Runnable> q = this.workQueue;
        try {
            Iterator it = q.iterator();
            while (it.hasNext()) {
                Runnable r = (Runnable)it.next();
                if (!(r instanceof Future) || !((Future)((Object)r)).isCancelled()) continue;
                it.remove();
            }
        }
        catch (ConcurrentModificationException fallThrough) {
            for (Object r : q.toArray()) {
                if (!(r instanceof Future) || !((Future)r).isCancelled()) continue;
                q.remove(r);
            }
        }
        this.tryTerminate();
    }

    public void contextStopping() {
        this.lastContextStoppedTime.set(System.currentTimeMillis());
        int savedCorePoolSize = this.getCorePoolSize();
        this.setCorePoolSize(0);
        this.setCorePoolSize(savedCorePoolSize);
    }

    public int getPoolSize() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0x40000000) ? 0 : this.workers.size();
            return n;
        }
        finally {
            mainLock.unlock();
        }
    }

    protected int getPoolSizeNoLock() {
        return ThreadPoolExecutor.runStateAtLeast(this.ctl.get(), 0x40000000) ? 0 : this.workers.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getActiveCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : this.workers) {
                if (!w.isLocked()) continue;
                ++n;
            }
            int n2 = n;
            return n2;
        }
        finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = this.largestPoolSize;
            return n;
        }
        finally {
            mainLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getTaskCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (Worker w : this.workers) {
                n += w.completedTasks;
                if (!w.isLocked()) continue;
                ++n;
            }
            long l = n + (long)this.workQueue.size();
            return l;
        }
        finally {
            mainLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getCompletedTaskCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (Worker w : this.workers) {
                n += w.completedTasks;
            }
            long l = n;
            return l;
        }
        finally {
            mainLock.unlock();
        }
    }

    public int getSubmittedCount() {
        return this.submittedCount.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        int nworkers;
        int nactive;
        long ncompleted;
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            ncompleted = this.completedTaskCount;
            nactive = 0;
            nworkers = this.workers.size();
            for (Worker w : this.workers) {
                ncompleted += w.completedTasks;
                if (!w.isLocked()) continue;
                ++nactive;
            }
        }
        finally {
            mainLock.unlock();
        }
        int c = this.ctl.get();
        String runState = ThreadPoolExecutor.isRunning(c) ? "Running" : (ThreadPoolExecutor.runStateAtLeast(c, 0x60000000) ? "Terminated" : "Shutting down");
        return super.toString() + "[" + runState + ", pool size = " + nworkers + ", active threads = " + nactive + ", queued tasks = " + this.workQueue.size() + ", completed tasks = " + ncompleted + "]";
    }

    protected void beforeExecute(Thread t, Runnable r) {
    }

    protected void afterExecute(Runnable r, Throwable t) {
        if (!(t instanceof StopPooledThreadException)) {
            this.submittedCount.decrementAndGet();
        }
        if (t == null) {
            this.stopCurrentThreadIfNeeded();
        }
    }

    protected void stopCurrentThreadIfNeeded() {
        long lastTime;
        if (this.currentThreadShouldBeStopped() && (lastTime = this.lastTimeThreadKilledItself.longValue()) + this.threadRenewalDelay < System.currentTimeMillis() && this.lastTimeThreadKilledItself.compareAndSet(lastTime, System.currentTimeMillis() + 1L)) {
            String msg = sm.getString("threadPoolExecutor.threadStoppedToAvoidPotentialLeak", Thread.currentThread().getName());
            throw new StopPooledThreadException(msg);
        }
    }

    protected boolean currentThreadShouldBeStopped() {
        TaskThread currentTaskThread;
        Thread currentThread = Thread.currentThread();
        return this.threadRenewalDelay >= 0L && currentThread instanceof TaskThread && (currentTaskThread = (TaskThread)currentThread).getCreationTime() < this.lastContextStoppedTime.longValue();
    }

    protected void terminated() {
    }

    private final class Worker
    extends AbstractQueuedSynchronizer
    implements Runnable {
        private static final long serialVersionUID = 6138294804551838833L;
        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            this.setState(-1);
            this.firstTask = firstTask;
            this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            ThreadPoolExecutor.this.runWorker(this);
        }

        @Override
        protected boolean isHeldExclusively() {
            return this.getState() != 0;
        }

        @Override
        protected boolean tryAcquire(int unused) {
            if (this.compareAndSetState(0, 1)) {
                this.setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int unused) {
            this.setExclusiveOwnerThread(null);
            this.setState(0);
            return true;
        }

        public void lock() {
            this.acquire(1);
        }

        public boolean tryLock() {
            return this.tryAcquire(1);
        }

        public void unlock() {
            this.release(1);
        }

        public boolean isLocked() {
            return this.isHeldExclusively();
        }

        void interruptIfStarted() {
            Thread t;
            if (this.getState() >= 0 && (t = this.thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
            }
        }
    }

    public static interface RejectedExecutionHandler {
        public void rejectedExecution(Runnable var1, ThreadPoolExecutor var2);
    }

    private static class RejectPolicy
    implements RejectedExecutionHandler {
        private RejectPolicy() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw new RejectedExecutionException();
        }
    }

    public static class DiscardOldestPolicy
    implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }

    public static class DiscardPolicy
    implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

    public static class AbortPolicy
    implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException(sm.getString("threadPoolExecutor.taskRejected", r.toString(), e.toString()));
        }
    }

    public static class CallerRunsPolicy
    implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }
}

