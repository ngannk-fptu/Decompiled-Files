/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.UserSearchQueryParameters;
import com.atlassian.confluence.search.v2.ChangesSearch;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.AbstractUserQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.ArchivedSpacesQuery;
import com.atlassian.confluence.search.v2.query.AttachmentTypeQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.BrowseUsersPermissionQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.ContributorQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelsQuery;
import com.atlassian.confluence.search.v2.query.LastModifierUserQuery;
import com.atlassian.confluence.search.v2.query.NotAnonymousUserQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TermRangeQuery;
import com.atlassian.confluence.search.v2.query.TextQuery;
import com.atlassian.confluence.search.v2.query.UserInfoQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.google.common.base.Preconditions;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class DefaultPredefinedSearchBuilder
implements PredefinedSearchBuilder {
    private static final TermRangeQuery USER_NAME_NOT_NULL_QUERY = new TermRangeQuery(SearchFieldNames.USER_NAME, null, null, true, true);
    private final SpaceManager spaceManager;
    private final LabelManager labelManager;
    private final PermissionManager permissionManager;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private final ConfluenceUserDao confluenceUserDao;

    public DefaultPredefinedSearchBuilder(PermissionManager permissionManager, SpaceManager spaceManager, LabelManager labelManager, SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory, ConfluenceUserDao confluenceUserDao) {
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
        this.confluenceUserDao = confluenceUserDao;
    }

    @Override
    public ISearch buildSiteSearch(SearchQueryParameters searchQueryParams, int startIndex, int pageSize) {
        Preconditions.checkNotNull((Object)searchQueryParams, (Object)"searchQueryParams cannot be null");
        Preconditions.checkArgument((startIndex >= 0 ? 1 : 0) != 0, (Object)"Start index must be greater than or equal to 0");
        return new ContentSearch(this.siteSearchQuery(searchQueryParams), searchQueryParams.getSort(), startIndex, pageSize);
    }

    private Set<String> removeEmptyLabels(Set<String> labels) {
        HashSet<String> newLabels = new HashSet<String>();
        if (labels != null) {
            for (String label : labels) {
                if (!StringUtils.isNotBlank((CharSequence)label)) continue;
                newLabels.add(label);
            }
        }
        return newLabels;
    }

    @Override
    public ISearch buildUsersSearch(String query, int maxResults) {
        Preconditions.checkArgument((maxResults > 0 ? 1 : 0) != 0, (Object)"maxResults must be greater than 0");
        Preconditions.checkArgument((boolean)StringUtils.isNotBlank((CharSequence)query), (Object)"query String must be specified");
        UserSearchQueryParameters params = UserSearchQueryParameters.builder().query(query).build();
        return this.buildUsersSearch(params, 0, maxResults);
    }

    @Override
    public ISearch buildUsersSearch(UserSearchQueryParameters userSearchQueryParameters, int startIndex, int numberOfResults) {
        BooleanQuery.Builder builder;
        Preconditions.checkArgument((startIndex >= 0 ? 1 : 0) != 0, (Object)"startIndex must be greater than or equal to 0");
        Preconditions.checkArgument((numberOfResults > 0 ? 1 : 0) != 0, (Object)"numberOfResults must be greater than 0");
        String query = userSearchQueryParameters.getQuery();
        Preconditions.checkArgument((boolean)StringUtils.isNotBlank((CharSequence)query), (Object)"query String must be specified");
        SearchQuery userNameQuery = userSearchQueryParameters.excludeEmptyUsernameUsers() ? USER_NAME_NOT_NULL_QUERY : AllQuery.getInstance();
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        searchQueryBuilder.addMust(userNameQuery);
        searchQueryBuilder.addMust(new UserInfoQuery(query));
        if (!userSearchQueryParameters.includeDeactivatedUsers()) {
            builder = BooleanQuery.builder();
            builder.addMustNot(new TermQuery(SearchFieldNames.IS_DEACTIVATED_USER, Boolean.TRUE.toString()));
            searchQueryBuilder.addFilter(builder.build());
        }
        if (!userSearchQueryParameters.includeExternallyDeletedUsers()) {
            builder = BooleanQuery.builder();
            builder.addMustNot(new TermQuery(SearchFieldNames.IS_EXTERNALLY_DELETED_USER, Boolean.TRUE.toString()));
            searchQueryBuilder.addFilter(builder.build());
        }
        if (!userSearchQueryParameters.includeUnlicensedUsers()) {
            builder = BooleanQuery.builder();
            builder.addMustNot(new TermQuery(SearchFieldNames.IS_LICENSED_USER, Boolean.FALSE.toString()));
            searchQueryBuilder.addFilter(builder.build());
        }
        BrowseUsersPermissionQuery viewUserProfilePermissionsQuery = new BrowseUsersPermissionQuery(this.permissionManager);
        searchQueryBuilder.addFilter(viewUserProfilePermissionsQuery);
        return new ContentSearch(searchQueryBuilder.build(), userSearchQueryParameters.getSort(), startIndex, numberOfResults);
    }

    @Override
    public ISearch buildRecentUpdateSearch(RecentUpdateQueryParameters params, int startIndex, int numberOfResults) {
        boolean networkFeed;
        Preconditions.checkNotNull((Object)params, (Object)"params cannot be null");
        Preconditions.checkArgument((startIndex >= 0 ? 1 : 0) != 0, (Object)"Start index must be greater than or equal to 0");
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        queryBuilder.addFilter(NotAnonymousUserQuery.getInstance());
        boolean bl = networkFeed = params.getFollowingUsers() != null && !params.getFollowingUsers().isEmpty();
        if (networkFeed) {
            queryBuilder.addFilter(new LastModifierUserQuery(params.getFollowingUsers()));
        }
        queryBuilder.addMust(this.getQueryForResultsUpToOneYearOld());
        EnumSet<ContentTypeEnum> allowedContentTypes = params.getContentTypes();
        if (allowedContentTypes != null && !allowedContentTypes.isEmpty()) {
            queryBuilder.addMust(new ContentTypeQuery(allowedContentTypes));
            queryBuilder.addMustNot(this.getAttachmentExcludeQueries(allowedContentTypes));
        }
        queryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        if (params.getSpaceKeys() != null && !params.getSpaceKeys().isEmpty()) {
            queryBuilder.addFilter(new InSpaceQuery(params.getSpaceKeys()));
        }
        if (params.getLabels() != null && !params.getLabels().isEmpty()) {
            queryBuilder.addFilter(new LabelsQuery(params.getLabels()));
        }
        if (networkFeed) {
            return new ChangesSearch(queryBuilder.build(), (SearchSort)ModifiedSort.DEFAULT, startIndex, numberOfResults){

                @Override
                public String getSearchType() {
                    return "NetworkFeedSearch";
                }
            };
        }
        queryBuilder.addMust(new ArchivedSpacesQuery(false, this.spaceManager));
        return new ChangesSearch(queryBuilder.build(), (SearchSort)ModifiedSort.DEFAULT, startIndex, numberOfResults){

            @Override
            public String getSearchType() {
                return "RecentUpdatesSearch";
            }
        };
    }

    private DateRangeQuery getQueryForResultsUpToOneYearOld() {
        return new DateRangeQuery.Builder().fromDate(Date.from(OffsetDateTime.now().minusYears(1L).toInstant())).includeFrom(true).includeTo(false).queryType(DateRangeQuery.DateRangeQueryType.MODIFIED).build();
    }

    private Set<SearchQuery> getAttachmentExcludeQueries(Set<ContentTypeEnum> allowedContentTypes) {
        EnumSet<ContentTypeEnum> excludedOwningContentTypes = EnumSet.allOf(ContentTypeEnum.class);
        excludedOwningContentTypes.removeAll(allowedContentTypes);
        excludedOwningContentTypes.add(ContentTypeEnum.PERSONAL_INFORMATION);
        HashSet<SearchQuery> excludeQueries = new HashSet<SearchQuery>(excludedOwningContentTypes.size());
        for (ContentTypeEnum contentTypeEnum : excludedOwningContentTypes) {
            TermQuery excludeQuery = new TermQuery(SearchFieldNames.ATTACHMENT_OWNER_CONTENT_TYPE, contentTypeEnum.getType().getName());
            excludeQueries.add(excludeQuery);
        }
        return excludeQueries;
    }

    private SearchQuery siteSearchQuery(SearchQueryParameters searchQueryParams) {
        Set<String> strippedLabels;
        BooleanQuery.Builder builder = BooleanQuery.builder();
        if (StringUtils.isBlank((CharSequence)searchQueryParams.getQuery())) {
            builder.addMust(AllQuery.getInstance());
        } else {
            builder.addMust(new TextQuery(searchQueryParams.getQuery(), searchQueryParams.getExtraFields()));
        }
        if (searchQueryParams.getContentTypes() != null && !searchQueryParams.getContentTypes().isEmpty()) {
            builder.addMust(new ContentTypeQuery(searchQueryParams.getContentTypes()));
        }
        if (searchQueryParams.getPluginContentTypes() != null) {
            for (ContentTypeSearchDescriptor descriptor : searchQueryParams.getPluginContentTypes()) {
                builder.addMust(descriptor.getQuery());
            }
        }
        if (searchQueryParams.getLastModified() != null) {
            builder.addMust(new DateRangeQuery(searchQueryParams.getLastModified(), DateRangeQuery.DateRangeQueryType.MODIFIED));
        }
        if (searchQueryParams.getCategory() != null) {
            builder.addMust(new SpaceCategoryQuery(searchQueryParams.getCategory(), this.labelManager));
        }
        if (searchQueryParams.getAttachmentTypes() != null && !searchQueryParams.getAttachmentTypes().isEmpty()) {
            builder.addMust(new AttachmentTypeQuery(searchQueryParams.getAttachmentTypes()));
        }
        if (searchQueryParams.getContributor() != null) {
            builder.addMust(this.contributorQuery(searchQueryParams.getContributor()));
        }
        if (searchQueryParams.getSpaceKeys() != null && !searchQueryParams.getSpaceKeys().isEmpty()) {
            builder.addFilter(new InSpaceQuery(searchQueryParams.getSpaceKeys()));
        }
        if (searchQueryParams.isOnlyArchivedSpaces()) {
            builder.addFilter(new ArchivedSpacesQuery(true, this.spaceManager));
        } else if (!searchQueryParams.isIncludeArchivedSpaces() && (searchQueryParams.getSpaceKeys() == null || searchQueryParams.getSpaceKeys().isEmpty())) {
            builder.addFilter(new ArchivedSpacesQuery(false, this.spaceManager));
        }
        builder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        if (searchQueryParams.getSearchQueryFilter() != null) {
            builder.addFilter(searchQueryParams.getSearchQueryFilter());
        }
        if (!(strippedLabels = this.removeEmptyLabels(searchQueryParams.getLabels())).isEmpty()) {
            builder.addFilter(new LabelsQuery(strippedLabels));
        }
        return builder.build();
    }

    private SearchQuery contributorQuery(ConfluenceUser contributor) {
        HashSet<AbstractUserQuery> subQueries = new HashSet<AbstractUserQuery>();
        subQueries.add(new CreatorQuery(contributor.getName()));
        subQueries.add(new ContributorQuery(contributor.getName(), this.confluenceUserDao));
        return BooleanQuery.composeOrQuery(subQueries);
    }
}

