/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Criteria
 *  org.hibernate.Filter
 *  org.hibernate.FlushMode
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.LockMode
 *  org.hibernate.LockOptions
 *  org.hibernate.ReplicationMode
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.DetachedCriteria
 *  org.hibernate.criterion.Example
 *  org.hibernate.query.Query
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.ResourceHolderSupport
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 */
package org.springframework.orm.hibernate5;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.PersistenceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.query.Query;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateOperations;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class HibernateTemplate
implements HibernateOperations,
InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private SessionFactory sessionFactory;
    @Nullable
    private String[] filterNames;
    private boolean exposeNativeSession = false;
    private boolean checkWriteOperations = true;
    private boolean cacheQueries = false;
    @Nullable
    private String queryCacheRegion;
    private int fetchSize = 0;
    private int maxResults = 0;

    public HibernateTemplate() {
    }

    public HibernateTemplate(SessionFactory sessionFactory) {
        this.setSessionFactory(sessionFactory);
        this.afterPropertiesSet();
    }

    public void setSessionFactory(@Nullable SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Nullable
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    protected final SessionFactory obtainSessionFactory() {
        SessionFactory sessionFactory = this.getSessionFactory();
        Assert.state((sessionFactory != null ? 1 : 0) != 0, (String)"No SessionFactory set");
        return sessionFactory;
    }

    public void setFilterNames(String ... filterNames) {
        this.filterNames = filterNames;
    }

    @Nullable
    public String[] getFilterNames() {
        return this.filterNames;
    }

    public void setExposeNativeSession(boolean exposeNativeSession) {
        this.exposeNativeSession = exposeNativeSession;
    }

    public boolean isExposeNativeSession() {
        return this.exposeNativeSession;
    }

    public void setCheckWriteOperations(boolean checkWriteOperations) {
        this.checkWriteOperations = checkWriteOperations;
    }

    public boolean isCheckWriteOperations() {
        return this.checkWriteOperations;
    }

    public void setCacheQueries(boolean cacheQueries) {
        this.cacheQueries = cacheQueries;
    }

    public boolean isCacheQueries() {
        return this.cacheQueries;
    }

    public void setQueryCacheRegion(@Nullable String queryCacheRegion) {
        this.queryCacheRegion = queryCacheRegion;
    }

    @Nullable
    public String getQueryCacheRegion() {
        return this.queryCacheRegion;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void afterPropertiesSet() {
        if (this.getSessionFactory() == null) {
            throw new IllegalArgumentException("Property 'sessionFactory' is required");
        }
    }

    @Override
    @Nullable
    public <T> T execute(HibernateCallback<T> action) throws DataAccessException {
        return this.doExecute(action, false);
    }

    @Nullable
    public <T> T executeWithNativeSession(HibernateCallback<T> action) {
        return this.doExecute(action, true);
    }

    @Nullable
    protected <T> T doExecute(HibernateCallback<T> action, boolean enforceNativeSession) throws DataAccessException {
        Assert.notNull(action, (String)"Callback object must not be null");
        Session session = null;
        boolean isNew = false;
        try {
            session = this.obtainSessionFactory().getCurrentSession();
        }
        catch (HibernateException ex) {
            this.logger.debug((Object)"Could not retrieve pre-bound Hibernate session", (Throwable)ex);
        }
        if (session == null) {
            session = this.obtainSessionFactory().openSession();
            session.setHibernateFlushMode(FlushMode.MANUAL);
            isNew = true;
        }
        try {
            this.enableFilters(session);
            Session sessionToExpose = enforceNativeSession || this.isExposeNativeSession() ? session : this.createSessionProxy(session);
            T t = action.doInHibernate(sessionToExpose);
            return t;
        }
        catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
        catch (PersistenceException ex) {
            if (ex.getCause() instanceof HibernateException) {
                throw SessionFactoryUtils.convertHibernateAccessException((HibernateException)ex.getCause());
            }
            throw ex;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        finally {
            if (isNew) {
                SessionFactoryUtils.closeSession(session);
            } else {
                this.disableFilters(session);
            }
        }
    }

    protected Session createSessionProxy(Session session) {
        return (Session)Proxy.newProxyInstance(session.getClass().getClassLoader(), new Class[]{Session.class}, (InvocationHandler)new CloseSuppressingInvocationHandler(session));
    }

    protected void enableFilters(Session session) {
        String[] filterNames = this.getFilterNames();
        if (filterNames != null) {
            for (String filterName : filterNames) {
                session.enableFilter(filterName);
            }
        }
    }

    protected void disableFilters(Session session) {
        String[] filterNames = this.getFilterNames();
        if (filterNames != null) {
            for (String filterName : filterNames) {
                session.disableFilter(filterName);
            }
        }
    }

    @Override
    @Nullable
    public <T> T get(Class<T> entityClass, Serializable id) throws DataAccessException {
        return this.get(entityClass, id, null);
    }

    @Override
    @Nullable
    public <T> T get(Class<T> entityClass, Serializable id, @Nullable LockMode lockMode) throws DataAccessException {
        return (T)this.executeWithNativeSession(session -> {
            if (lockMode != null) {
                return session.get(entityClass, id, new LockOptions(lockMode));
            }
            return session.get(entityClass, id);
        });
    }

    @Override
    @Nullable
    public Object get(String entityName, Serializable id) throws DataAccessException {
        return this.get(entityName, id, null);
    }

    @Override
    @Nullable
    public Object get(String entityName, Serializable id, @Nullable LockMode lockMode) throws DataAccessException {
        return this.executeWithNativeSession(session -> {
            if (lockMode != null) {
                return session.get(entityName, id, new LockOptions(lockMode));
            }
            return session.get(entityName, id);
        });
    }

    @Override
    public <T> T load(Class<T> entityClass, Serializable id) throws DataAccessException {
        return this.load(entityClass, id, null);
    }

    @Override
    public <T> T load(Class<T> entityClass, Serializable id, @Nullable LockMode lockMode) throws DataAccessException {
        return (T)HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            if (lockMode != null) {
                return session.load(entityClass, id, new LockOptions(lockMode));
            }
            return session.load(entityClass, id);
        }));
    }

    @Override
    public Object load(String entityName, Serializable id) throws DataAccessException {
        return this.load(entityName, id, null);
    }

    @Override
    public Object load(String entityName, Serializable id, @Nullable LockMode lockMode) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            if (lockMode != null) {
                return session.load(entityName, id, new LockOptions(lockMode));
            }
            return session.load(entityName, id);
        }));
    }

    @Override
    public <T> List<T> loadAll(Class<T> entityClass) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Criteria criteria = session.createCriteria(entityClass);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            this.prepareCriteria(criteria);
            return criteria.list();
        }));
    }

    @Override
    public void load(Object entity, Serializable id) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            session.load(entity, id);
            return null;
        });
    }

    @Override
    public void refresh(Object entity) throws DataAccessException {
        this.refresh(entity, null);
    }

    @Override
    public void refresh(Object entity, @Nullable LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            if (lockMode != null) {
                session.refresh(entity, new LockOptions(lockMode));
            } else {
                session.refresh(entity);
            }
            return null;
        });
    }

    @Override
    public boolean contains(Object entity) throws DataAccessException {
        Boolean result = this.executeWithNativeSession(session -> session.contains(entity));
        Assert.state((result != null ? 1 : 0) != 0, (String)"No contains result");
        return result;
    }

    @Override
    public void evict(Object entity) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            session.evict(entity);
            return null;
        });
    }

    @Override
    public void initialize(Object proxy) throws DataAccessException {
        try {
            Hibernate.initialize((Object)proxy);
        }
        catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }

    @Override
    public Filter enableFilter(String filterName) throws IllegalStateException {
        Session session = this.obtainSessionFactory().getCurrentSession();
        Filter filter = session.getEnabledFilter(filterName);
        if (filter == null) {
            filter = session.enableFilter(filterName);
        }
        return filter;
    }

    @Override
    public void lock(Object entity, LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
            return null;
        });
    }

    @Override
    public void lock(String entityName, Object entity, LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            session.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
            return null;
        });
    }

    @Override
    public Serializable save(Object entity) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            return session.save(entity);
        }));
    }

    @Override
    public Serializable save(String entityName, Object entity) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            return session.save(entityName, entity);
        }));
    }

    @Override
    public void update(Object entity) throws DataAccessException {
        this.update(entity, null);
    }

    @Override
    public void update(Object entity, @Nullable LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.update(entity);
            if (lockMode != null) {
                session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
            }
            return null;
        });
    }

    @Override
    public void update(String entityName, Object entity) throws DataAccessException {
        this.update(entityName, entity, null);
    }

    @Override
    public void update(String entityName, Object entity, @Nullable LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.update(entityName, entity);
            if (lockMode != null) {
                session.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
            }
            return null;
        });
    }

    @Override
    public void saveOrUpdate(Object entity) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.saveOrUpdate(entity);
            return null;
        });
    }

    @Override
    public void saveOrUpdate(String entityName, Object entity) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.saveOrUpdate(entityName, entity);
            return null;
        });
    }

    @Override
    public void replicate(Object entity, ReplicationMode replicationMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.replicate(entity, replicationMode);
            return null;
        });
    }

    @Override
    public void replicate(String entityName, Object entity, ReplicationMode replicationMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.replicate(entityName, entity, replicationMode);
            return null;
        });
    }

    @Override
    public void persist(Object entity) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.persist(entity);
            return null;
        });
    }

    @Override
    public void persist(String entityName, Object entity) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            session.persist(entityName, entity);
            return null;
        });
    }

    @Override
    public <T> T merge(T entity) throws DataAccessException {
        return (T)HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            return session.merge(entity);
        }));
    }

    @Override
    public <T> T merge(String entityName, T entity) throws DataAccessException {
        return (T)HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            return session.merge(entityName, entity);
        }));
    }

    @Override
    public void delete(Object entity) throws DataAccessException {
        this.delete(entity, null);
    }

    @Override
    public void delete(Object entity, @Nullable LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            if (lockMode != null) {
                session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
            }
            session.delete(entity);
            return null;
        });
    }

    @Override
    public void delete(String entityName, Object entity) throws DataAccessException {
        this.delete(entityName, entity, null);
    }

    @Override
    public void delete(String entityName, Object entity, @Nullable LockMode lockMode) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            if (lockMode != null) {
                session.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
            }
            session.delete(entityName, entity);
            return null;
        });
    }

    @Override
    public void deleteAll(Collection<?> entities) throws DataAccessException {
        this.executeWithNativeSession(session -> {
            this.checkWriteOperationAllowed(session);
            for (Object entity : entities) {
                session.delete(entity);
            }
            return null;
        });
    }

    @Override
    public void flush() throws DataAccessException {
        this.executeWithNativeSession(session -> {
            session.flush();
            return null;
        });
    }

    @Override
    public void clear() throws DataAccessException {
        this.executeWithNativeSession(session -> {
            session.clear();
            return null;
        });
    }

    @Override
    public List<?> findByCriteria(DetachedCriteria criteria) throws DataAccessException {
        return this.findByCriteria(criteria, -1, -1);
    }

    @Override
    public List<?> findByCriteria(DetachedCriteria criteria, int firstResult, int maxResults) throws DataAccessException {
        Assert.notNull((Object)criteria, (String)"DetachedCriteria must not be null");
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Criteria executableCriteria = criteria.getExecutableCriteria(session);
            this.prepareCriteria(executableCriteria);
            if (firstResult >= 0) {
                executableCriteria.setFirstResult(firstResult);
            }
            if (maxResults > 0) {
                executableCriteria.setMaxResults(maxResults);
            }
            return executableCriteria.list();
        }));
    }

    @Override
    public <T> List<T> findByExample(T exampleEntity) throws DataAccessException {
        return this.findByExample(null, exampleEntity, -1, -1);
    }

    @Override
    public <T> List<T> findByExample(String entityName, T exampleEntity) throws DataAccessException {
        return this.findByExample(entityName, exampleEntity, -1, -1);
    }

    @Override
    public <T> List<T> findByExample(T exampleEntity, int firstResult, int maxResults) throws DataAccessException {
        return this.findByExample(null, exampleEntity, firstResult, maxResults);
    }

    @Override
    public <T> List<T> findByExample(@Nullable String entityName, T exampleEntity, int firstResult, int maxResults) throws DataAccessException {
        Assert.notNull(exampleEntity, (String)"Example entity must not be null");
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Criteria executableCriteria = entityName != null ? session.createCriteria(entityName) : session.createCriteria(exampleEntity.getClass());
            executableCriteria.add((Criterion)Example.create((Object)exampleEntity));
            this.prepareCriteria(executableCriteria);
            if (firstResult >= 0) {
                executableCriteria.setFirstResult(firstResult);
            }
            if (maxResults > 0) {
                executableCriteria.setMaxResults(maxResults);
            }
            return executableCriteria.list();
        }));
    }

    @Override
    @Deprecated
    public List<?> find(String queryString, Object ... values) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.createQuery(queryString);
            this.prepareQuery(queryObject);
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    queryObject.setParameter(i, values[i]);
                }
            }
            return queryObject.list();
        }));
    }

    @Override
    @Deprecated
    public List<?> findByNamedParam(String queryString, String paramName, Object value) throws DataAccessException {
        return this.findByNamedParam(queryString, new String[]{paramName}, new Object[]{value});
    }

    @Override
    @Deprecated
    public List<?> findByNamedParam(String queryString, String[] paramNames, Object[] values) throws DataAccessException {
        if (paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.createQuery(queryString);
            this.prepareQuery(queryObject);
            for (int i = 0; i < values.length; ++i) {
                this.applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
            }
            return queryObject.list();
        }));
    }

    @Override
    @Deprecated
    public List<?> findByValueBean(String queryString, Object valueBean) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.createQuery(queryString);
            this.prepareQuery(queryObject);
            queryObject.setProperties(valueBean);
            return queryObject.list();
        }));
    }

    @Override
    @Deprecated
    public List<?> findByNamedQuery(String queryName, Object ... values) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.getNamedQuery(queryName);
            this.prepareQuery(queryObject);
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    queryObject.setParameter(i, values[i]);
                }
            }
            return queryObject.list();
        }));
    }

    @Override
    @Deprecated
    public List<?> findByNamedQueryAndNamedParam(String queryName, String paramName, Object value) throws DataAccessException {
        return this.findByNamedQueryAndNamedParam(queryName, new String[]{paramName}, new Object[]{value});
    }

    @Override
    @Deprecated
    public List<?> findByNamedQueryAndNamedParam(String queryName, @Nullable String[] paramNames, @Nullable Object[] values) throws DataAccessException {
        if (values != null && (paramNames == null || paramNames.length != values.length)) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.getNamedQuery(queryName);
            this.prepareQuery(queryObject);
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    this.applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
                }
            }
            return queryObject.list();
        }));
    }

    @Override
    @Deprecated
    public List<?> findByNamedQueryAndValueBean(String queryName, Object valueBean) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.getNamedQuery(queryName);
            this.prepareQuery(queryObject);
            queryObject.setProperties(valueBean);
            return queryObject.list();
        }));
    }

    @Override
    @Deprecated
    public Iterator<?> iterate(String queryString, Object ... values) throws DataAccessException {
        return HibernateTemplate.nonNull(this.executeWithNativeSession(session -> {
            Query queryObject = session.createQuery(queryString);
            this.prepareQuery(queryObject);
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    queryObject.setParameter(i, values[i]);
                }
            }
            return queryObject.iterate();
        }));
    }

    @Override
    @Deprecated
    public void closeIterator(Iterator<?> it) throws DataAccessException {
        try {
            Hibernate.close(it);
        }
        catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }

    @Override
    @Deprecated
    public int bulkUpdate(String queryString, Object ... values) throws DataAccessException {
        Integer result = this.executeWithNativeSession(session -> {
            Query queryObject = session.createQuery(queryString);
            this.prepareQuery(queryObject);
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    queryObject.setParameter(i, values[i]);
                }
            }
            return queryObject.executeUpdate();
        });
        Assert.state((result != null ? 1 : 0) != 0, (String)"No update count");
        return result;
    }

    protected void checkWriteOperationAllowed(Session session) throws InvalidDataAccessApiUsageException {
        if (this.isCheckWriteOperations() && session.getHibernateFlushMode().lessThan(FlushMode.COMMIT)) {
            throw new InvalidDataAccessApiUsageException("Write operations are not allowed in read-only mode (FlushMode.MANUAL): Turn your Session into FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.");
        }
    }

    protected void prepareCriteria(Criteria criteria) {
        ResourceHolderSupport sessionHolder;
        if (this.isCacheQueries()) {
            criteria.setCacheable(true);
            if (this.getQueryCacheRegion() != null) {
                criteria.setCacheRegion(this.getQueryCacheRegion());
            }
        }
        if (this.getFetchSize() > 0) {
            criteria.setFetchSize(this.getFetchSize());
        }
        if (this.getMaxResults() > 0) {
            criteria.setMaxResults(this.getMaxResults());
        }
        if ((sessionHolder = (ResourceHolderSupport)TransactionSynchronizationManager.getResource((Object)this.obtainSessionFactory())) != null && sessionHolder.hasTimeout()) {
            criteria.setTimeout(sessionHolder.getTimeToLiveInSeconds());
        }
    }

    protected void prepareQuery(Query<?> queryObject) {
        ResourceHolderSupport sessionHolder;
        if (this.isCacheQueries()) {
            queryObject.setCacheable(true);
            if (this.getQueryCacheRegion() != null) {
                queryObject.setCacheRegion(this.getQueryCacheRegion());
            }
        }
        if (this.getFetchSize() > 0) {
            queryObject.setFetchSize(this.getFetchSize());
        }
        if (this.getMaxResults() > 0) {
            queryObject.setMaxResults(this.getMaxResults());
        }
        if ((sessionHolder = (ResourceHolderSupport)TransactionSynchronizationManager.getResource((Object)this.obtainSessionFactory())) != null && sessionHolder.hasTimeout()) {
            queryObject.setTimeout(sessionHolder.getTimeToLiveInSeconds());
        }
    }

    protected void applyNamedParameterToQuery(Query<?> queryObject, String paramName, Object value) throws HibernateException {
        if (value instanceof Collection) {
            queryObject.setParameterList(paramName, (Collection)value);
        } else if (value instanceof Object[]) {
            queryObject.setParameterList(paramName, (Object[])value);
        } else {
            queryObject.setParameter(paramName, value);
        }
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result");
        return result;
    }

    private class CloseSuppressingInvocationHandler
    implements InvocationHandler {
        private final Session target;

        public CloseSuppressingInvocationHandler(Session target) {
            this.target = target;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "close": {
                    return null;
                }
            }
            try {
                Object retVal = method.invoke((Object)this.target, args);
                if (retVal instanceof Criteria) {
                    HibernateTemplate.this.prepareCriteria((Criteria)retVal);
                } else if (retVal instanceof Query) {
                    HibernateTemplate.this.prepareQuery((Query)retVal);
                }
                return retVal;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}

