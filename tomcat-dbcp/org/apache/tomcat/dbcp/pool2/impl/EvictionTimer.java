/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.dbcp.pool2.impl.BaseGenericObjectPool;

class EvictionTimer {
    private static ScheduledThreadPoolExecutor executor;
    private static final HashMap<WeakReference<BaseGenericObjectPool.Evictor>, WeakRunner<BaseGenericObjectPool.Evictor>> TASK_MAP;

    static synchronized void cancel(BaseGenericObjectPool.Evictor evictor, Duration timeout, boolean restarting) {
        if (evictor != null) {
            evictor.cancel();
            EvictionTimer.remove(evictor);
        }
        if (!restarting && executor != null && TASK_MAP.isEmpty()) {
            executor.shutdown();
            try {
                executor.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            executor.setCorePoolSize(0);
            executor = null;
        }
    }

    static synchronized int getNumTasks() {
        return TASK_MAP.size();
    }

    static HashMap<WeakReference<BaseGenericObjectPool.Evictor>, WeakRunner<BaseGenericObjectPool.Evictor>> getTaskMap() {
        return TASK_MAP;
    }

    private static void remove(BaseGenericObjectPool.Evictor evictor) {
        for (Map.Entry<WeakReference<BaseGenericObjectPool.Evictor>, WeakRunner<BaseGenericObjectPool.Evictor>> entry : TASK_MAP.entrySet()) {
            if (entry.getKey().get() != evictor) continue;
            executor.remove(entry.getValue());
            TASK_MAP.remove(entry.getKey());
            break;
        }
    }

    static synchronized void schedule(BaseGenericObjectPool.Evictor task, Duration delay, Duration period) {
        if (null == executor) {
            executor = new ScheduledThreadPoolExecutor(1, new EvictorThreadFactory());
            executor.setRemoveOnCancelPolicy(true);
            executor.scheduleAtFixedRate(new Reaper(), delay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
        }
        WeakReference<BaseGenericObjectPool.Evictor> ref = new WeakReference<BaseGenericObjectPool.Evictor>(task);
        WeakRunner runner = new WeakRunner(ref);
        ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(runner, delay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
        task.setScheduledFuture(scheduledFuture);
        TASK_MAP.put(ref, runner);
    }

    private EvictionTimer() {
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvictionTimer []");
        return builder.toString();
    }

    static {
        TASK_MAP = new HashMap();
    }

    private static class EvictorThreadFactory
    implements ThreadFactory {
        private EvictorThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(null, runnable, "commons-pool-evictor");
            thread.setDaemon(true);
            AccessController.doPrivileged(() -> {
                thread.setContextClassLoader(EvictorThreadFactory.class.getClassLoader());
                return null;
            });
            return thread;
        }
    }

    private static class Reaper
    implements Runnable {
        private Reaper() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Class<EvictionTimer> clazz = EvictionTimer.class;
            synchronized (EvictionTimer.class) {
                for (Map.Entry entry : TASK_MAP.entrySet()) {
                    if (((WeakReference)entry.getKey()).get() != null) continue;
                    executor.remove((Runnable)entry.getValue());
                    TASK_MAP.remove(entry.getKey());
                }
                if (TASK_MAP.isEmpty() && executor != null) {
                    executor.shutdown();
                    executor.setCorePoolSize(0);
                    executor = null;
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        }
    }

    private static class WeakRunner<R extends Runnable>
    implements Runnable {
        private final WeakReference<R> ref;

        private WeakRunner(WeakReference<R> ref) {
            this.ref = ref;
        }

        @Override
        public void run() {
            Runnable task = (Runnable)this.ref.get();
            if (task != null) {
                task.run();
            } else {
                executor.remove(this);
                TASK_MAP.remove(this.ref);
            }
        }
    }
}

