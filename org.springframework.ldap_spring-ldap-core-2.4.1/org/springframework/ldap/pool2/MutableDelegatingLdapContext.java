/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.KeyedObjectPool
 */
package org.springframework.ldap.pool2;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool2.KeyedObjectPool;
import org.springframework.ldap.pool2.DelegatingLdapContext;
import org.springframework.ldap.pool2.DirContextType;

public class MutableDelegatingLdapContext
extends DelegatingLdapContext {
    public MutableDelegatingLdapContext(KeyedObjectPool keyedObjectPool, LdapContext delegateLdapContext, DirContextType dirContextType) {
        super((KeyedObjectPool<Object, Object>)keyedObjectPool, delegateLdapContext, dirContextType);
    }

    @Override
    public void setRequestControls(Control[] requestControls) throws NamingException {
        this.assertOpen();
        this.getDelegateLdapContext().setRequestControls(requestControls);
    }
}

