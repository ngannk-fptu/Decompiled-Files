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
import java.util.Collection;
import net.java.ao.DatabaseProvider;
import net.java.ao.schema.NameConverters;

public final class OracleSequencesAroundImporter
extends NoOpAroundImporter {
    private final ImportExportErrorService errorService;
    private final DatabaseProvider provider;
    private final NameConverters nameConverters;

    public OracleSequencesAroundImporter(ImportExportErrorService errorService, DatabaseProvider provider, NameConverters nameConverters) {
        this.errorService = (ImportExportErrorService)Preconditions.checkNotNull((Object)errorService);
        this.provider = (DatabaseProvider)Preconditions.checkNotNull((Object)provider);
        this.nameConverters = (NameConverters)Preconditions.checkNotNull((Object)nameConverters);
    }

    @Override
    public void before(NodeParser node, ImportConfiguration configuration, Context context) {
        if (this.isOracle(configuration)) {
            this.doBefore(context);
        }
    }

    @Override
    public void after(NodeParser node, ImportConfiguration configuration, Context context) {
        if (this.isOracle(configuration)) {
            this.doAfter(context);
        }
    }

    private boolean isOracle(ImportConfiguration configuration) {
        return DatabaseInformations.Database.Type.ORACLE.equals((Object)DatabaseInformations.database(configuration.getDatabaseInformation()).getType());
    }

    private void doBefore(Context context) {
        Collection<Table> tables = context.getAll(Table.class);
        this.disableAllTriggers(tables);
        this.dropAllSequences(tables);
    }

    private void doAfter(Context context) {
        Collection<Table> tables = context.getAll(Table.class);
        this.createAllSequences(tables);
        this.enableAllTriggers(tables);
    }

    private void disableAllTriggers(Collection<Table> tables) {
        Connection connection = null;
        try {
            connection = this.provider.getConnection();
            for (Table table : tables) {
                SqlUtils.executeUpdate(this.errorService, table.getName(), connection, "ALTER TABLE " + this.tableName(connection, table.getName()) + " DISABLE ALL TRIGGERS");
            }
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "", e);
        }
        finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    private void dropAllSequences(Collection<Table> tables) {
        Connection connection = null;
        try {
            connection = this.provider.getConnection();
            for (SqlUtils.TableColumnPair tcp : SqlUtils.tableColumnPairs(tables)) {
                this.dropSequence(connection, tcp);
            }
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "", e);
        }
        finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    private void dropSequence(Connection connection, SqlUtils.TableColumnPair tcp) {
        SqlUtils.executeUpdate(this.errorService, tcp.table.getName(), connection, "DROP SEQUENCE " + this.sequenceName(connection, tcp));
    }

    private void createAllSequences(Collection<Table> tables) {
        Connection connection = null;
        try {
            connection = this.provider.getConnection();
            for (SqlUtils.TableColumnPair tcp : SqlUtils.tableColumnPairs(tables)) {
                this.createSequence(connection, tcp);
            }
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "", e);
        }
        finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    private void createSequence(Connection connection, SqlUtils.TableColumnPair tcp) {
        Statement maxStmt = null;
        String tableName = tcp.table.getName();
        try {
            maxStmt = connection.createStatement();
            ResultSet res = SqlUtils.executeQuery(this.errorService, tableName, maxStmt, "SELECT MAX(" + JdbcUtils.quote(this.errorService, tableName, connection, tcp.column.getName()) + ") FROM " + this.tableName(connection, tableName));
            int max = SqlUtils.getIntFromResultSet(this.errorService, tableName, res);
            SqlUtils.executeUpdate(this.errorService, tableName, connection, "CREATE SEQUENCE " + this.sequenceName(connection, tcp) + " INCREMENT BY 1 START WITH " + (max + 1) + " NOMAXVALUE MINVALUE " + (max + 1));
        }
        catch (SQLException e) {
            try {
                throw this.errorService.newImportExportSqlException(tableName, "", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeQuietly(new Statement[]{maxStmt});
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(new Statement[]{maxStmt});
    }

    private void enableAllTriggers(Collection<Table> tables) {
        Connection connection = null;
        try {
            connection = this.provider.getConnection();
            for (Table table : tables) {
                SqlUtils.executeUpdate(this.errorService, table.getName(), connection, "ALTER TABLE " + this.tableName(connection, table.getName()) + " ENABLE ALL TRIGGERS");
            }
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "", e);
        }
        finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    private String tableName(Connection connection, String tableName) {
        String schema = OracleSequencesAroundImporter.isBlank(this.provider.getSchema()) ? null : this.provider.getSchema();
        String quoted = JdbcUtils.quote(this.errorService, tableName, connection, tableName);
        return schema != null ? schema + "." + quoted : quoted;
    }

    private String sequenceName(Connection connection, SqlUtils.TableColumnPair tcp) {
        String schema = OracleSequencesAroundImporter.isBlank(this.provider.getSchema()) ? null : this.provider.getSchema();
        String quoted = JdbcUtils.quote(this.errorService, tcp.table.getName(), connection, this.nameConverters.getSequenceNameConverter().getName(tcp.table.getName(), tcp.column.getName()));
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

