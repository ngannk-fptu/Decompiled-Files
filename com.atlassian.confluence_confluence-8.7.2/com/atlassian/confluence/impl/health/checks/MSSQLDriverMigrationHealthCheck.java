/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.johnson.event.Event
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.impl.health.checks.DbHealthCheckHelper;
import com.atlassian.confluence.impl.util.OptionalUtils;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

public class MSSQLDriverMigrationHealthCheck
extends HealthCheckTemplate {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final DbHealthCheckHelper dbHealthCheckHelper;
    private AtlassianBootstrapManager bootstrapManager;

    public MSSQLDriverMigrationHealthCheck(DataSourceConfiguration dataSourceConfiguration, DbHealthCheckHelper dbHealthCheckHelper, AtlassianBootstrapManager bootstrapManager) {
        super(Collections.emptyList());
        this.dataSourceConfiguration = Objects.requireNonNull(dataSourceConfiguration);
        this.dbHealthCheckHelper = Objects.requireNonNull(dbHealthCheckHelper);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return ImmutableSet.of((Object)((Object)LifecyclePhase.BOOTSTRAP_END));
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        Supplier[] supplierArray = new Supplier[2];
        supplierArray[0] = this::findConfluenceCfgXmlUrl;
        supplierArray[1] = this.dataSourceConfiguration::getJdbcUrl;
        return OptionalUtils.firstNonEmpty(supplierArray).filter(this::isUrlJtds).map(url -> {
            HealthCheckMessage message = new HealthCheckMessage.Builder().withHeading("Your database is using an unsupported driver").append("The jTDS driver for Microsoft SQL Server is no longer supported. We were not able to automatically migrate your driver. To manually switch to the official Microsoft SQL Server JDBC driver, update the driver configuration in ").tag("code", this.dbHealthCheckHelper.getJDBCUrlConfigLocationDescription()).append(".").build();
            return HealthCheckResult.fail(this, new Event(JohnsonEventType.DATABASE.eventType(), message.getHeadline(), message.asHtml(), JohnsonEventLevel.FATAL.level()), UrlBuilder.createURL("https://confluence.atlassian.com/confkb/migrate-from-the-jtds-driver-to-the-supported-microsoft-sql-server-driver-in-confluence-6-4-or-later-939505122.html?utm_source=Install&utm_medium=in-product&utm_campaign=csseng_fy19_q1_server_confluence_errorstate"), "mssql-driver-error", message.asText());
        }).orElse(Collections.emptyList());
    }

    private boolean isUrlJtds(String url) {
        return StringUtils.startsWithIgnoreCase((CharSequence)url, (CharSequence)"jdbc:jtds:sqlserver");
    }

    private Optional<String> findConfluenceCfgXmlUrl() {
        ApplicationConfiguration applicationConfiguration = this.bootstrapManager.getApplicationConfig();
        return Optional.ofNullable((String)applicationConfiguration.getProperty((Object)"hibernate.connection.url"));
    }
}

