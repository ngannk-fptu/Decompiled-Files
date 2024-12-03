/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.jira.application.ApplicationRoleManager
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.applinks.spring;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.jira.application.ApplicationRoleManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.CrowdOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.FecruOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlassianPluginsComponentImports {
    @Bean
    @Conditional(value={JiraOnly.class})
    public FactoryBean<ApplicationRoleManager> applicationRoleManager() {
        return OsgiServices.factoryBeanForOsgiService(ApplicationRoleManager.class);
    }

    @Bean
    @Conditional(value={BambooOnly.class})
    public FactoryBean<InternalHostApplication> bambooInternalHostApplication() {
        return OsgiServices.factoryBeanForOsgiService(InternalHostApplication.class);
    }

    @Bean
    @Conditional(value={BitbucketOnly.class})
    public FactoryBean<InternalHostApplication> bitbucketInternalHostApplication() {
        return OsgiServices.factoryBeanForOsgiService(InternalHostApplication.class);
    }

    @Bean
    @Conditional(value={ConfluenceOnly.class})
    public FactoryBean<InternalHostApplication> confluenceInternalHostApplication() {
        return OsgiServices.factoryBeanForOsgiService(InternalHostApplication.class);
    }

    @Bean
    @Conditional(value={CrowdOnly.class})
    public FactoryBean<InternalHostApplication> crowdInternalHostApplication() {
        return OsgiServices.factoryBeanForOsgiService(InternalHostApplication.class);
    }

    @Bean
    @Conditional(value={FecruOnly.class})
    public FactoryBean<InternalHostApplication> fecruInternalHostApplication() {
        return OsgiServices.factoryBeanForOsgiService(InternalHostApplication.class);
    }

    @Bean
    @Conditional(value={JiraOnly.class})
    public FactoryBean<InternalHostApplication> jiraInternalHostApplication() {
        return OsgiServices.factoryBeanForOsgiService(InternalHostApplication.class);
    }
}

