/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.tenancy.api.Tenant
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.ContextClassLoaderThreadFactory;
import com.atlassian.activeobjects.spi.InitExecutorServiceProvider;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.tenancy.api.Tenant;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultInitExecutorServiceProvider
implements InitExecutorServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultInitExecutorServiceProvider.class);
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    @VisibleForTesting
    final ThreadFactory aoContextThreadFactory;

    public DefaultInitExecutorServiceProvider(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.threadLocalDelegateExecutorFactory = Objects.requireNonNull(threadLocalDelegateExecutorFactory);
        ClassLoader bundleContextClassLoader = Thread.currentThread().getContextClassLoader();
        this.aoContextThreadFactory = new ContextClassLoaderThreadFactory(bundleContextClassLoader);
    }

    @Override
    @Nonnull
    public ExecutorService initExecutorService(@Nonnull Tenant tenant) {
        logger.debug("creating default init executor");
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setThreadFactory(this.aoContextThreadFactory).setNameFormat("active-objects-init-%d").build();
        int threadPoolSize = Integer.getInteger("activeobjects.servicefactory.ddl.threadpoolsize", 1);
        ExecutorService delegate = Executors.newFixedThreadPool(threadPoolSize, threadFactory);
        return this.threadLocalDelegateExecutorFactory.createExecutorService(delegate);
    }
}

