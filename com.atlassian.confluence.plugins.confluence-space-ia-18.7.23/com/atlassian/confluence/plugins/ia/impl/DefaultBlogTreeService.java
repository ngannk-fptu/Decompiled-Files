/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.ia.model.BlogNodeBean;
import com.atlassian.confluence.plugins.ia.model.DateNodeBean;
import com.atlassian.confluence.plugins.ia.service.BlogTreeService;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.util.DateUtils;
import com.atlassian.user.User;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBlogTreeService
implements BlogTreeService {
    private final ContextPathHolder contextPathHolder;
    private final PageManager pageManager;
    private final LocaleManager localeManager;
    private final ContentPermissionManager contentPermissionManager;
    private final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private static final Logger log = LoggerFactory.getLogger(DefaultBlogTreeService.class);
    private static final Comparator<DateNodeBean> DATE_NODE_BEAN_COMPARATOR = (dateNodeBean1, dateNodeBean2) -> {
        if (dateNodeBean1.getGroupType() == 2) {
            return Integer.valueOf(dateNodeBean1.getGroupValue().split("/")[0]).compareTo(Integer.valueOf(dateNodeBean2.getGroupValue().split("/")[0]));
        }
        return Integer.valueOf(dateNodeBean1.getGroupValue()).compareTo(Integer.valueOf(dateNodeBean2.getGroupValue()));
    };
    private static final Comparator<BlogPost> BLOG_POST_REVERSE_COMPARATOR = (blogPost1, blogPost2) -> blogPost2.getPostingDate().compareTo(blogPost1.getPostingDate());

    public DefaultBlogTreeService(ContextPathHolder contextPathHolder, PageManager pageManager, LocaleManager localeManager, ContentPermissionManager contentPermissionManager, UserAccessor userAccessor, SpaceManager spaceManager, SpacePermissionManager spacePermissionManager) {
        this.contextPathHolder = contextPathHolder;
        this.pageManager = pageManager;
        this.localeManager = localeManager;
        this.contentPermissionManager = contentPermissionManager;
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public List<DateNodeBean> getBlogTree(User user, long id) {
        BlogPost blogPost = this.getTargetBlogPost(user, id);
        if (blogPost == null) {
            return Collections.emptyList();
        }
        String spaceKey = blogPost.getSpaceKey();
        if (!this.visibleSpace(user, spaceKey)) {
            return Collections.emptyList();
        }
        Calendar dateToExpand = this.now(user);
        dateToExpand.setTime(blogPost.getPostingDate());
        return this.generateYearBeans(user, spaceKey, dateToExpand);
    }

    @Override
    public List<DateNodeBean> getBlogTree(User user, String spaceKey, Calendar dateToExpand) {
        if (!this.visibleSpace(user, spaceKey)) {
            return Collections.emptyList();
        }
        if (dateToExpand == null) {
            dateToExpand = this.now(user);
            BlogPost latestVisibleBlogPost = this.getLatestVisibleBlogPost(user, spaceKey);
            if (latestVisibleBlogPost != null) {
                dateToExpand.setTime(latestVisibleBlogPost.getCreationDate());
            }
        }
        return this.generateYearBeans(user, spaceKey, dateToExpand);
    }

    @Override
    public List<DateNodeBean> getMonthsWithBlogPosts(User user, String spaceKey, String year) {
        if (!this.visibleSpace(user, spaceKey)) {
            return Collections.emptyList();
        }
        try {
            return this.generateMonthBeans(user, spaceKey, Integer.parseInt(year), null);
        }
        catch (NumberFormatException e) {
            log.debug("Couldn't parse the year '{}' when retrieving months with blog posts in the space with key '{}'", (Object)year, (Object)spaceKey);
            return Collections.emptyList();
        }
    }

    @Override
    public List<BlogNodeBean> getBlogsForMonth(User user, String spaceKey, String monthYear) {
        if (!this.visibleSpace(user, spaceKey)) {
            return Collections.emptyList();
        }
        try {
            Calendar calendarMonth = this.now(user);
            String[] split = monthYear.split("/");
            calendarMonth.set(2, Integer.parseInt(split[0]));
            calendarMonth.set(1, Integer.parseInt(split[1]));
            DateUtils.toStartOfPeriod((Calendar)calendarMonth, (int)2);
            List<BlogPost> blogPosts = this.getVisibleBlogPosts(user, spaceKey, calendarMonth);
            return this.convertToBlogNodeBeans(blogPosts);
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            log.debug("Couldn't parse the month '{}' when retrieving blog posts in the space with key '{}'", (Object)monthYear, (Object)spaceKey);
            return Collections.emptyList();
        }
    }

    @Override
    public String getDefaultBlogUrl(User user, String spaceKey) {
        Objects.requireNonNull(spaceKey);
        BlogPost latestPermittedBlogPost = this.getLatestVisibleBlogPost(user, spaceKey);
        Object urlPath = latestPermittedBlogPost != null ? latestPermittedBlogPost.getUrlPath() : "/pages/viewrecentblogposts.action?key=" + spaceKey;
        return this.contextPathHolder.getContextPath() + (String)urlPath;
    }

    private BlogPost getTargetBlogPost(User user, long pageId) {
        BlogPost blogPost = this.pageManager.getBlogPost(pageId);
        if (blogPost == null || !this.visibleBlog(user, (AbstractPage)blogPost)) {
            return null;
        }
        return blogPost;
    }

    private List<DateNodeBean> generateYearBeans(User user, String spaceKey, Calendar dateToExpand) {
        TimeZone userTimeZone = this.getUserTimeZone(user);
        Set<Integer> yearsWithBlogs = this.getYearsWithBlogPosts(spaceKey, userTimeZone);
        ArrayList<DateNodeBean> beans = new ArrayList<DateNodeBean>();
        for (Integer year : yearsWithBlogs) {
            String yearTitle = String.valueOf(year);
            DateNodeBean yearBean = new DateNodeBean(yearTitle, 1, yearTitle);
            List<DateNodeBean> monthNodes = this.generateMonthBeans(user, spaceKey, year, dateToExpand);
            if (monthNodes.isEmpty()) continue;
            yearBean.setChildren(monthNodes);
            beans.add(yearBean);
        }
        beans.sort(Collections.reverseOrder(DATE_NODE_BEAN_COMPARATOR));
        return beans;
    }

    private Set<Integer> getYearsWithBlogPosts(String spaceKey, TimeZone userTimeZone) {
        Calendar userTime = Calendar.getInstance(userTimeZone);
        List blogDates = this.pageManager.getBlogPostDates(spaceKey);
        return blogDates.stream().map(date -> {
            userTime.setTime((Date)date);
            return userTime.get(1);
        }).collect(Collectors.toSet());
    }

    private List<DateNodeBean> generateMonthBeans(User user, String spaceKey, Integer year, @Nullable Calendar dateToExpand) {
        TimeZone userTimeZone = this.getUserTimeZone(user);
        Calendar userTime = Calendar.getInstance(userTimeZone);
        userTime.set(1, year);
        List blogPosts = this.pageManager.getBlogPosts(spaceKey, userTime, 1);
        boolean[] visibleMonths = new boolean[12];
        ArrayList<BlogPost> visibleBlogPosts = new ArrayList<BlogPost>();
        for (BlogPost blogPost : blogPosts) {
            userTime.setTime(blogPost.getCreationDate());
            if (dateToExpand != null && userTime.get(2) == dateToExpand.get(2) && userTime.get(1) == dateToExpand.get(1)) {
                if (!this.visibleBlog(user, (AbstractPage)blogPost)) continue;
                visibleBlogPosts.add(blogPost);
                visibleMonths[dateToExpand.get((int)2)] = true;
                continue;
            }
            if (visibleMonths[userTime.get(2)]) continue;
            visibleMonths[userTime.get((int)2)] = this.visibleBlog(user, (AbstractPage)blogPost);
        }
        Locale userLocale = this.getUserLocale(user);
        ArrayList<DateNodeBean> monthBeans = new ArrayList<DateNodeBean>();
        for (int month = 11; month >= 0; --month) {
            if (!visibleMonths[month]) continue;
            String monthTitle = Month.of(month + 1).getDisplayName(TextStyle.FULL, userLocale);
            DateNodeBean monthBean = new DateNodeBean(monthTitle, 2, month + "/" + year);
            if (dateToExpand != null && month == dateToExpand.get(2) && !visibleBlogPosts.isEmpty()) {
                monthBean.setChildren(this.convertToBlogNodeBeans(visibleBlogPosts));
            }
            monthBeans.add(monthBean);
        }
        return monthBeans;
    }

    private List<BlogNodeBean> convertToBlogNodeBeans(List<BlogPost> blogPosts) {
        return blogPosts.stream().sorted(BLOG_POST_REVERSE_COMPARATOR).map(blogPost -> new BlogNodeBean(blogPost.getId(), blogPost.getTitle(), this.contextPathHolder.getContextPath() + blogPost.getUrlPath())).collect(Collectors.toList());
    }

    private List<BlogPost> getVisibleBlogPosts(User user, String spaceKey, Calendar calendarMonth) {
        List blogPosts = this.pageManager.getBlogPosts(spaceKey, calendarMonth, 2);
        return blogPosts.stream().filter(blog -> this.visibleBlog(user, (AbstractPage)blog)).collect(Collectors.toList());
    }

    private BlogPost getLatestVisibleBlogPost(User user, String spaceKey) {
        BlogPost blogPost = this.pageManager.getNewestBlogPost(spaceKey);
        while (blogPost != null && !this.visibleBlog(user, (AbstractPage)blogPost)) {
            blogPost = this.pageManager.findPreviousBlogPost(blogPost);
        }
        return blogPost;
    }

    private Calendar now(User user) {
        return Calendar.getInstance(this.getUserTimeZone(user));
    }

    private Locale getUserLocale(User user) {
        return Optional.ofNullable(this.localeManager.getLocale(user)).orElse(Locale.getDefault());
    }

    private TimeZone getUserTimeZone(User user) {
        return this.userAccessor.getConfluenceUserPreferences(user).getTimeZone().getWrappedTimeZone();
    }

    private boolean visibleBlog(User user, AbstractPage page) {
        return this.contentPermissionManager.hasContentLevelPermission(user, "View", (ContentEntityObject)page);
    }

    private boolean visibleSpace(User user, String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return false;
        }
        return this.spacePermissionManager.hasPermission("VIEWSPACE", space, user);
    }
}

