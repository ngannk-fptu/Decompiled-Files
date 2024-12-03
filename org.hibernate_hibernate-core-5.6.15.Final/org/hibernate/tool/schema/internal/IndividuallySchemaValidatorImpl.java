/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.AbstractSchemaValidator;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaFilter;

public class IndividuallySchemaValidatorImpl
extends AbstractSchemaValidator {
    public IndividuallySchemaValidatorImpl(HibernateSchemaManagementTool tool, SchemaFilter validateFilter) {
        super(tool, validateFilter);
    }

    @Override
    protected void validateTables(Metadata metadata, DatabaseInformation databaseInformation, ExecutionOptions options, Dialect dialect, Namespace namespace) {
        for (Table table : namespace.getTables()) {
            if (!this.schemaFilter.includeTable(table) || !table.isPhysicalTable()) continue;
            TableInformation tableInformation = databaseInformation.getTableInformation(table.getQualifiedTableName());
            this.validateTable(table, tableInformation, metadata, options, dialect);
        }
    }
}

