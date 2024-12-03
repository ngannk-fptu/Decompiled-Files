/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.threadpool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.i18n.Messages;
import org.apache.commons.logging.Log;

public class ThreadPool {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$threadpool$ThreadPool == null ? (class$org$apache$axis$components$threadpool$ThreadPool = ThreadPool.class$("org.apache.axis.components.threadpool.ThreadPool")) : class$org$apache$axis$components$threadpool$ThreadPool).getName());
    public static final int DEFAULT_MAX_THREADS = 100;
    protected Map threads = new Hashtable();
    protected long threadcount;
    public boolean _shutdown;
    private int maxPoolSize = 100;
    static /* synthetic */ Class class$org$apache$axis$components$threadpool$ThreadPool;

    public ThreadPool() {
    }

    public ThreadPool(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanup() throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::cleanup");
        }
        if (!this.isShutdown()) {
            this.safeShutdown();
            this.awaitShutdown();
        }
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            this.threads.clear();
            this._shutdown = false;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::cleanup");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isShutdown() {
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            return this._shutdown && this.threadcount == 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isShuttingDown() {
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            return this._shutdown;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getWorkerCount() {
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            return this.threadcount;
        }
    }

    public void addWorker(Runnable worker) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::addWorker");
        }
        if (this._shutdown || this.threadcount == (long)this.maxPoolSize) {
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        }
        Thread thread = new Thread(worker);
        this.threads.put(worker, thread);
        ++this.threadcount;
        thread.start();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::addWorker");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void interruptAll() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::interruptAll");
        }
        Map map = this.threads;
        synchronized (map) {
            Iterator i = this.threads.values().iterator();
            while (i.hasNext()) {
                Thread t = (Thread)i.next();
                t.interrupt();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::interruptAll");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::shutdown");
        }
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            this._shutdown = true;
        }
        this.interruptAll();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::shutdown");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void safeShutdown() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::safeShutdown");
        }
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            this._shutdown = true;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::safeShutdown");
        }
    }

    public synchronized void awaitShutdown() throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::awaitShutdown");
        }
        if (!this._shutdown) {
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        }
        while (this.threadcount > 0L) {
            this.wait();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::awaitShutdown");
        }
    }

    public synchronized boolean awaitShutdown(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::awaitShutdown");
        }
        if (!this._shutdown) {
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        }
        if (this.threadcount == 0L) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit: ThreadPool::awaitShutdown");
            }
            return true;
        }
        long waittime = timeout;
        if (waittime <= 0L) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit: ThreadPool::awaitShutdown");
            }
            return false;
        }
        do {
            this.wait(waittime);
            if (this.threadcount != 0L) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit: ThreadPool::awaitShutdown");
            }
            return true;
        } while ((waittime = timeout - System.currentTimeMillis()) > 0L);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::awaitShutdown");
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void workerDone(Runnable worker, boolean restart) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ThreadPool::workerDone");
        }
        ThreadPool threadPool = this;
        synchronized (threadPool) {
            this.threads.remove(worker);
            if (--this.threadcount == 0L && this._shutdown) {
                this.notifyAll();
            }
            if (!this._shutdown && restart) {
                this.addWorker(worker);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ThreadPool::workerDone");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

