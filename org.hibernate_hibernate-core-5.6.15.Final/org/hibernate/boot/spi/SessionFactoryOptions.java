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
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.NullPrecedence;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.boot.registry.StandardServiceRegistry;
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

public interface SessionFactoryOptions {
    public String getUuid();

    public StandardServiceRegistry getServiceRegistry();

    public Object getBeanManagerReference();

    public Object getValidatorFactoryReference();

    public boolean isJpaBootstrap();

    public boolean isJtaTransactionAccessEnabled();

    default public boolean isAllowRefreshDetachedEntity() {
        return false;
    }

    public String getSessionFactoryName();

    public boolean isSessionFactoryNameAlsoJndiName();

    public boolean isFlushBeforeCompletionEnabled();

    public boolean isAutoCloseSessionEnabled();

    public boolean isStatisticsEnabled();

    public Interceptor getInterceptor();

    @Deprecated
    public Class<? extends Interceptor> getStatelessInterceptorImplementor();

    default public Supplier<? extends Interceptor> getStatelessInterceptorImplementorSupplier() {
        return () -> {
            try {
                return this.getStatelessInterceptorImplementor().newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new HibernateException("Could not supply session-scoped SessionFactory Interceptor", e);
            }
        };
    }

    public StatementInspector getStatementInspector();

    public SessionFactoryObserver[] getSessionFactoryObservers();

    public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder();

    public boolean isIdentifierRollbackEnabled();

    public EntityMode getDefaultEntityMode();

    public EntityTuplizerFactory getEntityTuplizerFactory();

    public boolean isCheckNullability();

    public boolean isInitializeLazyStateOutsideTransactionsEnabled();

    public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy();

    public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling();

    public BatchFetchStyle getBatchFetchStyle();

    public boolean isDelayBatchFetchLoaderCreationsEnabled();

    public int getDefaultBatchFetchSize();

    public Integer getMaximumFetchDepth();

    public NullPrecedence getDefaultNullPrecedence();

    public boolean isOrderUpdatesEnabled();

    public boolean isOrderInsertsEnabled();

    public MultiTenancyStrategy getMultiTenancyStrategy();

    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();

    public boolean isJtaTrackByThread();

    public Map getQuerySubstitutions();

    @Deprecated
    default public boolean isStrictJpaQueryLanguageCompliance() {
        return this.getJpaCompliance().isJpaQueryComplianceEnabled();
    }

    public boolean isNamedQueryStartupCheckingEnabled();

    public boolean isConventionalJavaConstants();

    public boolean isSecondLevelCacheEnabled();

    public boolean isQueryCacheEnabled();

    public TimestampsCacheFactory getTimestampsCacheFactory();

    public String getCacheRegionPrefix();

    public boolean isMinimalPutsEnabled();

    public boolean isStructuredCacheEntriesEnabled();

    public boolean isDirectReferenceCacheEntriesEnabled();

    public boolean isAutoEvictCollectionCache();

    public SchemaAutoTooling getSchemaAutoTooling();

    public int getJdbcBatchSize();

    public boolean isJdbcBatchVersionedData();

    public boolean isScrollableResultSetsEnabled();

    @Deprecated
    public boolean isWrapResultSetsEnabled();

    public boolean isGetGeneratedKeysEnabled();

    public Integer getJdbcFetchSize();

    public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode();

    default public boolean doesConnectionProviderDisableAutoCommit() {
        return false;
    }

    @Deprecated
    public ConnectionReleaseMode getConnectionReleaseMode();

    public boolean isCommentsEnabled();

    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();

    public EntityNameResolver[] getEntityNameResolvers();

    public EntityNotFoundDelegate getEntityNotFoundDelegate();

    public Map<String, SQLFunction> getCustomSqlFunctionMap();

    public void setCheckNullability(boolean var1);

    public boolean isPreferUserTransaction();

    @Deprecated
    public boolean isProcedureParameterNullPassingEnabled();

    public boolean isCollectionJoinSubqueryRewriteEnabled();

    public boolean isAllowOutOfTransactionUpdateOperations();

    public boolean isReleaseResourcesOnCloseEnabled();

    public TimeZone getJdbcTimeZone();

    default public boolean isQueryParametersValidationEnabled() {
        return this.isJpaBootstrap();
    }

    default public LiteralHandlingMode getCriteriaLiteralHandlingMode() {
        return LiteralHandlingMode.AUTO;
    }

    public boolean jdbcStyleParamsZeroBased();

    public JpaCompliance getJpaCompliance();

    public boolean isFailOnPaginationOverCollectionFetchEnabled();

    default public ImmutableEntityUpdateQueryHandlingMode getImmutableEntityUpdateQueryHandlingMode() {
        return ImmutableEntityUpdateQueryHandlingMode.WARNING;
    }

    default public String getDefaultCatalog() {
        return null;
    }

    default public String getDefaultSchema() {
        return null;
    }

    default public boolean inClauseParameterPaddingEnabled() {
        return false;
    }

    default public boolean nativeExceptionHandling51Compliance() {
        return false;
    }

    default public int getQueryStatisticsMaxSize() {
        return 5000;
    }

    @Deprecated
    default public boolean isPostInsertIdentifierDelayableEnabled() {
        return true;
    }

    default public boolean areJPACallbacksEnabled() {
        return true;
    }

    @Deprecated
    default public boolean isEnhancementAsProxyEnabled() {
        return true;
    }

    default public boolean isCollectionsInDefaultFetchGroupEnabled() {
        return false;
    }

    public boolean isOmitJoinOfSuperclassTablesEnabled();
}

