/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.follow.persistence.dao;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface ConnectionDao {
    @Deprecated
    default public PageResponse<ConfluenceUser> getFollowers(ConfluenceUser followee, LimitedRequest limitedRequest, Predicate<ConfluenceUser> predicate) {
        return this.getFilteredFollowers(followee, limitedRequest, (java.util.function.Predicate<ConfluenceUser>)predicate);
    }

    public PageResponse<ConfluenceUser> getFilteredFollowers(ConfluenceUser var1, LimitedRequest var2, java.util.function.Predicate<ConfluenceUser> var3);

    @Deprecated
    default public PageResponse<ConfluenceUser> getFollowees(ConfluenceUser follower, LimitedRequest limitedRequest, Predicate<ConfluenceUser> predicate) {
        return this.getFilteredFollowees(follower, limitedRequest, (java.util.function.Predicate<ConfluenceUser>)predicate);
    }

    public PageResponse<ConfluenceUser> getFilteredFollowees(ConfluenceUser var1, LimitedRequest var2, java.util.function.Predicate<ConfluenceUser> var3);

    public boolean isUserFollowing(User var1, User var2);

    @Transactional
    public void followUser(User var1, User var2);

    @Transactional
    public void unfollowUser(User var1, User var2);

    @Transactional
    public void removeAllConnectionsFor(User var1);
}

