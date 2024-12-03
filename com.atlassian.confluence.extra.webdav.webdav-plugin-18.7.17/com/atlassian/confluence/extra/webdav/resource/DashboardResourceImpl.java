/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class DashboardResourceImpl
extends AbstractCollectionResource {
    private final String workspaceName;

    public DashboardResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, String workspaceName) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.workspaceName = workspaceName;
    }

    @Override
    protected long getCreationtTime() {
        return 0L;
    }

    @Override
    public String getDisplayName() {
        return this.workspaceName;
    }

    @Override
    protected Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> memberResources = new ArrayList<DavResource>();
            DavResourceFactory davResourceFactory = this.getFactory();
            DavResourceLocator locator = this.getLocator();
            memberResources.add(davResourceFactory.createResource(locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), "/Global", false), this.getSession()));
            memberResources.add(davResourceFactory.createResource(locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), "/Personal", false), this.getSession()));
            return memberResources;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

