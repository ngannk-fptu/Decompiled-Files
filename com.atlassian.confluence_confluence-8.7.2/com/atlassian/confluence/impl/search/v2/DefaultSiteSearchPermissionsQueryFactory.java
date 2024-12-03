/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.BrowseUsersPermissionQuery;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;

@Internal
public class DefaultSiteSearchPermissionsQueryFactory
implements SiteSearchPermissionsQueryFactory {
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;

    public DefaultSiteSearchPermissionsQueryFactory(UserAccessor userAccessor, PermissionManager permissionManager, SpacePermissionQueryFactory spacePermissionQueryFactory) {
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
    }

    @Override
    public SearchQuery create() {
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        ContentPermissionsQuery contentPermissionsQuery = ContentPermissionsQuery.builder().user(remoteUser).groupNames(this.userAccessor.getGroupNames(remoteUser)).build();
        if (this.permissionManager.isSystemAdministrator(remoteUser)) {
            return contentPermissionsQuery;
        }
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        searchQueryBuilder.addFilter(new BrowseUsersPermissionQuery(this.permissionManager));
        searchQueryBuilder.addFilter(contentPermissionsQuery);
        searchQueryBuilder.addFilter(this.spacePermissionQueryFactory.create(remoteUser));
        return searchQueryBuilder.build();
    }
}

