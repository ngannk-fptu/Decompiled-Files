/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.nav.Navigation;
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

class EntityCollectionLinkEnricher
extends AbstractLinkEnricher
implements EntityEnricher {
    private static final String COLLECTION_LINK = "collection";

    public EntityCollectionLinkEnricher(RestNavigationService navService, GraphQL graphql) {
        super(navService, graphql);
    }

    @Override
    public boolean isRecursive() {
        return false;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        Class clazz = ReflectionUtil.getClazz(type);
        if (Content.class.isAssignableFrom(clazz) || Space.class.isAssignableFrom(clazz)) {
            return super.getEnrichedPropertyTypes(COLLECTION_LINK);
        }
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestEntity entity, @NonNull SchemaType schemaType) {
        Object model = entity.getDelegate();
        Navigation.Builder builder = null;
        if (model instanceof Content) {
            builder = this.navigation().collection((Content)model);
        } else if (model instanceof Space) {
            builder = this.navigation().collection((Space)model);
        }
        if (builder != null) {
            this.enrichWithLink((RestObject)entity, COLLECTION_LINK, builder.buildRelative(), schemaType);
        }
    }
}

