/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import java.util.Map;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.Interceptor;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.NullPrecedence;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.TimestampsCacheFactory;
import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.query.ImmutableEntityUpdateQueryHandlingMode;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.tuple.entity.EntityTuplizerFactory;

public class AbstractDelegatingSessionFactoryOptions
implements SessionFactoryOptions {
    private final SessionFactoryOptions delegate;

    public AbstractDelegatingSessionFactoryOptions(SessionFactoryOptions delegate) {
        this.delegate = delegate;
    }

    protected SessionFactoryOptions delegate() {
        return this.delegate;
    }

    @Override
    public String getUuid() {
        return this.delegate().getUuid();
    }

    @Override
    public StandardServiceRegistry getServiceRegistry() {
        return this.delegate.getServiceRegistry();
    }

    @Override
    public boolean isJpaBootstrap() {
        return this.delegate.isJpaBootstrap();
    }

    @Override
    public boolean isJtaTransactionAccessEnabled() {
        return this.delegate.isJtaTransactionAccessEnabled();
    }

    @Override
    public boolean isAllowRefreshDetachedEntity() {
        return this.delegate.isAllowRefreshDetachedEntity();
    }

    @Override
    public Object getBeanManagerReference() {
        return this.delegate.getBeanManagerReference();
    }

    @Override
    public Object getValidatorFactoryReference() {
        return this.delegate.getValidatorFactoryReference();
    }

    @Override
    public String getSessionFactoryName() {
        return this.delegate.getSessionFactoryName();
    }

    @Override
    public boolean isSessionFactoryNameAlsoJndiName() {
        return this.delegate.isSessionFactoryNameAlsoJndiName();
    }

    @Override
    public boolean isFlushBeforeCompletionEnabled() {
        return this.delegate.isFlushBeforeCompletionEnabled();
    }

    @Override
    public boolean isAutoCloseSessionEnabled() {
        return this.delegate.isAutoCloseSessionEnabled();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return this.delegate.isStatisticsEnabled();
    }

    @Override
    public Interceptor getInterceptor() {
        return this.delegate.getInterceptor();
    }

    @Override
    public StatementInspector getStatementInspector() {
        return this.delegate.getStatementInspector();
    }

    @Override
    public SessionFactoryObserver[] getSessionFactoryObservers() {
        return this.delegate.getSessionFactoryObservers();
    }

    @Override
    public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
        return this.delegate.getBaselineSessionEventsListenerBuilder();
    }

    @Override
    public boolean isIdentifierRollbackEnabled() {
        return this.delegate.isIdentifierRollbackEnabled();
    }

    @Override
    public EntityMode getDefaultEntityMode() {
        return this.delegate.getDefaultEntityMode();
    }

    @Override
    public EntityTuplizerFactory getEntityTuplizerFactory() {
        return this.delegate.getEntityTuplizerFactory();
    }

    @Override
    public boolean isCheckNullability() {
        return this.delegate.isCheckNullability();
    }

    @Override
    public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
        return this.delegate.isInitializeLazyStateOutsideTransactionsEnabled();
    }

    @Override
    public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
        return this.delegate.getMultiTableBulkIdStrategy();
    }

    @Override
    public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling() {
        return this.delegate.getTempTableDdlTransactionHandling();
    }

    @Override
    public BatchFetchStyle getBatchFetchStyle() {
        return this.delegate.getBatchFetchStyle();
    }

    @Override
    public boolean isDelayBatchFetchLoaderCreationsEnabled() {
        return this.delegate.isDelayBatchFetchLoaderCreationsEnabled();
    }

    @Override
    public int getDefaultBatchFetchSize() {
        return this.delegate.getDefaultBatchFetchSize();
    }

    @Override
    public Integer getMaximumFetchDepth() {
        return this.delegate.getMaximumFetchDepth();
    }

    @Override
    public NullPrecedence getDefaultNullPrecedence() {
        return this.delegate.getDefaultNullPrecedence();
    }

    @Override
    public boolean isOrderUpdatesEnabled() {
        return this.delegate.isOrderUpdatesEnabled();
    }

    @Override
    public boolean isOrderInsertsEnabled() {
        return this.delegate.isOrderInsertsEnabled();
    }

    @Override
    public MultiTenancyStrategy getMultiTenancyStrategy() {
        return this.delegate.getMultiTenancyStrategy();
    }

    @Override
    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return this.delegate.getCurrentTenantIdentifierResolver();
    }

    @Override
    public boolean isJtaTrackByThread() {
        return this.delegate.isJtaTrackByThread();
    }

    @Override
    public Map getQuerySubstitutions() {
        return this.delegate.getQuerySubstitutions();
    }

    @Override
    public boolean isNamedQueryStartupCheckingEnabled() {
        return this.delegate.isNamedQueryStartupCheckingEnabled();
    }

    @Override
    public boolean isConventionalJavaConstants() {
        return this.delegate.isConventionalJavaConstants();
    }

    @Override
    public boolean isProcedureParameterNullPassingEnabled() {
        return this.delegate.isProcedureParameterNullPassingEnabled();
    }

    @Override
    public boolean isCollectionJoinSubqueryRewriteEnabled() {
        return this.delegate.isCollectionJoinSubqueryRewriteEnabled();
    }

    @Override
    public boolean isAllowOutOfTransactionUpdateOperations() {
        return this.delegate.isAllowOutOfTransactionUpdateOperations();
    }

    @Override
    public boolean isReleaseResourcesOnCloseEnabled() {
        return this.delegate.isReleaseResourcesOnCloseEnabled();
    }

    @Override
    public boolean isSecondLevelCacheEnabled() {
        return this.delegate.isSecondLevelCacheEnabled();
    }

    @Override
    public boolean isQueryCacheEnabled() {
        return this.delegate.isQueryCacheEnabled();
    }

    @Override
    public TimestampsCacheFactory getTimestampsCacheFactory() {
        return this.delegate.getTimestampsCacheFactory();
    }

    @Override
    public String getCacheRegionPrefix() {
        return this.delegate.getCacheRegionPrefix();
    }

    @Override
    public boolean isMinimalPutsEnabled() {
        return this.delegate.isMinimalPutsEnabled();
    }

    @Override
    public boolean isStructuredCacheEntriesEnabled() {
        return this.delegate.isStructuredCacheEntriesEnabled();
    }

    @Override
    public boolean isDirectReferenceCacheEntriesEnabled() {
        return this.delegate.isDirectReferenceCacheEntriesEnabled();
    }

    @Override
    public boolean isAutoEvictCollectionCache() {
        return this.delegate.isAutoEvictCollectionCache();
    }

    @Override
    public SchemaAutoTooling getSchemaAutoTooling() {
        return this.delegate.getSchemaAutoTooling();
    }

    @Override
    public int getJdbcBatchSize() {
        return this.delegate.getJdbcBatchSize();
    }

    @Override
    public boolean isJdbcBatchVersionedData() {
        return this.delegate.isJdbcBatchVersionedData();
    }

    @Override
    public boolean isScrollableResultSetsEnabled() {
        return this.delegate.isScrollableResultSetsEnabled();
    }

    @Override
    public boolean isWrapResultSetsEnabled() {
        return this.delegate.isWrapResultSetsEnabled();
    }

    @Override
    public boolean isGetGeneratedKeysEnabled() {
        return this.delegate.isGetGeneratedKeysEnabled();
    }

    @Override
    public Integer getJdbcFetchSize() {
        return this.delegate.getJdbcFetchSize();
    }

    @Override
    public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
        return this.delegate.getPhysicalConnectionHandlingMode();
    }

    @Override
    public boolean doesConnectionProviderDisableAutoCommit() {
        return this.delegate.doesConnectionProviderDisableAutoCommit();
    }

    @Override
    public ConnectionReleaseMode getConnectionReleaseMode() {
        return this.delegate.getConnectionReleaseMode();
    }

    @Override
    public boolean isCommentsEnabled() {
        return this.delegate.isCommentsEnabled();
    }

    @Override
    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
        return this.delegate.getCustomEntityDirtinessStrategy();
    }

    @Override
    public EntityNameResolver[] getEntityNameResolvers() {
        return this.delegate.getEntityNameResolvers();
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return this.delegate.getEntityNotFoundDelegate();
    }

    @Override
    public Map<String, SQLFunction> getCustomSqlFunctionMap() {
        return this.delegate.getCustomSqlFunctionMap();
    }

    @Override
    public void setCheckNullability(boolean enabled) {
        this.delegate.setCheckNullability(enabled);
    }

    @Override
    public boolean isPreferUserTransaction() {
        return this.delegate.isPreferUserTransaction();
    }

    @Override
    public Class<? extends Interceptor> getStatelessInterceptorImplementor() {
        return this.delegate.getStatelessInterceptorImplementor();
    }

    @Override
    public Supplier<? extends Interceptor> getStatelessInterceptorImplementorSupplier() {
        return this.delegate.getStatelessInterceptorImplementorSupplier();
    }

    @Override
    public TimeZone getJdbcTimeZone() {
        return this.delegate.getJdbcTimeZone();
    }

    @Override
    public boolean isQueryParametersValidationEnabled() {
        return this.delegate.isQueryParametersValidationEnabled();
    }

    @Override
    public LiteralHandlingMode getCriteriaLiteralHandlingMode() {
        return this.delegate.getCriteriaLiteralHandlingMode();
    }

    @Override
    public boolean jdbcStyleParamsZeroBased() {
        return this.delegate.jdbcStyleParamsZeroBased();
    }

    @Override
    public JpaCompliance getJpaCompliance() {
        return this.delegate.getJpaCompliance();
    }

    @Override
    public boolean isFailOnPaginationOverCollectionFetchEnabled() {
        return this.delegate.isFailOnPaginationOverCollectionFetchEnabled();
    }

    @Override
    public ImmutableEntityUpdateQueryHandlingMode getImmutableEntityUpdateQueryHandlingMode() {
        return this.delegate.getImmutableEntityUpdateQueryHandlingMode();
    }

    @Override
    public String getDefaultCatalog() {
        return this.delegate.getDefaultCatalog();
    }

    @Override
    public String getDefaultSchema() {
        return this.delegate.getDefaultSchema();
    }

    @Override
    public boolean inClauseParameterPaddingEnabled() {
        return this.delegate.inClauseParameterPaddingEnabled();
    }

    @Override
    public boolean nativeExceptionHandling51Compliance() {
        return this.delegate.nativeExceptionHandling51Compliance();
    }

    @Override
    public int getQueryStatisticsMaxSize() {
        return this.delegate.getQueryStatisticsMaxSize();
    }

    @Override
    public boolean areJPACallbacksEnabled() {
        return this.delegate.areJPACallbacksEnabled();
    }

    @Override
    public boolean isEnhancementAsProxyEnabled() {
        return this.delegate.isEnhancementAsProxyEnabled();
    }

    @Override
    public boolean isCollectionsInDefaultFetchGroupEnabled() {
        return this.delegate.isCollectionsInDefaultFetchGroupEnabled();
    }

    @Override
    public boolean isOmitJoinOfSuperclassTablesEnabled() {
        return this.delegate.isOmitJoinOfSuperclassTablesEnabled();
    }
}

