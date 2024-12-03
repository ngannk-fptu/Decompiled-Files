/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.concurrency;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadFactory
implements ThreadFactory {
    private final String threadNamePrefix;
    private static final AtomicInteger threadIdx = new AtomicInteger();
    private final boolean daemon;

    SimpleThreadFactory(String threadNamePrefix, boolean daemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        ThreadGroup securityManagerThreadGroup = null;
        try {
            Method getSecurityManager = System.class.getDeclaredMethod("getSecurityManager", new Class[0]);
            Object securityManager = getSecurityManager.invoke(null, new Object[0]);
            if (securityManager != null) {
                Method getThreadGroup = securityManager.getClass().getDeclaredMethod("getThreadGroup", new Class[0]);
                securityManagerThreadGroup = (ThreadGroup)getThreadGroup.invoke(securityManager, new Object[0]);
            }
        }
        catch (Throwable getSecurityManager) {
            // empty catch block
        }
        Thread thread = new Thread(securityManagerThreadGroup != null ? securityManagerThreadGroup : new ThreadGroup("ClassGraph-thread-group"), runnable, this.threadNamePrefix + threadIdx.getAndIncrement());
        thread.setDaemon(this.daemon);
        return thread;
    }
}

