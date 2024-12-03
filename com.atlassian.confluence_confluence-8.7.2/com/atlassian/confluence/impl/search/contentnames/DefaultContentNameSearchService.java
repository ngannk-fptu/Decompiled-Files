/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.contentnames;

import com.atlassian.confluence.impl.search.contentnames.SemaphoreHolder;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSection;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider;
import com.atlassian.confluence.search.contentnames.ContentNameSearchService;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.QueryTokenizer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultContentNameSearchService
implements ContentNameSearchService {
    private static final String NEXT_UI_SEARCH = "next.ui.search";
    private static final Logger log = LoggerFactory.getLogger(DefaultContentNameSearchService.class);
    public static final String SEARCH_FOR_CSS_CLASS = "search-for";
    private static final int MAX_PERMIT_ACQUIRE_TIME_MILLIS = 500;
    private static final Logger logger = LoggerFactory.getLogger(DefaultContentNameSearchService.class);
    private final SettingsManager settingsManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final QueryTokenizer contentNameQueryTokenizer;
    private final SemaphoreHolder contentNameSearchSemaphoreHolder;
    private final PluginAccessor pluginAccessor;
    private static final String PROFILING_LOG_MESSAGE = DefaultContentNameSearchService.class.getCanonicalName() + ".search";

    public DefaultContentNameSearchService(SettingsManager settingsManager, I18NBeanFactory i18NBeanFactory, QueryTokenizer contentNameQueryTokenizer, SemaphoreHolder contentNameSearchSemaphoreHolder, PluginAccessor pluginAccessor) {
        this.settingsManager = settingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.contentNameQueryTokenizer = contentNameQueryTokenizer;
        this.contentNameSearchSemaphoreHolder = contentNameSearchSemaphoreHolder;
        this.pluginAccessor = pluginAccessor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ContentNameSearchResult search(String query, ContentNameSearchContext context) {
        boolean permitAcquired;
        ContentNameSearchResult result = new ContentNameSearchResult(query);
        if (!this.settingsManager.getGlobalSettings().isEnableQuickNav()) {
            result.setStatusMessage(this.getI18n().getText("quick.nav.disabled"));
            return result;
        }
        Semaphore permits = this.contentNameSearchSemaphoreHolder.getSemaphore();
        try {
            permitAcquired = permits.tryAcquire(500L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            result.setStatusMessage(this.getI18n().getText("quick.nav.cancelled"));
            Thread.currentThread().interrupt();
            return result;
        }
        if (permitAcquired) {
            try (Ticker ignored = Timers.start((String)PROFILING_LOG_MESSAGE);){
                List<QueryToken> queryTokens = this.contentNameQueryTokenizer.tokenize(query);
                result.setQueryTokens(queryTokens);
                if (CollectionUtils.isEmpty(queryTokens)) {
                    result.setStatusMessage(this.getI18n().getText("contentnamesearch.invalid.query"));
                }
                long start = System.currentTimeMillis();
                this.performSearch(result, context, queryTokens);
                this.addSearchAllSection(result, query, context.getHttpServletRequest());
                log.debug("search takes {} ms", (Object)(System.currentTimeMillis() - start));
            }
            finally {
                permits.release();
            }
        } else {
            logger.warn("A single quick nav search request could not be fulfilled since the limit of simultaneous quick nav searches has been reached. The search should be attempted again. Alternatively, consider adjusting this limit in General Configuration via maxSimultaneousQuickNavRequests");
            result.setStatusMessage(this.getI18n().getText("quick.nav.server.busy"));
        }
        return result;
    }

    private void addSearchAllSection(ContentNameSearchResult result, String query, HttpServletRequest servletRequest) {
        if (!NEXT_UI_SEARCH.equalsIgnoreCase(servletRequest.getParameter("src"))) {
            result.addMatchGroup(this.createSearchAllGroupItem(query, servletRequest));
        }
    }

    private List<ContentNameMatch> createSearchAllGroupItem(String query, HttpServletRequest servletRequest) {
        String url = "/dosearchsite.action?queryString=" + HtmlUtil.urlEncode(query);
        String contextPath = servletRequest.getContextPath();
        ContentNameMatch searchAll = new ContentNameMatch(SEARCH_FOR_CSS_CLASS, contextPath + url, null, this.getI18n().getText("contentnamesearch.search.for", new String[]{HtmlUtil.htmlEncode(query)}), null, null);
        return Collections.singletonList(searchAll);
    }

    private void performSearch(ContentNameSearchResult result, ContentNameSearchContext context, List<QueryToken> queryTokens) {
        List providers = this.pluginAccessor.getEnabledModulesByClass(ContentNameSearchSectionsProvider.class);
        int limit = context.getLimit();
        AtomicInteger count = new AtomicInteger(0);
        providers.stream().map(provider -> {
            long start = System.currentTimeMillis();
            Collection<ContentNameSearchSection> sections = provider.getSections(queryTokens, context);
            log.debug("{}#getSections takes {} ms", provider, (Object)(System.currentTimeMillis() - start));
            return sections;
        }).filter(Objects::nonNull).flatMap(Collection::stream).sorted(ContentNameSearchSection.COMPARATOR).map(section -> {
            if (limit == -1) {
                return section.getResults();
            }
            if (count.get() > limit) {
                return Collections.emptyList();
            }
            List<ContentNameMatch> results = section.getResults();
            int tempTotalCount = count.get() + results.size();
            if (tempTotalCount > limit) {
                results = results.subList(0, results.size() - (tempTotalCount - limit));
            }
            count.set(count.get() + results.size());
            return results;
        }).filter(items -> !items.isEmpty()).forEach(result::addMatchGroup);
    }

    private I18NBean getI18n() {
        return this.i18NBeanFactory.getI18NBean();
    }
}

