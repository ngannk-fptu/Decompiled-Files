/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 */
package com.terracotta.entity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.terracotta.toolkit.Toolkit;

public class EntityLockHandler {
    private final ExecutorService executorService;
    private final Toolkit toolkit;

    EntityLockHandler(Toolkit toolkit) {
        this.toolkit = toolkit;
        this.executorService = Executors.newSingleThreadExecutor(new ThreadFactory(){

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "clustered-entity-locking-thread");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public void readLock(final String lockName) {
        try {
            this.executorService.submit(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    EntityLockHandler.this.toolkit.getReadWriteLock(lockName).readLock().lock();
                    return null;
                }
            }).get();
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to acquire read lock for lock " + lockName, e);
        }
    }

    public void readUnlock(final String lockName) {
        try {
            this.executorService.submit(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    EntityLockHandler.this.toolkit.getReadWriteLock(lockName).readLock().unlock();
                    return null;
                }
            }).get();
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to release read lock for lock " + lockName, e);
        }
    }

    public void dispose() {
        this.executorService.shutdownNow();
    }
}

