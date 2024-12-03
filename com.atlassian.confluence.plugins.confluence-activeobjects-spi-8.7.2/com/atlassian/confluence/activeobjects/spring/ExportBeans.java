/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.InitExecutorServiceProvider
 *  com.atlassian.activeobjects.spi.TransactionSynchronisationManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.activeobjects.spring;

import com.atlassian.activeobjects.spi.InitExecutorServiceProvider;
import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import javax.annotation.ParametersAreNonnullByDefault;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ParametersAreNonnullByDefault
public class ExportBeans {
    @Bean
    FactoryBean<ServiceRegistration> exportInitExecutorServiceProvider(@Qualifier(value="initExecutorServiceProvider") InitExecutorServiceProvider initExecutorServiceProvider) {
        return OsgiServices.exportOsgiService(initExecutorServiceProvider, ExportOptions.as(InitExecutorServiceProvider.class, new Class[0]));
    }

    @Bean
    FactoryBean<ServiceRegistration> exportTransactionSynchronisationManager(TransactionSynchronisationManager transactionSynchronisationManager) {
        return OsgiServices.exportOsgiService(transactionSynchronisationManager, ExportOptions.as(TransactionSynchronisationManager.class, new Class[0]));
    }
}

