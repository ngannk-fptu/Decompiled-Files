/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManager
 *  javax.persistence.FlushModeType
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.CriteriaUpdate
 */
package org.hibernate;

import java.io.Closeable;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import org.hibernate.CacheMode;
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
import org.hibernate.SessionEventListener;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SharedSessionContract;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.graph.RootGraph;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.query.NativeQuery;
import org.hibernate.stat.SessionStatistics;

public interface Session
extends SharedSessionContract,
EntityManager,
HibernateEntityManager,
AutoCloseable,
Closeable {
    public SharedSessionBuilder sessionWithOptions();

    public void flush() throws HibernateException;

    @Deprecated
    public void setFlushMode(FlushMode var1);

    public FlushModeType getFlushMode();

    public void setHibernateFlushMode(FlushMode var1);

    public FlushMode getHibernateFlushMode();

    public void setCacheMode(CacheMode var1);

    public CacheMode getCacheMode();

    public SessionFactory getSessionFactory();

    public void cancelQuery() throws HibernateException;

    public boolean isDirty() throws HibernateException;

    public boolean isDefaultReadOnly();

    public void setDefaultReadOnly(boolean var1);

    public Serializable getIdentifier(Object var1);

    public boolean contains(String var1, Object var2);

    public void evict(Object var1);

    public <T> T load(Class<T> var1, Serializable var2, LockMode var3);

    public <T> T load(Class<T> var1, Serializable var2, LockOptions var3);

    public Object load(String var1, Serializable var2, LockMode var3);

    public Object load(String var1, Serializable var2, LockOptions var3);

    public <T> T load(Class<T> var1, Serializable var2);

    public Object load(String var1, Serializable var2);

    public void load(Object var1, Serializable var2);

    public void replicate(Object var1, ReplicationMode var2);

    public void replicate(String var1, Object var2, ReplicationMode var3);

    public Serializable save(Object var1);

    public Serializable save(String var1, Object var2);

    public void saveOrUpdate(Object var1);

    public void saveOrUpdate(String var1, Object var2);

    public void update(Object var1);

    public void update(String var1, Object var2);

    public Object merge(Object var1);

    public Object merge(String var1, Object var2);

    public void persist(Object var1);

    public void persist(String var1, Object var2);

    public void delete(Object var1);

    public void delete(String var1, Object var2);

    public void lock(Object var1, LockMode var2);

    public void lock(String var1, Object var2, LockMode var3);

    public LockRequest buildLockRequest(LockOptions var1);

    public void refresh(Object var1);

    public void refresh(String var1, Object var2);

    public void refresh(Object var1, LockMode var2);

    public void refresh(Object var1, LockOptions var2);

    public void refresh(String var1, Object var2, LockOptions var3);

    public LockMode getCurrentLockMode(Object var1);

    @Deprecated
    public Query createFilter(Object var1, String var2);

    public void clear();

    public <T> T get(Class<T> var1, Serializable var2);

    public <T> T get(Class<T> var1, Serializable var2, LockMode var3);

    public <T> T get(Class<T> var1, Serializable var2, LockOptions var3);

    public Object get(String var1, Serializable var2);

    public Object get(String var1, Serializable var2, LockMode var3);

    public Object get(String var1, Serializable var2, LockOptions var3);

    public String getEntityName(Object var1);

    default public <T> T getReference(T object) {
        throw new IllegalStateException("getReference(Object) is not implemented in " + this.getClass());
    }

    public IdentifierLoadAccess byId(String var1);

    public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> var1);

    public MultiIdentifierLoadAccess byMultipleIds(String var1);

    public <T> IdentifierLoadAccess<T> byId(Class<T> var1);

    public NaturalIdLoadAccess byNaturalId(String var1);

    public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> var1);

    public SimpleNaturalIdLoadAccess bySimpleNaturalId(String var1);

    public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> var1);

    public Filter enableFilter(String var1);

    public Filter getEnabledFilter(String var1);

    public void disableFilter(String var1);

    public SessionStatistics getStatistics();

    public boolean isReadOnly(Object var1);

    public void setReadOnly(Object var1, boolean var2);

    public <T> RootGraph<T> createEntityGraph(Class<T> var1);

    public RootGraph<?> createEntityGraph(String var1);

    public RootGraph<?> getEntityGraph(String var1);

    default public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return this.getSessionFactory().findEntityGraphsByType(entityClass);
    }

    public Connection disconnect();

    public void reconnect(Connection var1);

    public boolean isFetchProfileEnabled(String var1) throws UnknownProfileException;

    public void enableFetchProfile(String var1) throws UnknownProfileException;

    public void disableFetchProfile(String var1) throws UnknownProfileException;

    public TypeHelper getTypeHelper();

    public LobHelper getLobHelper();

    public void addEventListeners(SessionEventListener ... var1);

    public <T> org.hibernate.query.Query<T> createQuery(String var1, Class<T> var2);

    @Override
    public <T> org.hibernate.query.Query<T> createQuery(CriteriaQuery<T> var1);

    @Override
    public org.hibernate.query.Query createQuery(CriteriaUpdate var1);

    @Override
    public org.hibernate.query.Query createQuery(CriteriaDelete var1);

    public <T> org.hibernate.query.Query<T> createNamedQuery(String var1, Class<T> var2);

    @Override
    @Deprecated
    public NativeQuery createSQLQuery(String var1);

    public static interface LockRequest {
        public static final int PESSIMISTIC_NO_WAIT = 0;
        public static final int PESSIMISTIC_WAIT_FOREVER = -1;

        public LockMode getLockMode();

        public LockRequest setLockMode(LockMode var1);

        public int getTimeOut();

        public LockRequest setTimeOut(int var1);

        public boolean getScope();

        public LockRequest setScope(boolean var1);

        public void lock(String var1, Object var2);

        public void lock(Object var1);
    }
}

