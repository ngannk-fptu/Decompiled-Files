/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;

public class BrowseUsersPermissionQuery
implements SearchQuery {
    public static final String KEY = "browseUsersPermission";
    private final PermissionManager permissionManager;

    public BrowseUsersPermissionQuery(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    @Override
    public SearchQuery expand() {
        if (this.permissionManager.hasPermission((User)this.getUser(), Permission.VIEW, User.class)) {
            return AllQuery.getInstance();
        }
        return (SearchQuery)BooleanQuery.builder().addMustNot(new TermQuery(SearchFieldNames.TYPE, "userinfo")).build();
    }

    private ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

