/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql;

import com.google.common.collect.Maps;
import com.querydsl.sql.SchemaAndTable;
import java.util.HashMap;
import java.util.Map;

final class NameMapping {
    private final Map<SchemaAndTable, SchemaAndTable> schemaTables = Maps.newHashMap();
    private final Map<String, String> schemas = Maps.newHashMap();
    private final Map<String, String> tables = Maps.newHashMap();
    private final Map<SchemaAndTable, Map<String, String>> schemaTableColumns = Maps.newHashMap();
    private final Map<String, Map<String, String>> tableColumns = Maps.newHashMap();

    NameMapping() {
    }

    public SchemaAndTable getOverride(SchemaAndTable key) {
        if (!this.schemaTables.isEmpty() && key.getSchema() != null && this.schemaTables.containsKey(key)) {
            return this.schemaTables.get(key);
        }
        String schema = key.getSchema();
        String table = key.getTable();
        boolean changed = false;
        if (this.schemas.containsKey(key.getSchema())) {
            schema = this.schemas.get(key.getSchema());
            changed = true;
        }
        if (this.tables.containsKey(key.getTable())) {
            table = this.tables.get(key.getTable());
            changed = true;
        }
        return changed ? new SchemaAndTable(schema, table) : key;
    }

    public String getColumnOverride(SchemaAndTable key, String column) {
        String newColumn = null;
        Map<String, String> columnOverrides = this.schemaTableColumns.get(key);
        if (columnOverrides != null && (newColumn = columnOverrides.get(column)) != null) {
            return newColumn;
        }
        columnOverrides = this.tableColumns.get(key.getTable());
        if (columnOverrides != null && (newColumn = columnOverrides.get(column)) != null) {
            return newColumn;
        }
        return column;
    }

    public String registerSchemaOverride(String oldSchema, String newSchema) {
        return this.schemas.put(oldSchema, newSchema);
    }

    public String registerTableOverride(String oldTable, String newTable) {
        return this.tables.put(oldTable, newTable);
    }

    public SchemaAndTable registerTableOverride(SchemaAndTable from, SchemaAndTable to) {
        return this.schemaTables.put(from, to);
    }

    public String registerColumnOverride(String schema, String table, String oldColumn, String newColumn) {
        SchemaAndTable key = new SchemaAndTable(schema, table);
        Map<String, String> columnOverrides = this.schemaTableColumns.get(key);
        if (columnOverrides == null) {
            columnOverrides = new HashMap<String, String>();
            this.schemaTableColumns.put(key, columnOverrides);
        }
        return columnOverrides.put(oldColumn, newColumn);
    }

    public String registerColumnOverride(String table, String oldColumn, String newColumn) {
        Map<String, String> columnOverrides = this.tableColumns.get(table);
        if (columnOverrides == null) {
            columnOverrides = new HashMap<String, String>();
            this.tableColumns.put(table, columnOverrides);
        }
        return columnOverrides.put(oldColumn, newColumn);
    }
}

