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
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.BlogPostsYearResourceImpl;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class BlogPostsMonthResourceImpl
extends BlogPostsYearResourceImpl {
    private final UserAccessor userAccessor;
    protected final int monthPublished;

    public BlogPostsMonthResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport UserAccessor userAccessor, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, String spaceKey, int yearPublished, int monthPublished) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, permissionManager, spaceManager, pageManager, spaceKey, yearPublished);
        this.userAccessor = userAccessor;
        this.monthPublished = monthPublished;
    }

    @Override
    public String getDisplayName() {
        return new DecimalFormat("00").format(this.monthPublished);
    }

    protected Calendar getUserDateWithTimeZone() {
        return Calendar.getInstance(this.userAccessor.getConfluenceUserPreferences(AuthenticatedUserThreadLocal.getUser()).getTimeZone().getWrappedTimeZone());
    }

    protected Calendar getBlogPostPeriod() {
        Calendar dateWithUserTimeZone = this.getUserDateWithTimeZone();
        dateWithUserTimeZone.set(this.yearPublished, this.monthPublished - 1, 1);
        return dateWithUserTimeZone;
    }

    @Override
    public Collection<DavResource> getMemberResources() {
        try {
            DavResourceLocator locator = this.getLocator();
            String parentPath = this.getParentResourcePath();
            StringBuffer childResourcePathBuffer = new StringBuffer();
            HashSet<String> uniqueChildPaths = new HashSet<String>();
            ArrayList<DavResource> members = new ArrayList<DavResource>();
            Calendar blogPostsPublishedDate = this.getBlogPostPeriod();
            List blogPosts = this.getPermissionManager().getPermittedEntities(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, this.getPageManager().getBlogPosts(this.spaceKey, blogPostsPublishedDate, 2));
            for (BlogPost blogPost : blogPosts) {
                childResourcePathBuffer.setLength(0);
                childResourcePathBuffer.append(parentPath).append('/').append(blogPost.getPostingMonthNumeric()).append('/').append(blogPost.getPostingDayOfMonth());
                uniqueChildPaths.add(childResourcePathBuffer.toString());
            }
            for (String uniqueChildPath : uniqueChildPaths) {
                DavResourceLocator blogPostDayResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), uniqueChildPath, false);
                members.add(this.getFactory().createResource(blogPostDayResourceLocator, this.getSession()));
            }
            return members;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

