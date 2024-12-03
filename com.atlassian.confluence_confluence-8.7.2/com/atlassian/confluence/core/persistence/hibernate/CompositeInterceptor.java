/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.CallbackException
 *  org.hibernate.EntityMode
 *  org.hibernate.Interceptor
 *  org.hibernate.Transaction
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.core.persistence.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

public class CompositeInterceptor
implements Interceptor {
    private Interceptor[] delegates;

    public CompositeInterceptor(List<Interceptor> delegates) {
        this.delegates = delegates.toArray(new Interceptor[0]);
    }

    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        boolean result = false;
        for (Interceptor delegate : this.delegates) {
            result |= delegate.onLoad(entity, id, state, propertyNames, types);
        }
        return result;
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        boolean result = false;
        for (Interceptor interceptor : this.delegates) {
            result |= interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        }
        return result;
    }

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        boolean result = false;
        for (Interceptor delegate : this.delegates) {
            result |= delegate.onSave(entity, id, state, propertyNames, types);
        }
        return result;
    }

    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            delegate.onDelete(entity, id, state, propertyNames, types);
        }
    }

    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            delegate.onCollectionRecreate(collection, key);
        }
    }

    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            delegate.onCollectionRemove(collection, key);
        }
    }

    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            delegate.onCollectionUpdate(collection, key);
        }
    }

    public void preFlush(Iterator entities) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            delegate.preFlush(entities);
        }
    }

    public void postFlush(Iterator entities) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            delegate.postFlush(entities);
        }
    }

    public Boolean isTransient(Object entity) {
        for (Interceptor delegate : this.delegates) {
            Boolean result = delegate.isTransient(entity);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        for (Interceptor delegate : this.delegates) {
            int[] dirty = delegate.findDirty(entity, id, currentState, previousState, propertyNames, types);
            if (dirty == null) continue;
            return dirty;
        }
        return null;
    }

    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            Object obj = delegate.instantiate(entityName, entityMode, id);
            if (obj == null) continue;
            return obj;
        }
        return null;
    }

    public String getEntityName(Object object) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            String entityName = delegate.getEntityName(object);
            if (entityName == null) continue;
            return entityName;
        }
        return null;
    }

    public Object getEntity(String entityName, Serializable id) throws CallbackException {
        for (Interceptor delegate : this.delegates) {
            Object entity = delegate.getEntity(entityName, id);
            if (entity == null) continue;
            return entity;
        }
        return null;
    }

    public void afterTransactionBegin(Transaction tx) {
        for (Interceptor delegate : this.delegates) {
            delegate.afterTransactionBegin(tx);
        }
    }

    public void beforeTransactionCompletion(Transaction tx) {
        for (Interceptor delegate : this.delegates) {
            delegate.beforeTransactionCompletion(tx);
        }
    }

    public void afterTransactionCompletion(Transaction tx) {
        for (Interceptor delegate : this.delegates) {
            delegate.afterTransactionCompletion(tx);
        }
    }

    public String onPrepareStatement(String sql) {
        return null;
    }
}

