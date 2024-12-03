/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.upm.api.license.DataCenterCrossgradeablePlugins;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.PluginLicenseEventRegistry;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.RemotePluginLicenseService;
import com.atlassian.upm.api.log.PluginLogService;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherServiceFactory;
import com.atlassian.upm.license.internal.impl.PluginLicenseManagerServiceFactory;
import com.atlassian.upm.license.internal.impl.remote.RemotePluginLicenseServiceServiceFactory;
import com.atlassian.upm.lifecycle.UpmLifecycleManager;
import com.atlassian.upm.log.ApplicationLifecycleLogger;
import com.atlassian.upm.log.AuditLogUpgradeTask;
import com.atlassian.upm.upgrade.UserSettingsUpgradeTask;
import java.util.Arrays;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniversalOsgiServiceExports {
    private static final Logger log = LoggerFactory.getLogger(UniversalOsgiServiceExports.class);

    @Bean
    public FactoryBean<ServiceRegistration> applicationLifecycleLoggerServiceExport(ApplicationLifecycleLogger applicationLifecycleLogger) {
        return OsgiServices.exportOsgiService(applicationLifecycleLogger, ExportOptions.as(LifecycleAware.class, ApplicationLifecycleLogger.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> auditLogUpgradeTaskServiceExport(AuditLogUpgradeTask auditLogUpgradeTask) {
        return OsgiServices.exportOsgiService(auditLogUpgradeTask, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> dataCenterCrossgradeablePluginsServiceExport(DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins) {
        return OsgiServices.exportOsgiService(dataCenterCrossgradeablePlugins, ExportOptions.as(DataCenterCrossgradeablePlugins.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> hostLicenseInformationServiceExport(HostLicenseInformation hostLicenseInformation) {
        return OsgiServices.exportOsgiService(hostLicenseInformation, ExportOptions.as(HostLicenseInformation.class, new Class[0]));
    }

    @Bean
    public ServiceRegistration<?> pluginLicenseEventPublisherServiceFactoryRegistration(BundleContext bundleContext, PluginLicenseEventPublisherServiceFactory pluginLicenseEventPublisherServiceFactory) {
        return UniversalOsgiServiceExports.registerServiceFactory(bundleContext, pluginLicenseEventPublisherServiceFactory, PluginLicenseEventRegistry.class);
    }

    @Bean
    public ServiceRegistration<?> pluginLicenseManagerServiceFactoryRegistration(BundleContext bundleContext, PluginLicenseManagerServiceFactory pluginLicenseManagerServiceFactory) {
        return UniversalOsgiServiceExports.registerServiceFactory(bundleContext, pluginLicenseManagerServiceFactory, PluginLicenseManager.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> pluginLogServiceExport(PluginLogService pluginLogService) {
        return OsgiServices.exportOsgiService(pluginLogService, ExportOptions.as(PluginLogService.class, new Class[0]));
    }

    @Bean
    public ServiceRegistration<?> remotePluginLicenseServiceServiceFactoryRegistration(BundleContext bundleContext, RemotePluginLicenseServiceServiceFactory remotePluginLicenseServiceServiceFactory) {
        return UniversalOsgiServiceExports.registerServiceFactory(bundleContext, remotePluginLicenseServiceServiceFactory, RemotePluginLicenseService.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> upmLifecycleManagerServiceExport(UpmLifecycleManager upmLifecycleManager) {
        return OsgiServices.exportOsgiService(upmLifecycleManager, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> userSettingsUpgradeTaskServiceExport(UserSettingsUpgradeTask userSettingsUpgradeTask) {
        return OsgiServices.exportOsgiService(userSettingsUpgradeTask, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    private static ServiceRegistration<?> registerServiceFactory(BundleContext bundleContext, ServiceFactory<?> serviceFactory, Class<?> ... interfaces) {
        Object[] interfaceNames = (String[])Arrays.stream(interfaces).map(Class::getName).toArray(String[]::new);
        log.debug("Publishing service under classes [{}]", (Object)Arrays.toString(interfaceNames));
        return bundleContext.registerService((String[])interfaceNames, serviceFactory, new Hashtable());
    }
}

