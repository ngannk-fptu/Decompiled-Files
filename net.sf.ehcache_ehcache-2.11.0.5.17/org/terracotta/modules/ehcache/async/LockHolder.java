/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 */
package org.terracotta.modules.ehcache.async;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;

public class LockHolder {
    private static final int PARTIES = 2;
    private final Map<String, CyclicBarrier> holdings = new HashMap<String, CyclicBarrier>();

    public synchronized void hold(final ToolkitLock lock) {
        if (lock == null || this.holdings.containsKey(lock)) {
            return;
        }
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Thread lockThread = new Thread(new Runnable(){

            @Override
            public void run() {
                lock.lock();
                try {
                    LockHolder.this.await(barrier);
                    LockHolder.this.await(barrier);
                }
                finally {
                    try {
                        lock.unlock();
                    }
                    catch (Throwable throwable) {}
                    LockHolder.this.await(barrier);
                }
            }
        });
        this.holdings.put(lock.getName(), barrier);
        lockThread.start();
        this.await(barrier);
    }

    public synchronized void release(ToolkitLock lock) {
        CyclicBarrier barrier = this.holdings.get(lock.getName());
        if (barrier != null) {
            this.releaseLock(barrier);
            this.holdings.remove(lock);
        }
    }

    private void releaseLock(CyclicBarrier barrier) {
        this.await(barrier);
        this.await(barrier);
    }

    public synchronized void reset() {
        for (CyclicBarrier barrier : this.holdings.values()) {
            this.releaseLock(barrier);
        }
        this.holdings.clear();
    }

    private void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (BrokenBarrierException brokenBarrierException) {
            // empty catch block
        }
    }
}

