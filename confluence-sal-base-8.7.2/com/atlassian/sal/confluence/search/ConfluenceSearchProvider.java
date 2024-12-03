/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.search.ResourceType
 *  com.atlassian.sal.api.search.SearchProvider
 *  com.atlassian.sal.api.search.SearchResults
 *  com.atlassian.sal.api.search.query.SearchQuery
 *  com.atlassian.sal.api.search.query.SearchQueryParser
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.core.message.DefaultMessage
 *  com.atlassian.sal.core.search.BasicResourceType
 *  com.atlassian.sal.core.search.BasicSearchMatch
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.search;

import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.ResourceType;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.core.message.DefaultMessage;
import com.atlassian.sal.core.search.BasicResourceType;
import com.atlassian.sal.core.search.BasicSearchMatch;
import java.io.Serializable;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSearchProvider
implements SearchProvider {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSearchProvider.class);
    private final UserAccessor userAccessor;
    private final ApplicationProperties applicationProperties;
    private final SearchQueryParser searchQueryParser;
    private final PredefinedSearchBuilder predefinedSearchBuilder;
    private final SearchManager searchManager;

    public ConfluenceSearchProvider(PredefinedSearchBuilder predefinedSearchBuilder, SearchManager searchManager, SearchQueryParser searchQueryParser, UserAccessor userAccessor, ApplicationProperties applicationProperties) {
        this.predefinedSearchBuilder = predefinedSearchBuilder;
        this.searchManager = searchManager;
        this.userAccessor = userAccessor;
        this.applicationProperties = applicationProperties;
        this.searchQueryParser = searchQueryParser;
    }

    public SearchResults search(String username, String stringQuery) {
        return this.search(this.userAccessor.getUserByName(username), stringQuery);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SearchResults search(ConfluenceUser user, String stringQuery) {
        long startTime = System.currentTimeMillis();
        ConfluenceUser oldUser = AuthenticatedUserThreadLocal.get();
        AuthenticatedUserThreadLocal.set((ConfluenceUser)user);
        try {
            SearchQuery searchQuery = this.searchQueryParser.parse(stringQuery);
            SearchQueryParameters searchQueryParams = new SearchQueryParameters(searchQuery.getSearchString());
            String projectKey = searchQuery.getParameter("project");
            if (StringUtils.isNotEmpty((CharSequence)projectKey)) {
                searchQueryParams.setSpaceKey(projectKey);
            }
            ISearch search = this.predefinedSearchBuilder.buildSiteSearch(searchQueryParams, 0, searchQuery.getParameter("maxhits", Integer.MAX_VALUE));
            com.atlassian.confluence.search.v2.SearchResults result = this.searchManager.search(search);
            ArrayList<BasicSearchMatch> matches = new ArrayList<BasicSearchMatch>();
            for (SearchResult searchResult : result.getAll()) {
                String url = this.applicationProperties.getBaseUrl() + searchResult.getUrlPath();
                String title = searchResult.getDisplayTitle();
                String excerpt = searchResult.getContent();
                BasicResourceType resourceType = new BasicResourceType(this.applicationProperties, searchResult.getType());
                matches.add(new BasicSearchMatch(url, title, excerpt, (ResourceType)resourceType));
            }
            SearchResults searchResults = new SearchResults(matches, result.getUnfilteredResultsCount(), System.currentTimeMillis() - startTime);
            return searchResults;
        }
        catch (InvalidSearchException e) {
            log.error("Error running confluence search", (Throwable)e);
            ArrayList<DefaultMessage> errors = new ArrayList<DefaultMessage>();
            errors.add(new DefaultMessage(e.getMessage(), new Serializable[0]));
            SearchResults searchResults = new SearchResults(errors);
            return searchResults;
        }
        finally {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)oldUser);
        }
    }

    public SearchResults search(UserKey userKey, String stringQuery) {
        ConfluenceUser user = userKey != null ? this.userAccessor.getExistingUserByKey(userKey) : null;
        return this.search(user, stringQuery);
    }
}

