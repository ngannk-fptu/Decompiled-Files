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

public class WorkspaceResourceImpl
extends AbstractCollectionResource {
    private final String workspaceName;

    public WorkspaceResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, String workspaceName) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.workspaceName = workspaceName;
    }

    @Override
    protected long getCreationtTime() {
        return 0L;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return this.workspaceName;
    }

    @Override
    protected Collection<DavResource> getMemberResources() {
        try {
            DavResourceLocator davResourceLocator = this.getLocator();
            ArrayList<DavResource> memberResources = new ArrayList<DavResource>();
            memberResources.add(this.getFactory().createResource(davResourceLocator.getFactory().createResourceLocator(davResourceLocator.getPrefix(), new StringBuffer("/").append(this.workspaceName).toString(), "/", false), this.getSession()));
            return memberResources;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

