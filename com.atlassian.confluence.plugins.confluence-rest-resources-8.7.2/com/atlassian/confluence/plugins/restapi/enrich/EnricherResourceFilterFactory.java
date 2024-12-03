/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.DefaultRestEntityFactory
 *  com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager
 *  com.google.common.collect.ImmutableList
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.restapi.enrich.DefaultRestEntityEnrichmentManager;
import com.atlassian.confluence.plugins.restapi.enrich.EnricherResourceFilter;
import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.DefaultRestEntityFactory;
import com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.List;

public final class EnricherResourceFilterFactory
implements ResourceFilterFactory {
    private final List<ResourceFilter> resourceFilters;

    public EnricherResourceFilterFactory(RestEntityEnrichmentManager restEntityEnrichmentManager) {
        this.resourceFilters = ImmutableList.builder().add((Object)new EnricherResourceFilter(restEntityEnrichmentManager)).build();
    }

    public EnricherResourceFilterFactory(RestNavigationService navBuilderService, GraphQL graphql, DefaultRestEntityFactory restEntityFactory) {
        this(new DefaultRestEntityEnrichmentManager(navBuilderService, graphql, restEntityFactory));
    }

    public List<ResourceFilter> create(AbstractMethod method) {
        return this.resourceFilters;
    }
}

