/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal;

import java.sql.Connection;
import java.util.Map;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.JdbcMetadaAccessStrategy;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.extract.internal.InformationExtractorJdbcDatabaseMetaDataImpl;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;
import org.hibernate.tool.schema.internal.DdlTransactionIsolatorProvidedConnectionImpl;
import org.hibernate.tool.schema.internal.DefaultSchemaFilterProvider;
import org.hibernate.tool.schema.internal.GroupedSchemaMigratorImpl;
import org.hibernate.tool.schema.internal.GroupedSchemaValidatorImpl;
import org.hibernate.tool.schema.internal.IndividuallySchemaMigratorImpl;
import org.hibernate.tool.schema.internal.IndividuallySchemaValidatorImpl;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.internal.SchemaDropperImpl;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToScript;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToStdout;
import org.hibernate.tool.schema.internal.exec.ImprovedExtractionContextImpl;
import org.hibernate.tool.schema.internal.exec.JdbcConnectionAccessProvidedConnectionImpl;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.ExtractionTool;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaDropper;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.jboss.logging.Logger;

public class HibernateSchemaManagementTool
implements SchemaManagementTool,
ServiceRegistryAwareService {
    private static final Logger log = Logger.getLogger(HibernateSchemaManagementTool.class);
    private ServiceRegistry serviceRegistry;
    private GenerationTarget customTarget;

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public SchemaCreator getSchemaCreator(Map options) {
        return new SchemaCreatorImpl(this, this.getSchemaFilterProvider(options).getCreateFilter());
    }

    @Override
    public SchemaDropper getSchemaDropper(Map options) {
        return new SchemaDropperImpl(this, this.getSchemaFilterProvider(options).getDropFilter());
    }

    @Override
    public SchemaMigrator getSchemaMigrator(Map options) {
        if (this.determineJdbcMetadaAccessStrategy(options) == JdbcMetadaAccessStrategy.GROUPED) {
            return new GroupedSchemaMigratorImpl(this, this.getSchemaFilterProvider(options).getMigrateFilter());
        }
        return new IndividuallySchemaMigratorImpl(this, this.getSchemaFilterProvider(options).getMigrateFilter());
    }

    @Override
    public SchemaValidator getSchemaValidator(Map options) {
        if (this.determineJdbcMetadaAccessStrategy(options) == JdbcMetadaAccessStrategy.GROUPED) {
            return new GroupedSchemaValidatorImpl(this, this.getSchemaFilterProvider(options).getValidateFilter());
        }
        return new IndividuallySchemaValidatorImpl(this, this.getSchemaFilterProvider(options).getValidateFilter());
    }

    private SchemaFilterProvider getSchemaFilterProvider(Map options) {
        Object configuredOption = options == null ? null : options.get("hibernate.hbm2ddl.schema_filter_provider");
        return this.serviceRegistry.getService(StrategySelector.class).resolveDefaultableStrategy(SchemaFilterProvider.class, configuredOption, DefaultSchemaFilterProvider.INSTANCE);
    }

    private JdbcMetadaAccessStrategy determineJdbcMetadaAccessStrategy(Map options) {
        return JdbcMetadaAccessStrategy.interpretSetting(options);
    }

    @Override
    public void setCustomDatabaseGenerationTarget(GenerationTarget generationTarget) {
        this.customTarget = generationTarget;
    }

    @Override
    public ExtractionTool getExtractionTool() {
        return HibernateExtractionTool.INSTANCE;
    }

    GenerationTarget getCustomDatabaseGenerationTarget() {
        return this.customTarget;
    }

    GenerationTarget[] buildGenerationTargets(TargetDescriptor targetDescriptor, JdbcContext jdbcContext, Map options, boolean needsAutoCommit) {
        String scriptDelimiter = ConfigurationHelper.getString("hibernate.hbm2ddl.delimiter", options, ";");
        GenerationTarget[] targets = new GenerationTarget[targetDescriptor.getTargetTypes().size()];
        int index = 0;
        if (targetDescriptor.getTargetTypes().contains((Object)TargetType.STDOUT)) {
            targets[index] = new GenerationTargetToStdout(scriptDelimiter);
            ++index;
        }
        if (targetDescriptor.getTargetTypes().contains((Object)TargetType.SCRIPT)) {
            if (targetDescriptor.getScriptTargetOutput() == null) {
                throw new SchemaManagementException("Writing to script was requested, but no script file was specified");
            }
            targets[index] = new GenerationTargetToScript(targetDescriptor.getScriptTargetOutput(), scriptDelimiter);
            ++index;
        }
        if (targetDescriptor.getTargetTypes().contains((Object)TargetType.DATABASE)) {
            targets[index] = this.customTarget == null ? new GenerationTargetToDatabase(this.getDdlTransactionIsolator(jdbcContext), true) : this.customTarget;
            ++index;
        }
        return targets;
    }

    GenerationTarget[] buildGenerationTargets(TargetDescriptor targetDescriptor, DdlTransactionIsolator ddlTransactionIsolator, Map options) {
        String scriptDelimiter = ConfigurationHelper.getString("hibernate.hbm2ddl.delimiter", options, ";");
        GenerationTarget[] targets = new GenerationTarget[targetDescriptor.getTargetTypes().size()];
        int index = 0;
        if (targetDescriptor.getTargetTypes().contains((Object)TargetType.STDOUT)) {
            targets[index] = new GenerationTargetToStdout(scriptDelimiter);
            ++index;
        }
        if (targetDescriptor.getTargetTypes().contains((Object)TargetType.SCRIPT)) {
            if (targetDescriptor.getScriptTargetOutput() == null) {
                throw new SchemaManagementException("Writing to script was requested, but no script file was specified");
            }
            targets[index] = new GenerationTargetToScript(targetDescriptor.getScriptTargetOutput(), scriptDelimiter);
            ++index;
        }
        if (targetDescriptor.getTargetTypes().contains((Object)TargetType.DATABASE)) {
            targets[index] = this.customTarget == null ? new GenerationTargetToDatabase(ddlTransactionIsolator, false) : this.customTarget;
            ++index;
        }
        return targets;
    }

    public DdlTransactionIsolator getDdlTransactionIsolator(JdbcContext jdbcContext) {
        if (jdbcContext.getJdbcConnectionAccess() instanceof JdbcConnectionAccessProvidedConnectionImpl) {
            return new DdlTransactionIsolatorProvidedConnectionImpl(jdbcContext);
        }
        return this.serviceRegistry.getService(TransactionCoordinatorBuilder.class).buildDdlTransactionIsolator(jdbcContext);
    }

    public JdbcContext resolveJdbcContext(Map configurationValues) {
        String explicitDbName;
        JdbcContextBuilder jdbcContextBuilder = new JdbcContextBuilder(this.serviceRegistry);
        Connection providedConnection = (Connection)configurationValues.get("javax.persistence.schema-generation-connection");
        if (providedConnection != null) {
            jdbcContextBuilder.jdbcConnectionAccess = new JdbcConnectionAccessProvidedConnectionImpl(providedConnection);
        } else {
            Connection jakartaProvidedConnection = (Connection)configurationValues.get("jakarta.persistence.schema-generation-connection");
            if (jakartaProvidedConnection != null) {
                jdbcContextBuilder.jdbcConnectionAccess = new JdbcConnectionAccessProvidedConnectionImpl(jakartaProvidedConnection);
            }
        }
        String dbName = (String)configurationValues.get("javax.persistence.database-product-name");
        if (dbName == null) {
            dbName = (String)configurationValues.get("jakarta.persistence.database-product-name");
        }
        if (StringHelper.isNotEmpty(explicitDbName = dbName)) {
            String dbMinor;
            String dbMajor = (String)configurationValues.get("javax.persistence.database-major-version");
            if (dbMajor == null) {
                dbMajor = (String)configurationValues.get("jakarta.persistence.database-major-version");
            }
            if ((dbMinor = (String)configurationValues.get("javax.persistence.database-minor-version")) == null) {
                dbMinor = (String)configurationValues.get("jakarta.persistence.database-minor-version");
            }
            final String explicitDbMajor = dbMajor;
            final String explicitDbMinor = dbMinor;
            Dialect indicatedDialect = this.serviceRegistry.getService(DialectResolver.class).resolveDialect(new DialectResolutionInfo(){

                @Override
                public String getDatabaseName() {
                    return explicitDbName;
                }

                @Override
                public int getDatabaseMajorVersion() {
                    return StringHelper.isEmpty(explicitDbMajor) ? -9999 : Integer.parseInt(explicitDbMajor);
                }

                @Override
                public int getDatabaseMinorVersion() {
                    return StringHelper.isEmpty(explicitDbMinor) ? -9999 : Integer.parseInt(explicitDbMinor);
                }

                @Override
                public String getDriverName() {
                    return null;
                }

                @Override
                public int getDriverMajorVersion() {
                    return -9999;
                }

                @Override
                public int getDriverMinorVersion() {
                    return -9999;
                }
            });
            if (indicatedDialect == null) {
                log.debugf("Unable to resolve indicated Dialect resolution info (%s, %s, %s)", (Object)explicitDbName, (Object)explicitDbMajor, (Object)explicitDbMinor);
            } else {
                jdbcContextBuilder.dialect = indicatedDialect;
            }
        }
        return jdbcContextBuilder.buildJdbcContext();
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    private static class HibernateExtractionTool
    implements ExtractionTool {
        private static final HibernateExtractionTool INSTANCE = new HibernateExtractionTool();

        private HibernateExtractionTool() {
        }

        @Override
        public ExtractionContext createExtractionContext(ServiceRegistry serviceRegistry, JdbcEnvironment jdbcEnvironment, SqlStringGenerationContext sqlStringGenerationContext, DdlTransactionIsolator ddlTransactionIsolator, ExtractionContext.DatabaseObjectAccess databaseObjectAccess) {
            return new ImprovedExtractionContextImpl(serviceRegistry, jdbcEnvironment, sqlStringGenerationContext, ddlTransactionIsolator, databaseObjectAccess);
        }

        @Override
        public InformationExtractor createInformationExtractor(ExtractionContext extractionContext) {
            return new InformationExtractorJdbcDatabaseMetaDataImpl(extractionContext);
        }
    }

    public static class JdbcContextImpl
    implements JdbcContext {
        private final JdbcConnectionAccess jdbcConnectionAccess;
        private final Dialect dialect;
        private final SqlStatementLogger sqlStatementLogger;
        private final SqlExceptionHelper sqlExceptionHelper;
        private final ServiceRegistry serviceRegistry;

        private JdbcContextImpl(JdbcConnectionAccess jdbcConnectionAccess, Dialect dialect, SqlStatementLogger sqlStatementLogger, SqlExceptionHelper sqlExceptionHelper, ServiceRegistry serviceRegistry) {
            this.jdbcConnectionAccess = jdbcConnectionAccess;
            this.dialect = dialect;
            this.sqlStatementLogger = sqlStatementLogger;
            this.sqlExceptionHelper = sqlExceptionHelper;
            this.serviceRegistry = serviceRegistry;
        }

        @Override
        public JdbcConnectionAccess getJdbcConnectionAccess() {
            return this.jdbcConnectionAccess;
        }

        @Override
        public Dialect getDialect() {
            return this.dialect;
        }

        @Override
        public SqlStatementLogger getSqlStatementLogger() {
            return this.sqlStatementLogger;
        }

        @Override
        public SqlExceptionHelper getSqlExceptionHelper() {
            return this.sqlExceptionHelper;
        }

        @Override
        public ServiceRegistry getServiceRegistry() {
            return this.serviceRegistry;
        }
    }

    private static class JdbcContextBuilder {
        private final ServiceRegistry serviceRegistry;
        private final SqlStatementLogger sqlStatementLogger;
        private final SqlExceptionHelper sqlExceptionHelper;
        private JdbcConnectionAccess jdbcConnectionAccess;
        private Dialect dialect;

        public JdbcContextBuilder(ServiceRegistry serviceRegistry) {
            this.serviceRegistry = serviceRegistry;
            JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
            this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
            this.dialect = jdbcServices.getJdbcEnvironment().getDialect();
            this.jdbcConnectionAccess = jdbcServices.getBootstrapJdbcConnectionAccess();
        }

        public JdbcContext buildJdbcContext() {
            return new JdbcContextImpl(this.jdbcConnectionAccess, this.dialect, this.sqlStatementLogger, this.sqlExceptionHelper, this.serviceRegistry);
        }
    }
}

