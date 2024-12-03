/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.CreatorQuery
 *  com.atlassian.confluence.search.v2.sort.CreatedSort
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.createcontent.SpaceUtils;
import com.atlassian.confluence.plugins.createcontent.rest.SpaceResultsEntity;
import com.atlassian.confluence.plugins.createcontent.rest.SpaceResultsEntityBuilder;
import com.atlassian.confluence.plugins.createcontent.services.SpaceCollectionService;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSpaceCollectionService
implements SpaceCollectionService {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpaceCollectionService.class);
    private final SpacePermissionManager spacePermissionManager;
    private final SpaceManager spaceManager;
    private final LabelManager labelManager;
    private final SearchManager searchManager;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    @Autowired
    public DefaultSpaceCollectionService(@ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport LabelManager labelManager, @ComponentImport SearchManager searchManager, @ComponentImport SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.spacePermissionManager = spacePermissionManager;
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
        this.searchManager = searchManager;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    @Override
    public Map<String, SpaceResultsEntity> getSpaces(List<String> promotedSpaceKeys, int promotedSpacesLimit, int otherSpacesLimit, String spacePermission) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        SpaceResultsEntityBuilder promotedSpaces = this.getPromotedSpaces(user, promotedSpaceKeys, promotedSpacesLimit, spacePermission);
        SpaceResultsEntityBuilder otherSpaces = this.getOtherSpaces(user, otherSpacesLimit, promotedSpaces.getSpaces(), spacePermission);
        return ImmutableMap.of((Object)"promotedSpaces", (Object)promotedSpaces.build(), (Object)"otherSpaces", (Object)otherSpaces.build());
    }

    private SpaceResultsEntityBuilder getPromotedSpaces(ConfluenceUser user, List<String> requiredSpaceKeys, int promotedSpacesLimit, String spacePermission) {
        Predicate<Space> spacePermissionsFilter = SpaceUtils.editableSpaceFilter(user, this.spacePermissionManager, spacePermission);
        SpaceResultsEntityBuilder spaceResultsBuilder = new SpaceResultsEntityBuilder(promotedSpacesLimit, spacePermissionsFilter);
        if (requiredSpaceKeys != null && !requiredSpaceKeys.isEmpty()) {
            spaceResultsBuilder.addSpaces(this.getRequiredSpaces(requiredSpaceKeys, promotedSpacesLimit));
        }
        if (user != null) {
            Space personalSpace = this.spaceManager.getPersonalSpace(user);
            if (personalSpace != null) {
                spaceResultsBuilder.addSpaces(personalSpace);
            }
            spaceResultsBuilder.addSpaces(this.getRecentContentSpaces(user)).addSpaces(this.labelManager.getFavouriteSpaces(user.getName()));
        }
        return spaceResultsBuilder;
    }

    private SpaceResultsEntityBuilder getOtherSpaces(ConfluenceUser user, int resultsLimit, Collection<Space> excludedSpaces, String spacePermission) {
        int editableSpacesQueryLimit = resultsLimit + excludedSpaces.size() + 1;
        ArrayList editableSpaces = Lists.newArrayList(SpaceUtils.getEditableSpaces(user, editableSpacesQueryLimit, this.spaceManager, spacePermission));
        Predicate spacesFilter = Predicates.not((Predicate)Predicates.in(excludedSpaces));
        editableSpaces.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return new SpaceResultsEntityBuilder(resultsLimit, (Predicate<Space>)spacesFilter).addSpaces(editableSpaces);
    }

    private List<Space> getRequiredSpaces(List<String> requiredSpaceKeys, int promotedSpacesLimit) {
        SpacesQuery query = SpacesQuery.newQuery().withSpaceKeys(requiredSpaceKeys).build();
        List spaces = this.spaceManager.getSpaces(query).getPage(0, promotedSpacesLimit);
        spaces.sort(DefaultSpaceCollectionService.sortByKeyOrder(requiredSpaceKeys));
        return spaces;
    }

    private static Comparator<Space> sortByKeyOrder(List<String> keyOrder) {
        return (space1, space2) -> keyOrder.indexOf(space1.getKey()) - keyOrder.indexOf(space2.getKey());
    }

    private Collection<Space> getRecentContentSpaces(ConfluenceUser user) {
        LinkedHashSet spaces = Sets.newLinkedHashSet();
        for (Searchable searchable : this.searchForRecentContent(user)) {
            Space space;
            if (!(searchable instanceof Spaced) || (space = ((Spaced)searchable).getSpace()) == null) continue;
            spaces.add(space);
        }
        return spaces;
    }

    private Iterable<Searchable> searchForRecentContent(ConfluenceUser user) {
        try {
            ContentSearch search = new ContentSearch(this.recentSearchQuery(user), (SearchSort)CreatedSort.DESCENDING, 0, 25);
            return this.searchManager.searchEntities((ISearch)search, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        }
        catch (Exception e) {
            log.error("Error when searching for recent content", (Throwable)e);
            return Collections.emptyList();
        }
    }

    private SearchQuery recentSearchQuery(ConfluenceUser user) {
        return (SearchQuery)BooleanQuery.builder().addFilter(this.siteSearchPermissionsQueryFactory.create()).addMust((Object[])new SearchQuery[]{new CreatorQuery(user.getKey()), new ContentTypeQuery(EnumSet.of(ContentTypeEnum.PAGE, ContentTypeEnum.BLOG))}).build();
    }
}

