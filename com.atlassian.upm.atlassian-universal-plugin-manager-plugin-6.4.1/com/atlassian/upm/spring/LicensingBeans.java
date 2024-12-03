/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.jwt.JwtTokenFactory;
import com.atlassian.upm.jwt.JwtTokenFactoryImpl;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicenseStore;
import com.atlassian.upm.license.internal.PluginLicenseValidator;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherPublishCheckEvent;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.license.internal.impl.HostApplicationLicenseFactoryImpl;
import com.atlassian.upm.license.internal.impl.LicenseEntityFactoryImpl;
import com.atlassian.upm.license.internal.impl.LicenseManagerProviderImpl;
import com.atlassian.upm.license.internal.impl.PluginLicenseRepositoryImpl;
import com.atlassian.upm.license.internal.impl.PluginLicenseValidatorImpl;
import com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseStore;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginServiceImpl;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginDescriptorMetadataCache;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginDescriptorMetadataCacheImpl;
import com.atlassian.upm.license.internal.mac.LicenseReceiptHandler;
import com.atlassian.upm.license.internal.mac.LicenseReceiptValidator;
import com.atlassian.upm.mail.ProductUserLists;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.notification.ManualUpdateRequiredNotificationService;
import com.atlassian.upm.notification.ManualUpdateRequiredNotificationServiceImpl;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationFactory;
import com.atlassian.upm.notification.NotificationFactoryImpl;
import com.atlassian.upm.notification.NotificationTypes;
import com.atlassian.upm.notification.PluginLicenseNotificationChecker;
import com.atlassian.upm.notification.PluginLicenseNotificationCheckerImpl;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.notification.PluginRequestNotificationCheckerImpl;
import com.atlassian.upm.notification.PluginSettingsNotificationCache;
import com.atlassian.upm.notification.PluginUpdateChecker;
import com.atlassian.upm.notification.PluginUpdateCheckerImpl;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentationFactory;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentationFactoryImpl;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.schedule.UpmScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LicensingBeans {
    @Bean
    public HostApplicationLicenseFactory hostApplicationLicenseFactory(LicenseEntityFactory factory, HostApplicationDescriptor hostApplicationDescriptor, RoleBasedLicensingPluginService roleBasedService, ApplicationProperties applicationProperties, UpmPluginAccessor accessor) {
        return new HostApplicationLicenseFactoryImpl(factory, hostApplicationDescriptor, roleBasedService, applicationProperties, accessor);
    }

    @Bean
    public JwtTokenFactory jwtTokenFactory() {
        return new JwtTokenFactoryImpl();
    }

    @Bean
    public LicenseEntityFactory licenseEntityFactory(HostApplicationDescriptor hostApplicationDescriptor, RoleBasedLicensingPluginService roleBasedLicensingPluginService, ApplicationProperties applicationProperties, UpmAppManager appManager) {
        return new LicenseEntityFactoryImpl(hostApplicationDescriptor, roleBasedLicensingPluginService, applicationProperties, appManager);
    }

    @Bean
    public LicenseManagerProvider licenseManagerProvider() {
        return new LicenseManagerProviderImpl();
    }

    @Bean
    public LicenseReceiptHandler licenseReceiptHandler(UpmPluginAccessor pluginAccessor, UserManager userManager, PluginLicenseValidator pluginLicenseValidator, PluginLicenseRepository pluginLicenseRepository, LicenseReceiptValidator licenseReceiptValidator) {
        return new LicenseReceiptHandler(pluginAccessor, userManager, pluginLicenseValidator, pluginLicenseRepository, licenseReceiptValidator);
    }

    @Bean
    public LicenseReceiptValidator licenseReceiptValidator(PluginLicenseRepository pluginLicenseRepository) {
        return new LicenseReceiptValidator(pluginLicenseRepository);
    }

    @Bean
    public ManualUpdateRequiredNotificationService manualUpdateRequiredNotificationService(PluginRetriever pluginRetriever, UpmMailSenderService mailSenderService, UserSettingsStore userSettingsStore, ProductUserLists userLists, PluginSettingsFactory pluginSettingsFactory) {
        return new ManualUpdateRequiredNotificationServiceImpl(pluginRetriever, mailSenderService, userSettingsStore, userLists, pluginSettingsFactory);
    }

    @Bean
    public NotificationFactory notificationFactory(UserManager userManager) {
        return new NotificationFactoryImpl(userManager);
    }

    @Bean
    public NotificationRepresentationFactory notificationRepresentationFactory(NotificationCache notificationCache, I18nResolver i18nResolver, UpmUriBuilder uriBuilder, PluginRetriever pluginRetriever, UpmLinkBuilder linkBuilder, UserManager userManager, WebResourceManager webResourceManager, PermissionEnforcer permissionEnforcer, NotificationTypes notificationTypes, PluginRequestStore pluginRequestStore, UpmRepresentationFactory representationFactory, UpmInformation upm, UpmHostApplicationInformation appInfo) {
        return new NotificationRepresentationFactoryImpl(notificationCache, i18nResolver, uriBuilder, pluginRetriever, linkBuilder, userManager, webResourceManager, permissionEnforcer, notificationTypes, pluginRequestStore, representationFactory, upm, appInfo);
    }

    @Bean
    public NotificationTypes notificationTypes(ApplicationProperties applicationProperties) {
        return new NotificationTypes(applicationProperties);
    }

    @Bean
    public PluginLicenseEventPublisherPublishCheckEvent pluginLicenseEventPublisherPublishCheckEvent(PluginLicenseEventPublisherRegistry publisherRegistry, PluginLicenseRepository repository, EventPublisher eventPublisher) {
        return new PluginLicenseEventPublisherPublishCheckEvent(publisherRegistry, repository, eventPublisher);
    }

    @Bean
    public PluginLicenseEventPublisherRegistry pluginLicenseEventPublisherRegistry() {
        return new PluginLicenseEventPublisherRegistry();
    }

    @Bean
    public PluginLicenseNotificationChecker pluginLicenseNotificationChecker(NotificationCache cache, PluginLicenseRepository licenseRepository, PluginRetriever pluginRetriever, PluginLicenseEventPublisherRegistry registry, LicensingUsageVerifier licensingUsageVerifier, ApplicationPluginsManager applicationPluginsManager) {
        return new PluginLicenseNotificationCheckerImpl(cache, licenseRepository, pluginRetriever, registry, licensingUsageVerifier, applicationPluginsManager);
    }

    @Bean
    public PluginLicenseRepository pluginLicenseRepository(HostLicenseProvider hostLicenseProvider, PluginLicenseValidator licenseValidator, PluginLicenseEventPublisherRegistry publisherRegistry, LicenseEntityFactory licenseEntityFactory, RoleBasedPluginDescriptorMetadataCache rbpCache, PluginLicenseStore licenseStore, UpmPluginAccessor accessor, CacheFactory cacheFactory, PluginLicenseEventPublisherRegistry licenseEventPublisher, EventPublisher atlassianEventPublisher) {
        return new PluginLicenseRepositoryImpl(hostLicenseProvider, licenseValidator, publisherRegistry, licenseEntityFactory, rbpCache, licenseStore, accessor, cacheFactory, licenseEventPublisher, atlassianEventPublisher);
    }

    @Bean
    public PluginLicenseValidator pluginLicenseValidator(LicenseEntityFactory factory, LicenseManagerProvider licManagerProvider, UpmPluginAccessor pluginAccessor, HostLicenseProvider hostLicenseProvider) {
        return new PluginLicenseValidatorImpl(factory, licManagerProvider, pluginAccessor, hostLicenseProvider);
    }

    @Bean
    public PluginRequestNotificationChecker pluginRequestNotificationChecker(NotificationCache cache, PluginRequestStore pluginRequestStore) {
        return new PluginRequestNotificationCheckerImpl(cache, pluginRequestStore);
    }

    @Bean
    public NotificationCache pluginSettingsNotificationCache(PluginSettingsFactory pluginSettingsFactory, NotificationFactory notificationFactory, ClusterLockService lockService) {
        return new PluginSettingsNotificationCache(pluginSettingsFactory, notificationFactory, lockService);
    }

    @Bean
    public PluginSettingsPluginLicenseStore pluginSettingsPluginLicenseStore(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate) {
        return new PluginSettingsPluginLicenseStore(pluginSettingsFactory, txTemplate);
    }

    @Bean
    public PluginUpdateChecker pluginUpdateChecker(AuditLogService auditLogService, NotificationCache cache, PacClient pacClient, PluginRetriever pluginRetriever, PluginDownloadService downloadService, PluginInstallationService pluginInstaller, PluginLicenseRepository licenseRepository, SelfUpdateController selfUpdateController, SysPersisted sysPersisted, UpmScheduler scheduler, UpmInformation upm, SafeModeAccessor safeMode, ManualUpdateRequiredNotificationService manualUpdateNotificationService, HostLicenseInformation hostLicenseInformation, LicensingUsageVerifier licensingUsageVerifier) {
        return new PluginUpdateCheckerImpl(auditLogService, cache, pacClient, pluginRetriever, downloadService, pluginInstaller, licenseRepository, selfUpdateController, sysPersisted, scheduler, upm, safeMode, manualUpdateNotificationService, hostLicenseInformation, licensingUsageVerifier);
    }

    @Bean
    public RoleBasedLicensingPluginService roleBasedLicensingPluginService(RoleBasedPluginDescriptorMetadataCache metadataCache, EventPublisher eventPublisher, UpmPluginAccessor pluginAccessor, PluginLicenseEventPublisherRegistry registry) {
        return new RoleBasedLicensingPluginServiceImpl(metadataCache, eventPublisher, pluginAccessor, registry);
    }

    @Bean
    public RoleBasedPluginDescriptorMetadataCache roleBasedPluginDescriptorMetadataCache(CacheFactory cacheFactory, UpmPluginAccessor pluginAccessor) {
        return new RoleBasedPluginDescriptorMetadataCacheImpl(cacheFactory, pluginAccessor);
    }
}

