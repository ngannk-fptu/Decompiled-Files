/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.KeyedObjectPool
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.ldap.pool2.factory;

import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool2.KeyedObjectPool;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.ldap.pool2.DelegatingDirContext;
import org.springframework.ldap.pool2.DirContextType;
import org.springframework.ldap.pool2.MutableDelegatingLdapContext;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;

public class MutablePooledContextSource
extends PooledContextSource {
    public MutablePooledContextSource(PoolConfig poolConfig) {
        super(poolConfig);
    }

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
        return new DelegatingDirContext((KeyedObjectPool<Object, Object>)this.keyedObjectPool, dirContext, dirContextType);
    }
}

