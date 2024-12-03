/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.TDSTimeoutTask;
import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SharedTimer
implements Serializable {
    private static final long serialVersionUID = -4069361613863955760L;
    static final String CORE_THREAD_PREFIX = "mssql-jdbc-shared-timer-core-";
    private static final AtomicLong CORE_THREAD_COUNTER = new AtomicLong();
    private static final Lock LOCK = new ReentrantLock();
    private final long id = CORE_THREAD_COUNTER.getAndIncrement();
    private final AtomicInteger refCount = new AtomicInteger();
    private static volatile SharedTimer instance;
    private transient ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, task -> {
        Thread t = new Thread(task, CORE_THREAD_PREFIX + this.id);
        t.setDaemon(true);
        return t;
    });

    private SharedTimer() {
        this.executor.setRemoveOnCancelPolicy(true);
    }

    public long getId() {
        return this.id;
    }

    static boolean isRunning() {
        return instance != null;
    }

    public void removeRef() {
        LOCK.lock();
        try {
            if (this.refCount.get() <= 0) {
                throw new IllegalStateException("removeRef() called more than actual references");
            }
            if (this.refCount.decrementAndGet() == 0) {
                this.executor.shutdownNow();
                this.executor = null;
                instance = null;
            }
        }
        finally {
            LOCK.unlock();
        }
    }

    public static SharedTimer getTimer() {
        LOCK.lock();
        try {
            if (instance == null) {
                instance = new SharedTimer();
            }
            SharedTimer.instance.refCount.getAndIncrement();
        }
        finally {
            LOCK.unlock();
        }
        return instance;
    }

    public ScheduledFuture<?> schedule(TDSTimeoutTask task, long delaySeconds) {
        return this.schedule(task, delaySeconds, TimeUnit.SECONDS);
    }

    public ScheduledFuture<?> schedule(TDSTimeoutTask task, long delay, TimeUnit unit) {
        if (this.executor == null) {
            throw new IllegalStateException("Cannot schedule tasks after shutdown");
        }
        return this.executor.schedule(task, delay, unit);
    }
}

