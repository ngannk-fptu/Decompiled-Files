/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedObjectPool
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.ldap.pool.factory;

import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool.KeyedObjectPool;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.ldap.pool.DelegatingDirContext;
import org.springframework.ldap.pool.DirContextType;
import org.springframework.ldap.pool.MutableDelegatingLdapContext;
import org.springframework.ldap.pool.factory.PoolingContextSource;

public class MutablePoolingContextSource
extends PoolingContextSource {
    @Override
    protected DirContext getContext(DirContextType dirContextType) {
        DirContext dirContext;
        try {
            dirContext = (DirContext)this.keyedObjectPool.borrowObject((Object)dirContextType);
        }
        catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to borrow DirContext from pool.", (Throwable)e);
        }
        if (dirContext instanceof LdapContext) {
            return new MutableDelegatingLdapContext((KeyedObjectPool)this.keyedObjectPool, (LdapContext)dirContext, dirContextType);
        }
        return new DelegatingDirContext((KeyedObjectPool)this.keyedObjectPool, dirContext, dirContextType);
    }
}

