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
 *  javax.persistence.metamodel.Metamodel
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.graph.RootGraph;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.NativeQuery;
import org.hibernate.stat.SessionStatistics;

public class SessionLazyDelegator
implements Session {
    private final Supplier<Session> lazySession;

    public SessionLazyDelegator(Supplier<Session> lazySessionLookup) {
        this.lazySession = lazySessionLookup;
    }

    @Override
    public SharedSessionBuilder sessionWithOptions() {
        return this.lazySession.get().sessionWithOptions();
    }

    @Override
    public void flush() throws HibernateException {
        this.lazySession.get().flush();
    }

    @Override
    @Deprecated
    public void setFlushMode(FlushMode flushMode) {
        this.lazySession.get().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return this.lazySession.get().getFlushMode();
    }

    @Override
    public void setHibernateFlushMode(FlushMode flushMode) {
        this.lazySession.get().setHibernateFlushMode(flushMode);
    }

    @Override
    public FlushMode getHibernateFlushMode() {
        return this.lazySession.get().getHibernateFlushMode();
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        this.lazySession.get().setCacheMode(cacheMode);
    }

    @Override
    public CacheMode getCacheMode() {
        return this.lazySession.get().getCacheMode();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return this.lazySession.get().getSessionFactory();
    }

    @Override
    public void cancelQuery() throws HibernateException {
        this.lazySession.get().cancelQuery();
    }

    @Override
    public boolean isDirty() throws HibernateException {
        return this.lazySession.get().isDirty();
    }

    @Override
    public boolean isDefaultReadOnly() {
        return this.lazySession.get().isDefaultReadOnly();
    }

    @Override
    public void setDefaultReadOnly(boolean readOnly) {
        this.lazySession.get().setDefaultReadOnly(readOnly);
    }

    @Override
    public Serializable getIdentifier(Object object) {
        return this.lazySession.get().getIdentifier(object);
    }

    @Override
    public boolean contains(String entityName, Object object) {
        return this.lazySession.get().contains(entityName, object);
    }

    @Override
    public void evict(Object object) {
        this.lazySession.get().evict(object);
    }

    @Override
    public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode) {
        return this.lazySession.get().load(theClass, id, lockMode);
    }

    @Override
    public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions) {
        return this.lazySession.get().load(theClass, id, lockOptions);
    }

    @Override
    public Object load(String entityName, Serializable id, LockMode lockMode) {
        return this.lazySession.get().load(entityName, id, lockMode);
    }

    @Override
    public Object load(String entityName, Serializable id, LockOptions lockOptions) {
        return this.lazySession.get().load(entityName, id, lockOptions);
    }

    @Override
    public <T> T load(Class<T> theClass, Serializable id) {
        return this.lazySession.get().load(theClass, id);
    }

    @Override
    public Object load(String entityName, Serializable id) {
        return this.lazySession.get().load(entityName, id);
    }

    @Override
    public void load(Object object, Serializable id) {
        this.lazySession.get().load(object, id);
    }

    @Override
    public void replicate(Object object, ReplicationMode replicationMode) {
        this.lazySession.get().replicate(object, replicationMode);
    }

    @Override
    public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
        this.lazySession.get().replicate(entityName, object, replicationMode);
    }

    @Override
    public Serializable save(Object object) {
        return this.lazySession.get().save(object);
    }

    @Override
    public Serializable save(String entityName, Object object) {
        return this.lazySession.get().save(entityName, object);
    }

    @Override
    public void saveOrUpdate(Object object) {
        this.lazySession.get().saveOrUpdate(object);
    }

    @Override
    public void saveOrUpdate(String entityName, Object object) {
        this.lazySession.get().saveOrUpdate(entityName, object);
    }

    @Override
    public void update(Object object) {
        this.lazySession.get().update(object);
    }

    @Override
    public void update(String entityName, Object object) {
        this.lazySession.get().update(entityName, object);
    }

    @Override
    public Object merge(Object object) {
        return this.lazySession.get().merge(object);
    }

    @Override
    public Object merge(String entityName, Object object) {
        return this.lazySession.get().merge(entityName, object);
    }

    @Override
    public void persist(Object object) {
        this.lazySession.get().persist(object);
    }

    @Override
    public void persist(String entityName, Object object) {
        this.lazySession.get().persist(entityName, object);
    }

    @Override
    public void delete(Object object) {
        this.lazySession.get().delete(object);
    }

    @Override
    public void delete(String entityName, Object object) {
        this.lazySession.get().delete(entityName, object);
    }

    @Override
    public void lock(Object object, LockMode lockMode) {
        this.lazySession.get().lock(object, lockMode);
    }

    @Override
    public void lock(String entityName, Object object, LockMode lockMode) {
        this.lazySession.get().lock(entityName, object, lockMode);
    }

    @Override
    public Session.LockRequest buildLockRequest(LockOptions lockOptions) {
        return this.lazySession.get().buildLockRequest(lockOptions);
    }

    @Override
    public void refresh(Object object) {
        this.lazySession.get().refresh(object);
    }

    @Override
    public void refresh(String entityName, Object object) {
        this.lazySession.get().refresh(entityName, object);
    }

    @Override
    public void refresh(Object object, LockMode lockMode) {
        this.lazySession.get().refresh(object, lockMode);
    }

    @Override
    public void refresh(Object object, LockOptions lockOptions) {
        this.lazySession.get().refresh(object, lockOptions);
    }

    @Override
    public void refresh(String entityName, Object object, LockOptions lockOptions) {
        this.lazySession.get().refresh(entityName, object, lockOptions);
    }

    @Override
    public LockMode getCurrentLockMode(Object object) {
        return this.lazySession.get().getCurrentLockMode(object);
    }

    @Override
    @Deprecated
    public Query createFilter(Object collection, String queryString) {
        return this.lazySession.get().createFilter(collection, queryString);
    }

    @Override
    public void clear() {
        this.lazySession.get().clear();
    }

    @Override
    public <T> T get(Class<T> entityType, Serializable id) {
        return this.lazySession.get().get(entityType, id);
    }

    @Override
    public <T> T get(Class<T> entityType, Serializable id, LockMode lockMode) {
        return this.lazySession.get().get(entityType, id, lockMode);
    }

    @Override
    public <T> T get(Class<T> entityType, Serializable id, LockOptions lockOptions) {
        return this.lazySession.get().get(entityType, id, lockOptions);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return this.lazySession.get().get(entityName, id);
    }

    @Override
    public Object get(String entityName, Serializable id, LockMode lockMode) {
        return this.lazySession.get().get(entityName, id, lockMode);
    }

    @Override
    public Object get(String entityName, Serializable id, LockOptions lockOptions) {
        return this.lazySession.get().get(entityName, id, lockOptions);
    }

    @Override
    public String getEntityName(Object object) {
        return this.lazySession.get().getEntityName(object);
    }

    @Override
    public <T> T getReference(T object) {
        return this.lazySession.get().getReference(object);
    }

    @Override
    public IdentifierLoadAccess byId(String entityName) {
        return this.lazySession.get().byId(entityName);
    }

    @Override
    public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass) {
        return this.lazySession.get().byMultipleIds(entityClass);
    }

    @Override
    public MultiIdentifierLoadAccess byMultipleIds(String entityName) {
        return this.lazySession.get().byMultipleIds(entityName);
    }

    @Override
    public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass) {
        return this.lazySession.get().byId(entityClass);
    }

    @Override
    public NaturalIdLoadAccess byNaturalId(String entityName) {
        return this.lazySession.get().byNaturalId(entityName);
    }

    @Override
    public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
        return this.lazySession.get().byNaturalId(entityClass);
    }

    @Override
    public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
        return this.lazySession.get().bySimpleNaturalId(entityName);
    }

    @Override
    public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
        return this.lazySession.get().bySimpleNaturalId(entityClass);
    }

    @Override
    public Filter enableFilter(String filterName) {
        return this.lazySession.get().enableFilter(filterName);
    }

    @Override
    public Filter getEnabledFilter(String filterName) {
        return this.lazySession.get().getEnabledFilter(filterName);
    }

    @Override
    public void disableFilter(String filterName) {
        this.lazySession.get().disableFilter(filterName);
    }

    @Override
    public SessionStatistics getStatistics() {
        return this.lazySession.get().getStatistics();
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        return this.lazySession.get().isReadOnly(entityOrProxy);
    }

    @Override
    public void setReadOnly(Object entityOrProxy, boolean readOnly) {
        this.lazySession.get().setReadOnly(entityOrProxy, readOnly);
    }

    @Override
    public <T> RootGraph<T> createEntityGraph(Class<T> rootType) {
        return this.lazySession.get().createEntityGraph((Class)rootType);
    }

    @Override
    public RootGraph<?> createEntityGraph(String graphName) {
        return this.lazySession.get().createEntityGraph(graphName);
    }

    @Override
    public RootGraph<?> getEntityGraph(String graphName) {
        return this.lazySession.get().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return this.lazySession.get().getEntityGraphs(entityClass);
    }

    @Override
    public Connection disconnect() {
        return this.lazySession.get().disconnect();
    }

    @Override
    public void reconnect(Connection connection) {
        this.lazySession.get().reconnect(connection);
    }

    @Override
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
        return this.lazySession.get().isFetchProfileEnabled(name);
    }

    @Override
    public void enableFetchProfile(String name) throws UnknownProfileException {
        this.lazySession.get().enableFetchProfile(name);
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException {
        this.lazySession.get().disableFetchProfile(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.lazySession.get().getTypeHelper();
    }

    @Override
    public LobHelper getLobHelper() {
        return this.lazySession.get().getLobHelper();
    }

    @Override
    public void addEventListeners(SessionEventListener ... listeners) {
        this.lazySession.get().addEventListeners(listeners);
    }

    @Override
    public <T> org.hibernate.query.Query<T> createQuery(String queryString, Class<T> resultType) {
        return this.lazySession.get().createQuery(queryString, (Class)resultType);
    }

    @Override
    public <T> org.hibernate.query.Query<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return this.lazySession.get().createQuery((CriteriaQuery)criteriaQuery);
    }

    @Override
    public org.hibernate.query.Query createQuery(CriteriaUpdate updateQuery) {
        return this.lazySession.get().createQuery(updateQuery);
    }

    @Override
    public org.hibernate.query.Query createQuery(CriteriaDelete deleteQuery) {
        return this.lazySession.get().createQuery(deleteQuery);
    }

    @Override
    public <T> org.hibernate.query.Query<T> createNamedQuery(String name, Class<T> resultType) {
        return this.lazySession.get().createNamedQuery(name, (Class)resultType);
    }

    @Override
    public NativeQuery createSQLQuery(String queryString) {
        return this.lazySession.get().createSQLQuery(queryString);
    }

    @Override
    public String getTenantIdentifier() {
        return this.lazySession.get().getTenantIdentifier();
    }

    @Override
    public void close() throws HibernateException {
        this.lazySession.get().close();
    }

    @Override
    public boolean isOpen() {
        return this.lazySession.get().isOpen();
    }

    @Override
    public boolean isConnected() {
        return this.lazySession.get().isConnected();
    }

    @Override
    public Transaction beginTransaction() {
        return this.lazySession.get().beginTransaction();
    }

    @Override
    public Transaction getTransaction() {
        return this.lazySession.get().getTransaction();
    }

    @Override
    public org.hibernate.query.Query createQuery(String queryString) {
        return this.lazySession.get().createQuery(queryString);
    }

    @Override
    public org.hibernate.query.Query getNamedQuery(String queryName) {
        return this.lazySession.get().getNamedQuery(queryName);
    }

    @Override
    public ProcedureCall getNamedProcedureCall(String name) {
        return this.lazySession.get().getNamedProcedureCall(name);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName) {
        return this.lazySession.get().createStoredProcedureCall(procedureName);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, Class ... resultClasses) {
        return this.lazySession.get().createStoredProcedureCall(procedureName, resultClasses);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, String ... resultSetMappings) {
        return this.lazySession.get().createStoredProcedureCall(procedureName, resultSetMappings);
    }

    @Override
    @Deprecated
    public Criteria createCriteria(Class persistentClass) {
        return this.lazySession.get().createCriteria(persistentClass);
    }

    @Override
    @Deprecated
    public Criteria createCriteria(Class persistentClass, String alias) {
        return this.lazySession.get().createCriteria(persistentClass, alias);
    }

    @Override
    @Deprecated
    public Criteria createCriteria(String entityName) {
        return this.lazySession.get().createCriteria(entityName);
    }

    @Override
    @Deprecated
    public Criteria createCriteria(String entityName, String alias) {
        return this.lazySession.get().createCriteria(entityName, alias);
    }

    @Override
    public Integer getJdbcBatchSize() {
        return this.lazySession.get().getJdbcBatchSize();
    }

    @Override
    public void setJdbcBatchSize(Integer jdbcBatchSize) {
        this.lazySession.get().setJdbcBatchSize(jdbcBatchSize);
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        this.lazySession.get().doWork(work);
    }

    @Override
    public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
        return this.lazySession.get().doReturningWork(work);
    }

    @Override
    public org.hibernate.query.Query createNamedQuery(String name) {
        return this.lazySession.get().createNamedQuery(name);
    }

    @Override
    public NativeQuery createNativeQuery(String sqlString) {
        return this.lazySession.get().createNativeQuery(sqlString);
    }

    @Override
    public NativeQuery createNativeQuery(String sqlString, String resultSetMapping) {
        return this.lazySession.get().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    @Deprecated
    public Query getNamedSQLQuery(String name) {
        return this.lazySession.get().getNamedSQLQuery(name);
    }

    @Override
    public NativeQuery getNamedNativeQuery(String name) {
        return this.lazySession.get().getNamedNativeQuery(name);
    }

    public void remove(Object entity) {
        this.lazySession.get().remove(entity);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return (T)this.lazySession.get().find(entityClass, primaryKey);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return (T)this.lazySession.get().find(entityClass, primaryKey, properties);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return (T)this.lazySession.get().find(entityClass, primaryKey, lockMode);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return (T)this.lazySession.get().find(entityClass, primaryKey, lockMode, properties);
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return (T)this.lazySession.get().getReference(entityClass, primaryKey);
    }

    public void setFlushMode(FlushModeType flushMode) {
        this.lazySession.get().setFlushMode(flushMode);
    }

    public void lock(Object entity, LockModeType lockMode) {
        this.lazySession.get().lock(entity, lockMode);
    }

    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        this.lazySession.get().lock(entity, lockMode, properties);
    }

    public void refresh(Object entity, Map<String, Object> properties) {
        this.lazySession.get().refresh(entity, properties);
    }

    public void refresh(Object entity, LockModeType lockMode) {
        this.lazySession.get().refresh(entity, lockMode);
    }

    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        this.lazySession.get().refresh(entity, lockMode, properties);
    }

    public void detach(Object entity) {
        this.lazySession.get().detach(entity);
    }

    public boolean contains(Object entity) {
        return this.lazySession.get().contains(entity);
    }

    public LockModeType getLockMode(Object entity) {
        return this.lazySession.get().getLockMode(entity);
    }

    public void setProperty(String propertyName, Object value) {
        this.lazySession.get().setProperty(propertyName, value);
    }

    public Map<String, Object> getProperties() {
        return this.lazySession.get().getProperties();
    }

    public NativeQuery createNativeQuery(String sqlString, Class resultClass) {
        return this.lazySession.get().createNativeQuery(sqlString, resultClass);
    }

    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return this.lazySession.get().createNamedStoredProcedureQuery(name);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return this.lazySession.get().createStoredProcedureQuery(procedureName);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class ... resultClasses) {
        return this.lazySession.get().createStoredProcedureQuery(procedureName, resultClasses);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String ... resultSetMappings) {
        return this.lazySession.get().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    public void joinTransaction() {
        this.lazySession.get().joinTransaction();
    }

    public boolean isJoinedToTransaction() {
        return this.lazySession.get().isJoinedToTransaction();
    }

    public <T> T unwrap(Class<T> cls) {
        return (T)this.lazySession.get().unwrap(cls);
    }

    public Object getDelegate() {
        return this.lazySession.get().getDelegate();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.lazySession.get().getEntityManagerFactory();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return this.lazySession.get().getCriteriaBuilder();
    }

    public Metamodel getMetamodel() {
        return this.lazySession.get().getMetamodel();
    }

    @Override
    public Session getSession() {
        return this.lazySession.get().getSession();
    }
}

