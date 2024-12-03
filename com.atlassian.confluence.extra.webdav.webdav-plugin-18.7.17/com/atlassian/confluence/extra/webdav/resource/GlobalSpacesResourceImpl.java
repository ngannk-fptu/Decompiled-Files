/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class GlobalSpacesResourceImpl
extends AbstractCollectionResource {
    public static final String DISPLAY_NAME = "Global";
    private final SpaceManager spaceManager;

    public GlobalSpacesResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SpaceManager spaceManager) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.spaceManager = spaceManager;
    }

    @Override
    protected long getCreationtTime() {
        return 0L;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public boolean isValidated() {
        return true;
    }

    @Override
    public Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> members = new ArrayList<DavResource>();
            DavResourceFactory davResourceFactory = this.getFactory();
            DavResourceLocator locator = this.getLocator();
            List globalSpaces = this.spaceManager.getAllSpaces(SpacesQuery.newQuery().forUser(AuthenticatedUserThreadLocal.getUser()).withSpaceType(SpaceType.GLOBAL).build());
            for (Space space : globalSpaces) {
                StringBuilder resourcePathBuffer = new StringBuilder();
                resourcePathBuffer.append("/Global/").append(space.getKey());
                members.add(davResourceFactory.createResource(locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), resourcePathBuffer.toString(), false), this.getSession()));
            }
            return members;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

