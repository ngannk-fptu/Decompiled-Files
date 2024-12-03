/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.RollbackException
 *  javax.transaction.SystemException
 */
package net.sf.ehcache.transaction.xa;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.xa.XAResource;
import net.sf.ehcache.transaction.xa.XAExecutionListener;
import net.sf.ehcache.transaction.xa.XATransactionContext;

public interface EhcacheXAResource
extends XAResource {
    public void addTwoPcExecutionListener(XAExecutionListener var1);

    public String getCacheName();

    public XATransactionContext createTransactionContext() throws SystemException, RollbackException;

    public XATransactionContext getCurrentTransactionContext();
}

