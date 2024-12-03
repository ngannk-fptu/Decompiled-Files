/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.repository.RepositoryIdentifier
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.DefaultPager
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.Pagers
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.atlassianuser.Conversions;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.Pagers;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Set;

@Deprecated
final class CrowdSearchResult<T>
implements SearchResult<T> {
    private final RepositoryIdentifier repositoryIdentifier;
    private final DefaultPager<T> results;

    static CrowdSearchResult<com.atlassian.user.Group> forGroups(RepositoryIdentifier repositoryIdentifier, Iterable<Group> results) {
        return new CrowdSearchResult<com.atlassian.user.Group>(repositoryIdentifier, Iterables.transform(results, Conversions.TO_ATLASSIAN_GROUP));
    }

    static CrowdSearchResult<com.atlassian.user.User> forUsers(RepositoryIdentifier repositoryIdentifier, Iterable<User> results) {
        return new CrowdSearchResult<com.atlassian.user.User>(repositoryIdentifier, Iterables.transform(results, Conversions.TO_ATLASSIAN_USER));
    }

    private CrowdSearchResult(RepositoryIdentifier repositoryIdentifier, Iterable<T> results) {
        this.repositoryIdentifier = repositoryIdentifier;
        this.results = Pagers.newDefaultPager(results);
    }

    public Pager<T> pager() {
        return this.results;
    }

    public Pager<T> pager(String repoKey) {
        if (this.repositoryIdentifier.getKey().equals(repoKey)) {
            return this.results;
        }
        return null;
    }

    public Set<String> repositoryKeyset() {
        return Collections.singleton(this.repositoryIdentifier.getKey());
    }
}

