/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.google.common.collect.ImmutableList;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

public class CanonicalUsersByEmailFinder {
    private final ApplicationService applicationService;

    public CanonicalUsersByEmailFinder(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @VisibleForTesting
    static EntityQuery<String> candidatesQuery(String email) {
        return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Restriction.on((Property)UserTermKeys.EMAIL).exactlyMatching((Object)email), (int)0, (int)-1);
    }

    @VisibleForTesting
    static EntityQuery<User> usersQuery(List<String> usernames) {
        return QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Restriction.on((Property)UserTermKeys.USERNAME).exactlyMatchingAny(usernames), (int)0, (int)-1);
    }

    public List<String> findCanonicalUsersByEmail(Application application, String email) {
        List<String> emailOwners = this.findEmailOwners(application, email);
        return this.findCanonicalUsersByUsernames(application, emailOwners).stream().filter(user -> IdentifierUtils.equalsInLowerCase((String)user.getEmailAddress(), (String)email)).map(Principal::getName).collect(Collectors.toList());
    }

    private List<String> findEmailOwners(Application application, String email) {
        return this.applicationService.searchUsers(application, CanonicalUsersByEmailFinder.candidatesQuery(email));
    }

    private List<User> findCanonicalUsersByUsernames(Application application, List<String> usernames) {
        return usernames.isEmpty() ? ImmutableList.of() : this.applicationService.searchUsers(application, CanonicalUsersByEmailFinder.usersQuery(usernames));
    }
}

