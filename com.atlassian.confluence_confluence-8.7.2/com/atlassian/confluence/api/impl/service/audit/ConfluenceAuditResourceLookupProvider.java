/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.Page$Builder
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.api.util.pagination.PageRequest$Builder
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.spi.lookup.AuditingResourcesLookupService
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.spi.lookup.AuditingResourcesLookupService;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryUtil;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.UserSearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceAuditResourceLookupProvider
implements AuditingResourcesLookupService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAuditResourceLookupProvider.class);
    private static final String AUTHOR_USER_TYPE = "user";
    private final StandardAuditResourceTypes resourceTypes;
    private final PredefinedSearchBuilder searchBuilder;
    private final SearchManager searchManager;
    private final SpaceManagerInternal spaceManager;
    private final ConfluenceUserDao userDao;

    public ConfluenceAuditResourceLookupProvider(StandardAuditResourceTypes resourceTypes, PredefinedSearchBuilder searchBuilder, SearchManager searchManager, SpaceManagerInternal spaceManager, ConfluenceUserDao userDao) {
        this.resourceTypes = resourceTypes;
        this.searchBuilder = searchBuilder;
        this.searchManager = searchManager;
        this.spaceManager = spaceManager;
        this.userDao = userDao;
    }

    public Page<AuditAuthor, String> lookupAuditAuthor(@Nullable String searchText, @NonNull PageRequest<String> pageRequest) {
        Preconditions.checkArgument((!pageRequest.getCursor().isPresent() ? 1 : 0) != 0, (Object)"Lookup with non-empty cursor is not supported in Confluence.");
        int offset = pageRequest.getOffset();
        int limit = pageRequest.getLimit();
        String searchQuery = ConfluenceAuditResourceLookupProvider.userSearchQuery(searchText);
        UserSearchQueryParameters params = UserSearchQueryParameters.builder().query(searchQuery).setExcludeEmptyUsernameUsers(true).addUserCategory(UserSearchQueryParameters.UserCategory.LICENSED).addUserCategory(UserSearchQueryParameters.UserCategory.UNLICENSED).addUserCategory(UserSearchQueryParameters.UserCategory.DEACTIVATED).addUserCategory(UserSearchQueryParameters.UserCategory.EXTERNALLY_DELETED).sort(TitleSort.ASCENDING).build();
        ISearch search = this.searchBuilder.buildUsersSearch(params, offset, limit);
        try {
            SearchResults results = this.searchManager.search(search);
            HashMap<String, UserInfo> missing = new HashMap<String, UserInfo>();
            List<UserInfo> userInfos = results.getAll().stream().filter(Objects::nonNull).map(result -> {
                UserInfo userInfo = new UserInfo(result.getField(SearchFieldNames.USER_KEY), result.getField(SearchFieldNames.USER_NAME), result.getField(SearchFieldNames.USER_FULLNAME));
                if (StringUtils.isBlank((CharSequence)userInfo.getUserKey())) {
                    missing.put(userInfo.getUserName().toLowerCase(), userInfo);
                }
                return userInfo;
            }).collect(Collectors.toList());
            List<AuditAuthor> authors = this.augmentUserKeyOrRemove(userInfos, missing);
            return new Page.Builder(authors, results.isLastPage()).nextPageRequest(new PageRequest.Builder().cursor(null).offset(offset + limit).limit(limit).build()).build();
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException("Unable to perform users search for audit view", e);
        }
    }

    @VisibleForTesting
    List<AuditAuthor> augmentUserKeyOrRemove(@NonNull List<UserInfo> userInfos, @NonNull Map<String, UserInfo> missing) {
        HashMap augmented = new HashMap();
        HashMap<String, UserInfo> stillMissing = new HashMap<String, UserInfo>(missing);
        if (!missing.isEmpty()) {
            Map<String, UserKey> userKeysByLowerNames = this.userDao.findUserKeysByLowerNames(missing.keySet());
            userKeysByLowerNames.forEach((userName, userKey) -> {
                UserInfo original = (UserInfo)stillMissing.get(userName.toLowerCase());
                augmented.put(userName.toLowerCase(), new UserInfo(userKey.getStringValue(), original.getUserName(), original.getUserFullName()));
                stillMissing.remove(userName.toLowerCase());
            });
        }
        if (log.isDebugEnabled()) {
            if (missing.isEmpty()) {
                log.debug("no missing userKeys, nothing to augment");
            } else {
                log.debug("{} missing userKeys, {} still missing after augmentation", (Object)missing.size(), (Object)stillMissing.size());
            }
        }
        return userInfos.stream().filter(userInfo -> !stillMissing.containsKey(userInfo.userName.toLowerCase())).map(userInfo -> augmented.getOrDefault(userInfo.getUserName().toLowerCase(), userInfo)).map(userInfo -> AuditAuthor.builder().id(userInfo.getUserKey()).name(userInfo.getUserFullName()).type(AUTHOR_USER_TYPE).build()).collect(Collectors.toList());
    }

    public Page<AuditResource, String> lookupAuditResource(@NonNull String resourceType, @Nullable String searchText, @NonNull PageRequest<String> pageRequest) {
        String spaceResourceType = this.resourceTypes.space();
        Preconditions.checkArgument((!pageRequest.getCursor().isPresent() ? 1 : 0) != 0, (Object)"Lookup with non-empty cursor is not supported in Confluence");
        Preconditions.checkArgument((boolean)spaceResourceType.equals(resourceType), (Object)("Only lookup for " + spaceResourceType + " resource type is supported"));
        int offset = pageRequest.getOffset();
        int limit = pageRequest.getLimit();
        String searchQuery = ConfluenceAuditResourceLookupProvider.spaceSearchQuery(searchText);
        SearchQueryParameters params = new SearchQueryParameters(searchQuery);
        params.setContentTypes((Set<ContentTypeEnum>)ImmutableSet.of((Object)((Object)ContentTypeEnum.SPACE_DESCRIPTION), (Object)((Object)ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION)));
        params.setSort(TitleSort.ASCENDING);
        ISearch search = this.searchBuilder.buildSiteSearch(params, offset, limit);
        try {
            SearchResults results = this.searchManager.search(search);
            Set<String> foundSpaceKeys = results.getAll().stream().map(SearchResult::getSpaceKey).collect(Collectors.toSet());
            SpacesQuery spaceQuery = SpacesQuery.newQuery().withSpaceKeys(foundSpaceKeys).build();
            List matchingSpaces = this.spaceManager.getSpaces(spaceQuery, LimitedRequestImpl.create((int)limit), x -> true).getResults();
            Map<String, Long> spaceIdByKey = matchingSpaces.stream().collect(Collectors.toMap(Space::getKey, EntityObject::getId));
            List resources = results.getAll().stream().filter(r -> spaceIdByKey.containsKey(r.getSpaceKey())).map(r -> AuditResource.builder((String)r.getSpaceName(), (String)spaceResourceType).id(String.valueOf(spaceIdByKey.get(r.getSpaceKey()))).build()).collect(Collectors.toList());
            return new Page.Builder(resources, results.isLastPage()).nextPageRequest(new PageRequest.Builder().cursor(null).offset(offset + limit).limit(limit).build()).build();
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException("Unable to perform spaces search for audit view", e);
        }
    }

    @VisibleForTesting
    static @NonNull String userSearchQuery(@Nullable String searchText) {
        return Optional.ofNullable(searchText).filter(StringUtils::isNotBlank).map(LuceneQueryUtil::safeEscape).orElse("") + "*";
    }

    @VisibleForTesting
    static @NonNull String spaceSearchQuery(@Nullable String searchText) {
        return Optional.ofNullable(searchText).filter(StringUtils::isNotBlank).map(LuceneQueryUtil::safeEscape).map(t -> t + "*").orElse("");
    }

    static class UserInfo {
        private final String userKey;
        private final String userName;
        private final String userFullName;

        UserInfo(@Nullable String userKey, @NonNull String userName, @Nullable String userFullName) {
            this.userKey = userKey;
            this.userName = Objects.requireNonNull(userName);
            this.userFullName = userFullName;
        }

        public @Nullable String getUserKey() {
            return this.userKey;
        }

        public @NonNull String getUserName() {
            return this.userName;
        }

        public @Nullable String getUserFullName() {
            return this.userFullName;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UserInfo userinfo = (UserInfo)o;
            return this.userName.equals(userinfo.userName);
        }

        public int hashCode() {
            return this.userName.hashCode();
        }
    }
}

