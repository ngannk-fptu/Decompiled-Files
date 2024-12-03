/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.StoredProcedureQuery
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.CriteriaUpdate
 *  javax.persistence.criteria.Selection
 *  javax.persistence.metamodel.Metamodel
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Metamodel;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Interceptor;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.ScrollMode;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class SessionDelegatorBaseImpl
implements SessionImplementor {
    protected final SessionImplementor delegate;

    @Deprecated
    public SessionDelegatorBaseImpl(SessionImplementor delegate, Session session) {
        if (delegate == null) {
            throw new IllegalArgumentException("Unable to create a SessionDelegatorBaseImpl from a null delegate object");
        }
        if (session == null) {
            throw new IllegalArgumentException("Unable to create a SessionDelegatorBaseImpl from a null Session");
        }
        if (delegate != session) {
            throw new IllegalArgumentException("Unable to create a SessionDelegatorBaseImpl from different Session/SessionImplementor references");
        }
        this.delegate = delegate;
    }

    public SessionDelegatorBaseImpl(SessionImplementor delegate) {
        this(delegate, delegate);
    }

    protected SessionImplementor delegate() {
        return this.delegate;
    }

    @Override
    public <T> T execute(LobCreationContext.Callback<T> callback) {
        return (T)this.delegate.execute(callback);
    }

    @Override
    public String getTenantIdentifier() {
        return this.delegate.getTenantIdentifier();
    }

    @Override
    public UUID getSessionIdentifier() {
        return this.delegate.getSessionIdentifier();
    }

    @Override
    public JdbcConnectionAccess getJdbcConnectionAccess() {
        return this.delegate.getJdbcConnectionAccess();
    }

    @Override
    public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
        return this.delegate.generateEntityKey(id, persister);
    }

    @Override
    public Interceptor getInterceptor() {
        return this.delegate.getInterceptor();
    }

    @Override
    public void setAutoClear(boolean enabled) {
        this.delegate.setAutoClear(enabled);
    }

    @Override
    public boolean isTransactionInProgress() {
        return this.delegate.isTransactionInProgress();
    }

    @Override
    public void checkTransactionNeededForUpdateOperation(String exceptionMessage) {
        this.delegate.checkTransactionNeededForUpdateOperation(exceptionMessage);
    }

    @Override
    public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
        return this.delegate.getLockRequest(lockModeType, properties);
    }

    @Override
    public LockOptions buildLockOptions(LockModeType lockModeType, Map<String, Object> properties) {
        return this.delegate.buildLockOptions(lockModeType, properties);
    }

    @Override
    public <T> QueryImplementor<T> createQuery(String jpaqlString, Class<T> resultClass, Selection selection, HibernateEntityManagerImplementor.QueryOptions queryOptions) {
        return this.delegate.createQuery(jpaqlString, (Class)resultClass, selection, queryOptions);
    }

    @Override
    public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
        this.delegate.initializeCollection(collection, writing);
    }

    @Override
    public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
        return this.delegate.internalLoad(entityName, id, eager, nullable);
    }

    @Override
    public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
        return this.delegate.immediateLoad(entityName, id);
    }

    @Override
    public long getTimestamp() {
        return this.delegate.getTimestamp();
    }

    @Override
    public SessionFactoryImplementor getFactory() {
        return this.delegate.getFactory();
    }

    @Override
    public List list(String query, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.list(query, queryParameters);
    }

    @Override
    public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.iterate(query, queryParameters);
    }

    @Override
    public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.scroll(query, queryParameters);
    }

    @Override
    public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
        return this.delegate.scroll(criteria, scrollMode);
    }

    @Override
    public List list(Criteria criteria) {
        return this.delegate.list(criteria);
    }

    @Override
    public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.listFilter(collection, filter, queryParameters);
    }

    @Override
    public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.iterateFilter(collection, filter, queryParameters);
    }

    @Override
    public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
        return this.delegate.getEntityPersister(entityName, object);
    }

    @Override
    public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
        return this.delegate.getEntityUsingInterceptor(key);
    }

    @Override
    public Serializable getContextEntityIdentifier(Object object) {
        return this.delegate.getContextEntityIdentifier(object);
    }

    @Override
    public String bestGuessEntityName(Object object) {
        return this.delegate.bestGuessEntityName(object);
    }

    @Override
    public String guessEntityName(Object entity) throws HibernateException {
        return this.delegate.guessEntityName(entity);
    }

    @Override
    public Object instantiate(String entityName, Serializable id) throws HibernateException {
        return this.delegate.instantiate(entityName, id);
    }

    @Override
    public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.listCustomQuery(customQuery, queryParameters);
    }

    @Override
    public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.scrollCustomQuery(customQuery, queryParameters);
    }

    @Override
    public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.list(spec, queryParameters);
    }

    @Override
    public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.scroll(spec, queryParameters);
    }

    @Override
    public int getDontFlushFromFind() {
        return this.delegate.getDontFlushFromFind();
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return this.delegate.getPersistenceContext();
    }

    @Override
    public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.executeUpdate(query, queryParameters);
    }

    @Override
    public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
        return this.delegate.executeNativeUpdate(specification, queryParameters);
    }

    @Override
    public CacheMode getCacheMode() {
        return this.delegate.getCacheMode();
    }

    @Override
    public void setCacheMode(CacheMode cm) {
        this.delegate.setCacheMode(cm);
    }

    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public boolean isConnected() {
        return this.delegate.isConnected();
    }

    @Override
    public void checkOpen(boolean markForRollbackIfClosed) {
        this.delegate.checkOpen(markForRollbackIfClosed);
    }

    @Override
    public void markForRollbackOnly() {
        this.delegate.markForRollbackOnly();
    }

    @Override
    public long getTransactionStartTimestamp() {
        return this.delegate.getTransactionStartTimestamp();
    }

    @Override
    public FlushModeType getFlushMode() {
        return this.delegate.getFlushMode();
    }

    public void setFlushMode(FlushModeType flushModeType) {
        this.delegate.setFlushMode(flushModeType);
    }

    @Override
    public void setHibernateFlushMode(FlushMode flushMode) {
        this.delegate.setHibernateFlushMode(flushMode);
    }

    @Override
    public FlushMode getHibernateFlushMode() {
        return this.delegate.getHibernateFlushMode();
    }

    @Override
    public void setFlushMode(FlushMode fm) {
        this.delegate.setHibernateFlushMode(fm);
    }

    public void lock(Object entity, LockModeType lockMode) {
        this.delegate.lock(entity, lockMode);
    }

    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        this.delegate.lock(entity, lockMode, properties);
    }

    @Override
    public Connection connection() {
        return this.delegate.connection();
    }

    @Override
    public void flush() {
        this.delegate.flush();
    }

    @Override
    public boolean isEventSource() {
        return this.delegate.isEventSource();
    }

    @Override
    public void afterScrollOperation() {
        this.delegate.afterScrollOperation();
    }

    @Override
    public TransactionCoordinator getTransactionCoordinator() {
        return this.delegate.getTransactionCoordinator();
    }

    @Override
    public JdbcCoordinator getJdbcCoordinator() {
        return this.delegate.getJdbcCoordinator();
    }

    @Override
    public JdbcServices getJdbcServices() {
        return this.delegate.getJdbcServices();
    }

    @Override
    public JdbcSessionContext getJdbcSessionContext() {
        return this.delegate.getJdbcSessionContext();
    }

    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Override
    public void checkOpen() {
        this.delegate.checkOpen();
    }

    @Override
    public boolean isOpenOrWaitingForAutoClose() {
        return this.delegate.isOpenOrWaitingForAutoClose();
    }

    @Override
    public boolean shouldAutoClose() {
        return this.delegate.shouldAutoClose();
    }

    @Override
    public boolean isAutoCloseSessionEnabled() {
        return this.delegate.isAutoCloseSessionEnabled();
    }

    @Override
    public boolean isQueryParametersValidationEnabled() {
        return this.delegate.isQueryParametersValidationEnabled();
    }

    @Override
    public boolean shouldAutoJoinTransaction() {
        return this.delegate.shouldAutoJoinTransaction();
    }

    @Override
    public LoadQueryInfluencers getLoadQueryInfluencers() {
        return this.delegate.getLoadQueryInfluencers();
    }

    @Override
    public ExceptionConverter getExceptionConverter() {
        return this.delegate.getExceptionConverter();
    }

    @Override
    public PersistenceContext getPersistenceContextInternal() {
        return this.delegate.getPersistenceContextInternal();
    }

    @Override
    public SessionEventListenerManager getEventListenerManager() {
        return this.delegate.getEventListenerManager();
    }

    @Override
    public Transaction accessTransaction() {
        return this.delegate.accessTransaction();
    }

    @Override
    public Transaction beginTransaction() {
        return this.delegate.beginTransaction();
    }

    @Override
    public Transaction getTransaction() {
        return this.delegate.getTransaction();
    }

    @Override
    public void startTransactionBoundary() {
        this.delegate.startTransactionBoundary();
    }

    @Override
    public CacheTransactionSynchronization getCacheTransactionSynchronization() {
        return this.delegate.getCacheTransactionSynchronization();
    }

    @Override
    public void afterTransactionBegin() {
        this.delegate.afterTransactionBegin();
    }

    @Override
    public void beforeTransactionCompletion() {
        this.delegate.beforeTransactionCompletion();
    }

    @Override
    public void afterTransactionCompletion(boolean successful, boolean delayed) {
        this.delegate.afterTransactionCompletion(successful, delayed);
    }

    @Override
    public void flushBeforeTransactionCompletion() {
        this.delegate.flushBeforeTransactionCompletion();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.delegate.getFactory();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return this.delegate.getCriteriaBuilder();
    }

    public Metamodel getMetamodel() {
        return this.delegate.getMetamodel();
    }

    @Override
    public <T> RootGraphImplementor<T> createEntityGraph(Class<T> rootType) {
        return this.delegate.createEntityGraph((Class)rootType);
    }

    @Override
    public RootGraphImplementor<?> createEntityGraph(String graphName) {
        return this.delegate.createEntityGraph(graphName);
    }

    @Override
    public RootGraphImplementor<?> getEntityGraph(String graphName) {
        return this.delegate.getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return this.delegate.getEntityGraphs(entityClass);
    }

    @Override
    public QueryImplementor getNamedQuery(String name) {
        return this.delegate.getNamedQuery(name);
    }

    @Override
    public NativeQueryImplementor getNamedSQLQuery(String name) {
        return this.delegate.getNamedSQLQuery(name);
    }

    @Override
    public NativeQueryImplementor getNamedNativeQuery(String name) {
        return this.delegate.getNamedNativeQuery(name);
    }

    @Override
    public QueryImplementor createQuery(String queryString) {
        return this.delegate.createQuery(queryString);
    }

    @Override
    public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultType) {
        return this.delegate.createQuery(queryString, (Class)resultType);
    }

    @Override
    public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return this.delegate.createQuery((CriteriaQuery)criteriaQuery);
    }

    @Override
    public QueryImplementor createQuery(CriteriaUpdate updateQuery) {
        return this.delegate.createQuery(updateQuery);
    }

    @Override
    public QueryImplementor createQuery(CriteriaDelete deleteQuery) {
        return this.delegate.createQuery(deleteQuery);
    }

    @Override
    public QueryImplementor createNamedQuery(String name) {
        return this.delegate.createNamedQuery(name);
    }

    @Override
    public <T> QueryImplementor<T> createNamedQuery(String name, Class<T> resultClass) {
        return this.delegate.createNamedQuery(name, (Class)resultClass);
    }

    @Override
    public NativeQueryImplementor createNativeQuery(String sqlString) {
        return this.delegate.createNativeQuery(sqlString);
    }

    @Override
    public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
        return this.delegate.createNativeQuery(sqlString, resultClass);
    }

    @Override
    public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
        return this.delegate.createNativeQuery(sqlString, resultSetMapping);
    }

    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return this.delegate.createNamedStoredProcedureQuery(name);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return this.delegate.createStoredProcedureQuery(procedureName);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class ... resultClasses) {
        return this.delegate.createStoredProcedureQuery(procedureName, resultClasses);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String ... resultSetMappings) {
        return this.delegate.createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    public void joinTransaction() {
        this.delegate.joinTransaction();
    }

    public boolean isJoinedToTransaction() {
        return this.delegate.isJoinedToTransaction();
    }

    public <T> T unwrap(Class<T> cls) {
        return (T)this.delegate.unwrap(cls);
    }

    public Object getDelegate() {
        return this;
    }

    @Override
    public NativeQueryImplementor createSQLQuery(String queryString) {
        return this.delegate.createSQLQuery(queryString);
    }

    @Override
    public ProcedureCall getNamedProcedureCall(String name) {
        return this.delegate.getNamedProcedureCall(name);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName) {
        return this.delegate.createStoredProcedureCall(procedureName);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, Class ... resultClasses) {
        return this.delegate.createStoredProcedureCall(procedureName, resultClasses);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, String ... resultSetMappings) {
        return this.delegate.createStoredProcedureCall(procedureName, resultSetMappings);
    }

    @Override
    public Criteria createCriteria(Class persistentClass) {
        return this.delegate.createCriteria(persistentClass);
    }

    @Override
    public Criteria createCriteria(Class persistentClass, String alias) {
        return this.delegate.createCriteria(persistentClass, alias);
    }

    @Override
    public Criteria createCriteria(String entityName) {
        return this.delegate.createCriteria(entityName);
    }

    @Override
    public Criteria createCriteria(String entityName, String alias) {
        return this.delegate.createCriteria(entityName, alias);
    }

    @Override
    public SharedSessionBuilder sessionWithOptions() {
        return this.delegate.sessionWithOptions();
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.delegate.getSessionFactory();
    }

    @Override
    public void close() throws HibernateException {
        this.delegate.close();
    }

    @Override
    public void cancelQuery() throws HibernateException {
        this.delegate.cancelQuery();
    }

    @Override
    public boolean isDirty() throws HibernateException {
        return this.delegate.isDirty();
    }

    @Override
    public boolean isDefaultReadOnly() {
        return this.delegate.isDefaultReadOnly();
    }

    @Override
    public void setDefaultReadOnly(boolean readOnly) {
        this.delegate.setDefaultReadOnly(readOnly);
    }

    @Override
    public Serializable getIdentifier(Object object) {
        return this.delegate.getIdentifier(object);
    }

    @Override
    public boolean contains(String entityName, Object object) {
        return this.delegate.contains(entityName, object);
    }

    public boolean contains(Object object) {
        return this.delegate.contains(object);
    }

    public LockModeType getLockMode(Object entity) {
        return this.delegate.getLockMode(entity);
    }

    public void setProperty(String propertyName, Object value) {
        this.delegate.setProperty(propertyName, value);
    }

    public Map<String, Object> getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public void evict(Object object) {
        this.delegate.evict(object);
    }

    @Override
    public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode) {
        return this.delegate.load(theClass, id, lockMode);
    }

    @Override
    public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions) {
        return this.delegate.load(theClass, id, lockOptions);
    }

    @Override
    public Object load(String entityName, Serializable id, LockMode lockMode) {
        return this.delegate.load(entityName, id, lockMode);
    }

    @Override
    public Object load(String entityName, Serializable id, LockOptions lockOptions) {
        return this.delegate.load(entityName, id, lockOptions);
    }

    @Override
    public <T> T load(Class<T> theClass, Serializable id) {
        return this.delegate.load(theClass, id);
    }

    @Override
    public Object load(String entityName, Serializable id) {
        return this.delegate.load(entityName, id);
    }

    @Override
    public void load(Object object, Serializable id) {
        this.delegate.load(object, id);
    }

    @Override
    public void replicate(Object object, ReplicationMode replicationMode) {
        this.delegate.replicate(object, replicationMode);
    }

    @Override
    public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
        this.delegate.replicate(entityName, object, replicationMode);
    }

    @Override
    public Serializable save(Object object) {
        return this.delegate.save(object);
    }

    @Override
    public Serializable save(String entityName, Object object) {
        return this.delegate.save(entityName, object);
    }

    @Override
    public void saveOrUpdate(Object object) {
        this.delegate.saveOrUpdate(object);
    }

    @Override
    public void saveOrUpdate(String entityName, Object object) {
        this.delegate.saveOrUpdate(entityName, object);
    }

    @Override
    public void update(Object object) {
        this.delegate.update(object);
    }

    @Override
    public void update(String entityName, Object object) {
        this.delegate.update(entityName, object);
    }

    @Override
    public Object merge(Object object) {
        return this.delegate.merge(object);
    }

    @Override
    public Object merge(String entityName, Object object) {
        return this.delegate.merge(entityName, object);
    }

    @Override
    public void persist(Object object) {
        this.delegate.persist(object);
    }

    public void remove(Object entity) {
        this.delegate.remove(entity);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return (T)this.delegate.find(entityClass, primaryKey);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return (T)this.delegate.find(entityClass, primaryKey, properties);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return (T)this.delegate.find(entityClass, primaryKey, lockMode);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return (T)this.delegate.find(entityClass, primaryKey, lockMode, properties);
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return (T)this.delegate.getReference(entityClass, primaryKey);
    }

    @Override
    public void persist(String entityName, Object object) {
        this.delegate.persist(entityName, object);
    }

    @Override
    public void delete(Object object) {
        this.delegate.delete(object);
    }

    @Override
    public void delete(String entityName, Object object) {
        this.delegate.delete(entityName, object);
    }

    @Override
    public void lock(Object object, LockMode lockMode) {
        this.delegate.lock(object, lockMode);
    }

    @Override
    public void lock(String entityName, Object object, LockMode lockMode) {
        this.delegate.lock(entityName, object, lockMode);
    }

    @Override
    public Session.LockRequest buildLockRequest(LockOptions lockOptions) {
        return this.delegate.buildLockRequest(lockOptions);
    }

    @Override
    public void refresh(Object object) {
        this.delegate.refresh(object);
    }

    public void refresh(Object entity, Map<String, Object> properties) {
        this.delegate.refresh(entity, properties);
    }

    public void refresh(Object entity, LockModeType lockMode) {
        this.delegate.refresh(entity, lockMode);
    }

    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        this.delegate.refresh(entity, lockMode, properties);
    }

    @Override
    public void refresh(String entityName, Object object) {
        this.delegate.refresh(entityName, object);
    }

    @Override
    public void refresh(Object object, LockMode lockMode) {
        this.delegate.refresh(object, lockMode);
    }

    @Override
    public void refresh(Object object, LockOptions lockOptions) {
        this.delegate.refresh(object, lockOptions);
    }

    @Override
    public void refresh(String entityName, Object object, LockOptions lockOptions) {
        this.delegate.refresh(entityName, object, lockOptions);
    }

    @Override
    public LockMode getCurrentLockMode(Object object) {
        return this.delegate.getCurrentLockMode(object);
    }

    @Override
    public Query createFilter(Object collection, String queryString) {
        return this.delegate.createFilter(collection, queryString);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    public void detach(Object entity) {
        this.delegate.detach(entity);
    }

    @Override
    public <T> T get(Class<T> theClass, Serializable id) {
        return this.delegate.get(theClass, id);
    }

    @Override
    public <T> T get(Class<T> theClass, Serializable id, LockMode lockMode) {
        return this.delegate.get(theClass, id, lockMode);
    }

    @Override
    public <T> T get(Class<T> theClass, Serializable id, LockOptions lockOptions) {
        return this.delegate.get(theClass, id, lockOptions);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return this.delegate.get(entityName, id);
    }

    @Override
    public Object get(String entityName, Serializable id, LockMode lockMode) {
        return this.delegate.get(entityName, id, lockMode);
    }

    @Override
    public Object get(String entityName, Serializable id, LockOptions lockOptions) {
        return this.delegate.get(entityName, id, lockOptions);
    }

    @Override
    public String getEntityName(Object object) {
        return this.delegate.getEntityName(object);
    }

    @Override
    public <T> T getReference(T object) {
        return this.delegate.getReference(object);
    }

    @Override
    public IdentifierLoadAccess byId(String entityName) {
        return this.delegate.byId(entityName);
    }

    @Override
    public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass) {
        return this.delegate.byMultipleIds(entityClass);
    }

    @Override
    public MultiIdentifierLoadAccess byMultipleIds(String entityName) {
        return this.delegate.byMultipleIds(entityName);
    }

    @Override
    public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass) {
        return this.delegate.byId(entityClass);
    }

    @Override
    public NaturalIdLoadAccess byNaturalId(String entityName) {
        return this.delegate.byNaturalId(entityName);
    }

    @Override
    public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
        return this.delegate.byNaturalId(entityClass);
    }

    @Override
    public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
        return this.delegate.bySimpleNaturalId(entityName);
    }

    @Override
    public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
        return this.delegate.bySimpleNaturalId(entityClass);
    }

    @Override
    public Filter enableFilter(String filterName) {
        return this.delegate.enableFilter(filterName);
    }

    @Override
    public Filter getEnabledFilter(String filterName) {
        return this.delegate.getEnabledFilter(filterName);
    }

    @Override
    public void disableFilter(String filterName) {
        this.delegate.disableFilter(filterName);
    }

    @Override
    public SessionStatistics getStatistics() {
        return this.delegate.getStatistics();
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        return this.delegate.isReadOnly(entityOrProxy);
    }

    @Override
    public void setReadOnly(Object entityOrProxy, boolean readOnly) {
        this.delegate.setReadOnly(entityOrProxy, readOnly);
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        this.delegate.doWork(work);
    }

    @Override
    public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
        return this.delegate.doReturningWork(work);
    }

    @Override
    public Connection disconnect() {
        return this.delegate.disconnect();
    }

    @Override
    public void reconnect(Connection connection) {
        this.delegate.reconnect(connection);
    }

    @Override
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
        return this.delegate.isFetchProfileEnabled(name);
    }

    @Override
    public void enableFetchProfile(String name) throws UnknownProfileException {
        this.delegate.enableFetchProfile(name);
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException {
        this.delegate.disableFetchProfile(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.delegate.getTypeHelper();
    }

    @Override
    public LobHelper getLobHelper() {
        return this.delegate.getLobHelper();
    }

    @Override
    public void addEventListeners(SessionEventListener ... listeners) {
        this.delegate.addEventListeners(listeners);
    }

    @Override
    public boolean isFlushBeforeCompletionEnabled() {
        return this.delegate.isFlushBeforeCompletionEnabled();
    }

    @Override
    public ActionQueue getActionQueue() {
        return this.delegate.getActionQueue();
    }

    @Override
    public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
        return this.delegate.instantiate(persister, id);
    }

    @Override
    public void forceFlush(EntityEntry e) throws HibernateException {
        this.delegate.forceFlush(e);
    }

    @Override
    public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
        this.delegate.merge(entityName, object, copiedAlready);
    }

    @Override
    public void persist(String entityName, Object object, Map createdAlready) throws HibernateException {
        this.delegate.persist(entityName, object, createdAlready);
    }

    @Override
    public void persistOnFlush(String entityName, Object object, Map copiedAlready) {
        this.delegate.persistOnFlush(entityName, object, copiedAlready);
    }

    @Override
    public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
        this.delegate.refresh(entityName, object, refreshedAlready);
    }

    @Override
    public void delete(String entityName, Object child, boolean isCascadeDeleteEnabled, Set transientEntities) {
        this.delegate.delete(entityName, child, isCascadeDeleteEnabled, transientEntities);
    }

    @Override
    public void removeOrphanBeforeUpdates(String entityName, Object child) {
        this.delegate.removeOrphanBeforeUpdates(entityName, child);
    }

    @Override
    public SessionImplementor getSession() {
        return this;
    }

    @Override
    public boolean useStreamForLobBinding() {
        return this.delegate.useStreamForLobBinding();
    }

    @Override
    public LobCreator getLobCreator() {
        return this.delegate.getLobCreator();
    }

    @Override
    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        return this.delegate.remapSqlTypeDescriptor(sqlTypeDescriptor);
    }

    @Override
    public Integer getJdbcBatchSize() {
        return this.delegate.getJdbcBatchSize();
    }

    @Override
    public void setJdbcBatchSize(Integer jdbcBatchSize) {
        this.delegate.setJdbcBatchSize(jdbcBatchSize);
    }

    @Override
    public TimeZone getJdbcTimeZone() {
        return this.delegate.getJdbcTimeZone();
    }
}

