/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.dbexporter.exporter;

import com.atlassian.dbexporter.ConnectionProvider;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.exporter.DatabaseInformationReader;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public final class ConnectionProviderInformationReader
implements DatabaseInformationReader {
    private final ImportExportErrorService errorService;
    private final ConnectionProvider connectionProvider;

    public ConnectionProviderInformationReader(ImportExportErrorService errorService, ConnectionProvider connectionProvider) {
        this.errorService = Objects.requireNonNull(errorService);
        this.connectionProvider = Objects.requireNonNull(connectionProvider);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<String, String> get() {
        ImmutableMap immutableMap;
        Connection connection = null;
        try {
            ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
            connection = this.getConnection();
            DatabaseMetaData metaData = JdbcUtils.metadata(this.errorService, connection);
            mapBuilder.put((Object)"database.name", (Object)this.getDatabaseName(metaData));
            mapBuilder.put((Object)"database.version", (Object)this.getDatabaseVersion(metaData));
            mapBuilder.put((Object)"database.minorVersion", (Object)this.getDatabaseMinorVersion(metaData));
            mapBuilder.put((Object)"database.majorVersion", (Object)this.getDatabaseMajorVersion(metaData));
            mapBuilder.put((Object)"driver.name", (Object)this.getDriverName(metaData));
            mapBuilder.put((Object)"driver.version", (Object)this.getDriverVersion(metaData));
            immutableMap = mapBuilder.build();
        }
        catch (Throwable throwable) {
            JdbcUtils.closeQuietly(connection);
            throw throwable;
        }
        JdbcUtils.closeQuietly(connection);
        return immutableMap;
    }

    private Connection getConnection() {
        try {
            return this.connectionProvider.getConnection();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get connection from provider", e);
        }
    }

    private String getDatabaseName(DatabaseMetaData metaData) {
        try {
            return metaData.getDatabaseProductName();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get database product name from metadata", e);
        }
    }

    private String getDatabaseVersion(DatabaseMetaData metaData) {
        try {
            return metaData.getDatabaseProductVersion();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get database product version from metadata", e);
        }
    }

    private String getDatabaseMinorVersion(DatabaseMetaData metaData) {
        try {
            return String.valueOf(metaData.getDatabaseMinorVersion());
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get database minor version from metadata", e);
        }
    }

    private String getDatabaseMajorVersion(DatabaseMetaData metaData) {
        try {
            return String.valueOf(metaData.getDatabaseMajorVersion());
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get database major version from metadata", e);
        }
    }

    private String getDriverName(DatabaseMetaData metaData) {
        try {
            return metaData.getDriverName();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get driver name from metadata", e);
        }
    }

    private String getDriverVersion(DatabaseMetaData metaData) {
        try {
            return metaData.getDriverVersion();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get driver version from metadata", e);
        }
    }
}

