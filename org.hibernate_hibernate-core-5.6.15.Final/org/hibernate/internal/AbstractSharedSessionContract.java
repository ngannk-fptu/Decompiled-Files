/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.TransactionRequiredException
 *  javax.persistence.Tuple
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.CriteriaUpdate
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import javax.persistence.FlushModeType;
import javax.persistence.TransactionRequiredException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Selection;
import org.hibernate.AssertionFailure;
import org.hibernate.CacheMode;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityNameResolver;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LockMode;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.SessionEventListener;
import org.hibernate.SessionException;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.transaction.internal.TransactionImpl;
import org.hibernate.engine.transaction.spi.TransactionImplementor;
import org.hibernate.id.uuid.StandardRandomStrategy;
import org.hibernate.internal.ContextualJdbcConnectionAccess;
import org.hibernate.internal.CoordinatingEntityNameResolver;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.ExceptionConverterImpl;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.JdbcSessionContextImpl;
import org.hibernate.internal.NonContextualJdbcConnectionAccess;
import org.hibernate.internal.SessionCreationOptions;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.internal.SharedSessionCreationOptions;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.jdbc.WorkExecutorVisitable;
import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
import org.hibernate.jpa.spi.CriteriaQueryTupleTransformer;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.jpa.spi.NativeQueryTupleTransformer;
import org.hibernate.jpa.spi.TupleBuilderTransformer;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.CriteriaCompiler;
import org.hibernate.query.criteria.internal.expression.CompoundSelectionImpl;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public abstract class AbstractSharedSessionContract
implements SharedSessionContractImplementor {
    private static final EntityManagerMessageLogger log = HEMLogging.messageLogger(SessionImpl.class);
    private transient SessionFactoryImpl factory;
    private final String tenantIdentifier;
    protected transient FastSessionServices fastSessionServices;
    private UUID sessionIdentifier;
    private transient JdbcConnectionAccess jdbcConnectionAccess;
    private transient JdbcSessionContext jdbcSessionContext;
    private transient JdbcCoordinator jdbcCoordinator;
    private transient TransactionImplementor currentHibernateTransaction;
    private transient TransactionCoordinator transactionCoordinator;
    private transient CacheTransactionSynchronization cacheTransactionSync;
    private final boolean isTransactionCoordinatorShared;
    private final Interceptor interceptor;
    private final TimeZone jdbcTimeZone;
    private FlushMode flushMode;
    private boolean autoJoinTransactions;
    private final PhysicalConnectionHandlingMode connectionHandlingMode;
    private CacheMode cacheMode;
    protected boolean closed;
    protected boolean waitingForAutoClose;
    private transient SessionEventListenerManagerImpl sessionEventsManager;
    private transient EntityNameResolver entityNameResolver;
    private Integer jdbcBatchSize;
    private transient ExceptionConverter exceptionConverter;
    private CriteriaCompiler criteriaCompiler;

    public AbstractSharedSessionContract(SessionFactoryImpl factory, SessionCreationOptions options) {
        this.factory = factory;
        this.fastSessionServices = factory.getFastSessionServices();
        this.cacheTransactionSync = factory.getCache().getRegionFactory().createTransactionContext(this);
        this.flushMode = options.getInitialSessionFlushMode();
        this.tenantIdentifier = options.getTenantIdentifier();
        if (MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy()) {
            if (this.tenantIdentifier != null) {
                throw new HibernateException("SessionFactory was not configured for multi-tenancy");
            }
        } else if (this.tenantIdentifier == null) {
            throw new HibernateException("SessionFactory configured for multi-tenancy, but no tenant identifier specified");
        }
        this.interceptor = this.interpret(options.getInterceptor());
        this.jdbcTimeZone = options.getJdbcTimeZone();
        List<SessionEventListener> customSessionEventListener = options.getCustomSessionEventListener();
        this.sessionEventsManager = customSessionEventListener == null ? new SessionEventListenerManagerImpl(this.fastSessionServices.defaultSessionEventListeners.buildBaseline()) : new SessionEventListenerManagerImpl(customSessionEventListener.toArray(new SessionEventListener[0]));
        this.entityNameResolver = new CoordinatingEntityNameResolver(factory, this.interceptor);
        StatementInspector statementInspector = this.interpret(options.getStatementInspector());
        if (options instanceof SharedSessionCreationOptions && ((SharedSessionCreationOptions)options).isTransactionCoordinatorShared()) {
            if (options.getConnection() != null) {
                throw new SessionException("Cannot simultaneously share transaction context and specify connection");
            }
            this.isTransactionCoordinatorShared = true;
            SharedSessionCreationOptions sharedOptions = (SharedSessionCreationOptions)options;
            this.transactionCoordinator = sharedOptions.getTransactionCoordinator();
            this.jdbcCoordinator = sharedOptions.getJdbcCoordinator();
            this.currentHibernateTransaction = sharedOptions.getTransaction();
            if (sharedOptions.shouldAutoJoinTransactions()) {
                log.debug("Session creation specified 'autoJoinTransactions', which is invalid in conjunction with sharing JDBC connection between sessions; ignoring");
                this.autoJoinTransactions = false;
            }
            this.connectionHandlingMode = this.jdbcCoordinator.getLogicalConnection().getConnectionHandlingMode();
            if (sharedOptions.getPhysicalConnectionHandlingMode() != this.connectionHandlingMode) {
                log.debug("Session creation specified 'PhysicalConnectionHandlingMode which is invalid in conjunction with sharing JDBC connection between sessions; ignoring");
            }
            this.jdbcSessionContext = new JdbcSessionContextImpl(this, statementInspector, this.connectionHandlingMode, this.fastSessionServices);
            this.addSharedSessionTransactionObserver(this.transactionCoordinator);
        } else {
            this.isTransactionCoordinatorShared = false;
            this.autoJoinTransactions = options.shouldAutoJoinTransactions();
            this.connectionHandlingMode = options.getPhysicalConnectionHandlingMode();
            this.jdbcSessionContext = new JdbcSessionContextImpl(this, statementInspector, this.connectionHandlingMode, this.fastSessionServices);
            this.jdbcCoordinator = new JdbcCoordinatorImpl(options.getConnection(), this, this.fastSessionServices.jdbcServices);
            this.transactionCoordinator = this.fastSessionServices.transactionCoordinatorBuilder.buildTransactionCoordinator(this.jdbcCoordinator, this);
        }
    }

    @Override
    public Integer getConfiguredJdbcBatchSize() {
        Integer sessionJdbcBatchSize = this.jdbcBatchSize;
        return sessionJdbcBatchSize == null ? this.fastSessionServices.defaultJdbcBatchSize : sessionJdbcBatchSize;
    }

    protected void addSharedSessionTransactionObserver(TransactionCoordinator transactionCoordinator) {
    }

    protected void removeSharedSessionTransactionObserver(TransactionCoordinator transactionCoordinator) {
        transactionCoordinator.invalidate();
    }

    protected void prepareForAutoClose() {
        this.waitingForAutoClose = true;
        this.closed = true;
        if (!this.isTransactionCoordinatorShared) {
            this.addSharedSessionTransactionObserver(this.transactionCoordinator);
        }
    }

    @Override
    public boolean shouldAutoJoinTransaction() {
        return this.autoJoinTransactions;
    }

    private Interceptor interpret(Interceptor interceptor) {
        return interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
    }

    private StatementInspector interpret(StatementInspector statementInspector) {
        if (statementInspector == null) {
            return this.interceptor::onPrepareStatement;
        }
        return statementInspector;
    }

    @Override
    public SessionFactoryImplementor getFactory() {
        return this.factory;
    }

    @Override
    public Interceptor getInterceptor() {
        return this.interceptor;
    }

    @Override
    public JdbcCoordinator getJdbcCoordinator() {
        return this.jdbcCoordinator;
    }

    @Override
    public TransactionCoordinator getTransactionCoordinator() {
        return this.transactionCoordinator;
    }

    @Override
    public JdbcSessionContext getJdbcSessionContext() {
        return this.jdbcSessionContext;
    }

    public EntityNameResolver getEntityNameResolver() {
        return this.entityNameResolver;
    }

    @Override
    public SessionEventListenerManager getEventListenerManager() {
        return this.sessionEventsManager;
    }

    @Override
    public UUID getSessionIdentifier() {
        if (this.sessionIdentifier == null) {
            this.sessionIdentifier = StandardRandomStrategy.INSTANCE.generateUUID(null);
        }
        return this.sessionIdentifier;
    }

    @Override
    public String getTenantIdentifier() {
        return this.tenantIdentifier;
    }

    @Override
    public boolean isOpen() {
        return !this.isClosed();
    }

    @Override
    public boolean isClosed() {
        return this.closed || this.factory.isClosed();
    }

    @Override
    public void close() {
        if (this.closed && !this.waitingForAutoClose) {
            return;
        }
        try {
            this.delayedAfterCompletion();
        }
        catch (HibernateException e) {
            if (this.getFactory().getSessionFactoryOptions().isJpaBootstrap()) {
                throw this.getExceptionConverter().convert(e);
            }
            throw e;
        }
        if (this.sessionEventsManager != null) {
            this.sessionEventsManager.end();
        }
        if (this.currentHibernateTransaction != null) {
            this.currentHibernateTransaction.invalidate();
        }
        if (this.transactionCoordinator != null) {
            this.removeSharedSessionTransactionObserver(this.transactionCoordinator);
        }
        try {
            if (this.shouldCloseJdbcCoordinatorOnClose(this.isTransactionCoordinatorShared)) {
                this.jdbcCoordinator.close();
            }
        }
        finally {
            this.setClosed();
        }
    }

    protected void setClosed() {
        this.closed = true;
        this.waitingForAutoClose = false;
        this.cleanupOnClose();
    }

    protected boolean shouldCloseJdbcCoordinatorOnClose(boolean isTransactionCoordinatorShared) {
        return true;
    }

    protected void cleanupOnClose() {
    }

    @Override
    public boolean isOpenOrWaitingForAutoClose() {
        return !this.isClosed() || this.waitingForAutoClose;
    }

    @Override
    public void checkOpen(boolean markForRollbackIfClosed) {
        if (this.isClosed()) {
            if (markForRollbackIfClosed && this.transactionCoordinator.isTransactionActive()) {
                this.markForRollbackOnly();
            }
            throw new IllegalStateException("Session/EntityManager is closed");
        }
    }

    protected void checkOpenOrWaitingForAutoClose() {
        if (!this.waitingForAutoClose) {
            this.checkOpen();
        }
    }

    @Deprecated
    protected void errorIfClosed() {
        this.checkOpen();
    }

    @Override
    public void markForRollbackOnly() {
        try {
            this.accessTransaction().markRollbackOnly();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean isTransactionInProgress() {
        if (this.waitingForAutoClose) {
            return this.factory.isOpen() && this.transactionCoordinator.isTransactionActive();
        }
        return !this.isClosed() && this.transactionCoordinator.isTransactionActive();
    }

    @Override
    public void checkTransactionNeededForUpdateOperation(String exceptionMessage) {
        if (this.fastSessionServices.disallowOutOfTransactionUpdateOperations && !this.isTransactionInProgress()) {
            throw new TransactionRequiredException(exceptionMessage);
        }
    }

    @Override
    public Transaction getTransaction() throws HibernateException {
        if (!this.fastSessionServices.isJtaTransactionAccessible) {
            throw new IllegalStateException("Transaction is not accessible when using JTA with JPA-compliant transaction access enabled");
        }
        return this.accessTransaction();
    }

    @Override
    public Transaction accessTransaction() {
        if (this.currentHibernateTransaction == null) {
            this.currentHibernateTransaction = new TransactionImpl(this.getTransactionCoordinator(), this);
        }
        if (!this.isClosed() || this.waitingForAutoClose && this.factory.isOpen()) {
            this.getTransactionCoordinator().pulse();
        }
        return this.currentHibernateTransaction;
    }

    @Override
    public void startTransactionBoundary() {
        this.getCacheTransactionSynchronization().transactionJoined();
    }

    @Override
    public void beforeTransactionCompletion() {
        this.getCacheTransactionSynchronization().transactionCompleting();
    }

    @Override
    public void afterTransactionCompletion(boolean successful, boolean delayed) {
        this.getCacheTransactionSynchronization().transactionCompleted(successful);
    }

    @Override
    public CacheTransactionSynchronization getCacheTransactionSynchronization() {
        return this.cacheTransactionSync;
    }

    @Override
    public long getTransactionStartTimestamp() {
        return this.getCacheTransactionSynchronization().getCurrentTransactionStartTimestamp();
    }

    @Override
    public Transaction beginTransaction() {
        this.checkOpen();
        Transaction result = this.getTransaction();
        result.begin();
        return result;
    }

    protected void checkTransactionSynchStatus() {
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
    }

    protected void pulseTransactionCoordinator() {
        if (!this.isClosed()) {
            this.transactionCoordinator.pulse();
        }
    }

    protected void delayedAfterCompletion() {
        if (this.transactionCoordinator instanceof JtaTransactionCoordinatorImpl) {
            ((JtaTransactionCoordinatorImpl)this.transactionCoordinator).getSynchronizationCallbackCoordinator().processAnyDelayedAfterCompletion();
        }
    }

    protected TransactionImplementor getCurrentTransaction() {
        return this.currentHibernateTransaction;
    }

    @Override
    public boolean isConnected() {
        this.pulseTransactionCoordinator();
        return this.jdbcCoordinator.getLogicalConnection().isOpen();
    }

    @Override
    public JdbcConnectionAccess getJdbcConnectionAccess() {
        if (this.jdbcConnectionAccess == null) {
            this.jdbcConnectionAccess = !this.fastSessionServices.requiresMultiTenantConnectionProvider ? new NonContextualJdbcConnectionAccess(this.getEventListenerManager(), this.fastSessionServices.connectionProvider) : new ContextualJdbcConnectionAccess(this.getTenantIdentifier(), this.getEventListenerManager(), this.fastSessionServices.multiTenantConnectionProvider);
        }
        return this.jdbcConnectionAccess;
    }

    @Override
    public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
        return new EntityKey(id, persister);
    }

    @Override
    public boolean useStreamForLobBinding() {
        return this.fastSessionServices.useStreamForLobBinding;
    }

    @Override
    public LobCreator getLobCreator() {
        return Hibernate.getLobCreator(this);
    }

    @Override
    public <T> T execute(LobCreationContext.Callback<T> callback) {
        return (T)this.getJdbcCoordinator().coordinateWork((workExecutor, connection) -> {
            try {
                return callback.executeOnConnection(connection);
            }
            catch (SQLException e) {
                throw this.getExceptionConverter().convert(e, "Error creating contextual LOB : " + e.getMessage());
            }
        });
    }

    @Override
    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        return this.fastSessionServices.remapSqlTypeDescriptor(sqlTypeDescriptor);
    }

    @Override
    public TimeZone getJdbcTimeZone() {
        return this.jdbcTimeZone;
    }

    @Override
    public JdbcServices getJdbcServices() {
        return this.getFactory().getJdbcServices();
    }

    @Override
    public void setFlushMode(FlushMode flushMode) {
        this.setHibernateFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        this.checkOpen();
        return FlushModeTypeHelper.getFlushModeType(this.flushMode);
    }

    @Override
    public void setHibernateFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    @Override
    public FlushMode getHibernateFlushMode() {
        return this.flushMode;
    }

    @Override
    public CacheMode getCacheMode() {
        return this.cacheMode;
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    protected HQLQueryPlan getQueryPlan(String query, boolean shallow) throws HibernateException {
        return this.getFactory().getQueryPlanCache().getHQLQueryPlan(query, shallow, this.getLoadQueryInfluencers().getEnabledFilters());
    }

    protected NativeSQLQueryPlan getNativeQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
        return this.getFactory().getQueryPlanCache().getNativeSQLQueryPlan(spec);
    }

    @Override
    public QueryImplementor getNamedQuery(String name) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        NamedQueryDefinition queryDefinition = this.factory.getNamedQueryRepository().getNamedQueryDefinition(name);
        if (queryDefinition != null) {
            return this.createQuery(queryDefinition);
        }
        NamedSQLQueryDefinition nativeQueryDefinition = this.factory.getNamedQueryRepository().getNamedSQLQueryDefinition(name);
        if (nativeQueryDefinition != null) {
            return this.createNativeQuery(nativeQueryDefinition, true);
        }
        throw this.getExceptionConverter().convert(new IllegalArgumentException("No query defined for that name [" + name + "]"));
    }

    protected QueryImpl createQuery(NamedQueryDefinition queryDefinition) {
        String queryString = queryDefinition.getQueryString();
        QueryImpl query = new QueryImpl(this, this.getQueryPlan(queryString, false), queryString);
        this.applyQuerySettingsAndHints(query);
        query.setHibernateFlushMode(queryDefinition.getFlushMode());
        query.setComment(queryDefinition.getComment() != null ? queryDefinition.getComment() : queryDefinition.getName());
        if (queryDefinition.getLockOptions() != null) {
            query.setLockOptions(queryDefinition.getLockOptions());
        }
        this.initQueryFromNamedDefinition(query, queryDefinition);
        return query;
    }

    private NativeQueryImplementor createNativeQuery(NamedSQLQueryDefinition queryDefinition, boolean isOrdinalParameterZeroBased) {
        ParameterMetadata parameterMetadata = this.factory.getQueryPlanCache().getSQLParameterMetadata(queryDefinition.getQueryString(), isOrdinalParameterZeroBased);
        return this.getNativeQueryImplementor(queryDefinition, parameterMetadata);
    }

    private NativeQueryImplementor getNativeQueryImplementor(NamedSQLQueryDefinition queryDefinition, ParameterMetadata parameterMetadata) {
        NativeQueryImpl query = new NativeQueryImpl(queryDefinition, this, parameterMetadata);
        this.applyQuerySettingsAndHints(query);
        query.setComment(queryDefinition.getComment() != null ? queryDefinition.getComment() : queryDefinition.getName());
        this.initQueryFromNamedDefinition(query, queryDefinition);
        return query;
    }

    protected void initQueryFromNamedDefinition(Query query, NamedQueryDefinition nqd) {
        query.setCacheable(nqd.isCacheable());
        query.setCacheRegion(nqd.getCacheRegion());
        query.setReadOnly(nqd.isReadOnly());
        if (nqd.getTimeout() != null) {
            query.setTimeout(nqd.getTimeout());
        }
        if (nqd.getFetchSize() != null) {
            query.setFetchSize(nqd.getFetchSize());
        }
        if (nqd.getCacheMode() != null) {
            query.setCacheMode(nqd.getCacheMode());
        }
        if (nqd.getComment() != null) {
            query.setComment(nqd.getComment());
        }
        if (nqd.getFirstResult() != null) {
            query.setFirstResult(nqd.getFirstResult());
        }
        if (nqd.getMaxResults() != null) {
            query.setMaxResults(nqd.getMaxResults());
        }
        if (nqd.getFlushMode() != null) {
            query.setHibernateFlushMode(nqd.getFlushMode());
        }
        if (nqd.getPassDistinctThrough() != null) {
            query.setHint("hibernate.query.passDistinctThrough", nqd.getPassDistinctThrough());
        }
    }

    @Override
    public QueryImpl createQuery(String queryString) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        try {
            QueryImpl query = new QueryImpl(this, this.getQueryPlan(queryString, false), queryString);
            this.applyQuerySettingsAndHints(query);
            query.setComment(queryString);
            return query;
        }
        catch (RuntimeException e) {
            this.markForRollbackOnly();
            throw this.getExceptionConverter().convert(e);
        }
    }

    protected CriteriaCompiler criteriaCompiler() {
        if (this.criteriaCompiler == null) {
            this.criteriaCompiler = new CriteriaCompiler(this);
        }
        return this.criteriaCompiler;
    }

    public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        this.checkOpen();
        try {
            return this.criteriaCompiler().compile((CompilableCriteria)criteriaQuery);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    @Override
    public QueryImplementor createQuery(CriteriaUpdate criteriaUpdate) {
        this.checkOpen();
        try {
            return this.criteriaCompiler().compile((CompilableCriteria)criteriaUpdate);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    @Override
    public QueryImplementor createQuery(CriteriaDelete criteriaDelete) {
        this.checkOpen();
        try {
            return this.criteriaCompiler().compile((CompilableCriteria)criteriaDelete);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    @Override
    public <T> QueryImplementor<T> createQuery(String jpaqlString, Class<T> resultClass, Selection selection, HibernateEntityManagerImplementor.QueryOptions queryOptions) {
        try {
            List<Selection<?>> tupleElements;
            QueryImpl query = this.createQuery(jpaqlString);
            if (queryOptions.getValueHandlers() == null && queryOptions.getResultMetadataValidator() != null) {
                queryOptions.getResultMetadataValidator().validate(query.getReturnTypes());
            }
            List<Selection<?>> list = tupleElements = Tuple.class.equals(resultClass) ? ((CompoundSelectionImpl)selection).getCompoundSelectionItems() : null;
            if (queryOptions.getValueHandlers() != null || tupleElements != null) {
                query.setResultTransformer(new CriteriaQueryTupleTransformer(queryOptions.getValueHandlers(), tupleElements));
            }
            return query;
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    protected void applyQuerySettingsAndHints(Query query) {
    }

    public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultClass) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        try {
            QueryImpl query = this.createQuery(queryString);
            this.resultClassChecking(resultClass, query);
            return query;
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    protected void resultClassChecking(Class resultClass, QueryImpl hqlQuery) {
        HQLQueryPlan queryPlan = hqlQuery.getQueryPlan();
        if (queryPlan.getTranslators()[0].isManipulationStatement()) {
            throw new IllegalArgumentException("Update/delete queries cannot be typed");
        }
        if (!Object[].class.equals((Object)resultClass)) {
            if (Tuple.class.equals((Object)resultClass)) {
                TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer(hqlQuery);
                hqlQuery.setResultTransformer(tupleTransformer);
            } else {
                Class dynamicInstantiationClass = queryPlan.getDynamicInstantiationResultType();
                if (dynamicInstantiationClass != null) {
                    if (!resultClass.isAssignableFrom(dynamicInstantiationClass)) {
                        throw new IllegalArgumentException("Mismatch in requested result type [" + resultClass.getName() + "] and actual result type [" + dynamicInstantiationClass.getName() + "]");
                    }
                } else if (queryPlan.getTranslators()[0].getReturnTypes().length == 1) {
                    Type queryResultType = queryPlan.getTranslators()[0].getReturnTypes()[0];
                    if (!resultClass.isAssignableFrom(queryResultType.getReturnedClass())) {
                        throw this.buildIncompatibleException(resultClass, queryResultType.getReturnedClass());
                    }
                } else {
                    throw new IllegalArgumentException("Cannot create TypedQuery for query with more than one return using requested result type [" + resultClass.getName() + "]");
                }
            }
        }
    }

    @Override
    public QueryImplementor createNamedQuery(String name) {
        return this.buildQueryFromName(name, null);
    }

    protected <T> QueryImplementor<T> buildQueryFromName(String name, Class<T> resultType) {
        this.checkOpen();
        try {
            this.pulseTransactionCoordinator();
            this.delayedAfterCompletion();
            NamedQueryDefinition namedQueryDefinition = this.getFactory().getNamedQueryRepository().getNamedQueryDefinition(name);
            if (namedQueryDefinition != null) {
                return this.createQuery(namedQueryDefinition, resultType);
            }
            NamedSQLQueryDefinition nativeQueryDefinition = this.getFactory().getNamedQueryRepository().getNamedSQLQueryDefinition(name);
            if (nativeQueryDefinition != null) {
                return this.createNativeQuery(nativeQueryDefinition, resultType);
            }
            throw this.getExceptionConverter().convert(new IllegalArgumentException("No query defined for that name [" + name + "]"));
        }
        catch (RuntimeException e) {
            throw !(e instanceof IllegalArgumentException) ? new IllegalArgumentException(e) : e;
        }
    }

    protected <T> QueryImplementor<T> createQuery(NamedQueryDefinition namedQueryDefinition, Class<T> resultType) {
        QueryImpl query = this.createQuery(namedQueryDefinition);
        if (resultType != null) {
            this.resultClassChecking(resultType, query);
        }
        return query;
    }

    protected <T> NativeQueryImplementor createNativeQuery(NamedSQLQueryDefinition queryDefinition, Class<T> resultType) {
        if (resultType != null && !Tuple.class.equals(resultType)) {
            this.resultClassChecking(resultType, queryDefinition);
        }
        NativeQueryImpl query = new NativeQueryImpl(queryDefinition, this, this.factory.getQueryPlanCache().getSQLParameterMetadata(queryDefinition.getQueryString(), false));
        if (Tuple.class.equals(resultType)) {
            query.setResultTransformer(new NativeQueryTupleTransformer());
        }
        this.applyQuerySettingsAndHints(query);
        query.setHibernateFlushMode(queryDefinition.getFlushMode());
        query.setComment(queryDefinition.getComment() != null ? queryDefinition.getComment() : queryDefinition.getName());
        if (queryDefinition.getLockOptions() != null) {
            query.setLockOptions(queryDefinition.getLockOptions());
        }
        this.initQueryFromNamedDefinition(query, queryDefinition);
        return query;
    }

    protected void resultClassChecking(Class resultType, NamedSQLQueryDefinition namedQueryDefinition) {
        NativeSQLQueryReturn[] queryReturns;
        if (namedQueryDefinition.getQueryReturns() != null) {
            queryReturns = namedQueryDefinition.getQueryReturns();
        } else if (namedQueryDefinition.getResultSetRef() != null) {
            ResultSetMappingDefinition rsMapping = this.getFactory().getNamedQueryRepository().getResultSetMappingDefinition(namedQueryDefinition.getResultSetRef());
            queryReturns = rsMapping.getQueryReturns();
        } else {
            throw new AssertionFailure("Unsupported named query model. Please report the bug in Hibernate EntityManager");
        }
        if (queryReturns.length > 1) {
            throw new IllegalArgumentException("Cannot create TypedQuery for query with more than one return");
        }
        if (queryReturns.length == 0) {
            throw new IllegalArgumentException("Named query exists but its result type is not compatible");
        }
        NativeSQLQueryReturn nativeSQLQueryReturn = queryReturns[0];
        if (nativeSQLQueryReturn instanceof NativeSQLQueryRootReturn) {
            Class actualReturnedClass;
            String entityClassName = ((NativeSQLQueryRootReturn)nativeSQLQueryReturn).getReturnEntityName();
            try {
                actualReturnedClass = this.fastSessionServices.classLoaderService.classForName(entityClassName);
            }
            catch (ClassLoadingException e) {
                throw new AssertionFailure("Unable to load class [" + entityClassName + "] declared on named native query [" + namedQueryDefinition.getName() + "]");
            }
            if (!resultType.isAssignableFrom(actualReturnedClass)) {
                throw this.buildIncompatibleException(resultType, actualReturnedClass);
            }
        } else if (nativeSQLQueryReturn instanceof NativeSQLQueryConstructorReturn) {
            NativeSQLQueryConstructorReturn ctorRtn = (NativeSQLQueryConstructorReturn)nativeSQLQueryReturn;
            if (!resultType.isAssignableFrom(ctorRtn.getTargetClass())) {
                throw this.buildIncompatibleException(resultType, ctorRtn.getTargetClass());
            }
        } else {
            log.debugf("Skiping unhandled NativeSQLQueryReturn type : " + nativeSQLQueryReturn, new Object[0]);
        }
    }

    private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
        String actualResultClassName;
        String resultClassName = resultClass.getName();
        if (resultClassName.equals(actualResultClassName = actualResultClass.getName())) {
            return new IllegalArgumentException("Type specified for TypedQuery [" + resultClassName + "] is incompatible with the query return type of the same name. Both classes have the same name but are different as they have been loaded respectively by Classloaders " + resultClass.getClassLoader().toString() + ", " + actualResultClass.getClassLoader().toString() + ". This suggests a classloader bug in the Runtime executing Hibernate ORM, or in the integration code.");
        }
        return new IllegalArgumentException("Type specified for TypedQuery [" + resultClassName + "] is incompatible with query return type [" + actualResultClass + "]");
    }

    @Override
    public <R> QueryImplementor<R> createNamedQuery(String name, Class<R> resultClass) {
        return this.buildQueryFromName(name, resultClass);
    }

    @Override
    public NativeQueryImplementor createNativeQuery(String sqlString) {
        return this.getNativeQueryImplementor(sqlString, false);
    }

    @Override
    public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        try {
            NativeQueryImplementor query = this.createNativeQuery(sqlString);
            this.handleNativeQueryResult(query, resultClass);
            return query;
        }
        catch (RuntimeException he) {
            throw this.getExceptionConverter().convert(he);
        }
    }

    private void handleNativeQueryResult(NativeQueryImplementor query, Class resultClass) {
        if (Tuple.class.equals((Object)resultClass)) {
            query.setResultTransformer(new NativeQueryTupleTransformer());
        } else {
            query.addEntity("alias1", resultClass.getName(), LockMode.READ);
        }
    }

    @Override
    public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        try {
            NativeQueryImplementor query = this.createNativeQuery(sqlString);
            query.setResultSetMapping(resultSetMapping);
            return query;
        }
        catch (RuntimeException he) {
            throw this.getExceptionConverter().convert(he);
        }
    }

    @Override
    public NativeQueryImplementor getNamedNativeQuery(String name) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        NamedSQLQueryDefinition nativeQueryDefinition = this.factory.getNamedQueryRepository().getNamedSQLQueryDefinition(name);
        if (nativeQueryDefinition != null) {
            return this.createNativeQuery(nativeQueryDefinition, true);
        }
        throw this.getExceptionConverter().convert(new IllegalArgumentException("No query defined for that name [" + name + "]"));
    }

    @Override
    public NativeQueryImplementor createSQLQuery(String queryString) {
        return this.getNativeQueryImplementor(queryString, true);
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        WorkExecutorVisitable<Void> realWork = (workExecutor, connection) -> {
            workExecutor.executeWork(work, connection);
            return null;
        };
        this.doWork(realWork);
    }

    @Override
    public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
        WorkExecutorVisitable<Object> realWork = (workExecutor, connection) -> workExecutor.executeReturningWork(work, connection);
        return (T)this.doWork(realWork);
    }

    private <T> T doWork(WorkExecutorVisitable<T> work) throws HibernateException {
        return this.getJdbcCoordinator().coordinateWork(work);
    }

    protected NativeQueryImplementor getNativeQueryImplementor(String queryString, boolean isOrdinalParameterZeroBased) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.delayedAfterCompletion();
        try {
            NativeQueryImpl query = new NativeQueryImpl(queryString, false, this, this.getFactory().getQueryPlanCache().getSQLParameterMetadata(queryString, isOrdinalParameterZeroBased));
            query.setComment("dynamic native SQL query");
            this.applyQuerySettingsAndHints(query);
            return query;
        }
        catch (RuntimeException he) {
            throw this.getExceptionConverter().convert(he);
        }
    }

    @Override
    public NativeQueryImplementor getNamedSQLQuery(String name) {
        return this.getNamedNativeQuery(name);
    }

    @Override
    public ProcedureCall getNamedProcedureCall(String name) {
        this.checkOpen();
        ProcedureCallMemento memento = this.factory.getNamedQueryRepository().getNamedProcedureCallMemento(name);
        if (memento == null) {
            throw new IllegalArgumentException("Could not find named stored procedure call with that registration name : " + name);
        }
        ProcedureCall procedureCall = memento.makeProcedureCall(this);
        return procedureCall;
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName) {
        this.checkOpen();
        ProcedureCallImpl procedureCall = new ProcedureCallImpl((SharedSessionContractImplementor)this, procedureName);
        return procedureCall;
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, Class ... resultClasses) {
        this.checkOpen();
        ProcedureCallImpl procedureCall = new ProcedureCallImpl((SharedSessionContractImplementor)this, procedureName, resultClasses);
        return procedureCall;
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, String ... resultSetMappings) {
        this.checkOpen();
        ProcedureCallImpl procedureCall = new ProcedureCallImpl((SharedSessionContractImplementor)this, procedureName, resultSetMappings);
        return procedureCall;
    }

    protected abstract Object load(String var1, Serializable var2);

    @Override
    public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
        return this.listCustomQuery(this.getNativeQueryPlan(spec).getCustomQuery(), queryParameters);
    }

    @Override
    public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
        return this.scrollCustomQuery(this.getNativeQueryPlan(spec).getCustomQuery(), queryParameters);
    }

    @Override
    public ExceptionConverter getExceptionConverter() {
        if (this.exceptionConverter == null) {
            this.exceptionConverter = new ExceptionConverterImpl(this);
        }
        return this.exceptionConverter;
    }

    @Override
    public Integer getJdbcBatchSize() {
        return this.jdbcBatchSize;
    }

    @Override
    public void setJdbcBatchSize(Integer jdbcBatchSize) {
        this.jdbcBatchSize = jdbcBatchSize;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Serializing " + this.getClass().getSimpleName() + " [");
        }
        if (!this.jdbcCoordinator.isReadyForSerialization()) {
            throw new IllegalStateException("Cannot serialize " + this.getClass().getSimpleName() + " [" + this.getSessionIdentifier() + "] while connected");
        }
        if (this.isTransactionCoordinatorShared) {
            throw new IllegalStateException("Cannot serialize " + this.getClass().getSimpleName() + " [" + this.getSessionIdentifier() + "] as it has a shared TransactionCoordinator");
        }
        oos.defaultWriteObject();
        this.factory.serialize(oos);
        oos.writeObject(this.jdbcSessionContext.getStatementInspector());
        this.jdbcCoordinator.serialize(oos);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
        if (log.isTraceEnabled()) {
            log.trace("Deserializing " + this.getClass().getSimpleName());
        }
        ois.defaultReadObject();
        this.factory = SessionFactoryImpl.deserialize(ois);
        this.fastSessionServices = this.factory.getFastSessionServices();
        this.sessionEventsManager = new SessionEventListenerManagerImpl(this.fastSessionServices.defaultSessionEventListeners.buildBaseline());
        this.jdbcSessionContext = new JdbcSessionContextImpl(this, (StatementInspector)ois.readObject(), this.connectionHandlingMode, this.fastSessionServices);
        this.jdbcCoordinator = JdbcCoordinatorImpl.deserialize(ois, this);
        this.cacheTransactionSync = this.factory.getCache().getRegionFactory().createTransactionContext(this);
        this.transactionCoordinator = this.factory.getServiceRegistry().getService(TransactionCoordinatorBuilder.class).buildTransactionCoordinator(this.jdbcCoordinator, this);
        this.entityNameResolver = new CoordinatingEntityNameResolver(this.factory, this.interceptor);
    }
}

