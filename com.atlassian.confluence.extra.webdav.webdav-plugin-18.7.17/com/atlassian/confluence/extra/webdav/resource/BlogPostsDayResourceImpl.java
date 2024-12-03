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
import com.atlassian.confluence.extra.webdav.resource.BlogPostsMonthResourceImpl;
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
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class BlogPostsDayResourceImpl
extends BlogPostsMonthResourceImpl {
    private static final String DISPLAY_NAME_SUFFIX = ".txt";
    protected final int dayPublished;

    public BlogPostsDayResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport UserAccessor userAccessor, @ComponentImport PermissionManager permissionManager, @ComponentImport PageManager pageManager, @ComponentImport SpaceManager spaceManager, String spaceKey, int yearPublished, int monthPublished, int dayPublished) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, userAccessor, permissionManager, spaceManager, pageManager, spaceKey, yearPublished, monthPublished);
        this.dayPublished = dayPublished;
    }

    public int getDayPublished() {
        return this.dayPublished;
    }

    @Override
    public String getDisplayName() {
        return new DecimalFormat("00").format(this.dayPublished);
    }

    @Override
    protected Calendar getBlogPostPeriod() {
        Calendar dateWithTimezoneOffset = super.getBlogPostPeriod();
        dateWithTimezoneOffset.set(5, this.dayPublished);
        return dateWithTimezoneOffset;
    }

    protected DavResourceLocator getBlogPostContentResourceLocator(DavResourceLocator locator, StringBuffer childResourcePathBuffer) {
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), childResourcePathBuffer.toString(), false);
    }

    @Override
    public Collection<DavResource> getMemberResources() {
        try {
            DavResourceLocator locator = this.getLocator();
            String parentPath = this.getParentResourcePath();
            StringBuffer childResourcePathBuffer = new StringBuffer();
            ArrayList<DavResource> members = new ArrayList<DavResource>();
            Calendar blogPostsPublishedDate = this.getBlogPostPeriod();
            List blogPosts = this.getPermissionManager().getPermittedEntities(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, this.getPageManager().getBlogPosts(this.spaceKey, blogPostsPublishedDate, 5));
            for (BlogPost blogPost : blogPosts) {
                childResourcePathBuffer.setLength(0);
                childResourcePathBuffer.append(parentPath).append('/').append(blogPost.getPostingDayOfMonth()).append('/').append(blogPost.getTitle()).append(DISPLAY_NAME_SUFFIX);
                DavResourceLocator blogPostContentResourceLocator = this.getBlogPostContentResourceLocator(locator, childResourcePathBuffer);
                members.add(this.getFactory().createResource(blogPostContentResourceLocator, this.getSession()));
            }
            return members;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

