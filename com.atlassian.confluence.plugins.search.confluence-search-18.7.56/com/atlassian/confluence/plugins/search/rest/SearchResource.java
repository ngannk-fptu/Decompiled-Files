/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.search.SearchPerformedEvent
 *  com.atlassian.confluence.event.events.search.SiteSearchAuditEvent
 *  com.atlassian.confluence.search.service.DateRangeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.search.rest;

import com.atlassian.confluence.event.events.search.SearchPerformedEvent;
import com.atlassian.confluence.event.events.search.SiteSearchAuditEvent;
import com.atlassian.confluence.plugins.search.api.Searcher;
import com.atlassian.confluence.plugins.search.api.events.RemoteSearchPerformedEvent;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.plugins.search.api.model.SearchResultList;
import com.atlassian.confluence.plugins.search.api.model.SearchResults;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/search")
@Produces(value={"application/json;charset=UTF-8"})
public class SearchResource {
    static final String AUDIT_LOG_SEARCH_DISABLED_KEY = "audit.log.search.disabled";
    private final Searcher searcher;
    private final TransactionTemplate transactionTemplate;
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;

    @Autowired
    public SearchResource(Searcher searcher, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport EventPublisher eventPublisher, @ComponentImport PluginAccessor pluginAccessor) {
        this.searcher = searcher;
        this.transactionTemplate = transactionTemplate;
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
    }

    @GET
    @AnonymousAllowed
    public Response search(@QueryParam(value="user") String username, @QueryParam(value="queryString") String query, @QueryParam(value="startIndex") @DefaultValue(value="0") int startIndex, @QueryParam(value="pageSize") @DefaultValue(value="10") int pageSize, @QueryParam(value="type") String type, @QueryParam(value="where") String where, @QueryParam(value="lastModified") String lastModified, @QueryParam(value="contributor") String contributor, @QueryParam(value="contributorUsername") String contributorUsername, @QueryParam(value="includeArchivedSpaces") boolean includeArchivedSpaces, @QueryParam(value="sessionUuid") String sessionUuid, @QueryParam(value="labels") Set<String> labels, @QueryParam(value="highlight") @DefaultValue(value="true") boolean highlight) {
        if (StringUtils.isNotEmpty((CharSequence)username) && !username.equals(AuthenticatedUserThreadLocal.getUsername())) {
            return Response.status((int)401).build();
        }
        return (Response)this.transactionTemplate.execute(() -> {
            SearchQueryParameters.Builder builder = new SearchQueryParameters.Builder(query).startIndex(startIndex).pageSize(pageSize).pluggableContentType(this.pluginAccessor, type).where(where).contributor(Strings.isNullOrEmpty((String)contributorUsername) ? contributor : contributorUsername).includeArchivedSpaces(includeArchivedSpaces).highlight(highlight).labels(labels);
            if (StringUtils.isNotEmpty((CharSequence)lastModified)) {
                DateRangeEnum lastModifiedDateRange = null;
                try {
                    lastModifiedDateRange = DateRangeEnum.valueOf((String)lastModified);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
                builder.lastModified(lastModifiedDateRange);
            }
            SearchQueryParameters searchQueryParameters = builder.build();
            SearchResults searchResults = this.searcher.search(searchQueryParameters, false);
            SearchResultList searchResultList = new SearchResultList(searchResults.getResults(), searchResults.getTotalSize(), searchResults.getExtendedTotalSize(), searchResults.getUuid().toString(), searchResults.getTimeSpent());
            this.publishSearchPerformedEvent(searchQueryParameters, searchResults, sessionUuid);
            this.publishSearchAuditEvent(searchQueryParameters.getQuery());
            return Response.ok((Object)searchResultList).build();
        });
    }

    private void publishSearchPerformedEvent(SearchQueryParameters searchQuery, SearchResults searchResults, String sessionUuid) {
        RemoteSearchPerformedEvent event;
        int totalSize = searchResults.getTotalSize();
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (StringUtils.isBlank((CharSequence)sessionUuid)) {
            Map<String, String> extraParams = Collections.emptyMap();
            SearchQuery search = searchQuery.toSearchV2Query(extraParams);
            event = new RemoteSearchPerformedEvent(this, search, (User)user, totalSize);
        } else {
            SearchQuery search = searchQuery.toSearchV2Query((Map<String, String>)ImmutableMap.of((Object)"sessionUuid", (Object)sessionUuid));
            event = new SearchPerformedEvent((Object)this, search, (User)user, totalSize);
        }
        this.eventPublisher.publish((Object)event);
    }

    private void publishSearchAuditEvent(String query) {
        if (!Boolean.getBoolean(AUDIT_LOG_SEARCH_DISABLED_KEY)) {
            this.eventPublisher.publish((Object)new SiteSearchAuditEvent(query, (User)AuthenticatedUserThreadLocal.get()));
        }
    }
}

