/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Throwables
 *  com.google.common.util.concurrent.SettableFuture
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.util.concurrent;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockedTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(LockedTaskExecutor.class);
    private final Lock lock;

    public LockedTaskExecutor(Lock lock) {
        this.lock = lock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> Future<T> tryExecuteUnderLock(Callable<T> task, long lockTimeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        if (this.lock.tryLock(lockTimeout, timeUnit)) {
            SettableFuture result = SettableFuture.create();
            try {
                result.set(task.call());
            }
            catch (Exception e) {
                result.setException((Throwable)e);
            }
            finally {
                this.lock.unlock();
            }
            return result;
        }
        throw new TimeoutException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tryExecuteUnderLock(Runnable task, long lockTimeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        if (this.lock.tryLock(lockTimeout, timeUnit)) {
            try {
                task.run();
            }
            finally {
                this.lock.unlock();
            }
        } else {
            throw new TimeoutException();
        }
    }

    public <T> T executeUnderLock(Callable<T> task, String taskDescription, long lockTimeout, TimeUnit timeUnit) {
        try {
            return this.tryExecuteUnderLock(task, lockTimeout, timeUnit).get();
        }
        catch (InterruptedException e) {
            log.error("Thread interrupted whilst [{}]", (Object)taskDescription);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (ExecutionException e) {
            log.error("Failed whilst [{}]", (Object)taskDescription, (Object)e);
            Throwables.throwIfUnchecked((Throwable)e.getCause());
            throw new RuntimeException(e.getCause());
        }
        catch (TimeoutException e) {
            log.error("Timed out waiting {}ms for lock whilst [{}]", new Object[]{timeUnit.toMillis(lockTimeout), taskDescription, e});
            Thread.dumpStack();
            throw new RuntimeException(e);
        }
    }

    public void executeUnderLock(Runnable task, Supplier<String> taskDescription, long lockTimeout, TimeUnit timeUnit) {
        try {
            this.tryExecuteUnderLock(task, lockTimeout, timeUnit);
        }
        catch (InterruptedException e) {
            log.error("Thread interrupted whilst {}", taskDescription.get());
            Thread.currentThread().interrupt();
            throw Throwables.propagate((Throwable)e);
        }
        catch (TimeoutException e) {
            log.error("Timed out waiting for lock before " + (String)taskDescription.get(), (Throwable)e);
            throw Throwables.propagate((Throwable)e);
        }
    }
}

