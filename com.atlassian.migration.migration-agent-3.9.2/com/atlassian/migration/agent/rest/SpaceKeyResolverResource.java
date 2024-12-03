/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="spaces/resolver")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class SpaceKeyResolverResource {
    static final int MAX_PAGE_SIZE = 500;
    private final ContentService contentService;

    public SpaceKeyResolverResource(ContentService contentService) {
        this.contentService = contentService;
    }

    @POST
    @Path(value="/pages")
    public Response bulkResolvePageIdsToSpaceKeys(List<Long> pageIds) {
        if (pageIds.size() > 500) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto("Maximum number of pageIds to resolve: 500")).build();
        }
        List pages = this.contentService.find(new Expansion[]{new Expansion("space")}).withId(this.fromLongs(pageIds)).fetchMany(ContentType.PAGE, (PageRequest)new SimplePageRequest(0, pageIds.size())).getResults();
        Map<Long, String> pageIdToSpaceKey = pages.stream().collect(Collectors.toMap(page -> page.getId().asLong(), page -> page.getSpace().getKey()));
        return Response.ok(pageIdToSpaceKey).build();
    }

    private Set<ContentId> fromLongs(Iterable<Long> ids) {
        return StreamSupport.stream(ids.spliterator(), false).map(ContentId::of).collect(Collectors.toSet());
    }
}

