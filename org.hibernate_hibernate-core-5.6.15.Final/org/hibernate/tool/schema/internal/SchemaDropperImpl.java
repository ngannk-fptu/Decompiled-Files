/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.SourceType;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.internal.ExceptionHandlerLoggedImpl;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.DelayedDropAction;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaDropper;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.hibernate.tool.schema.spi.SourceDescriptor;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.jboss.logging.Logger;

public class SchemaDropperImpl
implements SchemaDropper {
    private static final Logger log = Logger.getLogger(SchemaDropperImpl.class);
    private final HibernateSchemaManagementTool tool;
    private final SchemaFilter schemaFilter;

    public SchemaDropperImpl(HibernateSchemaManagementTool tool) {
        this(tool, (SchemaFilter)DefaultSchemaFilter.INSTANCE);
    }

    public SchemaDropperImpl(HibernateSchemaManagementTool tool, SchemaFilter schemaFilter) {
        this.tool = tool;
        this.schemaFilter = schemaFilter;
    }

    public SchemaDropperImpl(ServiceRegistry serviceRegistry) {
        this(serviceRegistry, (SchemaFilter)DefaultSchemaFilter.INSTANCE);
    }

    public SchemaDropperImpl(ServiceRegistry serviceRegistry, SchemaFilter schemaFilter) {
        SchemaManagementTool smt = serviceRegistry.getService(SchemaManagementTool.class);
        if (smt == null || !HibernateSchemaManagementTool.class.isInstance(smt)) {
            smt = new HibernateSchemaManagementTool();
            ((HibernateSchemaManagementTool)smt).injectServices((ServiceRegistryImplementor)serviceRegistry);
        }
        this.tool = (HibernateSchemaManagementTool)smt;
        this.schemaFilter = schemaFilter;
    }

    @Override
    public void doDrop(Metadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor, TargetDescriptor targetDescriptor) {
        if (targetDescriptor.getTargetTypes().isEmpty()) {
            return;
        }
        JdbcContext jdbcContext = this.tool.resolveJdbcContext(options.getConfigurationValues());
        GenerationTarget[] targets = this.tool.buildGenerationTargets(targetDescriptor, jdbcContext, options.getConfigurationValues(), true);
        this.doDrop(metadata, options, jdbcContext.getDialect(), sourceDescriptor, targets);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doDrop(Metadata metadata, ExecutionOptions options, Dialect dialect, SourceDescriptor sourceDescriptor, GenerationTarget ... targets) {
        for (GenerationTarget target : targets) {
            target.prepare();
        }
        try {
            this.performDrop(metadata, options, dialect, sourceDescriptor, targets);
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

    private void performDrop(Metadata metadata, ExecutionOptions options, Dialect dialect, SourceDescriptor sourceDescriptor, GenerationTarget ... targets) {
        Formatter formatter;
        ImportSqlCommandExtractor commandExtractor = this.tool.getServiceRegistry().getService(ImportSqlCommandExtractor.class);
        boolean format = Helper.interpretFormattingEnabled(options.getConfigurationValues());
        Formatter formatter2 = formatter = format ? FormatStyle.DDL.getFormatter() : FormatStyle.NONE.getFormatter();
        if (sourceDescriptor.getSourceType() == SourceType.SCRIPT) {
            this.dropFromScript(sourceDescriptor.getScriptSourceInput(), commandExtractor, formatter, options, targets);
        } else if (sourceDescriptor.getSourceType() == SourceType.METADATA) {
            this.dropFromMetadata(metadata, options, dialect, formatter, targets);
        } else if (sourceDescriptor.getSourceType() == SourceType.METADATA_THEN_SCRIPT) {
            this.dropFromMetadata(metadata, options, dialect, formatter, targets);
            this.dropFromScript(sourceDescriptor.getScriptSourceInput(), commandExtractor, formatter, options, targets);
        } else {
            this.dropFromScript(sourceDescriptor.getScriptSourceInput(), commandExtractor, formatter, options, targets);
            this.dropFromMetadata(metadata, options, dialect, formatter, targets);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dropFromScript(ScriptSourceInput scriptSourceInput, ImportSqlCommandExtractor commandExtractor, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        scriptSourceInput.prepare();
        try {
            for (String command : scriptSourceInput.read(commandExtractor)) {
                SchemaDropperImpl.applySqlString(command, formatter, options, targets);
            }
        }
        finally {
            scriptSourceInput.release();
        }
    }

    private void dropFromMetadata(Metadata metadata, ExecutionOptions options, Dialect dialect, Formatter formatter, GenerationTarget ... targets) {
        Database database = metadata.getDatabase();
        SqlStringGenerationContext sqlStringGenerationContext = SqlStringGenerationContextImpl.fromConfigurationMap(metadata.getDatabase().getJdbcEnvironment(), database, options.getConfigurationValues());
        boolean tryToDropCatalogs = false;
        boolean tryToDropSchemas = false;
        if (options.shouldManageNamespaces()) {
            if (dialect.canCreateSchema()) {
                tryToDropSchemas = true;
            }
            if (dialect.canCreateCatalog()) {
                tryToDropCatalogs = true;
            }
        }
        HashSet<String> exportIdentifiers = new HashSet<String>(50);
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (!auxiliaryDatabaseObject.beforeTablesOnCreation() || !auxiliaryDatabaseObject.appliesToDialect(dialect)) continue;
            SchemaDropperImpl.applySqlStrings(dialect.getAuxiliaryDatabaseObjectExporter().getSqlDropStrings(auxiliaryDatabaseObject, metadata, sqlStringGenerationContext), formatter, options, targets);
        }
        for (Namespace namespace : database.getNamespaces()) {
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            this.applyConstraintDropping(namespace, metadata, formatter, options, sqlStringGenerationContext, targets);
            for (Table table : namespace.getTables()) {
                if (!table.isPhysicalTable() || !this.schemaFilter.includeTable(table)) continue;
                SchemaDropperImpl.checkExportIdentifier(table, exportIdentifiers);
                SchemaDropperImpl.applySqlStrings(dialect.getTableExporter().getSqlDropStrings(table, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
            for (Sequence sequence : namespace.getSequences()) {
                if (!this.schemaFilter.includeSequence(sequence)) continue;
                SchemaDropperImpl.checkExportIdentifier(sequence, exportIdentifiers);
                SchemaDropperImpl.applySqlStrings(dialect.getSequenceExporter().getSqlDropStrings(sequence, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
        }
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (auxiliaryDatabaseObject.beforeTablesOnCreation() || !auxiliaryDatabaseObject.appliesToDialect(dialect)) continue;
            SchemaDropperImpl.applySqlStrings(auxiliaryDatabaseObject.sqlDropStrings(sqlStringGenerationContext), formatter, options, targets);
        }
        if (tryToDropCatalogs || tryToDropSchemas) {
            HashSet<Identifier> exportedCatalogs = new HashSet<Identifier>();
            for (Namespace namespace : database.getNamespaces()) {
                if (!this.schemaFilter.includeNamespace(namespace)) continue;
                if (tryToDropSchemas && namespace.getPhysicalName().getSchema() != null) {
                    SchemaDropperImpl.applySqlStrings(dialect.getDropSchemaCommand(namespace.getPhysicalName().getSchema().render(dialect)), formatter, options, targets);
                }
                if (!tryToDropCatalogs) continue;
                Identifier catalogLogicalName = namespace.getName().getCatalog();
                Identifier catalogPhysicalName = namespace.getPhysicalName().getCatalog();
                if (catalogPhysicalName == null || exportedCatalogs.contains(catalogLogicalName)) continue;
                SchemaDropperImpl.applySqlStrings(dialect.getDropCatalogCommand(catalogPhysicalName.render(dialect)), formatter, options, targets);
                exportedCatalogs.add(catalogLogicalName);
            }
        }
    }

    private void applyConstraintDropping(Namespace namespace, Metadata metadata, Formatter formatter, ExecutionOptions options, SqlStringGenerationContext sqlStringGenerationContext, GenerationTarget ... targets) {
        Dialect dialect = metadata.getDatabase().getJdbcEnvironment().getDialect();
        if (!dialect.dropConstraints()) {
            return;
        }
        for (Table table : namespace.getTables()) {
            if (!table.isPhysicalTable() || !this.schemaFilter.includeTable(table)) continue;
            Iterator<ForeignKey> fks = table.getForeignKeyIterator();
            while (fks.hasNext()) {
                ForeignKey foreignKey = fks.next();
                SchemaDropperImpl.applySqlStrings(dialect.getForeignKeyExporter().getSqlDropStrings(foreignKey, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
        }
    }

    private static void checkExportIdentifier(Exportable exportable, Set<String> exportIdentifiers) {
        String exportIdentifier = exportable.getExportIdentifier();
        if (exportIdentifiers.contains(exportIdentifier)) {
            throw new SchemaManagementException("SQL strings added more than once for: " + exportIdentifier);
        }
        exportIdentifiers.add(exportIdentifier);
    }

    private static void applySqlStrings(String[] sqlStrings, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        if (sqlStrings == null) {
            return;
        }
        for (String sqlString : sqlStrings) {
            SchemaDropperImpl.applySqlString(sqlString, formatter, options, targets);
        }
    }

    private static void applySqlString(String sqlString, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        if (StringHelper.isEmpty(sqlString)) {
            return;
        }
        String sqlStringFormatted = formatter.format(sqlString);
        for (GenerationTarget target : targets) {
            try {
                target.accept(sqlStringFormatted);
            }
            catch (CommandAcceptanceException e) {
                options.getExceptionHandler().handleException(e);
            }
        }
    }

    public List<String> generateDropCommands(Metadata metadata, final boolean manageNamespaces) {
        JournalingGenerationTarget target = new JournalingGenerationTarget();
        StandardServiceRegistry serviceRegistry = ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry();
        Dialect dialect = serviceRegistry.getService(JdbcEnvironment.class).getDialect();
        ExecutionOptions options = new ExecutionOptions(){

            @Override
            public boolean shouldManageNamespaces() {
                return manageNamespaces;
            }

            @Override
            public Map getConfigurationValues() {
                return Collections.emptyMap();
            }

            @Override
            public ExceptionHandler getExceptionHandler() {
                return ExceptionHandlerHaltImpl.INSTANCE;
            }
        };
        this.dropFromMetadata(metadata, options, dialect, FormatStyle.NONE.getFormatter(), target);
        return target.commands;
    }

    @Override
    public DelayedDropAction buildDelayedAction(Metadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor) {
        JournalingGenerationTarget target = new JournalingGenerationTarget();
        this.doDrop(metadata, options, this.tool.getServiceRegistry().getService(JdbcEnvironment.class).getDialect(), sourceDescriptor, target);
        return new DelayedDropActionImpl(target.commands, this.tool.getCustomDatabaseGenerationTarget());
    }

    public void doDrop(Metadata metadata, boolean manageNamespaces, GenerationTarget ... targets) {
        StandardServiceRegistry serviceRegistry = ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry();
        this.doDrop(metadata, serviceRegistry, serviceRegistry.getService(ConfigurationService.class).getSettings(), manageNamespaces, targets);
    }

    public void doDrop(Metadata metadata, ServiceRegistry serviceRegistry, final Map settings, final boolean manageNamespaces, GenerationTarget ... targets) {
        if (targets == null || targets.length == 0) {
            JdbcContext jdbcContext = this.tool.resolveJdbcContext(settings);
            targets = new GenerationTarget[]{new GenerationTargetToDatabase(serviceRegistry.getService(TransactionCoordinatorBuilder.class).buildDdlTransactionIsolator(jdbcContext), true)};
        }
        this.doDrop(metadata, new ExecutionOptions(){

            @Override
            public boolean shouldManageNamespaces() {
                return manageNamespaces;
            }

            @Override
            public Map getConfigurationValues() {
                return settings;
            }

            @Override
            public ExceptionHandler getExceptionHandler() {
                return ExceptionHandlerLoggedImpl.INSTANCE;
            }
        }, serviceRegistry.getService(JdbcEnvironment.class).getDialect(), new SourceDescriptor(){

            @Override
            public SourceType getSourceType() {
                return SourceType.METADATA;
            }

            @Override
            public ScriptSourceInput getScriptSourceInput() {
                return null;
            }
        }, targets);
    }

    private static class DelayedDropActionImpl
    implements DelayedDropAction,
    Serializable {
        private static final CoreMessageLogger log = CoreLogging.messageLogger(DelayedDropActionImpl.class);
        private final ArrayList<String> commands;
        private GenerationTarget target;

        public DelayedDropActionImpl(ArrayList<String> commands, GenerationTarget target) {
            this.commands = commands;
            this.target = target;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void perform(ServiceRegistry serviceRegistry) {
            log.startingDelayedSchemaDrop();
            JdbcContextDelayedDropImpl jdbcContext = new JdbcContextDelayedDropImpl(serviceRegistry);
            if (this.target == null) {
                this.target = new GenerationTargetToDatabase(serviceRegistry.getService(TransactionCoordinatorBuilder.class).buildDdlTransactionIsolator(jdbcContext), true);
            }
            this.target.prepare();
            try {
                for (String command : this.commands) {
                    try {
                        this.target.accept(command);
                    }
                    catch (CommandAcceptanceException e) {
                        log.unsuccessfulSchemaManagementCommand(command);
                        log.debugf((Throwable)((Object)e), "Error performing delayed DROP command [%s]", command);
                    }
                }
            }
            finally {
                this.target.release();
            }
        }

        private static class JdbcContextDelayedDropImpl
        implements JdbcContext {
            private final ServiceRegistry serviceRegistry;
            private final JdbcServices jdbcServices;
            private final JdbcConnectionAccess jdbcConnectionAccess;

            public JdbcContextDelayedDropImpl(ServiceRegistry serviceRegistry) {
                this.serviceRegistry = serviceRegistry;
                this.jdbcServices = serviceRegistry.getService(JdbcServices.class);
                this.jdbcConnectionAccess = this.jdbcServices.getBootstrapJdbcConnectionAccess();
                if (this.jdbcConnectionAccess == null) {
                    throw new SchemaManagementException("Could not build JDBC Connection context to drop schema on SessionFactory close");
                }
            }

            @Override
            public JdbcConnectionAccess getJdbcConnectionAccess() {
                return this.jdbcConnectionAccess;
            }

            @Override
            public Dialect getDialect() {
                return this.jdbcServices.getJdbcEnvironment().getDialect();
            }

            @Override
            public SqlStatementLogger getSqlStatementLogger() {
                return this.jdbcServices.getSqlStatementLogger();
            }

            @Override
            public SqlExceptionHelper getSqlExceptionHelper() {
                return this.jdbcServices.getSqlExceptionHelper();
            }

            @Override
            public ServiceRegistry getServiceRegistry() {
                return this.serviceRegistry;
            }
        }
    }

    private static class JournalingGenerationTarget
    implements GenerationTarget {
        private final ArrayList<String> commands = new ArrayList();

        private JournalingGenerationTarget() {
        }

        @Override
        public void prepare() {
        }

        @Override
        public void accept(String command) {
            this.commands.add(command);
        }

        @Override
        public void release() {
        }
    }
}

