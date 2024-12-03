/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.queries.BooleanFilter
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.SiteSearchPermissionsQuery;
import com.atlassian.confluence.impl.search.v2.lucene.filter.BrowseUsersPermissionsFilterFactory;
import com.atlassian.confluence.impl.search.v2.lucene.filter.ContentPermissionsFilter;
import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilterFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.util.List;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

public class SiteSearchPermissionsQueryMapper
implements LuceneQueryMapper<SiteSearchPermissionsQuery> {
    private UserAccessor userAccessor;
    private PermissionManager permissionManager;
    private SpacePermissionsFilterFactory spacePermissionsFilterFactory;
    private BrowseUsersPermissionsFilterFactory browseUsersPermissionsFilterFactory;

    @Override
    public Query convertToLuceneQuery(SiteSearchPermissionsQuery searchQuery) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        List<String> groupNames = currentUser == null ? null : this.userAccessor.getGroupNames(currentUser);
        ContentPermissionsFilter contentPermissionsFilter = new ContentPermissionsFilter(currentUser, groupNames);
        if (this.permissionManager.isSystemAdministrator(currentUser)) {
            return new FilteredQuery((Query)new MatchAllDocsQuery(), (Filter)contentPermissionsFilter);
        }
        BooleanFilter booleanFilter = new BooleanFilter();
        booleanFilter.add((Filter)this.spacePermissionsFilterFactory.create(currentUser), BooleanClause.Occur.MUST);
        booleanFilter.add((Filter)contentPermissionsFilter, BooleanClause.Occur.MUST);
        booleanFilter.add(this.browseUsersPermissionsFilterFactory.create(currentUser), BooleanClause.Occur.MUST);
        return new FilteredQuery((Query)new MatchAllDocsQuery(), (Filter)booleanFilter);
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setSpacePermissionsFilterFactory(SpacePermissionsFilterFactory spacePermissionsFilterFactory) {
        this.spacePermissionsFilterFactory = spacePermissionsFilterFactory;
    }

    public void setBrowseUsersPermissionsFilterFactory(BrowseUsersPermissionsFilterFactory browseUsersPermissionsFilterFactory) {
        this.browseUsersPermissionsFilterFactory = browseUsersPermissionsFilterFactory;
    }
}

