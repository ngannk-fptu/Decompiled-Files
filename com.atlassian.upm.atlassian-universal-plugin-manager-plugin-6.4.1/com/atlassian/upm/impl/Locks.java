/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.impl;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.user.UserKey;
import java.util.function.Supplier;

public abstract class Locks {
    public static ClusterLock getLock(ClusterLockService lockService, Class lockOwner) {
        return lockService.getLockForName(lockOwner.getName());
    }

    public static ClusterLock getLock(ClusterLockService lockService, Class lockOwner, String lockId) {
        return lockService.getLockForName(lockOwner.getName() + ":" + lockId);
    }

    public static ClusterLock getLock(ClusterLockService lockService, Class lockOwner, UserKey user) {
        return Locks.getLock(lockService, lockOwner, user.getStringValue());
    }

    public static <T> T writeWithLock(ClusterLock lock, Supplier<T> task) {
        return Locks.runWithLock(lock, task);
    }

    public static void writeWithLock(ClusterLock lock, Runnable task) {
        Locks.runWithLock(lock, () -> {
            task.run();
            return null;
        });
    }

    public static <T> T readWithLock(ClusterLock lock, Supplier<T> task) {
        return Locks.runWithLock(lock, task);
    }

    private static <T> T runWithLock(ClusterLock lock, Supplier<T> task) {
        lock.lock();
        try {
            T t = task.get();
            return t;
        }
        finally {
            lock.unlock();
        }
    }

    public static <T> T readWithoutLock(ClusterLock lock, Supplier<T> task) {
        return task.get();
    }
}

