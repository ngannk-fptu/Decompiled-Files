/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.caesium.impl.CaesiumSchedulerService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WorkerThreadFactory
implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(WorkerThreadFactory.class);
    private static final AtomicInteger FACTORY_COUNTER = new AtomicInteger();
    private static final ClassLoader CLASS_LOADER = CaesiumSchedulerService.class.getClassLoader();
    private final String groupName = "Caesium-" + FACTORY_COUNTER.incrementAndGet();
    private final ThreadGroup group = new ThreadGroup(this.groupName);
    private final AtomicInteger threadCounter = new AtomicInteger();

    WorkerThreadFactory() {
    }

    @Override
    @Nonnull
    public Thread newThread(Runnable runnable) {
        String name = this.group.getName() + '-' + this.threadCounter.incrementAndGet();
        Thread thd = new Thread(this.group, runnable, name);
        thd.setDaemon(true);
        thd.setContextClassLoader(CLASS_LOADER);
        LOG.debug("Creating new worker: {}", (Object)thd);
        return thd;
    }
}

