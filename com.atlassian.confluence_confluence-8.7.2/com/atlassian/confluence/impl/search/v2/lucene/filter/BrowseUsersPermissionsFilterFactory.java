/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.CachingWrapperFilter
 *  org.apache.lucene.search.Filter
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.search.v2.lucene.filter.MatchAllDocsFilter;
import com.atlassian.confluence.impl.search.v2.lucene.filter.MultiTermFilter;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;
import java.util.Objects;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;

public class BrowseUsersPermissionsFilterFactory {
    private static Filter cachedFilter = new CachingWrapperFilter(BrowseUsersPermissionsFilterFactory.createFilter());
    private final PermissionManager permissionManager;

    public BrowseUsersPermissionsFilterFactory(PermissionManager permissionManager) {
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    public Filter create(User currentUser) {
        if (!this.canBrowseUsers(currentUser)) {
            return cachedFilter;
        }
        return MatchAllDocsFilter.getInstance();
    }

    private boolean canBrowseUsers(User user) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, User.class);
    }

    private static Filter createFilter() {
        MultiTermFilter multiTermFilter = new MultiTermFilter(true);
        multiTermFilter.addTerm(new Term(SearchFieldNames.TYPE, "userinfo"));
        return multiTermFilter;
    }
}

