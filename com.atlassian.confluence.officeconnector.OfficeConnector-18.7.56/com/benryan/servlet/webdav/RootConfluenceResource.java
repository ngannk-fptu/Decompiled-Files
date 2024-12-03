/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.extra.webdav.ConfluenceDavSession
 *  com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource
 *  org.apache.jackrabbit.webdav.DavResource
 *  org.apache.jackrabbit.webdav.DavResourceFactory
 *  org.apache.jackrabbit.webdav.DavResourceLocator
 *  org.apache.jackrabbit.webdav.lock.LockManager
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import java.util.Collection;
import java.util.Collections;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class RootConfluenceResource
extends AbstractCollectionResource {
    public RootConfluenceResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
    }

    public String getDisplayName() {
        return "Atlassian Confluence";
    }

    protected Collection<DavResource> getMemberResources() {
        return Collections.emptySet();
    }

    protected long getCreationtTime() {
        return 0L;
    }

    public boolean exists() {
        return true;
    }
}

