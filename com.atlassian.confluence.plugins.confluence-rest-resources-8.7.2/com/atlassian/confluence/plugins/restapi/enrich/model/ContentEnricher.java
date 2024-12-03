/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich.model;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.plugins.restapi.enrich.AbstractLinkEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.plugins.restapi.graphql.ReflectionUtil;
import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.api.model.RestObject;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentEnricher
extends AbstractLinkEnricher
implements EntityEnricher {
    public ContentEnricher(RestNavigationService navigationService, GraphQL graphql) {
        super(navigationService, graphql);
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        if (!Content.class.isAssignableFrom(ReflectionUtil.getClazz(type))) {
            return Collections.emptyMap();
        }
        String[] linkTypes = (String[])LinkType.BUILT_IN.stream().map(LinkType::getType).toArray(String[]::new);
        return super.getEnrichedPropertyTypes(linkTypes);
    }

    @Override
    public void enrich(@NonNull RestEntity entity, @NonNull SchemaType schemaType) {
        if (!(entity.getDelegate() instanceof Content)) {
            return;
        }
        Object extensions = entity.getProperty("extensions");
        if (extensions instanceof RestEntity && ((RestEntity)extensions).properties().isEmpty()) {
            entity.removeProperty("extensions");
        }
        this.enrichLinks((RestObject)entity, schemaType);
    }
}

