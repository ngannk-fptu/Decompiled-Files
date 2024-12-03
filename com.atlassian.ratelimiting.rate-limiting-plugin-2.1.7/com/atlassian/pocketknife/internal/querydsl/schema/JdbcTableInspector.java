/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.pocketknife.internal.querydsl.schema.JdbcTableAndColumns;
import io.atlassian.fugue.Option;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JdbcTableInspector {
    private static final String SCHEMA_NAME_KEY = "TABLE_SCHEM";
    private static final String TABLE_NAME_KEY = "TABLE_NAME";
    private static final String COLUMN_NAME_KEY = "COLUMN_NAME";

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public JdbcTableAndColumns inspectTableAndColumns(Connection connection, Optional<String> schema, String logicalTableName) {
        String lookupSchemaName;
        try {
            lookupSchemaName = JdbcTableInspector.getSchemaPattern(connection.getMetaData(), schema);
        }
        catch (SQLException sqlEx) {
            throw new RuntimeException("Unable to enquire connection metadata the system", sqlEx);
        }
        try (ResultSet resultSet = connection.getMetaData().getTables(null, lookupSchemaName, null, null);){
            String realTableName;
            String realSchemaName;
            boolean matches;
            do {
                if (!resultSet.next()) return new JdbcTableAndColumns((Option<String>)Option.none(), new LinkedHashSet<String>());
            } while (!(matches = this.matchesTableAndSchema(lookupSchemaName, realSchemaName = resultSet.getString(SCHEMA_NAME_KEY), logicalTableName, realTableName = resultSet.getString(TABLE_NAME_KEY))));
            LinkedHashSet<String> tableColumns = this.inspectColumnNames(connection, realTableName);
            JdbcTableAndColumns jdbcTableAndColumns = new JdbcTableAndColumns((Option<String>)Option.some((Object)realTableName), tableColumns);
            return jdbcTableAndColumns;
        }
        catch (SQLException sqlEx) {
            throw new RuntimeException("Unable to enquire table names available in the system", sqlEx);
        }
    }

    private LinkedHashSet<String> inspectColumnNames(Connection connection, String realTableName) throws SQLException {
        LinkedHashSet<String> columnNames = new LinkedHashSet<String>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columnResultSet = metaData.getColumns(null, null, realTableName, null);){
            while (columnResultSet.next()) {
                String tableName = columnResultSet.getString(TABLE_NAME_KEY);
                boolean matches = this.matchesTableName(tableName, realTableName);
                if (!matches) continue;
                String columnName = columnResultSet.getString(COLUMN_NAME_KEY);
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }

    private boolean matchesTableAndSchema(String lookupSchemaName, String realSchemaName, String logicalTableName, String realTableName) {
        return this.matchesTableName(logicalTableName, realTableName) && this.matchesSchema(lookupSchemaName, realSchemaName);
    }

    private boolean matchesTableName(String logicalTableName, String realTableName) {
        return logicalTableName.equalsIgnoreCase(realTableName);
    }

    private boolean matchesSchema(String lookupSchemaName, String realSchemaName) {
        if (lookupSchemaName != null) {
            return lookupSchemaName.equalsIgnoreCase(realSchemaName);
        }
        return realSchemaName == null;
    }

    private static String getSchemaPattern(DatabaseMetaData dbData, Optional<String> schemaName) throws SQLException {
        if (dbData.supportsSchemasInTableDefinitions()) {
            if (schemaName.isPresent() && schemaName.get().length() > 0) {
                return schemaName.get();
            }
            if ("Oracle".equalsIgnoreCase(dbData.getDatabaseProductName())) {
                return dbData.getUserName();
            }
        }
        return null;
    }
}

