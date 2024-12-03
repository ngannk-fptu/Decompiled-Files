/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import java.util.Map;
import java.util.function.Supplier;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.Interceptor;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.NullPrecedence;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.cache.spi.TimestampsCacheFactory;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.tuple.entity.EntityTuplizerFactory;

public interface SessionFactoryBuilder {
    public SessionFactoryBuilder applyValidatorFactory(Object var1);

    public SessionFactoryBuilder applyBeanManager(Object var1);

    public SessionFactoryBuilder applyName(String var1);

    public SessionFactoryBuilder applyNameAsJndiName(boolean var1);

    public SessionFactoryBuilder applyAutoClosing(boolean var1);

    public SessionFactoryBuilder applyAutoFlushing(boolean var1);

    public SessionFactoryBuilder applyStatisticsSupport(boolean var1);

    public SessionFactoryBuilder applyInterceptor(Interceptor var1);

    public SessionFactoryBuilder applyStatelessInterceptor(Class<? extends Interceptor> var1);

    public SessionFactoryBuilder applyStatelessInterceptor(Supplier<? extends Interceptor> var1);

    public SessionFactoryBuilder applyStatementInspector(StatementInspector var1);

    public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver ... var1);

    public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy var1);

    public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver ... var1);

    public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate var1);

    public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean var1);

    @Deprecated
    public SessionFactoryBuilder applyDefaultEntityMode(EntityMode var1);

    public SessionFactoryBuilder applyNullabilityChecking(boolean var1);

    public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean var1);

    public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory var1);

    public SessionFactoryBuilder applyEntityTuplizer(EntityMode var1, Class<? extends EntityTuplizer> var2);

    public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy var1);

    public SessionFactoryBuilder applyTempTableDdlTransactionHandling(TempTableDdlTransactionHandling var1);

    public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle var1);

    public SessionFactoryBuilder applyDelayedEntityLoaderCreations(boolean var1);

    public SessionFactoryBuilder applyDefaultBatchFetchSize(int var1);

    public SessionFactoryBuilder applyMaximumFetchDepth(int var1);

    public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence var1);

    public SessionFactoryBuilder applyOrderingOfInserts(boolean var1);

    public SessionFactoryBuilder applyOrderingOfUpdates(boolean var1);

    public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy var1);

    public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver var1);

    public SessionFactoryBuilder applyJtaTrackingByThread(boolean var1);

    public SessionFactoryBuilder applyPreferUserTransactions(boolean var1);

    @Deprecated
    public SessionFactoryBuilder applyQuerySubstitutions(Map var1);

    @Deprecated
    public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean var1);

    public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean var1);

    public SessionFactoryBuilder applySecondLevelCacheSupport(boolean var1);

    public SessionFactoryBuilder applyQueryCacheSupport(boolean var1);

    public SessionFactoryBuilder applyTimestampsCacheFactory(TimestampsCacheFactory var1);

    public SessionFactoryBuilder applyCacheRegionPrefix(String var1);

    public SessionFactoryBuilder applyMinimalPutsForCaching(boolean var1);

    public SessionFactoryBuilder applyStructuredCacheEntries(boolean var1);

    public SessionFactoryBuilder applyDirectReferenceCaching(boolean var1);

    public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean var1);

    public SessionFactoryBuilder applyJdbcBatchSize(int var1);

    public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean var1);

    public SessionFactoryBuilder applyScrollableResultsSupport(boolean var1);

    public SessionFactoryBuilder applyResultSetsWrapping(boolean var1);

    public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean var1);

    public SessionFactoryBuilder applyJdbcFetchSize(int var1);

    public SessionFactoryBuilder applyConnectionHandlingMode(PhysicalConnectionHandlingMode var1);

    @Deprecated
    public SessionFactoryBuilder applyConnectionReleaseMode(ConnectionReleaseMode var1);

    default public SessionFactoryBuilder applyConnectionProviderDisablesAutoCommit(boolean providerDisablesAutoCommit) {
        return this;
    }

    public SessionFactoryBuilder applySqlComments(boolean var1);

    public SessionFactoryBuilder applySqlFunction(String var1, SQLFunction var2);

    public SessionFactoryBuilder allowOutOfTransactionUpdateOperations(boolean var1);

    public SessionFactoryBuilder enableReleaseResourcesOnCloseEnabled(boolean var1);

    public SessionFactoryBuilder enableJpaQueryCompliance(boolean var1);

    public SessionFactoryBuilder enableJpaTransactionCompliance(boolean var1);

    public SessionFactoryBuilder enableJpaListCompliance(boolean var1);

    public SessionFactoryBuilder enableJpaClosedCompliance(boolean var1);

    public <T extends SessionFactoryBuilder> T unwrap(Class<T> var1);

    public SessionFactory build();
}

