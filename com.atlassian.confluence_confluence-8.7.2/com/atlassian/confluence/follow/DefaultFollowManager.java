/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.follow;

import com.atlassian.confluence.api.impl.pagination.Paginated;
import com.atlassian.confluence.api.impl.pagination.PaginationQuery;
import com.atlassian.confluence.api.impl.pagination.PaginationServiceInternal;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.event.events.follow.FollowEvent;
import com.atlassian.confluence.follow.persistence.dao.ConnectionDao;
import com.atlassian.confluence.internal.follow.FollowManagerInternal;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultFollowManager
implements FollowManagerInternal {
    private final ConnectionDao connectionDao;
    private final EventPublisher eventPublisher;
    private final PaginationServiceInternal paginationService;
    private final Predicate<ConfluenceUser> filterPredicate;

    public DefaultFollowManager(ConnectionDao connectionDao, SpacePermissionManager spacePermissionManager, EventPublisher eventPublisher, PaginationServiceInternal paginationService) {
        this.connectionDao = connectionDao;
        this.eventPublisher = eventPublisher;
        this.paginationService = paginationService;
        this.filterPredicate = new CanUserUseConfluence(spacePermissionManager);
    }

    @Override
    public boolean isUserFollowing(User follower, User followee) {
        return this.connectionDao.isUserFollowing(follower, followee);
    }

    @Override
    public void followUser(ConfluenceUser follower, ConfluenceUser followee) {
        if (this.isUserFollowing(follower, followee)) {
            return;
        }
        this.connectionDao.followUser(follower, followee);
        this.eventPublisher.publish((Object)new FollowEvent(this, followee, follower));
    }

    @Override
    public void unfollowUser(User follower, User followee) {
        this.connectionDao.unfollowUser(follower, followee);
    }

    @Override
    public void removeAllConnectionsFor(User user) {
        this.connectionDao.removeAllConnectionsFor(user);
    }

    @Override
    public <T> Paginated<T> getFollowers(ConfluenceUser user, PaginationQuery<ConfluenceUser, T> query) {
        Predicate<ConfluenceUser> predicate = this.andCanAccessConfluence(query.predicates());
        return this.paginationService.newPaginated(input -> this.connectionDao.getFilteredFollowers(user, (LimitedRequest)input, predicate::test), input -> StreamSupport.stream(input.spliterator(), false).map(query.modelConverter()).collect(Collectors.toList()), PaginationLimits.networkFollowers());
    }

    @Override
    public <T> Paginated<T> getFollowing(ConfluenceUser user, PaginationQuery<ConfluenceUser, T> query) {
        Predicate<ConfluenceUser> predicate = this.andCanAccessConfluence(query.predicates());
        return this.paginationService.newPaginated(input -> this.connectionDao.getFilteredFollowees(user, (LimitedRequest)input, predicate::test), input -> StreamSupport.stream(input.spliterator(), false).map(query.modelConverter()).collect(Collectors.toList()), PaginationLimits.networkFollowers());
    }

    private Predicate<ConfluenceUser> andCanAccessConfluence(List<Predicate<ConfluenceUser>> predicates) {
        return ImmutableList.builder().add(this.filterPredicate).addAll(predicates).build().stream().reduce(t -> true, Predicate::and);
    }

    public static class CanUserUseConfluence
    implements Predicate<ConfluenceUser> {
        private final UserCanUseConfluencePredicate delegate;

        public CanUserUseConfluence(SpacePermissionManager spacePermissionManager) {
            this.delegate = new UserCanUseConfluencePredicate(spacePermissionManager);
        }

        @Override
        public boolean test(@Nullable ConfluenceUser user) {
            return this.delegate.apply(user);
        }
    }

    @Deprecated
    public static class UserCanUseConfluencePredicate
    implements com.google.common.base.Predicate<ConfluenceUser> {
        private final SpacePermissionManager spacePermissionManager;

        public UserCanUseConfluencePredicate(SpacePermissionManager spacePermissionManager) {
            this.spacePermissionManager = spacePermissionManager;
        }

        public boolean apply(@Nullable ConfluenceUser user) {
            return user != null && this.spacePermissionManager.hasPermission("USECONFLUENCE", null, user);
        }
    }
}

