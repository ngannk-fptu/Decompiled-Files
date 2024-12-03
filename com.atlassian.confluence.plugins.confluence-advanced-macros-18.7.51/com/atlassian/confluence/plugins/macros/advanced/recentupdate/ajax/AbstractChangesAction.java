/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchTokenExpiredException
 *  com.atlassian.confluence.search.v2.SearchWithToken
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.util.actions.ContentTypesDisplayMapper
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate.ajax;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultUpdateItemFactory;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.RecentChangesSearchBuilder;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Theme;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;
import com.atlassian.plugin.PluginAccessor;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChangesAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractChangesAction.class);
    private SearchManager searchManager;
    private ContextPathHolder contextPathHolder;
    private ContentTypesDisplayMapper contentTypesDisplayMapper;
    private PluginAccessor pluginAccessor;
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private String authors;
    private String contentType;
    private String spaceKeys;
    private String labels;
    private int startIndex;
    private int pageSize;
    private long searchToken;
    private String nextPageUrl;
    List<UpdateItem> updateItems;

    public String execute() throws Exception {
        SearchResults searchResults;
        RecentChangesSearchBuilder searchBuilder = new RecentChangesSearchBuilder(this.pluginAccessor, this.userAccessor, this.siteSearchPermissionsQueryFactory).withLabels(this.labels).withAuthors(this.authors).withContentTypes(this.contentType).withSpaceKeys(this.spaceKeys);
        if (this.searchToken > 0L) {
            searchBuilder.withSearchToken(this.searchToken);
        }
        if (this.startIndex >= 0) {
            searchBuilder.withStartIndex(this.startIndex);
        }
        if (this.pageSize > 0) {
            searchBuilder.withPageSize(this.pageSize);
        }
        if (this.searchToken > 0L) {
            try {
                searchResults = this.searchManager.search(searchBuilder.buildSearchWithToken());
            }
            catch (SearchTokenExpiredException e) {
                this.addActionError(this.getText("recently.updated.search.token.expired"));
                return "input";
            }
            catch (InvalidSearchException e) {
                log.debug("Invalid search", (Throwable)e);
                this.addActionError(e.getMessage());
                return "input";
            }
        }
        try {
            searchResults = this.searchManager.search(searchBuilder.buildSearch());
        }
        catch (InvalidSearchException e) {
            log.debug("Invalid search", (Throwable)e);
            this.addActionError(e.getMessage());
            return "input";
        }
        if (!searchResults.isLastPage()) {
            SearchWithToken nextPageSearch = searchResults.getNextPageSearch();
            searchBuilder.withStartIndex(nextPageSearch.getStartOffset()).withPageSize(nextPageSearch.getLimit()).withSearchToken(nextPageSearch.getSearchToken());
            this.nextPageUrl = searchBuilder.buildSearchUrl(this.getTheme(), this.contextPathHolder.getContextPath());
        }
        DefaultUpdateItemFactory updateItemFactory = new DefaultUpdateItemFactory(this.getDateFormatter(), this.getI18n(), this.contentTypesDisplayMapper, this.pluginAccessor);
        this.updateItems = new LinkedList<UpdateItem>();
        for (SearchResult searchResult : searchResults) {
            UpdateItem updateItem = updateItemFactory.get(searchResult);
            if (updateItem == null) continue;
            this.updateItems.add(updateItem);
        }
        return super.execute();
    }

    protected abstract Theme getTheme();

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSpaceKeys(String spaceKeys) {
        this.spaceKeys = spaceKeys;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getNextPageUrl() {
        return this.nextPageUrl;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public List<UpdateItem> getUpdateItems() {
        return this.updateItems;
    }

    public void setContentTypesDisplayMapper(ContentTypesDisplayMapper contentTypesDisplayMapper) {
        this.contentTypesDisplayMapper = contentTypesDisplayMapper;
    }

    public void setSearchToken(long searchToken) {
        this.searchToken = searchToken;
    }

    public void setContextPathHolder(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = contextPathHolder;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setSiteSearchPermissionsQueryFactory(SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }
}

