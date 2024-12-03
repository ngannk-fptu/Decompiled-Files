/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockEntry;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.simple.DavResourceImpl;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.SupportedMethodSetProperty;

public abstract class AbstractConfluenceResource
implements DavResource {
    private final DavResourceLocator davResourceLocator;
    private final DavResourceFactory davResourceFactory;
    private LockManager lockManager;
    private final ConfluenceDavSession davSession;
    private DavPropertySet propertySet;

    public AbstractConfluenceResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession) {
        this.davResourceLocator = davResourceLocator;
        this.davResourceFactory = davResourceFactory;
        this.lockManager = lockManager;
        this.davSession = davSession;
    }

    protected abstract long getCreationtTime();

    protected abstract SupportedLock getSupportedLock();

    @Override
    public boolean exists() {
        DavResource parent = this.getCollection();
        return null != parent && parent.exists();
    }

    protected String getParentResourcePath() {
        String[] resourcePathTokens = StringUtils.split((String)this.getResourcePath(), (char)'/');
        if (resourcePathTokens == null) {
            resourcePathTokens = new String[]{};
        }
        StringBuilder parentPathBuffer = new StringBuilder();
        for (int i = 0; i < resourcePathTokens.length - 1; ++i) {
            parentPathBuffer.append('/').append(resourcePathTokens[i]);
        }
        return parentPathBuffer.toString();
    }

    protected String getWorkspaceHref() {
        return this.getLocator().getWorkspacePath();
    }

    @Override
    public DavResource getCollection() {
        try {
            DavResourceLocator locator = this.getLocator();
            DavResourceLocator parentLocator = locator.getFactory().createResourceLocator(Text.escape(locator.getPrefix()), Text.escape(this.getParentResourcePath()));
            return this.getFactory().createResource(parentLocator, this.getSession());
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }

    @Override
    public String getComplianceClass() {
        return DavResourceImpl.COMPLIANCE_CLASSES;
    }

    @Override
    public String getSupportedMethods() {
        return "OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";
    }

    @Override
    public DavResourceLocator getLocator() {
        return this.davResourceLocator;
    }

    @Override
    public String getResourcePath() {
        return this.getLocator().getResourcePath();
    }

    @Override
    public String getHref() {
        return this.getLocator().getHref(this.isCollection());
    }

    @Override
    public long getModificationTime() {
        return System.currentTimeMillis();
    }

    @Override
    public DavPropertyName[] getPropertyNames() {
        return this.getProperties().getPropertyNames();
    }

    public DavProperty getProperty(DavPropertyName davPropertyName) {
        return this.getProperties().get(davPropertyName);
    }

    @Override
    public DavPropertySet getProperties() {
        if (null == this.propertySet) {
            this.propertySet = new DavPropertySet();
            this.initProperties(this.propertySet);
        }
        return this.propertySet;
    }

    protected void initProperties(DavPropertySet propertySet) {
        if (this.getDisplayName() != null) {
            propertySet.add(new DefaultDavProperty<String>(DavPropertyName.DISPLAYNAME, this.getDisplayName()));
        }
        if (this.isCollection()) {
            propertySet.add(new ResourceType(1));
            propertySet.add(new DefaultDavProperty<String>(DavPropertyName.ISCOLLECTION, "1"));
        } else {
            propertySet.add(new ResourceType(0));
            propertySet.add(new DefaultDavProperty<String>(DavPropertyName.ISCOLLECTION, "0"));
        }
        String lastModified = IOUtil.getLastModified(this.getModificationTime());
        propertySet.add(new DefaultDavProperty<String>(DavPropertyName.GETLASTMODIFIED, lastModified));
        propertySet.add(new DefaultDavProperty<String>(DavPropertyName.CREATIONDATE, DavConstants.creationDateFormat.format(new Date(this.getCreationtTime()))));
        propertySet.add(new SupportedMethodSetProperty(this.getSupportedMethods().split(",\\s")));
        String workspaceHref = this.getWorkspaceHref();
        if (workspaceHref != null) {
            propertySet.add(new HrefProperty(DeltaVConstants.WORKSPACE, workspaceHref, true));
        }
    }

    public void setProperty(DavProperty davProperty) throws DavException {
        throw new DavException(403);
    }

    @Override
    public void removeProperty(DavPropertyName davPropertyName) throws DavException {
        throw new DavException(403);
    }

    public MultiStatusResponse alterProperties(DavPropertySet davPropertySet, DavPropertyNameSet davPropertyNameSet) throws DavException {
        ArrayList<DavConstants> changeList = new ArrayList<DavConstants>();
        if (null != davPropertySet) {
            DavPropertyIterator davPropertyIterator = davPropertySet.iterator();
            while (davPropertyIterator.hasNext()) {
                changeList.add(davPropertyIterator.nextProperty());
            }
        }
        if (null != davPropertyNameSet) {
            DavPropertyNameIterator davPropertyNameIterator = davPropertyNameSet.iterator();
            while (davPropertyNameIterator.hasNext()) {
                changeList.add(davPropertyNameIterator.nextPropertyName());
            }
        }
        return this.alterProperties(changeList);
    }

    public MultiStatusResponse alterProperties(List changeList) throws DavException {
        MultiStatusResponse msr = new MultiStatusResponse(this.getHref(), null);
        for (Object o : changeList) {
            int statusCode = 403;
            if (o instanceof DavProperty) {
                msr.add(((DavProperty)o).getName(), statusCode);
                continue;
            }
            msr.add((DavPropertyName)o, statusCode);
        }
        return msr;
    }

    @Override
    public boolean isLockable(Type type, Scope scope) {
        SupportedLock supportedLock = this.getSupportedLock();
        return null != supportedLock && supportedLock.isSupportedLock(type, scope);
    }

    @Override
    public boolean hasLock(Type type, Scope scope) {
        if (null == this.lockManager) {
            throw new NullPointerException(String.format("Unable to check if %s is locked because LockManager is null.", this.getResourcePath()));
        }
        return null != this.getLock(type, scope);
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope) {
        if (null == this.lockManager) {
            throw new NullPointerException(String.format("Unable to query for lock on %s because LockManager is null.", this.getResourcePath()));
        }
        return this.lockManager.getLock(type, scope, this);
    }

    @Override
    public ActiveLock[] getLocks() {
        SupportedLock supportedLock = this.getSupportedLock();
        if (null != supportedLock) {
            ArrayList<ActiveLock> activeLocks = new ArrayList<ActiveLock>();
            Iterator<LockEntry> i = supportedLock.getSupportedLocks();
            while (i.hasNext()) {
                LockEntry lockEntry = i.next();
                ActiveLock activeLock = this.getLock(lockEntry.getType(), lockEntry.getScope());
                if (null == activeLock) continue;
                activeLocks.add(activeLock);
            }
            return activeLocks.toArray(new ActiveLock[activeLocks.size()]);
        }
        return new ActiveLock[0];
    }

    @Override
    public ActiveLock lock(LockInfo lockInfo) throws DavException {
        if (null == this.lockManager) {
            throw new DavException(412, String.format("Unable to lock on %s because LockManager is null.", this.getResourcePath()));
        }
        return this.lockManager.createLock(lockInfo, this);
    }

    @Override
    public ActiveLock refreshLock(LockInfo lockInfo, String lockToken) throws DavException {
        if (null == this.lockManager) {
            throw new DavException(412, String.format("Unable to refresh lock on %s because LockManager is null.", this.getResourcePath()));
        }
        return this.lockManager.refreshLock(lockInfo, lockToken, this);
    }

    @Override
    public void unlock(String lockToken) throws DavException {
        if (null == this.lockManager) {
            throw new DavException(412, String.format("Unable to release lock on %s because LockManager is null.", this.getResourcePath()));
        }
        this.lockManager.releaseLock(lockToken, this);
    }

    @Override
    public void addLockManager(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    public DavResourceFactory getFactory() {
        return this.davResourceFactory;
    }

    @Override
    public DavSession getSession() {
        return this.davSession;
    }

    @Override
    public void addMember(DavResource davResource, InputContext inputContext) throws DavException {
        throw new DavException(403, "Not allowed to add members to " + this.getHref());
    }

    @Override
    public void removeMember(DavResource davResource) throws DavException {
        throw new DavException(403, "Not allowed to remove members from " + this.getHref());
    }

    @Override
    public void move(DavResource davResource) throws DavException {
        throw new DavException(403, "Not allowed to move " + this.getHref());
    }

    @Override
    public void copy(DavResource davResource, boolean shallow) throws DavException {
        throw new DavException(403, "Not allowed to copy from " + this.getHref());
    }
}

