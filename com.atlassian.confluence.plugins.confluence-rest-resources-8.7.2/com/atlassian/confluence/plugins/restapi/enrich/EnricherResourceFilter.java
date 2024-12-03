/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.restapi.filters.AbstractResponseResourceFilter;
import com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

class EnricherResourceFilter
extends AbstractResponseResourceFilter {
    private final RestEntityEnrichmentManager enricherVisitor;

    EnricherResourceFilter(RestEntityEnrichmentManager enricherVisitor) {
        this.enricherVisitor = enricherVisitor;
    }

    public ContainerResponseFilter getResponseFilter() {
        return (request, response) -> {
            Object entity = response.getEntity();
            if (entity == null && response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
                response.setEntity((Object)"");
                return response;
            }
            Object restEntity = this.enricherVisitor.convertAndEnrich(entity, SchemaType.REST);
            response.setEntity(restEntity);
            return response;
        };
    }
}

