/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.internal;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.NullPrecedence;
import org.hibernate.SessionEventListener;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.boot.internal.DefaultCustomEntityDirtinessStrategy;
import org.hibernate.boot.internal.StandardEntityNotFoundDelegate;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cache.internal.StandardTimestampsCacheFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsCacheFactory;
import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.config.internal.ConfigurationServiceImpl;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.id.uuid.LocalObjectUuidHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.jpa.spi.MutableJpaCompliance;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.query.ImmutableEntityUpdateQueryHandlingMode;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.tuple.entity.EntityTuplizerFactory;

public class SessionFactoryOptionsBuilder
implements SessionFactoryOptions {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SessionFactoryOptionsBuilder.class);
    private final String uuid = LocalObjectUuidHelper.generateLocalObjectUuid();
    private final StandardServiceRegistry serviceRegistry;
    private Object beanManagerReference;
    private Object validatorFactoryReference;
    private boolean jpaBootstrap;
    private String sessionFactoryName;
    private boolean sessionFactoryNameAlsoJndiName;
    private boolean flushBeforeCompletionEnabled;
    private boolean autoCloseSessionEnabled;
    private boolean jtaTransactionAccessEnabled;
    private boolean allowOutOfTransactionUpdateOperations;
    private boolean releaseResourcesOnCloseEnabled;
    private boolean allowRefreshDetachedEntity;
    private boolean jtaTrackByThread;
    private boolean preferUserTransaction;
    private boolean statisticsEnabled;
    private Interceptor interceptor;
    private Class<? extends Interceptor> statelessInterceptorClass;
    private Supplier<? extends Interceptor> statelessInterceptorSupplier;
    private StatementInspector statementInspector;
    private List<SessionFactoryObserver> sessionFactoryObserverList = new ArrayList<SessionFactoryObserver>();
    private BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder;
    private CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
    private List<EntityNameResolver> entityNameResolvers = new ArrayList<EntityNameResolver>();
    private EntityNotFoundDelegate entityNotFoundDelegate;
    private boolean identifierRollbackEnabled;
    private EntityMode defaultEntityMode;
    private EntityTuplizerFactory entityTuplizerFactory = new EntityTuplizerFactory();
    private boolean checkNullability;
    private boolean initializeLazyStateOutsideTransactions;
    private MultiTableBulkIdStrategy multiTableBulkIdStrategy;
    private TempTableDdlTransactionHandling tempTableDdlTransactionHandling;
    private BatchFetchStyle batchFetchStyle;
    private boolean delayBatchFetchLoaderCreations;
    private int defaultBatchFetchSize;
    private Integer maximumFetchDepth;
    private NullPrecedence defaultNullPrecedence;
    private boolean orderUpdatesEnabled;
    private boolean orderInsertsEnabled;
    private boolean postInsertIdentifierDelayed;
    private boolean collectionsInDefaultFetchGroupEnabled;
    private boolean callbacksEnabled;
    private MultiTenancyStrategy multiTenancyStrategy;
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
    private Map querySubstitutions;
    private boolean namedQueryStartupCheckingEnabled;
    private boolean conventionalJavaConstants;
    private final boolean procedureParameterNullPassingEnabled;
    private final boolean collectionJoinSubqueryRewriteEnabled;
    private boolean jdbcStyleParamsZeroBased;
    private final boolean omitJoinOfSuperclassTablesEnabled;
    private boolean secondLevelCacheEnabled;
    private boolean queryCacheEnabled;
    private TimestampsCacheFactory timestampsCacheFactory;
    private String cacheRegionPrefix;
    private boolean minimalPutsEnabled;
    private boolean structuredCacheEntriesEnabled;
    private boolean directReferenceCacheEntriesEnabled;
    private boolean autoEvictCollectionCache;
    private SchemaAutoTooling schemaAutoTooling;
    private boolean getGeneratedKeysEnabled;
    private int jdbcBatchSize;
    private boolean jdbcBatchVersionedData;
    private Integer jdbcFetchSize;
    private boolean scrollableResultSetsEnabled;
    private boolean commentsEnabled;
    private PhysicalConnectionHandlingMode connectionHandlingMode;
    private boolean connectionProviderDisablesAutoCommit;
    private boolean wrapResultSetsEnabled;
    private TimeZone jdbcTimeZone;
    private boolean queryParametersValidationEnabled;
    private LiteralHandlingMode criteriaLiteralHandlingMode;
    private ImmutableEntityUpdateQueryHandlingMode immutableEntityUpdateQueryHandlingMode;
    private final String defaultCatalog;
    private final String defaultSchema;
    private Map<String, SQLFunction> sqlFunctions;
    private JpaCompliance jpaCompliance;
    private boolean failOnPaginationOverCollectionFetchEnabled;
    private boolean inClauseParameterPaddingEnabled;
    private boolean nativeExceptionHandling51Compliance;
    private int queryStatisticsMaxSize;

    public SessionFactoryOptionsBuilder(StandardServiceRegistry serviceRegistry, BootstrapContext context) {
        this.serviceRegistry = serviceRegistry;
        this.jpaBootstrap = context.isJpaBootstrap();
        StrategySelector strategySelector = serviceRegistry.getService(StrategySelector.class);
        ConfigurationService cfgService = serviceRegistry.getService(ConfigurationService.class);
        JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        HashMap<Object, Object> configurationSettings = new HashMap<Object, Object>();
        configurationSettings.putAll(jdbcServices.getJdbcEnvironment().getDialect().getDefaultProperties());
        configurationSettings.putAll(cfgService.getSettings());
        if (cfgService == null) {
            cfgService = new ConfigurationServiceImpl(configurationSettings);
            ((ConfigurationServiceImpl)cfgService).injectServices((ServiceRegistryImplementor)((Object)serviceRegistry));
        }
        this.beanManagerReference = configurationSettings.getOrDefault("javax.persistence.bean.manager", configurationSettings.get("jakarta.persistence.bean.manager"));
        this.validatorFactoryReference = configurationSettings.getOrDefault("javax.persistence.validation.factory", configurationSettings.get("jakarta.persistence.validation.factory"));
        this.sessionFactoryName = (String)configurationSettings.get("hibernate.session_factory_name");
        this.sessionFactoryNameAlsoJndiName = cfgService.getSetting("hibernate.session_factory_name_is_jndi", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.jtaTransactionAccessEnabled = cfgService.getSetting("hibernate.jta.allowTransactionAccess", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.allowRefreshDetachedEntity = cfgService.getSetting("hibernate.allow_refresh_detached_entity", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.flushBeforeCompletionEnabled = cfgService.getSetting("hibernate.transaction.flush_before_completion", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.autoCloseSessionEnabled = cfgService.getSetting("hibernate.transaction.auto_close_session", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        this.statisticsEnabled = cfgService.getSetting("hibernate.generate_statistics", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        this.interceptor = SessionFactoryOptionsBuilder.determineInterceptor(configurationSettings, strategySelector);
        this.statelessInterceptorSupplier = SessionFactoryOptionsBuilder.determineStatelessInterceptor(configurationSettings, strategySelector);
        this.statementInspector = strategySelector.resolveStrategy(StatementInspector.class, configurationSettings.get("hibernate.session_factory.statement_inspector"));
        String autoSessionEventsListenerName = (String)configurationSettings.get("hibernate.session.events.auto");
        Class<SessionEventListener> autoSessionEventsListener = autoSessionEventsListenerName == null ? null : strategySelector.selectStrategyImplementor(SessionEventListener.class, autoSessionEventsListenerName);
        boolean logSessionMetrics = cfgService.getSetting("hibernate.session.events.log", StandardConverters.BOOLEAN, Boolean.valueOf(this.statisticsEnabled));
        this.baselineSessionEventsListenerBuilder = new BaselineSessionEventsListenerBuilder(logSessionMetrics, autoSessionEventsListener);
        this.customEntityDirtinessStrategy = strategySelector.resolveDefaultableStrategy(CustomEntityDirtinessStrategy.class, configurationSettings.get("hibernate.entity_dirtiness_strategy"), DefaultCustomEntityDirtinessStrategy.INSTANCE);
        this.entityNotFoundDelegate = StandardEntityNotFoundDelegate.INSTANCE;
        this.identifierRollbackEnabled = cfgService.getSetting("hibernate.use_identifier_rollback", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        this.defaultEntityMode = EntityMode.parse((String)configurationSettings.get("hibernate.default_entity_mode"));
        this.checkNullability = cfgService.getSetting("hibernate.check_nullability", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.initializeLazyStateOutsideTransactions = cfgService.getSetting("hibernate.enable_lazy_load_no_trans", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        this.multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy(configurationSettings);
        this.currentTenantIdentifierResolver = strategySelector.resolveStrategy(CurrentTenantIdentifierResolver.class, configurationSettings.get("hibernate.tenant_identifier_resolver"));
        this.multiTableBulkIdStrategy = strategySelector.resolveDefaultableStrategy(MultiTableBulkIdStrategy.class, configurationSettings.get("hibernate.hql.bulk_id_strategy"), jdbcServices.getJdbcEnvironment().getDialect().getDefaultMultiTableBulkIdStrategy());
        this.batchFetchStyle = BatchFetchStyle.interpret(configurationSettings.get("hibernate.batch_fetch_style"));
        this.delayBatchFetchLoaderCreations = cfgService.getSetting("hibernate.loader.delay_entity_loader_creations", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.defaultBatchFetchSize = ConfigurationHelper.getInt("hibernate.default_batch_fetch_size", configurationSettings, -1);
        this.maximumFetchDepth = ConfigurationHelper.getInteger("hibernate.max_fetch_depth", configurationSettings);
        String defaultNullPrecedence = ConfigurationHelper.getString("hibernate.order_by.default_null_ordering", configurationSettings, "none", "first", "last");
        this.defaultNullPrecedence = NullPrecedence.parse(defaultNullPrecedence);
        this.orderUpdatesEnabled = ConfigurationHelper.getBoolean("hibernate.order_updates", configurationSettings);
        this.orderInsertsEnabled = ConfigurationHelper.getBoolean("hibernate.order_inserts", configurationSettings);
        this.callbacksEnabled = ConfigurationHelper.getBoolean("hibernate.jpa_callbacks.enabled", configurationSettings, true);
        this.jtaTrackByThread = cfgService.getSetting("hibernate.jta.track_by_thread", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.querySubstitutions = ConfigurationHelper.toMap("hibernate.query.substitutions", " ,=;:\n\t\r\f", configurationSettings);
        this.namedQueryStartupCheckingEnabled = cfgService.getSetting("hibernate.query.startup_check", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.conventionalJavaConstants = cfgService.getSetting("hibernate.query.conventional_java_constants", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.procedureParameterNullPassingEnabled = cfgService.getSetting("hibernate.proc.param_null_passing", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        this.collectionJoinSubqueryRewriteEnabled = cfgService.getSetting("hibernate.collection_join_subquery", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.omitJoinOfSuperclassTablesEnabled = cfgService.getSetting("hibernate.query.omit_join_of_superclass_tables", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        RegionFactory regionFactory = serviceRegistry.getService(RegionFactory.class);
        if (!NoCachingRegionFactory.class.isInstance(regionFactory)) {
            this.secondLevelCacheEnabled = cfgService.getSetting("hibernate.cache.use_second_level_cache", StandardConverters.BOOLEAN, Boolean.valueOf(true));
            this.queryCacheEnabled = cfgService.getSetting("hibernate.cache.use_query_cache", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.timestampsCacheFactory = strategySelector.resolveDefaultableStrategy(TimestampsCacheFactory.class, configurationSettings.get("hibernate.cache.query_cache_factory"), StandardTimestampsCacheFactory.INSTANCE);
            this.cacheRegionPrefix = ConfigurationHelper.extractPropertyValue("hibernate.cache.region_prefix", configurationSettings);
            this.minimalPutsEnabled = cfgService.getSetting("hibernate.cache.use_minimal_puts", StandardConverters.BOOLEAN, Boolean.valueOf(regionFactory.isMinimalPutsEnabledByDefault()));
            this.structuredCacheEntriesEnabled = cfgService.getSetting("hibernate.cache.use_structured_entries", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.directReferenceCacheEntriesEnabled = cfgService.getSetting("hibernate.cache.use_reference_entries", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.autoEvictCollectionCache = cfgService.getSetting("hibernate.cache.auto_evict_collection_cache", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        } else {
            this.secondLevelCacheEnabled = false;
            this.queryCacheEnabled = false;
            this.timestampsCacheFactory = null;
            this.cacheRegionPrefix = null;
            this.minimalPutsEnabled = false;
            this.structuredCacheEntriesEnabled = false;
            this.directReferenceCacheEntriesEnabled = false;
            this.autoEvictCollectionCache = false;
        }
        try {
            this.schemaAutoTooling = SchemaAutoTooling.interpret((String)configurationSettings.get("hibernate.hbm2ddl.auto"));
        }
        catch (Exception e) {
            log.warn(e.getMessage() + "  Ignoring");
        }
        ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
        this.tempTableDdlTransactionHandling = TempTableDdlTransactionHandling.NONE;
        if (meta.doesDataDefinitionCauseTransactionCommit()) {
            this.tempTableDdlTransactionHandling = meta.supportsDataDefinitionInTransaction() ? TempTableDdlTransactionHandling.ISOLATE_AND_TRANSACT : TempTableDdlTransactionHandling.ISOLATE;
        }
        this.jdbcBatchSize = ConfigurationHelper.getInt("hibernate.jdbc.batch_size", configurationSettings, 1);
        if (!meta.supportsBatchUpdates()) {
            this.jdbcBatchSize = 0;
        }
        this.jdbcBatchVersionedData = ConfigurationHelper.getBoolean("hibernate.jdbc.batch_versioned_data", configurationSettings, true);
        this.scrollableResultSetsEnabled = ConfigurationHelper.getBoolean("hibernate.jdbc.use_scrollable_resultset", configurationSettings, meta.supportsScrollableResults());
        this.wrapResultSetsEnabled = ConfigurationHelper.getBoolean("hibernate.jdbc.wrap_result_sets", configurationSettings, false);
        this.getGeneratedKeysEnabled = ConfigurationHelper.getBoolean("hibernate.jdbc.use_get_generated_keys", configurationSettings, meta.supportsGetGeneratedKeys());
        this.jdbcFetchSize = ConfigurationHelper.getInteger("hibernate.jdbc.fetch_size", configurationSettings);
        this.connectionHandlingMode = this.interpretConnectionHandlingMode(configurationSettings, serviceRegistry);
        this.connectionProviderDisablesAutoCommit = ConfigurationHelper.getBoolean("hibernate.connection.provider_disables_autocommit", configurationSettings, false);
        this.commentsEnabled = ConfigurationHelper.getBoolean("hibernate.use_sql_comments", configurationSettings);
        this.preferUserTransaction = ConfigurationHelper.getBoolean("hibernate.jta.prefer_user_transaction", configurationSettings, false);
        this.allowOutOfTransactionUpdateOperations = ConfigurationHelper.getBoolean("hibernate.allow_update_outside_transaction", configurationSettings, false);
        this.releaseResourcesOnCloseEnabled = (Boolean)NullnessHelper.coalesceSuppliedValues(() -> ConfigurationHelper.getBooleanWrapper("hibernate.discard_pc_on_close", configurationSettings, null), () -> {
            Boolean oldSetting = ConfigurationHelper.getBooleanWrapper("hibernate.ejb.discard_pc_on_close", configurationSettings, null);
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.discard_pc_on_close", "hibernate.discard_pc_on_close");
            }
            return oldSetting;
        }, () -> false);
        Object jdbcTimeZoneValue = configurationSettings.get("hibernate.jdbc.time_zone");
        if (jdbcTimeZoneValue instanceof TimeZone) {
            this.jdbcTimeZone = (TimeZone)jdbcTimeZoneValue;
        } else if (jdbcTimeZoneValue instanceof ZoneId) {
            this.jdbcTimeZone = TimeZone.getTimeZone((ZoneId)jdbcTimeZoneValue);
        } else if (jdbcTimeZoneValue instanceof String) {
            this.jdbcTimeZone = TimeZone.getTimeZone(ZoneId.of((String)jdbcTimeZoneValue));
        } else if (jdbcTimeZoneValue != null) {
            throw new IllegalArgumentException("Configuration property hibernate.jdbc.time_zone value [" + jdbcTimeZoneValue + "] is not supported!");
        }
        this.queryParametersValidationEnabled = ConfigurationHelper.getBoolean("hibernate.query.validate_parameters", configurationSettings, true);
        this.criteriaLiteralHandlingMode = LiteralHandlingMode.interpret(configurationSettings.get("hibernate.criteria.literal_handling_mode"));
        this.jdbcStyleParamsZeroBased = ConfigurationHelper.getBoolean("hibernate.query.sql.jdbc_style_params_base", configurationSettings, false);
        this.jpaCompliance = context.getJpaCompliance();
        this.failOnPaginationOverCollectionFetchEnabled = ConfigurationHelper.getBoolean("hibernate.query.fail_on_pagination_over_collection_fetch", configurationSettings, false);
        this.immutableEntityUpdateQueryHandlingMode = ImmutableEntityUpdateQueryHandlingMode.interpret(configurationSettings.get("hibernate.query.immutable_entity_update_query_handling_mode"));
        this.defaultCatalog = ConfigurationHelper.getString("hibernate.default_catalog", configurationSettings);
        this.defaultSchema = ConfigurationHelper.getString("hibernate.default_schema", configurationSettings);
        this.inClauseParameterPaddingEnabled = ConfigurationHelper.getBoolean("hibernate.query.in_clause_parameter_padding", configurationSettings, false);
        this.nativeExceptionHandling51Compliance = ConfigurationHelper.getBoolean("hibernate.native_exception_handling_51_compliance", configurationSettings, false);
        this.queryStatisticsMaxSize = ConfigurationHelper.getInt("hibernate.statistics.query_max_size", configurationSettings, 5000);
        if (context.isJpaBootstrap() && this.nativeExceptionHandling51Compliance) {
            log.nativeExceptionHandling51ComplianceJpaBootstrapping();
            this.nativeExceptionHandling51Compliance = false;
        }
    }

    private static Interceptor determineInterceptor(Map configurationSettings, StrategySelector strategySelector) {
        Object setting = NullnessHelper.coalesceSuppliedValues(() -> configurationSettings.get("hibernate.session_factory.interceptor"), () -> {
            Object oldSetting = configurationSettings.get("hibernate.ejb.interceptor");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.interceptor", "hibernate.session_factory.interceptor");
            }
            return oldSetting;
        });
        return strategySelector.resolveStrategy(Interceptor.class, setting);
    }

    private static Supplier<? extends Interceptor> determineStatelessInterceptor(Map configurationSettings, StrategySelector strategySelector) {
        Object setting = NullnessHelper.coalesceSuppliedValues(() -> configurationSettings.get("hibernate.session_factory.session_scoped_interceptor"), () -> {
            Object oldSetting = configurationSettings.get("hibernate.ejb.interceptor.session_scoped");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.interceptor.session_scoped", "hibernate.session_factory.session_scoped_interceptor");
            }
            return oldSetting;
        });
        if (setting == null) {
            return null;
        }
        if (setting instanceof Supplier) {
            return (Supplier)setting;
        }
        if (setting instanceof Class) {
            Class clazz = (Class)setting;
            return SessionFactoryOptionsBuilder.interceptorSupplier(clazz);
        }
        return SessionFactoryOptionsBuilder.interceptorSupplier(strategySelector.selectStrategyImplementor(Interceptor.class, setting.toString()));
    }

    private static Supplier<? extends Interceptor> interceptorSupplier(Class<? extends Interceptor> clazz) {
        return () -> {
            try {
                return (Interceptor)clazz.newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new HibernateException("Could not supply session-scoped SessionFactory Interceptor", e);
            }
        };
    }

    private PhysicalConnectionHandlingMode interpretConnectionHandlingMode(Map configurationSettings, StandardServiceRegistry serviceRegistry) {
        PhysicalConnectionHandlingMode specifiedHandlingMode = PhysicalConnectionHandlingMode.interpret(configurationSettings.get("hibernate.connection.handling_mode"));
        if (specifiedHandlingMode != null) {
            return specifiedHandlingMode;
        }
        TransactionCoordinatorBuilder transactionCoordinatorBuilder = serviceRegistry.getService(TransactionCoordinatorBuilder.class);
        ConnectionAcquisitionMode specifiedAcquisitionMode = ConnectionAcquisitionMode.interpret(configurationSettings.get("hibernate.connection.acquisition_mode"));
        ConnectionReleaseMode specifiedReleaseMode = ConnectionReleaseMode.interpret(configurationSettings.get("hibernate.connection.release_mode"));
        if (specifiedAcquisitionMode != null || specifiedReleaseMode != null) {
            return this.interpretConnectionHandlingMode(specifiedAcquisitionMode, specifiedReleaseMode, configurationSettings, transactionCoordinatorBuilder);
        }
        return transactionCoordinatorBuilder.getDefaultConnectionHandlingMode();
    }

    private PhysicalConnectionHandlingMode interpretConnectionHandlingMode(ConnectionAcquisitionMode specifiedAcquisitionMode, ConnectionReleaseMode specifiedReleaseMode, Map configurationSettings, TransactionCoordinatorBuilder transactionCoordinatorBuilder) {
        ConnectionReleaseMode effectiveReleaseMode;
        ConnectionAcquisitionMode effectiveAcquisitionMode;
        DeprecationLogger.DEPRECATION_LOGGER.logUseOfDeprecatedConnectionHandlingSettings();
        ConnectionAcquisitionMode connectionAcquisitionMode = effectiveAcquisitionMode = specifiedAcquisitionMode == null ? ConnectionAcquisitionMode.AS_NEEDED : specifiedAcquisitionMode;
        if (specifiedReleaseMode == null) {
            String releaseModeName = ConfigurationHelper.getString("hibernate.connection.release_mode", configurationSettings, "auto");
            assert ("auto".equalsIgnoreCase(releaseModeName));
            effectiveReleaseMode = effectiveAcquisitionMode == ConnectionAcquisitionMode.IMMEDIATELY ? ConnectionReleaseMode.ON_CLOSE : transactionCoordinatorBuilder.getDefaultConnectionReleaseMode();
        } else {
            effectiveReleaseMode = specifiedReleaseMode;
        }
        return PhysicalConnectionHandlingMode.interpret(effectiveAcquisitionMode, effectiveReleaseMode);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public StandardServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public boolean isJpaBootstrap() {
        return this.jpaBootstrap;
    }

    @Override
    public boolean isJtaTransactionAccessEnabled() {
        return this.jtaTransactionAccessEnabled;
    }

    @Override
    public boolean isAllowRefreshDetachedEntity() {
        return this.allowRefreshDetachedEntity;
    }

    @Override
    public boolean isAllowOutOfTransactionUpdateOperations() {
        return this.allowOutOfTransactionUpdateOperations;
    }

    @Override
    public boolean isReleaseResourcesOnCloseEnabled() {
        return this.releaseResourcesOnCloseEnabled;
    }

    @Override
    public Object getBeanManagerReference() {
        return this.beanManagerReference;
    }

    @Override
    public Object getValidatorFactoryReference() {
        return this.validatorFactoryReference;
    }

    @Override
    public String getSessionFactoryName() {
        return this.sessionFactoryName;
    }

    @Override
    public boolean isSessionFactoryNameAlsoJndiName() {
        return this.sessionFactoryNameAlsoJndiName;
    }

    @Override
    public boolean isFlushBeforeCompletionEnabled() {
        return this.flushBeforeCompletionEnabled;
    }

    @Override
    public boolean isAutoCloseSessionEnabled() {
        return this.autoCloseSessionEnabled;
    }

    @Override
    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    @Override
    public Interceptor getInterceptor() {
        return this.interceptor == null ? EmptyInterceptor.INSTANCE : this.interceptor;
    }

    @Override
    public Class<? extends Interceptor> getStatelessInterceptorImplementor() {
        return this.statelessInterceptorClass;
    }

    @Override
    public Supplier<? extends Interceptor> getStatelessInterceptorImplementorSupplier() {
        return this.statelessInterceptorSupplier;
    }

    @Override
    public StatementInspector getStatementInspector() {
        return this.statementInspector;
    }

    @Override
    public SessionFactoryObserver[] getSessionFactoryObservers() {
        return this.sessionFactoryObserverList.toArray(new SessionFactoryObserver[this.sessionFactoryObserverList.size()]);
    }

    @Override
    public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
        return this.baselineSessionEventsListenerBuilder;
    }

    @Override
    public boolean isIdentifierRollbackEnabled() {
        return this.identifierRollbackEnabled;
    }

    @Override
    public EntityMode getDefaultEntityMode() {
        return this.defaultEntityMode;
    }

    @Override
    public EntityTuplizerFactory getEntityTuplizerFactory() {
        return this.entityTuplizerFactory;
    }

    @Override
    public boolean isCheckNullability() {
        return this.checkNullability;
    }

    @Override
    public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
        return this.initializeLazyStateOutsideTransactions;
    }

    @Override
    public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
        return this.multiTableBulkIdStrategy;
    }

    @Override
    public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling() {
        return this.tempTableDdlTransactionHandling;
    }

    @Override
    public BatchFetchStyle getBatchFetchStyle() {
        return this.batchFetchStyle;
    }

    @Override
    public boolean isDelayBatchFetchLoaderCreationsEnabled() {
        return this.delayBatchFetchLoaderCreations;
    }

    @Override
    public int getDefaultBatchFetchSize() {
        return this.defaultBatchFetchSize;
    }

    @Override
    public Integer getMaximumFetchDepth() {
        return this.maximumFetchDepth;
    }

    @Override
    public NullPrecedence getDefaultNullPrecedence() {
        return this.defaultNullPrecedence;
    }

    @Override
    public boolean isOrderUpdatesEnabled() {
        return this.orderUpdatesEnabled;
    }

    @Override
    public boolean isOrderInsertsEnabled() {
        return this.orderInsertsEnabled;
    }

    @Override
    public MultiTenancyStrategy getMultiTenancyStrategy() {
        return this.multiTenancyStrategy;
    }

    @Override
    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return this.currentTenantIdentifierResolver;
    }

    @Override
    public boolean isJtaTrackByThread() {
        return this.jtaTrackByThread;
    }

    @Override
    public Map getQuerySubstitutions() {
        return this.querySubstitutions;
    }

    @Override
    public boolean isNamedQueryStartupCheckingEnabled() {
        return this.namedQueryStartupCheckingEnabled;
    }

    @Override
    public boolean isConventionalJavaConstants() {
        return this.conventionalJavaConstants;
    }

    @Override
    public boolean isProcedureParameterNullPassingEnabled() {
        return this.procedureParameterNullPassingEnabled;
    }

    @Override
    public boolean isCollectionJoinSubqueryRewriteEnabled() {
        return this.collectionJoinSubqueryRewriteEnabled;
    }

    @Override
    public boolean isSecondLevelCacheEnabled() {
        return this.secondLevelCacheEnabled;
    }

    @Override
    public boolean isQueryCacheEnabled() {
        return this.queryCacheEnabled;
    }

    @Override
    public TimestampsCacheFactory getTimestampsCacheFactory() {
        return this.timestampsCacheFactory;
    }

    @Override
    public String getCacheRegionPrefix() {
        return this.cacheRegionPrefix;
    }

    @Override
    public boolean isMinimalPutsEnabled() {
        return this.minimalPutsEnabled;
    }

    @Override
    public boolean isStructuredCacheEntriesEnabled() {
        return this.structuredCacheEntriesEnabled;
    }

    @Override
    public boolean isDirectReferenceCacheEntriesEnabled() {
        return this.directReferenceCacheEntriesEnabled;
    }

    @Override
    public boolean isAutoEvictCollectionCache() {
        return this.autoEvictCollectionCache;
    }

    @Override
    public SchemaAutoTooling getSchemaAutoTooling() {
        return this.schemaAutoTooling;
    }

    @Override
    public int getJdbcBatchSize() {
        return this.jdbcBatchSize;
    }

    @Override
    public boolean isJdbcBatchVersionedData() {
        return this.jdbcBatchVersionedData;
    }

    @Override
    public boolean isScrollableResultSetsEnabled() {
        return this.scrollableResultSetsEnabled;
    }

    @Override
    public boolean isWrapResultSetsEnabled() {
        return this.wrapResultSetsEnabled;
    }

    @Override
    public boolean isGetGeneratedKeysEnabled() {
        return this.getGeneratedKeysEnabled;
    }

    @Override
    public Integer getJdbcFetchSize() {
        return this.jdbcFetchSize;
    }

    @Override
    public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
        return this.connectionHandlingMode;
    }

    @Override
    public void setCheckNullability(boolean enabled) {
        this.checkNullability = enabled;
    }

    @Override
    public ConnectionReleaseMode getConnectionReleaseMode() {
        return this.getPhysicalConnectionHandlingMode().getReleaseMode();
    }

    @Override
    public boolean doesConnectionProviderDisableAutoCommit() {
        return this.connectionProviderDisablesAutoCommit;
    }

    @Override
    public boolean isCommentsEnabled() {
        return this.commentsEnabled;
    }

    @Override
    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
        return this.customEntityDirtinessStrategy;
    }

    @Override
    public EntityNameResolver[] getEntityNameResolvers() {
        return this.entityNameResolvers.toArray(new EntityNameResolver[this.entityNameResolvers.size()]);
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return this.entityNotFoundDelegate;
    }

    @Override
    public Map<String, SQLFunction> getCustomSqlFunctionMap() {
        return this.sqlFunctions == null ? Collections.emptyMap() : this.sqlFunctions;
    }

    @Override
    public boolean isPreferUserTransaction() {
        return this.preferUserTransaction;
    }

    @Override
    public TimeZone getJdbcTimeZone() {
        return this.jdbcTimeZone;
    }

    @Override
    public boolean isQueryParametersValidationEnabled() {
        return this.queryParametersValidationEnabled;
    }

    @Override
    public LiteralHandlingMode getCriteriaLiteralHandlingMode() {
        return this.criteriaLiteralHandlingMode;
    }

    @Override
    public ImmutableEntityUpdateQueryHandlingMode getImmutableEntityUpdateQueryHandlingMode() {
        return this.immutableEntityUpdateQueryHandlingMode;
    }

    @Override
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    @Override
    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    @Override
    public boolean jdbcStyleParamsZeroBased() {
        return this.jdbcStyleParamsZeroBased;
    }

    @Override
    public boolean isFailOnPaginationOverCollectionFetchEnabled() {
        return this.failOnPaginationOverCollectionFetchEnabled;
    }

    @Override
    public boolean inClauseParameterPaddingEnabled() {
        return this.inClauseParameterPaddingEnabled;
    }

    @Override
    public JpaCompliance getJpaCompliance() {
        return this.jpaCompliance;
    }

    @Override
    public boolean nativeExceptionHandling51Compliance() {
        return this.nativeExceptionHandling51Compliance;
    }

    @Override
    public int getQueryStatisticsMaxSize() {
        return this.queryStatisticsMaxSize;
    }

    @Override
    public boolean areJPACallbacksEnabled() {
        return this.callbacksEnabled;
    }

    @Override
    public boolean isCollectionsInDefaultFetchGroupEnabled() {
        return this.collectionsInDefaultFetchGroupEnabled;
    }

    @Override
    public boolean isOmitJoinOfSuperclassTablesEnabled() {
        return this.omitJoinOfSuperclassTablesEnabled;
    }

    public void applyBeanManager(Object beanManager) {
        this.beanManagerReference = beanManager;
    }

    public void applyValidatorFactory(Object validatorFactory) {
        this.validatorFactoryReference = validatorFactory;
    }

    public void applySessionFactoryName(String sessionFactoryName) {
        this.sessionFactoryName = sessionFactoryName;
    }

    public void enableSessionFactoryNameAsJndiName(boolean isJndiName) {
        this.sessionFactoryNameAlsoJndiName = isJndiName;
    }

    public void enableSessionAutoClosing(boolean autoClosingEnabled) {
        this.autoCloseSessionEnabled = autoClosingEnabled;
    }

    public void enableSessionAutoFlushing(boolean flushBeforeCompletionEnabled) {
        this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
    }

    public void enableJtaTrackingByThread(boolean enabled) {
        this.jtaTrackByThread = enabled;
    }

    public void enablePreferUserTransaction(boolean preferUserTransaction) {
        this.preferUserTransaction = preferUserTransaction;
    }

    public void enableStatisticsSupport(boolean enabled) {
        this.statisticsEnabled = enabled;
    }

    public void addSessionFactoryObservers(SessionFactoryObserver ... observers) {
        Collections.addAll(this.sessionFactoryObserverList, observers);
    }

    public void applyInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void applyStatelessInterceptor(Class<? extends Interceptor> statelessInterceptorClass) {
        this.statelessInterceptorClass = statelessInterceptorClass;
    }

    public void applyStatelessInterceptorSupplier(Supplier<? extends Interceptor> statelessInterceptorSupplier) {
        this.statelessInterceptorSupplier = statelessInterceptorSupplier;
    }

    public void applyStatementInspector(StatementInspector statementInspector) {
        this.statementInspector = statementInspector;
    }

    public void applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
        this.customEntityDirtinessStrategy = strategy;
    }

    public void addEntityNameResolvers(EntityNameResolver ... entityNameResolvers) {
        Collections.addAll(this.entityNameResolvers, entityNameResolvers);
    }

    public void applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
        this.entityNotFoundDelegate = entityNotFoundDelegate;
    }

    public void enableIdentifierRollbackSupport(boolean enabled) {
        this.identifierRollbackEnabled = enabled;
    }

    public void applyDefaultEntityMode(EntityMode entityMode) {
        this.defaultEntityMode = entityMode;
    }

    public void enableNullabilityChecking(boolean enabled) {
        this.checkNullability = enabled;
    }

    public void allowLazyInitializationOutsideTransaction(boolean enabled) {
        this.initializeLazyStateOutsideTransactions = enabled;
    }

    public void applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
        this.entityTuplizerFactory = entityTuplizerFactory;
    }

    public void applyEntityTuplizer(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass) {
        this.entityTuplizerFactory.registerDefaultTuplizerClass(entityMode, tuplizerClass);
    }

    public void applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy) {
        this.multiTableBulkIdStrategy = strategy;
    }

    public void applyTempTableDdlTransactionHandling(TempTableDdlTransactionHandling handling) {
        this.tempTableDdlTransactionHandling = handling;
    }

    public void applyBatchFetchStyle(BatchFetchStyle style) {
        this.batchFetchStyle = style;
    }

    public void applyDelayedEntityLoaderCreations(boolean delay) {
        this.delayBatchFetchLoaderCreations = delay;
    }

    public void applyDefaultBatchFetchSize(int size) {
        this.defaultBatchFetchSize = size;
    }

    public void applyMaximumFetchDepth(int depth) {
        this.maximumFetchDepth = depth;
    }

    public void applyDefaultNullPrecedence(NullPrecedence nullPrecedence) {
        this.defaultNullPrecedence = nullPrecedence;
    }

    public void enableOrderingOfInserts(boolean enabled) {
        this.orderInsertsEnabled = enabled;
    }

    public void enableOrderingOfUpdates(boolean enabled) {
        this.orderUpdatesEnabled = enabled;
    }

    public void enableDelayedIdentityInserts(boolean enabled) {
        this.postInsertIdentifierDelayed = enabled;
    }

    public void applyMultiTenancyStrategy(MultiTenancyStrategy strategy) {
        this.multiTenancyStrategy = strategy;
    }

    public void applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
        this.currentTenantIdentifierResolver = resolver;
    }

    public void applyQuerySubstitutions(Map substitutions) {
        this.querySubstitutions.putAll(substitutions);
    }

    public void enableNamedQueryCheckingOnStartup(boolean enabled) {
        this.namedQueryStartupCheckingEnabled = enabled;
    }

    public void enableSecondLevelCacheSupport(boolean enabled) {
        this.secondLevelCacheEnabled = enabled;
    }

    public void enableQueryCacheSupport(boolean enabled) {
        this.queryCacheEnabled = enabled;
    }

    public void applyTimestampsCacheFactory(TimestampsCacheFactory factory) {
        this.timestampsCacheFactory = factory;
    }

    public void applyCacheRegionPrefix(String prefix) {
        this.cacheRegionPrefix = prefix;
    }

    public void enableMinimalPuts(boolean enabled) {
        this.minimalPutsEnabled = enabled;
    }

    public void enabledStructuredCacheEntries(boolean enabled) {
        this.structuredCacheEntriesEnabled = enabled;
    }

    public void allowDirectReferenceCacheEntries(boolean enabled) {
        this.directReferenceCacheEntriesEnabled = enabled;
    }

    public void enableAutoEvictCollectionCaches(boolean enabled) {
        this.autoEvictCollectionCache = enabled;
    }

    public void applyJdbcBatchSize(int size) {
        this.jdbcBatchSize = size;
    }

    public void enableJdbcBatchingForVersionedEntities(boolean enabled) {
        this.jdbcBatchVersionedData = enabled;
    }

    public void enableScrollableResultSupport(boolean enabled) {
        this.scrollableResultSetsEnabled = enabled;
    }

    @Deprecated
    public void enableResultSetWrappingSupport(boolean enabled) {
        this.wrapResultSetsEnabled = enabled;
    }

    public void enableGeneratedKeysSupport(boolean enabled) {
        this.getGeneratedKeysEnabled = enabled;
    }

    public void applyJdbcFetchSize(int size) {
        this.jdbcFetchSize = size;
    }

    public void applyConnectionHandlingMode(PhysicalConnectionHandlingMode mode) {
        this.connectionHandlingMode = mode;
    }

    public void applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
        this.connectionHandlingMode = this.connectionHandlingMode == null ? PhysicalConnectionHandlingMode.interpret(ConnectionAcquisitionMode.AS_NEEDED, connectionReleaseMode) : PhysicalConnectionHandlingMode.interpret(this.connectionHandlingMode.getAcquisitionMode(), connectionReleaseMode);
    }

    public void applyConnectionProviderDisablesAutoCommit(boolean providerDisablesAutoCommit) {
        this.connectionProviderDisablesAutoCommit = providerDisablesAutoCommit;
    }

    public void enableCommentsSupport(boolean enabled) {
        this.commentsEnabled = enabled;
    }

    public void applySqlFunction(String registrationName, SQLFunction sqlFunction) {
        if (this.sqlFunctions == null) {
            this.sqlFunctions = new HashMap<String, SQLFunction>();
        }
        this.sqlFunctions.put(registrationName, sqlFunction);
    }

    public void allowOutOfTransactionUpdateOperations(boolean allow) {
        this.allowOutOfTransactionUpdateOperations = allow;
    }

    public void enableReleaseResourcesOnClose(boolean enable) {
        this.releaseResourcesOnCloseEnabled = enable;
    }

    public void enableStrictJpaQueryLanguageCompliance(boolean enabled) {
        this.enableJpaQueryCompliance(enabled);
    }

    public void enableJpaQueryCompliance(boolean enabled) {
        this.mutableJpaCompliance().setQueryCompliance(enabled);
    }

    private MutableJpaCompliance mutableJpaCompliance() {
        if (!MutableJpaCompliance.class.isInstance(this.jpaCompliance)) {
            throw new IllegalStateException("JpaCompliance is no longer mutable");
        }
        return (MutableJpaCompliance)this.jpaCompliance;
    }

    public void enableJpaTransactionCompliance(boolean enabled) {
        this.mutableJpaCompliance().setTransactionCompliance(enabled);
    }

    public void enableJpaListCompliance(boolean enabled) {
        this.mutableJpaCompliance().setListCompliance(enabled);
    }

    public void enableJpaClosedCompliance(boolean enabled) {
        this.mutableJpaCompliance().setClosedCompliance(enabled);
    }

    public void enableJpaProxyCompliance(boolean enabled) {
        this.mutableJpaCompliance().setProxyCompliance(enabled);
    }

    public void enableJpaCachingCompliance(boolean enabled) {
        this.mutableJpaCompliance().setCachingCompliance(enabled);
    }

    public void enableCollectionInDefaultFetchGroup(boolean enabled) {
        this.collectionsInDefaultFetchGroupEnabled = enabled;
    }

    public void disableRefreshDetachedEntity() {
        this.allowRefreshDetachedEntity = false;
    }

    public void disableJtaTransactionAccess() {
        this.jtaTransactionAccessEnabled = false;
    }

    public void enableJdbcStyleParamsZeroBased() {
        this.jdbcStyleParamsZeroBased = true;
    }

    public SessionFactoryOptions buildOptions() {
        if (MutableJpaCompliance.class.isInstance(this.jpaCompliance)) {
            this.jpaCompliance = this.mutableJpaCompliance().immutableCopy();
        }
        return this;
    }
}

