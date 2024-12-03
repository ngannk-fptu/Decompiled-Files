/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

public class EmptyInterceptor
implements Interceptor,
Serializable {
    public static final Interceptor INSTANCE = new EmptyInterceptor();

    protected EmptyInterceptor() {
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return false;
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        return false;
    }

    @Override
    public void postFlush(Iterator entities) {
    }

    @Override
    public void preFlush(Iterator entities) {
    }

    @Override
    public Boolean isTransient(Object entity) {
        return null;
    }

    @Override
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
        return null;
    }

    @Override
    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return null;
    }

    @Override
    public String getEntityName(Object object) {
        return null;
    }

    @Override
    public Object getEntity(String entityName, Serializable id) {
        return null;
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
    }

    @Override
    public String onPrepareStatement(String sql) {
        return sql;
    }

    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
    }
}

