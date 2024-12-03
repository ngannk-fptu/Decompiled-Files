/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformations;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.importer.DataImporter;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class SqlServerAroundTableImporter
implements DataImporter.AroundTableImporter {
    private final ImportExportErrorService errorService;
    private final String schema;

    public SqlServerAroundTableImporter(ImportExportErrorService errorService, String schema) {
        this.errorService = Objects.requireNonNull(errorService);
        this.schema = schema;
    }

    @Override
    public void before(ImportConfiguration configuration, Context context, String table, Connection connection) {
        this.setIdentityInsert(configuration, context, connection, table, "ON");
    }

    @Override
    public void after(ImportConfiguration configuration, Context context, String table, Connection connection) {
        this.setIdentityInsert(configuration, context, connection, table, "OFF");
    }

    private void setIdentityInsert(ImportConfiguration configuration, Context context, Connection connection, String table, String onOff) {
        if (this.isSqlServer(configuration) && this.isAutoIncrementTable(context, table)) {
            this.setIdentityInsert(connection, table, onOff);
        }
    }

    private boolean isAutoIncrementTable(Context context, String tableName) {
        return this.hasAnyAutoIncrementColumn(this.findTable(context, tableName));
    }

    private boolean hasAnyAutoIncrementColumn(Table table) {
        return table.getColumns().stream().anyMatch(Column::isAutoIncrement);
    }

    private Table findTable(Context context, String tableName) {
        Objects.requireNonNull(context.getAll(Table.class));
        return context.getAll(Table.class).stream().filter(table -> table.getName().equals(tableName)).findAny().orElseThrow(NoSuchElementException::new);
    }

    private void setIdentityInsert(Connection connection, String table, String onOff) {
        Statement s = null;
        try {
            s = connection.createStatement();
            s.execute(this.setIdentityInsertSql(JdbcUtils.quote(this.errorService, table, connection, table), onOff));
        }
        catch (SQLException e) {
            try {
                throw this.errorService.newImportExportSqlException(table, "", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeQuietly(new Statement[]{s});
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(new Statement[]{s});
    }

    private String setIdentityInsertSql(String table, String onOff) {
        return String.format("SET IDENTITY_INSERT %s %s", this.schema != null ? this.schema + "." + table : table, onOff);
    }

    private boolean isSqlServer(ImportConfiguration configuration) {
        return DatabaseInformations.Database.Type.MSSQL.equals((Object)DatabaseInformations.database(configuration.getDatabaseInformation()).getType());
    }
}

