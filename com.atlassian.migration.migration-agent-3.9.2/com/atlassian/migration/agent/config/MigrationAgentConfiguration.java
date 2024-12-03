/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.config;

import com.atlassian.migration.agent.config.url.MigrationEnvironment;
import com.atlassian.migration.agent.config.url.MigrationUrlProvider;
import com.atlassian.migration.agent.config.url.MigrationUrlProviderFactory;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.service.impl.ConcurrencySettingsService;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.time.Duration;
import java.util.Optional;
import java.util.TimeZone;

public class MigrationAgentConfiguration {
    private static final String MIN_CONFLUENCE_SUPPORTED_VERSION = "6.15";
    private static final String MIN_TC_SUPPORTED_CONFLUENCE_VERSION = "7.11";
    private static final String MIN_SUPPORTED_TC_VERSION = "7.0";
    private static final String DEFAULT_FX3_ENVIRONMENT_KEY = "53169a2c-e168-4f53-8635-52fa1daf3bfd";
    private final MigrationUrlProviderFactory migrationUrlProviderFactory;
    private final ConcurrencySettingsService concurrencySettingsService;
    private final DbType dbType;
    private MigrationUrlProvider urlProvider;

    public MigrationAgentConfiguration(DialectResolver dialectResolver, MigrationUrlProviderFactory migrationUrlProviderFactory, ConcurrencySettingsService concurrencySettingsService) {
        this.dbType = dialectResolver.getDbType();
        this.migrationUrlProviderFactory = migrationUrlProviderFactory;
        this.concurrencySettingsService = concurrencySettingsService;
        this.setUrlProvider(MigrationEnvironment.DEFAULT);
    }

    public void setUrlProvider(MigrationEnvironment environment) {
        this.urlProvider = this.migrationUrlProviderFactory.createUrlProvider(environment);
    }

    public String getPluginKey() {
        return System.getProperty("migration.plugin.key", "com.atlassian.migration.agent");
    }

    public String getPluginVersion() {
        return System.getProperty("migration.plugin.version", "");
    }

    public boolean isFrontEndDevModeEnabled() {
        return Boolean.getBoolean("fe.dev.mode");
    }

    public String getMediaServiceUrl() {
        return this.urlProvider.getMediaServiceUrl();
    }

    public String getServerTimezone() {
        return TimeZone.getDefault().getID();
    }

    public String getMigrationAppAggregatorUrl() {
        return this.urlProvider.getMigrationAppAggregatorUrl();
    }

    public String getMigrationServiceBaseUrl() {
        return this.urlProvider.getMigrationServiceBaseUrl();
    }

    public String getUserMigrationServiceViaEGBaseUrl(String version) {
        return this.urlProvider.getUserMigrationServiceViaEGBaseUrl(version);
    }

    public String getUserMigrationServiceBaseUrl() {
        return this.urlProvider.getUserMigrationServiceBaseUrl();
    }

    public String getMigrationMappingServiceBaseUrl(String version) {
        return this.urlProvider.getMigrationMappingServiceBaseUrl(version);
    }

    public String getMigrationAnalyticsServiceBaseUrl() {
        return this.urlProvider.getMigrationAnalyticsServiceBaseUrl();
    }

    public String getAppMigrationServiceBaseUrl() {
        return this.urlProvider.getAppMigrationServiceBaseUrl();
    }

    public String getMigrationCatalogueServiceUrl(String version) {
        return this.urlProvider.getMigrationCatalogueServiceUrl(version);
    }

    public String getMigrationGatewayUrl() {
        return this.urlProvider.getMigrationGatewayUrl();
    }

    public String getConfluenceCloudUrl(String version) {
        return this.urlProvider.getConfluenceCloudUrl(version);
    }

    public String getMapiUrl(String version) {
        return this.urlProvider.getMapiUrl(version);
    }

    public String getMigrationMetadataAggregatorUrl(String version) {
        return this.urlProvider.getMigrationMetadataAggregatorUrl(version);
    }

    public String getPrcHostUrl() {
        return this.urlProvider.getPrcHostUrl();
    }

    public String getMigrationOrchestratorServiceBaseUrl() {
        return this.urlProvider.getMigrationOrchestratorServiceBaseUrl();
    }

    public String getFrontendTargetCloudEnv() {
        return this.urlProvider.getFrontendTargetCloudEnv();
    }

    public Integer getSpaceStatisticCalculationBatchLimit() {
        return Integer.getInteger("space.statistic.calculation.batch.limit", 1000);
    }

    public int getSpaceStatisticCalculationAnalyticsBatchErrorLimit() {
        return Integer.getInteger("space.statistic.calculation.analytics.batch.error.limit", 5);
    }

    public int getAppVendorCheckGlobalTimeout() {
        return Integer.getInteger("app_vendor_check.timeout.global.minutes", 10);
    }

    public int getAppVendorCheckPerCheckTimeout() {
        return Integer.getInteger("app_vendor_check.timeout.per_check.minutes", 5);
    }

    public long getAppVendorCheckCsvFileSizeInBytes() {
        return Long.getLong("app_vendor_check.max.csv_file_size.kb", 250L) * 1000L;
    }

    public Integer getDBQueryParameterLimit() {
        return Integer.getInteger("migration.service.db.queryparamlimit", 1000);
    }

    public boolean isBypassStargate() {
        return Boolean.getBoolean("stargate.service.bypass");
    }

    public boolean isSkipNonceCheck() {
        return Boolean.getBoolean("skip.nonce.check");
    }

    public int getAttachmentMigrationConcurrencyClusterMax() {
        return this.concurrencySettingsService.getAttachmentMigrationConcurrencyClusterMax();
    }

    public int getAttachmentMigrationConcurrencyNodeMax() {
        return this.concurrencySettingsService.getAttachmentMigrationConcurrencyNodeMax();
    }

    public Optional<Integer> getExportConcurrencyNodeMax() {
        return Optional.ofNullable(this.concurrencySettingsService.getExportConcurrencyNodeMax());
    }

    public Optional<Integer> getExportConcurrencyClusterMax() {
        return Optional.ofNullable(this.concurrencySettingsService.getExportConcurrencyClusterMax());
    }

    public int getUploadConcurrencyClusterMax() {
        return this.concurrencySettingsService.getUploadConcurrencyClusterMax();
    }

    public int getUploadConcurrencyNodeMax() {
        return this.concurrencySettingsService.getUploadConcurrencyNodeMax();
    }

    public int getImportConcurrencyClusterMax() {
        return this.concurrencySettingsService.getImportConcurrencyClusterMax();
    }

    public int getImportConcurrencyNodeMax() {
        return this.concurrencySettingsService.getImportConcurrencyNodeMax();
    }

    public int getAttachmentUploadBatchSize() {
        return Integer.getInteger("attachment.upload.batch.size", 1000);
    }

    public int getAttachmentUploadConcurrency() {
        return this.concurrencySettingsService.getAttachmentUploadConcurrency();
    }

    public int getAttachmentPrepareBatchSize() {
        return Integer.getInteger("attachment.prepare.batch.size", 1000);
    }

    @Deprecated
    public int getMaxConcurrentSpaceImportRequests() {
        return Integer.getInteger("max.concurrent.initiate.space.import.requests", 4);
    }

    public int getMaxStepExecutionThreads() {
        return Integer.getInteger("max.step.execution.threads", 32);
    }

    public int getAnalyticsSenderJobIntervalInSeconds() {
        return Integer.getInteger("migration.analytics.job.interval", 120);
    }

    public int getAnalyticsSenderBatchSize() {
        return Integer.getInteger("migration.analytics.batch.size", 100);
    }

    public int getAnalyticsSenderMaxWaitInMinutes() {
        return Integer.getInteger("migration.analytics.sender.max.wait", 45);
    }

    public int getAnalyticsSenderMaxEventLength() {
        return Integer.getInteger("migration.analytics.event.max.length", 10000);
    }

    public int getLimitPerPageRequest() {
        return Integer.getInteger("app.usage.limit.per.page.request", 100);
    }

    public int getBatchSizePerQuery() {
        return Integer.getInteger("app.usage.batch.size.per.query", 999);
    }

    public boolean isAnalyticsSenderDisabled() {
        return Boolean.getBoolean("migration.analytics.sender.disabled");
    }

    public boolean isMapiTaskStatusSenderDisabled() {
        return Boolean.getBoolean("migration.mapi.task.status.sender.disabled");
    }

    public int getMapiStatusSenderJobIntervalInSeconds() {
        return Integer.getInteger("migration.mapi.status.sender.job.interval", 30);
    }

    public int getDetectedUserEmailAnalyticsEventUsersBatchSize() {
        return Integer.getInteger("migration.analytics.detected.email.users.batch.size", 100);
    }

    public Duration getIntervalBetweenDetectedUserEmailAnalyticsRuns() {
        long days = Long.getLong("migration.analytics.detected.email.interval.days", 5L);
        return Duration.ofDays(days);
    }

    public String getMinConfluenceSupportedVersion() {
        return MIN_CONFLUENCE_SUPPORTED_VERSION;
    }

    public String getMinTCSupportedConfluenceVersion() {
        return MIN_TC_SUPPORTED_CONFLUENCE_VERSION;
    }

    public String getMinSupportedTCVersion() {
        return MIN_SUPPORTED_TC_VERSION;
    }

    public DbType getDBType() {
        return this.dbType;
    }

    public String getEncryptionKeyFromEnv() {
        return System.getenv("MIGRATION_ENCRYPTION_SECRET");
    }

    public String getFx3EnvironmentKey() {
        return System.getProperty("fx3.environment.key", DEFAULT_FX3_ENVIRONMENT_KEY);
    }

    public String getFx3baseUrl() {
        return this.urlProvider.getFx3baseUrl();
    }
}

