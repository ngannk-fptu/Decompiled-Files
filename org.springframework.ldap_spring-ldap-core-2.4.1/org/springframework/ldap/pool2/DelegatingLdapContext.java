/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.KeyedObjectPool
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.pool2;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool2.KeyedObjectPool;
import org.springframework.ldap.pool2.DelegatingDirContext;
import org.springframework.ldap.pool2.DirContextType;
import org.springframework.util.Assert;

public class DelegatingLdapContext
extends DelegatingDirContext
implements LdapContext {
    private LdapContext delegateLdapContext;

    public DelegatingLdapContext(KeyedObjectPool<Object, Object> keyedObjectPool, LdapContext delegateLdapContext, DirContextType dirContextType) {
        super(keyedObjectPool, delegateLdapContext, dirContextType);
        Assert.notNull((Object)delegateLdapContext, (String)"delegateLdapContext may not be null");
        this.delegateLdapContext = delegateLdapContext;
    }

    public LdapContext getDelegateLdapContext() {
        return this.delegateLdapContext;
    }

    @Override
    public DirContext getDelegateDirContext() {
        return this.getDelegateLdapContext();
    }

    public LdapContext getInnermostDelegateLdapContext() {
        LdapContext delegateLdapContext = this.getDelegateLdapContext();
        if (delegateLdapContext instanceof DelegatingLdapContext) {
            return ((DelegatingLdapContext)delegateLdapContext).getInnermostDelegateLdapContext();
        }
        return delegateLdapContext;
    }

    @Override
    protected void assertOpen() throws NamingException {
        if (this.delegateLdapContext == null) {
            throw new NamingException("LdapContext is closed.");
        }
        super.assertOpen();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LdapContext)) {
            return false;
        }
        LdapContext thisLdapContext = this.getInnermostDelegateLdapContext();
        LdapContext otherLdapContext = (LdapContext)obj;
        if (otherLdapContext instanceof DelegatingLdapContext) {
            otherLdapContext = ((DelegatingLdapContext)otherLdapContext).getInnermostDelegateLdapContext();
        }
        return thisLdapContext == otherLdapContext || thisLdapContext != null && thisLdapContext.equals(otherLdapContext);
    }

    @Override
    public int hashCode() {
        LdapContext context = this.getInnermostDelegateLdapContext();
        return context != null ? context.hashCode() : 0;
    }

    @Override
    public String toString() {
        LdapContext context = this.getInnermostDelegateLdapContext();
        return context != null ? context.toString() : "LdapContext is closed";
    }

    @Override
    public ExtendedResponse extendedOperation(ExtendedRequest request) throws NamingException {
        this.assertOpen();
        return this.getDelegateLdapContext().extendedOperation(request);
    }

    @Override
    public Control[] getConnectControls() throws NamingException {
        this.assertOpen();
        return this.getDelegateLdapContext().getConnectControls();
    }

    @Override
    public Control[] getRequestControls() throws NamingException {
        this.assertOpen();
        return this.getDelegateLdapContext().getRequestControls();
    }

    @Override
    public Control[] getResponseControls() throws NamingException {
        this.assertOpen();
        return this.getDelegateLdapContext().getResponseControls();
    }

    @Override
    public LdapContext newInstance(Control[] requestControls) throws NamingException {
        throw new UnsupportedOperationException("Cannot call newInstance on a pooled context");
    }

    @Override
    public void reconnect(Control[] connCtls) throws NamingException {
        throw new UnsupportedOperationException("Cannot call reconnect on a pooled context");
    }

    @Override
    public void setRequestControls(Control[] requestControls) throws NamingException {
        throw new UnsupportedOperationException("Cannot call setRequestControls on a pooled context");
    }

    @Override
    public void close() throws NamingException {
        if (this.delegateLdapContext == null) {
            return;
        }
        super.close();
        this.delegateLdapContext = null;
    }
}

