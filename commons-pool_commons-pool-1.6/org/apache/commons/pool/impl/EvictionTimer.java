/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;

class EvictionTimer {
    private static Timer _timer;
    private static int _usageCount;

    private EvictionTimer() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static synchronized void schedule(TimerTask task, long delay, long period) {
        if (null == _timer) {
            ClassLoader ccl = AccessController.doPrivileged(new PrivilegedGetTccl());
            try {
                AccessController.doPrivileged(new PrivilegedSetTccl(EvictionTimer.class.getClassLoader()));
                _timer = new Timer(true);
            }
            finally {
                AccessController.doPrivileged(new PrivilegedSetTccl(ccl));
            }
        }
        ++_usageCount;
        _timer.schedule(task, delay, period);
    }

    static synchronized void cancel(TimerTask task) {
        task.cancel();
        if (--_usageCount == 0) {
            _timer.cancel();
            _timer = null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class PrivilegedSetTccl
    implements PrivilegedAction<ClassLoader> {
        private final ClassLoader cl;

        PrivilegedSetTccl(ClassLoader cl) {
            this.cl = cl;
        }

        @Override
        public ClassLoader run() {
            Thread.currentThread().setContextClassLoader(this.cl);
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class PrivilegedGetTccl
    implements PrivilegedAction<ClassLoader> {
        private PrivilegedGetTccl() {
        }

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}

