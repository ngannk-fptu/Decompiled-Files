/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.env.internal;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.internal.ExtractedDatabaseMetaDataImpl;
import org.hibernate.engine.jdbc.env.internal.LobCreatorBuilderImpl;
import org.hibernate.engine.jdbc.env.internal.QualifiedObjectNameFormatterStandardImpl;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.env.spi.LobCreatorBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter;
import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.internal.SQLExceptionTypeDelegate;
import org.hibernate.exception.internal.SQLStateConversionDelegate;
import org.hibernate.exception.internal.StandardSQLExceptionConverter;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class JdbcEnvironmentImpl
implements JdbcEnvironment {
    private static final Logger log = Logger.getLogger(JdbcEnvironmentImpl.class);
    private final Dialect dialect;
    private final SqlExceptionHelper sqlExceptionHelper;
    private final ExtractedDatabaseMetaData extractedMetaDataSupport;
    private final Identifier currentCatalog;
    private final Identifier currentSchema;
    private final IdentifierHelper identifierHelper;
    private final QualifiedObjectNameFormatter qualifiedObjectNameFormatter;
    private final LobCreatorBuilderImpl lobCreatorBuilder;
    private final NameQualifierSupport nameQualifierSupport;
    public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";

    public JdbcEnvironmentImpl(ServiceRegistryImplementor serviceRegistry, Dialect dialect) {
        this.dialect = dialect;
        ConfigurationService cfgService = serviceRegistry.getService(ConfigurationService.class);
        NameQualifierSupport nameQualifierSupport = dialect.getNameQualifierSupport();
        if (nameQualifierSupport == null) {
            nameQualifierSupport = NameQualifierSupport.BOTH;
        }
        this.nameQualifierSupport = nameQualifierSupport;
        this.sqlExceptionHelper = this.buildSqlExceptionHelper(dialect, JdbcEnvironmentImpl.logWarnings(cfgService, dialect));
        IdentifierHelperBuilder identifierHelperBuilder = IdentifierHelperBuilder.from(this);
        identifierHelperBuilder.setGloballyQuoteIdentifiers(JdbcEnvironmentImpl.globalQuoting(cfgService));
        identifierHelperBuilder.setSkipGlobalQuotingForColumnDefinitions(this.globalQuotingSkippedForColumnDefinitions(cfgService));
        identifierHelperBuilder.setAutoQuoteKeywords(JdbcEnvironmentImpl.autoKeywordQuoting(cfgService));
        identifierHelperBuilder.setNameQualifierSupport(nameQualifierSupport);
        IdentifierHelper identifierHelper = null;
        ExtractedDatabaseMetaDataImpl.Builder dbMetaDataBuilder = new ExtractedDatabaseMetaDataImpl.Builder(this, false, null);
        try {
            identifierHelper = dialect.buildIdentifierHelper(identifierHelperBuilder, null);
            dbMetaDataBuilder.setSupportsNamedParameters(dialect.supportsNamedParameters(null));
        }
        catch (SQLException sqle) {
            log.debug((Object)"There was a problem accessing DatabaseMetaData in building the JdbcEnvironment", (Throwable)sqle);
        }
        if (identifierHelper == null) {
            identifierHelper = identifierHelperBuilder.build();
        }
        this.identifierHelper = identifierHelper;
        this.extractedMetaDataSupport = dbMetaDataBuilder.build();
        this.currentCatalog = identifierHelper.toIdentifier(cfgService.getSetting("hibernate.default_catalog", StandardConverters.STRING));
        this.currentSchema = Identifier.toIdentifier(cfgService.getSetting("hibernate.default_schema", StandardConverters.STRING));
        this.qualifiedObjectNameFormatter = new QualifiedObjectNameFormatterStandardImpl(nameQualifierSupport);
        this.lobCreatorBuilder = LobCreatorBuilderImpl.makeLobCreatorBuilder();
    }

    private static boolean logWarnings(ConfigurationService cfgService, Dialect dialect) {
        return cfgService.getSetting("hibernate.jdbc.log.warnings", StandardConverters.BOOLEAN, Boolean.valueOf(dialect.isJdbcLogWarningsEnabledByDefault()));
    }

    private static boolean globalQuoting(ConfigurationService cfgService) {
        return cfgService.getSetting("hibernate.globally_quoted_identifiers", StandardConverters.BOOLEAN, Boolean.valueOf(false));
    }

    private boolean globalQuotingSkippedForColumnDefinitions(ConfigurationService cfgService) {
        return cfgService.getSetting("hibernate.globally_quoted_identifiers_skip_column_definitions", StandardConverters.BOOLEAN, Boolean.valueOf(false));
    }

    private static boolean autoKeywordQuoting(ConfigurationService cfgService) {
        return cfgService.getSetting("hibernate.auto_quote_keyword", StandardConverters.BOOLEAN, Boolean.valueOf(false));
    }

    public JdbcEnvironmentImpl(DatabaseMetaData databaseMetaData, Dialect dialect, JdbcConnectionAccess jdbcConnectionAccess) throws SQLException {
        this.dialect = dialect;
        this.sqlExceptionHelper = this.buildSqlExceptionHelper(dialect, false);
        NameQualifierSupport nameQualifierSupport = dialect.getNameQualifierSupport();
        if (nameQualifierSupport == null) {
            nameQualifierSupport = this.determineNameQualifierSupport(databaseMetaData);
        }
        this.nameQualifierSupport = nameQualifierSupport;
        IdentifierHelperBuilder identifierHelperBuilder = IdentifierHelperBuilder.from(this);
        identifierHelperBuilder.setNameQualifierSupport(nameQualifierSupport);
        IdentifierHelper identifierHelper = null;
        try {
            identifierHelper = dialect.buildIdentifierHelper(identifierHelperBuilder, databaseMetaData);
        }
        catch (SQLException sqle) {
            log.debug((Object)"There was a problem accessing DatabaseMetaData in building the JdbcEnvironment", (Throwable)sqle);
        }
        if (identifierHelper == null) {
            identifierHelper = identifierHelperBuilder.build();
        }
        this.identifierHelper = identifierHelper;
        this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl.Builder(this, true, jdbcConnectionAccess).apply(databaseMetaData).setSupportsNamedParameters(databaseMetaData.supportsNamedParameters()).build();
        this.currentCatalog = null;
        this.currentSchema = null;
        this.qualifiedObjectNameFormatter = new QualifiedObjectNameFormatterStandardImpl(nameQualifierSupport, databaseMetaData);
        this.lobCreatorBuilder = LobCreatorBuilderImpl.makeLobCreatorBuilder();
    }

    private NameQualifierSupport determineNameQualifierSupport(DatabaseMetaData databaseMetaData) throws SQLException {
        boolean supportsCatalogs = databaseMetaData.supportsCatalogsInTableDefinitions();
        boolean supportsSchemas = databaseMetaData.supportsSchemasInTableDefinitions();
        if (supportsCatalogs && supportsSchemas) {
            return NameQualifierSupport.BOTH;
        }
        if (supportsCatalogs) {
            return NameQualifierSupport.CATALOG;
        }
        if (supportsSchemas) {
            return NameQualifierSupport.SCHEMA;
        }
        return NameQualifierSupport.NONE;
    }

    @Deprecated
    public JdbcEnvironmentImpl(ServiceRegistryImplementor serviceRegistry, Dialect dialect, DatabaseMetaData databaseMetaData) throws SQLException {
        this(serviceRegistry, dialect);
    }

    public JdbcEnvironmentImpl(ServiceRegistryImplementor serviceRegistry, Dialect dialect, DatabaseMetaData databaseMetaData, JdbcConnectionAccess jdbcConnectionAccess) throws SQLException {
        this.dialect = dialect;
        ConfigurationService cfgService = serviceRegistry.getService(ConfigurationService.class);
        this.sqlExceptionHelper = this.buildSqlExceptionHelper(dialect, JdbcEnvironmentImpl.logWarnings(cfgService, dialect));
        NameQualifierSupport nameQualifierSupport = dialect.getNameQualifierSupport();
        if (nameQualifierSupport == null) {
            nameQualifierSupport = this.determineNameQualifierSupport(databaseMetaData);
        }
        this.nameQualifierSupport = nameQualifierSupport;
        IdentifierHelperBuilder identifierHelperBuilder = IdentifierHelperBuilder.from(this);
        identifierHelperBuilder.setGloballyQuoteIdentifiers(JdbcEnvironmentImpl.globalQuoting(cfgService));
        identifierHelperBuilder.setSkipGlobalQuotingForColumnDefinitions(this.globalQuotingSkippedForColumnDefinitions(cfgService));
        identifierHelperBuilder.setAutoQuoteKeywords(JdbcEnvironmentImpl.autoKeywordQuoting(cfgService));
        identifierHelperBuilder.setNameQualifierSupport(nameQualifierSupport);
        IdentifierHelper identifierHelper = null;
        try {
            identifierHelper = dialect.buildIdentifierHelper(identifierHelperBuilder, databaseMetaData);
        }
        catch (SQLException sqle) {
            log.debug((Object)"There was a problem accessing DatabaseMetaData in building the JdbcEnvironment", (Throwable)sqle);
        }
        if (identifierHelper == null) {
            identifierHelper = identifierHelperBuilder.build();
        }
        this.identifierHelper = identifierHelper;
        this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl.Builder(this, true, jdbcConnectionAccess).apply(databaseMetaData).setConnectionSchemaName(this.determineCurrentSchemaName(databaseMetaData, serviceRegistry, dialect)).setSupportsNamedParameters(dialect.supportsNamedParameters(databaseMetaData)).build();
        this.currentCatalog = identifierHelper.toIdentifier(this.extractedMetaDataSupport.getConnectionCatalogName());
        this.currentSchema = identifierHelper.toIdentifier(this.extractedMetaDataSupport.getConnectionSchemaName());
        this.qualifiedObjectNameFormatter = new QualifiedObjectNameFormatterStandardImpl(nameQualifierSupport, databaseMetaData);
        this.lobCreatorBuilder = LobCreatorBuilderImpl.makeLobCreatorBuilder(dialect, cfgService.getSettings(), databaseMetaData.getConnection());
    }

    private String determineCurrentSchemaName(DatabaseMetaData databaseMetaData, ServiceRegistry serviceRegistry, Dialect dialect) {
        Object setting = serviceRegistry.getService(ConfigurationService.class).getSettings().get(SCHEMA_NAME_RESOLVER);
        SchemaNameResolver schemaNameResolver = setting == null ? dialect.getSchemaNameResolver() : serviceRegistry.getService(StrategySelector.class).resolveDefaultableStrategy(SchemaNameResolver.class, setting, dialect.getSchemaNameResolver());
        try {
            return schemaNameResolver.resolveSchemaName(databaseMetaData.getConnection(), dialect);
        }
        catch (Exception e) {
            log.debug((Object)"Unable to resolve connection default schema", (Throwable)e);
            return null;
        }
    }

    private SqlExceptionHelper buildSqlExceptionHelper(Dialect dialect, boolean logWarnings) {
        StandardSQLExceptionConverter sqlExceptionConverter = new StandardSQLExceptionConverter();
        sqlExceptionConverter.addDelegate(dialect.buildSQLExceptionConversionDelegate());
        sqlExceptionConverter.addDelegate(new SQLExceptionTypeDelegate(dialect));
        sqlExceptionConverter.addDelegate(new SQLStateConversionDelegate(dialect));
        return new SqlExceptionHelper(sqlExceptionConverter, logWarnings);
    }

    @Override
    public Dialect getDialect() {
        return this.dialect;
    }

    @Override
    public ExtractedDatabaseMetaData getExtractedDatabaseMetaData() {
        return this.extractedMetaDataSupport;
    }

    @Override
    public Identifier getCurrentCatalog() {
        return this.currentCatalog;
    }

    @Override
    public Identifier getCurrentSchema() {
        return this.currentSchema;
    }

    @Override
    public QualifiedObjectNameFormatter getQualifiedObjectNameFormatter() {
        return this.qualifiedObjectNameFormatter;
    }

    @Override
    public IdentifierHelper getIdentifierHelper() {
        return this.identifierHelper;
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return this.nameQualifierSupport;
    }

    @Override
    public SqlExceptionHelper getSqlExceptionHelper() {
        return this.sqlExceptionHelper;
    }

    @Override
    public LobCreatorBuilder getLobCreatorBuilder() {
        return this.lobCreatorBuilder;
    }
}

