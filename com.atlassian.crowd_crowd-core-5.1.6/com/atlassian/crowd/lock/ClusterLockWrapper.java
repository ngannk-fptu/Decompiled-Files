/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 */
package com.atlassian.crowd.lock;

import com.atlassian.beehive.ClusterLock;
import java.util.function.Supplier;

public class ClusterLockWrapper {
    private final Supplier<ClusterLock> lockSupplier;

    public ClusterLockWrapper(Supplier<ClusterLock> lockSupplier) {
        this.lockSupplier = lockSupplier;
    }

    public void run(Runnable runnable) {
        ClusterLock lock = this.lockSupplier.get();
        lock.lock();
        try {
            runnable.run();
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T run(Supplier<T> supplier) {
        ClusterLock lock = this.lockSupplier.get();
        lock.lock();
        try {
            T t = supplier.get();
            return t;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean tryRun(Runnable runnable) {
        ClusterLock lock = this.lockSupplier.get();
        if (lock.tryLock()) {
            try {
                runnable.run();
                boolean bl = true;
                return bl;
            }
            finally {
                lock.unlock();
            }
        }
        return false;
    }
}

