/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.fx3.Fx3Client
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  lombok.Generated
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.cache.CacheManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.fx3.Fx3Client;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.DatabaseConnectionServiceBeans;
import com.atlassian.migration.agent.ImportedOsgiServiceBeans;
import com.atlassian.migration.agent.annotation.ConditionalOnClass;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.config.url.MigrationUrlProviderFactory;
import com.atlassian.migration.agent.logging.MigrationsLogAppender;
import com.atlassian.migration.agent.mma.service.MigrationMetadataAggregatorService;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.okhttp.HttpProxyStrategy;
import com.atlassian.migration.agent.okhttp.HttpsProxyStrategy;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.OkHttpClientSingleton;
import com.atlassian.migration.agent.okhttp.ProxyStrategy;
import com.atlassian.migration.agent.okhttp.ProxyStrategyFactory;
import com.atlassian.migration.agent.service.FileServiceManager;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.cloud.NonceService;
import com.atlassian.migration.agent.service.encryption.AutowireHelper;
import com.atlassian.migration.agent.service.featureflag.FeatureFlagClient;
import com.atlassian.migration.agent.service.featureflag.Fx3ClientFactory;
import com.atlassian.migration.agent.service.featureflag.Fx3OkhttpAdapter;
import com.atlassian.migration.agent.service.impl.ConcurrencySettingsService;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.agent.service.mo.MigrationOrchestratorClient;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.SpaceKeyResolver;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.WarnLogFileWriter;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.service.version.AppOutdatedInfoProvider;
import com.atlassian.migration.agent.service.version.DefaultPluginVersionManager;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.impl.SpaceStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.SessionFactorySupplier;
import com.atlassian.migration.agent.store.jpa.impl.DefaultEntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DefaultPluginTransactionTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DefaultSessionFactorySupplier;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Set;
import lombok.Generated;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ImportedOsgiServiceBeans.class, DatabaseConnectionServiceBeans.class})
@Configuration
public class CommonBeanConfiguration {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CommonBeanConfiguration.class);

    @Bean
    public Fx3ClientFactory fx3ClientFactory(OKHttpProxyBuilder okHttpProxyBuilder, MigrationAgentConfiguration migrationAgentConfiguration, LicenseHandler licenseHandler) {
        return new Fx3ClientFactory(new Fx3OkhttpAdapter(okHttpProxyBuilder), migrationAgentConfiguration, licenseHandler);
    }

    @Bean
    public FeatureFlagClient featureFlagClient(Fx3ClientFactory fx3ClientFactory) {
        Fx3Client fx3Client = null;
        try {
            fx3Client = fx3ClientFactory.create();
        }
        catch (RuntimeException e) {
            log.error("Error occurred while initializing FeatureFlagClient", (Throwable)e);
        }
        return new FeatureFlagClient(fx3Client);
    }

    @Bean
    public MigrationDarkFeaturesManager migrationDarkFeaturesManager(DarkFeatureManager darkFeatureManager, FeatureFlagClient featureFlagClient) {
        return new MigrationDarkFeaturesManager(darkFeatureManager, featureFlagClient);
    }

    @Bean
    public SENSupplier sENSupplier(LicenseHandler licenseHandler) {
        return new SENSupplier(licenseHandler);
    }

    @Bean
    public DialectResolver dialectResolver(BootstrapManager bootstrapManager) {
        return new DialectResolver(bootstrapManager);
    }

    @Bean
    public MigrationTimeEstimationUtils migrationEstimationTimeResolve(DialectResolver dialectResolver, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new MigrationTimeEstimationUtils(dialectResolver, migrationDarkFeaturesManager);
    }

    @Bean
    public SessionFactorySupplier sessionFactorySupplier(ConnectionProvider connectionProvider, DialectResolver dialectResolver) {
        return new DefaultSessionFactorySupplier(connectionProvider, dialectResolver);
    }

    @Bean
    public MigrationUrlProviderFactory migrationUrlProviderFactory() {
        return new MigrationUrlProviderFactory();
    }

    @Bean
    public MigrationAgentConfiguration migrationAgentConfiguration(DialectResolver dialectResolver, MigrationUrlProviderFactory migrationUrlProviderFactory, ConcurrencySettingsService concurrencySettingsService) {
        return new MigrationAgentConfiguration(dialectResolver, migrationUrlProviderFactory, concurrencySettingsService);
    }

    @Bean
    public PluginTransactionTemplate defaultPluginTransactionTemplate(SessionFactorySupplier sessionFactorySupplier) {
        return new DefaultPluginTransactionTemplate(sessionFactorySupplier);
    }

    @Bean
    public EntityManagerTemplate entityManagerTemplate(SessionFactorySupplier sessionFactorySupplier) {
        return new DefaultEntityManagerTemplate(sessionFactorySupplier);
    }

    @Bean
    public UserMappingsFileManager userMappingsFileManager(BootstrapManager bootstrapManager) {
        return new UserMappingsFileManager(bootstrapManager);
    }

    @Bean
    public UserAgentInterceptor userAgentInterceptor(PluginVersionManager pluginVersionManager, SystemInformationService systemInformationService, SENSupplier senProvider) {
        return new UserAgentInterceptor(pluginVersionManager, systemInformationService, senProvider);
    }

    @Bean
    public AppOutdatedInfoProvider appOutdatedInfoProvider(MigrationAgentConfiguration configuration, LicenseService licenseService, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new AppOutdatedInfoProvider(configuration, licenseService, okHttpProxyBuilder);
    }

    @Bean
    public PluginVersionManager defaultPluginVersionManager(MigrationAgentConfiguration migrationAgentConfiguration, PluginAccessor pluginAccessor, AppOutdatedInfoProvider appOutdatedInfoProvider, SchedulerService schedulerService, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
        return new DefaultPluginVersionManager(migrationAgentConfiguration, pluginAccessor, appOutdatedInfoProvider, schedulerService, pluginSettingsFactory, transactionTemplate);
    }

    @Bean
    public PluginManager pluginManager(PluginAccessor pluginAccessor, PluginMetadataManager pluginMetadataManager, MigrationAppAggregatorService migrationAppAggregatorService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new PluginManager(pluginAccessor, pluginMetadataManager, migrationAppAggregatorService, migrationDarkFeaturesManager);
    }

    @Bean
    public MigrationAppAggregatorService aggregatorService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, CacheManager cacheManager, LicenseHandler licenseHandler, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new MigrationAppAggregatorService(configuration, userAgentInterceptor, cacheManager, licenseHandler, okHttpProxyBuilder);
    }

    @Bean
    public MigrationMetadataAggregatorService migrationMetadataAggregatorService(SystemInformationService sysInfoService, PluginVersionManager pluginVersionManager, LicenseHandler licenseHandler, EnterpriseGatekeeperClient enterpriseGatekeeperClient, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SpaceStore spaceStore, CloudSiteStore cloudSiteStore) {
        return new MigrationMetadataAggregatorService(sysInfoService, pluginVersionManager, licenseHandler, enterpriseGatekeeperClient, migrationDarkFeaturesManager, spaceStore, cloudSiteStore);
    }

    @Bean
    public NonceService nonceService(MigrationAgentConfiguration configuration) {
        return new NonceService(configuration);
    }

    @Bean
    public FileServiceManager fileServiceManager(BootstrapManager bootstrapManager) {
        return new FileServiceManager(bootstrapManager);
    }

    @Bean
    public BundleContext bundleContext() {
        return FrameworkUtil.getBundle(this.getClass()).getBundleContext();
    }

    @Bean
    public OkHttpClientSingleton okHttpClientSingleton() {
        return new OkHttpClientSingleton();
    }

    @Bean
    public HttpProxyStrategy httpProxyStrategy(OkHttpClientSingleton okHttpClientSingleton) {
        return new HttpProxyStrategy(okHttpClientSingleton);
    }

    @Bean
    public HttpsProxyStrategy httpsProxyStrategy(OkHttpClientSingleton okHttpClientSingleton) {
        return new HttpsProxyStrategy(okHttpClientSingleton);
    }

    @Bean
    public ProxyStrategyFactory proxyStrategyFactory(Set<ProxyStrategy> strategySet) {
        return new ProxyStrategyFactory(strategySet);
    }

    @Bean
    public OKHttpProxyBuilder okHttpProxyBuilder(DarkFeatureManager darkFeatureManager, ProxyStrategyFactory proxyStrategyFactory) {
        return new OKHttpProxyBuilder(darkFeatureManager, proxyStrategyFactory);
    }

    @Bean
    @ConditionalOnClass(value={"com.atlassian.confluence.util.PatternLayoutWithContext", "org.apache.log4j.RollingFileAppender"})
    public MigrationsLogAppender migrationsLogAppender(ApplicationConfiguration applicationConfiguration) {
        return new MigrationsLogAppender(applicationConfiguration);
    }

    @Bean
    public MigrationOrchestratorClient migrationOrchestratorClient(MigrationAgentConfiguration migrationAgentConfiguration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new MigrationOrchestratorClient(migrationAgentConfiguration, userAgentInterceptor, okHttpProxyBuilder);
    }

    @Bean
    public TeamCalendarHelper teamCalendarHelper(PluginManager pluginManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager, JdbcConfluenceStore jdbcConfluenceStore) {
        return new TeamCalendarHelper(pluginManager, migrationDarkFeaturesManager, jdbcConfluenceStore);
    }

    @Bean
    public AutowireHelper autowireHelper() {
        return new AutowireHelper();
    }

    @Bean
    public WarnLogFileWriter warnLogFileWriter(BootstrapManager bootstrapManager) {
        return new WarnLogFileWriter(bootstrapManager);
    }

    @Bean
    public SpaceKeyResolver spaceKeyResolver(ApplicationLinkService applicationLinkService, WarnLogFileWriter warnLogFileWriter) {
        return new SpaceKeyResolver(applicationLinkService, warnLogFileWriter);
    }

    @Bean
    public Clock time() {
        return Clock.system(ZoneId.systemDefault());
    }
}

