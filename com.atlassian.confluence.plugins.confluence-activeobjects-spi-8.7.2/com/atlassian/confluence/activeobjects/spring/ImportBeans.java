/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.confluence.core.SynchronizationManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.tenancy.api.TenantAccessor
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.activeobjects.spring;

import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.tenancy.api.TenantAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportBeans {
    @Bean
    EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    SynchronizationManager synchronizationManager() {
        return OsgiServices.importOsgiService(SynchronizationManager.class);
    }

    @Bean
    TenantAccessor tenantAccessor() {
        return OsgiServices.importOsgiService(TenantAccessor.class);
    }

    @Bean
    DataSourceProvider dataSourceProvider() {
        return OsgiServices.importOsgiService(DataSourceProvider.class);
    }

    @Bean
    ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory() {
        return OsgiServices.importOsgiService(ThreadLocalDelegateExecutorFactory.class);
    }

    @Bean
    TransactionTemplate transactionTemplate() {
        return OsgiServices.importOsgiService(TransactionTemplate.class);
    }
}

