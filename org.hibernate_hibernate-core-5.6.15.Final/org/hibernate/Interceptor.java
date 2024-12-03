/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

public interface Interceptor {
    public boolean onLoad(Object var1, Serializable var2, Object[] var3, String[] var4, Type[] var5) throws CallbackException;

    public boolean onFlushDirty(Object var1, Serializable var2, Object[] var3, Object[] var4, String[] var5, Type[] var6) throws CallbackException;

    public boolean onSave(Object var1, Serializable var2, Object[] var3, String[] var4, Type[] var5) throws CallbackException;

    public void onDelete(Object var1, Serializable var2, Object[] var3, String[] var4, Type[] var5) throws CallbackException;

    public void onCollectionRecreate(Object var1, Serializable var2) throws CallbackException;

    public void onCollectionRemove(Object var1, Serializable var2) throws CallbackException;

    public void onCollectionUpdate(Object var1, Serializable var2) throws CallbackException;

    public void preFlush(Iterator var1) throws CallbackException;

    public void postFlush(Iterator var1) throws CallbackException;

    public Boolean isTransient(Object var1);

    public int[] findDirty(Object var1, Serializable var2, Object[] var3, Object[] var4, String[] var5, Type[] var6);

    public Object instantiate(String var1, EntityMode var2, Serializable var3) throws CallbackException;

    public String getEntityName(Object var1) throws CallbackException;

    public Object getEntity(String var1, Serializable var2) throws CallbackException;

    public void afterTransactionBegin(Transaction var1);

    public void beforeTransactionCompletion(Transaction var1);

    public void afterTransactionCompletion(Transaction var1);

    @Deprecated
    public String onPrepareStatement(String var1);
}

