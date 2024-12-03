/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.BaseGenericObjectPool;

class EvictionTimer {
    private static ScheduledThreadPoolExecutor executor;

    private EvictionTimer() {
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvictionTimer []");
        return builder.toString();
    }

    static synchronized void schedule(BaseGenericObjectPool.Evictor task, long delay, long period) {
        if (null == executor) {
            executor = new ScheduledThreadPoolExecutor(1, new EvictorThreadFactory());
            executor.setRemoveOnCancelPolicy(true);
        }
        ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
        task.setScheduledFuture(scheduledFuture);
    }

    static synchronized void cancel(BaseGenericObjectPool.Evictor evictor, long timeout, TimeUnit unit) {
        if (evictor != null) {
            evictor.cancel();
        }
        if (executor != null && executor.getQueue().isEmpty()) {
            executor.shutdown();
            try {
                executor.awaitTermination(timeout, unit);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            executor.setCorePoolSize(0);
            executor = null;
        }
    }

    private static class EvictorThreadFactory
    implements ThreadFactory {
        private EvictorThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable runnable) {
            final Thread thread = new Thread(null, runnable, "commons-pool-evictor-thread");
            thread.setDaemon(true);
            AccessController.doPrivileged(new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    thread.setContextClassLoader(EvictorThreadFactory.class.getClassLoader());
                    return null;
                }
            });
            return thread;
        }
    }
}

