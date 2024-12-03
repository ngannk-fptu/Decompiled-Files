/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.plugins.cql.impl.factory.ModelResultFactory;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceSearchResultsFactory
implements ModelResultFactory<Space> {
    private static final Set<String> requiredIndexFields = ImmutableSet.of((Object)SearchFieldNames.SPACE_KEY, (Object)SearchFieldNames.SPACE_NAME, (Object)SearchFieldNames.SPACE_TYPE);
    private final SpaceService spaceService;

    @Autowired
    public SpaceSearchResultsFactory(@ComponentImport SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @Override
    public Map<SearchResult, Space> buildFrom(Iterable<SearchResult> results, Expansions expansions) {
        if (Iterables.isEmpty(results)) {
            return Collections.emptyMap();
        }
        if (expansions.isEmpty()) {
            return this.buildFromSearchResults(results);
        }
        return this.buildFromService(results, expansions);
    }

    private Map<SearchResult, Space> buildFromService(Iterable<SearchResult> results, Expansions expansions) {
        Iterable keys = Iterables.transform(results, SearchResult::getSpaceKey);
        PageResponse spaces = this.spaceService.find(expansions.toArray()).withKeys((String[])Iterables.toArray((Iterable)keys, String.class)).fetchMany((PageRequest)new SimplePageRequest(0, Iterables.size(results)));
        ImmutableMap spaceByKey = Maps.uniqueIndex((Iterable)spaces, Space::getKey);
        LinkedHashMap spaceByResult = Maps.newLinkedHashMap();
        for (SearchResult result : results) {
            Space space = (Space)spaceByKey.get(result.getSpaceKey());
            if (space == null) continue;
            spaceByResult.put(result, space);
        }
        return spaceByResult;
    }

    private Map<SearchResult, Space> buildFromSearchResults(Iterable<SearchResult> results) {
        LinkedHashMap spaceByResult = Maps.newLinkedHashMap();
        for (SearchResult result : results) {
            spaceByResult.put(result, Space.builder().key(result.getSpaceKey()).name(result.getSpaceName()).type(SpaceType.forName((String)result.getField(SearchFieldNames.SPACE_TYPE))).build());
        }
        return spaceByResult;
    }

    @Override
    public boolean handles(ContentTypeEnum contentType) {
        return ContentTypeEnum.SPACE_DESCRIPTION.equals((Object)contentType) || ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION.equals((Object)contentType);
    }

    @Override
    public Set<String> getRequiredIndexFields() {
        return requiredIndexFields;
    }
}

