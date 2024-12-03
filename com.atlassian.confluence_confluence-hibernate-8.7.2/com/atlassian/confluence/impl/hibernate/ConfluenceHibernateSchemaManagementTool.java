/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.boot.Metadata
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.boot.model.relational.Exportable
 *  org.hibernate.boot.model.relational.Namespace
 *  org.hibernate.boot.model.relational.Namespace$Name
 *  org.hibernate.boot.model.relational.SqlStringGenerationContext
 *  org.hibernate.boot.registry.selector.spi.StrategySelector
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess
 *  org.hibernate.engine.jdbc.internal.Formatter
 *  org.hibernate.engine.jdbc.spi.SqlExceptionHelper
 *  org.hibernate.engine.jdbc.spi.SqlStatementLogger
 *  org.hibernate.mapping.Table
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.tool.schema.extract.spi.DatabaseInformation
 *  org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation
 *  org.hibernate.tool.schema.internal.DefaultSchemaFilterProvider
 *  org.hibernate.tool.schema.internal.HibernateSchemaManagementTool
 *  org.hibernate.tool.schema.internal.IndividuallySchemaMigratorImpl
 *  org.hibernate.tool.schema.internal.SchemaCreatorImpl
 *  org.hibernate.tool.schema.internal.SchemaDropperImpl
 *  org.hibernate.tool.schema.internal.exec.GenerationTarget
 *  org.hibernate.tool.schema.internal.exec.JdbcContext
 *  org.hibernate.tool.schema.spi.ExecutionOptions
 *  org.hibernate.tool.schema.spi.SchemaCreator
 *  org.hibernate.tool.schema.spi.SchemaDropper
 *  org.hibernate.tool.schema.spi.SchemaFilterProvider
 *  org.hibernate.tool.schema.spi.SchemaMigrator
 *  org.hibernate.tool.schema.spi.SourceDescriptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate;

import java.util.Map;
import java.util.Set;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.internal.DefaultSchemaFilterProvider;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.IndividuallySchemaMigratorImpl;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.internal.SchemaDropperImpl;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaDropper;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.SourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceHibernateSchemaManagementTool
extends HibernateSchemaManagementTool {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceHibernateSchemaManagementTool.class);

    public SchemaCreator getSchemaCreator(Map options) {
        return new SchemaCreatorImpl(this, this.getSchemaFilterProvider(options).getCreateFilter()){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void doCreation(Metadata metadata, Dialect dialect, ExecutionOptions options, SourceDescriptor sourceDescriptor, GenerationTarget ... targets) {
                try {
                    super.doCreation(metadata, dialect, options, sourceDescriptor, ConfluenceHibernateSchemaManagementTool.noRelease(targets));
                }
                finally {
                    ConfluenceHibernateSchemaManagementTool.releaseGenerationTargets(targets);
                }
            }
        };
    }

    public SchemaDropper getSchemaDropper(Map options) {
        return new SchemaDropperImpl(this, this.getSchemaFilterProvider(options).getDropFilter()){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void doDrop(Metadata metadata, ExecutionOptions options, Dialect dialect, SourceDescriptor sourceDescriptor, GenerationTarget ... targets) {
                try {
                    super.doDrop(metadata, options, dialect, sourceDescriptor, ConfluenceHibernateSchemaManagementTool.noRelease(targets));
                }
                finally {
                    ConfluenceHibernateSchemaManagementTool.releaseGenerationTargets(targets);
                }
            }
        };
    }

    public SchemaMigrator getSchemaMigrator(Map options) {
        return new IndividuallySchemaMigratorImpl(this, this.getSchemaFilterProvider(options).getMigrateFilter()){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            protected NameSpaceTablesInformation performTablesMigration(Metadata metadata, DatabaseInformation existingDatabase, ExecutionOptions options, Dialect dialect, Formatter formatter, Set<String> exportIdentifiers, boolean tryToCreateCatalogs, boolean tryToCreateSchemas, Set<Identifier> exportedCatalogs, Namespace namespace, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget[] targets) {
                try {
                    NameSpaceTablesInformation result = super.performTablesMigration(metadata, existingDatabase, options, dialect, formatter, exportIdentifiers, tryToCreateCatalogs, tryToCreateSchemas, exportedCatalogs, namespace, sqlStringGenerationContext, ConfluenceHibernateSchemaManagementTool.noRelease(targets));
                    Namespace.Name name = metadata.getDatabase().getDefaultNamespace().getName();
                    Identifier hibernateUniqueKey = new Identifier("hibernate_unique_key", false);
                    if (existingDatabase.getTableInformation(name.getCatalog(), name.getSchema(), hibernateUniqueKey) == null) {
                        ConfluenceHibernateSchemaManagementTool.this.createGeneratorTable(dialect, metadata, targets, namespace, sqlStringGenerationContext, hibernateUniqueKey);
                    }
                    NameSpaceTablesInformation nameSpaceTablesInformation = result;
                    return nameSpaceTablesInformation;
                }
                finally {
                    ConfluenceHibernateSchemaManagementTool.releaseGenerationTargets(targets);
                }
            }
        };
    }

    public JdbcContext resolveJdbcContext(Map configurationValues) {
        final JdbcContext jdbcContext = super.resolveJdbcContext(configurationValues);
        final SqlExceptionHelper noWarningsExceptionHelper = new SqlExceptionHelper(false);
        JdbcContext noWarningsJdbcContext = new JdbcContext(){

            public JdbcConnectionAccess getJdbcConnectionAccess() {
                return jdbcContext.getJdbcConnectionAccess();
            }

            public Dialect getDialect() {
                return jdbcContext.getDialect();
            }

            public SqlStatementLogger getSqlStatementLogger() {
                return jdbcContext.getSqlStatementLogger();
            }

            public SqlExceptionHelper getSqlExceptionHelper() {
                return noWarningsExceptionHelper;
            }

            public ServiceRegistry getServiceRegistry() {
                return jdbcContext.getServiceRegistry();
            }
        };
        return noWarningsJdbcContext;
    }

    private SchemaFilterProvider getSchemaFilterProvider(Map options) {
        Object configuredOption = options == null ? null : options.get("hibernate.hbm2ddl.schema_filter_provider");
        return (SchemaFilterProvider)((StrategySelector)this.getServiceRegistry().getService(StrategySelector.class)).resolveDefaultableStrategy(SchemaFilterProvider.class, configuredOption, (Object)DefaultSchemaFilterProvider.INSTANCE);
    }

    private void createGeneratorTable(Dialect dialect, Metadata metadata, GenerationTarget[] targets, Namespace namespace, SqlStringGenerationContext sqlStringGenerationContext, Identifier hibernateUniqueKey) {
        String[] createSQLs;
        for (String sql : createSQLs = this.getGeneratorTableCreateStrings(dialect, namespace.locateTable(hibernateUniqueKey), metadata, sqlStringGenerationContext)) {
            for (GenerationTarget target : targets) {
                target.accept(sql);
            }
        }
    }

    private static GenerationTarget[] noRelease(GenerationTarget[] targets) {
        GenerationTarget[] array = new GenerationTarget[targets.length];
        for (int i = 0; i < targets.length; ++i) {
            array[i] = ConfluenceHibernateSchemaManagementTool.noRelease(targets[i]);
        }
        return array;
    }

    private static GenerationTarget noRelease(final GenerationTarget target) {
        return new GenerationTarget(){

            public void prepare() {
                target.prepare();
            }

            public void accept(String command) {
                target.accept(command);
            }

            public void release() {
            }
        };
    }

    private static void releaseGenerationTargets(GenerationTarget[] targets) {
        for (GenerationTarget target : targets) {
            try {
                target.release();
            }
            catch (Exception ex) {
                log.debug("Problem releasing GenerationTarget [{}] : {}", (Object)target, (Object)ex.getMessage());
            }
        }
    }

    private String[] getGeneratorTableCreateStrings(Dialect dialect, Table table, Metadata metadata, SqlStringGenerationContext sqlStringGenerationContext) {
        return dialect.getTableExporter().getSqlCreateStrings((Exportable)table, metadata, sqlStringGenerationContext);
    }
}

