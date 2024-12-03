/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntityList;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.plugins.rest.service.RestSearchParameters;
import com.atlassian.confluence.plugins.rest.service.RestSearchService;
import com.atlassian.confluence.plugins.rest.service.SearchServiceException;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/search")
@AnonymousAllowed
public class PrototypeSearchService
extends AbstractResource {
    private RestSearchService searchService;
    private static final String SEARCH_SITE = "site";
    private static final String SEARCH_NAME = "name";
    private static final String SEARCH_USER = "user";

    private PrototypeSearchService() {
    }

    public PrototypeSearchService(UserAccessor userAccessor, RestSearchService searchService, SpacePermissionManager spacePermissionManager) {
        super(userAccessor, spacePermissionManager);
        this.searchService = searchService;
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response doSearch(@QueryParam(value="query") String query, @QueryParam(value="type") String type, @DefaultValue(value="site") @QueryParam(value="search") String search, @DefaultValue(value="0") @QueryParam(value="startIndex") int startIndex, @QueryParam(value="pageSize") Integer pageSize, @QueryParam(value="max-results") Integer maxResults, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="attachmentType") Set<String> attachmentType, @QueryParam(value="label") Set<String> label, @DefaultValue(value="true") @QueryParam(value="groupResults") boolean groupResults, @DefaultValue(value="false") @QueryParam(value="searchParentName") boolean searchParentName, @QueryParam(value="preferredSpaceKey") String preferredSpaceKey, @QueryParam(value="maxResultsPerGroup") Integer maxResultsPerGroup) {
        if (SEARCH_NAME.equals(search)) {
            return this.doContentNameSearch(query, type, spaceKey, attachmentType, label, groupResults, searchParentName, preferredSpaceKey, startIndex, pageSize, maxResultsPerGroup);
        }
        if (SEARCH_USER.equals(search)) {
            return this.doUserSearch(query, maxResults, false);
        }
        return this.doSiteSearch(query, type, spaceKey, attachmentType, label, startIndex, pageSize);
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/site")
    public Response doSiteSearch(@QueryParam(value="query") String query, @QueryParam(value="type") String type, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="attachmentType") Set<String> attachmentType, @QueryParam(value="label") Set<String> label, @DefaultValue(value="0") @QueryParam(value="startIndex") int startIndex, @QueryParam(value="pageSize") Integer pageSize) {
        try {
            this.createRequestContext();
            RestSearchParameters parameters = new RestSearchParameters(query, type, spaceKey, attachmentType, label, false, null);
            return Response.ok((Object)this.searchService.fullSearch(parameters, startIndex, pageSize)).build();
        }
        catch (SearchServiceException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/name")
    public Response doContentNameSearch(@QueryParam(value="query") String query, @QueryParam(value="type") String type, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="attachmentType") Set<String> attachmentType, @QueryParam(value="label") Set<String> label, @DefaultValue(value="true") @QueryParam(value="groupResults") boolean groupResults, @DefaultValue(value="false") @QueryParam(value="searchParentName") boolean searchParentName, @QueryParam(value="preferredSpaceKey") String preferredSpaceKey, @DefaultValue(value="0") @QueryParam(value="startIndex") int startIndex, @QueryParam(value="pageSize") Integer pageSize, @QueryParam(value="maxResultsPerGroup") Integer maxResultsPerGroup) {
        try {
            this.createRequestContext();
            RestSearchParameters parameters = new RestSearchParameters(query, type, spaceKey, attachmentType, label, searchParentName, preferredSpaceKey);
            return Response.ok((Object)this.searchService.nameSearch(parameters, groupResults, startIndex, pageSize, maxResultsPerGroup)).build();
        }
        catch (SearchServiceException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/user")
    public Response doUserSearch(@QueryParam(value="query") String query, @QueryParam(value="max-results") int maxResults, @DefaultValue(value="false") @QueryParam(value="show-unlicensed") boolean showUnlicensed) {
        try {
            this.createRequestContext();
            return Response.ok((Object)this.searchService.userSearch(query, maxResults, showUnlicensed)).build();
        }
        catch (SearchServiceException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/group")
    public Response doGroupSearch(@QueryParam(value="query") String query, @QueryParam(value="max-results") int maxResults) {
        try {
            this.createRequestContext();
            return Response.ok((Object)this.searchService.groupSearch(query, maxResults)).build();
        }
        catch (SearchServiceException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/user-or-group")
    public Response doUserOrGroupSearch(@QueryParam(value="query") String query, @QueryParam(value="max-results") int maxResults) {
        try {
            this.createRequestContext();
            List<SearchResultEntity> users = this.searchService.userSearch(query, maxResults).getResults();
            List<SearchResultEntity> groups = this.searchService.groupSearch(query, maxResults).getResults();
            SearchResultEntityList combined = new SearchResultEntityList();
            combined.setResults((List<SearchResultEntity>)ImmutableList.copyOf(this.balancedConcat(users, groups, maxResults, 0.7f)));
            return Response.ok((Object)combined).build();
        }
        catch (SearchServiceException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
    }

    private <E> Iterable<E> balancedConcat(Iterable<E> users, List<E> groups, int maxResults, float ratio) {
        int maxUsers = Math.max((int)((float)maxResults * ratio), maxResults - groups.size());
        return Iterables.limit((Iterable)Iterables.concat((Iterable)Iterables.limit(users, (int)maxUsers), groups), (int)maxResults);
    }
}

