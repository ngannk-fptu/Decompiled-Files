/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedObjectPool
 */
package org.springframework.ldap.pool;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool.KeyedObjectPool;
import org.springframework.ldap.pool.DelegatingLdapContext;
import org.springframework.ldap.pool.DirContextType;

public class MutableDelegatingLdapContext
extends DelegatingLdapContext {
    public MutableDelegatingLdapContext(KeyedObjectPool keyedObjectPool, LdapContext delegateLdapContext, DirContextType dirContextType) {
        super(keyedObjectPool, delegateLdapContext, dirContextType);
    }

    @Override
    public void setRequestControls(Control[] requestControls) throws NamingException {
        this.assertOpen();
        this.getDelegateLdapContext().setRequestControls(requestControls);
    }
}

