/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal;

import java.util.Iterator;
import java.util.Locale;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.jboss.logging.Logger;

public abstract class AbstractSchemaValidator
implements SchemaValidator {
    private static final Logger log = Logger.getLogger(AbstractSchemaValidator.class);
    protected HibernateSchemaManagementTool tool;
    protected SchemaFilter schemaFilter;

    public AbstractSchemaValidator(HibernateSchemaManagementTool tool, SchemaFilter validateFilter) {
        this.tool = tool;
        this.schemaFilter = validateFilter == null ? DefaultSchemaFilter.INSTANCE : validateFilter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doValidation(Metadata metadata, ExecutionOptions options) {
        SqlStringGenerationContext sqlStringGenerationContext = SqlStringGenerationContextImpl.fromConfigurationMap(this.tool.getServiceRegistry().getService(JdbcEnvironment.class), metadata.getDatabase(), options.getConfigurationValues());
        JdbcContext jdbcContext = this.tool.resolveJdbcContext(options.getConfigurationValues());
        DdlTransactionIsolator isolator = this.tool.getDdlTransactionIsolator(jdbcContext);
        DatabaseInformation databaseInformation = Helper.buildDatabaseInformation(this.tool.getServiceRegistry(), isolator, sqlStringGenerationContext, this.tool);
        try {
            this.performValidation(metadata, databaseInformation, options, jdbcContext.getDialect());
        }
        finally {
            try {
                databaseInformation.cleanup();
            }
            catch (Exception e) {
                log.debug((Object)("Problem releasing DatabaseInformation : " + e.getMessage()));
            }
            isolator.release();
        }
    }

    public void performValidation(Metadata metadata, DatabaseInformation databaseInformation, ExecutionOptions options, Dialect dialect) {
        for (Namespace namespace : metadata.getDatabase().getNamespaces()) {
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            this.validateTables(metadata, databaseInformation, options, dialect, namespace);
        }
        for (Namespace namespace : metadata.getDatabase().getNamespaces()) {
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            for (Sequence sequence : namespace.getSequences()) {
                if (!this.schemaFilter.includeSequence(sequence)) continue;
                SequenceInformation sequenceInformation = databaseInformation.getSequenceInformation(sequence.getName());
                this.validateSequence(sequence, sequenceInformation);
            }
        }
    }

    protected abstract void validateTables(Metadata var1, DatabaseInformation var2, ExecutionOptions var3, Dialect var4, Namespace var5);

    protected void validateTable(Table table, TableInformation tableInformation, Metadata metadata, ExecutionOptions options, Dialect dialect) {
        if (tableInformation == null) {
            throw new SchemaManagementException(String.format("Schema-validation: missing table [%s]", table.getQualifiedTableName().toString()));
        }
        Iterator<Column> selectableItr = table.getColumnIterator();
        while (selectableItr.hasNext()) {
            Selectable selectable = selectableItr.next();
            if (!Column.class.isInstance(selectable)) continue;
            Column column = (Column)selectable;
            ColumnInformation existingColumn = tableInformation.getColumn(Identifier.toIdentifier(column.getQuotedName()));
            if (existingColumn == null) {
                throw new SchemaManagementException(String.format("Schema-validation: missing column [%s] in table [%s]", column.getName(), table.getQualifiedTableName()));
            }
            this.validateColumnType(table, column, existingColumn, metadata, options, dialect);
        }
    }

    protected void validateColumnType(Table table, Column column, ColumnInformation columnInformation, Metadata metadata, ExecutionOptions options, Dialect dialect) {
        boolean typesMatch;
        boolean bl = typesMatch = dialect.equivalentTypes(column.getSqlTypeCode(metadata), columnInformation.getTypeCode()) || column.getSqlType(dialect, metadata).toLowerCase(Locale.ROOT).startsWith(columnInformation.getTypeName().toLowerCase(Locale.ROOT));
        if (!typesMatch) {
            throw new SchemaManagementException(String.format("Schema-validation: wrong column type encountered in column [%s] in table [%s]; found [%s (Types#%s)], but expecting [%s (Types#%s)]", column.getName(), table.getQualifiedTableName(), columnInformation.getTypeName().toLowerCase(Locale.ROOT), JdbcTypeNameMapper.getTypeName(columnInformation.getTypeCode()), column.getSqlType().toLowerCase(Locale.ROOT), JdbcTypeNameMapper.getTypeName(column.getSqlTypeCode(metadata))));
        }
    }

    protected void validateSequence(Sequence sequence, SequenceInformation sequenceInformation) {
        if (sequenceInformation == null) {
            throw new SchemaManagementException(String.format("Schema-validation: missing sequence [%s]", sequence.getName()));
        }
        if (sequenceInformation.getIncrementSize() > 0 && sequence.getIncrementSize() != sequenceInformation.getIncrementSize()) {
            throw new SchemaManagementException(String.format("Schema-validation: sequence [%s] defined inconsistent increment-size; found [%s] but expecting [%s]", sequence.getName(), sequenceInformation.getIncrementSize(), sequence.getIncrementSize()));
        }
    }
}

