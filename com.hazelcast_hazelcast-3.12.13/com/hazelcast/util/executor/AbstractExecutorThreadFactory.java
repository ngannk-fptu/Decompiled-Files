/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractExecutorThreadFactory
implements ThreadFactory {
    protected final ClassLoader classLoader;

    public AbstractExecutorThreadFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public final Thread newThread(Runnable r) {
        Thread t = this.createThread(r);
        t.setContextClassLoader(this.classLoader);
        if (t.getContextClassLoader() == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = this.getClass().getClassLoader();
            }
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            t.setContextClassLoader(cl);
        }
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != 5) {
            t.setPriority(5);
        }
        return t;
    }

    protected abstract Thread createThread(Runnable var1);
}

