/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.serialization.RestEnrichable
 *  com.atlassian.confluence.api.serialization.RestEnrichable$Helper
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.services.RestEntityFactory
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.services.RestEntityFactory;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import com.google.common.collect.ImmutableList;
import java.util.List;

public final class RestEntityConverter {
    private final RestEntityFactory restEntityFactory;

    public RestEntityConverter(RestEntityFactory restEntityFactory) {
        this.restEntityFactory = restEntityFactory;
    }

    public Object convert(Object entity) {
        return this.convert(entity, SchemaType.REST);
    }

    public Object convert(Object entity, SchemaType schemaType) {
        if (this.isEnrichableList(entity)) {
            return this.convertToRestEntityList((PageResponse)entity, schemaType);
        }
        if (entity != null && this.isEnrichableEntity(entity.getClass())) {
            return this.restEntityFactory.create(entity, schemaType == SchemaType.GRAPHQL);
        }
        return entity;
    }

    public boolean isEnrichableList(Class listType) {
        return RestList.class.isAssignableFrom(listType) || PageResponse.class.isAssignableFrom(listType);
    }

    public boolean isEnrichableEntity(Class entityType) {
        return entityType.isAnnotationPresent(RestEnrichable.class);
    }

    private boolean isEnrichableList(Object entity) {
        return entity instanceof RestList || entity instanceof PageResponse && RestEnrichable.Helper.isAnnotationOnClass((Object)entity);
    }

    private RestList convertToRestEntityList(PageResponse initialList, SchemaType schemaType) {
        ImmutableList.Builder listBuilder = ImmutableList.builder();
        for (Object entity : initialList) {
            listBuilder.add(this.convert(entity, schemaType));
        }
        ImmutableList builtList = listBuilder.build();
        RestList restList = RestList.newRestList((PageRequest)initialList.getPageRequest()).results((List)builtList, initialList.getNextCursor(), initialList.getPrevCursor(), initialList.hasMore()).build();
        if (initialList instanceof SearchPageResponse) {
            SearchPageResponse searchPageResponse = (SearchPageResponse)initialList;
            restList.putProperty("totalSize", (Object)searchPageResponse.totalSize());
            restList.putProperty("cqlQuery", (Object)searchPageResponse.getCqlQuery());
            restList.putProperty("searchDuration", (Object)searchPageResponse.getSearchDuration());
            if (searchPageResponse.archivedResultCount().isPresent()) {
                restList.putProperty("archivedResultCount", searchPageResponse.archivedResultCount().get());
            }
        }
        if (initialList instanceof ContentRestrictionsPageResponse) {
            ContentRestrictionsPageResponse restrictionsResponse = (ContentRestrictionsPageResponse)initialList;
            restList.putProperty("links", (Object)restrictionsResponse.getLinks());
            restList.putProperty("restrictionsHash", (Object)restrictionsResponse.getRestrictionsHash());
        }
        if (initialList instanceof RestList) {
            restList.putProperties(((RestList)initialList).properties());
        }
        return restList;
    }
}

