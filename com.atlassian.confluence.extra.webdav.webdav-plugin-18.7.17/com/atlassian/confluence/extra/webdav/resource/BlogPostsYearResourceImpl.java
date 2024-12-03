/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.BlogPostsResourceImpl;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class BlogPostsYearResourceImpl
extends BlogPostsResourceImpl {
    protected final int yearPublished;

    public BlogPostsYearResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, String spaceKey, int yearPublished) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, permissionManager, spaceManager, pageManager, spaceKey);
        this.yearPublished = yearPublished;
    }

    public int getYearPublished() {
        return this.yearPublished;
    }

    @Override
    public String getDisplayName() {
        return String.valueOf(this.yearPublished);
    }

    @Override
    public Collection<DavResource> getMemberResources() {
        try {
            DavResourceLocator locator = this.getLocator();
            String parentPath = this.getParentResourcePath();
            StringBuffer childResourcePathBuffer = new StringBuffer();
            HashSet<String> uniqueChildPaths = new HashSet<String>();
            ArrayList<DavResource> members = new ArrayList<DavResource>();
            List blogPosts = this.getPermissionManager().getPermittedEntities(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, this.getPageManager().getBlogPosts(this.getSpace(), true));
            String yearPublishedString = this.getDisplayName();
            for (BlogPost blogPost : blogPosts) {
                if (!StringUtils.equals((String)yearPublishedString, (String)blogPost.getPostingYear())) continue;
                childResourcePathBuffer.setLength(0);
                childResourcePathBuffer.append(parentPath).append('/').append(this.yearPublished).append('/').append(blogPost.getPostingMonthNumeric());
                uniqueChildPaths.add(childResourcePathBuffer.toString());
            }
            for (String uniqueChildPath : uniqueChildPaths) {
                DavResourceLocator blogPostMonthResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), uniqueChildPath, false);
                members.add(this.getFactory().createResource(blogPostMonthResourceLocator, this.getSession()));
            }
            return members;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

