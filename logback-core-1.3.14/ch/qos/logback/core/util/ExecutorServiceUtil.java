/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.util.EnvUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceUtil {
    private static final String NEW_VIRTUAL_TPT_METHOD_NAME = "newVirtualThreadPerTaskExecutor";
    private static final ThreadFactory THREAD_FACTORY_FOR_SCHEDULED_EXECUTION_SERVICE = new ThreadFactory(){
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadFactory defaultFactory = this.makeThreadFactory();

        private ThreadFactory makeThreadFactory() {
            if (EnvUtil.isJDK21OrHigher()) {
                try {
                    Method ofVirtualMethod = Thread.class.getMethod("ofVirtual", new Class[0]);
                    Object threadBuilderOfVirtual = ofVirtualMethod.invoke(null, new Object[0]);
                    Method factoryMethod = threadBuilderOfVirtual.getClass().getMethod("factory", new Class[0]);
                    return (ThreadFactory)factoryMethod.invoke(threadBuilderOfVirtual, new Object[0]);
                }
                catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    return Executors.defaultThreadFactory();
                }
            }
            return Executors.defaultThreadFactory();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = this.defaultFactory.newThread(r);
            if (!thread.isDaemon()) {
                thread.setDaemon(true);
            }
            thread.setName("logback-" + this.threadNumber.getAndIncrement());
            return thread;
        }
    };

    public static ScheduledExecutorService newScheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(4, THREAD_FACTORY_FOR_SCHEDULED_EXECUTION_SERVICE);
    }

    public static ExecutorService newExecutorService() {
        return ExecutorServiceUtil.newThreadPoolExecutor();
    }

    public static ThreadPoolExecutor newThreadPoolExecutor() {
        return new ThreadPoolExecutor(0, 32, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), THREAD_FACTORY_FOR_SCHEDULED_EXECUTION_SERVICE);
    }

    public static void shutdown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public static ExecutorService newAlternateThreadPoolExecutor() {
        if (EnvUtil.isJDK21OrHigher()) {
            try {
                Method newVirtualTPTMethod = Executors.class.getMethod(NEW_VIRTUAL_TPT_METHOD_NAME, new Class[0]);
                return (ExecutorService)newVirtualTPTMethod.invoke(null, new Object[0]);
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                return ExecutorServiceUtil.newThreadPoolExecutor();
            }
        }
        return ExecutorServiceUtil.newThreadPoolExecutor();
    }
}

