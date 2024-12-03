/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.actions.RssDescriptor;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostsCalendar;
import com.atlassian.confluence.pages.actions.AbstractBlogPostsAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RequiresAnyConfluenceAccess
public class RecentBlogPostsAction
extends AbstractBlogPostsAction
implements SpaceAware {
    private List viewingBlogs;
    private BlogPostsCalendar calendar;
    private static final int POSTS_PER_PAGE = 15;
    private static final String PLUGIN_KEY = "space-blogposts";
    private int currentPage = 1;
    private boolean isOldestPage = false;

    public List getBlogPosts() {
        return this.viewingBlogs;
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.viewingBlogs = this.calculateViewingBlogs();
        GeneralUtil.setCookie("confluence.browse.space.cookie", PLUGIN_KEY);
        return super.execute();
    }

    public BlogPostsCalendar getCalendarForThisMonth() {
        Calendar postingDay = Calendar.getInstance();
        postingDay.setTime(new Date());
        if (this.calendar == null) {
            this.calendar = new BlogPostsCalendar(postingDay.getTime(), this.pageManager.getBlogPosts(this.getKey(), postingDay, 2), this.getKey(), this.getDateFormatter());
            this.calendar.setFirstPostInNextMonth(this.getFirstPostInNextMonth(postingDay));
            this.calendar.setLastPostInPreviousMonth(this.getLastPostInPreviousMonth(postingDay));
        }
        return this.calendar;
    }

    public RssDescriptor getRssDescriptor() {
        String title = this.getText("rss.descriptor.space.news.items", new Object[]{this.getSpace().getName()});
        return new RssDescriptor("/spaces/createrssfeed.action?types=blogpost&spaces=" + HtmlUtil.urlEncode(this.getKey()) + "&sort=modified&title=" + HtmlUtil.urlEncode(title) + "&maxResults=15", title, this.getAuthenticatedUser() != null);
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    private List calculateViewingBlogs() {
        BlogPost lastPermittedPost;
        this.currentPage = Math.max(this.currentPage, 1);
        int totalBlogs = 15 * this.currentPage;
        List<BlogPost> permittedBlogPosts = this.getPermittedRecentBlogPosts(totalBlogs);
        if (permittedBlogPosts.size() < 15) {
            this.isOldestPage = true;
            this.currentPage = 1;
            return permittedBlogPosts;
        }
        if (permittedBlogPosts.size() < totalBlogs) {
            totalBlogs = permittedBlogPosts.size();
            this.currentPage = totalBlogs / 15 + 1;
        }
        if (this.pageManager.findPreviousBlogPost(lastPermittedPost = permittedBlogPosts.get(permittedBlogPosts.size() - 1)) == null) {
            this.isOldestPage = true;
        }
        return permittedBlogPosts.subList(Math.max(0, totalBlogs - 15), totalBlogs);
    }

    private List<BlogPost> getPermittedRecentBlogPosts(int totalBlogs) {
        List blogPosts = this.pageManager.getRecentlyAddedBlogPosts(totalBlogs, this.getKey());
        return Lists.newArrayList((Iterable)Collections2.filter((Collection)blogPosts, blogPost -> this.permissionManager.hasPermissionNoExemptions(this.getAuthenticatedUser(), Permission.VIEW, blogPost.getLatestVersion())));
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public boolean isRecentBlogPosts() {
        return true;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isOldestPage() {
        return this.isOldestPage;
    }
}

