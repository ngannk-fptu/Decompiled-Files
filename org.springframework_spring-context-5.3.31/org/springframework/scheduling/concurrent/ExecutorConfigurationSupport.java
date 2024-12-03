/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public abstract class ExecutorConfigurationSupport
extends CustomizableThreadFactory
implements BeanNameAware,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private ThreadFactory threadFactory = this;
    private boolean threadNamePrefixSet = false;
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    private boolean waitForTasksToCompleteOnShutdown = false;
    private long awaitTerminationMillis = 0L;
    @Nullable
    private String beanName;
    @Nullable
    private ExecutorService executor;

    public void setThreadFactory(@Nullable ThreadFactory threadFactory) {
        this.threadFactory = threadFactory != null ? threadFactory : this;
    }

    public void setThreadNamePrefix(@Nullable String threadNamePrefix) {
        super.setThreadNamePrefix(threadNamePrefix);
        this.threadNamePrefixSet = true;
    }

    public void setRejectedExecutionHandler(@Nullable RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler != null ? rejectedExecutionHandler : new ThreadPoolExecutor.AbortPolicy();
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationMillis = (long)awaitTerminationSeconds * 1000L;
    }

    public void setAwaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void afterPropertiesSet() {
        this.initialize();
    }

    public void initialize() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Initializing ExecutorService" + (this.beanName != null ? " '" + this.beanName + "'" : "")));
        }
        if (!this.threadNamePrefixSet && this.beanName != null) {
            this.setThreadNamePrefix(this.beanName + "-");
        }
        this.executor = this.initializeExecutor(this.threadFactory, this.rejectedExecutionHandler);
    }

    protected abstract ExecutorService initializeExecutor(ThreadFactory var1, RejectedExecutionHandler var2);

    public void destroy() {
        this.shutdown();
    }

    public void shutdown() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Shutting down ExecutorService" + (this.beanName != null ? " '" + this.beanName + "'" : "")));
        }
        if (this.executor != null) {
            if (this.waitForTasksToCompleteOnShutdown) {
                this.executor.shutdown();
            } else {
                for (Runnable remainingTask : this.executor.shutdownNow()) {
                    this.cancelRemainingTask(remainingTask);
                }
            }
            this.awaitTerminationIfNecessary(this.executor);
        }
    }

    protected void cancelRemainingTask(Runnable task) {
        if (task instanceof Future) {
            ((Future)((Object)task)).cancel(true);
        }
    }

    private void awaitTerminationIfNecessary(ExecutorService executor) {
        if (this.awaitTerminationMillis > 0L) {
            try {
                if (!executor.awaitTermination(this.awaitTerminationMillis, TimeUnit.MILLISECONDS) && this.logger.isWarnEnabled()) {
                    this.logger.warn((Object)("Timed out while waiting for executor" + (this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate"));
                }
            }
            catch (InterruptedException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn((Object)("Interrupted while waiting for executor" + (this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate"));
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}

