/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 */
package com.atlassian.dbexporter.exporter;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.exporter.Exporter;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeCreator;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import com.google.common.collect.Iterables;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Objects;

public final class DataExporter
implements Exporter {
    private final ImportExportErrorService errorService;
    private final String schema;

    public DataExporter(ImportExportErrorService errorService, String schema) {
        this.errorService = Objects.requireNonNull(errorService);
        this.schema = DataExporter.isBlank(schema) ? null : schema;
    }

    @Override
    public void export(NodeCreator node, ExportConfiguration configuration, Context context) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(ProgressMonitor.Task.TABLES_DATA, new Object[0]);
        JdbcUtils.withConnection(this.errorService, configuration.getConnectionProvider(), connection -> {
            for (String table : this.getTableNames(context)) {
                JdbcUtils.withNoAutoCommit(this.errorService, connection, connection1 -> {
                    this.exportTable(table, connection1, node, monitor, configuration.getEntityNameProcessor());
                    return null;
                });
            }
            node.closeEntity();
            return null;
        });
        monitor.end(ProgressMonitor.Task.TABLES_DATA, new Object[0]);
    }

    private Iterable<String> getTableNames(Context context) {
        return Iterables.transform(context.getAll(Table.class), Table::getName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void exportTable(String table, Connection connection, NodeCreator node, ProgressMonitor monitor, EntityNameProcessor entityNameProcessor) {
        monitor.begin(ProgressMonitor.Task.TABLE_DATA, entityNameProcessor.tableName(table));
        NodeBackup.TableDataNode.add(node, entityNameProcessor.tableName(table));
        Statement statement = JdbcUtils.createStatement(this.errorService, table, connection);
        ResultSet result = null;
        try {
            result = this.executeQueryWithFetchSize100(table, statement, "SELECT * FROM " + this.tableName(table, connection));
            ResultSetMetaData meta = this.resultSetMetaData(table, result);
            this.writeColumnDefinitions(table, node, meta, entityNameProcessor);
            while (this.next(table, result)) {
                this.exportRow(table, node, result, monitor);
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeQuietly(result, statement);
            throw throwable;
        }
        JdbcUtils.closeQuietly(result, statement);
        monitor.end(ProgressMonitor.Task.TABLE_DATA, entityNameProcessor.tableName(table));
        node.closeEntity();
    }

    private String tableName(String table, Connection connection) {
        String quoted = JdbcUtils.quote(this.errorService, table, connection, table);
        return this.schema != null ? this.schema + "." + quoted : quoted;
    }

    private void exportRow(String table, NodeCreator node, ResultSet result, ProgressMonitor monitor) {
        monitor.begin(ProgressMonitor.Task.TABLE_ROW, new Object[0]);
        ResultSetMetaData metaData = this.resultSetMetaData(table, result);
        NodeBackup.RowDataNode.add(node);
        block10: for (int col = 1; col <= this.columnCount(table, metaData); ++col) {
            switch (this.columnType(table, metaData, col)) {
                case -6: 
                case -5: 
                case 4: 
                case 5: {
                    this.appendInteger(table, result, col, node);
                    continue block10;
                }
                case 2: {
                    if (this.scale(table, metaData, col) > 0 || this.precision(table, metaData, col) == 126) {
                        this.appendDouble(table, result, col, node);
                        continue block10;
                    }
                    if (this.precision(table, metaData, col) == 1) {
                        this.appendBoolean(table, result, col, node);
                        continue block10;
                    }
                    this.appendInteger(table, result, col, node);
                    continue block10;
                }
                case -16: 
                case -15: 
                case -9: 
                case -1: 
                case 1: 
                case 12: {
                    String s = this.getString(table, result, col);
                    NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : s);
                    continue block10;
                }
                case -7: 
                case 16: {
                    this.appendBoolean(table, result, col, node);
                    continue block10;
                }
                case 3: 
                case 8: {
                    this.appendDouble(table, result, col, node);
                    continue block10;
                }
                case 93: {
                    Timestamp t = this.getTimestamp(table, result, col);
                    NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : t);
                    continue block10;
                }
                case 2005: 
                case 2011: {
                    String c = this.getClobAsString(table, result, col);
                    NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : c);
                    continue block10;
                }
                case -4: 
                case -3: 
                case -2: 
                case 2004: {
                    byte[] b = this.getBinary(table, result, col);
                    NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : b);
                    continue block10;
                }
                default: {
                    throw this.errorService.newImportExportException(table, String.format("Cannot encode value for unsupported column type: \"%s\" (%d) of column %s.%s", this.columnTypeName(table, metaData, col), this.columnType(table, metaData, col), table, this.columnName(table, metaData, col)));
                }
            }
        }
        monitor.end(ProgressMonitor.Task.TABLE_ROW, new Object[0]);
        node.closeEntity();
    }

    private void appendBoolean(String table, ResultSet result, int col, NodeCreator node) {
        boolean b = this.getBoolean(table, result, col);
        NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : Boolean.valueOf(b));
    }

    private void appendInteger(String table, ResultSet result, int col, NodeCreator node) {
        BigDecimal bd = this.getBigDecimal(table, result, col);
        NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : bd.toBigInteger());
    }

    private void appendDouble(String table, ResultSet result, int col, NodeCreator node) {
        double d = this.getDouble(table, result, col);
        NodeBackup.RowDataNode.append(node, this.wasNull(table, result) ? null : BigDecimal.valueOf(d));
    }

    private void writeColumnDefinitions(String table, NodeCreator node, ResultSetMetaData metaData, EntityNameProcessor entityNameProcessor) {
        for (int i = 1; i <= this.columnCount(table, metaData); ++i) {
            String columnName = entityNameProcessor.columnName(this.columnName(table, metaData, i));
            NodeBackup.ColumnDataNode.add(node, columnName).closeEntity();
        }
    }

    private ResultSetMetaData resultSetMetaData(String table, ResultSet result) {
        try {
            return result.getMetaData();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get result set metadata", e);
        }
    }

    private int scale(String table, ResultSetMetaData metaData, int col) {
        try {
            return metaData.getScale(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get scale for col #" + col + " from result set meta data", e);
        }
    }

    private int precision(String table, ResultSetMetaData metaData, int col) {
        try {
            return metaData.getPrecision(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get scale for col #" + col + " from result set meta data", e);
        }
    }

    private int columnCount(String table, ResultSetMetaData metaData) {
        try {
            return metaData.getColumnCount();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get column count from result set metadata", e);
        }
    }

    private int columnType(String table, ResultSetMetaData metaData, int col) {
        try {
            return metaData.getColumnType(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get column type for col #" + col + " from result set meta data", e);
        }
    }

    private String columnTypeName(String table, ResultSetMetaData metaData, int col) {
        try {
            return metaData.getColumnTypeName(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get column type name for col #" + col + " from result set meta data", e);
        }
    }

    private String columnName(String table, ResultSetMetaData metaData, int i) {
        try {
            return metaData.getColumnName(i);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get column #" + i + " name from result set meta data", e);
        }
    }

    private String getString(String table, ResultSet result, int col) {
        try {
            return result.getString(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get string value for col #" + col, e);
        }
    }

    private boolean getBoolean(String table, ResultSet result, int col) {
        try {
            return result.getBoolean(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get boolean value for col #" + col, e);
        }
    }

    private BigDecimal getBigDecimal(String table, ResultSet result, int col) {
        try {
            return result.getBigDecimal(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get big decimal value for col #" + col, e);
        }
    }

    private double getDouble(String table, ResultSet result, int col) {
        try {
            return result.getDouble(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get double value for col #" + col, e);
        }
    }

    private Timestamp getTimestamp(String table, ResultSet result, int col) {
        try {
            return result.getTimestamp(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get timestamp value for col #" + col, e);
        }
    }

    private String getClobAsString(String table, ResultSet result, int col) {
        try {
            Clob clob = result.getClob(col);
            return clob == null ? null : clob.getSubString(1L, (int)clob.length());
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get clob value for col #" + col, e);
        }
    }

    private byte[] getBinary(String table, ResultSet result, int col) {
        try {
            return result.getBytes(col);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get binary value for col #" + col, e);
        }
    }

    private boolean wasNull(String table, ResultSet result) {
        try {
            return result.wasNull();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not figure out whether value was NULL", e);
        }
    }

    private boolean next(String table, ResultSet result) {
        try {
            return result.next();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not get next for result set", e);
        }
    }

    private ResultSet executeQueryWithFetchSize100(String table, Statement statement, String sql) {
        try {
            statement.setFetchSize(100);
            return statement.executeQuery(sql);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(table, "Could not execute query '" + sql + "' with fetch size " + 100, e);
        }
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

