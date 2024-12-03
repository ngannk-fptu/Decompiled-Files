/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.io.IOException;
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;

public interface DavResource {
    public static final String METHODS = "OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";

    public String getComplianceClass();

    public String getSupportedMethods();

    public boolean exists();

    public boolean isCollection();

    public String getDisplayName();

    public DavResourceLocator getLocator();

    public String getResourcePath();

    public String getHref();

    public long getModificationTime();

    public void spool(OutputContext var1) throws IOException;

    public DavPropertyName[] getPropertyNames();

    public DavProperty<?> getProperty(DavPropertyName var1);

    public DavPropertySet getProperties();

    public void setProperty(DavProperty<?> var1) throws DavException;

    public void removeProperty(DavPropertyName var1) throws DavException;

    public MultiStatusResponse alterProperties(List<? extends PropEntry> var1) throws DavException;

    public DavResource getCollection();

    public void addMember(DavResource var1, InputContext var2) throws DavException;

    public DavResourceIterator getMembers();

    public void removeMember(DavResource var1) throws DavException;

    public void move(DavResource var1) throws DavException;

    public void copy(DavResource var1, boolean var2) throws DavException;

    public boolean isLockable(Type var1, Scope var2);

    public boolean hasLock(Type var1, Scope var2);

    public ActiveLock getLock(Type var1, Scope var2);

    public ActiveLock[] getLocks();

    public ActiveLock lock(LockInfo var1) throws DavException;

    public ActiveLock refreshLock(LockInfo var1, String var2) throws DavException;

    public void unlock(String var1) throws DavException;

    public void addLockManager(LockManager var1);

    public DavResourceFactory getFactory();

    public DavSession getSession();
}

