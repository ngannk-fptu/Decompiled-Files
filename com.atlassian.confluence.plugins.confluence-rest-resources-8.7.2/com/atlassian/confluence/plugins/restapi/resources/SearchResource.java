/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLNonNull
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
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
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.plugins.restapi.graphql.GraphQLOffsetCursor;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLNonNull;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Strings;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.map.ObjectMapper;

@AnonymousAllowed
@LimitRequestSize
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/search")
@GraphQLProvider
@PublicApi
public class SearchResource {
    private final CQLSearchService searchService;
    private final ObjectMapper mapper = new ObjectMapper();

    public SearchResource(@ComponentImport CQLSearchService searchService) {
        this.searchService = searchService;
    }

    @GET
    @GraphQLName(value="search")
    public SearchPageResponse<SearchResult> search(@GraphQLName(value="cql") @GraphQLNonNull @QueryParam(value="cql") String cql, @GraphQLName(value="cqlcontext") @QueryParam(value="cqlcontext") String cqlcontext, @GraphQLName(value="excerpt") @QueryParam(value="excerpt") @DefaultValue(value="highlight") String excerpt, @GraphQLExpansionParam @QueryParam(value="expand") @DefaultValue(value="") String expand, @GraphQLName(value="offset") @QueryParam(value="start") int offset, @GraphQLName(value="after") String afterOffset, @GraphQLName(value="first") @QueryParam(value="limit") @DefaultValue(value="25") int limit, @GraphQLName(value="includeArchivedSpaces") @QueryParam(value="includeArchivedSpaces") @DefaultValue(value="false") boolean includeArchivedSpaces, @Context UriInfo uriInfo) {
        if (Strings.isNullOrEmpty((String)cql)) {
            throw new BadRequestException("cql query parameter is required");
        }
        SearchContext searchContext = !Strings.isNullOrEmpty((String)cqlcontext) ? SearchContext.deserialize((String)cqlcontext, (ObjectMapper)this.mapper) : SearchContext.builder().build();
        SearchOptions searchOptions = SearchOptions.builder().searchContext(searchContext).excerptStrategy(excerpt).includeArchivedSpaces(includeArchivedSpaces).fireSearchPerformed(true).build();
        return this.searchService.search(cql, searchOptions, (PageRequest)new RestPageRequest(uriInfo, GraphQLOffsetCursor.parseOffset(offset, afterOffset), limit), ExpansionsParser.parse((String)expand));
    }
}

