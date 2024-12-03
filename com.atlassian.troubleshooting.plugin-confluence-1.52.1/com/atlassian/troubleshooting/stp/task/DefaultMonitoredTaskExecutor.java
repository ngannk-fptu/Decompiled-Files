/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.MonitoredCallable;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.PublishingTaskMonitorListener;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.stp.task.TaskType;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultMonitoredTaskExecutor<V, M extends TaskMonitor<V>>
implements MonitoredTaskExecutor<V, M> {
    private final ThreadPoolExecutor executorService;
    @Nonnull
    private final TaskType taskType;
    private final ClusterService clusterService;
    private final TaskMonitorRepositoryFactory repositoryFactory;
    private volatile boolean shutdown;

    public DefaultMonitoredTaskExecutor(@Nonnull TaskType taskType, int maxThreads, @Nonnull ClusterService clusterService, @Nonnull TaskMonitorRepositoryFactory repositoryFactory) {
        this.taskType = Objects.requireNonNull(taskType);
        this.clusterService = Objects.requireNonNull(clusterService);
        this.repositoryFactory = Objects.requireNonNull(repositoryFactory);
        this.executorService = new ThreadPoolExecutor(maxThreads, maxThreads, 2L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), r -> {
            Thread thread = new Thread(r);
            thread.setName(taskType.getKey());
            thread.setDaemon(true);
            return thread;
        });
        this.executorService.allowCoreThreadTimeOut(true);
    }

    @Override
    @Nullable
    public M getMonitor(@Nonnull String id) {
        return (M)((TaskMonitor)this.getTaskMonitorRepository().getTaskMonitor(id).orElse(null));
    }

    @Override
    public Collection<M> getMonitors() {
        return this.getTaskMonitorRepository().getRecentTaskMonitors();
    }

    @Override
    public void shutdown() {
        if (!this.shutdown) {
            this.shutdown = true;
            this.executorService.shutdown();
        }
    }

    @Override
    public <MM extends MutableTaskMonitor<V>> MM submit(@Nonnull MonitoredCallable<V, MM> task, @Nullable String clusteredTaskId) {
        Preconditions.checkState((!this.shutdown ? 1 : 0) != 0, (Object)"The executor has already been shut down");
        ListenableFutureTask futureTask = ListenableFutureTask.create(task);
        MM monitor = task.getMonitor();
        this.clusterService.getCurrentNodeId().ifPresent(arg_0 -> monitor.setNodeId(arg_0));
        if (clusteredTaskId != null) {
            monitor.setClusteredTaskId(clusteredTaskId);
        }
        this.initMonitor(monitor, (ListenableFutureTask<V>)futureTask);
        this.executorService.submit((Runnable)futureTask);
        return monitor;
    }

    @Override
    @Nonnull
    public TaskMonitorRepository<M> getTaskMonitorRepository() {
        return this.repositoryFactory.getRepository(this.taskType);
    }

    private synchronized <MM extends MutableTaskMonitor<V>> void initMonitor(MM monitor, ListenableFutureTask<V> future) {
        String id = UUID.randomUUID().toString();
        monitor.init(id, future);
        TaskMonitorRepository<MM> taskMonitorRepository = this.getTaskMonitorRepository();
        monitor.addListener(new PublishingTaskMonitorListener(taskMonitorRepository));
        taskMonitorRepository.storeTaskMonitor(monitor);
    }
}

