/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.IndividuallySchemaMigratorImpl;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.Exporter;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.jboss.logging.Logger;

public abstract class AbstractSchemaMigrator
implements SchemaMigrator {
    private static final Logger log = Logger.getLogger(IndividuallySchemaMigratorImpl.class);
    protected HibernateSchemaManagementTool tool;
    protected SchemaFilter schemaFilter;
    private UniqueConstraintSchemaUpdateStrategy uniqueConstraintStrategy;

    public AbstractSchemaMigrator(HibernateSchemaManagementTool tool, SchemaFilter schemaFilter) {
        this.tool = tool;
        this.schemaFilter = schemaFilter == null ? DefaultSchemaFilter.INSTANCE : schemaFilter;
    }

    public void setUniqueConstraintStrategy(UniqueConstraintSchemaUpdateStrategy uniqueConstraintStrategy) {
        this.uniqueConstraintStrategy = uniqueConstraintStrategy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doMigration(Metadata metadata, ExecutionOptions options, TargetDescriptor targetDescriptor) {
        SqlStringGenerationContext sqlStringGenerationContext = SqlStringGenerationContextImpl.fromConfigurationMap(this.tool.getServiceRegistry().getService(JdbcEnvironment.class), metadata.getDatabase(), options.getConfigurationValues());
        if (!targetDescriptor.getTargetTypes().isEmpty()) {
            JdbcContext jdbcContext = this.tool.resolveJdbcContext(options.getConfigurationValues());
            DdlTransactionIsolator ddlTransactionIsolator = this.tool.getDdlTransactionIsolator(jdbcContext);
            try {
                DatabaseInformation databaseInformation = Helper.buildDatabaseInformation(this.tool.getServiceRegistry(), ddlTransactionIsolator, sqlStringGenerationContext, this.tool);
                GenerationTarget[] targets = this.tool.buildGenerationTargets(targetDescriptor, ddlTransactionIsolator, options.getConfigurationValues());
                try {
                    for (GenerationTarget target : targets) {
                        target.prepare();
                    }
                    try {
                        this.performMigration(metadata, databaseInformation, options, jdbcContext.getDialect(), sqlStringGenerationContext, targets);
                    }
                    finally {
                        for (GenerationTarget target : targets) {
                            try {
                                target.release();
                            }
                            catch (Exception e) {
                                log.debugf("Problem releasing GenerationTarget [%s] : %s", (Object)target, (Object)e.getMessage());
                            }
                        }
                    }
                }
                finally {
                    try {
                        databaseInformation.cleanup();
                    }
                    catch (Exception e) {
                        log.debug((Object)("Problem releasing DatabaseInformation : " + e.getMessage()));
                    }
                }
            }
            finally {
                ddlTransactionIsolator.release();
            }
        }
    }

    protected abstract NameSpaceTablesInformation performTablesMigration(Metadata var1, DatabaseInformation var2, ExecutionOptions var3, Dialect var4, Formatter var5, Set<String> var6, boolean var7, boolean var8, Set<Identifier> var9, Namespace var10, SqlStringGenerationContext var11, GenerationTarget[] var12);

    private void performMigration(Metadata metadata, DatabaseInformation existingDatabase, ExecutionOptions options, Dialect dialect, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        NameSpaceTablesInformation nameSpaceTablesInformation;
        boolean format = Helper.interpretFormattingEnabled(options.getConfigurationValues());
        Formatter formatter = format ? FormatStyle.DDL.getFormatter() : FormatStyle.NONE.getFormatter();
        HashSet<String> exportIdentifiers = new HashSet<String>(50);
        Database database = metadata.getDatabase();
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (!auxiliaryDatabaseObject.appliesToDialect(dialect)) continue;
            AbstractSchemaMigrator.applySqlStrings(true, dialect.getAuxiliaryDatabaseObjectExporter().getSqlDropStrings(auxiliaryDatabaseObject, metadata, sqlStringGenerationContext), formatter, options, targets);
        }
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (auxiliaryDatabaseObject.beforeTablesOnCreation() || !auxiliaryDatabaseObject.appliesToDialect(dialect)) continue;
            AbstractSchemaMigrator.applySqlStrings(true, auxiliaryDatabaseObject.sqlCreateStrings(sqlStringGenerationContext), formatter, options, targets);
        }
        boolean tryToCreateCatalogs = false;
        boolean tryToCreateSchemas = false;
        if (options.shouldManageNamespaces()) {
            if (dialect.canCreateSchema()) {
                tryToCreateSchemas = true;
            }
            if (dialect.canCreateCatalog()) {
                tryToCreateCatalogs = true;
            }
        }
        HashMap<Namespace, NameSpaceTablesInformation> tablesInformation = new HashMap<Namespace, NameSpaceTablesInformation>();
        HashSet<Identifier> exportedCatalogs = new HashSet<Identifier>();
        for (Namespace namespace : database.getNamespaces()) {
            nameSpaceTablesInformation = this.performTablesMigration(metadata, existingDatabase, options, dialect, formatter, exportIdentifiers, tryToCreateCatalogs, tryToCreateSchemas, exportedCatalogs, namespace, sqlStringGenerationContext, targets);
            tablesInformation.put(namespace, nameSpaceTablesInformation);
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            for (Sequence sequence : namespace.getSequences()) {
                this.checkExportIdentifier(sequence, exportIdentifiers);
                SequenceInformation sequenceInformation = existingDatabase.getSequenceInformation(sequence.getName());
                if (sequenceInformation != null) continue;
                AbstractSchemaMigrator.applySqlStrings(false, dialect.getSequenceExporter().getSqlCreateStrings(sequence, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
        }
        for (Namespace namespace : database.getNamespaces()) {
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            nameSpaceTablesInformation = (NameSpaceTablesInformation)tablesInformation.get(namespace);
            for (Table table : namespace.getTables()) {
                TableInformation tableInformation;
                if (!this.schemaFilter.includeTable(table) || (tableInformation = nameSpaceTablesInformation.getTableInformation(table)) != null && !tableInformation.isPhysicalTable()) continue;
                this.applyForeignKeys(table, tableInformation, dialect, metadata, formatter, options, sqlStringGenerationContext, targets);
            }
        }
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (!auxiliaryDatabaseObject.beforeTablesOnCreation() || !auxiliaryDatabaseObject.appliesToDialect(dialect)) continue;
            AbstractSchemaMigrator.applySqlStrings(true, auxiliaryDatabaseObject.sqlCreateStrings(sqlStringGenerationContext), formatter, options, targets);
        }
    }

    protected void createTable(Table table, Dialect dialect, Metadata metadata, Formatter formatter, ExecutionOptions options, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        AbstractSchemaMigrator.applySqlStrings(false, dialect.getTableExporter().getSqlCreateStrings(table, metadata, sqlStringGenerationContext), formatter, options, targets);
    }

    protected void migrateTable(Table table, TableInformation tableInformation, Dialect dialect, Metadata metadata, Formatter formatter, ExecutionOptions options, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        Database database = metadata.getDatabase();
        AbstractSchemaMigrator.applySqlStrings(false, table.sqlAlterStrings(dialect, metadata, tableInformation, sqlStringGenerationContext), formatter, options, targets);
    }

    protected void applyIndexes(Table table, TableInformation tableInformation, Dialect dialect, Metadata metadata, Formatter formatter, ExecutionOptions options, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        Exporter<Index> exporter = dialect.getIndexExporter();
        Iterator<Index> indexItr = table.getIndexIterator();
        while (indexItr.hasNext()) {
            Index index = indexItr.next();
            if (StringHelper.isEmpty(index.getName())) continue;
            IndexInformation existingIndex = null;
            if (tableInformation != null) {
                existingIndex = this.findMatchingIndex(index, tableInformation);
            }
            if (existingIndex != null) continue;
            AbstractSchemaMigrator.applySqlStrings(false, exporter.getSqlCreateStrings(index, metadata, sqlStringGenerationContext), formatter, options, targets);
        }
    }

    private IndexInformation findMatchingIndex(Index index, TableInformation tableInformation) {
        return tableInformation.getIndex(Identifier.toIdentifier(index.getName()));
    }

    protected void applyUniqueKeys(Table table, TableInformation tableInfo, Dialect dialect, Metadata metadata, Formatter formatter, ExecutionOptions options, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        if (this.uniqueConstraintStrategy == null) {
            this.uniqueConstraintStrategy = this.determineUniqueConstraintSchemaUpdateStrategy(metadata);
        }
        if (this.uniqueConstraintStrategy != UniqueConstraintSchemaUpdateStrategy.SKIP) {
            Exporter<Constraint> exporter = dialect.getUniqueKeyExporter();
            Iterator<UniqueKey> ukItr = table.getUniqueKeyIterator();
            while (ukItr.hasNext()) {
                UniqueKey uniqueKey = ukItr.next();
                IndexInformation indexInfo = null;
                if (tableInfo != null && StringHelper.isNotEmpty(uniqueKey.getName())) {
                    indexInfo = tableInfo.getIndex(Identifier.toIdentifier(uniqueKey.getName()));
                }
                if (indexInfo != null) continue;
                if (this.uniqueConstraintStrategy == UniqueConstraintSchemaUpdateStrategy.DROP_RECREATE_QUIETLY) {
                    AbstractSchemaMigrator.applySqlStrings(true, exporter.getSqlDropStrings(uniqueKey, metadata, sqlStringGenerationContext), formatter, options, targets);
                }
                AbstractSchemaMigrator.applySqlStrings(true, exporter.getSqlCreateStrings(uniqueKey, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
        }
    }

    private UniqueConstraintSchemaUpdateStrategy determineUniqueConstraintSchemaUpdateStrategy(Metadata metadata) {
        ConfigurationService cfgService = ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry().getService(ConfigurationService.class);
        return UniqueConstraintSchemaUpdateStrategy.interpret(cfgService.getSetting("hibernate.schema_update.unique_constraint_strategy", StandardConverters.STRING));
    }

    protected void applyForeignKeys(Table table, TableInformation tableInformation, Dialect dialect, Metadata metadata, Formatter formatter, ExecutionOptions options, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        if (dialect.hasAlterTable()) {
            Exporter<ForeignKey> exporter = dialect.getForeignKeyExporter();
            Iterator<ForeignKey> fkItr = table.getForeignKeyIterator();
            while (fkItr.hasNext()) {
                ForeignKey foreignKey = fkItr.next();
                if (!foreignKey.isPhysicalConstraint() || !foreignKey.isCreationEnabled()) continue;
                boolean existingForeignKeyFound = false;
                if (tableInformation != null) {
                    existingForeignKeyFound = this.checkForExistingForeignKey(foreignKey, tableInformation);
                }
                if (existingForeignKeyFound) continue;
                AbstractSchemaMigrator.applySqlStrings(false, exporter.getSqlCreateStrings(foreignKey, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
        }
    }

    private boolean checkForExistingForeignKey(ForeignKey foreignKey, TableInformation tableInformation) {
        String referencedTable;
        if (foreignKey.getName() == null || tableInformation == null) {
            return false;
        }
        String referencingColumn = foreignKey.getColumn(0).getName();
        if (this.equivalentForeignKeyExistsInDatabase(tableInformation, referencingColumn, referencedTable = foreignKey.getReferencedTable().getName())) {
            return true;
        }
        return tableInformation.getForeignKey(Identifier.toIdentifier(foreignKey.getName())) != null;
    }

    boolean equivalentForeignKeyExistsInDatabase(TableInformation tableInformation, String referencingColumn, String referencedTable) {
        Predicate<ForeignKeyInformation.ColumnReferenceMapping> mappingPredicate = m -> {
            String existingReferencingColumn = m.getReferencingColumnMetadata().getColumnIdentifier().getText();
            String existingReferencedTable = m.getReferencedColumnMetadata().getContainingTableInformation().getName().getTableName().getCanonicalName();
            return referencingColumn.equalsIgnoreCase(existingReferencingColumn) && referencedTable.equalsIgnoreCase(existingReferencedTable);
        };
        Stream<ForeignKeyInformation> keyStream = StreamSupport.stream(tableInformation.getForeignKeys().spliterator(), false);
        Stream mappingStream = keyStream.flatMap(k -> StreamSupport.stream(k.getColumnReferenceMappings().spliterator(), false));
        return mappingStream.anyMatch(mappingPredicate);
    }

    protected void checkExportIdentifier(Exportable exportable, Set<String> exportIdentifiers) {
        String exportIdentifier = exportable.getExportIdentifier();
        if (exportIdentifiers.contains(exportIdentifier)) {
            throw new SchemaManagementException(String.format("Export identifier [%s] encountered more than once", exportIdentifier));
        }
        exportIdentifiers.add(exportIdentifier);
    }

    protected static void applySqlStrings(boolean quiet, String[] sqlStrings, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        if (sqlStrings != null) {
            for (String sqlString : sqlStrings) {
                AbstractSchemaMigrator.applySqlString(quiet, sqlString, formatter, options, targets);
            }
        }
    }

    protected void createSchemaAndCatalog(DatabaseInformation existingDatabase, ExecutionOptions options, Dialect dialect, Formatter formatter, boolean tryToCreateCatalogs, boolean tryToCreateSchemas, Set<Identifier> exportedCatalogs, Namespace namespace, GenerationTarget[] targets) {
        if (tryToCreateCatalogs || tryToCreateSchemas) {
            if (tryToCreateCatalogs) {
                Identifier catalogLogicalName = namespace.getName().getCatalog();
                Identifier catalogPhysicalName = namespace.getPhysicalName().getCatalog();
                if (catalogPhysicalName != null && !exportedCatalogs.contains(catalogLogicalName) && !existingDatabase.catalogExists(catalogLogicalName)) {
                    AbstractSchemaMigrator.applySqlStrings(false, dialect.getCreateCatalogCommand(catalogPhysicalName.render(dialect)), formatter, options, targets);
                    exportedCatalogs.add(catalogLogicalName);
                }
            }
            if (tryToCreateSchemas && namespace.getPhysicalName().getSchema() != null && !existingDatabase.schemaExists(namespace.getName())) {
                AbstractSchemaMigrator.applySqlStrings(false, dialect.getCreateSchemaCommand(namespace.getPhysicalName().getSchema().render(dialect)), formatter, options, targets);
            }
        }
    }

    private static void applySqlString(boolean quiet, String sqlString, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        if (!StringHelper.isEmpty(sqlString)) {
            String sqlStringFormatted = formatter.format(sqlString);
            for (GenerationTarget target : targets) {
                try {
                    target.accept(sqlStringFormatted);
                }
                catch (CommandAcceptanceException e) {
                    if (quiet) continue;
                    options.getExceptionHandler().handleException(e);
                }
            }
        }
    }

    private static void applySqlStrings(boolean quiet, Iterator<String> sqlStrings, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        if (sqlStrings != null) {
            while (sqlStrings.hasNext()) {
                String sqlString = sqlStrings.next();
                AbstractSchemaMigrator.applySqlString(quiet, sqlString, formatter, options, targets);
            }
        }
    }
}

