/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

public interface EntityManager {
    public void persist(Object var1);

    public <T> T merge(T var1);

    public void remove(Object var1);

    public <T> T find(Class<T> var1, Object var2);

    public <T> T find(Class<T> var1, Object var2, Map<String, Object> var3);

    public <T> T find(Class<T> var1, Object var2, LockModeType var3);

    public <T> T find(Class<T> var1, Object var2, LockModeType var3, Map<String, Object> var4);

    public <T> T getReference(Class<T> var1, Object var2);

    public void flush();

    public void setFlushMode(FlushModeType var1);

    public FlushModeType getFlushMode();

    public void lock(Object var1, LockModeType var2);

    public void lock(Object var1, LockModeType var2, Map<String, Object> var3);

    public void refresh(Object var1);

    public void refresh(Object var1, Map<String, Object> var2);

    public void refresh(Object var1, LockModeType var2);

    public void refresh(Object var1, LockModeType var2, Map<String, Object> var3);

    public void clear();

    public void detach(Object var1);

    public boolean contains(Object var1);

    public LockModeType getLockMode(Object var1);

    public void setProperty(String var1, Object var2);

    public Map<String, Object> getProperties();

    public Query createQuery(String var1);

    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> var1);

    public Query createQuery(CriteriaUpdate var1);

    public Query createQuery(CriteriaDelete var1);

    public <T> TypedQuery<T> createQuery(String var1, Class<T> var2);

    public Query createNamedQuery(String var1);

    public <T> TypedQuery<T> createNamedQuery(String var1, Class<T> var2);

    public Query createNativeQuery(String var1);

    public Query createNativeQuery(String var1, Class var2);

    public Query createNativeQuery(String var1, String var2);

    public StoredProcedureQuery createNamedStoredProcedureQuery(String var1);

    public StoredProcedureQuery createStoredProcedureQuery(String var1);

    public StoredProcedureQuery createStoredProcedureQuery(String var1, Class ... var2);

    public StoredProcedureQuery createStoredProcedureQuery(String var1, String ... var2);

    public void joinTransaction();

    public boolean isJoinedToTransaction();

    public <T> T unwrap(Class<T> var1);

    public Object getDelegate();

    public void close();

    public boolean isOpen();

    public EntityTransaction getTransaction();

    public EntityManagerFactory getEntityManagerFactory();

    public CriteriaBuilder getCriteriaBuilder();

    public Metamodel getMetamodel();

    public <T> EntityGraph<T> createEntityGraph(Class<T> var1);

    public EntityGraph<?> createEntityGraph(String var1);

    public EntityGraph<?> getEntityGraph(String var1);

    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> var1);
}

