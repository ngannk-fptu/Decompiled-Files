/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.pocketknife.internal.querydsl.cache.PKQCacheClearer;
import com.atlassian.pocketknife.internal.querydsl.schema.JdbcTableAndColumns;
import com.atlassian.pocketknife.internal.querydsl.schema.JdbcTableInspector;
import com.atlassian.pocketknife.internal.querydsl.schema.ProductSchemaProvider;
import com.atlassian.pocketknife.internal.querydsl.schema.SchemaProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class DefaultSchemaProvider
implements SchemaProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultSchemaProvider.class);
    @TenantAware(value=TenancyScope.UNRESOLVED)
    private final ConcurrentHashMap<UpperCaseNameKey, String> tableAndColumnNames;
    private final ProductSchemaProvider productSchemaProvider;
    private final PKQCacheClearer cacheClearer;
    private final JdbcTableInspector tableInspector;

    @Autowired
    public DefaultSchemaProvider(ProductSchemaProvider productSchemaProvider, JdbcTableInspector tableInspector, PKQCacheClearer cacheClearer) {
        this.productSchemaProvider = productSchemaProvider;
        this.tableInspector = tableInspector;
        this.cacheClearer = cacheClearer;
        this.tableAndColumnNames = new ConcurrentHashMap();
    }

    @PostConstruct
    void postConstruct() {
        this.cacheClearer.registerCacheClearing(this.tableAndColumnNames::clear);
    }

    @VisibleForTesting
    Map<UpperCaseNameKey, String> getTableAndColumnNames() {
        return this.tableAndColumnNames;
    }

    @Override
    public Optional<String> getProductSchema() {
        return this.productSchemaProvider.getProductSchema();
    }

    @Override
    public Optional<String> getTableName(Connection connection, String logicalTableName) {
        Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)logicalTableName) ? 1 : 0) != 0, (Object)"Table name is required");
        UpperCaseNameKey key = new UpperCaseNameKey(logicalTableName);
        String tableName = this.tableAndColumnNames.get(key);
        if (tableName == null) {
            this.cacheTableAndColumns(connection, logicalTableName);
            tableName = this.tableAndColumnNames.get(key);
        }
        return this.logMissing(tableName, logicalTableName, "table:" + logicalTableName);
    }

    @Override
    public Optional<String> getColumnName(Connection connection, String logicalTableName, String logicalColumnName) {
        Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)logicalTableName) ? 1 : 0) != 0, (Object)"Table name is required");
        Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)logicalColumnName) ? 1 : 0) != 0, (Object)"Column name is required");
        UpperCaseNameKey key = new UpperCaseNameKey(logicalTableName, logicalColumnName);
        String columnName = this.tableAndColumnNames.get(key);
        if (columnName == null) {
            this.cacheTableAndColumns(connection, logicalTableName);
            columnName = this.tableAndColumnNames.get(key);
        }
        return this.logMissing(columnName, logicalColumnName, "column:" + logicalTableName + "." + logicalColumnName);
    }

    private Optional<String> logMissing(@Nullable String physical, String logical, String targetName) {
        Optional<String> dbObj = Optional.ofNullable(physical);
        if (!dbObj.isPresent()) {
            log.warn(String.format("Could not find the physical database object for the logically named '%s' aka '%s'. Is this expected database state?", logical, targetName));
        }
        return dbObj;
    }

    private void cacheTableAndColumns(Connection connection, String logicalTableName) {
        JdbcTableAndColumns tableAndColumns = this.tableInspector.inspectTableAndColumns(connection, this.getProductSchema(), logicalTableName);
        if (tableAndColumns.getTableName().isDefined()) {
            String realTableName = (String)tableAndColumns.getTableName().get();
            UpperCaseNameKey tableKey = new UpperCaseNameKey(realTableName);
            this.tableAndColumnNames.put(tableKey, realTableName);
            for (String realColumnName : tableAndColumns.getColumnNames()) {
                UpperCaseNameKey columnKey = new UpperCaseNameKey(realTableName, realColumnName);
                this.tableAndColumnNames.put(columnKey, realColumnName);
            }
        }
    }

    private static class UpperCaseNameKey {
        private final String tableName;
        private final String columnName;

        private UpperCaseNameKey(@Nonnull String tableName) {
            this(tableName, (String)null);
        }

        private UpperCaseNameKey(@Nonnull String tableName, @Nullable String columnName) {
            this.tableName = ((String)Preconditions.checkNotNull((Object)tableName)).toUpperCase();
            this.columnName = columnName == null ? null : columnName.toUpperCase();
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            UpperCaseNameKey other = (UpperCaseNameKey)obj;
            return this.tableName.equals(other.tableName) && (this.columnName == null ? other.columnName == null : this.columnName.equals(other.columnName));
        }

        public int hashCode() {
            int result = this.tableName.hashCode();
            result = 31 * result + (this.columnName != null ? this.columnName.hashCode() : 0);
            return result;
        }
    }
}

