/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.analytics.api.services.AnalyticsUploadService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.shared;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.analytics.api.services.AnalyticsUploadService;
import com.atlassian.analytics.client.detect.PrivacyPolicyUpdateDetector;
import com.atlassian.analytics.client.hash.AnalyticsEmailHasher;
import com.atlassian.analytics.client.upload.UploadAnalyticsInitialiser;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedExports {
    @Bean
    public FactoryBean<ServiceRegistration> exportAnalyticsConfigService(AnalyticsConfigService analyticsConfigService) {
        return OsgiServices.exportOsgiService(analyticsConfigService, null, AnalyticsConfigService.class, new Class[0]);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAnalyticsEmailHasher(AnalyticsEmailHasher analyticsEmailHasher) {
        return OsgiServices.exportOsgiService(analyticsEmailHasher, null, AnalyticsEmailHasher.class, new Class[0]);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAnalyticsUploadService(AnalyticsUploadService analyticsUploadService) {
        return OsgiServices.exportOsgiService(analyticsUploadService, null, AnalyticsUploadService.class, new Class[0]);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportPrivacyPolicyUpdateDetector(PrivacyPolicyUpdateDetector privacyPolicyUpdateDetector) {
        return SharedExports.exportAsLifecycleAware(privacyPolicyUpdateDetector);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportProductAnalyticsEventListener(ProductAnalyticsEventListener productAnalyticsEventListener) {
        return OsgiServices.exportOsgiService(productAnalyticsEventListener, null, ProductAnalyticsEventListener.class, new Class[0]);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportUploadAnalyticsInitialiser(UploadAnalyticsInitialiser uploadAnalyticsInitialiser) {
        return SharedExports.exportAsLifecycleAware(uploadAnalyticsInitialiser);
    }

    public static FactoryBean<ServiceRegistration> exportAsLifecycleAware(Object bean) {
        return OsgiServices.exportOsgiService(bean, null, LifecycleAware.class, new Class[0]);
    }
}

