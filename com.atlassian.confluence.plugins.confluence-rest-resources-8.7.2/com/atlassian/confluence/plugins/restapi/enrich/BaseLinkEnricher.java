/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.restapi.enrich.AbstractLinkEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.CollectionEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestObject;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

class BaseLinkEnricher
extends AbstractLinkEnricher
implements EntityEnricher,
CollectionEnricher {
    private static final String BASE_LINK = "base";
    private static final String CONTEXT_PATH = "context";

    public BaseLinkEnricher(RestNavigationService navBuilderService, GraphQL graphql) {
        super(navBuilderService, graphql);
    }

    @Override
    public boolean isRecursive() {
        return false;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        return super.getEnrichedPropertyTypes(BASE_LINK, CONTEXT_PATH);
    }

    @Override
    public void enrich(@NonNull RestEntity entity, @NonNull SchemaType schemaType) {
        this.enrichObject((RestObject)entity, schemaType);
    }

    @Override
    public void enrich(@NonNull RestList enrich, @NonNull SchemaType schemaType) {
        this.enrichObject((RestObject)enrich, schemaType);
    }

    private void enrichObject(RestObject enrich, SchemaType schemaType) {
        this.enrichWithBaseLink(enrich, schemaType);
        this.enrichWithContextPath(enrich, schemaType);
    }

    private void enrichWithBaseLink(RestObject entity, SchemaType schemaType) {
        this.enrichWithLink(entity, BASE_LINK, this.navigation().baseUrl(), schemaType);
    }

    private void enrichWithContextPath(RestObject entity, SchemaType schemaType) {
        this.enrichWithLink(entity, CONTEXT_PATH, this.navigation().contextPath(), schemaType);
    }
}

