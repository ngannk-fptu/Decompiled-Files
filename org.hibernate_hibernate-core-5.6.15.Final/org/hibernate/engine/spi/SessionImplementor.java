/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.CriteriaUpdate
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.engine.spi;

import java.util.Map;
import java.util.Set;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Selection;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;

public interface SessionImplementor
extends Session,
SharedSessionContractImplementor,
HibernateEntityManagerImplementor {
    @Override
    public SessionFactoryImplementor getSessionFactory();

    public <T> RootGraphImplementor<T> createEntityGraph(Class<T> var1);

    @Override
    public RootGraphImplementor<?> createEntityGraph(String var1);

    @Override
    public RootGraphImplementor<?> getEntityGraph(String var1);

    @Deprecated
    public boolean isFlushBeforeCompletionEnabled();

    public ActionQueue getActionQueue();

    public void forceFlush(EntityEntry var1) throws HibernateException;

    @Override
    public QueryImplementor createQuery(String var1);

    public <T> QueryImplementor<T> createQuery(String var1, Class<T> var2);

    public <T> QueryImplementor<T> createNamedQuery(String var1, Class<T> var2);

    @Override
    public QueryImplementor createNamedQuery(String var1);

    @Override
    public NativeQueryImplementor createNativeQuery(String var1);

    @Override
    public NativeQueryImplementor createNativeQuery(String var1, Class var2);

    @Override
    public NativeQueryImplementor createNativeQuery(String var1, String var2);

    @Override
    public NativeQueryImplementor createSQLQuery(String var1);

    @Override
    public NativeQueryImplementor getNamedNativeQuery(String var1);

    @Override
    public QueryImplementor getNamedQuery(String var1);

    @Override
    public NativeQueryImplementor getNamedSQLQuery(String var1);

    public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> var1);

    @Override
    public QueryImplementor createQuery(CriteriaUpdate var1);

    @Override
    public QueryImplementor createQuery(CriteriaDelete var1);

    @Override
    @Deprecated
    public <T> QueryImplementor<T> createQuery(String var1, Class<T> var2, Selection var3, HibernateEntityManagerImplementor.QueryOptions var4);

    @Deprecated
    public void merge(String var1, Object var2, Map var3) throws HibernateException;

    @Deprecated
    public void persist(String var1, Object var2, Map var3) throws HibernateException;

    @Deprecated
    public void persistOnFlush(String var1, Object var2, Map var3);

    @Deprecated
    public void refresh(String var1, Object var2, Map var3) throws HibernateException;

    @Deprecated
    public void delete(String var1, Object var2, boolean var3, Set var4);

    @Deprecated
    public void removeOrphanBeforeUpdates(String var1, Object var2);
}

