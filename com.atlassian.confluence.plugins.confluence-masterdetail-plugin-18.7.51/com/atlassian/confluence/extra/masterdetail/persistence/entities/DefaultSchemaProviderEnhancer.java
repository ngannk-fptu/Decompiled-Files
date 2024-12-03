/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.recovery.DbDumpException
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail.persistence.entities;

import com.atlassian.confluence.upgrade.recovery.DbDumpException;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.internal.querydsl.schema.DefaultSchemaProvider;
import com.atlassian.pocketknife.internal.querydsl.schema.ProductSchemaProvider;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.querydsl.core.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class DefaultSchemaProviderEnhancer
implements LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSchemaProviderEnhancer.class);
    private final DefaultSchemaProvider defaultSchemaProvider;
    private final DatabaseAccessor databaseAccessor;
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private String currentSchemaName;

    public DefaultSchemaProviderEnhancer(TransactionalExecutorFactory transactionalExecutorFactory, DefaultSchemaProvider defaultSchemaProvider, DatabaseAccessor databaseAccessor) {
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.defaultSchemaProvider = defaultSchemaProvider;
        this.databaseAccessor = databaseAccessor;
    }

    public void onStart() {
        TransactionalExecutor executor = this.transactionalExecutorFactory.createReadOnly();
        this.currentSchemaName = (String)executor.execute(connection -> {
            try {
                return this.parseSchema(connection.getMetaData());
            }
            catch (SQLException throwables) {
                logger.warn("Could not detect runtime schema name");
                return null;
            }
        });
        logger.info("Enhancing DefaultDialectConfiguration");
        Field schemaProviderField = ReflectionUtils.getFieldOrNull(DefaultSchemaProvider.class, "productSchemaProvider");
        if (schemaProviderField == null) {
            logger.warn("Could not get schemaProviderField");
            return;
        }
        schemaProviderField.setAccessible(true);
        try {
            ConfigSchemaProvider schemaProvider = new ConfigSchemaProvider(null);
            schemaProviderField.set(this.defaultSchemaProvider, schemaProvider);
        }
        catch (IllegalAccessException e) {
            logger.warn("Could not enhancing DefaultDialectConfiguration", (Throwable)e);
        }
        logger.info("Enhancing DefaultDialectConfiguration - DONE");
    }

    public void onStop() {
    }

    private String parseSchema(DatabaseMetaData metaData) throws SQLException {
        String confVersionTableNamePattern = this.toIdentifier(metaData, "CONFVERSION");
        try (ResultSet rs = metaData.getTables(null, null, confVersionTableNamePattern, new String[]{"TABLE"});){
            if (!rs.next()) {
                throw new DbDumpException("Could not find CONFVERSION table in the database");
            }
            String string = rs.getString("TABLE_SCHEM");
            return string;
        }
    }

    private String toIdentifier(DatabaseMetaData metaData, String mixedCaseIdentifier) throws SQLException {
        if (metaData.storesLowerCaseIdentifiers()) {
            return mixedCaseIdentifier.toLowerCase();
        }
        if (metaData.storesUpperCaseIdentifiers()) {
            return mixedCaseIdentifier.toUpperCase();
        }
        return mixedCaseIdentifier;
    }

    private class ConfigSchemaProvider
    extends ProductSchemaProvider {
        public ConfigSchemaProvider(TransactionalExecutorFactory executorFactory) {
            super(executorFactory);
        }

        @Override
        public Optional<String> getProductSchema() {
            return Optional.ofNullable(DefaultSchemaProviderEnhancer.this.currentSchemaName);
        }
    }
}

