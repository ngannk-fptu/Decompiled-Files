/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

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
import org.hibernate.boot.SessionFactoryBuilder;
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

public abstract class AbstractDelegatingSessionFactoryBuilder<T extends SessionFactoryBuilder>
implements SessionFactoryBuilder {
    private final SessionFactoryBuilder delegate;

    public AbstractDelegatingSessionFactoryBuilder(SessionFactoryBuilder delegate) {
        this.delegate = delegate;
    }

    protected abstract T getThis();

    protected SessionFactoryBuilder delegate() {
        return this.delegate;
    }

    public T applyValidatorFactory(Object validatorFactory) {
        this.delegate.applyValidatorFactory(validatorFactory);
        return this.getThis();
    }

    public T applyBeanManager(Object beanManager) {
        this.delegate.applyBeanManager(beanManager);
        return this.getThis();
    }

    public T applyName(String sessionFactoryName) {
        this.delegate.applyName(sessionFactoryName);
        return this.getThis();
    }

    public T applyNameAsJndiName(boolean isJndiName) {
        this.delegate.applyNameAsJndiName(isJndiName);
        return this.getThis();
    }

    public T applyAutoClosing(boolean enabled) {
        this.delegate.applyAutoClosing(enabled);
        return this.getThis();
    }

    public T applyAutoFlushing(boolean enabled) {
        this.delegate.applyAutoFlushing(enabled);
        return this.getThis();
    }

    public T applyStatisticsSupport(boolean enabled) {
        this.delegate.applyStatisticsSupport(enabled);
        return this.getThis();
    }

    public T applyInterceptor(Interceptor interceptor) {
        this.delegate.applyInterceptor(interceptor);
        return this.getThis();
    }

    public T applyStatementInspector(StatementInspector statementInspector) {
        this.delegate.applyStatementInspector(statementInspector);
        return this.getThis();
    }

    public T addSessionFactoryObservers(SessionFactoryObserver ... observers) {
        this.delegate.addSessionFactoryObservers(observers);
        return this.getThis();
    }

    public T applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
        this.delegate.applyCustomEntityDirtinessStrategy(strategy);
        return this.getThis();
    }

    public T addEntityNameResolver(EntityNameResolver ... entityNameResolvers) {
        this.delegate.addEntityNameResolver(entityNameResolvers);
        return this.getThis();
    }

    public T applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
        this.delegate.applyEntityNotFoundDelegate(entityNotFoundDelegate);
        return this.getThis();
    }

    public T applyIdentifierRollbackSupport(boolean enabled) {
        this.delegate.applyIdentifierRollbackSupport(enabled);
        return this.getThis();
    }

    public T applyDefaultEntityMode(EntityMode entityMode) {
        this.delegate.applyDefaultEntityMode(entityMode);
        return this.getThis();
    }

    public T applyNullabilityChecking(boolean enabled) {
        this.delegate.applyNullabilityChecking(enabled);
        return this.getThis();
    }

    public T applyLazyInitializationOutsideTransaction(boolean enabled) {
        this.delegate.applyLazyInitializationOutsideTransaction(enabled);
        return this.getThis();
    }

    public T applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
        this.delegate.applyEntityTuplizerFactory(entityTuplizerFactory);
        return this.getThis();
    }

    public T applyEntityTuplizer(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass) {
        this.delegate.applyEntityTuplizer(entityMode, tuplizerClass);
        return this.getThis();
    }

    public T applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy) {
        this.delegate.applyMultiTableBulkIdStrategy(strategy);
        return this.getThis();
    }

    public T applyTempTableDdlTransactionHandling(TempTableDdlTransactionHandling handling) {
        this.delegate.applyTempTableDdlTransactionHandling(handling);
        return this.getThis();
    }

    public T applyBatchFetchStyle(BatchFetchStyle style) {
        this.delegate.applyBatchFetchStyle(style);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder applyDelayedEntityLoaderCreations(boolean delay) {
        this.delegate.applyDelayedEntityLoaderCreations(delay);
        return this.getThis();
    }

    public T applyDefaultBatchFetchSize(int size) {
        this.delegate.applyDefaultBatchFetchSize(size);
        return this.getThis();
    }

    public T applyMaximumFetchDepth(int depth) {
        this.delegate.applyMaximumFetchDepth(depth);
        return this.getThis();
    }

    public T applyDefaultNullPrecedence(NullPrecedence nullPrecedence) {
        this.delegate.applyDefaultNullPrecedence(nullPrecedence);
        return this.getThis();
    }

    public T applyOrderingOfInserts(boolean enabled) {
        this.delegate.applyOrderingOfInserts(enabled);
        return this.getThis();
    }

    public T applyOrderingOfUpdates(boolean enabled) {
        this.delegate.applyOrderingOfUpdates(enabled);
        return this.getThis();
    }

    public T applyMultiTenancyStrategy(MultiTenancyStrategy strategy) {
        this.delegate.applyMultiTenancyStrategy(strategy);
        return this.getThis();
    }

    public T applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
        this.delegate.applyCurrentTenantIdentifierResolver(resolver);
        return this.getThis();
    }

    public T applyJtaTrackingByThread(boolean enabled) {
        this.delegate.applyJtaTrackingByThread(enabled);
        return this.getThis();
    }

    public T applyPreferUserTransactions(boolean preferUserTransactions) {
        this.delegate.applyPreferUserTransactions(preferUserTransactions);
        return this.getThis();
    }

    public T applyQuerySubstitutions(Map substitutions) {
        this.delegate.applyQuerySubstitutions(substitutions);
        return this.getThis();
    }

    public T applyStrictJpaQueryLanguageCompliance(boolean enabled) {
        this.delegate.applyStrictJpaQueryLanguageCompliance(enabled);
        return this.getThis();
    }

    public T applyNamedQueryCheckingOnStartup(boolean enabled) {
        this.delegate.applyNamedQueryCheckingOnStartup(enabled);
        return this.getThis();
    }

    public T applySecondLevelCacheSupport(boolean enabled) {
        this.delegate.applySecondLevelCacheSupport(enabled);
        return this.getThis();
    }

    public T applyQueryCacheSupport(boolean enabled) {
        this.delegate.applyQueryCacheSupport(enabled);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder applyTimestampsCacheFactory(TimestampsCacheFactory factory) {
        this.delegate.applyTimestampsCacheFactory(factory);
        return this.getThis();
    }

    public T applyCacheRegionPrefix(String prefix) {
        this.delegate.applyCacheRegionPrefix(prefix);
        return this.getThis();
    }

    public T applyMinimalPutsForCaching(boolean enabled) {
        this.delegate.applyMinimalPutsForCaching(enabled);
        return this.getThis();
    }

    public T applyStructuredCacheEntries(boolean enabled) {
        this.delegate.applyStructuredCacheEntries(enabled);
        return this.getThis();
    }

    public T applyDirectReferenceCaching(boolean enabled) {
        this.delegate.applyDirectReferenceCaching(enabled);
        return this.getThis();
    }

    public T applyAutomaticEvictionOfCollectionCaches(boolean enabled) {
        this.delegate.applyAutomaticEvictionOfCollectionCaches(enabled);
        return this.getThis();
    }

    public T applyJdbcBatchSize(int size) {
        this.delegate.applyJdbcBatchSize(size);
        return this.getThis();
    }

    public T applyJdbcBatchingForVersionedEntities(boolean enabled) {
        this.delegate.applyJdbcBatchingForVersionedEntities(enabled);
        return this.getThis();
    }

    public T applyScrollableResultsSupport(boolean enabled) {
        this.delegate.applyScrollableResultsSupport(enabled);
        return this.getThis();
    }

    public T applyResultSetsWrapping(boolean enabled) {
        this.delegate.applyResultSetsWrapping(enabled);
        return this.getThis();
    }

    public T applyGetGeneratedKeysSupport(boolean enabled) {
        this.delegate.applyGetGeneratedKeysSupport(enabled);
        return this.getThis();
    }

    public T applyJdbcFetchSize(int size) {
        this.delegate.applyJdbcFetchSize(size);
        return this.getThis();
    }

    public T applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
        this.delegate.applyConnectionReleaseMode(connectionReleaseMode);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder applyConnectionProviderDisablesAutoCommit(boolean providerDisablesAutoCommit) {
        this.delegate.applyConnectionProviderDisablesAutoCommit(providerDisablesAutoCommit);
        return this.getThis();
    }

    public T applySqlComments(boolean enabled) {
        this.delegate.applySqlComments(enabled);
        return this.getThis();
    }

    public T applySqlFunction(String registrationName, SQLFunction sqlFunction) {
        this.delegate.applySqlFunction(registrationName, sqlFunction);
        return this.getThis();
    }

    public T allowOutOfTransactionUpdateOperations(boolean allow) {
        this.delegate.allowOutOfTransactionUpdateOperations(allow);
        return this.getThis();
    }

    public T enableReleaseResourcesOnCloseEnabled(boolean enable) {
        this.delegate.enableReleaseResourcesOnCloseEnabled(enable);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder enableJpaQueryCompliance(boolean enabled) {
        this.delegate.enableJpaQueryCompliance(enabled);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder enableJpaTransactionCompliance(boolean enabled) {
        this.delegate.enableJpaTransactionCompliance(enabled);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder enableJpaListCompliance(boolean enabled) {
        this.delegate.enableJpaListCompliance(enabled);
        return this.getThis();
    }

    @Override
    public SessionFactoryBuilder enableJpaClosedCompliance(boolean enabled) {
        this.delegate.enableJpaClosedCompliance(enabled);
        return this.getThis();
    }

    public <S extends SessionFactoryBuilder> S unwrap(Class<S> type) {
        return (S)this;
    }

    public T applyStatelessInterceptor(Supplier<? extends Interceptor> statelessInterceptorSupplier) {
        this.delegate.applyStatelessInterceptor(statelessInterceptorSupplier);
        return this.getThis();
    }

    public T applyStatelessInterceptor(Class<? extends Interceptor> statelessInterceptorClass) {
        this.delegate.applyStatelessInterceptor(statelessInterceptorClass);
        return this.getThis();
    }

    public T applyConnectionHandlingMode(PhysicalConnectionHandlingMode connectionHandlingMode) {
        this.delegate.applyConnectionHandlingMode(connectionHandlingMode);
        return this.getThis();
    }

    @Override
    public SessionFactory build() {
        return this.delegate.build();
    }
}

