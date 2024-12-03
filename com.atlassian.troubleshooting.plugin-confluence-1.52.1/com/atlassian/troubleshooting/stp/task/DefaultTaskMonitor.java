/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.rest.RestTaskStatus;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorListener;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTaskMonitor<V>
implements MutableTaskMonitor<V> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskMonitor.class);
    private final List<Message> errors;
    private final List<Message> warnings;
    private transient List<TaskMonitorListener<V>> listeners;
    private volatile transient Future<V> future;
    private volatile int progressPercentage;
    private volatile String progressMessage = "";
    private volatile String taskId;
    private volatile String nodeId;
    private volatile String clusteredTaskId;
    private long createdTimestamp = System.currentTimeMillis();

    public DefaultTaskMonitor() {
        this.errors = new CopyOnWriteArrayList<Message>();
        this.listeners = new CopyOnWriteArrayList<TaskMonitorListener<V>>();
        this.warnings = new CopyOnWriteArrayList<Message>();
    }

    @Override
    public void addError(@Nonnull Message error) {
        this.errors.add((Message)Preconditions.checkNotNull((Object)error, (Object)"error"));
        this.notifyUpdated();
    }

    @Override
    public void addListener(@Nonnull TaskMonitorListener<V> listener) {
        this.listeners.add((TaskMonitorListener<V>)Preconditions.checkNotNull(listener, (Object)"listener"));
    }

    @Override
    public Optional<String> getClusteredTaskId() {
        return Optional.ofNullable(this.clusteredTaskId);
    }

    @Override
    public void setClusteredTaskId(@Nonnull String clusteredTaskId) {
        this.clusteredTaskId = clusteredTaskId;
    }

    @Override
    public long getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(long timestamp) {
        this.createdTimestamp = timestamp;
    }

    @Override
    public void addWarning(@Nonnull Message warning) {
        this.warnings.add((Message)Preconditions.checkNotNull((Object)warning, (Object)"warning"));
        this.notifyUpdated();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.future != null && this.future.cancel(mayInterruptIfRunning);
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.checkInitialized();
        return this.future.get();
    }

    @Override
    public V get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.checkInitialized();
        return this.future.get(timeout, unit);
    }

    @Override
    @Nonnull
    public List<Message> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Override
    @Nonnull
    public String getProgressMessage() {
        return StringUtils.isEmpty((String)this.progressMessage) ? "" : this.progressMessage;
    }

    @Override
    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    @Override
    @Nonnull
    public String getTaskId() {
        return this.taskId;
    }

    @Override
    @Nonnull
    public List<Message> getWarnings() {
        return Collections.unmodifiableList(this.warnings);
    }

    public boolean hasWarnings() {
        return this.getWarnings().size() > 0;
    }

    @Override
    public void init(@Nonnull String taskId, @Nonnull ListenableFutureTask<V> future) {
        this.future = (Future)Preconditions.checkNotNull(future, (Object)"future");
        this.taskId = (String)Preconditions.checkNotNull((Object)taskId, (Object)"taskId");
        future.addListener(this::notifyFinished, MoreExecutors.directExecutor());
    }

    @Override
    public boolean isCancelled() {
        this.checkInitialized();
        return this.future.isCancelled();
    }

    @Override
    public boolean isDone() {
        this.checkInitialized();
        return this.future.isDone();
    }

    @Override
    public void updateProgress(int percentage, @Nonnull String message) {
        this.progressMessage = Objects.requireNonNull(message);
        this.progressPercentage = percentage;
        this.notifyUpdated();
    }

    @Override
    public Map<String, Serializable> getAttributes() {
        RestTaskStatus attributes = new RestTaskStatus(this);
        this.addCustomAttributes(attributes);
        return attributes;
    }

    protected void addCustomAttributes(@Nonnull Map<String, Serializable> attributesToUpdate) {
    }

    @Override
    public void setCustomAttributes(@Nonnull Map<String, Serializable> attributesToRead) {
    }

    @Override
    public Optional<String> getNodeId() {
        return Optional.ofNullable(this.nodeId);
    }

    @Override
    public void setNodeId(@Nonnull String nodeId) {
        this.nodeId = nodeId;
    }

    protected void notifyFinished() {
        for (TaskMonitorListener<V> listener : this.listeners) {
            try {
                listener.onFinished(this);
            }
            catch (Exception e) {
                LOG.warn("Error while notifying TaskMonitorListener {}", (Object)listener.getClass().getName(), (Object)e);
            }
        }
    }

    protected void notifyUpdated() {
        for (TaskMonitorListener<V> listener : this.listeners) {
            try {
                listener.onUpdated(this);
            }
            catch (Exception e) {
                LOG.warn("Error while notifying TaskMonitorListener {}", (Object)listener.getClass().getName(), (Object)e);
            }
        }
    }

    private void checkInitialized() {
        Preconditions.checkState((this.future != null ? 1 : 0) != 0, (Object)"Monitor hasn't been initialized");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        boolean done = in.readBoolean();
        boolean cancelled = done && in.readBoolean();
        Object result = done && !cancelled ? in.readObject() : null;
        this.future = new RemoteFuture<V>(cancelled, done, result);
        this.listeners = new CopyOnWriteArrayList<TaskMonitorListener<V>>();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        boolean done = this.future.isDone();
        out.writeBoolean(done);
        if (done) {
            boolean cancelled = this.future.isCancelled();
            out.writeBoolean(cancelled);
            if (!cancelled) {
                try {
                    V result = this.future.get();
                    out.writeObject(result);
                }
                catch (ExecutionException e) {
                    out.writeObject(e.getCause());
                }
                catch (InterruptedException e) {
                    LOG.warn("interrupted while writing TaskMonitor state to the cluster");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static class RemoteFuture<T>
    implements Future<T> {
        private final boolean cancelled;
        private final boolean done;
        private final Throwable exception;
        private final T result;

        private RemoteFuture(boolean cancelled, boolean done, Object result) {
            this.cancelled = cancelled;
            this.done = done;
            if (result instanceof Throwable) {
                this.exception = (Throwable)result;
                this.result = null;
            } else {
                this.exception = null;
                this.result = result;
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException("cannot cancel a remotely running task");
        }

        @Override
        public boolean isCancelled() {
            return this.cancelled;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public T get() throws ExecutionException {
            if (this.cancelled) {
                throw new CancellationException();
            }
            if (!this.done) {
                throw new UnsupportedOperationException("cannot retrieve results of remotely running task");
            }
            if (this.exception != null) {
                throw new ExecutionException(this.exception);
            }
            return this.result;
        }

        @Override
        public T get(long timeout, @Nonnull TimeUnit unit) throws ExecutionException, TimeoutException {
            if (this.cancelled) {
                throw new CancellationException();
            }
            if (!this.done) {
                throw new TimeoutException("cannot retrieve results of remotely running task");
            }
            if (this.exception != null) {
                throw new ExecutionException(this.exception);
            }
            return this.result;
        }
    }
}

