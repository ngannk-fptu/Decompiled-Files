/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.user.GroupManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainCheckRegistration;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainChecker;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionRegistration;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckContextProvider;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckRegistration;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckContextProvider;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckRegistration;
import com.atlassian.migration.agent.service.check.email.SpacesDuplicateEmailCheckRegistration;
import com.atlassian.migration.agent.service.check.email.SpacesInvalidEmailCheckRegistration;
import com.atlassian.migration.agent.service.check.group.GroupNamesCheckContextProvider;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictCheckRegistration;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckContextProvider;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckRegistration;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.GlobalUnsupportedUserHandler;
import com.atlassian.migration.agent.service.email.IncorrectEmailService;
import com.atlassian.migration.agent.service.email.InvalidEmailValidator;
import com.atlassian.migration.agent.service.email.MostFrequentDomainService;
import com.atlassian.migration.agent.service.email.NewEmailSuggestingService;
import com.atlassian.migration.agent.service.email.UserBaseScanRunner;
import com.atlassian.migration.agent.service.email.UserBaseScanService;
import com.atlassian.migration.agent.service.email.UserEmailFixer;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.DetectedUserEmailAnalyticsScheduledRunner;
import com.atlassian.migration.agent.service.impl.DetectedUserEmailAnalyticsService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import com.atlassian.migration.agent.service.impl.UserService;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisher;
import com.atlassian.migration.agent.service.stepexecutor.user.UsersMigrationExecutor;
import com.atlassian.migration.agent.service.user.DefaultUsersMigrationService;
import com.atlassian.migration.agent.service.user.GlobalEmailFixesService;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.service.user.UserMigrationViaEGService;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import com.atlassian.migration.agent.service.user.UsersToTombstoneFileManager;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.agent.store.IncorrectEmailStore;
import com.atlassian.migration.agent.store.StepProgressPropertiesStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TombstoneAccountStore;
import com.atlassian.migration.agent.store.UserBaseScanStore;
import com.atlassian.migration.agent.store.impl.DetectedEmailEventLogStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.user.GroupManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class})
@Configuration
public class UserGroupsBeanConfiguration {
    @Bean
    public DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService(UserService userService, SchedulerService schedulerService, AnalyticsEventService analyticsEventService, SENSupplier senSupplier, CloudSiteService cloudSiteService, DetectedEmailEventLogStore eventLogStore, PluginTransactionTemplate ptx, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new DetectedUserEmailAnalyticsService(userService, schedulerService, analyticsEventService, senSupplier, cloudSiteService, eventLogStore, ptx, migrationAgentConfiguration);
    }

    @Bean
    public DetectedUserEmailAnalyticsScheduledRunner detectedUserEmailAnalyticsScheduledRunner(SchedulerService schedulerService, DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService, MigrationAgentConfiguration configuration) {
        return new DetectedUserEmailAnalyticsScheduledRunner(schedulerService, detectedUserEmailAnalyticsService, configuration);
    }

    @Bean
    public DuplicateEmailCheckRegistration duplicateEmailCheckRegistration(UserEmailFixer userEmailFixer, GlobalEmailFixesConfigService globalEmailFixesConfigService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, DuplicateEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new DuplicateEmailCheckRegistration(userEmailFixer, globalEmailFixesConfigService, migrationDarkFeaturesManager, contextProvider, systemInformationService, analyticsEventBuilder);
    }

    @Bean
    public DuplicateEmailCheckContextProvider emailCheckContextProvider(UserGroupExtractFacade userGroupExtractFacade) {
        return new DuplicateEmailCheckContextProvider(userGroupExtractFacade);
    }

    @Bean
    public SpacesDuplicateEmailCheckRegistration spacesDuplicateEmailCheckRegistration(UserEmailFixer userEmailFixer, GlobalEmailFixesConfigService globalEmailFixesConfigService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, DuplicateEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new SpacesDuplicateEmailCheckRegistration(userEmailFixer, globalEmailFixesConfigService, migrationDarkFeaturesManager, contextProvider, systemInformationService, analyticsEventBuilder);
    }

    @Bean
    public InvalidEmailCheckContextProvider invalidEmailsContextProvider(UsersMigrationRequestBuilder usersMigrationRequestBuilder) {
        return new InvalidEmailCheckContextProvider(usersMigrationRequestBuilder);
    }

    @Bean
    public InvalidEmailCheckRegistration emailCheckRegistration(InvalidEmailCheckContextProvider invalidEmailCheckContextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder, PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        return new InvalidEmailCheckRegistration(invalidEmailCheckContextProvider, systemInformationService, analyticsEventBuilder, platformService, cloudSiteService, migrationCatalogueStorageService, userMigrationViaEGService, fileServiceManager, objectStorageService, darkFeaturesManager);
    }

    @Bean
    public SpacesInvalidEmailCheckRegistration spaceEmailCheckRegistration(InvalidEmailCheckContextProvider invalidEmailCheckContextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder, PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        return new SpacesInvalidEmailCheckRegistration(invalidEmailCheckContextProvider, systemInformationService, analyticsEventBuilder, platformService, cloudSiteService, migrationCatalogueStorageService, userMigrationViaEGService, fileServiceManager, objectStorageService, darkFeaturesManager);
    }

    @Bean
    public TrustedDomainChecker trustedDomainChecker(UserGroupExtractFacade userGroupExtractFacade, UserDomainService userDomainService) {
        return new TrustedDomainChecker(userGroupExtractFacade, userDomainService);
    }

    @Bean
    public TrustedDomainCheckRegistration trustedDomainCheckRegistration(TrustedDomainChecker trustedDomainChecker, AnalyticsEventBuilder analyticsEventBuilder) {
        return new TrustedDomainCheckRegistration(trustedDomainChecker, analyticsEventBuilder);
    }

    @Bean
    public DefaultUsersMigrationService defaultUsersMigrationService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new DefaultUsersMigrationService(configuration, userAgentInterceptor, okHttpProxyBuilder);
    }

    @Bean
    public RetryingUsersMigrationService usersMigrationService(DefaultUsersMigrationService defaultUsersMigrationService) {
        return new RetryingUsersMigrationService(defaultUsersMigrationService);
    }

    @Bean
    public UserMigrationViaEGService egUsersMigrationService(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        return new UserMigrationViaEGService(enterpriseGatekeeperClient);
    }

    @Bean
    public UsersMigrationRequestBuilder usersMigrationRequestBuilder(GroupManager groupManager, SpacePermissionManager spacePermissionManager, UserService userService, UserGroupExtractFacade userGroupExtractFacade, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SENSupplier senSupplier, PluginVersionManager pluginVersionManager, SystemInformationService systemInformationService, GlobalEmailFixesService globalEmailFixesService, UsersToTombstoneFileManager usersToTombstoneFileManager) {
        return new UsersMigrationRequestBuilder(groupManager, spacePermissionManager, userService, userGroupExtractFacade, migrationDarkFeaturesManager, senSupplier, pluginVersionManager, systemInformationService, globalEmailFixesService, usersToTombstoneFileManager);
    }

    @Bean
    public LicenceCheckRegistration cloudFreeUsersConflictCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, CloudSiteService cloudSiteService, PlatformService platformService, MigrationCatalogueStorageService migrationCatalogueStorageService, UsersMigrationRequestBuilder usersMigrationRequestBuilder, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, SystemInformationService systemInformationService) {
        return new LicenceCheckRegistration(analyticsEventBuilder, cloudSiteService, usersMigrationRequestBuilder, platformService, migrationCatalogueStorageService, userMigrationViaEGService, fileServiceManager, systemInformationService);
    }

    @Bean
    public LicenceCheckContextProvider cloudFreeConflictCheckContextProvider(UsersMigrationRequestBuilder usersMigrationRequestBuilder) {
        return new LicenceCheckContextProvider(usersMigrationRequestBuilder);
    }

    @Bean
    public GroupNamesCheckContextProvider groupNamesCheckContextProvider(UserGroupExtractFacade userGroupExtractFacade) {
        return new GroupNamesCheckContextProvider(userGroupExtractFacade);
    }

    @Bean
    public GroupNamesConflictCheckRegistration groupNamesConflictCheckRegistration(CloudSiteService cloudSiteService, RetryingUsersMigrationService usersMigrationService, AnalyticsEventBuilder analyticsEventBuilder, UserGroupExtractFacade userGroupExtractFacade) {
        return new GroupNamesConflictCheckRegistration(cloudSiteService, usersMigrationService, analyticsEventBuilder, userGroupExtractFacade);
    }

    @Bean
    public CloudPremiumEditionRegistration cloudEditionRegistration(AnalyticsEventBuilder analyticsEventBuilder, CloudSiteService cloudSiteService, RetryingUsersMigrationService usersMigrationService) {
        return new CloudPremiumEditionRegistration(analyticsEventBuilder, cloudSiteService, usersMigrationService);
    }

    @Bean
    public UsersMigrationExecutor usersMigrationExecutor(RetryingUsersMigrationService usersMigrationService, ProgressTracker progressTracker, UsersMigrationRequestBuilder usersMigrationRequestBuilder, StepStore stepStore, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, UserMappingsFileManager userMappingsFileManager, EnterpriseGatekeeperClient enterpriseGatekeeperClient, PluginTransactionTemplate pluginTransactionTemplate, MigrationDarkFeaturesManager migrationDarkFeaturesManager, GlobalEmailFixesConfigService globalEmailFixesConfigService, UsersToTombstoneFileManager usersToTombstoneFileManager, TombstoneMappingsPublisher tombstoneMappingsPublisher, StepProgressPropertiesStore stepProgressPropertiesStore) {
        return new UsersMigrationExecutor(usersMigrationService, progressTracker, usersMigrationRequestBuilder, stepStore, analyticsEventService, analyticsEventBuilder, userMappingsFileManager, enterpriseGatekeeperClient, pluginTransactionTemplate, migrationDarkFeaturesManager, globalEmailFixesConfigService, usersToTombstoneFileManager, tombstoneMappingsPublisher, stepProgressPropertiesStore);
    }

    @Bean
    public GlobalEmailFixesConfigService globalEmailFixesConfigService(PluginSettingsFactory pluginSettingsFactory, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new GlobalEmailFixesConfigService(pluginSettingsFactory, migrationAgentConfiguration);
    }

    @Bean
    public GlobalUnsupportedUserHandler globalUnsupportedUserHandler(GlobalEmailFixesConfigService globalEmailFixesConfigService, NewEmailSuggestingService newEmailSuggestingService, IncorrectEmailStore incorrectEmailStore, UserBaseScanService userBaseScanService, PluginTransactionTemplate ptx, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new GlobalUnsupportedUserHandler(globalEmailFixesConfigService, newEmailSuggestingService, incorrectEmailStore, userBaseScanService, ptx, migrationDarkFeaturesManager);
    }

    @Bean
    public IncorrectEmailService incorrectEmailService(CrowdService crowdService, DirectoryManager directoryManager, IncorrectEmailStore incorrectEmailStore, PluginTransactionTemplate ptx, GlobalUnsupportedUserHandler globalUnsupportedUserHandler, UserBaseScanService userBaseScanService, NewEmailSuggestingService newEmailSuggestingService) {
        return new IncorrectEmailService(crowdService, directoryManager, incorrectEmailStore, ptx, globalUnsupportedUserHandler, userBaseScanService, newEmailSuggestingService);
    }

    @Bean
    public UserBaseScanService userBaseScanService(UserBaseScanStore userBaseScanStore, PluginTransactionTemplate ptx, GlobalEmailFixesConfigService globalEmailFixesConfigService, ClusterLockService clusterLockService) {
        return new UserBaseScanService(userBaseScanStore, ptx, globalEmailFixesConfigService, clusterLockService);
    }

    @Bean
    public UserBaseScanRunner userBaseScanRunner(UserGroupExtractFacade userGroupExtractFacade, IncorrectEmailService incorrectEmailService, ClusterLockService clusterLockService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, UserBaseScanService userBaseScanService, InvalidEmailValidator invalidEmailValidator, MostFrequentDomainService mostFrequentDomainService) {
        return new UserBaseScanRunner(userGroupExtractFacade, incorrectEmailService, clusterLockService, analyticsEventService, analyticsEventBuilder, userBaseScanService, invalidEmailValidator, mostFrequentDomainService);
    }

    @Bean
    public UserEmailFixer userEmailFixer(GlobalUnsupportedUserHandler globalUnsupportedUserHandler, InvalidEmailValidator invalidEmailValidator, MostFrequentDomainService mostFrequentDomainService) {
        return new UserEmailFixer(globalUnsupportedUserHandler, invalidEmailValidator, mostFrequentDomainService);
    }

    @Bean
    public InvalidEmailValidator invalidEmailValidator(PlatformService platformService, CloudSiteService cloudSiteService, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMigrationViaEGService userMigrationViaEGService, FileServiceManager fileServiceManager, ObjectStorageService objectStorageService, MigrationDarkFeaturesManager darkFeaturesManager) {
        return new InvalidEmailValidator(platformService, cloudSiteService, migrationCatalogueStorageService, userMigrationViaEGService, fileServiceManager, objectStorageService, darkFeaturesManager);
    }

    @Bean
    public GlobalEmailFixesService globalEmailFixesService(UserEmailFixer userEmailFixer) {
        return new GlobalEmailFixesService(userEmailFixer);
    }

    @Bean
    public UsersToTombstoneFileManager usersToTombstoneFileManager(BootstrapManager bootstrapManager) {
        return new UsersToTombstoneFileManager(bootstrapManager);
    }

    @Bean
    public TombstoneMappingsPublisher tombstoneMappingsPublisher(RetryingUsersMigrationService usersMigrationService, UsersMigrationRequestBuilder usersMigrationRequestBuilder, TombstoneAccountStore tombstoneAccountStore, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new TombstoneMappingsPublisher(usersMigrationService, usersMigrationRequestBuilder, tombstoneAccountStore, migrationDarkFeaturesManager);
    }
}

