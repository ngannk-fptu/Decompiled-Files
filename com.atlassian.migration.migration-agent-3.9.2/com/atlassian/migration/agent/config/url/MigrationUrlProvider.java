/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.config.url;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.migration.agent.config.url.MigrationEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationUrlProvider {
    private Properties properties;
    private static final Logger log = LoggerFactory.getLogger(MigrationUrlProvider.class);
    private static final String MIGRATIONS_URL = "https://api-private.atlassian.com/migrations";

    public MigrationUrlProvider(MigrationEnvironment migrationEnvironment) {
        String propertiesFileName = migrationEnvironment.getFileName();
        this.properties = new Properties();
        try (InputStream inputStream = ClassLoaderUtils.getResourceAsStream((String)propertiesFileName, this.getClass());){
            this.properties.load(inputStream);
        }
        catch (IOException e) {
            log.warn("Couldn't load url from properties file", (Throwable)e);
        }
    }

    private String getProperty(String key, String defaultValue) {
        String propertiesVal = this.properties.getProperty(key, defaultValue);
        return System.getProperty(key, propertiesVal);
    }

    public String getMediaServiceUrl() {
        return this.getProperty("media.service.url", "https://api.media.atlassian.com");
    }

    public String getMigrationAppAggregatorUrl() {
        return this.getProperty("migration.aggregator.base.url", "https://api.atlassian.com/migration/aggregator");
    }

    public String getMigrationServiceBaseUrl() {
        return this.getProperty("migration.service.base.url", "https://api-private.atlassian.com/migration");
    }

    public String getUserMigrationServiceViaEGBaseUrl(String version) {
        return this.getProperty("users.migration.service.via.eg.base.url", MIGRATIONS_URL) + "/" + version;
    }

    public String getUserMigrationServiceBaseUrl() {
        return this.getProperty("users.migration.service.base.url", "https://api-private.atlassian.com/migration/users");
    }

    public String getMigrationMappingServiceBaseUrl(String version) {
        return this.getProperty("migration.mapping.service.base.url", MIGRATIONS_URL) + "/" + version;
    }

    public String getMigrationAnalyticsServiceBaseUrl() {
        return this.getProperty("migration.analytics.base.url", "https://api-private.atlassian.com/migration/analytics");
    }

    public String getAppMigrationServiceBaseUrl() {
        return this.getProperty("app.migration.service.base.url", "https://api.atlassian.com/app/migration");
    }

    public String getMigrationCatalogueServiceUrl(String version) {
        return this.getProperty("migration.catalogue.service.base.url", MIGRATIONS_URL) + "/" + version;
    }

    public String getMigrationGatewayUrl() {
        return this.getProperty("migration.gateway.url", "https://migration.atlassian.com");
    }

    public String getConfluenceCloudUrl(String version) {
        return this.getProperty("confluence.cloud.service.base.url", "https://api-private.atlassian.com/migrations/confluence") + "/" + version;
    }

    public String getMapiUrl(String version) {
        return this.getProperty("confluence.mapi.service.base.url", "https://api.atlassian.com/migrations/public") + "/" + version;
    }

    public String getPrcHostUrl() {
        return this.getProperty("prc.host.base.url", "https://api.atlassian.com");
    }

    public String getMigrationMetadataAggregatorUrl(String version) {
        return this.getProperty("migration.metadata.aggregator.service.base.url", "https://api.atlassian.com/migrations/metadata") + "/" + version;
    }

    public String getMigrationOrchestratorServiceBaseUrl() {
        return this.getProperty("migration.orchestrator.base.url", "https://api-private.atlassian.com/migration/orchestrator/api/plugin");
    }

    public String getFrontendTargetCloudEnv() {
        return this.getProperty("frontend.target.cloud.env", "production");
    }

    public String getFx3baseUrl() {
        return this.getProperty("fx3.base.url", "https://api.atlassian.com");
    }
}

