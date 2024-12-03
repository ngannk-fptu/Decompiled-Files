/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.api.impl.service.network;

import com.atlassian.confluence.api.impl.pagination.PaginationQueryImpl;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.internal.follow.FollowManagerInternal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.util.function.Function;

public class NetworkServiceImpl
implements NetworkService {
    private final FollowManagerInternal followManager;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PersonFactory personFactory;
    private final Function<ConfluenceUser, User> mapUser = new Function<ConfluenceUser, User>(){

        @Override
        public User apply(ConfluenceUser input) {
            return NetworkServiceImpl.this.personFactory.fromUser((ConfluenceUser)Preconditions.checkNotNull((Object)input));
        }
    };

    public NetworkServiceImpl(FollowManagerInternal followManager, ConfluenceUserResolver confluenceUserResolver, PersonFactory personFactory) {
        this.followManager = followManager;
        this.confluenceUserResolver = confluenceUserResolver;
        this.personFactory = personFactory;
    }

    public PageResponse<User> getFollowers(UserKey userKey, PageRequest request) throws NotFoundException {
        ConfluenceUser user = this.getUserOrNotFound(userKey);
        return this.followManager.getFollowers(user, PaginationQueryImpl.createNewQuery(this.mapUser)).page(request);
    }

    public PageResponse<User> getFollowing(UserKey userKey, PageRequest request) throws NotFoundException {
        ConfluenceUser user = this.getUserOrNotFound(userKey);
        return this.followManager.getFollowing(user, PaginationQueryImpl.createNewQuery(this.mapUser)).page(request);
    }

    private ConfluenceUser getUserOrNotFound(UserKey userKey) throws NotFoundException {
        ConfluenceUser user = this.confluenceUserResolver.getUserByKey(userKey);
        if (user == null) {
            throw new NotFoundException("No user found with key " + userKey);
        }
        return user;
    }
}

