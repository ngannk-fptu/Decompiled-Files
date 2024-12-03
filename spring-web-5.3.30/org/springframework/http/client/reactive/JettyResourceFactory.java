/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.io.MappedByteBufferPool
 *  org.eclipse.jetty.util.ProcessorUtils
 *  org.eclipse.jetty.util.component.LifeCycle
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.ThreadPool$SizedThreadPool
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.client.reactive;

import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JettyResourceFactory
implements InitializingBean,
DisposableBean {
    @Nullable
    private Executor executor;
    @Nullable
    private ByteBufferPool byteBufferPool;
    @Nullable
    private Scheduler scheduler;
    private String threadPrefix = "jetty-http";

    public void setExecutor(@Nullable Executor executor) {
        this.executor = executor;
    }

    public void setByteBufferPool(@Nullable ByteBufferPool byteBufferPool) {
        this.byteBufferPool = byteBufferPool;
    }

    public void setScheduler(@Nullable Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setThreadPrefix(String threadPrefix) {
        Assert.notNull((Object)threadPrefix, (String)"Thread prefix is required");
        this.threadPrefix = threadPrefix;
    }

    @Nullable
    public Executor getExecutor() {
        return this.executor;
    }

    @Nullable
    public ByteBufferPool getByteBufferPool() {
        return this.byteBufferPool;
    }

    @Nullable
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public void afterPropertiesSet() throws Exception {
        String name = this.threadPrefix + "@" + Integer.toHexString(this.hashCode());
        if (this.executor == null) {
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName(name);
            this.executor = threadPool;
        }
        if (this.byteBufferPool == null) {
            this.byteBufferPool = new MappedByteBufferPool(2048, this.executor instanceof ThreadPool.SizedThreadPool ? ((ThreadPool.SizedThreadPool)this.executor).getMaxThreads() / 2 : ProcessorUtils.availableProcessors() * 2);
        }
        if (this.scheduler == null) {
            this.scheduler = new ScheduledExecutorScheduler(name + "-scheduler", false);
        }
        if (this.executor instanceof LifeCycle) {
            ((LifeCycle)this.executor).start();
        }
        this.scheduler.start();
    }

    public void destroy() throws Exception {
        try {
            if (this.executor instanceof LifeCycle) {
                ((LifeCycle)this.executor).stop();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            if (this.scheduler != null) {
                this.scheduler.stop();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

