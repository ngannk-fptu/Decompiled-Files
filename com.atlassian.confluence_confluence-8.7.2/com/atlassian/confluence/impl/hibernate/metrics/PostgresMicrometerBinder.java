/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics
 *  javax.annotation.PostConstruct
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.DriverManagerDataSource
 */
package com.atlassian.confluence.impl.hibernate.metrics;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

final class PostgresMicrometerBinder {
    private final HibernateConfig hibernateConfig;
    private final MeterRegistry micrometerRegistry;

    PostgresMicrometerBinder(HibernateConfig hibernateConfig, MeterRegistry micrometerRegistry) {
        this.hibernateConfig = hibernateConfig;
        this.micrometerRegistry = micrometerRegistry;
    }

    @PostConstruct
    void bind() {
        Properties properties = this.hibernateConfig.getHibernateProperties();
        String driverClass = properties.getProperty("hibernate.connection.driver_class");
        if ("org.postgresql.Driver".equals(driverClass) && ConfluenceMicrometer.isMicrometerEnabled()) {
            String jdbcUrl = properties.getProperty("hibernate.connection.url");
            Properties connectionProps = ConnectionProviderInitiator.getConnectionProperties((Map)properties);
            DriverManagerDataSource dataSource = new DriverManagerDataSource(jdbcUrl, connectionProps);
            String databaseName = (String)new JdbcTemplate((DataSource)dataSource).execute(Connection::getCatalog);
            new PostgreSQLDatabaseMetrics((DataSource)dataSource, StringUtils.defaultString((String)databaseName, (String)"unknown")).bindTo(this.micrometerRegistry);
        }
    }
}

