/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.db.HibernateConfigurator
 *  com.atlassian.config.setup.SetupPersister
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.jmx.support.MBeanServerFactoryBean
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.db.HibernateConfigurator;
import com.atlassian.config.setup.SetupPersister;
import com.atlassian.confluence.core.ConfluenceSynchronizationManager;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.impl.setup.LicenseServiceBootstrapAppConfig;
import com.atlassian.confluence.impl.util.tomcat.DefaultTomcatConfigHelper;
import com.atlassian.confluence.setup.DefaultHibernateConfigurator;
import com.atlassian.confluence.setup.DefaultSetupPersister;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.management.MBeanServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.MBeanServerFactoryBean;

@Configuration
@Import(value={LicenseServiceBootstrapAppConfig.class})
public class BootstrapCommonAppConfig {
    @Bean
    @AvailableToPlugins(interfaces={TomcatConfigHelper.class})
    TomcatConfigHelper tomcatConfigHelper(MBeanServer mbeanServer) {
        return new DefaultTomcatConfigHelper(mbeanServer);
    }

    @Bean
    HibernateConfigurator hibernateConfigurator() {
        return new DefaultHibernateConfigurator();
    }

    @Bean
    @AvailableToPlugins
    SynchronizationManager synchronizationManager() {
        return new ConfluenceSynchronizationManager();
    }

    @Bean
    MBeanServerFactoryBean mbeanServer() {
        MBeanServerFactoryBean factory = new MBeanServerFactoryBean();
        factory.setLocateExistingServerIfPossible(true);
        return factory;
    }

    @Bean
    @AvailableToPlugins
    HttpContext httpContext() {
        return new StaticHttpContext();
    }

    @Bean
    SetupPersister setupPersister(ApplicationConfiguration applicationConfig) {
        DefaultSetupPersister bean = new DefaultSetupPersister();
        bean.setApplicationConfig(applicationConfig);
        return bean;
    }
}

