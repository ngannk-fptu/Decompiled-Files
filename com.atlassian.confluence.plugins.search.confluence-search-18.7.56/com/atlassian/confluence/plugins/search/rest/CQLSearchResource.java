/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Strings
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.search.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.search.CQLSearchResult;
import com.atlassian.confluence.plugins.search.CQLSearcher;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Strings;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.map.ObjectMapper;

@ExperimentalApi
@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/cqlSearch")
@Internal
public class CQLSearchResource {
    private final CQLSearcher cqlSearcher;

    public CQLSearchResource(CQLSearcher cqlSearcher) {
        this.cqlSearcher = Objects.requireNonNull(cqlSearcher);
    }

    @GET
    public SearchPageResponse<CQLSearchResult> search(@QueryParam(value="cql") String cql, @QueryParam(value="cqlcontext") String cqlcontext, @QueryParam(value="excerpt") String excerpt, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @QueryParam(value="includeArchivedSpaces") @DefaultValue(value="false") boolean includeArchivedSpaces, @Context UriInfo uriInfo) {
        if (Strings.isNullOrEmpty((String)cql)) {
            throw new BadRequestException("cql query parameter is required");
        }
        SearchContext searchContext = SearchContext.builder().build();
        if (!Strings.isNullOrEmpty((String)cqlcontext)) {
            ObjectMapper mapper = new ObjectMapper();
            searchContext = SearchContext.deserialize((String)cqlcontext, (ObjectMapper)mapper);
        }
        SearchOptions searchOptions = SearchOptions.builder().searchContext(searchContext).excerptStrategy(excerpt).includeArchivedSpaces(includeArchivedSpaces).build();
        return this.cqlSearcher.getCqlSearchResults(cql, searchOptions, (PageRequest)new RestPageRequest(uriInfo, start, limit), ExpansionsParser.parseExperimental((String)expand));
    }
}

