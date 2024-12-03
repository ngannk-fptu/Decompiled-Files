/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.quartz.SchedulerConfigException
 *  org.quartz.spi.ThreadPool
 */
package org.springframework.scheduling.quartz;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerConfigException;
import org.quartz.spi.ThreadPool;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;

public class LocalTaskExecutorThreadPool
implements ThreadPool {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Executor taskExecutor;

    public void setInstanceId(String schedInstId) {
    }

    public void setInstanceName(String schedName) {
    }

    public void initialize() throws SchedulerConfigException {
        this.taskExecutor = SchedulerFactoryBean.getConfigTimeTaskExecutor();
        if (this.taskExecutor == null) {
            throw new SchedulerConfigException("No local Executor found for configuration - 'taskExecutor' property must be set on SchedulerFactoryBean");
        }
    }

    public void shutdown(boolean waitForJobsToComplete) {
    }

    public int getPoolSize() {
        return -1;
    }

    public boolean runInThread(Runnable runnable) {
        Assert.state(this.taskExecutor != null, "No TaskExecutor available");
        try {
            this.taskExecutor.execute(runnable);
            return true;
        }
        catch (RejectedExecutionException ex) {
            this.logger.error((Object)"Task has been rejected by TaskExecutor", (Throwable)ex);
            return false;
        }
    }

    public int blockForAvailableThreads() {
        return 1;
    }
}

