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

public class PersonalSpacesResourceImpl
extends AbstractCollectionResource {
    public static final String DISPLAY_NAME = "Personal";
    private final SpaceManager spaceManager;

    public PersonalSpacesResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SpaceManager spaceManager) {
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
    protected Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> memberResources = new ArrayList<DavResource>();
            DavResourceLocator locator = this.getLocator();
            List personalSpaces = this.spaceManager.getAllSpaces(SpacesQuery.newQuery().forUser(AuthenticatedUserThreadLocal.getUser()).withSpaceType(SpaceType.PERSONAL).build());
            for (Space space : personalSpaces) {
                StringBuilder resourcePathBuffer = new StringBuilder();
                resourcePathBuffer.append("/Personal/").append(space.getKey());
                DavResourceLocator spaceResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), resourcePathBuffer.toString(), false);
                memberResources.add(this.getFactory().createResource(spaceResourceLocator, this.getSession()));
            }
            return memberResources;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

