/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.task;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.core.task.FifoBuffer;
import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;
import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskQueue
implements TaskQueue {
    @VisibleForTesting
    protected static final String DEFAULT_QUEUE_NAME = AbstractTaskQueue.class.getSimpleName();
    @VisibleForTesting
    protected static final String METRIC_NAME = "task";
    @VisibleForTesting
    protected static final String QUEUE_NAME_TAG = "queueName";
    private static final transient Logger log = LoggerFactory.getLogger(AbstractTaskQueue.class);
    private final String queueName;
    private final Map<Task, String> workCreators;
    protected FifoBuffer<Task> buffer;
    private boolean flushing;
    private Timestamp flushStarted;

    @Deprecated
    public AbstractTaskQueue(@Nonnull FifoBuffer<Task> buffer) {
        this(buffer, DEFAULT_QUEUE_NAME);
    }

    public AbstractTaskQueue(@Nonnull FifoBuffer<Task> buffer, @Nullable String queueName) {
        this.buffer = buffer;
        this.queueName = queueName;
        this.workCreators = new WeakHashMap<Task, String>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flush() {
        block18: {
            if (this.flushing) {
                return;
            }
            this.flushing = true;
            this.flushStarted = new Timestamp(System.currentTimeMillis());
            block14: while (true) {
                Task task;
                while ((task = this.buffer.remove()) != null) {
                    log.debug("Executing: " + task);
                    try {
                        Ticker ignored = Metrics.metric((String)METRIC_NAME).tag(QUEUE_NAME_TAG, this.queueName).invokerPluginKey(this.workCreators.get(task)).withAnalytics().startTimer();
                        Throwable throwable = null;
                        try {
                            task.execute();
                            continue block14;
                        }
                        catch (Throwable throwable2) {
                            throwable = throwable2;
                            throw throwable2;
                        }
                        finally {
                            if (ignored == null) continue block14;
                            if (throwable != null) {
                                try {
                                    ignored.close();
                                }
                                catch (Throwable throwable3) {
                                    throwable.addSuppressed(throwable3);
                                }
                                continue block14;
                            }
                            ignored.close();
                            continue block14;
                        }
                    }
                    catch (Exception e) {
                        this.handleException(task, e);
                    }
                }
                break block18;
                {
                    continue block14;
                    break;
                }
                break;
            }
            finally {
                this.flushing = false;
                this.flushStarted = null;
            }
        }
    }

    protected void handleException(Task task, Exception e) {
        log.error("Failed to execute task : " + task, (Throwable)e);
    }

    public String getQueueName() {
        return this.queueName;
    }

    @Override
    public int size() {
        return this.buffer.size();
    }

    @Override
    public void addTask(Task task) {
        log.debug("Queued: " + task);
        this.buffer.add(task);
        this.workCreators.put(task, PluginKeyStack.getFirstPluginKey());
    }

    public Collection<Task> getQueue() {
        return this.buffer.getItems();
    }

    @Override
    public boolean isFlushing() {
        return this.flushing;
    }

    @Override
    public Timestamp getFlushStarted() {
        return this.flushStarted;
    }

    @Override
    public void clear() {
        this.buffer.clear();
    }

    @Override
    public Collection<Task> getTasks() {
        return this.buffer.getItems();
    }
}

