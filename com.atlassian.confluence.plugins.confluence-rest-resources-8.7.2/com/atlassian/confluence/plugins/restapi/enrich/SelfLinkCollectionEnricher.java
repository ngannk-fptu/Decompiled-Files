/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  javax.ws.rs.core.UriBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.plugins.restapi.enrich.AbstractLinkEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.CollectionEnricher;
import com.atlassian.confluence.plugins.restapi.graphql.ReflectionUtil;
import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestObject;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

class SelfLinkCollectionEnricher
extends AbstractLinkEnricher
implements CollectionEnricher {
    public SelfLinkCollectionEnricher(RestNavigationService navService, GraphQL graphql) {
        super(navService, graphql);
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        if (PageRequest.class.isAssignableFrom(ReflectionUtil.getClazz(type))) {
            return super.getEnrichedPropertyTypes("self");
        }
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestList enrich, @NonNull SchemaType schemaType) {
        PageRequest request = enrich.getPageRequest();
        if (request instanceof RestPageRequest) {
            RestPageRequest restPageRequest = (RestPageRequest)request;
            UriBuilder builder = restPageRequest.getUriBuilder().replaceQueryParam("limit", new Object[0]).replaceQueryParam("start", new Object[0]);
            this.enrichWithLink((RestObject)enrich, "self", this.navigation().fromUriBuilder(builder).buildCanonicalAbsolute(), schemaType);
        }
    }
}

