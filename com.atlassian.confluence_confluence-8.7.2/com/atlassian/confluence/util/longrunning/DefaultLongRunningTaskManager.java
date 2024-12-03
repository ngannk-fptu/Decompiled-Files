/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.FieldsAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.config.lifecycle.events.ApplicationStoppedEvent
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskStatus
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.annotations.nullability.FieldsAreNonnullByDefault;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.config.lifecycle.events.ApplicationStoppedEvent;
import com.atlassian.confluence.api.impl.service.longtasks.LongTaskFactory;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.confluence.internal.diagnostics.LongRunningTaskMonitor;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.internal.pagination.SubListResponse;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.ManagedTask;
import com.atlassian.confluence.util.longrunning.TaskWrapper;
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class DefaultLongRunningTaskManager
implements LongRunningTaskManagerInternal,
InitializingBean,
DisposableBean {
    private static final Logger lifecycleLog = LoggerFactory.getLogger((String)"com.atlassian.confluence.lifecycle");
    private static final long NOT_STARTED = 0L;
    private final EventListenerRegistrar eventPublisher;
    private ConcurrentMap<LongRunningTaskId, TaskWrapper> trackedTasks = new ConcurrentHashMap<LongRunningTaskId, TaskWrapper>();
    private volatile ExecutorService executorService;
    private final PermissionManager permissionManager;
    private final ActivityMonitor activityMonitor;
    private final LongRunningTaskMonitor longRunningTaskMonitor;

    public DefaultLongRunningTaskManager(PermissionManager permissionManager, ActivityMonitor activityMonitor, LongRunningTaskMonitor longRunningTaskMonitor, EventListenerRegistrar eventListenerRegistrar) {
        this(permissionManager, activityMonitor, longRunningTaskMonitor, ConfluenceExecutors.wrap(DefaultLongRunningTaskManager.newThreadPoolExecutor(), ConfluenceExecutors.VCACHE_TASK_WRAPPER, ConfluenceExecutors.THREAD_LOCAL_CONTEXT_TASK_WRAPPER), eventListenerRegistrar);
    }

    @VisibleForTesting
    DefaultLongRunningTaskManager(PermissionManager permissionManager, ActivityMonitor activityMonitor, LongRunningTaskMonitor longRunningTaskMonitor, ExecutorService executor, EventListenerRegistrar eventPublisher) {
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.activityMonitor = Objects.requireNonNull(activityMonitor);
        this.longRunningTaskMonitor = Objects.requireNonNull(longRunningTaskMonitor);
        this.executorService = Objects.requireNonNull(executor);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationStopped(ApplicationStoppedEvent e) {
        this.destroy(30L, TimeUnit.SECONDS);
    }

    @VisibleForTesting
    static ThreadPoolExecutor newThreadPoolExecutor() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), ThreadFactories.namedThreadFactory((String)"long-running-task", (ThreadFactories.Type)ThreadFactories.Type.USER));
    }

    @Override
    public LongRunningTaskId queueLongRunningTask(LongRunningTask task) {
        Objects.requireNonNull(task, "task cannot be null");
        if (this.executorService.isShutdown()) {
            throw new IllegalStateException("Unable to queue long-running task. Task manager has been stopped");
        }
        LongRunningTaskId id = LongRunningTaskId.newInstance();
        this.trackedTasks.put(id, new TaskWrapper(null, id, task, 0L));
        return id;
    }

    @Override
    public void startIfQueued(LongRunningTaskId taskId) {
        Objects.requireNonNull(taskId, "taskId cannot be null");
        TaskWrapper taskWrapper = (TaskWrapper)this.trackedTasks.get(taskId);
        if (taskWrapper != null && taskWrapper.getStarted() == 0L) {
            if (this.executorService.isShutdown()) {
                throw new IllegalStateException("Unable to queue long-running task. Task manager has been stopped");
            }
            this.executorService.submit(new ManagedTask(taskId, taskWrapper.getTask(), this, this.activityMonitor));
            this.trackedTasks.put(taskId, new TaskWrapper(null, taskId, taskWrapper.getTask(), System.currentTimeMillis()));
        }
    }

    @Override
    public LongRunningTaskId startLongRunningTask(@Nullable User user, LongRunningTask task) {
        ManagedTask wrappedTask = this.wrapAsManagedTask(user, task);
        this.executorService.submit(wrappedTask);
        return wrappedTask.getId();
    }

    @Override
    public void runToCompletion(@Nullable User user, LongRunningTask task) {
        ManagedTask wrappedTask = this.wrapAsManagedTask(user, task);
        wrappedTask.run();
    }

    private ManagedTask wrapAsManagedTask(@Nullable User user, LongRunningTask task) {
        Objects.requireNonNull(task, "task cannot be null");
        if (this.executorService.isShutdown()) {
            throw new IllegalStateException("Unable to queue long-running task. Task manager has been stopped");
        }
        LongRunningTaskId id = LongRunningTaskId.newInstance();
        ManagedTask wrappedTask = new ManagedTask(id, task, this, this.activityMonitor);
        this.longRunningTaskMonitor.start(task);
        this.trackedTasks.put(id, new TaskWrapper(user, id, task, System.currentTimeMillis()));
        return wrappedTask;
    }

    @Override
    public @Nullable LongRunningTask getLongRunningTask(@Nullable User user, LongRunningTaskId taskId) {
        Objects.requireNonNull(taskId, "taskId cannot be null");
        TaskWrapper wrapper = (TaskWrapper)this.trackedTasks.get(taskId);
        if (wrapper != null && this.userCanGetTask(user, wrapper)) {
            return wrapper.getTask();
        }
        return null;
    }

    private boolean userCanGetTask(@Nullable User user, TaskWrapper wrapper) {
        User taskUser = wrapper.getUser();
        if (taskUser == null || wrapper.isSameUser(user)) {
            return true;
        }
        if (this.permissionManager.isSystemAdministrator(user)) {
            return true;
        }
        if (this.permissionManager.isConfluenceAdministrator(user)) {
            return !this.permissionManager.isSystemAdministrator(taskUser);
        }
        return false;
    }

    @Override
    public PageResponse<LongTaskStatus> getAllTasks(@Nullable ConfluenceUser asUser, LimitedRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        ArrayList<LongTaskStatus> fullList = new ArrayList<LongTaskStatus>();
        for (Map.Entry entry : this.trackedTasks.entrySet()) {
            TaskWrapper wrapper = (TaskWrapper)entry.getValue();
            if (!this.userCanGetTask(asUser, wrapper)) continue;
            LongTaskId id = ((LongRunningTaskId)entry.getKey()).asLongTaskId();
            fullList.add(LongTaskFactory.buildStatus(id, wrapper.getTask()));
        }
        fullList.sort(LongTaskStatus::compareTo);
        return SubListResponse.from(fullList, request);
    }

    @Override
    public List<LongTaskStatus> removeComplete() {
        ImmutableSet entries = ImmutableSet.copyOf(this.trackedTasks.entrySet());
        ImmutableList.Builder removed = ImmutableList.builder();
        for (Map.Entry entry : entries) {
            TaskWrapper wrapper = (TaskWrapper)entry.getValue();
            LongRunningTask task = wrapper.getTask();
            if (!task.isComplete()) continue;
            LongRunningTaskId taskId = (LongRunningTaskId)entry.getKey();
            LongTaskId id = taskId.asLongTaskId();
            removed.add((Object)LongTaskFactory.buildStatus(id, task));
            this.trackedTasks.remove(taskId);
        }
        return removed.build();
    }

    @Override
    public int getTaskCount() {
        return (int)this.trackedTasks.values().stream().filter(t -> !t.getTask().isComplete()).count();
    }

    @Override
    public void stopTrackingLongRunningTask(LongRunningTaskId taskId) {
        Objects.requireNonNull(taskId, "taskId cannot be null");
        this.trackedTasks.remove(taskId);
    }

    public synchronized void destroy() {
        this.destroy(30L, TimeUnit.SECONDS);
    }

    private synchronized void destroy(long timeout, TimeUnit unit) {
        lifecycleLog.info("Shutting down long running task service");
        this.executorService.shutdown();
        try {
            if (!this.executorService.awaitTermination(timeout, unit)) {
                lifecycleLog.warn("Long running task service took more than {} {} to shutdown. Killing any remaining tasks.", (Object)timeout, (Object)unit);
                this.executorService.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public synchronized void stop(long timeout, TimeUnit unit) throws TimeoutException {
        Objects.requireNonNull(unit, "unit cannot be null");
        this.executorService.shutdown();
        try {
            if (!this.executorService.awaitTermination(timeout, unit)) {
                this.resume();
                throw new TimeoutException("Unable to shut down LongRunningTask service in " + timeout + " " + unit);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public synchronized void resume() {
        this.executorService = ConfluenceExecutors.wrap(DefaultLongRunningTaskManager.newThreadPoolExecutor());
    }

    protected void taskFinished(LongRunningTaskId taskId) {
        Objects.requireNonNull(taskId, "taskId cannot be null");
        TaskWrapper oldTaskWrapper = (TaskWrapper)this.trackedTasks.get(taskId);
        if (oldTaskWrapper != null) {
            TaskWrapper completedTaskWrapper = new TaskWrapper(oldTaskWrapper, System.currentTimeMillis());
            this.trackedTasks.replace(taskId, oldTaskWrapper, completedTaskWrapper);
            this.longRunningTaskMonitor.stop(oldTaskWrapper.getTask());
        }
    }
}

