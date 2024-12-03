/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.core.bean.EntityObject
 *  javax.persistence.OptimisticLockException
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.StaleStateException
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.persistence.VersionedObjectDaoInternal;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.core.bean.EntityObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.persistence.OptimisticLockException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.StaleStateException;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VersionedHibernateObjectDao<T extends EntityObject>
extends HibernateObjectDao<T>
implements VersionedObjectDaoInternal<T> {
    private static final Logger log = LoggerFactory.getLogger(VersionedHibernateObjectDao.class);
    protected CacheFactory cacheFactory;
    protected ConfluenceUserDao confluenceUserDao;

    private String generateFindAllQueryString(String sortField, String ... statuses) {
        StringBuilder sb = new StringBuilder();
        sb.append("FROM ").append(this.getPersistentClass().getName()).append(" result");
        if (Versioned.class.isAssignableFrom(this.getPersistentClass())) {
            sb.append(" WHERE result.originalVersion is null");
            if (ConfluenceEntityObject.class.isAssignableFrom(this.getPersistentClass())) {
                sb.append(" AND result.contentStatus in (");
                boolean isFirst = true;
                for (String status : statuses) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        sb.append(',');
                    }
                    sb.append('\'').append(status).append('\'');
                }
                sb.append(')');
            }
        }
        if (sortField != null) {
            sb.append(" ORDER BY LOWER(result.").append(sortField).append(')');
        }
        return sb.toString();
    }

    @Override
    public Iterator<T> findLatestVersionsIterator() {
        return this.findLatestVersionsIterator("current");
    }

    protected Iterator<T> findLatestVersionsIterator(String ... statuses) {
        return this.getHibernateTemplate().iterate(this.generateFindAllQueryString(null, statuses), new Object[0]);
    }

    @Override
    public long findLatestVersionsCount() {
        return this.findLatestVersionsCount("current");
    }

    protected long findLatestVersionsCount(String ... statuses) {
        String originalQueryString = "SELECT COUNT(*) " + this.generateFindAllQueryString(null, statuses);
        List resultsList = this.getHibernateTemplate().find(originalQueryString, new Object[0]);
        if (resultsList.size() == 0) {
            return 0L;
        }
        Number number = (Number)resultsList.get(0);
        return number.longValue();
    }

    @Override
    public @NonNull List<T> findAllSorted(String sortField) {
        String finalQuery = this.generateFindAllQueryString(sortField, "current");
        List result = (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(finalQuery, this.getPersistentClass());
            queryObject.setCacheable(true);
            VersionedHibernateObjectDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        });
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public void saveEntity(T objectToSave, @Nullable T previousVersion) {
        this.save((EntityObject)objectToSave, (EntityObject)previousVersion);
    }

    @Override
    @Deprecated
    public void save(EntityObject objectToSave, EntityObject previousVersion) {
        this.updateModificationData(objectToSave);
        if (objectToSave instanceof Versioned && previousVersion != null) {
            Versioned previousVersioned = (Versioned)previousVersion;
            Versioned updatedVersioned = (Versioned)objectToSave;
            if (updatedVersioned.getVersion() > previousVersioned.getVersion()) {
                throw new StaleObjectStateException("The version of the object to be saved was more than the previous version!");
            }
            previousVersioned.convertToHistoricalVersion();
            previousVersioned.setOriginalVersion(updatedVersioned);
            previousVersioned.applyChildVersioningPolicy(updatedVersioned, this);
            this.getHibernateTemplate().saveOrUpdate((Object)previousVersion);
            updatedVersioned.setVersion(updatedVersioned.getVersion() + 1);
        }
        this.getHibernateTemplate().saveOrUpdate((Object)objectToSave);
        try {
            this.getHibernateTemplate().flush();
        }
        catch (OptimisticLockException | StaleStateException e) {
            throw new StaleObjectStateException(e);
        }
        if (objectToSave instanceof Versioned && previousVersion != null) {
            this.reIndex(previousVersion);
            this.reIndex(objectToSave);
        } else {
            this.index(objectToSave);
        }
    }

    @Override
    protected void updateModificationData(EntityObject objectToSave) {
        if (objectToSave instanceof ConfluenceEntityObject) {
            this.updateConfluenceModificationData((ConfluenceEntityObject)objectToSave);
        }
        super.updateModificationData(objectToSave);
    }

    @Override
    protected void updateEntityModificationData(T objectToSave) {
        this.updateModificationData((EntityObject)objectToSave);
    }

    @Deprecated
    protected List findNamedQueryStringParams(String queryName, boolean cacheable, LimitedRequest limitedRequest, Object ... paramNamesAndValues) {
        return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.fromBoolean(cacheable), limitedRequest, paramNamesAndValues);
    }

    protected List findNamedQueryStringParams(String queryName, HibernateObjectDao.Cacheability cacheability, LimitedRequest limitedRequest, Object ... paramNamesAndValues) {
        int offset = limitedRequest.getCursor() != null ? -1 : limitedRequest.getStart();
        int maxResultCount = limitedRequest.getLimit() + 1;
        return this.findNamedQueryStringParams(queryName, cacheability, offset, maxResultCount, paramNamesAndValues);
    }

    private void updateConfluenceModificationData(ConfluenceEntityObject confluenceEntityObject) {
        try {
            ConfluenceUser lastModifier = AuthenticatedUserThreadLocal.get();
            confluenceEntityObject.setLastModifier(lastModifier);
            if (!confluenceEntityObject.isPersistent() && confluenceEntityObject.getCreator() == null) {
                confluenceEntityObject.setCreator(lastModifier);
            }
        }
        catch (Exception e) {
            log.error("Can not fetch the current user!", (Throwable)e);
        }
    }

    @Deprecated
    public void setCacheFactory(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    @Deprecated
    public void setConfluenceUserDao(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
    }
}

