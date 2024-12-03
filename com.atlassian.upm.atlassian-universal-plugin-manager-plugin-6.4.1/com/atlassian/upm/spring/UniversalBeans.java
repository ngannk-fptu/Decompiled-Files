/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.osgi.framework.BundleContext
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.DisallowStartupInCloud;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.PluginsEnablementStateStore;
import com.atlassian.upm.SafeModeService;
import com.atlassian.upm.SelfUpdatePluginAccessor;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.analytics.PluginLicenseChangeAnalyticHelper;
import com.atlassian.upm.analytics.impl.MpacAnalyticsPublisher;
import com.atlassian.upm.api.license.DataCenterCrossgradeablePlugins;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.log.PluginLogService;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.HttpClientFactory;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.VersionAwareHostApplicationInformation;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.analytics.SenFinderImpl;
import com.atlassian.upm.core.analytics.impl.AtlassianAnalyticsPublisher;
import com.atlassian.upm.core.analytics.impl.DefaultAnalyticsLogger;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.async.AsynchronousTaskStatusStore;
import com.atlassian.upm.core.impl.ApplicationPluginsManagerImpl;
import com.atlassian.upm.core.impl.LicensingUsageVerifierImpl;
import com.atlassian.upm.core.impl.PluginEnablementServiceImpl;
import com.atlassian.upm.core.impl.PluginMetadataAccessorImpl;
import com.atlassian.upm.core.impl.PluginRestartRequiredServiceImpl;
import com.atlassian.upm.core.impl.PluginRetrieverImpl;
import com.atlassian.upm.core.impl.PluginSettingsBundledUpdateInfoStore;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.impl.UpmAppManagerImpl;
import com.atlassian.upm.core.install.ConnectPluginControlHandlerRegistryImpl;
import com.atlassian.upm.core.install.ConnectPluginInstallHandlerRegistryImpl;
import com.atlassian.upm.core.install.JarPluginInstallHandler;
import com.atlassian.upm.core.install.NoOpControlHandler;
import com.atlassian.upm.core.install.ObrPluginInstallHandler;
import com.atlassian.upm.core.install.PluginDownloadServiceImpl;
import com.atlassian.upm.core.install.PluginInstallHandlerRegistry;
import com.atlassian.upm.core.install.XmlPluginInstallHandler;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.log.PluginInstallerPluginLogAccessor;
import com.atlassian.upm.core.log.PluginInstallerPluginLogAccessorImpl;
import com.atlassian.upm.core.pac.ClientContextFactory;
import com.atlassian.upm.core.pac.MarketplaceClientManager;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.PluginRestUninstaller;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactoryImpl;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.token.TokenManager;
import com.atlassian.upm.core.token.TokenManagerImpl;
import com.atlassian.upm.impl.HostLicenseInformationImpl;
import com.atlassian.upm.impl.LicensedPluginFactory;
import com.atlassian.upm.impl.PluginManagerPluginAsynchronousTaskStatusStoreImpl;
import com.atlassian.upm.impl.PluginSettingsPluginUpdateRequestStore;
import com.atlassian.upm.impl.PluginSettingsPluginsEnablementStateStore;
import com.atlassian.upm.impl.PluginSettingsUserSettingsStore;
import com.atlassian.upm.impl.SafeModeServiceImpl;
import com.atlassian.upm.impl.SelfUpdateControllerImpl;
import com.atlassian.upm.impl.SelfUpdatePluginAccessorImpl;
import com.atlassian.upm.impl.UpmHostApplicationInformationImpl;
import com.atlassian.upm.impl.UpmPluginAccessorImpl;
import com.atlassian.upm.install.UpmPluginInstallationService;
import com.atlassian.upm.jwt.JwtTokenFactory;
import com.atlassian.upm.license.DataCenterCrossgradeablePluginsImpl;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherServiceFactory;
import com.atlassian.upm.license.internal.impl.PluginLicenseManagerServiceFactory;
import com.atlassian.upm.license.internal.impl.remote.RemotePluginLicenseServiceServiceFactory;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.lifecycle.UpmLifecycleManager;
import com.atlassian.upm.lifecycle.UpmLifecycleManagerImpl;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.lifecycle.UpmUntenantedStartupComponent;
import com.atlassian.upm.log.ApplicationLifecycleLogger;
import com.atlassian.upm.log.ApplicationLifecycleLoggerImpl;
import com.atlassian.upm.log.PluginLogServiceImpl;
import com.atlassian.upm.log.UpmAuditLogService;
import com.atlassian.upm.mac.HamletClientPacProxyImpl;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.notification.ManualUpdateRequiredNotificationService;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.osgi.BundleAccessor;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.ServiceAccessor;
import com.atlassian.upm.osgi.impl.BundleAccessorImpl;
import com.atlassian.upm.osgi.impl.PackageAccessorImpl;
import com.atlassian.upm.osgi.impl.ServiceAccessorImpl;
import com.atlassian.upm.pac.AddonMarketplaceQueries;
import com.atlassian.upm.pac.MpacApplicationCacheManager;
import com.atlassian.upm.pac.MpacApplicationCacheUpdateOnStartUp;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.pac.PacClientImpl;
import com.atlassian.upm.pac.UpmClientContextFactory;
import com.atlassian.upm.pac.UpmMarketplaceClientManager;
import com.atlassian.upm.permission.UpmPermissionService;
import com.atlassian.upm.permission.UpmVisibility;
import com.atlassian.upm.permission.UpmVisibilityImpl;
import com.atlassian.upm.request.PluginRequestFactory;
import com.atlassian.upm.request.PluginRequestFactoryImpl;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.request.PluginSettingsPluginRequestStore;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.representations.UpmRepresentationFactoryImpl;
import com.atlassian.upm.schedule.UpmScheduler;
import com.atlassian.upm.servlet.PluginManagerHandler;
import com.atlassian.upm.spi.PluginControlHandler;
import com.atlassian.upm.spi.PluginInstallHandler;
import com.atlassian.upm.velocity.EscapeTool;
import java.util.List;
import java.util.Set;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniversalBeans {
    @Bean
    public AddonMarketplaceQueries addonMarketplaceQueries(PacClient pacClient, SysPersisted sysPersisted) {
        return new AddonMarketplaceQueries(pacClient, sysPersisted);
    }

    @Bean
    public ApplicationLifecycleLogger applicationLifecycleLogger(AuditLogService auditLog) {
        return new ApplicationLifecycleLoggerImpl(auditLog);
    }

    @Bean
    public ApplicationPluginsManager applicationPluginsManager(VersionAwareHostApplicationInformation versionAwareHostApplicationInformation, UpmAppManager upmAppManager, PluginMetadataManager pluginMetadataManager, PluginRetriever pluginRetriever) {
        return new ApplicationPluginsManagerImpl(versionAwareHostApplicationInformation, upmAppManager, pluginMetadataManager, pluginRetriever);
    }

    @Bean
    public AsyncTaskRepresentationFactory asyncTaskRepresentationFactory(BaseUriBuilder baseUriBuilder) {
        return new AsyncTaskRepresentationFactoryImpl(baseUriBuilder);
    }

    @Bean
    public AsynchronousTaskManager asynchronousTaskManager(ApplicationProperties applicationProperties, ThreadLocalDelegateExecutorFactory factory, BaseUriBuilder uriBuilder, UserManager userManager, AsynchronousTaskStatusStore statusStore) {
        return new AsynchronousTaskManager(applicationProperties, factory, uriBuilder, userManager, statusStore);
    }

    @Bean
    public AtlassianAnalyticsPublisher atlassianAnalyticsPublisher(AnalyticsLogger analytics, EventPublisher eventPublisher) {
        return new AtlassianAnalyticsPublisher(analytics, eventPublisher);
    }

    @Bean
    public BundleAccessor bundleAccessor(PackageAccessor packageAccessor, BundleContext bundleContext) {
        return new BundleAccessorImpl(packageAccessor, bundleContext);
    }

    @Bean
    public ConnectPluginControlHandlerRegistryImpl connectPluginControlHandlerRegistryImpl(BundleContext bundleContext, Set<PluginControlHandler> internalHandlers) {
        return new ConnectPluginControlHandlerRegistryImpl(bundleContext, internalHandlers);
    }

    @Bean
    public ConnectPluginInstallHandlerRegistryImpl connectPluginInstallHandlerRegistryImpl(BundleContext bundleContext, Set<PluginInstallHandler> pluginInstallHandlers) {
        return new ConnectPluginInstallHandlerRegistryImpl(bundleContext, pluginInstallHandlers);
    }

    @Bean
    public DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins(PluginRetriever pluginRetriever, PluginLicenseRepository pluginLicenseRepository, LicensingUsageVerifier licensingUsageVerifier) {
        return new DataCenterCrossgradeablePluginsImpl(pluginRetriever, pluginLicenseRepository, licensingUsageVerifier);
    }

    @Bean
    public DefaultAnalyticsLogger defaultAnalyticsLogger() {
        return new DefaultAnalyticsLogger();
    }

    @Bean
    public DisallowStartupInCloud disallowStartupInCloud() {
        return new DisallowStartupInCloud();
    }

    @Bean
    public HamletClientPacProxyImpl hamletClientPacProxyImpl(ClientContextFactory clientContextFactory, MarketplaceClientManager marketplaceClientFactory, SysPersisted sysPersisted, JwtTokenFactory jwtTokenFactory, HostLicenseProvider hostLicenseProvider, UpmUriBuilder uriBuilder, PluginLicenseRepository pluginLicenseRepository, HostLicenseInformation hostLicenseInformation) {
        return new HamletClientPacProxyImpl(clientContextFactory, marketplaceClientFactory, sysPersisted, jwtTokenFactory, hostLicenseProvider, uriBuilder, pluginLicenseRepository, hostLicenseInformation);
    }

    @Bean
    public HostLicenseInformation hostLicenseInformation(HostLicenseProvider hostLicenseProvider) {
        return new HostLicenseInformationImpl(hostLicenseProvider);
    }

    @Bean
    public JarPluginInstallHandler jarPluginInstallHandler(DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate) {
        return new JarPluginInstallHandler(hostApplicationInformation, permissionEnforcer, pluginAccessor, pluginController, txTemplate);
    }

    @Bean
    public LicensedPluginFactory licensedPluginFactory(I18nResolver i18nResolver, PluginAccessor accessor, PluginMetadataAccessor metadata, PluginLicenseRepository licenseRepository, PluginControlHandlerRegistry pluginControlHandlerRegistry) {
        return new LicensedPluginFactory(i18nResolver, accessor, metadata, licenseRepository, pluginControlHandlerRegistry);
    }

    @Bean
    public LicensingUsageVerifier licensingUsageVerifier(VersionAwareHostApplicationInformation versionAwareHostApplicationInformation, PluginRetriever pluginRetriever, ApplicationPluginsManager applicationPluginsManager) {
        return new LicensingUsageVerifierImpl(versionAwareHostApplicationInformation, pluginRetriever, applicationPluginsManager);
    }

    @Bean
    public MpacAnalyticsPublisher mpacAnalyticsPublisher(AnalyticsLogger analyticsLogger, ClientContextFactory clientContextFactory, MarketplaceClientManager marketplaceClientManager, SysPersisted sysPersisted, HttpClientFactory httpClientFactory) {
        return new MpacAnalyticsPublisher(analyticsLogger, clientContextFactory, marketplaceClientManager, sysPersisted, httpClientFactory);
    }

    @Bean
    public MpacAnalyticsPublisher.ClientFactory mpacAnalyticsPublisherClientFactory() {
        return new MpacAnalyticsPublisher.ClientFactory();
    }

    @Bean
    public MpacApplicationCacheManager mpacApplicationCacheManager(ApplicationProperties applicationProperties, CacheFactory cacheFactory, SysPersisted sysPersisted, MarketplaceClientManager mpacV2ClientFactory, UpmHostApplicationInformation hostApplicationInformation, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        return new MpacApplicationCacheManager(applicationProperties, cacheFactory, sysPersisted, mpacV2ClientFactory, hostApplicationInformation, threadLocalDelegateExecutorFactory);
    }

    @Bean
    public MpacApplicationCacheUpdateOnStartUp mpacApplicationCacheUpdateOnStartUp(MpacApplicationCacheManager mpacApplicationCacheManager) {
        return new MpacApplicationCacheUpdateOnStartUp(mpacApplicationCacheManager);
    }

    @Bean
    public NoOpControlHandler noOpControlHandler() {
        return new NoOpControlHandler();
    }

    @Bean
    public ObrPluginInstallHandler obrPluginInstallHandler(BundleContext bundleContext, DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate, SafeModeAccessor safeMode) {
        return new ObrPluginInstallHandler(bundleContext, hostApplicationInformation, permissionEnforcer, pluginAccessor, pluginController, txTemplate, safeMode);
    }

    @Bean
    public PacClient pacClient(ApplicationProperties applicationProperties, CacheFactory cacheFactory, EventPublisher eventPublisher, MarketplaceClientManager mpacV2ClientFactory, AnalyticsLogger analytics, PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, SysPersisted sysPersisted, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, UpmHostApplicationInformation hostApplicationInformation, PluginLicenseRepository pluginLicenseRepository, SenFinder senFinder, ApplicationPluginsManager applicationPluginsManager, MpacApplicationCacheManager mpacApplicationCacheManager) {
        return new PacClientImpl(applicationProperties, cacheFactory, eventPublisher, mpacV2ClientFactory, analytics, pluginRetriever, metadata, sysPersisted, threadLocalDelegateExecutorFactory, hostApplicationInformation, pluginLicenseRepository, senFinder, applicationPluginsManager, mpacApplicationCacheManager);
    }

    @Bean
    public PackageAccessor packageAccessor(PackageAdmin packageAdmin) {
        return new PackageAccessorImpl(packageAdmin);
    }

    @Bean
    public PermissionEnforcer permissionEnforcer(UserManager userManager, PermissionService permissionService) {
        return new PermissionEnforcer(userManager, permissionService);
    }

    @Bean
    public PluginDownloadService pluginDownloadService(ClientContextFactory clientContextFactory) {
        return new PluginDownloadServiceImpl(clientContextFactory);
    }

    @Bean
    public PluginEnablementService pluginEnablementService(PluginAccessor pluginAccessor, PluginController pluginController, AuditLogService auditLogger, TransactionTemplate txTemplate, AnalyticsLogger analytics, PluginRetriever pluginRetriever, PluginControlHandlerRegistry pluginControlHandlerRegistry, UserManager userManager, DefaultHostApplicationInformation hostApplicationInformation, SenFinder senFinder, LicensingUsageVerifier licensingUsageVerifier) {
        return new PluginEnablementServiceImpl(pluginAccessor, pluginController, auditLogger, txTemplate, analytics, pluginRetriever, pluginControlHandlerRegistry, userManager, hostApplicationInformation, senFinder, licensingUsageVerifier);
    }

    @Bean
    public PluginInstallerPluginLogAccessor pluginInstallerPluginLogAccessor(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, BaseUriBuilder uriBuilder) {
        return new PluginInstallerPluginLogAccessorImpl(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, uriBuilder);
    }

    @Bean
    public PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper(UpmHostApplicationInformation appInfo, DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins, DefaultAnalyticsLogger analyticsLogger, PluginLicenseRepository licenseRepository) {
        return new PluginLicenseChangeAnalyticHelper(appInfo, dataCenterCrossgradeablePlugins, analyticsLogger, licenseRepository);
    }

    @Bean
    public PluginLicenseEventPublisherServiceFactory pluginLicenseEventPublisherServiceFactory(PluginLicenseEventPublisherRegistry publisherRegistry, PluginLicenseRepository repository) {
        return new PluginLicenseEventPublisherServiceFactory(publisherRegistry, repository);
    }

    @Bean
    public PluginLicenseManagerServiceFactory pluginLicenseManagerServiceFactory(PluginLicenseRepository repository, UpmPluginAccessor pluginAccessor, RoleBasedLicensingPluginService roleBasedService) {
        return new PluginLicenseManagerServiceFactory(repository, pluginAccessor, roleBasedService);
    }

    @Bean
    public PluginLogService pluginLogService(AuditLogService auditLog) {
        return new PluginLogServiceImpl(auditLog);
    }

    @Bean
    public PluginManagerHandler pluginManagerHandler(TemplateRenderer renderer, PermissionEnforcer permissionEnforcer, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager, UpmInformation upm, SysPersisted sysPersisted, UpmHostApplicationInformation appInfo, UpmAppManager appManager, HostApplicationDescriptor hostApplicationDescriptor, HostLicenseProvider hostLicenseProvider, PacClient pacClient, ApplicationProperties applicationProperties) {
        return new PluginManagerHandler(renderer, permissionEnforcer, loginUriProvider, webSudoManager, upm, sysPersisted, appInfo, appManager, hostApplicationDescriptor, hostLicenseProvider, pacClient, applicationProperties);
    }

    @Bean
    public PluginManagerPluginAsynchronousTaskStatusStoreImpl pluginManagerPluginAsynchronousTaskStatusStoreImpl(PluginSettingsFactory pluginSettingsFactory, ClusterLockService clusterLockService) {
        return new PluginManagerPluginAsynchronousTaskStatusStoreImpl(pluginSettingsFactory, clusterLockService);
    }

    @Bean
    public PluginMetadataAccessor pluginMetadataAccessor(ApplicationProperties applicationProperties, PluginControlHandlerRegistry pluginControlHandlerRegistry, PluginMetadataManager pluginMetadataManager, DefaultHostApplicationInformation hostApplicationInformation) {
        return new PluginMetadataAccessorImpl(applicationProperties, pluginControlHandlerRegistry, pluginMetadataManager, hostApplicationInformation);
    }

    @Bean
    public PluginRequestFactory pluginRequestFactory(UserManager userManager) {
        return new PluginRequestFactoryImpl(userManager);
    }

    @Bean
    public PluginRestUninstaller pluginRestUninstaller(PluginInstallationService pluginInstallationService, PluginRetriever pluginRetriever, PluginMetadataAccessor pluginMetadataAccessor, SafeModeAccessor safeModeAccessor, PermissionService permissionService, UserManager userManager, I18nResolver i18nResolver, ApplicationPluginsManager applicationPluginsManager) {
        return new PluginRestUninstaller(pluginInstallationService, pluginRetriever, pluginMetadataAccessor, safeModeAccessor, permissionService, userManager, i18nResolver, applicationPluginsManager);
    }

    @Bean
    public PluginRestartRequiredService pluginRestartRequiredService(PluginRetriever pluginRetriever, PluginController pluginController, AuditLogService auditLogger) {
        return new PluginRestartRequiredServiceImpl(pluginRetriever, pluginController, auditLogger);
    }

    @Bean
    public PluginRetriever pluginRetriever(UpmPluginAccessor pluginAccessor, PluginFactory pluginFactory) {
        return new PluginRetrieverImpl(pluginAccessor, pluginFactory);
    }

    @Bean
    public PluginSettingsBundledUpdateInfoStore pluginSettingsBundledUpdateInfoStore(PluginSettingsFactory pluginSettingsFactory) {
        return new PluginSettingsBundledUpdateInfoStore(pluginSettingsFactory);
    }

    @Bean
    public PluginSettingsPluginRequestStore pluginSettingsPluginRequestStore(PluginSettingsFactory pluginSettingsFactory, PluginRequestFactory requestFactory, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder, UserManager userManager, UpmScheduler scheduler, ClusterLockService lockService) {
        return new PluginSettingsPluginRequestStore(pluginSettingsFactory, requestFactory, linkBuilder, uriBuilder, userManager, scheduler, lockService);
    }

    @Bean
    public PluginSettingsPluginUpdateRequestStore pluginSettingsPluginUpdateRequestStore(PluginSettingsFactory pluginSettingsFactory) {
        return new PluginSettingsPluginUpdateRequestStore(pluginSettingsFactory);
    }

    @Bean
    public PluginSettingsPluginsEnablementStateStore pluginSettingsPluginsEnablementStateStore(PluginSettingsFactory pluginSettingsFactory) {
        return new PluginSettingsPluginsEnablementStateStore(pluginSettingsFactory);
    }

    @Bean
    public PluginSettingsUserSettingsStore pluginSettingsUserSettingsStore(PluginSettingsFactory pluginSettingsFactory) {
        return new PluginSettingsUserSettingsStore(pluginSettingsFactory);
    }

    @Bean
    public RemotePluginLicenseServiceServiceFactory remotePluginLicenseServiceServiceFactory(PluginLicenseRepository repository, UpmPluginAccessor accessor) {
        return new RemotePluginLicenseServiceServiceFactory(repository, accessor);
    }

    @Bean
    public SafeModeService safeModeService(UpmPluginAccessor pluginAccessor, PluginEnablementService enabler, AuditLogService auditLogger, PluginsEnablementStateStore enablementStateStore, TransactionTemplate txTemplate, PluginMetadataAccessor metadata, PluginRetriever pluginRetriever, ApplicationPluginsManager applicationPluginsManager) {
        return new SafeModeServiceImpl(pluginAccessor, enabler, auditLogger, enablementStateStore, txTemplate, metadata, pluginRetriever, applicationPluginsManager);
    }

    @Bean
    public SelfUpdateController selfUpdateController(AuditLogService auditLogService, UpmLifecycleManager lifecycleManager, NotificationCache notificationCache, PluginInstallationService pluginInstallationService, PluginRetriever pluginRetriever, PluginSettingsFactory pluginSettingsFactory, SelfUpdatePluginAccessor selfUpdatePluginAccessor, UpmScheduler scheduler, UpmUriBuilder uriBuilder, UpmInformation upm) {
        return new SelfUpdateControllerImpl(auditLogService, lifecycleManager, notificationCache, pluginInstallationService, pluginRetriever, pluginSettingsFactory, selfUpdatePluginAccessor, scheduler, uriBuilder, upm);
    }

    @Bean
    public SelfUpdatePluginAccessor selfUpdatePluginAccessor(ApplicationProperties applicationProperties, PluginRetriever pluginRetriever, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate) {
        return new SelfUpdatePluginAccessorImpl(applicationProperties, pluginRetriever, pluginSettingsFactory, txTemplate);
    }

    @Bean
    public SenFinder senFinder(PluginLicenseRepository pluginLicenseRepository) {
        return new SenFinderImpl(pluginLicenseRepository);
    }

    @Bean
    public ServiceAccessor serviceAccessor(PackageAccessor packageAccessor, BundleContext bundleContext) {
        return new ServiceAccessorImpl(packageAccessor, bundleContext);
    }

    @Bean
    public SysPersisted sysPersisted(PluginSettingsFactory pluginSettingsFactory, ClusterLockService clusterLockService) {
        return new SysPersisted(pluginSettingsFactory, clusterLockService);
    }

    @Bean
    public UpmVisibility tabVisibility(PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted, PacClient pacClient, UpmHostApplicationInformation hostApplicationInformation) {
        return new UpmVisibilityImpl(permissionEnforcer, sysPersisted, pacClient, hostApplicationInformation);
    }

    @Bean
    public TokenManager tokenManager(ClusterLockService lockService) {
        return new TokenManagerImpl(lockService);
    }

    @Bean
    public UpmAppManager upmAppManager(BundleContext bundleContext) {
        return new UpmAppManagerImpl(bundleContext);
    }

    @Bean
    public UpmAuditLogService upmAuditLogService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate, UpmUriBuilder uriBuilder, PluginInstallerPluginLogAccessor pipLogAccessor) {
        return new UpmAuditLogService(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, txTemplate, uriBuilder, pipLogAccessor);
    }

    @Bean
    public UpmClientContextFactory upmClientContextFactory(ApplicationProperties applicationProperties, HostApplicationDescriptor hostApplicationDescriptor, HostLicenseProvider hostLicenseProvider, UpmHostApplicationInformation appInfo) {
        return new UpmClientContextFactory(applicationProperties, hostApplicationDescriptor, hostLicenseProvider, appInfo);
    }

    @Bean
    public UpmHostApplicationInformation upmHostApplicationInformation(HostLicenseProvider hostLicenseProvider, ApplicationProperties applicationProperties, LicenseHandler licenseHandler, PackageAccessor packageAccessor, PluginAccessor pluginAccessor) {
        return new UpmHostApplicationInformationImpl(hostLicenseProvider, applicationProperties, licenseHandler, packageAccessor, pluginAccessor);
    }

    @Bean
    public UpmInformation upmInformation(PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, ApplicationProperties applicationProperties, BundleContext bundleContext) {
        return new UpmInformation(pluginAccessor, pluginSettingsFactory, eventPublisher, applicationProperties, bundleContext);
    }

    @Bean
    public UpmLifecycleManager upmLifecycleManager(List<UpmProductDataStartupComponent> productDataStartupComponents, List<UpmUntenantedStartupComponent> untenantedStartupComponents) {
        return new UpmLifecycleManagerImpl(productDataStartupComponents, untenantedStartupComponents);
    }

    @Bean
    public UpmLinkBuilder upmLinkBuilder(UpmUriBuilder uriBuilder, PluginRestartRequiredService restartRequiredService, PluginMetadataAccessor metadata, AsynchronousTaskManager asynchronousTaskManager, PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted, ProductMailService mailService, SafeModeAccessor safeMode, PluginControlHandlerRegistry pluginControlHandlerRegistry, RoleBasedLicensingPluginService roleBasedLicensingService, UpmHostApplicationInformation appInfo, HostLicenseProvider hostLicenseProvider, HostLicenseInformation hostLicenseInformation, UpmAppManager appManager, LicensingUsageVerifier licensingUsageVerifier, ApplicationProperties applicationProperties) {
        return new UpmLinkBuilder(uriBuilder, restartRequiredService, metadata, asynchronousTaskManager, permissionEnforcer, sysPersisted, mailService, safeMode, pluginControlHandlerRegistry, roleBasedLicensingService, appInfo, hostLicenseProvider, hostLicenseInformation, appManager, licensingUsageVerifier, applicationProperties);
    }

    @Bean
    public UpmMarketplaceClientManager upmMarketplaceClientManager(ApplicationProperties applicationProperties, ClientContextFactory clientContextFactory, BundleContext bundleContext, EventPublisher eventPublisher, UpmInformation upm) {
        return new UpmMarketplaceClientManager(applicationProperties, clientContextFactory, bundleContext, eventPublisher, upm);
    }

    @Bean
    public UpmPermissionService upmPermissionService(PluginMetadataAccessor metadata, SysPersisted sysPersisted, ApplicationProperties applicationProperties, EventPublisher eventPublisher, UpmPluginAccessor pluginAccessor) {
        return new UpmPermissionService(metadata, sysPersisted, applicationProperties, eventPublisher, pluginAccessor);
    }

    @Bean
    public UpmPluginAccessor upmPluginAccessor(PluginAccessor pluginAccessor, PluginControlHandlerRegistry pluginControllerHandlerRegistry) {
        return new UpmPluginAccessorImpl(pluginAccessor, pluginControllerHandlerRegistry);
    }

    @Bean
    public UpmPluginInstallationService upmPluginInstallationService(AnalyticsLogger analytics, AuditLogService auditLogger, I18nResolver i18nResolver, PluginController pluginController, PluginFactory pluginFactory, PluginInstallHandlerRegistry pluginInstallHandlerRegistry, PluginRetriever pluginRetriever, SafeModeAccessor safeMode, TransactionTemplate txTemplate, PluginUpdateRequestStore pluginUpdateRequestStore, PluginRequestStore pluginRequestStore, PluginRequestNotificationChecker pluginRequestNotificationChecker, UpmMailSenderService mailSenderService, UserManager userManager, UpmScheduler upmScheduler, ManualUpdateRequiredNotificationService manualUpdateNotificationService, PluginControlHandlerRegistry pluginControlHandlerRegistry, DefaultHostApplicationInformation hostApplicationInformation, PluginLicenseRepository licenseRepository, SenFinder senFinder, LicensingUsageVerifier licensingUsageVerifier) {
        return new UpmPluginInstallationService(analytics, auditLogger, i18nResolver, pluginController, pluginFactory, pluginInstallHandlerRegistry, pluginRetriever, safeMode, txTemplate, pluginUpdateRequestStore, pluginRequestStore, pluginRequestNotificationChecker, mailSenderService, userManager, upmScheduler, manualUpdateNotificationService, pluginControlHandlerRegistry, hostApplicationInformation, licenseRepository, senFinder, licensingUsageVerifier);
    }

    @Bean
    public UpmRepresentationFactory upmRepresentationFactory(PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PacClient pacClient, AsynchronousTaskManager taskManager, BundleAccessor bundleAccessor, ServiceAccessor serviceAccessor, PackageAccessor packageAccessor, PermissionEnforcer permissionEnforcer, ApplicationProperties applicationProperties, LicenseDateFormatter licenseDateFormatter, HostLicenseProvider hostLicenseProvider, PluginLicenseRepository licenseRepository, SysPersisted sysPersisted, UserManager userManager, PluginRequestStore pluginRequestStore, HostLicenseInformation hostLicenseInformation, UpmInformation upm, PluginUpdateRequestStore pluginUpdateRequestStore, SafeModeAccessor safeMode, PluginRestartRequiredService restartRequiredService, I18nResolver i18nResolver, RoleBasedLicensingPluginService roleBasedLicensingPluginService, UpmHostApplicationInformation appInfo, HostApplicationDescriptor hostApplicationDescriptor, UpmAppManager appManager, LicensingUsageVerifier licensingUsageVerifier, ApplicationPluginsManager applicationPluginsManager) {
        return new UpmRepresentationFactoryImpl(pluginRetriever, metadata, uriBuilder, linkBuilder, pacClient, taskManager, bundleAccessor, serviceAccessor, packageAccessor, permissionEnforcer, applicationProperties, licenseDateFormatter, hostLicenseProvider, licenseRepository, sysPersisted, userManager, pluginRequestStore, hostLicenseInformation, upm, pluginUpdateRequestStore, safeMode, restartRequiredService, i18nResolver, roleBasedLicensingPluginService, appInfo, hostApplicationDescriptor, appManager, licensingUsageVerifier, applicationPluginsManager);
    }

    @Bean
    public UpmUriBuilder upmUriBuilder(ApplicationProperties applicationProperties) {
        return new UpmUriBuilder(applicationProperties);
    }

    @Bean
    public EscapeTool velocityEscapeTool() {
        return new EscapeTool();
    }

    @Bean
    public XmlPluginInstallHandler xmlPluginInstallHandler(DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate) {
        return new XmlPluginInstallHandler(hostApplicationInformation, permissionEnforcer, pluginAccessor, pluginController, txTemplate);
    }
}

