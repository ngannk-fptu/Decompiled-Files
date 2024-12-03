/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.ActiveUserQuery;
import com.atlassian.confluence.search.v2.query.ArchivedSpacesQuery;
import com.atlassian.confluence.search.v2.query.AttachmentTypeQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.BrowseUsersPermissionQuery;
import com.atlassian.confluence.search.v2.query.ContentStatusQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.PrefixQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.ListUtils;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Internal
class V2ContentNameQueryFactory {
    private static final float CONTENT_NAME_SCORE = 2.0f;
    private static final float PARENT_NAME_SCORE = 1.0f;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final ContentPermissionsQueryFactory contentPermissionsQueryFactory;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;

    public V2ContentNameQueryFactory(SpaceManager spaceManager, PermissionManager permissionManager, ContentPermissionsQueryFactory contentPermissionsQueryFactory, SpacePermissionQueryFactory spacePermissionQueryFactory) {
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.contentPermissionsQueryFactory = Objects.requireNonNull(contentPermissionsQueryFactory);
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
    }

    public SearchQuery createQuery(List<QueryToken> queryTokens, boolean searchParentName) {
        return this.createNameQuery(queryTokens, searchParentName);
    }

    public SearchQuery createQuery(List<QueryToken> queryTokens, boolean searchParentName, Map<String, Object> params) {
        SearchQuery query = this.createNameQuery(queryTokens, searchParentName);
        return params == null || params.get("preferredSpaceKey") == null ? query : V2ContentNameQueryFactory.createPreferredSpaceQuery(query, (String)params.get("preferredSpaceKey"));
    }

    private SearchQuery createNameQuery(List<QueryToken> queryTokens, boolean searchParentName) {
        HashSet<SearchQuery> shouldSubQueries = new HashSet<SearchQuery>();
        shouldSubQueries.add(V2ContentNameQueryFactory.createSingleFieldQuery(SearchFieldNames.CONTENT_NAME_UNSTEMMED, queryTokens, 2.0f));
        shouldSubQueries.add(V2ContentNameQueryFactory.createSingleFieldQuery(SearchFieldNames.TITLE, queryTokens, 2.0f));
        if (searchParentName) {
            shouldSubQueries.add(V2ContentNameQueryFactory.createSingleFieldQuery(SearchFieldNames.PARENT_TITLE_UNSTEMMED, queryTokens, 1.0f));
        }
        SearchQuery shouldQueries = BooleanQuery.composeOrQuery(shouldSubQueries);
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        Optional<SearchQuery> contentPermissionsQuery = this.contentPermissionsQueryFactory.create(remoteUser);
        if (contentPermissionsQuery.isPresent()) {
            return BooleanQuery.composeAndQuery((Set<? extends SearchQuery>)ImmutableSet.of((Object)shouldQueries, (Object)contentPermissionsQuery.get()));
        }
        return shouldQueries;
    }

    private static SearchQuery createPreferredSpaceQuery(SearchQuery query, String spaceKey) {
        return (SearchQuery)BooleanQuery.builder().addMust(query).addShould(new TermQuery(SearchFieldNames.SPACE_KEY, spaceKey)).build();
    }

    private static SearchQuery createSingleFieldQuery(String fieldName, List<QueryToken> queryTokens, float boost) {
        BooleanQueryBuilder builder = BooleanQuery.builder().boost(boost);
        queryTokens.forEach(queryToken -> {
            if (queryToken.getType() == QueryToken.Type.PARTIAL) {
                builder.addMust((SearchQuery)BooleanQuery.builder().addShould(new PrefixQuery(fieldName, queryToken.getText())).addShould(new TermQuery(fieldName, queryToken.getText())).build());
            } else {
                builder.addMust(new TermQuery(fieldName, queryToken.getText()));
            }
        });
        return (SearchQuery)builder.build();
    }

    SearchQuery createFilter(Set<Attachment.Type> attachmentTypes, String ... spaceKeys) {
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        Set<String> keys = ListUtils.createSetOfNonEmptyElementsFromStringArray(spaceKeys);
        if (attachmentTypes != null && !attachmentTypes.isEmpty()) {
            queryBuilder.addFilter(new AttachmentTypeQuery(attachmentTypes));
        }
        if (!keys.isEmpty()) {
            queryBuilder.addMust(new InSpaceQuery(keys));
        }
        if (keys.isEmpty()) {
            queryBuilder.addMust(new ArchivedSpacesQuery(false, this.spaceManager));
        }
        queryBuilder.addMust(ActiveUserQuery.getInstance());
        queryBuilder.addMust(new BrowseUsersPermissionQuery(this.permissionManager));
        if (!this.permissionManager.isSystemAdministrator(AuthenticatedUserThreadLocal.get())) {
            queryBuilder.addMust(this.spacePermissionQueryFactory.create(AuthenticatedUserThreadLocal.get()));
        }
        queryBuilder.addMust(ContentStatusQuery.getDefaultContentStatusQuery());
        return queryBuilder.build();
    }
}

