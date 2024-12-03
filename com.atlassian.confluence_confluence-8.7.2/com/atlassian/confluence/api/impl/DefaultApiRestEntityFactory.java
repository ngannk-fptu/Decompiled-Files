/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.core.ApiRestEntityFactory;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultApiRestEntityFactory
implements ApiRestEntityFactory {
    private final ContentFactory contentFactory;
    private final SpaceFactory spaceFactory;
    private final RestEntityEnrichmentManager restEntityEnrichmentManager;

    public DefaultApiRestEntityFactory(ContentFactory contentFactory, SpaceFactory spaceFactory, RestEntityEnrichmentManager restEntityEnrichmentManager) {
        this.contentFactory = contentFactory;
        this.spaceFactory = spaceFactory;
        this.restEntityEnrichmentManager = restEntityEnrichmentManager;
    }

    public RestEntity buildRestEntityFrom(ContentEntityObject entity, Expansions expansions) {
        return this.enrichOrThrow(this.contentFactory.buildFrom(entity, expansions));
    }

    @Override
    public RestEntity<com.atlassian.confluence.api.model.content.Space> buildRestEntityFrom(Space space, Expansions expansions) {
        return this.enrichOrThrow(this.spaceFactory.buildFrom(space, expansions));
    }

    @Override
    public Iterable<RestEntity> buildRestEntityFromContent(Iterable<? extends ContentEntityObject> entities, Expansions expansions) {
        return this.enrichIterable(this.contentFactory.buildFrom(entities, expansions));
    }

    @Override
    public Iterable<RestEntity> buildRestEntityFromSpaces(Iterable<Space> spaces, Expansions expansions) {
        return this.enrichIterable(this.spaceFactory.buildFrom(spaces, expansions));
    }

    @Override
    public boolean isEnrichableList(Class listType) {
        return this.restEntityEnrichmentManager.isEnrichableList(listType);
    }

    @Override
    public boolean isEnrichableEntity(Class entityType) {
        return this.restEntityEnrichmentManager.isEnrichableEntity(entityType);
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(Type type, boolean root) {
        return this.restEntityEnrichmentManager.getEnrichedPropertyTypes(type, root);
    }

    @Override
    public Object convertAndEnrich(Object entity, ApiRestEntityFactory.SchemaType schemaType) {
        return this.restEntityEnrichmentManager.convertAndEnrich(entity, DefaultApiRestEntityFactory.convert(schemaType));
    }

    private <T> RestEntity<T> enrichOrThrow(T content) {
        Object obj = this.convertAndEnrich(content, ApiRestEntityFactory.SchemaType.REST);
        if (!(obj instanceof RestEntity)) {
            throw new IllegalArgumentException("Entity was not converted to RestEntity got : " + obj);
        }
        return (RestEntity)obj;
    }

    private static SchemaType convert(ApiRestEntityFactory.SchemaType schemaType) {
        switch (schemaType) {
            case REST: {
                return SchemaType.REST;
            }
            case GRAPHQL: {
                return SchemaType.GRAPHQL;
            }
        }
        throw new IllegalArgumentException();
    }

    private <T> Iterable<RestEntity> enrichIterable(Iterable<T> modelEntities) {
        return ImmutableList.copyOf(modelEntities).stream().map(this::enrichOrThrow).collect(Collectors.toList());
    }
}

