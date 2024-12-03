/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.extra.webdav.ConfluenceDavSession
 *  com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource
 *  com.atlassian.confluence.spaces.Space
 *  org.apache.jackrabbit.webdav.DavResource
 *  org.apache.jackrabbit.webdav.DavResourceFactory
 *  org.apache.jackrabbit.webdav.DavResourceLocator
 *  org.apache.jackrabbit.webdav.lock.LockManager
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.Collections;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class SpaceResource
extends AbstractCollectionResource {
    private Space space;

    public SpaceResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, Space space) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.space = space;
    }

    public String getDisplayName() {
        return this.space.getKey();
    }

    public boolean exists() {
        return this.space != null;
    }

    protected Collection<DavResource> getMemberResources() {
        return Collections.emptySet();
    }

    protected long getCreationtTime() {
        return this.space.getCreationDate().getTime();
    }
}

