/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Indexer
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Lists
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Criteria
 *  org.hibernate.NonUniqueResultException
 *  org.hibernate.ReplicationMode
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DaoSupport
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.orm.jpa.EntityManagerHolder
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.bonnie.Indexer;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.impl.search.IndexerEventPublisher;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DaoSupport;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class HibernateObjectDao<T extends EntityObject>
extends DaoSupport
implements ObjectDaoInternal<T> {
    private static final Logger LOG = LoggerFactory.getLogger(HibernateObjectDao.class);
    private Indexer indexer;
    private HibernateTemplate hibernateTemplate;
    private EventPublisher eventPublisher;

    public final SessionFactory getSessionFactory() {
        return this.hibernateTemplate != null ? this.hibernateTemplate.getSessionFactory() : null;
    }

    public final void setSessionFactory(SessionFactory sessionFactory) {
        if (this.hibernateTemplate == null || sessionFactory != this.hibernateTemplate.getSessionFactory()) {
            this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        }
    }

    @Deprecated(forRemoval=true)
    public final HibernateTemplate getHibernateTemplate() {
        return this.hibernateTemplate;
    }

    @Deprecated(forRemoval=true)
    public final void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Deprecated(forRemoval=true)
    protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
        return new HibernateTemplate(sessionFactory);
    }

    @Deprecated
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected @Nullable T getByClassId(long id) {
        return (T)((EntityObject)this.getHibernateTemplate().execute(session -> (EntityObject)session.get(this.getPersistentClass(), (Serializable)Long.valueOf(id))));
    }

    @Override
    @Deprecated
    public <E> @NonNull PageResponse<E> findByClassIds(Iterable<Long> ids, LimitedRequest limitedRequest, com.google.common.base.Predicate<? super E> filter) {
        int maxResultCount = limitedRequest.getLimit() + 1;
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)((List)this.getHibernateTemplate().execute(session -> {
            Query<T> queryObject = this.buildFindByClassIdsQuery(session, Lists.newArrayList((Iterable)ids));
            this.limitQuery(queryObject, limitedRequest.getStart(), maxResultCount);
            return queryObject.list();
        })), filter);
    }

    @Override
    public @NonNull PageResponse<T> findByClassIdsFiltered(Iterable<Long> ids, LimitedRequest limitedRequest, Predicate<? super T> filter) {
        int maxResultCount = limitedRequest.getLimit() + 1;
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)((List)this.getHibernateTemplate().execute(session -> {
            Query<T> queryObject = this.buildFindByClassIdsQuery(session, Lists.newArrayList((Iterable)ids));
            this.limitQuery(queryObject, limitedRequest.getStart(), maxResultCount);
            return queryObject.list();
        })), filter);
    }

    private Query<T> buildFindByClassIdsQuery(Session session, Collection<Long> ids) {
        CriteriaQuery query = session.getCriteriaBuilder().createQuery(this.getPersistentClass());
        Root root = query.from(this.getPersistentClass());
        query.select((Selection)root);
        query.where((Expression)root.get("id").in(ids));
        return session.createQuery(query);
    }

    @Override
    public @NonNull List<T> findAll() {
        return this.findAllSorted(null);
    }

    @Override
    public @NonNull List<T> findAllSorted(String sortField) {
        return this.findAllSorted(sortField, true, -1, -1);
    }

    @Override
    public @NonNull List<T> findAllSorted(String sortField, boolean cacheable, int offset, int maxResultCount) {
        String query = "FROM " + this.getPersistentClass().getName() + " result";
        if (sortField != null) {
            query = query + " ORDER BY LOWER(result." + sortField + ")";
        }
        String finalQuery = query;
        List result = (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(finalQuery, this.getPersistentClass());
            queryObject.setCacheable(cacheable);
            HibernateObjectDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            this.limitQuery(queryObject, offset, maxResultCount);
            return queryObject.list();
        });
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    @Deprecated
    public void save(EntityObject objectToSave) {
        this.updateModificationData(objectToSave);
        this.saveRaw(objectToSave);
    }

    @Override
    public void saveEntity(T objectToSave) {
        this.save((EntityObject)objectToSave);
    }

    @Deprecated
    protected void updateModificationData(EntityObject objectToSave) {
        Date date = objectToSave.getCurrentDate();
        objectToSave.setLastModificationDate(date);
        if (objectToSave.getCreationDate() == null) {
            objectToSave.setCreationDate(date);
        }
    }

    protected void updateEntityModificationData(T objectToSave) {
        this.updateModificationData((EntityObject)objectToSave);
    }

    @Override
    public void saveRaw(EntityObject objectToSave) {
        this.getHibernateTemplate().saveOrUpdate((Object)objectToSave);
        this.reIndex(objectToSave);
    }

    @Override
    public void saveRawEntity(T objectToSave) {
        this.saveRaw((EntityObject)objectToSave);
    }

    @Override
    public void remove(EntityObject objectToRemove) {
        try {
            this.getHibernateTemplate().delete((Object)objectToRemove);
            this.unIndex(objectToRemove, true);
        }
        catch (Exception e) {
            LOG.error("remove error!", (Throwable)e);
            throw new InfrastructureException((Throwable)e);
        }
    }

    @Override
    public void removeEntity(T objectToRemove) {
        this.remove((EntityObject)objectToRemove);
    }

    @Override
    public void refresh(EntityObject objectToRefresh) {
        this.getHibernateTemplate().execute(session -> {
            session.refresh((Object)objectToRefresh);
            return null;
        });
    }

    @Override
    public void refreshEntity(T objectToRefresh) {
        this.refresh((EntityObject)objectToRefresh);
    }

    @Override
    public void replicate(Object objectToReplicate) {
        this.getHibernateTemplate().execute(session -> {
            session.replicate(objectToReplicate, ReplicationMode.OVERWRITE);
            return null;
        });
    }

    @Override
    public void replicateEntity(T objectToReplicate) {
        this.replicate(objectToReplicate);
    }

    private void withIndexer(Consumer<Indexer> task) {
        if (this.indexer != null) {
            task.accept(this.indexer);
        } else if (this.eventPublisher != null) {
            new IndexerEventPublisher(this.eventPublisher).publishCallbackEvent(task::accept);
        }
    }

    @Deprecated
    protected void index(EntityObject objectToSave) {
        this.withIndexer(indexer -> HibernateObjectDao.index(objectToSave, indexer));
    }

    private static void index(EntityObject objectToSave, Indexer indexer) {
        if (objectToSave instanceof Searchable) {
            HibernateObjectDao.index((Searchable)objectToSave, indexer);
        }
    }

    private static void index(Searchable objectToSave, Indexer indexer) {
        try {
            indexer.index(objectToSave);
        }
        catch (RuntimeException e) {
            LOG.error("Unable to index object: {} -- {}", new Object[]{objectToSave, e.getMessage(), e});
        }
    }

    protected void indexEntity(T objectToSave) {
        this.index((EntityObject)objectToSave);
    }

    @Deprecated
    protected void reIndex(EntityObject objectToSave) {
        this.withIndexer(indexer -> HibernateObjectDao.reIndex(objectToSave, indexer));
    }

    protected void reIndexEntity(T objectToSave) {
        this.reIndex((EntityObject)objectToSave);
    }

    private static void reIndex(EntityObject objectToSave, Indexer indexer) {
        if (objectToSave instanceof Searchable) {
            HibernateObjectDao.reIndex((Searchable)objectToSave, indexer);
        }
    }

    private static void reIndex(Searchable objectToSave, Indexer indexer) {
        try {
            indexer.reIndex(objectToSave);
        }
        catch (RuntimeException e) {
            LOG.error("Unable to reIndex object: {} -- {}", new Object[]{objectToSave, e.getMessage(), e});
        }
    }

    @Deprecated
    protected void unIndex(EntityObject objectToSave, boolean unindexDependents) {
        this.withIndexer(indexer -> HibernateObjectDao.unIndex(objectToSave, unindexDependents, indexer));
    }

    private static void unIndex(EntityObject objectToSave, boolean unindexDependents, Indexer indexer) {
        if (objectToSave instanceof Searchable) {
            HibernateObjectDao.unIndex((Searchable)objectToSave, unindexDependents, indexer);
        }
    }

    private static void unIndex(Searchable objectToSave, boolean unindexDependents, Indexer indexer) {
        try {
            if (unindexDependents) {
                for (Object o : objectToSave.getSearchableDependants()) {
                    HibernateObjectDao.unIndex((EntityObject)o, true, indexer);
                }
            }
            indexer.unIndex(objectToSave);
        }
        catch (RuntimeException e) {
            LOG.error("Unable to index object: {} -- {}", new Object[]{objectToSave, e.getMessage(), e});
        }
    }

    protected void unIndexEntity(T objectToSave, boolean unindexDependents) {
        this.unIndex((EntityObject)objectToSave, unindexDependents);
    }

    @Deprecated
    protected List findNamedQuery(String queryName) {
        return this.findNamedQueryStringParams(queryName, Cacheability.CACHEABLE, -1, -1, new Object[0]);
    }

    protected List findNamedQuery(String queryName, Cacheability cacheability) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, -1, new Object[0]);
    }

    protected List findNamedQuery(String queryName, Cacheability cacheability, int maxResultCount) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, maxResultCount, new Object[0]);
    }

    protected List findNamedQueryStringParam(String queryName, String paramName, Object paramValue) {
        return this.findNamedQueryStringParams(queryName, Cacheability.CACHEABLE, -1, -1, paramName, paramValue);
    }

    protected List findNamedQueryStringParam(String queryName, String paramName, Object paramValue, Cacheability cacheability) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, -1, paramName, paramValue);
    }

    protected List findNamedQueryStringParam(String queryName, String paramName, Object paramValue, Cacheability cacheability, int maxResultCount) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, maxResultCount, paramName, paramValue);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value) {
        return this.findNamedQueryStringParams(queryName, Cacheability.CACHEABLE, -1, -1, paramName, paramValue, param2Name, param2Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, Cacheability cacheability) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, -1, paramName, paramValue, param2Name, param2Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, Cacheability cacheability, int maxResultCount) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, maxResultCount, paramName, paramValue, param2Name, param2Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, String param3Name, Object param3Value) {
        return this.findNamedQueryStringParams(queryName, Cacheability.CACHEABLE, -1, -1, paramName, paramValue, param2Name, param2Value, param3Name, param3Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, String param3Name, Object param3Value, Cacheability cacheability) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, -1, paramName, paramValue, param2Name, param2Value, param3Name, param3Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, String param3Name, Object param3Value, Cacheability cacheability, int maxResultCount) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, maxResultCount, paramName, paramValue, param2Name, param2Value, param3Name, param3Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, String param3Name, Object param3Value, String param4Name, Object param4Value) {
        return this.findNamedQueryStringParams(queryName, Cacheability.CACHEABLE, -1, -1, paramName, paramValue, param2Name, param2Value, param3Name, param3Value, param4Name, param4Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, String param3Name, Object param3Value, String param4Name, Object param4Value, Cacheability cacheability) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, -1, paramName, paramValue, param2Name, param2Value, param3Name, param3Value, param4Name, param4Value);
    }

    protected List findNamedQueryStringParams(String queryName, String paramName, Object paramValue, String param2Name, Object param2Value, String param3Name, Object param3Value, String param4Name, Object param4Value, Cacheability cacheability, int maxResultCount) {
        return this.findNamedQueryStringParams(queryName, cacheability, -1, maxResultCount, paramName, paramValue, param2Name, param2Value, param3Name, param3Value, param4Name, param4Value);
    }

    @Deprecated
    protected List findNamedQueryStringParams(String queryName, boolean cacheable, int offset, int maxResultCount, Object ... paramNamesAndValues) {
        return this.findNamedQueryStringParams(queryName, Cacheability.fromBoolean(cacheable), offset, maxResultCount, paramNamesAndValues);
    }

    protected List findNamedQueryStringParams(String queryName, Cacheability cacheability, int offset, int maxResultCount, Object ... paramNamesAndValues) {
        if (paramNamesAndValues.length % 2 == 1) {
            throw new IllegalArgumentException("There must be an even number of parameter names and values.");
        }
        return (List)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Query queryObject = session.getNamedQuery(queryName);
            queryObject.setCacheable(cacheability == Cacheability.CACHEABLE);
            HibernateObjectDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            for (int i = 0; i < paramNamesAndValues.length; i += 2) {
                String paramName = (String)paramNamesAndValues[i];
                if (paramName == null) {
                    throw new IllegalArgumentException("Parameter names cannot be null.");
                }
                Object paramValue = paramNamesAndValues[i + 1];
                if (paramValue instanceof Collection) {
                    Collection collection = (Collection)paramValue;
                    if (collection.isEmpty()) {
                        throw new IllegalArgumentException("Encountered empty collection for parameter: " + paramName + ". Hibernate does not support empty brackets after the sql 'in' clause.");
                    }
                    queryObject.setParameterList(paramName, collection);
                    continue;
                }
                queryObject.setParameter(paramName, paramValue);
            }
            this.limitQuery(queryObject, offset, maxResultCount);
            return queryObject.list();
        });
    }

    private void limitQuery(Query queryObject, int offset, int maxResultCount) {
        if (offset != -1) {
            queryObject.setFirstResult(offset);
        }
        if (maxResultCount != -1) {
            queryObject.setMaxResults(maxResultCount);
        }
    }

    @Deprecated
    protected int getCountResult(@Nullable List<Number> results) {
        return DataAccessUtils.intResult(results);
    }

    @Deprecated
    protected <T extends EntityObject> @Nullable T findSingleObject(List<T> results) {
        if (results == null || results.isEmpty()) {
            return null;
        }
        EntityObject result = (EntityObject)results.get(0);
        if (results.size() != 1) {
            LOG.error("Uh oh - found more than one object when single object requested: " + results);
            for (EntityObject entity : results) {
                if (entity.getId() >= result.getId()) continue;
                result = entity;
            }
        }
        return (T)result;
    }

    protected <E extends EntityObject> E uniqueResult(List<E> results) throws NonUniqueResultException {
        if (results == null || results.isEmpty()) {
            return null;
        }
        Iterator<E> iterator = results.iterator();
        EntityObject firstResult = (EntityObject)iterator.next();
        while (iterator.hasNext()) {
            EntityObject nextResult = (EntityObject)iterator.next();
            if (firstResult == nextResult) continue;
            throw new NonUniqueResultException(results.size());
        }
        return (E)firstResult;
    }

    protected final void checkDaoConfig() {
        if (this.hibernateTemplate == null) {
            throw new IllegalArgumentException("'sessionFactory' or 'hibernateTemplate' is required");
        }
    }

    protected static void applyTransactionTimeout(Query query, SessionFactory sessionFactory) {
        Integer timeout = HibernateObjectDao.getTransactionTimeout(sessionFactory);
        if (timeout != null) {
            query.setTimeout(timeout.intValue());
        }
    }

    protected static void applyTransactionTimeout(Criteria criteria, SessionFactory sessionFactory) {
        Integer timeout = HibernateObjectDao.getTransactionTimeout(sessionFactory);
        if (timeout != null) {
            criteria.setTimeout(timeout.intValue());
        }
    }

    private static Integer getTransactionTimeout(SessionFactory sessionFactory) {
        EntityManagerHolder entityManagerHolder;
        if (TransactionSynchronizationManager.hasResource((Object)sessionFactory) && (entityManagerHolder = (EntityManagerHolder)TransactionSynchronizationManager.getResource((Object)sessionFactory)) != null && entityManagerHolder.hasTimeout()) {
            return entityManagerHolder.getTimeToLiveInSeconds();
        }
        return null;
    }

    public static enum Cacheability {
        CACHEABLE,
        NOT_CACHEABLE;


        public static Cacheability fromBoolean(boolean flag) {
            return flag ? CACHEABLE : NOT_CACHEABLE;
        }
    }
}

