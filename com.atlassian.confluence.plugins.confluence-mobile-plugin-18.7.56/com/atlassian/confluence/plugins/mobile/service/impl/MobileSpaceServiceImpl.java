/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.hibernate.MobileContentQueryFactory;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import com.atlassian.confluence.plugins.mobile.service.MobileSpaceService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileSpaceServiceImpl
implements MobileSpaceService {
    private static final Logger log = LoggerFactory.getLogger(MobileSpaceServiceImpl.class);
    private static final int MAX_LIMIT_REQUEST = 1000;
    private static final int MAX_RECENTLY_REQUEST = 20;
    private static final String GET_APP_FAVOURITE_PRIORITY = "getAllFavourites";
    private final RecentlyViewedManager recentlyViewedManager;
    private final MobileSpaceConverter mobileSpaceConverter;
    private final SpaceManager spaceManager;
    private final CustomContentManager customContentManager;
    private final MobileAbstractPageConverter abstractPageConverter;
    private final PermissionManager permissionManager;
    private final PredefinedSearchBuilder predefinedSearchBuilder;
    private final SearchManager searchManager;

    @Autowired
    public MobileSpaceServiceImpl(@ComponentImport RecentlyViewedManager recentlyViewedManager, MobileSpaceConverter mobileSpaceConverter, @ComponentImport SpaceManager spaceManager, @ComponentImport CustomContentManager customContentManager, MobileAbstractPageConverter abstractPageConverter, @ComponentImport PermissionManager permissionManager, @ComponentImport PredefinedSearchBuilder predefinedSearchBuilder, @ComponentImport SearchManager searchManager) {
        this.recentlyViewedManager = recentlyViewedManager;
        this.mobileSpaceConverter = mobileSpaceConverter;
        this.spaceManager = spaceManager;
        this.customContentManager = customContentManager;
        this.abstractPageConverter = abstractPageConverter;
        this.permissionManager = permissionManager;
        this.predefinedSearchBuilder = predefinedSearchBuilder;
        this.searchManager = searchManager;
    }

    @Override
    public PageResponse<SpaceDto> getSpaces(String priority, Expansions expansions, Inclusions inclusions, PageRequest pageRequest) {
        List<SpaceDto> spaces;
        return PageResponseImpl.from(spaces, ((spaces = this.getRequestSpaces(priority, expansions, inclusions, pageRequest)).size() == pageRequest.getLimit() ? 1 : 0) != 0).pageRequest(pageRequest).build();
    }

    @Override
    public ContentDto getHomePage(String spaceKey, Expansions expansions) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null || space.getHomePage() == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space.getHomePage())) {
            throw new NotFoundException("Cannot find home page of this space key: " + spaceKey);
        }
        return this.abstractPageConverter.to((ContentEntityObject)space.getHomePage(), expansions);
    }

    @Override
    public Space getSuggestionSpace() {
        SpacesQuery spacesQuery;
        List spaces;
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        Space suggestionSpace = this.spaceManager.getPersonalSpace(loginUser);
        if (suggestionSpace == null) {
            PageResponse pageResponse = this.customContentManager.findByQueryAndFilter(MobileContentQueryFactory.findFavouriteSpacesByUserName(loginUser.getName()), true, LimitedRequestImpl.create((int)1000), s -> true);
            suggestionSpace = pageResponse.getResults().stream().filter(s -> this.permissionManager.hasCreatePermission((User)loginUser, s, Page.class)).findFirst().orElse(null);
        }
        if (suggestionSpace == null) {
            List recentlySpaces = this.recentlyViewedManager.getRecentlyViewedSpaces(loginUser.getKey().toString(), 20);
            suggestionSpace = recentlySpaces.stream().filter(s -> this.permissionManager.hasCreatePermission((User)loginUser, s, Page.class)).findFirst().orElse(null);
        }
        if (suggestionSpace == null && !(spaces = this.spaceManager.getSpaces(spacesQuery = SpacesQuery.newQuery().forUser((User)loginUser).withSpaceStatus(SpaceStatus.CURRENT).withPermission("EDITSPACE").build()).getPage(0, 1)).isEmpty()) {
            suggestionSpace = (Space)spaces.get(0);
        }
        return suggestionSpace;
    }

    private List<SpaceDto> getRequestSpaces(String priority, Expansions expansions, Inclusions inclusions, PageRequest pageRequest) {
        ArrayList<String> spaceProcessedKeys = new ArrayList<String>();
        HashMap<String, Pair<Space, SpaceDto.ResultType>> spaceMap = new HashMap<String, Pair<Space, SpaceDto.ResultType>>();
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        int startIndex = pageRequest.getStart();
        int totalNeededSpaces = startIndex + pageRequest.getLimit();
        if (inclusions.isInclude(SpaceDto.ResultType.FAVOURITE.getValue())) {
            PageResponse pageResponse = this.customContentManager.findByQueryAndFilter(MobileContentQueryFactory.findFavouriteSpacesByUserName(loginUser.getName().toLowerCase()), true, LimitedRequestImpl.create((int)1000), space -> this.permissionManager.hasPermission((User)loginUser, Permission.VIEW, space));
            this.addSpaces(pageResponse.getResults(), SpaceDto.ResultType.FAVOURITE, spaceProcessedKeys, spaceMap);
            if (spaceProcessedKeys.size() >= totalNeededSpaces && GET_APP_FAVOURITE_PRIORITY.equals(priority)) {
                return this.convertToSpaceDto(spaceProcessedKeys.subList(startIndex, spaceProcessedKeys.size()), spaceMap, expansions);
            }
        }
        if (spaceProcessedKeys.size() < totalNeededSpaces && inclusions.isInclude(SpaceDto.ResultType.RECENT.getValue())) {
            List recentlySpaces = this.recentlyViewedManager.getRecentlyViewedSpaces(loginUser.getKey().toString(), 20);
            this.addSpaces(recentlySpaces, SpaceDto.ResultType.RECENT, spaceProcessedKeys, spaceMap);
        }
        if (spaceProcessedKeys.size() < totalNeededSpaces && inclusions.isInclude(SpaceDto.ResultType.OTHER.getValue())) {
            int previousProcessedKeySize = spaceProcessedKeys.size();
            List<String> otherSpaceKeys = this.getOtherSpaceKeys(Math.max(0, startIndex - previousProcessedKeySize), previousProcessedKeySize + pageRequest.getLimit());
            if (!otherSpaceKeys.isEmpty()) {
                List<Space> otherSpaces = this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceKeys(otherSpaceKeys).unsorted().build()).getPage(0, otherSpaceKeys.size()).stream().sorted(Comparator.comparing(space -> space.getDisplayTitle().toLowerCase())).collect(Collectors.toList());
                this.addSpaces(otherSpaces, SpaceDto.ResultType.OTHER, spaceProcessedKeys, spaceMap);
            }
            if (inclusions.isOnlyInclude(SpaceDto.ResultType.OTHER.getValue())) {
                startIndex = 0;
            } else {
                int n = startIndex = startIndex > previousProcessedKeySize ? previousProcessedKeySize : startIndex;
            }
        }
        if (spaceProcessedKeys.size() <= startIndex) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(startIndex + pageRequest.getLimit(), spaceProcessedKeys.size());
        return this.convertToSpaceDto(spaceProcessedKeys.subList(startIndex, toIndex), spaceMap, expansions);
    }

    private void addSpaces(List<Space> sources, SpaceDto.ResultType resultType, List<String> spaceKeys, Map<String, Pair<Space, SpaceDto.ResultType>> spaceMap) {
        if (sources != null) {
            sources.forEach(space -> {
                if (!spaceKeys.contains(space.getKey())) {
                    spaceKeys.add(space.getKey());
                    spaceMap.put(space.getKey(), Pair.pair((Object)space, (Object)((Object)resultType)));
                }
            });
        }
    }

    private List<SpaceDto> convertToSpaceDto(List<String> spaceKeys, Map<String, Pair<Space, SpaceDto.ResultType>> spaceMap, Expansions expansions) {
        return spaceKeys.stream().map(spaceKey -> {
            Pair pair = (Pair)spaceMap.get(spaceKey);
            return this.mobileSpaceConverter.to((Space)pair.left(), (SpaceDto.ResultType)((Object)((Object)pair.right())), expansions);
        }).collect(Collectors.toList());
    }

    private List<String> getOtherSpaceKeys(int start, int limit) {
        SearchQueryParameters params = new SearchQueryParameters();
        params.setSort((SearchSort)TitleSort.ASCENDING);
        params.setIncludeArchivedSpaces(false);
        params.setContentType(ContentTypeEnum.SPACE_DESCRIPTION);
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(params, start, limit);
        try {
            SearchResults searchResults = this.searchManager.search(search);
            return StreamSupport.stream(searchResults.spliterator(), false).map(SearchResult::getSpaceKey).collect(Collectors.toList());
        }
        catch (InvalidSearchException e) {
            log.debug("Invalid search", (Throwable)e);
            return Collections.emptyList();
        }
    }
}

