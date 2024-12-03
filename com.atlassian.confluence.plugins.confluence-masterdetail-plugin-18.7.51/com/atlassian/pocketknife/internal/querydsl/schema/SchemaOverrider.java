/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.pocketknife.internal.querydsl.schema.SchemaProvider;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SchemaAndTable;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SchemaOverrider {
    private final SchemaProvider schemaProvider;

    public SchemaOverrider(SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    public Configuration registerOverrides(Connection connection, Configuration configuration, Set<RelationalPath<?>> relationalPaths) {
        for (RelationalPath<?> relationalPath : relationalPaths) {
            this.registerTableOverride(connection, configuration, relationalPath);
        }
        return configuration;
    }

    private void registerTableOverride(Connection connection, Configuration configuration, RelationalPath<?> relationalPath) {
        boolean isAOTable;
        String actualTableName;
        SchemaAndTable currentSchemaAndTable = relationalPath.getSchemaAndTable();
        String logicalTableName = relationalPath.getTableName();
        String actualSchemaName = this.schemaProvider.getProductSchema().orElse(null);
        SchemaAndTable replacementSchemaAndTable = new SchemaAndTable(actualSchemaName, actualTableName = (isAOTable = this.isAOTable(logicalTableName)) ? logicalTableName.toUpperCase() : this.schemaProvider.getTableName(connection, logicalTableName).orElse(logicalTableName));
        if (!Objects.equals(replacementSchemaAndTable, currentSchemaAndTable)) {
            configuration.registerTableOverride(currentSchemaAndTable, replacementSchemaAndTable);
        }
        List<Path<?>> columns = relationalPath.getColumns();
        for (Path<?> column : columns) {
            String actualColumnName;
            String logicalColumnName = ColumnMetadata.getName(column);
            if (Objects.equals(logicalColumnName, actualColumnName = isAOTable ? logicalColumnName.toUpperCase() : this.schemaProvider.getColumnName(connection, logicalTableName, logicalColumnName).orElse(logicalColumnName))) continue;
            configuration.registerColumnOverride(logicalTableName, logicalColumnName, actualColumnName);
        }
    }

    private boolean isAOTable(String logical) {
        return logical.toUpperCase().startsWith("AO_");
    }
}

