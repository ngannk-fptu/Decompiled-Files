/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.concurrent.ManagedExecutors
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.core.task.support.TaskExecutorAdapter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.scheduling.concurrent;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.enterprise.concurrent.ManagedExecutors;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;

public class ConcurrentTaskExecutor
implements AsyncListenableTaskExecutor,
SchedulingTaskExecutor {
    @Nullable
    private static Class<?> managedExecutorServiceClass;
    private Executor concurrentExecutor;
    private TaskExecutorAdapter adaptedExecutor;
    @Nullable
    private TaskDecorator taskDecorator;

    public ConcurrentTaskExecutor() {
        this.concurrentExecutor = Executors.newSingleThreadExecutor();
        this.adaptedExecutor = new TaskExecutorAdapter(this.concurrentExecutor);
    }

    public ConcurrentTaskExecutor(@Nullable Executor executor) {
        this.concurrentExecutor = executor != null ? executor : Executors.newSingleThreadExecutor();
        this.adaptedExecutor = this.getAdaptedExecutor(this.concurrentExecutor);
    }

    public final void setConcurrentExecutor(@Nullable Executor executor) {
        this.concurrentExecutor = executor != null ? executor : Executors.newSingleThreadExecutor();
        this.adaptedExecutor = this.getAdaptedExecutor(this.concurrentExecutor);
    }

    public final Executor getConcurrentExecutor() {
        return this.concurrentExecutor;
    }

    public final void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
        this.adaptedExecutor.setTaskDecorator(taskDecorator);
    }

    public void execute(Runnable task) {
        this.adaptedExecutor.execute(task);
    }

    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        this.adaptedExecutor.execute(task, startTimeout);
    }

    public Future<?> submit(Runnable task) {
        return this.adaptedExecutor.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.adaptedExecutor.submit(task);
    }

    public ListenableFuture<?> submitListenable(Runnable task) {
        return this.adaptedExecutor.submitListenable(task);
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return this.adaptedExecutor.submitListenable(task);
    }

    private TaskExecutorAdapter getAdaptedExecutor(Executor concurrentExecutor) {
        if (managedExecutorServiceClass != null && managedExecutorServiceClass.isInstance(concurrentExecutor)) {
            return new ManagedTaskExecutorAdapter(concurrentExecutor);
        }
        TaskExecutorAdapter adapter = new TaskExecutorAdapter(concurrentExecutor);
        if (this.taskDecorator != null) {
            adapter.setTaskDecorator(this.taskDecorator);
        }
        return adapter;
    }

    static {
        try {
            managedExecutorServiceClass = ClassUtils.forName((String)"javax.enterprise.concurrent.ManagedExecutorService", (ClassLoader)ConcurrentTaskScheduler.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            managedExecutorServiceClass = null;
        }
    }

    protected static class ManagedTaskBuilder {
        protected ManagedTaskBuilder() {
        }

        public static Runnable buildManagedTask(Runnable task, String identityName) {
            HashMap<String, String> properties;
            if (task instanceof SchedulingAwareRunnable) {
                properties = new HashMap<String, String>(4);
                properties.put("javax.enterprise.concurrent.LONGRUNNING_HINT", Boolean.toString(((SchedulingAwareRunnable)task).isLongLived()));
            } else {
                properties = new HashMap(2);
            }
            properties.put("javax.enterprise.concurrent.IDENTITY_NAME", identityName);
            return ManagedExecutors.managedTask((Runnable)task, properties, null);
        }

        public static <T> Callable<T> buildManagedTask(Callable<T> task, String identityName) {
            HashMap<String, String> properties = new HashMap<String, String>(2);
            properties.put("javax.enterprise.concurrent.IDENTITY_NAME", identityName);
            return ManagedExecutors.managedTask(task, properties, null);
        }
    }

    private static class ManagedTaskExecutorAdapter
    extends TaskExecutorAdapter {
        public ManagedTaskExecutorAdapter(Executor concurrentExecutor) {
            super(concurrentExecutor);
        }

        public void execute(Runnable task) {
            super.execute(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        public Future<?> submit(Runnable task) {
            return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        public ListenableFuture<?> submitListenable(Runnable task) {
            return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
            return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }
    }
}

