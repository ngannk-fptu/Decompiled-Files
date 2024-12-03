/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.net.URL;
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
import org.hibernate.boot.model.relational.InitCommand;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
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
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputFromUrl;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputNonExistentImpl;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.hibernate.tool.schema.spi.SourceDescriptor;
import org.hibernate.tool.schema.spi.TargetDescriptor;

public class SchemaCreatorImpl
implements SchemaCreator {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SchemaCreatorImpl.class);
    public static final String DEFAULT_IMPORT_FILE = "/import.sql";
    private final HibernateSchemaManagementTool tool;
    private final SchemaFilter schemaFilter;

    public SchemaCreatorImpl(HibernateSchemaManagementTool tool) {
        this(tool, (SchemaFilter)DefaultSchemaFilter.INSTANCE);
    }

    public SchemaCreatorImpl(HibernateSchemaManagementTool tool, SchemaFilter schemaFilter) {
        this.tool = tool;
        this.schemaFilter = schemaFilter;
    }

    public SchemaCreatorImpl(ServiceRegistry serviceRegistry) {
        this(serviceRegistry, (SchemaFilter)DefaultSchemaFilter.INSTANCE);
    }

    public SchemaCreatorImpl(ServiceRegistry serviceRegistry, SchemaFilter schemaFilter) {
        SchemaManagementTool smt = serviceRegistry.getService(SchemaManagementTool.class);
        if (smt == null || !HibernateSchemaManagementTool.class.isInstance(smt)) {
            smt = new HibernateSchemaManagementTool();
            ((HibernateSchemaManagementTool)smt).injectServices((ServiceRegistryImplementor)serviceRegistry);
        }
        this.tool = (HibernateSchemaManagementTool)smt;
        this.schemaFilter = schemaFilter;
    }

    @Override
    public void doCreation(Metadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor, TargetDescriptor targetDescriptor) {
        if (targetDescriptor.getTargetTypes().isEmpty()) {
            return;
        }
        JdbcContext jdbcContext = this.tool.resolveJdbcContext(options.getConfigurationValues());
        GenerationTarget[] targets = this.tool.buildGenerationTargets(targetDescriptor, jdbcContext, options.getConfigurationValues(), true);
        this.doCreation(metadata, jdbcContext.getDialect(), options, sourceDescriptor, targets);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doCreation(Metadata metadata, Dialect dialect, ExecutionOptions options, SourceDescriptor sourceDescriptor, GenerationTarget ... targets) {
        for (GenerationTarget target : targets) {
            target.prepare();
        }
        try {
            this.performCreation(metadata, dialect, options, sourceDescriptor, targets);
        }
        finally {
            for (GenerationTarget target : targets) {
                try {
                    target.release();
                }
                catch (Exception e) {
                    log.debugf("Problem releasing GenerationTarget [%s] : %s", target, e.getMessage());
                }
            }
        }
    }

    private void performCreation(Metadata metadata, Dialect dialect, ExecutionOptions options, SourceDescriptor sourceDescriptor, GenerationTarget ... targets) {
        ImportSqlCommandExtractor commandExtractor = this.tool.getServiceRegistry().getService(ImportSqlCommandExtractor.class);
        boolean format = Helper.interpretFormattingEnabled(options.getConfigurationValues());
        Formatter formatter = format ? FormatStyle.DDL.getFormatter() : FormatStyle.NONE.getFormatter();
        switch (sourceDescriptor.getSourceType()) {
            case SCRIPT: {
                this.createFromScript(sourceDescriptor.getScriptSourceInput(), commandExtractor, formatter, options, targets);
                break;
            }
            case METADATA: {
                this.createFromMetadata(metadata, options, dialect, formatter, targets);
                break;
            }
            case METADATA_THEN_SCRIPT: {
                this.createFromMetadata(metadata, options, dialect, formatter, targets);
                this.createFromScript(sourceDescriptor.getScriptSourceInput(), commandExtractor, formatter, options, targets);
                break;
            }
            case SCRIPT_THEN_METADATA: {
                this.createFromScript(sourceDescriptor.getScriptSourceInput(), commandExtractor, formatter, options, targets);
                this.createFromMetadata(metadata, options, dialect, formatter, targets);
            }
        }
        this.applyImportSources(options, commandExtractor, format, targets);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void createFromScript(ScriptSourceInput scriptSourceInput, ImportSqlCommandExtractor commandExtractor, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        scriptSourceInput.prepare();
        try {
            for (String command : scriptSourceInput.read(commandExtractor)) {
                SchemaCreatorImpl.applySqlString(command, formatter, options, targets);
            }
        }
        finally {
            scriptSourceInput.release();
        }
    }

    public void createFromMetadata(Metadata metadata, ExecutionOptions options, Dialect dialect, Formatter formatter, GenerationTarget ... targets) {
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
        Database database = metadata.getDatabase();
        JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
        SqlStringGenerationContext sqlStringGenerationContext = SqlStringGenerationContextImpl.fromConfigurationMap(jdbcEnvironment, database, options.getConfigurationValues());
        HashSet<String> exportIdentifiers = new HashSet<String>(50);
        if (tryToCreateCatalogs || tryToCreateSchemas) {
            HashSet<Identifier> exportedCatalogs = new HashSet<Identifier>();
            for (Namespace namespace : database.getNamespaces()) {
                if (!this.schemaFilter.includeNamespace(namespace)) continue;
                if (tryToCreateCatalogs) {
                    Identifier catalogLogicalName = namespace.getName().getCatalog();
                    Identifier catalogPhysicalName = sqlStringGenerationContext.catalogWithDefault(namespace.getPhysicalName().getCatalog());
                    if (catalogPhysicalName != null && !exportedCatalogs.contains(catalogLogicalName)) {
                        SchemaCreatorImpl.applySqlStrings(dialect.getCreateCatalogCommand(catalogPhysicalName.render(dialect)), formatter, options, targets);
                        exportedCatalogs.add(catalogLogicalName);
                    }
                }
                Identifier schemaPhysicalName = sqlStringGenerationContext.schemaWithDefault(namespace.getPhysicalName().getSchema());
                if (!tryToCreateSchemas || schemaPhysicalName == null) continue;
                SchemaCreatorImpl.applySqlStrings(dialect.getCreateSchemaCommand(schemaPhysicalName.render(dialect)), formatter, options, targets);
            }
        }
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (!auxiliaryDatabaseObject.beforeTablesOnCreation() || !auxiliaryDatabaseObject.appliesToDialect(dialect)) continue;
            SchemaCreatorImpl.checkExportIdentifier(auxiliaryDatabaseObject, exportIdentifiers);
            SchemaCreatorImpl.applySqlStrings(dialect.getAuxiliaryDatabaseObjectExporter().getSqlCreateStrings(auxiliaryDatabaseObject, metadata, sqlStringGenerationContext), formatter, options, targets);
        }
        for (Namespace namespace : database.getNamespaces()) {
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            for (Sequence sequence : namespace.getSequences()) {
                if (!this.schemaFilter.includeSequence(sequence)) continue;
                SchemaCreatorImpl.checkExportIdentifier(sequence, exportIdentifiers);
                SchemaCreatorImpl.applySqlStrings(dialect.getSequenceExporter().getSqlCreateStrings(sequence, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
            for (Table table : namespace.getTables()) {
                if (!table.isPhysicalTable() || !this.schemaFilter.includeTable(table)) continue;
                SchemaCreatorImpl.checkExportIdentifier(table, exportIdentifiers);
                SchemaCreatorImpl.applySqlStrings(dialect.getTableExporter().getSqlCreateStrings(table, metadata, sqlStringGenerationContext), formatter, options, targets);
            }
            for (Table table : namespace.getTables()) {
                if (!table.isPhysicalTable() || !this.schemaFilter.includeTable(table)) continue;
                Iterator<Index> indexItr = table.getIndexIterator();
                while (indexItr.hasNext()) {
                    Index index = indexItr.next();
                    SchemaCreatorImpl.checkExportIdentifier(index, exportIdentifiers);
                    SchemaCreatorImpl.applySqlStrings(dialect.getIndexExporter().getSqlCreateStrings(index, metadata, sqlStringGenerationContext), formatter, options, targets);
                }
                Iterator<UniqueKey> ukItr = table.getUniqueKeyIterator();
                while (ukItr.hasNext()) {
                    UniqueKey uniqueKey = ukItr.next();
                    SchemaCreatorImpl.checkExportIdentifier(uniqueKey, exportIdentifiers);
                    SchemaCreatorImpl.applySqlStrings(dialect.getUniqueKeyExporter().getSqlCreateStrings(uniqueKey, metadata, sqlStringGenerationContext), formatter, options, targets);
                }
            }
        }
        for (Namespace namespace : database.getNamespaces()) {
            if (!this.schemaFilter.includeNamespace(namespace)) continue;
            for (Table table : namespace.getTables()) {
                if (!this.schemaFilter.includeTable(table)) continue;
                Iterator<ForeignKey> fkItr = table.getForeignKeyIterator();
                while (fkItr.hasNext()) {
                    ForeignKey foreignKey = fkItr.next();
                    SchemaCreatorImpl.applySqlStrings(dialect.getForeignKeyExporter().getSqlCreateStrings(foreignKey, metadata, sqlStringGenerationContext), formatter, options, targets);
                }
            }
        }
        for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : database.getAuxiliaryDatabaseObjects()) {
            if (!auxiliaryDatabaseObject.appliesToDialect(dialect) || auxiliaryDatabaseObject.beforeTablesOnCreation()) continue;
            SchemaCreatorImpl.checkExportIdentifier(auxiliaryDatabaseObject, exportIdentifiers);
            SchemaCreatorImpl.applySqlStrings(dialect.getAuxiliaryDatabaseObjectExporter().getSqlCreateStrings(auxiliaryDatabaseObject, metadata, sqlStringGenerationContext), formatter, options, targets);
        }
        for (InitCommand initCommand : database.getInitCommands()) {
            SchemaCreatorImpl.applySqlStrings(initCommand.getInitCommands(), formatter, options, targets);
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
            SchemaCreatorImpl.applySqlString(sqlString, formatter, options, targets);
        }
    }

    private static void applySqlString(String sqlString, Formatter formatter, ExecutionOptions options, GenerationTarget ... targets) {
        if (StringHelper.isEmpty(sqlString)) {
            return;
        }
        try {
            String sqlStringFormatted = formatter.format(sqlString);
            for (GenerationTarget target : targets) {
                target.accept(sqlStringFormatted);
            }
        }
        catch (CommandAcceptanceException e) {
            options.getExceptionHandler().handleException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void applyImportSources(ExecutionOptions options, ImportSqlCommandExtractor commandExtractor, boolean format, GenerationTarget ... targets) {
        ServiceRegistry serviceRegistry = this.tool.getServiceRegistry();
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        Formatter formatter = FormatStyle.NONE.getFormatter();
        Object importScriptSetting = options.getConfigurationValues().get("javax.persistence.sql-load-script-source");
        if (importScriptSetting == null) {
            importScriptSetting = options.getConfigurationValues().get("jakarta.persistence.sql-load-script-source");
        }
        String charsetName = (String)options.getConfigurationValues().get("hibernate.hbm2ddl.charset_name");
        if (importScriptSetting != null) {
            ScriptSourceInput importScriptInput = Helper.interpretScriptSourceSetting(importScriptSetting, classLoaderService, charsetName);
            importScriptInput.prepare();
            try {
                for (String command : importScriptInput.read(commandExtractor)) {
                    SchemaCreatorImpl.applySqlString(command, formatter, options, targets);
                }
            }
            finally {
                importScriptInput.release();
            }
        }
        String importFiles = ConfigurationHelper.getString("hibernate.hbm2ddl.import_files", options.getConfigurationValues(), DEFAULT_IMPORT_FILE);
        for (String currentFile : importFiles.split(",")) {
            String resourceName = currentFile.trim();
            if (resourceName != null && resourceName.isEmpty()) continue;
            ScriptSourceInput importScriptInput = this.interpretLegacyImportScriptSetting(resourceName, classLoaderService, charsetName);
            importScriptInput.prepare();
            try {
                for (String command : importScriptInput.read(commandExtractor)) {
                    SchemaCreatorImpl.applySqlString(command, formatter, options, targets);
                }
            }
            finally {
                importScriptInput.release();
            }
        }
    }

    private ScriptSourceInput interpretLegacyImportScriptSetting(String resourceName, ClassLoaderService classLoaderService, String charsetName) {
        try {
            URL resourceUrl = classLoaderService.locateResource(resourceName);
            if (resourceUrl == null) {
                return ScriptSourceInputNonExistentImpl.INSTANCE;
            }
            return new ScriptSourceInputFromUrl(resourceUrl, charsetName);
        }
        catch (Exception e) {
            throw new SchemaManagementException("Error resolving legacy import resource : " + resourceName, e);
        }
    }

    public List<String> generateCreationCommands(Metadata metadata, final boolean manageNamespaces) {
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
        this.createFromMetadata(metadata, options, dialect, FormatStyle.NONE.getFormatter(), target);
        return target.commands;
    }

    public void doCreation(Metadata metadata, boolean manageNamespaces, GenerationTarget ... targets) {
        StandardServiceRegistry serviceRegistry = ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry();
        this.doCreation(metadata, serviceRegistry, serviceRegistry.getService(ConfigurationService.class).getSettings(), manageNamespaces, targets);
    }

    public void doCreation(Metadata metadata, ServiceRegistry serviceRegistry, final Map settings, final boolean manageNamespaces, GenerationTarget ... targets) {
        this.doCreation(metadata, serviceRegistry.getService(JdbcEnvironment.class).getDialect(), new ExecutionOptions(){

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
        }, new SourceDescriptor(){

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

