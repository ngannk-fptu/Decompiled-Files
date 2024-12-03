/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.activeobjects.spi.DefaultInitExecutorServiceProvider
 *  com.atlassian.activeobjects.spi.InitExecutorServiceProvider
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.tenancy.api.Tenant
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.activeobjects;

import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.activeobjects.spi.DefaultInitExecutorServiceProvider;
import com.atlassian.activeobjects.spi.InitExecutorServiceProvider;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.tenancy.api.Tenant;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceInitExecutorServiceProvider
implements InitExecutorServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultInitExecutorServiceProvider.class);
    private final DataSourceProvider dataSourceProvider;
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    @VisibleForTesting
    final InitExecutorServiceProvider defaultInitExecutorServiceProvider;

    public ConfluenceInitExecutorServiceProvider(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, DataSourceProvider dataSourceProvider, InitExecutorServiceProvider defaultInitExecutorServiceProvider) {
        this.threadLocalDelegateExecutorFactory = Objects.requireNonNull(threadLocalDelegateExecutorFactory);
        this.dataSourceProvider = Objects.requireNonNull(dataSourceProvider);
        this.defaultInitExecutorServiceProvider = Objects.requireNonNull(defaultInitExecutorServiceProvider);
    }

    @Nonnull
    public ExecutorService initExecutorService(@Nonnull Tenant tenant) {
        DatabaseType databaseType = this.dataSourceProvider.getDatabaseType();
        if (databaseType == DatabaseType.HSQL) {
            logger.debug("creating HSQL snowflake init executor");
            return this.threadLocalDelegateExecutorFactory.createExecutorService((ExecutorService)MoreExecutors.newDirectExecutorService());
        }
        return this.defaultInitExecutorServiceProvider.initExecutorService();
    }
}

