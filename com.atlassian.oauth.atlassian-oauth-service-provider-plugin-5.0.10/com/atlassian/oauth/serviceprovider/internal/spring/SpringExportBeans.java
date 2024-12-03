/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth.serviceprovider.internal.spring;

import com.atlassian.oauth.serviceprovider.internal.ExpiredSessionRemover;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringExportBeans {
    @Bean
    public FactoryBean<ServiceRegistration> exportExpiredSessionRemover(ExpiredSessionRemover expiredSessionRemover) {
        return OsgiServices.exportOsgiService((Object)expiredSessionRemover, (ExportOptions)ExportOptions.as(LifecycleAware.class, (Class[])new Class[0]));
    }
}

