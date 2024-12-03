/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Criteria
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.AllPersistentObjectsHandlesLoader;
import com.atlassian.confluence.core.persistence.hibernate.AllPersistentObjectsLoader;
import com.atlassian.confluence.core.persistence.hibernate.ExporterAnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.core.persistence.EntityRemover;
import com.atlassian.core.util.ClassLoaderUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

@Deprecated
public class HibernateAnyTypeDao
implements ExporterAnyTypeDao,
EntityRemover {
    private static final Logger log = LoggerFactory.getLogger(HibernateAnyTypeDao.class);
    private HibernateTemplate hibernateTemplate;
    private Set excludedClassesForRetrievingAllObjects;

    @Override
    public @Nullable Object findByHandle(Handle handle) {
        if (handle instanceof HibernateHandle) {
            HibernateHandle hibernateHandle = (HibernateHandle)handle;
            try {
                return this.getByIdAndType(hibernateHandle.getId(), ClassLoaderUtils.loadClass((String)hibernateHandle.getClassName(), this.getClass()));
            }
            catch (ClassNotFoundException e) {
                log.warn("HibernateObjectDao.findByHandle can not resolve handles of type: " + handle.getClass().getName());
            }
        } else if (handle instanceof TransientHibernateHandle) {
            return this.hibernateTemplate.execute(((TransientHibernateHandle)handle)::get);
        }
        log.warn("HibernateObjectDao.findByHandle can not resolve handles of type: " + handle.getClass().getName());
        return null;
    }

    @Override
    public Object getByIdAndType(long id, Class type) {
        Class lookupType = ContentEntityObject.class.isAssignableFrom(type) ? ContentEntityObject.class : type;
        return this.hibernateTemplate.execute(session -> session.get(lookupType, (Serializable)Long.valueOf(id)));
    }

    @Override
    public List findByIdsAndClassName(List<Long> ids, String className) {
        if (ids == null) {
            throw new IllegalArgumentException("ids is required.");
        }
        if (className == null) {
            throw new IllegalArgumentException("className is required.");
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            session.clear();
            return session.createQuery("from " + className + " s where s.id = " + StringUtils.join((Iterable)ids, (String)" or s.id = ")).list();
        });
    }

    @Override
    public List findAllPersistentObjects() {
        return (List)this.hibernateTemplate.execute(session -> new AllPersistentObjectsLoader().doInHibernate(session, this.excludedClassesForRetrievingAllObjects));
    }

    @Override
    public List<Handle> findAllPersistentObjectsHandles() {
        return this.findAllPersistentObjectsHandlesUntyped(Collections.emptyList());
    }

    @Override
    public List<TransientHibernateHandle> findAllPersistentObjectsHibernateHandles(Collection<Class<?>> excludedInterfaces) {
        return this.findAllPersistentObjectsHandlesUntyped(excludedInterfaces);
    }

    private <T extends Handle> List<T> findAllPersistentObjectsHandlesUntyped(Collection<Class<?>> excludedInterfaces) {
        return (List)this.hibernateTemplate.execute(session -> new AllPersistentObjectsHandlesLoader().doInHibernate(session, this.excludedClassesForRetrievingAllObjects, excludedInterfaces));
    }

    public Class getPersistentClass() {
        return Object.class;
    }

    @Override
    public <T> int removeAllPersistentObjectsByType(Class<T> type) {
        return (Integer)this.hibernateTemplate.execute(session -> {
            Criteria criteria = session.createCriteria(type);
            criteria.setCacheable(false);
            List results = criteria.list();
            int count = results.size();
            for (Object result : results) {
                session.delete(result);
            }
            return count;
        });
    }

    public void setExcludedClassesForRetrievingAllObjects(Set excludedClassesForRetrievingAllObjects) {
        this.excludedClassesForRetrievingAllObjects = excludedClassesForRetrievingAllObjects;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
}

