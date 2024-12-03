/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.Map;
import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

public interface EntityManagerFactory {
    public EntityManager createEntityManager();

    public EntityManager createEntityManager(Map var1);

    public EntityManager createEntityManager(SynchronizationType var1);

    public EntityManager createEntityManager(SynchronizationType var1, Map var2);

    public CriteriaBuilder getCriteriaBuilder();

    public Metamodel getMetamodel();

    public boolean isOpen();

    public void close();

    public Map<String, Object> getProperties();

    public Cache getCache();

    public PersistenceUnitUtil getPersistenceUnitUtil();

    public void addNamedQuery(String var1, Query var2);

    public <T> T unwrap(Class<T> var1);

    public <T> void addNamedEntityGraph(String var1, EntityGraph<T> var2);
}

