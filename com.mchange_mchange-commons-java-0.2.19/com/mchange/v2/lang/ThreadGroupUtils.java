/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

public final class ThreadGroupUtils {
    public static ThreadGroup rootThreadGroup() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup threadGroup2 = threadGroup.getParent();
        while (threadGroup2 != null) {
            threadGroup = threadGroup2;
            threadGroup2 = threadGroup.getParent();
        }
        return threadGroup;
    }

    private ThreadGroupUtils() {
    }
}

