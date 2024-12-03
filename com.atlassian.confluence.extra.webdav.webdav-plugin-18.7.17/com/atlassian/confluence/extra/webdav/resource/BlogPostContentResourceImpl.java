/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractTextContentResource;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class BlogPostContentResourceImpl
extends AbstractTextContentResource {
    public static final String DISPLAY_NAME_SUFFIX = ".txt";
    private final UserAccessor userAccessor;
    private final PageManager pageManager;
    private final String spaceKey;
    private final Calendar publishedDate;
    private final String blogTitle;
    private BlogPost blogPost;

    public BlogPostContentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor, @ComponentImport PageManager pageManager, String spaceKey, int yearPublished, int monthPublished, int dayPublished, String blogTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager);
        this.userAccessor = userAccessor;
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.blogTitle = blogTitle;
        this.publishedDate = this.getBlogPostPublishedDate(yearPublished, monthPublished, dayPublished);
    }

    protected Calendar getBlogPostPublishedDate(int yearPublished, int monthPublished, int dayPublished) {
        Calendar publishedDate = Calendar.getInstance(this.userAccessor.getConfluenceUserPreferences(AuthenticatedUserThreadLocal.getUser()).getTimeZone().getWrappedTimeZone());
        publishedDate.set(yearPublished, monthPublished - 1, dayPublished);
        return publishedDate;
    }

    private BlogPost getBlogPost() {
        if (null == this.blogPost) {
            this.blogPost = this.pageManager.getBlogPost(this.spaceKey, this.blogTitle, this.publishedDate);
        }
        return this.blogPost;
    }

    @Override
    protected byte[] getTextContentAsBytes(String encoding) throws UnsupportedEncodingException {
        return this.getBlogPost().getBodyContent().getBody().getBytes(encoding);
    }

    @Override
    public long getModificationTime() {
        return this.getBlogPost().getLastModificationDate().getTime();
    }

    @Override
    protected long getCreationtTime() {
        return this.getBlogPost().getCreationDate().getTime();
    }

    @Override
    public boolean exists() {
        return super.exists() && null != this.getBlogPost();
    }

    @Override
    public String getDisplayName() {
        return this.getBlogPost().getTitle() + DISPLAY_NAME_SUFFIX;
    }
}

