/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
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

import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
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

class NavigationCollectionEnricher
extends AbstractLinkEnricher
implements CollectionEnricher {
    private static final String PREV_LINK = "prev";
    private static final String NEXT_LINK = "next";

    public NavigationCollectionEnricher(RestNavigationService navigationService, GraphQL graphql) {
        super(navigationService, graphql);
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        Class clazz = ReflectionUtil.getClazz(type);
        if (PageRequest.class.isAssignableFrom(clazz) || RestList.class.isAssignableFrom(clazz)) {
            return super.getEnrichedPropertyTypes(NEXT_LINK, PREV_LINK);
        }
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestList enrich, @NonNull SchemaType schemaType) {
        PageRequest pageRequest = enrich.getPageRequest();
        if (pageRequest == null) {
            return;
        }
        if (pageRequest instanceof RestPageRequest) {
            RestPageRequest request = (RestPageRequest)enrich.getPageRequest();
            UriBuilder uriBuilder = request.getUriBuilder();
            this.enrichWithRequestAndUri(enrich, (PageRequest)request, uriBuilder, schemaType);
        } else {
            Navigation.Builder navBuilder = enrich.resolveNavigation((NavigationService)this.navigationService);
            if (navBuilder == null) {
                return;
            }
            this.enrichWithRequestAndUri(enrich, pageRequest, UriBuilder.fromUri((String)navBuilder.buildAbsolute()), schemaType);
        }
    }

    private void enrichWithRequestAndUri(RestList enrich, PageRequest request, UriBuilder uriBuilder, SchemaType schemaType) {
        uriBuilder.replaceQueryParam("limit", new Object[]{request.getLimit()});
        if (request.getCursor() == null) {
            this.enrichWithNavLinkOffset(enrich, request, uriBuilder, schemaType);
        } else {
            this.enrichWithNavLinkCursor(enrich, request, uriBuilder, schemaType);
        }
    }

    private void enrichWithNavLinkOffset(RestList enrich, PageRequest request, UriBuilder uriBuilder, SchemaType schemaType) {
        if (enrich.getPageResponse().hasMore()) {
            this.enrichWithNavLinkOffset(enrich, NEXT_LINK, request.getStart() + request.getLimit(), uriBuilder, schemaType);
        }
        if (request.getStart() > 0) {
            int possiblePrev = request.getStart() - request.getLimit();
            if (possiblePrev < 0) {
                possiblePrev = 0;
                int limit = request.getStart();
                uriBuilder.replaceQueryParam("limit", new Object[]{limit});
            }
            this.enrichWithNavLinkOffset(enrich, PREV_LINK, possiblePrev, uriBuilder, schemaType);
        }
    }

    private void enrichWithNavLinkCursor(RestList enrich, PageRequest request, UriBuilder uriBuilder, SchemaType schemaType) {
        if (enrich.getPageResponse().hasMore() && enrich.getPageResponse().getNextCursor() != null) {
            Cursor nextCursor = enrich.getPageResponse().getNextCursor();
            this.enrichWithNavLinkCursor(enrich, NEXT_LINK, nextCursor, uriBuilder, schemaType);
        }
        if (!request.getCursor().isEmpty() && enrich.getPageResponse().getPrevCursor() != null) {
            Cursor prevCursor = enrich.getPageResponse().getPrevCursor();
            this.enrichWithNavLinkCursor(enrich, PREV_LINK, prevCursor, uriBuilder, schemaType);
        }
    }

    private void enrichWithNavLinkOffset(RestList enrich, String linkName, int start, UriBuilder uriBuilder, SchemaType schemaType) {
        uriBuilder.replaceQueryParam("start", new Object[]{start});
        this.enrichWithNavLink(enrich, linkName, uriBuilder, schemaType);
    }

    private void enrichWithNavLinkCursor(RestList enrich, String linkName, Cursor cursor, UriBuilder uriBuilder, SchemaType schemaType) {
        uriBuilder.replaceQueryParam("cursor", new Object[]{cursor.toString()});
        this.enrichWithNavLink(enrich, linkName, uriBuilder, schemaType);
    }

    private void enrichWithNavLink(RestList enrich, String linkName, UriBuilder uriBuilder, SchemaType schemaType) {
        this.enrichWithLink((RestObject)enrich, linkName, this.navigation().fromUriBuilder(uriBuilder).buildRelative(), schemaType);
    }
}

