/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserResolver
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@Component
public class FollowService {
    private final NetworkService networkService;
    private final ConfluenceUserResolver userResolver;

    @Autowired
    FollowService(@ComponentImport NetworkService networkService, @ComponentImport UserAccessor userResolver) {
        this.networkService = networkService;
        this.userResolver = userResolver;
    }

    Set<ConfluenceUser> getFollowingUsers(ConfluenceUser user) {
        return (Set)this.getFollowing(user).stream().map(arg_0 -> ((ConfluenceUserResolver)this.userResolver).getExistingByApiUser(arg_0)).filter(Optional::isPresent).map(Optional::get).collect(ImmutableSet.toImmutableSet());
    }

    private Collection<User> getFollowing(ConfluenceUser user) {
        SimplePageRequest request = new SimplePageRequest(LimitedRequestImpl.create((int)PaginationLimits.networkFollowers()));
        return this.networkService.getFollowing(user.getKey(), (PageRequest)request).getResults();
    }
}

