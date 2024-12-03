/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.applinks.trusted.spring;

import com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector;
import com.atlassian.applinks.trusted.auth.TrustedAppsOrphanedTrustDetector;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlassianPluginsComponents {
    @Bean
    public FactoryBean<ServiceRegistration> exportTrustedAppsOrphanedTrustDetector(TrustedAppsOrphanedTrustDetector trustedAppsOrphanedTrustDetector) {
        return OsgiServices.exportOsgiService(trustedAppsOrphanedTrustDetector, ExportOptions.as(InternalOrphanedTrustDetector.class, new Class[0]));
    }
}

