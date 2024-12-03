/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableSet
 *  com.rometools.rome.feed.synd.SyndFeed
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.rss.actions;

import com.atlassian.confluence.api.impl.pagination.PaginationQueryImpl;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.internal.follow.FollowManagerInternal;
import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.confluence.rss.FeedType;
import com.atlassian.confluence.rss.SyndFeedService;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableSet;
import com.rometools.rome.feed.synd.SyndFeed;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class NetworkFeedAction
extends ConfluenceActionSupport
implements UserAware {
    public static final int DEFAULT_MAX_RESULTS = 40;
    private SyndFeedService syndFeedService;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private FollowManagerInternal followManager;
    private UserAccessor userAccessor;
    private String username;
    private String rssType = FeedType.RSS.code();
    private int max = 40;
    private ConfluenceUser user;
    private ContentTypeEnum contentType;

    public SyndFeed getSyndFeed() {
        String title = this.getText("feeds.network.default.title", Collections.singletonList(this.getUser().getFullName()));
        ImmutableSet followingUsers = ImmutableSet.copyOf(this.followManager.getFollowing(this.getUser(), PaginationQueryImpl.newIdentityQuery(ConfluenceUser.class)).pagingIterator());
        if (followingUsers.size() == 0) {
            return this.syndFeedService.createSyndFeed(null, new FeedProperties(title, this.getText("feeds.not.following", Collections.singletonList(this.getUser().getFullName())), false, true));
        }
        EnumSet<ContentTypeEnum> contentTypes = this.contentType == null ? EnumSet.of(ContentTypeEnum.PAGE, ContentTypeEnum.BLOG, ContentTypeEnum.ATTACHMENT, ContentTypeEnum.COMMENT) : EnumSet.of(this.contentType);
        RecentUpdateQueryParameters parameters = new RecentUpdateQueryParameters((Set<ConfluenceUser>)followingUsers, null, null, contentTypes);
        ISearch recentUpdateSearch = this.predefinedSearchBuilder.buildRecentUpdateSearch(parameters, 0, Math.min(this.max, this.settingsManager.getGlobalSettings().getMaxRssItems()));
        String description = this.getText("feeds.user.follow.description", Collections.singletonList(this.getUser().getFullName()));
        FeedProperties feedProperties = new FeedProperties(title, description, true, this.getAuthenticatedUser() == null);
        return this.syndFeedService.createSyndFeed(recentUpdateSearch, feedProperties);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        try {
            return FeedType.valueOf(this.rssType.toUpperCase()).code();
        }
        catch (IllegalArgumentException e) {
            return FeedType.RSS.code();
        }
    }

    public void setFeedBuilder(SyndFeedService feedBuilder) {
        this.syndFeedService = feedBuilder;
    }

    public void setPredefinedSearchBuilder(PredefinedSearchBuilder predefinedSearchBuilder) {
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    public void setFollowManager(FollowManagerInternal followManager) {
        this.followManager = followManager;
    }

    @Override
    public ConfluenceUser getUser() {
        if (this.user == null) {
            this.user = StringUtils.isBlank((CharSequence)this.username) ? AuthenticatedUserThreadLocal.get() : this.userAccessor.getUserByName(this.username);
        }
        return this.user;
    }

    @Override
    public boolean isUserRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }

    @Override
    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRssType(String rssType) {
        this.rssType = rssType;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }
}

