/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.ResizableExecutor
 *  org.apache.tomcat.util.threads.TaskQueue
 *  org.apache.tomcat.util.threads.TaskThreadFactory
 *  org.apache.tomcat.util.threads.ThreadPoolExecutor
 */
package org.apache.catalina.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ResizableExecutor;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

public class StandardThreadExecutor
extends LifecycleMBeanBase
implements Executor,
ResizableExecutor {
    protected static final StringManager sm = StringManager.getManager(StandardThreadExecutor.class);
    protected int threadPriority = 5;
    protected boolean daemon = true;
    protected String namePrefix = "tomcat-exec-";
    protected int maxThreads = 200;
    protected int minSpareThreads = 25;
    protected int maxIdleTime = 60000;
    protected ThreadPoolExecutor executor = null;
    protected String name;
    protected int maxQueueSize = Integer.MAX_VALUE;
    protected long threadRenewalDelay = 1000L;
    private TaskQueue taskqueue = null;

    @Override
    protected void startInternal() throws LifecycleException {
        this.taskqueue = new TaskQueue(this.maxQueueSize);
        TaskThreadFactory tf = new TaskThreadFactory(this.namePrefix, this.daemon, this.getThreadPriority());
        this.executor = new ThreadPoolExecutor(this.getMinSpareThreads(), this.getMaxThreads(), (long)this.maxIdleTime, TimeUnit.MILLISECONDS, (BlockingQueue)this.taskqueue, (ThreadFactory)tf);
        this.executor.setThreadRenewalDelay(this.threadRenewalDelay);
        this.taskqueue.setParent(this.executor);
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        this.executor = null;
        this.taskqueue = null;
    }

    @Override
    @Deprecated
    public void execute(Runnable command, long timeout, TimeUnit unit) {
        if (this.executor == null) {
            throw new IllegalStateException(sm.getString("standardThreadExecutor.notStarted"));
        }
        this.executor.execute(command, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        if (this.executor == null) {
            throw new IllegalStateException(sm.getString("standardThreadExecutor.notStarted"));
        }
        this.executor.execute(command);
    }

    public void contextStopping() {
        if (this.executor != null) {
            this.executor.contextStopping();
        }
    }

    public int getThreadPriority() {
        return this.threadPriority;
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    public int getMaxThreads() {
        return this.maxThreads;
    }

    public int getMinSpareThreads() {
        return this.minSpareThreads;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        if (this.executor != null) {
            this.executor.setKeepAliveTime((long)maxIdleTime, TimeUnit.MILLISECONDS);
        }
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        if (this.executor != null) {
            this.executor.setMaximumPoolSize(maxThreads);
        }
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        if (this.executor != null) {
            this.executor.setCorePoolSize(minSpareThreads);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxQueueSize(int size) {
        this.maxQueueSize = size;
    }

    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }

    public long getThreadRenewalDelay() {
        return this.threadRenewalDelay;
    }

    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
        if (this.executor != null) {
            this.executor.setThreadRenewalDelay(threadRenewalDelay);
        }
    }

    public int getActiveCount() {
        return this.executor != null ? this.executor.getActiveCount() : 0;
    }

    public long getCompletedTaskCount() {
        return this.executor != null ? this.executor.getCompletedTaskCount() : 0L;
    }

    public int getCorePoolSize() {
        return this.executor != null ? this.executor.getCorePoolSize() : 0;
    }

    public int getLargestPoolSize() {
        return this.executor != null ? this.executor.getLargestPoolSize() : 0;
    }

    public int getPoolSize() {
        return this.executor != null ? this.executor.getPoolSize() : 0;
    }

    public int getQueueSize() {
        return this.executor != null ? this.executor.getQueue().size() : -1;
    }

    public boolean resizePool(int corePoolSize, int maximumPoolSize) {
        if (this.executor == null) {
            return false;
        }
        this.executor.setCorePoolSize(corePoolSize);
        this.executor.setMaximumPoolSize(maximumPoolSize);
        return true;
    }

    public boolean resizeQueue(int capacity) {
        return false;
    }

    @Override
    protected String getDomainInternal() {
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "type=Executor,name=" + this.getName();
    }
}

