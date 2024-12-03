/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.UserManager
 *  com.google.common.collect.Iterables
 */
package com.atlassian.analytics.client.base;

import com.atlassian.analytics.client.AnalyticsMd5Hasher;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.ConfluenceBaseDataEvent;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.EntityException;
import com.atlassian.user.UserManager;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;

public class ConfluenceBaseDataLogger
implements BaseDataLogger {
    private final EventPublisher eventPublisher;
    private final UserManager userManager;
    private final PageManager pageManager;
    private final SpaceManager spaceManager;
    private final CommentManager commentManager;
    private final LicenseService licenseService;

    public ConfluenceBaseDataLogger(EventPublisher publisher, UserManager userManager, PageManager pageManager, SpaceManager spaceManager, CommentManager commentManager, LicenseService licenseService) {
        this.eventPublisher = publisher;
        this.userManager = userManager;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.commentManager = commentManager;
        this.licenseService = licenseService;
    }

    @Override
    public void logBaseData() {
    }

    public void logBaseDataRevise() {
        int numUsers = 0;
        try {
            numUsers = Iterables.size((Iterable)this.userManager.getUsers());
        }
        catch (EntityException entityException) {
            // empty catch block
        }
        List allSpaces = this.spaceManager.getAllSpaces();
        int numSpaces = allSpaces.size();
        ArrayList allPages = new ArrayList();
        ArrayList allBlogPosts = new ArrayList();
        int commentCount = 0;
        for (Space space : allSpaces) {
            allPages.addAll(this.pageManager.getPages(space, false));
            allBlogPosts.addAll(this.pageManager.getBlogPosts(space, false));
        }
        for (Page page : allPages) {
            commentCount += this.commentManager.countComments((Searchable)page);
        }
        for (BlogPost blogPost : allBlogPosts) {
            commentCount += this.commentManager.countComments((Searchable)blogPost);
        }
        int numPages = allPages.size();
        int numBlogPosts = allBlogPosts.size();
        String serverKey = AnalyticsMd5Hasher.md5Hex(this.licenseService.retrieve().getServerId());
        this.eventPublisher.publish((Object)new ConfluenceBaseDataEvent(numUsers, numSpaces, numPages, numBlogPosts, commentCount, serverKey));
    }
}

