/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.follow;

import com.atlassian.confluence.api.impl.pagination.Paginated;
import com.atlassian.confluence.api.impl.pagination.PaginationQuery;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.user.ConfluenceUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
@Deprecated
public interface FollowManagerInternal
extends FollowManager {
    public <T> Paginated<T> getFollowers(ConfluenceUser var1, PaginationQuery<ConfluenceUser, T> var2);

    public <T> Paginated<T> getFollowing(ConfluenceUser var1, PaginationQuery<ConfluenceUser, T> var2);
}

