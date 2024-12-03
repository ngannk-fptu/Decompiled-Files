/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.search.api.Searcher
 *  com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters
 *  com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters$Builder
 *  com.atlassian.confluence.plugins.search.api.model.SearchResultList
 *  com.atlassian.confluence.plugins.search.api.model.SearchResults
 *  com.atlassian.confluence.search.service.DateRangeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest;

import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.events.KbSearchPerformedEvent;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SearchResultAugmenter;
import com.atlassian.confluence.plugins.search.api.Searcher;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.plugins.search.api.model.SearchResultList;
import com.atlassian.confluence.plugins.search.api.model.SearchResults;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/search")
@Produces(value={"application/json;charset=UTF-8"})
public class SearchResource {
    private final TransactionTemplate transactionTemplate;
    private final Searcher searcher;
    private final EventPublisher eventPublisher;
    private final PluginAccessor pluginAccessor;
    private final SearchResultAugmenter searchResultAugmenter;
    private final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final I18NBeanFactory i18NBeanFactory;

    public SearchResource(TransactionTemplate transactionTemplate, Searcher searcher, EventPublisher eventPublisher, PluginAccessor pluginAccessor, SearchResultAugmenter searchResultAugmenter, UserAccessor userAccessor, SpaceManager spaceManager, PermissionManager permissionManager, I18NBeanFactory i18NBeanFactory) {
        this.transactionTemplate = transactionTemplate;
        this.searcher = searcher;
        this.eventPublisher = eventPublisher;
        this.pluginAccessor = pluginAccessor;
        this.searchResultAugmenter = searchResultAugmenter;
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @GET
    @AnonymousAllowed
    public Response search(@QueryParam(value="user") String user, @QueryParam(value="queryString") String query, @QueryParam(value="startIndex") @DefaultValue(value="0") int startIndex, @QueryParam(value="pageSize") @DefaultValue(value="10") int pageSize, @QueryParam(value="type") String type, @QueryParam(value="where") String where, @QueryParam(value="lastModified") String lastModified, @QueryParam(value="contributor") String contributor, @QueryParam(value="contributorUsername") String contributorUsername, @QueryParam(value="includeArchivedSpaces") boolean includeArchivedSpaces, @QueryParam(value="sessionUuid") String sessionUuid, @QueryParam(value="labels") Set<String> labels, @QueryParam(value="highlight") @DefaultValue(value="true") boolean highlight) {
        if (StringUtils.isNotBlank((CharSequence)user) && !user.equals(AuthenticatedUserThreadLocal.getUsername())) {
            return Response.status((int)401).build();
        }
        SearchQueryParameters searchQueryParameters = this.buildSearchQueryParameters(SearchResource.sanitizeQuery(query), startIndex, pageSize, type, where, lastModified, contributor, contributorUsername, includeArchivedSpaces, labels, highlight);
        SearchResultList rawResults = this.executeSearch(searchQueryParameters, sessionUuid);
        SearchResultList searchResultsWithLikes = new SearchResultList(this.searchResultAugmenter.addLikeCountsToResults(rawResults.getResults()), rawResults.getTotal(), rawResults.getArchivedResultsCount(), rawResults.getUuid(), rawResults.getTimeSpent());
        return Response.ok((Object)searchResultsWithLikes).build();
    }

    @GET
    @Path(value="{spaceKey}")
    @AnonymousAllowed
    public Response searchWithViewPermissionChecks(@PathParam(value="spaceKey") String spaceKey, @QueryParam(value="permissionCheckedUser") String permissionCheckedUser, @QueryParam(value="queryString") String query, @QueryParam(value="startIndex") @DefaultValue(value="0") int startIndex, @QueryParam(value="pageSize") @DefaultValue(value="10") int pageSize, @QueryParam(value="type") String type, @QueryParam(value="lastModified") String lastModified, @QueryParam(value="contributor") String contributor, @QueryParam(value="contributorUsername") String contributorUsername, @QueryParam(value="includeArchivedSpaces") boolean includeArchivedSpaces, @QueryParam(value="sessionUuid") String sessionUuid, @QueryParam(value="labels") Set<String> labels, @QueryParam(value="highlight") @DefaultValue(value="true") boolean highlight) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return this.spaceNotFoundResponse();
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
            return this.noViewSpacePermissionResponse();
        }
        SearchQueryParameters searchQueryParameters = this.buildSearchQueryParameters(SearchResource.sanitizeQuery(query), startIndex, pageSize, type, spaceKey, lastModified, contributor, contributorUsername, includeArchivedSpaces, labels, highlight);
        SearchResultList rawResults = this.executeSearch(searchQueryParameters, sessionUuid);
        Option permCheckUserOpt = Option.option((Object)this.userAccessor.getUserByName(permissionCheckedUser));
        SearchResultList searchResultsWithPermissionChecks = new SearchResultList(this.searchResultAugmenter.addViewPermissionChecksToResults(rawResults.getResults(), (Option<ConfluenceUser>)permCheckUserOpt), rawResults.getTotal(), rawResults.getArchivedResultsCount(), rawResults.getUuid(), rawResults.getTimeSpent());
        return Response.ok((Object)searchResultsWithPermissionChecks).build();
    }

    private SearchQueryParameters buildSearchQueryParameters(String query, int startIndex, int pageSize, String type, String where, String lastModified, String contributor, String contributorUsername, boolean includeArchivedSpaces, Set<String> labels, boolean highlight) {
        SearchQueryParameters.Builder builder = new SearchQueryParameters.Builder(query).startIndex(startIndex).pageSize(pageSize).pluggableContentType(this.pluginAccessor, type).where(where).contributor(Strings.isNullOrEmpty((String)contributorUsername) ? contributor : contributorUsername).includeArchivedSpaces(includeArchivedSpaces).highlight(highlight).labels(labels);
        if (!Strings.isNullOrEmpty((String)lastModified)) {
            DateRangeEnum lastModifiedDateRange = null;
            try {
                lastModifiedDateRange = DateRangeEnum.valueOf((String)lastModified);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            builder.lastModified(lastModifiedDateRange);
        }
        return builder.build();
    }

    private SearchResultList executeSearch(SearchQueryParameters searchQueryParameters, String sessionUuid) {
        return (SearchResultList)this.transactionTemplate.execute(() -> {
            SearchResults searchResults = this.searcher.search(searchQueryParameters, false);
            this.publishKbSearchPerformedEvent(searchQueryParameters, searchResults, sessionUuid);
            List searchResultEntities = searchResults.getResults();
            return new SearchResultList(searchResultEntities, searchResults.getTotalSize(), searchResults.getExtendedTotalSize(), searchResults.getUuid().toString(), searchResults.getTimeSpent());
        });
    }

    private void publishKbSearchPerformedEvent(SearchQueryParameters searchQuery, SearchResults searchResults, String sessionUuid) {
        String incomingUuid = sessionUuid;
        if (StringUtils.isBlank((CharSequence)incomingUuid)) {
            incomingUuid = UUID.randomUUID().toString();
        }
        SearchQuery searchv2Query = searchQuery.toSearchV2Query((Map)ImmutableMap.of((Object)"sessionUuid", (Object)incomingUuid));
        this.eventPublisher.publish((Object)new KbSearchPerformedEvent(this, searchv2Query, (User)AuthenticatedUserThreadLocal.get(), searchResults.getTotalSize()));
    }

    private Response spaceNotFoundResponse() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)SearchResource.jsonErrorObject(i18NBean.getText("com.atlassian.confluence.plugins.confluence-knowledge-base.space.not.found"))).build();
    }

    private Response noViewSpacePermissionResponse() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)SearchResource.jsonErrorObject(i18NBean.getText("com.atlassian.confluence.plugins.confluence-knowledge-base.no.view.space.permission"))).build();
    }

    private static String jsonErrorObject(String errorMessage) {
        return new JSONObject((Map)ImmutableMap.of((Object)"errorMessage", (Object)StringUtils.defaultIfBlank((CharSequence)errorMessage, (CharSequence)""))).toString();
    }

    @VisibleForTesting
    static String sanitizeQuery(String query) {
        String sanitizedQuery = query.replaceAll("[\\{\\}]", " ");
        return sanitizedQuery;
    }
}

