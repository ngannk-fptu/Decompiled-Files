/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.threadlocal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RegisteredThreadLocals {
    private static ThreadLocal<Set<ThreadLocal<?>>> trackingThreadLocal = new ThreadLocal<Set<ThreadLocal<?>>>(){

        @Override
        protected Set<ThreadLocal<?>> initialValue() {
            return new HashSet();
        }
    };

    public static ThreadLocal register(ThreadLocal threadLocal) {
        trackingThreadLocal.get().add(threadLocal);
        return threadLocal;
    }

    public static void reset() {
        Set<ThreadLocal<?>> threadLocals = trackingThreadLocal.get();
        for (ThreadLocal<?> threadLocal : threadLocals) {
            threadLocal.remove();
        }
        trackingThreadLocal.remove();
    }

    public static Set<ThreadLocal<?>> get() {
        return new HashSet((Collection)trackingThreadLocal.get());
    }
}

