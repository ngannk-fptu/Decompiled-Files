/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.search.GroupSearchStrategy;
import com.atlassian.crowd.manager.application.search.MembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.PagedSearcherImpl;
import com.atlassian.crowd.manager.application.search.UserSearchStrategy;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.Collections;
import java.util.List;

public enum NoDirectorySearchStrategy implements UserSearchStrategy,
GroupSearchStrategy,
MembershipSearchStrategy
{
    INSTANCE;


    @Override
    public <T> List<T> searchUsers(EntityQuery<T> query) {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> searchGroups(EntityQuery<T> query) {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> searchDirectGroupRelationships(MembershipQuery<T> query) {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> searchNestedGroupRelationships(MembershipQuery<T> query) {
        return Collections.emptyList();
    }

    @Override
    public <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(MembershipQuery<T> query) {
        return ImmutableListMultimap.of();
    }

    @Override
    public <T> PagedSearcher<T> createPagedUserSearcher(EntityQuery<T> query) {
        return PagedSearcherImpl.emptySearcher();
    }

    @Override
    public <T> PagedSearcher<T> createPagedGroupSearcher(EntityQuery<T> query) {
        return PagedSearcherImpl.emptySearcher();
    }
}

