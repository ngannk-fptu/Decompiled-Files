/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.johnson.event.EventLevels
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.impl.health.checks.DbHealthCheckHelper;
import com.atlassian.confluence.impl.util.OptionalUtils;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventLevels;
import com.google.common.collect.ImmutableSet;
import java.net.URL;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlJdbcUrlHealthCheck
extends HealthCheckTemplate {
    private static final Logger log = LoggerFactory.getLogger(MySqlJdbcUrlHealthCheck.class);
    private static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/x/DZHPO");
    private final HibernateConfig hibernateConfig;
    private final DataSourceConfiguration dataSourceConfiguration;
    private final SingleConnectionProvider databaseHelper;
    private final DbHealthCheckHelper dbHealthCheckHelper;

    public MySqlJdbcUrlHealthCheck(HibernateConfig hibernateConfig, DataSourceConfiguration dataSourceConfiguration, SingleConnectionProvider databaseHelper, DbHealthCheckHelper dbHealthCheckHelper) {
        super(Collections.emptyList());
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.dataSourceConfiguration = Objects.requireNonNull(dataSourceConfiguration);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.dbHealthCheckHelper = Objects.requireNonNull(dbHealthCheckHelper);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return ImmutableSet.of((Object)((Object)LifecyclePhase.BOOTSTRAP_END));
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        return this.getJDBCUrl().map(this::checkConnectionUri).orElse(Collections.emptyList());
    }

    private Optional<String> getJDBCUrl() {
        Supplier[] supplierArray = new Supplier[2];
        supplierArray[0] = this.dataSourceConfiguration::getJdbcUrl;
        supplierArray[1] = () -> Optional.ofNullable(this.hibernateConfig.getHibernateProperties().getProperty("hibernate.connection.url"));
        return OptionalUtils.firstNonEmpty(supplierArray);
    }

    private List<HealthCheckResult> checkConnectionUri(String connectionUriString) {
        if (this.hibernateConfig.isMySql() && MySqlJdbcUrlHealthCheck.isUriIncompatibleWithMySql57(connectionUriString)) {
            ConnectionStatus status = this.canWorkWithCurrentConfiguration();
            switch (status) {
                case OTHER_FAIL: {
                    return Collections.emptyList();
                }
                case OK: {
                    return this.makeResult(ResultLevel.WARN);
                }
                case BAD_PARAM_FAIL: {
                    return this.makeResult(ResultLevel.ERROR);
                }
            }
            throw new RuntimeException("Unexpected value: " + status);
        }
        return Collections.emptyList();
    }

    private @NonNull EventLevel getEventSeverity(ResultLevel level) {
        return Objects.requireNonNull(level == ResultLevel.WARN ? EventLevels.warning() : EventLevels.fatal());
    }

    private List<HealthCheckResult> makeResult(ResultLevel level) {
        return this.createHealthCheckResult(this.createErrorMessage(level), level);
    }

    private List<HealthCheckResult> createHealthCheckResult(HealthCheckMessage.Builder messageFormatter, ResultLevel level) {
        HealthCheckMessage message = messageFormatter.build();
        return HealthCheckResult.fail(this, new Event(JohnsonEventType.DATABASE.eventType(), level == ResultLevel.WARN ? "We've found a problem with your database connection URL" : "We've found an error in your database connection URL", message.asHtml(), this.getEventSeverity(level)), KB_URL, "mysql-engine-type", message.asText());
    }

    private HealthCheckMessage.Builder createErrorMessage(ResultLevel level) {
        HealthCheckMessage.Builder builder = new HealthCheckMessage.Builder();
        builder.append("The connection URL in your ").tag("code", this.dbHealthCheckHelper.getJDBCUrlConfigLocationDescription()).append(" file contains the ").tag("code", "storage_engine").append(" parameter, ");
        if (level == ResultLevel.WARN) {
            builder.append("which has been deprecated. This should be replaced with the ");
        } else {
            builder.append("which is no longer allowed. This needs to be replaced with the ");
        }
        builder.tag("code", "default_storage_engine").append(" parameter.").lineBreak().append("See our documentation for more information on changing your connection URL.");
        return builder;
    }

    private ConnectionStatus canWorkWithCurrentConfiguration() {
        ConnectionStatus connectionStatus;
        block9: {
            Connection ignored = this.databaseHelper.getConnection(this.hibernateConfig.getHibernateProperties());
            try {
                connectionStatus = ConnectionStatus.OK;
                if (ignored == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    if (e.getMessage().contains("Unknown system variable 'storage_engine'")) {
                        return ConnectionStatus.BAD_PARAM_FAIL;
                    }
                    log.error("Exception occurred while trying to connect to the database: ", (Throwable)e);
                    return ConnectionStatus.OTHER_FAIL;
                }
            }
            ignored.close();
        }
        return connectionStatus;
    }

    private static boolean isUriIncompatibleWithMySql57(String uri) {
        String query = UrlUtils.getJdbcUrlQuery(uri);
        return !StringUtils.contains((CharSequence)query, (CharSequence)"default_storage_engine") && StringUtils.contains((CharSequence)query, (CharSequence)"storage_engine");
    }

    private static enum ResultLevel {
        WARN,
        ERROR;

    }

    private static enum ConnectionStatus {
        OK,
        BAD_PARAM_FAIL,
        OTHER_FAIL;

    }
}

