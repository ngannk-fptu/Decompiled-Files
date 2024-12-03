/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.content.service.blogpost.BlogPostProvider;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.user.User;

public interface BlogPostService {
    public ServiceCommand newDeleteBlogPostCommand(BlogPostLocator var1);

    public BlogPostLocator getIdBlogPostLocator(long var1);

    public ServiceCommand newRevertBlogPostCommand(BlogPostLocator var1, int var2, String var3, boolean var4);

    public ServiceCommand newCreateBlogPostCommand(BlogPostProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, BlogPost var4, User var5, boolean var6);

    public ServiceCommand newCreateBlogPostCommand(BlogPostProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, ContentEntityObject var4, User var5, boolean var6);

    public ServiceCommand newCreateBlogPostCommand(BlogPostProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, Draft var4, User var5, boolean var6);

    public ServiceCommand newMoveBlogPostCommand(BlogPostLocator var1, SpaceLocator var2);

    public ServiceCommand newRemoveBlogPostVersionCommand(BlogPostLocator var1);
}

