/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.util.Optional;
import javax.annotation.Nullable;

public class DefaultContentPermissionsQueryFactory
implements ContentPermissionsQueryFactory {
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;

    public DefaultContentPermissionsQueryFactory(PermissionManager permissionManager, UserAccessor userAccessor) {
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public Optional<SearchQuery> create(@Nullable ConfluenceUser user) {
        if (!this.permissionManager.isSystemAdministrator(user)) {
            return Optional.of(ContentPermissionsQuery.builder().user(user).groupNames(this.userAccessor.getGroupNames(user)).build());
        }
        return Optional.empty();
    }
}

