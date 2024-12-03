/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.actions.json.ContentNameSearchResult
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchContext
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.quicknav.resources;

import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/search")
public class QuickNavResource {
    private final ContentNameSearchService contentNameSearchService;

    public QuickNavResource(ContentNameSearchService contentNameSearchService) {
        this.contentNameSearchService = contentNameSearchService;
    }

    @GET
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response getQuickNavResults(@Context HttpServletRequest httpServletRequest, @QueryParam(value="query") String query, @QueryParam(value="type") List<String> types, @QueryParam(value="spaceKey") String spaceKey, @DefaultValue(value="-1") @QueryParam(value="maxPerCategory") int maxPerCategory, @DefaultValue(value="-1") @QueryParam(value="limit") int limit) {
        ContentNameSearchResult result = this.contentNameSearchService.search(query, new ContentNameSearchContext(types, spaceKey, maxPerCategory, httpServletRequest, limit));
        return Response.ok((Object)result).build();
    }
}

