/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.google.common.collect.ImmutableMap
 *  javax.inject.Inject
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.HealthCheckRegistrar;
import com.atlassian.confluence.impl.health.checks.AttachmentDataFileStoreCheck;
import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.impl.health.checks.DatabaseCollationHealthCheck;
import com.atlassian.confluence.impl.health.checks.DatabaseSetupHealthCheck;
import com.atlassian.confluence.impl.health.checks.DbConnectionHealthCheck;
import com.atlassian.confluence.impl.health.checks.DbHealthCheckHelper;
import com.atlassian.confluence.impl.health.checks.FakeHealthCheck;
import com.atlassian.confluence.impl.health.checks.HomeHealthCheck;
import com.atlassian.confluence.impl.health.checks.HttpThreadsVsDbConnectionsHealthCheck;
import com.atlassian.confluence.impl.health.checks.LicenseValidationHealthCheck;
import com.atlassian.confluence.impl.health.checks.MSSQLDriverMigrationHealthCheck;
import com.atlassian.confluence.impl.health.checks.MySqlJdbcUrlHealthCheck;
import com.atlassian.confluence.impl.health.checks.OperatingSystemFreeMemoryHealthCheck;
import com.atlassian.confluence.impl.health.checks.rules.DataSourceSetupRule;
import com.atlassian.confluence.impl.health.checks.rules.DbConnectionPoolRule;
import com.atlassian.confluence.impl.health.checks.rules.HealthCheckRule;
import com.atlassian.confluence.impl.health.checks.rules.HttpThreadsVsDbConnectionPoolRule;
import com.atlassian.confluence.impl.health.checks.rules.MsSqlCollationRule;
import com.atlassian.confluence.impl.health.checks.rules.MySqlCollationRule;
import com.atlassian.confluence.impl.health.checks.rules.PostgresCollationRule;
import com.atlassian.confluence.impl.health.checks.rules.SqlServerSetupRule;
import com.atlassian.confluence.impl.health.checks.rules.TomcatHttpMaxThreadsRule;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckRegistry;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.DatabaseCollationVerifier;
import com.atlassian.confluence.util.db.DatabaseConfigHelper;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.spring.container.LazyComponentReference;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapHealthcheckRegistrarConfig {
    @Inject
    DatabaseConfigHelper databaseConfigHelper;
    @Inject
    ErrorMessageProvider errorMessageProvider;
    @Inject
    TomcatConfigHelper tomcatConfigHelper;
    @Inject
    DatabaseCollationVerifier databaseVerifier;
    @Inject
    SingleConnectionProvider databaseHelper;
    @Inject
    HibernateConfig hibernateConfig;
    @Inject
    DataSourceConfiguration dataSourceConfiguration;
    @Inject
    DbHealthCheckHelper dbHealthCheckHelper;
    @Inject
    BootstrapManager bootstrapManager;
    @Inject
    LicenseService licenseService;
    @Inject
    LicenseValidator licenseValidator;
    @Inject
    ApplicationConfiguration applicationConfig;
    @Inject
    ClusterConfigurationHelperInternal clusterConfigurationHelper;

    @Bean
    HealthCheckRegistrar healthCheckRegistrar(HealthCheckRegistry healthCheckRegistry) {
        return new HealthCheckRegistrar(healthCheckRegistry, this.healthchecks());
    }

    private List<HealthCheck> healthchecks() {
        return Arrays.asList(this.fakeHealthCheck(), this.homeHealthCheck(), this.httpThreadsVsDbConnectionsHealthCheck(), this.databaseCollationHealthCheck(), this.databaseSetupHealthCheck(), this.mssqlDriverMigrationHealthCheck(), this.licenseValidationHealthCheck(), this.operatingSystemFreeMemoryHealthCheck(), this.mySqlJdbcUrlHealthCheck(), this.dbConnectionHealthCheck(), this.attachmentStorageHealthCheck());
    }

    private HealthCheck fakeHealthCheck() {
        return new FakeHealthCheck();
    }

    private HealthCheck homeHealthCheck() {
        return new HomeHealthCheck();
    }

    private HealthCheck httpThreadsVsDbConnectionsHealthCheck() {
        return new HttpThreadsVsDbConnectionsHealthCheck(Arrays.asList(new DbConnectionPoolRule(this.databaseConfigHelper, this.errorMessageProvider), new TomcatHttpMaxThreadsRule(this.tomcatConfigHelper, this.errorMessageProvider), new HttpThreadsVsDbConnectionPoolRule(this.tomcatConfigHelper, this.databaseConfigHelper, this.errorMessageProvider)));
    }

    private HealthCheck databaseCollationHealthCheck() {
        return new DatabaseCollationHealthCheck((Map<String, HealthCheckRule>)ImmutableMap.of((Object)"mssql", (Object)new MsSqlCollationRule(this.errorMessageProvider, this.databaseVerifier, this.databaseHelper, this.hibernateConfig, new String[]{"SQL_Latin1_General_CP1_CS_AS"}), (Object)"mysql", (Object)new MySqlCollationRule(this.errorMessageProvider, this.databaseVerifier, this.databaseHelper, this.hibernateConfig, new String[]{"utf8mb4_bin"}), (Object)"postgresql", (Object)new PostgresCollationRule(this.errorMessageProvider, this.databaseVerifier, this.databaseHelper, this.hibernateConfig, new String[]{"%utf-8", "%utf8", "%1252"})), this.databaseConfigHelper);
    }

    private HealthCheck databaseSetupHealthCheck() {
        return new DatabaseSetupHealthCheck(this.databaseVendorSpecific("mssql", new SqlServerSetupRule(this.errorMessageProvider, this.databaseHelper, this.hibernateConfig)), new DataSourceSetupRule(this.errorMessageProvider, this.dataSourceConfiguration));
    }

    private HealthCheck mssqlDriverMigrationHealthCheck() {
        return new MSSQLDriverMigrationHealthCheck(this.dataSourceConfiguration, this.dbHealthCheckHelper, this.bootstrapManager);
    }

    private HealthCheck licenseValidationHealthCheck() {
        return new LicenseValidationHealthCheck(this.bootstrapManager, this.licenseService, this.licenseValidator);
    }

    private HealthCheck operatingSystemFreeMemoryHealthCheck() {
        return new OperatingSystemFreeMemoryHealthCheck(this.licenseService);
    }

    private HealthCheck mySqlJdbcUrlHealthCheck() {
        return new MySqlJdbcUrlHealthCheck(this.hibernateConfig, this.dataSourceConfiguration, this.databaseHelper, this.dbHealthCheckHelper);
    }

    private HealthCheck dbConnectionHealthCheck() {
        return new DbConnectionHealthCheck(this.databaseHelper, this.hibernateConfig, this.dbHealthCheckHelper);
    }

    private HealthCheck attachmentStorageHealthCheck() {
        return new AttachmentDataFileStoreCheck((Supplier<I18NBean>)new LazyComponentReference("i18NBean"), this.applicationConfig, (Supplier<AttachmentDataFileSystem>)new LazyComponentReference("attachmentDataFileSystem"), this.clusterConfigurationHelper, this.licenseService);
    }

    private HealthCheckRule databaseVendorSpecific(String targetVendor, HealthCheckRule delegate) {
        return parent -> {
            if (targetVendor.equals(this.databaseConfigHelper.getProductName().orElse(""))) {
                return delegate.validate(parent);
            }
            return Collections.emptyList();
        };
    }
}

