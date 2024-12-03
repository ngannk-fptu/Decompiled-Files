/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment$Type
 *  com.atlassian.confluence.search.contentnames.Category
 *  com.atlassian.confluence.search.contentnames.ContentNameSearcher
 *  com.atlassian.confluence.search.contentnames.QueryTokenizer
 *  com.atlassian.confluence.search.contentnames.ResultTemplate
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.service.UserSearchQueryParameters
 *  com.atlassian.confluence.search.service.UserSearchQueryParameters$Builder
 *  com.atlassian.confluence.search.service.UserSearchQueryParameters$UserCategory
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.confluence.util.ListUtils
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.GroupQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.rest.service;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.GroupSearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntityList;
import com.atlassian.confluence.plugins.rest.entities.SearchResultGroupEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.EntityBuilderFactory;
import com.atlassian.confluence.plugins.rest.service.RestSearchParameters;
import com.atlassian.confluence.plugins.rest.service.RestSearchService;
import com.atlassian.confluence.plugins.rest.service.SearchServiceException;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearcher;
import com.atlassian.confluence.search.contentnames.QueryTokenizer;
import com.atlassian.confluence.search.contentnames.ResultTemplate;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.UserSearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.util.ListUtils;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.GroupQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class DefaultRestSearchService
implements RestSearchService {
    private static final int DEFAULT_MAX_PAGE_SIZE = 50;
    private static final int MAX_CATEGORY_RESULTS = 7;
    private static final String DARK_FEATURE_REMOVE_DUPLICATED_PERSONAL_INFO_DISABLE = "remove.duplicated.personalInformation.disable";
    private final ContentNameSearcher contentNameSearcher;
    private final QueryTokenizer contentNameQueryTokenizer;
    private SearchManager searchManager;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private CrowdService crowdService;
    private final EntityBuilderFactory entityBuilderFactory;

    public DefaultRestSearchService(ContentNameSearcher contentNameSearcher, QueryTokenizer contentNameQueryTokenizer, SearchManager searchManager, PredefinedSearchBuilder predefinedSearchBuilder, CrowdService crowdService, EntityBuilderFactory entityBuilderFactory) {
        this.contentNameSearcher = contentNameSearcher;
        this.contentNameQueryTokenizer = contentNameQueryTokenizer;
        this.searchManager = searchManager;
        this.predefinedSearchBuilder = predefinedSearchBuilder;
        this.crowdService = crowdService;
        this.entityBuilderFactory = entityBuilderFactory;
    }

    @Override
    public SearchResultEntityList userSearch(String query, Integer maxResults) throws SearchServiceException {
        return this.userSearch(query, maxResults, false);
    }

    @Override
    public SearchResultEntityList userSearch(String query, Integer maxResults, boolean showUnlicensedUsers) throws SearchServiceException {
        Integer max = maxResults == null || maxResults == 0 ? 50 : Math.min(50, maxResults);
        UserSearchQueryParameters.Builder params = UserSearchQueryParameters.builder().query(query);
        if (showUnlicensedUsers) {
            params.addUserCategory(UserSearchQueryParameters.UserCategory.UNLICENSED);
        }
        ISearch usersSearch = this.predefinedSearchBuilder.buildUsersSearch(params.build(), 0, max.intValue());
        try {
            SearchResults searchResults = this.searchManager.search(usersSearch);
            if (DarkFeatures.isDarkFeatureEnabled((String)DARK_FEATURE_REMOVE_DUPLICATED_PERSONAL_INFO_DISABLE)) {
                return this.makeSearchResultsEntityList(searchResults);
            }
            return this.makeSearchResultsEntityListRemovePIDuplicate(searchResults);
        }
        catch (InvalidSearchException | IllegalArgumentException e) {
            throw new SearchServiceException(e);
        }
    }

    @Override
    public SearchResultEntityList groupSearch(String query, Integer maxResults) throws SearchServiceException {
        Integer max = Math.min(50, maxResults == null || maxResults == 0 ? 50 : maxResults);
        NullRestriction restriction = NullRestrictionImpl.INSTANCE;
        if (!StringUtils.isBlank((CharSequence)query)) {
            restriction = Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)GroupTermKeys.NAME).startingWith((Object)query), Restriction.on((Property)GroupTermKeys.ACTIVE).exactlyMatching((Object)true)});
        }
        GroupQuery crowdQuery = new GroupQuery(Group.class, GroupType.GROUP, (SearchRestriction)restriction, 0, max.intValue());
        Iterable result = this.crowdService.search((Query)crowdQuery);
        ArrayList<SearchResultEntity> resultList = new ArrayList<SearchResultEntity>();
        for (Group group : result) {
            resultList.add(new GroupSearchResultEntity(group.getName()));
        }
        SearchResultEntityList resultEntities = new SearchResultEntityList();
        resultEntities.setResults(resultList);
        return resultEntities;
    }

    @Override
    public SearchResultEntityList nameSearch(final RestSearchParameters searchParameters, boolean groupResults, int startIndex, Integer pageSize, final Integer maxResultsPerGroup) throws SearchServiceException {
        ResultTemplate resultTemplate = ResultTemplate.DEFAULT;
        List tokens = this.contentNameQueryTokenizer.tokenize(searchParameters.getQuery());
        if (tokens.isEmpty()) {
            throw new SearchServiceException();
        }
        HashMap<String, String> params = new HashMap<String, String>();
        if (searchParameters.getPreferredSpaceKey() != null) {
            params.put("preferredSpaceKey", searchParameters.getPreferredSpaceKey());
        }
        SearchResultEntityList list = new SearchResultEntityList();
        if (groupResults) {
            if (!StringUtils.isEmpty((CharSequence)searchParameters.getType())) {
                resultTemplate = new ResultTemplate(){
                    {
                        StringTokenizer tok = new StringTokenizer(searchParameters.getType(), ",");
                        while (tok.hasMoreTokens()) {
                            String token = tok.nextToken();
                            if (!StringUtils.isNotBlank((CharSequence)token)) continue;
                            this.addCategory(Category.getCategory((String)token), maxResultsPerGroup == null ? 7 : maxResultsPerGroup);
                        }
                    }
                };
            }
            Map categoryListMap = this.contentNameSearcher.search(tokens, resultTemplate, Attachment.Type.getTypes(searchParameters.getAttachmentType()), searchParameters.isSearchParentName(), startIndex, pageSize, params, this.getSpaceKeys(searchParameters));
            ArrayList<SearchResultGroupEntity> groups = new ArrayList<SearchResultGroupEntity>(categoryListMap.size());
            for (Map.Entry entry : categoryListMap.entrySet()) {
                Category category = (Category)entry.getKey();
                SearchResultGroupEntity groupEntity = new SearchResultGroupEntity();
                groupEntity.setName(category.getName());
                List<SearchResultEntity> resultList = this.toSearchResultEntityList((List)entry.getValue());
                groupEntity.setResults(resultList);
                if (resultList.size() <= 0) continue;
                groups.add(groupEntity);
            }
            list.setGroups(groups);
        } else {
            List searchResultList = this.contentNameSearcher.searchNoCategorisation(tokens, resultTemplate, Attachment.Type.getTypes(searchParameters.getAttachmentType()), searchParameters.isSearchParentName(), startIndex, pageSize, params, this.getSpaceKeys(searchParameters));
            list.setResults(this.toSearchResultEntityList(searchResultList));
        }
        return list;
    }

    @Override
    public SearchResultEntityList fullSearch(RestSearchParameters restSearchParameters, Integer startIndex, Integer pageSize) throws SearchServiceException {
        SearchResults searchResults;
        SearchQueryParameters params = new SearchQueryParameters(restSearchParameters.getQuery());
        params.setSpaceKeys(ListUtils.createSetOfNonEmptyElementsFromStringArray((String[])this.getSpaceKeys(restSearchParameters)));
        params.setAttachmentTypes(Attachment.Type.getTypes(restSearchParameters.getAttachmentType()));
        params.setLabels(restSearchParameters.getLabel());
        if (StringUtils.isNotBlank((CharSequence)restSearchParameters.getType())) {
            HashSet<ContentTypeEnum> contentTypes = new HashSet<ContentTypeEnum>();
            StringTokenizer tok = new StringTokenizer(restSearchParameters.getType(), ",");
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation((String)token);
                if (contentType == null) continue;
                contentTypes.add(contentType);
            }
            if (!contentTypes.isEmpty()) {
                params.setContentTypes(contentTypes);
            }
        }
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(params, startIndex.intValue(), Math.min(50, pageSize == null ? 50 : pageSize));
        try {
            searchResults = this.searchManager.search(search);
        }
        catch (InvalidSearchException | IllegalArgumentException e) {
            throw new SearchServiceException(e);
        }
        return this.makeSearchResultsEntityList(searchResults);
    }

    private SearchResultEntityList makeSearchResultsEntityList(SearchResults searchResults) {
        List<SearchResultEntity> resultList = this.toSearchResultEntityList(searchResults);
        SearchResultEntityList list = new SearchResultEntityList();
        list.setResults(resultList);
        list.setTotalSize(searchResults.getUnfilteredResultsCount());
        return list;
    }

    private SearchResultEntityList makeSearchResultsEntityListRemovePIDuplicate(SearchResults searchResults) {
        List<SearchResultEntity> resultList = this.toSearchResultEntityList(searchResults);
        HashSet usernameSet = new HashSet();
        List<SearchResultEntity> result = resultList.stream().filter(resultEntity -> {
            String username = ((ContentEntity)resultEntity).getUsername();
            if (!usernameSet.contains(username)) {
                usernameSet.add(username);
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        SearchResultEntityList list = new SearchResultEntityList();
        list.setResults(result);
        list.setTotalSize(result.size());
        return list;
    }

    private String[] getSpaceKeys(RestSearchParameters restSearchParameters) {
        if (StringUtils.isBlank((CharSequence)restSearchParameters.getSpaceKey())) {
            return null;
        }
        return restSearchParameters.getSpaceKey().split("\\s*,\\s*");
    }

    private List<SearchResultEntity> toSearchResultEntityList(List<com.atlassian.confluence.search.contentnames.SearchResult> searchResultList) {
        ArrayList<SearchResultEntity> resultEntityList = new ArrayList<SearchResultEntity>(searchResultList.size());
        for (com.atlassian.confluence.search.contentnames.SearchResult result : searchResultList) {
            SearchResultEntity resultEntity = this.toSearchResultEntity(result);
            if (resultEntity == null) continue;
            resultEntityList.add(resultEntity);
        }
        return resultEntityList;
    }

    private SearchResultEntity toSearchResultEntity(com.atlassian.confluence.search.contentnames.SearchResult result) {
        return this.entityBuilderFactory.createBuilder(result.getContentType()).build(result);
    }

    private List<SearchResultEntity> toSearchResultEntityList(SearchResults searchResults) {
        ArrayList<SearchResultEntity> resultList = new ArrayList<SearchResultEntity>(searchResults.size());
        for (SearchResult r : searchResults) {
            SearchResultEntity resultEntity;
            if (r.getType() == null || (resultEntity = this.toSearchResultEntity(r)) == null) continue;
            resultList.add(resultEntity);
        }
        return resultList;
    }

    private SearchResultEntity toSearchResultEntity(SearchResult result) {
        return this.entityBuilderFactory.createBuilder(result.getType()).build(result);
    }
}

