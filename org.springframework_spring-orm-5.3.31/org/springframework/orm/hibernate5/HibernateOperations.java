/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Filter
 *  org.hibernate.LockMode
 *  org.hibernate.ReplicationMode
 *  org.hibernate.criterion.DetachedCriteria
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.hibernate5;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Filter;
import org.hibernate.LockMode;
import org.hibernate.ReplicationMode;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.HibernateCallback;

public interface HibernateOperations {
    @Nullable
    public <T> T execute(HibernateCallback<T> var1) throws DataAccessException;

    @Nullable
    public <T> T get(Class<T> var1, Serializable var2) throws DataAccessException;

    @Nullable
    public <T> T get(Class<T> var1, Serializable var2, LockMode var3) throws DataAccessException;

    @Nullable
    public Object get(String var1, Serializable var2) throws DataAccessException;

    @Nullable
    public Object get(String var1, Serializable var2, LockMode var3) throws DataAccessException;

    public <T> T load(Class<T> var1, Serializable var2) throws DataAccessException;

    public <T> T load(Class<T> var1, Serializable var2, LockMode var3) throws DataAccessException;

    public Object load(String var1, Serializable var2) throws DataAccessException;

    public Object load(String var1, Serializable var2, LockMode var3) throws DataAccessException;

    public <T> List<T> loadAll(Class<T> var1) throws DataAccessException;

    public void load(Object var1, Serializable var2) throws DataAccessException;

    public void refresh(Object var1) throws DataAccessException;

    public void refresh(Object var1, LockMode var2) throws DataAccessException;

    public boolean contains(Object var1) throws DataAccessException;

    public void evict(Object var1) throws DataAccessException;

    public void initialize(Object var1) throws DataAccessException;

    public Filter enableFilter(String var1) throws IllegalStateException;

    public void lock(Object var1, LockMode var2) throws DataAccessException;

    public void lock(String var1, Object var2, LockMode var3) throws DataAccessException;

    public Serializable save(Object var1) throws DataAccessException;

    public Serializable save(String var1, Object var2) throws DataAccessException;

    public void update(Object var1) throws DataAccessException;

    public void update(Object var1, LockMode var2) throws DataAccessException;

    public void update(String var1, Object var2) throws DataAccessException;

    public void update(String var1, Object var2, LockMode var3) throws DataAccessException;

    public void saveOrUpdate(Object var1) throws DataAccessException;

    public void saveOrUpdate(String var1, Object var2) throws DataAccessException;

    public void replicate(Object var1, ReplicationMode var2) throws DataAccessException;

    public void replicate(String var1, Object var2, ReplicationMode var3) throws DataAccessException;

    public void persist(Object var1) throws DataAccessException;

    public void persist(String var1, Object var2) throws DataAccessException;

    public <T> T merge(T var1) throws DataAccessException;

    public <T> T merge(String var1, T var2) throws DataAccessException;

    public void delete(Object var1) throws DataAccessException;

    public void delete(Object var1, LockMode var2) throws DataAccessException;

    public void delete(String var1, Object var2) throws DataAccessException;

    public void delete(String var1, Object var2, LockMode var3) throws DataAccessException;

    public void deleteAll(Collection<?> var1) throws DataAccessException;

    public void flush() throws DataAccessException;

    public void clear() throws DataAccessException;

    public List<?> findByCriteria(DetachedCriteria var1) throws DataAccessException;

    public List<?> findByCriteria(DetachedCriteria var1, int var2, int var3) throws DataAccessException;

    public <T> List<T> findByExample(T var1) throws DataAccessException;

    public <T> List<T> findByExample(String var1, T var2) throws DataAccessException;

    public <T> List<T> findByExample(T var1, int var2, int var3) throws DataAccessException;

    public <T> List<T> findByExample(String var1, T var2, int var3, int var4) throws DataAccessException;

    @Deprecated
    public List<?> find(String var1, Object ... var2) throws DataAccessException;

    @Deprecated
    public List<?> findByNamedParam(String var1, String var2, Object var3) throws DataAccessException;

    @Deprecated
    public List<?> findByNamedParam(String var1, String[] var2, Object[] var3) throws DataAccessException;

    @Deprecated
    public List<?> findByValueBean(String var1, Object var2) throws DataAccessException;

    @Deprecated
    public List<?> findByNamedQuery(String var1, Object ... var2) throws DataAccessException;

    @Deprecated
    public List<?> findByNamedQueryAndNamedParam(String var1, String var2, Object var3) throws DataAccessException;

    @Deprecated
    public List<?> findByNamedQueryAndNamedParam(String var1, String[] var2, Object[] var3) throws DataAccessException;

    @Deprecated
    public List<?> findByNamedQueryAndValueBean(String var1, Object var2) throws DataAccessException;

    @Deprecated
    public Iterator<?> iterate(String var1, Object ... var2) throws DataAccessException;

    @Deprecated
    public void closeIterator(Iterator<?> var1) throws DataAccessException;

    @Deprecated
    public int bulkUpdate(String var1, Object ... var2) throws DataAccessException;
}

