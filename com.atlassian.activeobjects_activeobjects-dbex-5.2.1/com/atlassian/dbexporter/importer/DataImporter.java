/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.BatchMode;
import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.DatabaseInformations;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.AbstractSingleNodeImporter;
import com.atlassian.dbexporter.importer.AroundImporter;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.ImporterUtils;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeParser;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class DataImporter
extends AbstractSingleNodeImporter {
    private final String schema;
    private final AroundTableImporter aroundTable;

    public DataImporter(ImportExportErrorService errorService, String schema, AroundTableImporter aroundTableImporter, List<AroundImporter> arounds) {
        super(errorService, arounds);
        this.schema = DataImporter.isBlank(schema) ? null : schema;
        this.aroundTable = Objects.requireNonNull(aroundTableImporter);
    }

    public DataImporter(ImportExportErrorService errorService, String schema, AroundTableImporter aroundTableImporter, AroundImporter ... arounds) {
        this(errorService, schema, aroundTableImporter, Arrays.asList((Object[])Objects.requireNonNull(arounds)));
    }

    @Override
    protected String getNodeName() {
        return "data";
    }

    @Override
    protected void doImportNode(final NodeParser node, final ImportConfiguration configuration, final Context context) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(ProgressMonitor.Task.TABLES_DATA, new Object[0]);
        JdbcUtils.withConnection(this.errorService, configuration.getConnectionProvider(), new JdbcUtils.JdbcCallable<Void>(){

            @Override
            public Void call(Connection connection) {
                try {
                    boolean autoCommit = connection.getAutoCommit();
                    try {
                        connection.setAutoCommit(false);
                        while ("data".equals(node.getName()) && !node.isClosed()) {
                            DataImporter.this.importTable(node, configuration, context, connection, configuration.getDatabaseInformation());
                            node.getNextNode();
                        }
                        connection.commit();
                    }
                    finally {
                        connection.setAutoCommit(autoCommit);
                    }
                }
                catch (SQLException e) {
                    throw DataImporter.this.errorService.newImportExportSqlException(null, "", e);
                }
                return null;
            }
        });
        monitor.end(ProgressMonitor.Task.TABLES_DATA, new Object[0]);
    }

    private NodeParser importTable(NodeParser node, ImportConfiguration configuration, Context context, Connection connection, DatabaseInformation databaseInformation) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        EntityNameProcessor entityNameProcessor = configuration.getEntityNameProcessor();
        String currentTable = entityNameProcessor.tableName(NodeBackup.TableDataNode.getName(node));
        monitor.begin(ProgressMonitor.Task.TABLE_DATA, currentTable);
        InserterBuilder builder = new InserterBuilder(this.errorService, this.schema, currentTable, configuration.getBatchMode());
        node = node.getNextNode();
        while (ImporterUtils.isNodeNotClosed(node, "column")) {
            String column = NodeBackup.ColumnDataNode.getName(node);
            builder.addColumn(entityNameProcessor.columnName(column));
            node = node.getNextNode();
            node = node.getNextNode();
        }
        Inserter inserter = builder.build(connection);
        long rowNum = 0L;
        try {
            this.aroundTable.before(configuration, context, currentTable, connection);
            while (ImporterUtils.isNodeNotClosed(node, "row")) {
                node = node.getNextNode();
                while (!node.isClosed()) {
                    inserter.setValue(node, databaseInformation);
                    node = node.getNextNode();
                }
                inserter.execute();
                ++rowNum;
                node = node.getNextNode();
            }
        }
        catch (SQLException e) {
            throw this.errorService.newRowImportSqlException(currentTable, rowNum, e);
        }
        finally {
            inserter.close();
            this.aroundTable.after(configuration, context, currentTable, connection);
        }
        monitor.end(ProgressMonitor.Task.TABLE_DATA, currentTable);
        return node;
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

    public static interface AroundTableImporter {
        public void before(ImportConfiguration var1, Context var2, String var3, Connection var4);

        public void after(ImportConfiguration var1, Context var2, String var3, Connection var4);
    }

    private static class BatchInserter
    extends BaseInserter {
        private final int batchSize;
        private int batch = 0;

        private BatchInserter(ImportExportErrorService errorService, String table, List<String> columns, PreparedStatement ps, List<Integer> maxColumnSize) {
            super(errorService, table, columns, ps, maxColumnSize);
            this.batchSize = 5000;
        }

        @Override
        protected void executePS() throws SQLException {
            this.ps.addBatch();
            if (++this.batch % this.batchSize == 0) {
                this.flush();
                this.batch = 0;
            }
        }

        private void flush() {
            if (this.batch == 0) {
                return;
            }
            try {
                for (int result : this.ps.executeBatch()) {
                    if (result != -3) continue;
                    throw new SQLException("SQL batch insert failed.");
                }
                this.ps.getConnection().commit();
            }
            catch (SQLException e) {
                throw this.errorService.newImportExportSqlException(this.tableName, "", e);
            }
        }

        @Override
        public void close() {
            this.flush();
            JdbcUtils.closeQuietly(new Statement[]{this.ps});
        }
    }

    private static class ImmediateInserter
    extends BaseInserter {
        private ImmediateInserter(ImportExportErrorService errorService, String table, List<String> columns, PreparedStatement ps, List<Integer> maxColumnSize) {
            super(errorService, table, columns, ps, maxColumnSize);
        }

        @Override
        protected void executePS() throws SQLException {
            this.ps.execute();
        }

        @Override
        public void close() {
            JdbcUtils.closeQuietly(new Statement[]{this.ps});
        }
    }

    private static abstract class BaseInserter
    implements Inserter {
        protected final ImportExportErrorService errorService;
        protected final String tableName;
        private int col;
        private final List<String> columnNames;
        protected final PreparedStatement ps;
        private final List<Integer> maxColumnSize;

        public BaseInserter(ImportExportErrorService errorService, String tableName, List<String> columnNames, PreparedStatement ps, List<Integer> maxColumnSize) {
            this.errorService = Objects.requireNonNull(errorService);
            this.tableName = tableName;
            this.columnNames = columnNames;
            this.ps = ps;
            this.maxColumnSize = maxColumnSize;
            this.col = 1;
        }

        private void setBoolean(Boolean value, DatabaseInformations.Database.Type databaseType) throws SQLException {
            if (value == null) {
                if (databaseType == DatabaseInformations.Database.Type.ORACLE) {
                    this.ps.setNull(this.col, 2);
                } else if (databaseType == DatabaseInformations.Database.Type.MSSQL) {
                    this.ps.setNull(this.col, -7);
                } else {
                    this.ps.setNull(this.col, 16);
                }
            } else if (databaseType == DatabaseInformations.Database.Type.ORACLE) {
                this.ps.setObject(this.col, (Object)value, 2, 1);
            } else {
                this.ps.setBoolean(this.col, value);
            }
        }

        private void setString(String value) throws SQLException {
            if (value == null) {
                this.ps.setNull(this.col, 12);
            } else {
                int maxSize = this.maxColumnSize.get(this.col);
                if (maxSize != -1 && value.length() > maxSize) {
                    throw this.errorService.newImportExportException(this.tableName, "Could not import data in table '" + this.tableName + "' column #" + this.col + ", value is too big for column which size limit is " + maxSize + ", value is:\n" + value + "\n");
                }
                this.ps.setString(this.col, value);
            }
        }

        private void setDate(Date value) throws SQLException {
            if (value == null) {
                this.ps.setNull(this.col, 93);
            } else {
                this.ps.setTimestamp(this.col, new Timestamp(value.getTime()));
            }
        }

        private void setBigInteger(BigInteger value) throws SQLException {
            if (value == null) {
                this.ps.setNull(this.col, -5);
            } else {
                this.ps.setBigDecimal(this.col, new BigDecimal(value));
            }
        }

        private void setBigDecimal(BigDecimal value) throws SQLException {
            if (value == null) {
                this.ps.setNull(this.col, 8);
            } else {
                this.ps.setBigDecimal(this.col, value);
            }
        }

        @Override
        public void setValue(NodeParser node, DatabaseInformation databaseInformation) throws SQLException {
            DatabaseInformations.Database.Type databaseType = DatabaseInformations.database(databaseInformation).getType();
            if (NodeBackup.RowDataNode.isString(node)) {
                this.setString(node.getContentAsString());
            } else if (NodeBackup.RowDataNode.isBoolean(node)) {
                this.setBoolean(node.getContentAsBoolean(), databaseType);
            } else if (NodeBackup.RowDataNode.isInteger(node)) {
                BigInteger bigInt = node.getContentAsBigInteger();
                if (bigInt != null && this.maxColumnSize.get(this.col) == 1) {
                    this.setBoolean(bigInt.intValue() == 1, databaseType);
                } else {
                    this.setBigInteger(bigInt);
                }
            } else if (NodeBackup.RowDataNode.isDouble(node)) {
                this.setBigDecimal(node.getContentAsBigDecimal());
            } else if (NodeBackup.RowDataNode.isDate(node)) {
                this.setDate(node.getContentAsDate());
            } else {
                throw new IllegalArgumentException("Unsupported field encountered: " + node.getName());
            }
            ++this.col;
        }

        @Override
        public final void execute() throws SQLException {
            this.executePS();
            this.col = 1;
        }

        protected abstract void executePS() throws SQLException;
    }

    private static class ColumnNameAndSize {
        private static ColumnNameAndSize NULL = new ColumnNameAndSize();
        public final String name;
        public final int size;

        private ColumnNameAndSize() {
            this.name = null;
            this.size = -1;
        }

        public ColumnNameAndSize(String name, int size) {
            this.name = Objects.requireNonNull(name);
            this.size = size <= 0 ? -1 : size;
        }
    }

    private static class InserterBuilder {
        public static final int UNLIMITED_COLUMN_SIZE = -1;
        private final ImportExportErrorService errorService;
        private final String schema;
        private final String table;
        private final BatchMode batch;
        private final List<String> columns;

        public InserterBuilder(ImportExportErrorService errorService, String schema, String table, BatchMode batch) {
            this.errorService = Objects.requireNonNull(errorService);
            this.schema = schema;
            this.table = table;
            this.batch = batch;
            this.columns = new ArrayList<String>();
        }

        public String getTable() {
            return this.table;
        }

        public void addColumn(String column) {
            this.columns.add(column);
        }

        public Inserter build(Connection connection) {
            int i;
            StringBuilder query = new StringBuilder("INSERT INTO ").append(this.tableName(connection)).append(" (");
            for (i = 0; i < this.columns.size(); ++i) {
                query.append(JdbcUtils.quote(this.errorService, this.table, connection, this.columns.get(i)));
                if (i >= this.columns.size() - 1) continue;
                query.append(", ");
            }
            query.append(") VALUES (");
            for (i = 0; i < this.columns.size(); ++i) {
                query.append("?");
                if (i >= this.columns.size() - 1) continue;
                query.append(", ");
            }
            query.append(")");
            List<Integer> maxColumnSizes = this.calculateColumnSizes(connection, this.columns);
            PreparedStatement ps = JdbcUtils.preparedStatement(this.errorService, this.table, connection, query.toString());
            return this.newInserter(maxColumnSizes, ps);
        }

        private String tableName(Connection connection) {
            String quoted = JdbcUtils.quote(this.errorService, this.table, connection, this.table);
            return this.schema != null ? this.schema + "." + quoted : quoted;
        }

        private Inserter newInserter(List<Integer> maxColumnSizes, PreparedStatement ps) {
            return this.batch.equals((Object)BatchMode.ON) ? new BatchInserter(this.errorService, this.getTable(), this.columns, ps, maxColumnSizes) : new ImmediateInserter(this.errorService, this.getTable(), this.columns, ps, maxColumnSizes);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private List<Integer> calculateColumnSizes(Connection connection, List<String> columns) {
            HashMap<String, Integer> columnSizeMap = new HashMap<String, Integer>();
            ResultSet rs = null;
            try {
                rs = this.getColumnsResultSet(connection);
                ColumnNameAndSize columnNameAndSize = this.getColumnNameAndSize(rs);
                while (columnNameAndSize != ColumnNameAndSize.NULL) {
                    columnSizeMap.put(columnNameAndSize.name, columnNameAndSize.size);
                    columnNameAndSize = this.getColumnNameAndSize(rs);
                }
                ArrayList sizes = Lists.newArrayList((Object[])new Integer[]{0});
                for (String column : columns) {
                    Integer size = (Integer)columnSizeMap.get(column);
                    sizes.add(size != null ? size : -1);
                }
                ArrayList arrayList = sizes;
                return arrayList;
            }
            finally {
                JdbcUtils.closeQuietly(rs);
            }
        }

        private ColumnNameAndSize getColumnNameAndSize(ResultSet rs) {
            try {
                if (rs.next()) {
                    String name = rs.getString("COLUMN_NAME");
                    int size = rs.getInt("COLUMN_SIZE");
                    int type = rs.getInt("DATA_TYPE");
                    return new ColumnNameAndSize(name, type == 2005 ? -1 : size);
                }
                return ColumnNameAndSize.NULL;
            }
            catch (SQLException e) {
                throw this.errorService.newImportExportSqlException(this.table, "", e);
            }
        }

        private ResultSet getColumnsResultSet(Connection connection) {
            try {
                return JdbcUtils.metadata(this.errorService, connection).getColumns(null, null, this.table, null);
            }
            catch (SQLException e) {
                throw this.errorService.newImportExportSqlException(this.table, "", e);
            }
        }
    }

    private static interface Inserter {
        public void setValue(NodeParser var1, DatabaseInformation var2) throws SQLException;

        public void execute() throws SQLException;

        public void close();
    }
}

