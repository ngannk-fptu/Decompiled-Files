/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedObjectPool
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.pool;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.commons.pool.KeyedObjectPool;
import org.springframework.ldap.core.DirContextProxy;
import org.springframework.ldap.pool.DelegatingContext;
import org.springframework.ldap.pool.DirContextType;
import org.springframework.util.Assert;

public class DelegatingDirContext
extends DelegatingContext
implements DirContext,
DirContextProxy {
    private DirContext delegateDirContext;

    public DelegatingDirContext(KeyedObjectPool keyedObjectPool, DirContext delegateDirContext, DirContextType dirContextType) {
        super(keyedObjectPool, delegateDirContext, dirContextType);
        Assert.notNull((Object)delegateDirContext, (String)"delegateDirContext may not be null");
        this.delegateDirContext = delegateDirContext;
    }

    public DirContext getDelegateDirContext() {
        return this.delegateDirContext;
    }

    @Override
    public Context getDelegateContext() {
        return this.getDelegateDirContext();
    }

    public DirContext getInnermostDelegateDirContext() {
        DirContext delegateDirContext = this.getDelegateDirContext();
        if (delegateDirContext instanceof DelegatingDirContext) {
            return ((DelegatingDirContext)delegateDirContext).getInnermostDelegateDirContext();
        }
        return delegateDirContext;
    }

    @Override
    protected void assertOpen() throws NamingException {
        if (this.delegateDirContext == null) {
            throw new NamingException("DirContext is closed.");
        }
        super.assertOpen();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DirContext)) {
            return false;
        }
        DirContext thisDirContext = this.getInnermostDelegateDirContext();
        DirContext otherDirContext = (DirContext)obj;
        if (otherDirContext instanceof DelegatingDirContext) {
            otherDirContext = ((DelegatingDirContext)otherDirContext).getInnermostDelegateDirContext();
        }
        return thisDirContext == otherDirContext || thisDirContext != null && thisDirContext.equals(otherDirContext);
    }

    @Override
    public int hashCode() {
        DirContext context = this.getInnermostDelegateDirContext();
        return context != null ? context.hashCode() : 0;
    }

    @Override
    public String toString() {
        DirContext context = this.getInnermostDelegateDirContext();
        return context != null ? context.toString() : "DirContext is closed";
    }

    @Override
    public DirContext getTargetContext() {
        return this.getInnermostDelegateDirContext();
    }

    @Override
    public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().bind(name, obj, attrs);
    }

    @Override
    public void bind(String name, Object obj, Attributes attrs) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().bind(name, obj, attrs);
    }

    @Override
    public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException("Cannot call createSubcontext on a pooled context");
    }

    @Override
    public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException("Cannot call createSubcontext on a pooled context");
    }

    @Override
    public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().getAttributes(name, attrIds);
    }

    @Override
    public Attributes getAttributes(Name name) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().getAttributes(name);
    }

    @Override
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().getAttributes(name, attrIds);
    }

    @Override
    public Attributes getAttributes(String name) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().getAttributes(name);
    }

    @Override
    public DirContext getSchema(Name name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call getSchema on a pooled context");
    }

    @Override
    public DirContext getSchema(String name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call getSchema on a pooled context");
    }

    @Override
    public DirContext getSchemaClassDefinition(Name name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call getSchemaClassDefinition on a pooled context");
    }

    @Override
    public DirContext getSchemaClassDefinition(String name) throws NamingException {
        throw new UnsupportedOperationException("Cannot call getSchemaClassDefinition on a pooled context");
    }

    @Override
    public void modifyAttributes(Name name, int modOp, Attributes attrs) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().modifyAttributes(name, modOp, attrs);
    }

    @Override
    public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().modifyAttributes(name, mods);
    }

    @Override
    public void modifyAttributes(String name, int modOp, Attributes attrs) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().modifyAttributes(name, modOp, attrs);
    }

    @Override
    public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().modifyAttributes(name, mods);
    }

    @Override
    public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().rebind(name, obj, attrs);
    }

    @Override
    public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
        this.assertOpen();
        this.getDelegateDirContext().rebind(name, obj, attrs);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, matchingAttributes, attributesToReturn);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, matchingAttributes);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, filterExpr, filterArgs, cons);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, filter, cons);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, matchingAttributes, attributesToReturn);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, matchingAttributes);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, filterExpr, filterArgs, cons);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
        this.assertOpen();
        return this.getDelegateDirContext().search(name, filter, cons);
    }

    @Override
    public void close() throws NamingException {
        if (this.delegateDirContext == null) {
            return;
        }
        super.close();
        this.delegateDirContext = null;
    }
}

