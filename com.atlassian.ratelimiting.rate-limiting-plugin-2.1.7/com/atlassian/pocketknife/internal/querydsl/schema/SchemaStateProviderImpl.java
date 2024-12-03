/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  io.atlassian.fugue.Option
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.pocketknife.api.querydsl.schema.SchemaState;
import com.atlassian.pocketknife.api.querydsl.schema.SchemaStateProvider;
import com.atlassian.pocketknife.internal.querydsl.schema.JdbcTableAndColumns;
import com.atlassian.pocketknife.internal.querydsl.schema.JdbcTableInspector;
import com.atlassian.pocketknife.internal.querydsl.schema.ProductSchemaProvider;
import com.atlassian.pocketknife.internal.querydsl.schema.SchemaStateImpl;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import io.atlassian.fugue.Option;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
@ParametersAreNonnullByDefault
public class SchemaStateProviderImpl
implements SchemaStateProvider {
    private final JdbcTableInspector tableInspector;
    private final ProductSchemaProvider productSchemaProvider;

    @Autowired
    public SchemaStateProviderImpl(ProductSchemaProvider productSchemaProvider, JdbcTableInspector tableInspector) {
        this.tableInspector = tableInspector;
        this.productSchemaProvider = productSchemaProvider;
    }

    @Override
    public SchemaState getSchemaState(Connection connection, RelationalPath<?> relationalPath) {
        SchemaState.Presence tablePresence;
        String logicalTableName = relationalPath.getTableName();
        JdbcTableAndColumns tableAndColumns = this.tableInspector.inspectTableAndColumns(connection, this.productSchemaProvider.getProductSchema(), logicalTableName);
        HashMap<Path, SchemaState.Presence> columnState = new HashMap<Path, SchemaState.Presence>();
        List<Path<?>> relationalColumns = relationalPath.getColumns();
        LinkedHashSet<String> addedColumns = new LinkedHashSet<String>();
        Option<String> tableName = tableAndColumns.getTableName();
        if (tableName.isDefined()) {
            tablePresence = SchemaState.Presence.SAME;
            LinkedHashSet<String> physicalTableColumns = tableAndColumns.getColumnNames();
            for (Path<?> col2 : relationalColumns) {
                SchemaState.Presence columnPresence;
                String logicalColumnName = col2.getMetadata().getName();
                Option<String> columnName = this.findPhysicalColumn(logicalColumnName, physicalTableColumns);
                if (columnName.isDefined()) {
                    columnPresence = SchemaState.Presence.SAME;
                } else {
                    tablePresence = SchemaState.Presence.DIFFERENT;
                    columnPresence = SchemaState.Presence.MISSING;
                }
                columnState.put(col2, columnPresence);
            }
            if (relationalColumns.size() != physicalTableColumns.size()) {
                tablePresence = SchemaState.Presence.DIFFERENT;
            }
            for (String physicalColumn : physicalTableColumns) {
                boolean foundInLogical = this.hasLogicalColumn(relationalColumns, physicalColumn);
                if (foundInLogical) continue;
                addedColumns.add(physicalColumn);
            }
        } else {
            tablePresence = SchemaState.Presence.MISSING;
            relationalColumns.forEach(col -> columnState.put((Path)col, SchemaState.Presence.MISSING));
        }
        return new SchemaStateImpl(relationalPath, tablePresence, columnState, addedColumns);
    }

    private Option<String> findPhysicalColumn(String logicalColumnName, LinkedHashSet<String> tableColumns) {
        for (String physicalColumName : tableColumns) {
            if (!logicalColumnName.equalsIgnoreCase(physicalColumName)) continue;
            return Option.some((Object)physicalColumName);
        }
        return Option.none();
    }

    private boolean hasLogicalColumn(List<Path<?>> relationalColumns, String physicalColumn) {
        return relationalColumns.stream().anyMatch(logicalColumn -> logicalColumn.getMetadata().getName().equalsIgnoreCase(physicalColumn));
    }
}

