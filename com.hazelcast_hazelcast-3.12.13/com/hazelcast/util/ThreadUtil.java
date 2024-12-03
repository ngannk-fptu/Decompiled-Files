/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.Preconditions;

public final class ThreadUtil {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal();

    private ThreadUtil() {
    }

    public static long getThreadId() {
        Long threadId = THREAD_LOCAL.get();
        if (threadId != null) {
            return threadId;
        }
        return Thread.currentThread().getId();
    }

    public static void setThreadId(long threadId) {
        THREAD_LOCAL.set(threadId);
    }

    public static void removeThreadId() {
        THREAD_LOCAL.remove();
    }

    public static String createThreadName(String hzName, String name) {
        Preconditions.checkNotNull(name, "name can't be null");
        return "hz." + hzName + "." + name;
    }

    public static String createThreadPoolName(String hzName, String poolName) {
        return ThreadUtil.createThreadName(hzName, poolName) + ".thread-";
    }

    public static void assertRunningOnPartitionThread() {
        assert (Thread.currentThread().getName().contains("partition-operation"));
    }
}

