/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationAware
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SelfLinkEntityEnricher
extends AbstractLinkEnricher
implements EntityEnricher {
    private static final Logger log = LoggerFactory.getLogger(SelfLinkEntityEnricher.class);

    public SelfLinkEntityEnricher(RestNavigationService navBuilderService, GraphQL graphql) {
        super(navBuilderService, graphql);
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        if (NavigationAware.class.isAssignableFrom(ReflectionUtil.getClazz(type))) {
            return super.getEnrichedPropertyTypes("self");
        }
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestEntity entity, @NonNull SchemaType schemaType) {
        log.debug("Enriching {}", (Object)entity);
        Object delegate = entity.getDelegate();
        if (delegate instanceof Collapsed) {
            return;
        }
        if (!(delegate instanceof NavigationAware)) {
            log.info("Unable to locate Navigation.Builder for RestEntity with delegate class: {}", (Object)delegate.getClass().getName());
            return;
        }
        Navigation.Builder navBuilder = ((NavigationAware)delegate).resolveNavigation((NavigationService)this.navigationService);
        if (navBuilder != null) {
            this.enrichWithLink((RestObject)entity, "self", navBuilder.buildAbsolute(), schemaType);
        }
    }
}

