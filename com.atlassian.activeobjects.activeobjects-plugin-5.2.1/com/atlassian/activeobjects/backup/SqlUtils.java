/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.java.ao.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SqlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"net.java.ao.sql");

    private SqlUtils() {
        throw new UnsupportedOperationException("Not for instantiation");
    }

    static Iterable<TableColumnPair> tableColumnPairs(Iterable<Table> tables) {
        return StreamSupport.stream(tables.spliterator(), false).flatMap(table -> SqlUtils.getAutoIncrementColumns(table).stream()).collect(Collectors.toList());
    }

    static void executeUpdate(ImportExportErrorService errorService, String tableName, Statement s, String sql) {
        try {
            if (!StringUtils.isBlank(sql)) {
                LOGGER.debug(sql);
                s.executeUpdate(sql);
            }
        }
        catch (SQLException e) {
            SqlUtils.onSqlException(errorService, tableName, sql, e);
        }
    }

    private static void onSqlException(ImportExportErrorService errorService, String table, String sql, SQLException e) {
        if (sql.startsWith("DROP") && e.getMessage().contains("does not exist")) {
            LOGGER.debug("Ignoring exception for SQL <{}>", (Object)sql, (Object)e);
            return;
        }
        throw errorService.newImportExportSqlException(table, "Error executing update for SQL statement '" + sql + "'", e);
    }

    static void executeUpdate(ImportExportErrorService errorService, String tableName, Connection connection, String sql) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            SqlUtils.executeUpdate(errorService, tableName, stmt, sql);
        }
        catch (SQLException e) {
            try {
                throw errorService.newImportExportSqlException(tableName, "", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeQuietly(new Statement[]{stmt});
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(new Statement[]{stmt});
    }

    static int getIntFromResultSet(ImportExportErrorService errorService, String tableName, ResultSet res) {
        try {
            return res.next() ? res.getInt(1) : 1;
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(tableName, "Error getting int value from result set.", e);
        }
    }

    static ResultSet executeQuery(ImportExportErrorService errorService, String tableName, Statement s, String sql) {
        try {
            return s.executeQuery(sql);
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(tableName, "Error executing query for SQL statement '" + sql + "'", e);
        }
    }

    private static List<TableColumnPair> getAutoIncrementColumns(@Nonnull Table table) {
        return table.getColumns().stream().filter(Column::isAutoIncrement).map(column -> new TableColumnPair(table, (Column)column)).collect(Collectors.toList());
    }

    static class TableColumnPair {
        final Table table;
        final Column column;

        public TableColumnPair(Table table, Column column) {
            this.table = Objects.requireNonNull(table);
            this.column = Objects.requireNonNull(column);
        }
    }
}

