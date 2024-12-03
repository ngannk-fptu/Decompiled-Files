/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.backup.SqlUtils;
import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformations;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.NoOpAroundImporter;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import com.atlassian.dbexporter.node.NodeParser;
import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.java.ao.DatabaseProvider;

public final class PostgresSequencesAroundImporter
extends NoOpAroundImporter {
    private final ImportExportErrorService errorService;
    private final DatabaseProvider provider;

    public PostgresSequencesAroundImporter(ImportExportErrorService errorService, DatabaseProvider provider) {
        this.errorService = (ImportExportErrorService)Preconditions.checkNotNull((Object)errorService);
        this.provider = (DatabaseProvider)Preconditions.checkNotNull((Object)provider);
    }

    @Override
    public void after(NodeParser node, ImportConfiguration configuration, Context context) {
        if (this.isPostgres(configuration)) {
            this.updateSequences(configuration, context);
        }
    }

    private boolean isPostgres(ImportConfiguration configuration) {
        return DatabaseInformations.Database.Type.POSTGRES.equals((Object)DatabaseInformations.database(configuration.getDatabaseInformation()).getType());
    }

    private void updateSequences(ImportConfiguration configuration, Context context) {
        EntityNameProcessor entityNameProcessor = configuration.getEntityNameProcessor();
        for (SqlUtils.TableColumnPair tableColumnPair : SqlUtils.tableColumnPairs(context.getAll(Table.class))) {
            String tableName = entityNameProcessor.tableName(tableColumnPair.table.getName());
            String columnName = entityNameProcessor.columnName(tableColumnPair.column.getName());
            this.updateSequence(tableName, columnName);
        }
    }

    private void updateSequence(String tableName, String columnName) {
        Connection connection = null;
        Statement maxStmt = null;
        Statement alterSeqStmt = null;
        try {
            connection = this.provider.getConnection();
            maxStmt = connection.createStatement();
            ResultSet res = SqlUtils.executeQuery(this.errorService, tableName, maxStmt, this.max(connection, tableName, columnName));
            int max = SqlUtils.getIntFromResultSet(this.errorService, tableName, res);
            alterSeqStmt = connection.createStatement();
            SqlUtils.executeUpdate(this.errorService, tableName, alterSeqStmt, this.alterSequence(connection, tableName, columnName, max + 1));
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        }
        catch (SQLException e) {
            try {
                throw this.errorService.newImportExportSqlException(tableName, "", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeQuietly(maxStmt, alterSeqStmt);
                JdbcUtils.closeQuietly(connection);
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(maxStmt, alterSeqStmt);
        JdbcUtils.closeQuietly(connection);
    }

    private String max(Connection connection, String tableName, String columnName) {
        return "SELECT MAX(" + JdbcUtils.quote(this.errorService, tableName, connection, columnName) + ") FROM " + this.tableName(connection, tableName);
    }

    private String tableName(Connection connection, String tableName) {
        String schema = PostgresSequencesAroundImporter.isBlank(this.provider.getSchema()) ? null : this.provider.getSchema();
        String quoted = JdbcUtils.quote(this.errorService, tableName, connection, tableName);
        return schema != null ? schema + "." + quoted : quoted;
    }

    private String alterSequence(Connection connection, String tableName, String columnName, int val) {
        return "ALTER SEQUENCE " + this.sequenceName(connection, tableName, columnName) + " RESTART WITH " + val;
    }

    private String sequenceName(Connection connection, String tableName, String columnName) {
        String schema = PostgresSequencesAroundImporter.isBlank(this.provider.getSchema()) ? null : this.provider.getSchema();
        String quoted = JdbcUtils.quote(this.errorService, tableName, connection, tableName + "_" + columnName + "_seq");
        return schema != null ? schema + "." + quoted : quoted;
    }

    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

