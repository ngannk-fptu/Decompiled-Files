/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.Set;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.AbstractSchemaMigrator;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaFilter;

public class GroupedSchemaMigratorImpl
extends AbstractSchemaMigrator {
    public GroupedSchemaMigratorImpl(HibernateSchemaManagementTool tool, SchemaFilter schemaFilter) {
        super(tool, schemaFilter);
    }

    @Override
    protected NameSpaceTablesInformation performTablesMigration(Metadata metadata, DatabaseInformation existingDatabase, ExecutionOptions options, Dialect dialect, Formatter formatter, Set<String> exportIdentifiers, boolean tryToCreateCatalogs, boolean tryToCreateSchemas, Set<Identifier> exportedCatalogs, Namespace namespace, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget[] targets) {
        NameSpaceTablesInformation tablesInformation = new NameSpaceTablesInformation(metadata.getDatabase().getJdbcEnvironment().getIdentifierHelper());
        if (this.schemaFilter.includeNamespace(namespace)) {
            TableInformation tableInformation;
            this.createSchemaAndCatalog(existingDatabase, options, dialect, formatter, tryToCreateCatalogs, tryToCreateSchemas, exportedCatalogs, namespace, targets);
            NameSpaceTablesInformation tables = existingDatabase.getTablesInformation(namespace);
            for (Table table : namespace.getTables()) {
                if (!this.schemaFilter.includeTable(table) || !table.isPhysicalTable()) continue;
                this.checkExportIdentifier(table, exportIdentifiers);
                tableInformation = tables.getTableInformation(table);
                if (tableInformation == null) {
                    this.createTable(table, dialect, metadata, formatter, options, sqlStringGenerationContext, targets);
                    continue;
                }
                if (!tableInformation.isPhysicalTable()) continue;
                tablesInformation.addTableInformation(tableInformation);
                this.migrateTable(table, tableInformation, dialect, metadata, formatter, options, sqlStringGenerationContext, targets);
            }
            for (Table table : namespace.getTables()) {
                if (!this.schemaFilter.includeTable(table) || !table.isPhysicalTable() || (tableInformation = tablesInformation.getTableInformation(table)) != null && !tableInformation.isPhysicalTable()) continue;
                this.applyIndexes(table, tableInformation, dialect, metadata, formatter, options, sqlStringGenerationContext, targets);
                this.applyUniqueKeys(table, tableInformation, dialect, metadata, formatter, options, sqlStringGenerationContext, targets);
            }
        }
        return tablesInformation;
    }
}

