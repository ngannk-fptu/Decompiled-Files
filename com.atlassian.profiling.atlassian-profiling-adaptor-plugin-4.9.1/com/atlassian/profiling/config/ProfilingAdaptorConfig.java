/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.profiling.metrics.api.context.MetricContext
 *  com.atlassian.profiling.metrics.api.tags.TagFactoryFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.profiling.config;

import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.profiling.metrics.api.context.MetricContext;
import com.atlassian.profiling.metrics.api.tags.TagFactoryFactory;
import com.atlassian.profiling.metrics.context.MetricContextAdaptor;
import com.atlassian.profiling.metrics.tags.TagFactoryFactoryImpl;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfilingAdaptorConfig {
    @Bean
    public MetricContext metricContext() {
        return new MetricContextAdaptor();
    }

    @Bean
    public TagFactoryFactory tagFactoryFactory() {
        return new TagFactoryFactoryImpl();
    }

    @Bean
    public FactoryBean<ServiceRegistration> registerMetricContext(MetricContext metricContext) {
        return OsgiServices.exportOsgiService(metricContext, ExportOptions.as(MetricContext.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> registerTagFactoryFactory(TagFactoryFactory tagFactoryFactory) {
        return OsgiServices.exportOsgiService(tagFactoryFactory, ExportOptions.as(TagFactoryFactory.class, new Class[0]));
    }
}

