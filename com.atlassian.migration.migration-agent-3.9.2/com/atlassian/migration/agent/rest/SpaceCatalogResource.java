/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.migration.agent.dto.SpaceSearchResultDto;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.service.impl.SpaceCatalogService;
import com.atlassian.migration.agent.service.impl.SpaceTypeFilter;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sun.jersey.spi.container.ResourceFilters;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="space")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Produces(value={"application/json"})
public class SpaceCatalogResource {
    private final SpaceCatalogService spaceService;

    public SpaceCatalogResource(SpaceCatalogService spaceService) {
        this.spaceService = spaceService;
    }

    @GET
    @Path(value="/")
    public SpaceSearchResultDto getSpaces(@QueryParam(value="cloudId") String cloudId, @QueryParam(value="query") String nameQuery, @QueryParam(value="spaceType") String spaceType, @QueryParam(value="startIndex") int startIndex, @Nullable @QueryParam(value="statuses") String statuses, @Nullable @QueryParam(value="pageSize") Integer pageSize, @Nullable @QueryParam(value="sortKey") String sortKey, @Nullable @QueryParam(value="lastEditedStartDate") String lastEditedStartDateISO8601, @Nullable @QueryParam(value="lastEditedEndDate") String lastEditedEndDateISO8601, @DefaultValue(value="ASC") @QueryParam(value="sortOrder") SortOrder sortOrder) throws InvalidSearchException {
        if (pageSize == null) {
            pageSize = Integer.MAX_VALUE;
        }
        Preconditions.checkNotNull((Object)cloudId, (Object)"cloudId must be set");
        Preconditions.checkArgument((startIndex >= 0 ? 1 : 0) != 0, (Object)"startIndex must be a positive integer.");
        Preconditions.checkArgument((pageSize > 0 ? 1 : 0) != 0, (Object)"pageSize must be at least 1");
        SpaceTypeFilter spaceTypeFilter = SpaceCatalogResource.toSpaceTypeFilter(spaceType);
        Instant lastEditedStartDate = this.maybeParseISO8601DateString(lastEditedStartDateISO8601);
        Instant lastEditedEndDate = this.maybeParseISO8601DateString(lastEditedEndDateISO8601);
        if (lastEditedEndDate != null && lastEditedStartDate != null) {
            Preconditions.checkArgument((boolean)lastEditedStartDate.isBefore(lastEditedEndDate), (Object)"The query parameter 'lastEditedStartDate' must refer to an earlier date than 'lastEditedEndDate'.");
        }
        return this.spaceService.getSpaces(cloudId, nameQuery, spaceTypeFilter, this.parseCommaDelimitedStatusesToArray(statuses), startIndex, pageSize, sortKey, sortOrder, lastEditedStartDate, lastEditedEndDate);
    }

    private Instant maybeParseISO8601DateString(String maybeDateString) {
        try {
            return Strings.isNullOrEmpty((String)maybeDateString) ? null : Instant.parse(maybeDateString);
        }
        catch (Exception e) {
            throw new WebApplicationException((Throwable)e, Response.Status.BAD_REQUEST);
        }
    }

    private List<String> parseCommaDelimitedStatusesToArray(String value) {
        try {
            if (Strings.isNullOrEmpty((String)value)) {
                return Collections.emptyList();
            }
            String decodedString = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
            return Arrays.asList(decodedString.split(","));
        }
        catch (Exception e) {
            throw new WebApplicationException((Throwable)e, Response.Status.BAD_REQUEST);
        }
    }

    private static SpaceTypeFilter toSpaceTypeFilter(String value) {
        if (Strings.isNullOrEmpty((String)value)) {
            return SpaceTypeFilter.ALL;
        }
        try {
            return SpaceTypeFilter.valueOf(value.trim().toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
    }
}

