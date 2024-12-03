/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ScheduledExecutorFactoryBean
extends ExecutorConfigurationSupport
implements FactoryBean<ScheduledExecutorService> {
    private int poolSize = 1;
    @Nullable
    private ScheduledExecutorTask[] scheduledExecutorTasks;
    private boolean removeOnCancelPolicy = false;
    private boolean continueScheduledExecutionAfterException = false;
    private boolean exposeUnconfigurableExecutor = false;
    @Nullable
    private ScheduledExecutorService exposedExecutor;

    public void setPoolSize(int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        this.poolSize = poolSize;
    }

    public void setScheduledExecutorTasks(ScheduledExecutorTask ... scheduledExecutorTasks) {
        this.scheduledExecutorTasks = scheduledExecutorTasks;
    }

    public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
        this.removeOnCancelPolicy = removeOnCancelPolicy;
    }

    public void setContinueScheduledExecutionAfterException(boolean continueScheduledExecutionAfterException) {
        this.continueScheduledExecutionAfterException = continueScheduledExecutionAfterException;
    }

    public void setExposeUnconfigurableExecutor(boolean exposeUnconfigurableExecutor) {
        this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        ScheduledExecutorService executor = this.createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        if (this.removeOnCancelPolicy) {
            if (executor instanceof ScheduledThreadPoolExecutor) {
                ((ScheduledThreadPoolExecutor)executor).setRemoveOnCancelPolicy(true);
            } else {
                this.logger.info("Could not apply remove-on-cancel policy - not a ScheduledThreadPoolExecutor");
            }
        }
        if (!ObjectUtils.isEmpty(this.scheduledExecutorTasks)) {
            this.registerTasks(this.scheduledExecutorTasks, executor);
        }
        this.exposedExecutor = this.exposeUnconfigurableExecutor ? Executors.unconfigurableScheduledExecutorService(executor) : executor;
        return executor;
    }

    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }

    protected void registerTasks(ScheduledExecutorTask[] tasks, ScheduledExecutorService executor) {
        for (ScheduledExecutorTask task : tasks) {
            Runnable runnable = this.getRunnableToSchedule(task);
            if (task.isOneTimeTask()) {
                executor.schedule(runnable, task.getDelay(), task.getTimeUnit());
                continue;
            }
            if (task.isFixedRate()) {
                executor.scheduleAtFixedRate(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
                continue;
            }
            executor.scheduleWithFixedDelay(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
        }
    }

    protected Runnable getRunnableToSchedule(ScheduledExecutorTask task) {
        return this.continueScheduledExecutionAfterException ? new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER) : new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
    }

    @Override
    @Nullable
    public ScheduledExecutorService getObject() {
        return this.exposedExecutor;
    }

    @Override
    public Class<? extends ScheduledExecutorService> getObjectType() {
        return this.exposedExecutor != null ? this.exposedExecutor.getClass() : ScheduledExecutorService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

