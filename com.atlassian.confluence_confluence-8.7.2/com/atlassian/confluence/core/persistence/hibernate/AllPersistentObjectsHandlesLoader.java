/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.persister.entity.EntityPersister
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
final class AllPersistentObjectsHandlesLoader {
    private static final Logger log = LoggerFactory.getLogger(AllPersistentObjectsHandlesLoader.class);

    AllPersistentObjectsHandlesLoader() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<TransientHibernateHandle> doInHibernate(Session session, Set excludedClassesForRetrievingAllObjects, Collection<Class<?>> excludedInterfaces) throws HibernateException {
        ArrayList<TransientHibernateHandle> handles = new ArrayList<TransientHibernateHandle>();
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)session.getSessionFactory();
        Transaction tx = session.getTransaction().isActive() ? session.getTransaction() : session.beginTransaction();
        try {
            for (String entityName : sessionFactory.getMetamodel().getAllEntityNames()) {
                EntityPersister persister = sessionFactory.getMetamodel().entityPersister(entityName);
                Class clazz = persister.getMappedClass();
                if (excludedClassesForRetrievingAllObjects != null && excludedClassesForRetrievingAllObjects.contains(clazz.getName()) || Modifier.isAbstract(clazz.getModifiers())) continue;
                if (excludedInterfaces.stream().anyMatch(iface -> iface.isAssignableFrom(clazz))) {
                    log.debug("Excluding class '{}' because it implements an excluded interface", (Object)clazz);
                    continue;
                }
                String query = "select id from " + clazz.getName();
                List results = session.createQuery(query).list();
                for (Object obj : results) {
                    Serializable objId = (Serializable)obj;
                    handles.add(TransientHibernateHandle.create(clazz, objId));
                }
            }
        }
        finally {
            tx.commit();
            session.beginTransaction();
        }
        return handles;
    }
}

