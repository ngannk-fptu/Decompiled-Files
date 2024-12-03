/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.troubleshooting.spring;

import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.CurrentPluginInfo;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.api.WebResourcesService;
import com.atlassian.troubleshooting.api.healthcheck.OptionalServiceProvider;
import com.atlassian.troubleshooting.cluster.DefaultClusterMessagingService;
import com.atlassian.troubleshooting.cluster.DefaultJsonSerialiser;
import com.atlassian.troubleshooting.healthcheck.impl.DbVersionExtractor;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultDbPlatformFactory;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultJvmMemoryInfo;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultLocalHomeFileSystemInfo;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultOperatingSystemInfo;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultOptionalServiceProvider;
import com.atlassian.troubleshooting.http.HttpClientFactory;
import com.atlassian.troubleshooting.preupgrade.DefaultPreUpgradePlanningManager;
import com.atlassian.troubleshooting.preupgrade.PreUpgradeDataRetriever;
import com.atlassian.troubleshooting.preupgrade.accessors.DefaultPupEnvironmentAccessor;
import com.atlassian.troubleshooting.preupgrade.accessors.LicenseCompatibilityChecker;
import com.atlassian.troubleshooting.preupgrade.checks.DefaultZduAvailabilityChecker;
import com.atlassian.troubleshooting.preupgrade.client.DefaultPreUpgradeDataServiceClient;
import com.atlassian.troubleshooting.preupgrade.modz.ChecksumRegistry;
import com.atlassian.troubleshooting.preupgrade.modz.ModzDetectorService;
import com.atlassian.troubleshooting.spring.CommonJfrBeans;
import com.atlassian.troubleshooting.spring.CommonSupportZipBundleBeans;
import com.atlassian.troubleshooting.spring.ImportedOsgiServiceBeans;
import com.atlassian.troubleshooting.stp.DefaultWebResourcesService;
import com.atlassian.troubleshooting.stp.action.impl.DefaultSupportActionFactory;
import com.atlassian.troubleshooting.stp.audit.AuditorImpl;
import com.atlassian.troubleshooting.stp.audit.AuditorNoop;
import com.atlassian.troubleshooting.stp.hercules.DefaultLogScanService;
import com.atlassian.troubleshooting.stp.hercules.LogScanFactory;
import com.atlassian.troubleshooting.stp.hercules.LogScanHelper;
import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.hercules.cache.LogScanCacheSupplier;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResourceService;
import com.atlassian.troubleshooting.stp.host.OsgiHostApplication;
import com.atlassian.troubleshooting.stp.mxbean.DefaultMBeanServerFactoryBean;
import com.atlassian.troubleshooting.stp.mxbean.DefaultMXBeanProvider;
import com.atlassian.troubleshooting.stp.persistence.ZipConfigurationRepository;
import com.atlassian.troubleshooting.stp.properties.LinuxDistributionDataProvider;
import com.atlassian.troubleshooting.stp.properties.PluginSupportDataAppenderManager;
import com.atlassian.troubleshooting.stp.properties.PluginSupportDataXmlKeyResolver;
import com.atlassian.troubleshooting.stp.properties.appenders.ApplicationLinksDataAppender;
import com.atlassian.troubleshooting.stp.properties.appenders.ClusterInfoAppender;
import com.atlassian.troubleshooting.stp.properties.appenders.DarkFeaturesInfoAppender;
import com.atlassian.troubleshooting.stp.properties.appenders.PluginsSupportDataAppender;
import com.atlassian.troubleshooting.stp.properties.appenders.ServerInfoAppender;
import com.atlassian.troubleshooting.stp.properties.appenders.SsoConfigDataAppender;
import com.atlassian.troubleshooting.stp.properties.appenders.SupportZipConfigurationAppender;
import com.atlassian.troubleshooting.stp.request.DefaultSupportRequestService;
import com.atlassian.troubleshooting.stp.request.SupportTaskFactory;
import com.atlassian.troubleshooting.stp.salext.mail.MailQueueItemFactory;
import com.atlassian.troubleshooting.stp.salext.mail.MailServerManagerProvider;
import com.atlassian.troubleshooting.stp.salext.output.XmlSupportDataFormatter;
import com.atlassian.troubleshooting.stp.scheduler.DefaultSchedulerServiceProvider;
import com.atlassian.troubleshooting.stp.scheduler.ScheduleFactory;
import com.atlassian.troubleshooting.stp.scheduler.ScheduledHerculesHealthReportAction;
import com.atlassian.troubleshooting.stp.scheduler.SchedulerServiceProvider;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.atlassian.troubleshooting.stp.security.UserService;
import com.atlassian.troubleshooting.stp.servlet.PreUpgradeServlet;
import com.atlassian.troubleshooting.stp.servlet.StpServletUtils;
import com.atlassian.troubleshooting.stp.servlet.SupportToolsViewServlet;
import com.atlassian.troubleshooting.stp.spi.DefaultFileSystemInfo;
import com.atlassian.troubleshooting.stp.spi.DefaultRuntimeHelper;
import com.atlassian.troubleshooting.stp.spi.SupportDataModuleDescriptorFactory;
import com.atlassian.troubleshooting.stp.spi.SupportHealthCheckModuleDescriptorFactory;
import com.atlassian.troubleshooting.stp.task.DefaultMonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitorFactory;
import com.atlassian.troubleshooting.stp.upgrade.StpSchedulerCleanUpTask;
import com.atlassian.troubleshooting.stp.zip.DefaultSupportZipService;
import com.atlassian.troubleshooting.stp.zip.SupportZipFileNameGenerator;
import com.atlassian.troubleshooting.thready.manager.DefaultConfigurationPersistenceService;
import com.atlassian.troubleshooting.thready.manager.DefaultThreadDiagnosticsConfigurationManager;
import com.atlassian.troubleshooting.thready.manager.DefaultThreadNameManager;
import com.atlassian.troubleshooting.thready.manager.DisableOldThreadyPlugin;
import com.atlassian.troubleshooting.thready.manager.RequestValidator;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import com.atlassian.troubleshooting.upgrade.TroubleshootingPluginReplacementService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ImportedOsgiServiceBeans.class, CommonSupportZipBundleBeans.class, CommonJfrBeans.class, ApplicationLinksDataAppender.class, ChecksumRegistry.class, DefaultDbPlatformFactory.class, DbVersionExtractor.class, SavedExternalResourceService.ExternalResourceHelper.class, DefaultFileSystemInfo.class, OsgiHostApplication.class, HttpClientFactory.class, LicenseCompatibilityChecker.class, LogScanCacheSupplier.class, LogScanHelper.class, LogScanFactory.class, DefaultLogScanService.class, MailQueueItemFactory.class, MailServerManagerProvider.class, DefaultMBeanServerFactoryBean.class, ModzDetectorService.class, DefaultMonitoredTaskExecutorFactory.class, DefaultMXBeanProvider.class, DefaultOperatingSystemInfo.class, PermissionValidationService.class, CurrentPluginInfo.class, PluginsSupportDataAppender.class, StpSchedulerCleanUpTask.class, PreUpgradeDataRetriever.class, DefaultPreUpgradeDataServiceClient.class, DefaultPreUpgradePlanningManager.class, DefaultZduAvailabilityChecker.class, DefaultPupEnvironmentAccessor.class, DefaultRuntimeHelper.class, SavedExternalResourceService.class, ScheduledHerculesHealthReportAction.class, ScheduleFactory.class, DefaultSchedulerServiceProvider.class, StpServletUtils.class, DefaultSupportActionFactory.class, PluginSupportDataAppenderManager.class, PluginSupportDataXmlKeyResolver.class, DefaultSupportRequestService.class, SupportTaskFactory.class, SupportZipFileNameGenerator.class, DefaultSupportZipService.class, DefaultTaskMonitorFactory.class, SupportDataModuleDescriptorFactory.class, SupportHealthCheckModuleDescriptorFactory.class, TroubleshootingPluginReplacementService.class, UserService.class, XmlSupportDataFormatter.class, DefaultWebResourcesService.class, DefaultJvmMemoryInfo.class, DefaultLocalHomeFileSystemInfo.class, SupportToolsViewServlet.class, PreUpgradeServlet.class, AuditorImpl.class, AuditorNoop.class, RequestValidator.class, DefaultThreadNameManager.class, DefaultJsonSerialiser.class, DefaultClusterMessagingService.class, DefaultThreadDiagnosticsConfigurationManager.class, DefaultConfigurationPersistenceService.class, DisableOldThreadyPlugin.class, ServerInfoAppender.class, ClusterInfoAppender.class, SupportZipConfigurationAppender.class, ZipConfigurationRepository.class, DarkFeaturesInfoAppender.class, SsoConfigDataAppender.class, LinuxDistributionDataProvider.class})
public class CommonBeans {
    @Bean
    public BundleContext bundleContext() {
        return FrameworkUtil.getBundle(this.getClass()).getBundleContext();
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportPluginInfo(PluginInfo pluginInfo) {
        return OsgiServices.exportOsgiService(pluginInfo, ExportOptions.as(PluginInfo.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportLogScanService(LogScanService logScanService) {
        return OsgiServices.exportOsgiService(logScanService, ExportOptions.as(LogScanService.class, LifecycleAware.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSchedulerServiceProvider(SchedulerServiceProvider schedulerServiceProvider) {
        return OsgiServices.exportOsgiService(schedulerServiceProvider, ExportOptions.as(SchedulerServiceProvider.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSupportDataModuleDescriptorFactory(SupportDataModuleDescriptorFactory supportDataModuleDescriptorFactory) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)supportDataModuleDescriptorFactory);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSupportHealthCheckModuleDescriptorFactory(SupportHealthCheckModuleDescriptorFactory supportHealthCheckModuleDescriptorFactory) {
        return OsgiServices.exportAsModuleType((ListableModuleDescriptorFactory)supportHealthCheckModuleDescriptorFactory);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTroubleshootingPluginReplacementService(TroubleshootingPluginReplacementService troubleshootingPluginReplacementService) {
        return OsgiServices.exportOsgiService(troubleshootingPluginReplacementService, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportWebResourcesService(WebResourcesService webResourcesService) {
        return OsgiServices.exportOsgiService(webResourcesService, ExportOptions.as(WebResourcesService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportThreadDiagnosticsConfigurationManager(ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager) {
        return OsgiServices.exportOsgiService(threadDiagnosticsConfigurationManager, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportDisableOldThreadyPlugin(DisableOldThreadyPlugin disableOldThreadyPlugin) {
        return OsgiServices.exportOsgiService(disableOldThreadyPlugin, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public OptionalServiceProvider optionalServiceProvider(BundleContext bundleContext) {
        return new DefaultOptionalServiceProvider(bundleContext);
    }
}

