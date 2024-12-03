/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRange
 *  com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.InvalidDurationException
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.InvalidDurationException;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SearchMacro
extends BaseMacro {
    private static final Logger log = LoggerFactory.getLogger(SearchMacro.class);
    private static final int DEFAULT_MAXLIMIT = 10;
    private SearchManager searchManager;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private UserAccessor userAccessor;
    private PluginAccessor pluginAccessor;
    private I18NBean i18NBean;

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map params, String string, RenderContext renderContext) throws MacroException {
        SearchResults searchResults;
        if (!(renderContext instanceof PageContext)) {
            throw new ClassCastException(this.i18NBean.getText("search.error.macro-works-in-only-confluence", (Object[])new String[]{renderContext.getClass().getName(), PageContext.class.getName()}));
        }
        Object queryString = StringUtils.trimToNull((String)((String)params.get("query")));
        String userEnteredQuery = queryString;
        if (queryString == null) {
            queryString = StringUtils.trimToNull((String)((String)params.get("0")));
        }
        if (!StringUtils.isEmpty((CharSequence)queryString) && ((PageContext)renderContext).getEntity() != null) {
            queryString = "(" + (String)queryString + ") AND NOT handle: " + new HibernateHandle((Searchable)((PageContext)renderContext).getEntity());
            log.info("Instrumenting query to become: " + (String)queryString);
        }
        SearchQueryParameters searchQueryParams = new SearchQueryParameters((String)queryString);
        Integer maxLimit = GeneralUtil.convertToInteger(params.get("maxLimit"));
        if (maxLimit == null) {
            maxLimit = 10;
        }
        String spaceKey = StringUtils.trimToNull((String)((String)params.get("spacekey")));
        String type = StringUtils.trimToNull((String)((String)params.get("type")));
        String lastModified = StringUtils.trimToNull((String)((String)params.get("lastModified")));
        String contributor = StringUtils.trimToNull((String)((String)params.get("contributor")));
        searchQueryParams.setSpaceKey(spaceKey);
        if (StringUtils.isNotBlank((CharSequence)type)) {
            ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation((String)type);
            if (contentType == null) {
                throw new MacroException(this.i18NBean.getText("search.error.unknown-content-type"));
            }
            searchQueryParams.setContentType(contentType);
        }
        if (StringUtils.isNotBlank((CharSequence)lastModified)) {
            try {
                long duration = DateUtils.getDuration((String)lastModified);
                Date currentDateLessDuration = this.getCurrentDateLessDuration(duration);
                DateRangeQuery.DateRange dateRange = new DateRangeQuery.DateRange(currentDateLessDuration, null, true, false);
                searchQueryParams.setLastModified(dateRange);
            }
            catch (InvalidDurationException e) {
                throw new IllegalArgumentException(this.i18NBean.getText("search.error.invalid-date-period", (Object[])new String[]{StringEscapeUtils.escapeHtml4((String)lastModified)}));
            }
        }
        if (StringUtils.isNotBlank((CharSequence)contributor)) {
            searchQueryParams.setContributor(this.userAccessor.getUserByName(contributor));
        }
        searchQueryParams.setSearchQueryFilter((SearchQuery)new NonViewableCustomContentTypeQuery(this.pluginAccessor));
        ISearch search = this.buildSiteSearch(searchQueryParams, maxLimit);
        try {
            searchResults = this.searchManager.search(search);
        }
        catch (IllegalArgumentException e) {
            throw new MacroException(this.i18NBean.getText("search.error.invalid-query-params", (Object[])new String[]{StringEscapeUtils.escapeHtml4((String)searchQueryParams.toString())}), (Throwable)e);
        }
        catch (InvalidSearchException e) {
            throw new MacroException(this.i18NBean.getText("search.error.fail-for-term", (Object[])new String[]{StringEscapeUtils.escapeHtml4((String)searchQueryParams.getQuery())}), (Throwable)e);
        }
        List results = searchResults.getAll();
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        contextMap.put("searchResults", results);
        contextMap.put("totalSearchResults", results.size());
        contextMap.put("maxLimit", maxLimit);
        contextMap.put("query", userEnteredQuery);
        contextMap.put("generalUtil", GeneralUtil.INSTANCE);
        try {
            return this.getRenderedTemplate(contextMap);
        }
        catch (Exception e) {
            throw new MacroException("Error while trying to assemble the search result!", (Throwable)e);
        }
    }

    @VisibleForTesting
    protected ISearch buildSiteSearch(SearchQueryParameters searchQueryParameters, Integer maxLimit) {
        return this.predefinedSearchBuilder.buildSiteSearch(searchQueryParameters, 0, maxLimit.intValue());
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    protected String getRenderedTemplate(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)"com/atlassian/confluence/plugins/macros/advanced/search.vm", contextMap);
    }

    protected Date getCurrentDateLessDuration(long duration) {
        return new Date(System.currentTimeMillis() - duration * 1000L);
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public void setPredefinedSearchBuilder(PredefinedSearchBuilder predefinedSearchBuilder) {
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setI18NBean(I18NBean i18NBean) {
        this.i18NBean = i18NBean;
    }
}

