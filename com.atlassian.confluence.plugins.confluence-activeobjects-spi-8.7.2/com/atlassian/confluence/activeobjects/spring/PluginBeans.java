/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.DefaultInitExecutorServiceProvider
 *  com.atlassian.activeobjects.spi.InitExecutorServiceProvider
 *  com.atlassian.activeobjects.spi.TransactionSynchronisationManager
 *  com.atlassian.confluence.core.SynchronizationManager
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.activeobjects.spring;

import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DefaultInitExecutorServiceProvider;
import com.atlassian.activeobjects.spi.InitExecutorServiceProvider;
import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;
import com.atlassian.confluence.activeobjects.ConfluenceInitExecutorServiceProvider;
import com.atlassian.confluence.activeobjects.transaction.ConfluenceAOSynchronisationManager;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ParametersAreNonnullByDefault
public class PluginBeans {
    @Bean
    DefaultInitExecutorServiceProvider defaultInitExecutorServiceProvider(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        return new DefaultInitExecutorServiceProvider(threadLocalDelegateExecutorFactory);
    }

    @Bean
    InitExecutorServiceProvider initExecutorServiceProvider(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, DataSourceProvider dataSourceProvider, InitExecutorServiceProvider defaultInitExecutorServiceProvider) {
        return new ConfluenceInitExecutorServiceProvider(threadLocalDelegateExecutorFactory, dataSourceProvider, defaultInitExecutorServiceProvider);
    }

    @Bean
    TransactionSynchronisationManager transactionSynchronisationManager(SynchronizationManager synchManager) {
        return new ConfluenceAOSynchronisationManager(synchManager);
    }
}

