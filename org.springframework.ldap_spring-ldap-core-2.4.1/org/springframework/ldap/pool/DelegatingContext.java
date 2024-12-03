/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedObjectPool
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.pool;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.commons.pool.KeyedObjectPool;
import org.springframework.ldap.pool.DirContextType;
import org.springframework.ldap.pool.FailureAwareContext;
import org.springframework.util.Assert;

public class DelegatingContext
implements Context {
    private KeyedObjectPool keyedObjectPool;
    private Context delegateContext;
    private final DirContextType dirContextType;

    public DelegatingContext(KeyedObjectPool keyedObjectPool, Context delegateContext, DirContextType dirContextType) {
        Assert.notNull((Object)keyedObjectPool, (String)"keyedObjectPool may not be null");
        Assert.notNull((Object)delegateContext, (String)"delegateContext may not be null");
        Assert.notNull((Object)dirContextType, (String)"dirContextType may not be null");
        this.keyedObjectPool = keyedObjectPool;
        this.delegateContext = delegateContext;
        this.dirContextType = dirContextType;
    }

    public Context getDelegateContext() {
        return this.delegateContext;
    }

    public Context getInnermostDelegateContext() {
        Context delegateContext = this.getDelegateContext();
        if (delegateContext instanceof DelegatingContext) {
            return ((DelegatingContext)delegateContext).getInnermostDelegateContext();
        }
        return delegateContext;
    }

    protected void assertOpen() throws NamingException {
        if (this.delegateContext == null) {
            throw new NamingException("Context is closed.");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Context)) {
            return false;
        }
        Context thisContext = this.getInnermostDelegateContext();
        Context otherContext = (Context)obj;
        if (otherContext instanceof DelegatingContext) {
            otherContext = ((DelegatingContext)otherContext).getInnermostDelegateContext();
        }
        return thisContext == otherContext || thisContext != null && thisContext.equals(otherContext);
    }

    public int hashCode() {
        Context context = this.getInnermostDelegateContext();
        return context != null ? context.hashCode() : 0;
    }

    public String toString() {
        Context context = this.getInnermostDelegateContext();
        return context != null ? context.toString() : "Context is closed";
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException("Cannot call addToEnvironment on a pooled context");
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().bind(name, obj);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().bind(name, obj);
    }

    @Override
    public void close() throws NamingException {
        Context context = this.getInnermostDelegateContext();
        if (context == null) {
            return;
        }
        this.delegateContext = null;
        try {
            FailureAwareContext failureAwareContext;
            boolean valid = true;
            if (context instanceof FailureAwareContext && (failureAwareContext = (FailureAwareContext)((Object)context)).hasFailed()) {
                valid = false;
            }
            if (valid) {
                this.keyedObjectPool.returnObject((Object)this.dirContextType, (Object)context);
            } else {
                this.keyedObjectPool.invalidateObject((Object)this.dirContextType, (Object)context);
            }
        }
        catch (Exception e) {
            NamingException namingException = new NamingException("Failed to return delegate Context to pool.");
            namingException.setRootCause(e);
            throw namingException;
        }
        finally {
            this.keyedObjectPool = null;
        }
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().composeName(name, prefix);
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().composeName(name, prefix);
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call createSubcontext on a pooled context");
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call createSubcontext on a pooled context");
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call destroySubcontext on a pooled context");
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call destroySubcontext on a pooled context");
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().getEnvironment();
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().getNameInNamespace();
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().getNameParser(name);
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().getNameParser(name);
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().list(name);
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().list(name);
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().listBindings(name);
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().listBindings(name);
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().lookup(name);
    }

    @Override
    public Object lookup(String name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().lookup(name);
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().lookupLink(name);
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        this.assertOpen();
        return this.getDelegateContext().lookupLink(name);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().rebind(name, obj);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().rebind(name, obj);
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException("Cannot call removeFromEnvironment on a pooled context");
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().rename(oldName, newName);
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().rename(oldName, newName);
    }

    @Override
    public void unbind(Name name) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().unbind(name);
    }

    @Override
    public void unbind(String name) throws NamingException {
        this.assertOpen();
        this.getDelegateContext().unbind(name);
    }
}

