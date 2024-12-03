/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.internal.AbstractSchemaValidator;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaFilter;

public class GroupedSchemaValidatorImpl
extends AbstractSchemaValidator {
    public GroupedSchemaValidatorImpl(HibernateSchemaManagementTool tool, SchemaFilter validateFilter) {
        super(tool, validateFilter);
    }

    @Override
    protected void validateTables(Metadata metadata, DatabaseInformation databaseInformation, ExecutionOptions options, Dialect dialect, Namespace namespace) {
        NameSpaceTablesInformation tables = databaseInformation.getTablesInformation(namespace);
        for (Table table : namespace.getTables()) {
            if (!this.schemaFilter.includeTable(table) || !table.isPhysicalTable()) continue;
            this.validateTable(table, tables.getTableInformation(table), metadata, options, dialect);
        }
    }
}

