/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class BlogPostsResourceImpl
extends AbstractCollectionResource {
    public static final String DISPLAY_NAME = "@news";
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    protected final String spaceKey;
    private Space space;

    public BlogPostsResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, String spaceKey) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
    }

    protected PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    protected SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    protected PageManager getPageManager() {
        return this.pageManager;
    }

    public Space getSpace() {
        if (null == this.space) {
            this.space = this.spaceManager.getSpace(this.spaceKey);
        }
        return this.space;
    }

    @Override
    protected long getCreationtTime() {
        return this.getSpace().getCreationDate().getTime();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> members = new ArrayList<DavResource>();
            DavResourceLocator locator = this.getLocator();
            String parentPath = this.getParentResourcePath();
            StringBuffer childResourcePathBuffer = new StringBuffer();
            HashSet<String> uniqueChildPaths = new HashSet<String>();
            List blogPosts = this.permissionManager.getPermittedEntities(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, this.pageManager.getBlogPosts(this.getSpace(), true));
            for (BlogPost blogPost : blogPosts) {
                childResourcePathBuffer.setLength(0);
                childResourcePathBuffer.append(parentPath).append('/').append(DISPLAY_NAME).append('/').append(blogPost.getPostingYear());
                uniqueChildPaths.add(childResourcePathBuffer.toString());
            }
            for (String uniqueChildPage : uniqueChildPaths) {
                DavResourceLocator blogPostYearResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), uniqueChildPage, false);
                members.add(this.getFactory().createResource(blogPostYearResourceLocator, this.getSession()));
            }
            return members;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

