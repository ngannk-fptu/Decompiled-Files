/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.security.PrivilegedSetAccessControlContext;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.apache.tomcat.util.threads.Constants;
import org.apache.tomcat.util.threads.TaskThread;

public class TaskThreadFactory
implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final int threadPriority;

    public TaskThreadFactory(String namePrefix, boolean daemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.threadPriority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        TaskThread t = new TaskThread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
        t.setDaemon(this.daemon);
        t.setPriority(this.threadPriority);
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedAction<Void> pa = new PrivilegedSetTccl(t, this.getClass().getClassLoader());
            AccessController.doPrivileged(pa);
            pa = new PrivilegedSetAccessControlContext(t);
            AccessController.doPrivileged(pa);
        } else {
            t.setContextClassLoader(this.getClass().getClassLoader());
        }
        return t;
    }
}

