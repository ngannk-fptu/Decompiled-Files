/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.resources.AbstractResource
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.spacedirectory.rest;

import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectory;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryEntity;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryEntityBuilder;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryScope;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.ArrayList;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/search")
@AnonymousAllowed
@Produces(value={"application/xml", "application/json"})
public class SpaceDirectoryResource
extends AbstractResource {
    private static final Logger log = LoggerFactory.getLogger(SpaceDirectoryResource.class);
    private static final int DEFAULT_MAX_PAGE_SIZE = 50;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private SearchManager searchManager;
    private SpaceDirectoryEntityBuilder builder;

    public SpaceDirectoryResource(UserAccessor userAccessor, SpacePermissionManager spacePermissionManager, PredefinedSearchBuilder predefinedSearchBuilder, SearchManager searchManager, SpaceDirectoryEntityBuilder builder) {
        super(userAccessor, spacePermissionManager);
        this.predefinedSearchBuilder = predefinedSearchBuilder;
        this.searchManager = searchManager;
        this.builder = builder;
    }

    @GET
    @AnonymousAllowed
    public Response doSearch(@QueryParam(value="query") String query, @DefaultValue(value="0") @QueryParam(value="startIndex") int startIndex, @QueryParam(value="pageSize") Integer pageSize, @QueryParam(value="label") Set<String> label, @QueryParam(value="type") String typeStr, @QueryParam(value="status") String status) {
        log.debug("Performing a space directory search for '{}' with labels {}", (Object)query, label);
        SpaceDirectory result = new SpaceDirectory();
        try {
            this.createRequestContext();
            SearchQueryParameters params = new SearchQueryParameters(query);
            params.setLabels(label);
            params.setExtraFields(Set.of(SearchFieldNames.SPACE_KEY));
            this.setSpaceStatusParams(status, params);
            SpaceDirectoryScope scope = SpaceDirectoryScope.toScope(typeStr);
            params.setContentTypes(scope.getContentTypes());
            params.setSort((SearchSort)TitleSort.ASCENDING);
            ISearch search = this.predefinedSearchBuilder.buildSiteSearch(params, startIndex, Math.min(50, pageSize == null ? 50 : pageSize));
            SearchResults searchResults = this.searchManager.search(search);
            ArrayList<SpaceDirectoryEntity> directoryList = new ArrayList<SpaceDirectoryEntity>();
            for (SearchResult searchResult : searchResults) {
                SpaceDirectoryEntity entity = this.builder.build(searchResult);
                if (entity == null) continue;
                directoryList.add(entity);
            }
            result.setSpaces(directoryList);
            result.setTotalSize(searchResults.getUnfilteredResultsCount());
        }
        catch (IllegalArgumentException e) {
            log.debug("An illegal arg was given for search for spaces.", (Throwable)e);
        }
        catch (InvalidSearchException e) {
            log.debug("An invalid search was given for search for spaces", (Throwable)e);
        }
        return Response.ok((Object)result).build();
    }

    private void setSpaceStatusParams(String status, SearchQueryParameters params) {
        if (status == null) {
            params.setIncludeArchivedSpaces(true);
            params.setOnlyArchivedSpaces(false);
        } else if ("archived".equals(status)) {
            params.setOnlyArchivedSpaces(true);
        } else if ("current".equals(status)) {
            params.setIncludeArchivedSpaces(false);
        }
    }
}

