/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchOptions$Excerpt
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.search.plugin.SiteSearchPluginModule
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.DateRangeEnum
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.ParameterSafe
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.search.actions;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.search.CQLSearchResult;
import com.atlassian.confluence.plugins.search.CQLSearcher;
import com.atlassian.confluence.plugins.search.actions.PaginationSupport;
import com.atlassian.confluence.plugins.search.actions.SearchActionParameterMigrator;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import com.atlassian.confluence.plugins.search.event.SiteSearchCompleteEvent;
import com.atlassian.confluence.search.plugin.SiteSearchPluginModule;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.util.PairType;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.ParameterSafe;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

public class SearchAction
extends ConfluenceActionSupport
implements InitializingBean {
    private static final String ALL = "conf_all";
    static final long serialVersionUID = 1L;
    public static final int PAGE_SIZE = 10;
    private UUID sessionUuid;
    private int startIndex;
    private String queryString;
    private String where;
    private String type;
    private DateRangeEnum lastModified;
    private String contributor;
    private String contributorUsername;
    private String labels;
    private boolean includeArchivedSpaces;
    private String cql;
    private transient BackwardsCompatibility searchQuery = new BackwardsCompatibility();
    private transient CQLSearcher cqlSearcher;
    private transient SearchPageResponse<CQLSearchResult> cqlSearchResults;
    private transient PaginationSupport<SearchResult> paginationSupport = new PaginationSupport(10);
    private transient SpaceManager spaceManager;
    private transient PluginAccessor pluginAccessor;
    private transient WebInterfaceManager webInterfaceManager;
    private transient EventPublisher eventPublisher;

    public void afterPropertiesSet() throws Exception {
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        SearchQueryParameters searchQuery = SearchQueryParameters.newSearchQueryParameters(this.queryString).startIndex(this.startIndex).pageSize(10).lastModified(this.lastModified).pluggableContentType(this.pluginAccessor, this.type).includeArchivedSpaces(this.includeArchivedSpaces).where(this.where).contributor(Strings.isNullOrEmpty((String)this.contributorUsername) ? this.contributor : this.contributorUsername).labels(this.sanitiseLabels(this.labels)).build();
        return this.doCqlSearch(searchQuery);
    }

    @VisibleForTesting
    Set<String> sanitiseLabels(String labels) {
        if (Strings.isNullOrEmpty((String)labels)) {
            return null;
        }
        HashSet<String> result = new HashSet<String>(Arrays.asList(labels.split(",")));
        return result.stream().map(String::trim).filter(label -> !Strings.isNullOrEmpty((String)label)).collect(Collectors.toSet());
    }

    @VisibleForTesting
    String doCqlSearch(SearchQueryParameters searchQuery) {
        if (Strings.isNullOrEmpty((String)this.cql)) {
            this.cql = SearchActionParameterMigrator.migrate(searchQuery);
        }
        String sessionUuidStr = this.sessionUuid.toString();
        SearchOptions searchOptions = SearchOptions.builder().excerptStrategy(SearchOptions.Excerpt.HIGHLIGHT).includeArchivedSpaces(searchQuery.isIncludeArchivedSpaces()).fireSearchPerformed(true).build();
        SimplePageRequest pageRequest = new SimplePageRequest(searchQuery.getStartIndex(), searchQuery.getPageSize());
        try {
            this.cqlSearchResults = this.cqlSearcher.getCqlSearchResults(this.cql, searchOptions, (PageRequest)pageRequest, new Expansion[0]);
        }
        catch (BadRequestException bre) {
            this.cqlSearchResults = SearchPageResponse.builder().build();
            this.setActionErrors(Collections.singletonList(bre.getMessage()));
        }
        this.paginationSupport.setTotal(this.cqlSearchResults.totalSize());
        this.paginationSupport.setStartIndex(this.startIndex);
        this.eventPublisher.publish((Object)new SiteSearchCompleteEvent(this.queryString != null ? this.queryString : "", this.cql != null ? this.cql : "", this.sessionUuid != null ? this.sessionUuid.toString() : "", this.cqlSearchResults.totalSize(), StringUtils.isNotEmpty((CharSequence)sessionUuidStr) && (sessionUuidStr.charAt(sessionUuidStr.length() - 1) & '\u0001') == 1));
        return this.hasActionErrors() ? "error" : "success";
    }

    public void validate() {
        if (this.sessionUuid == null) {
            this.sessionUuid = UUID.randomUUID();
        }
    }

    public Map<String, Object> getContext() {
        HashMap<String, Object> context = super.getContext();
        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put("queryString", this.queryString);
        context.put("where", this.where);
        context.put("type", this.type);
        context.put("lastModified", this.lastModified);
        context.put("contributor", this.contributor);
        context.put("contributorUsername", this.contributorUsername);
        context.put("includeArchivedSpaces", this.includeArchivedSpaces);
        return context;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public String getContributor() {
        return this.contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributorUsername() {
        return this.contributorUsername;
    }

    public void setContributorUsername(String contributorUsername) {
        this.contributorUsername = contributorUsername;
    }

    public List<PairType> getTypeOptions() {
        LinkedList<PairType> typeOptions = new LinkedList<PairType>();
        typeOptions.add(new PairType(null, (Serializable)((Object)this.getText("type.allcontent"))));
        typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.PAGE.getRepresentation()), (Serializable)((Object)this.getText("type.pages"))));
        typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.BLOG.getRepresentation()), (Serializable)((Object)this.getText("type.newsitems"))));
        typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.ATTACHMENT.getRepresentation()), (Serializable)((Object)this.getText("type.attachments"))));
        typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.SPACE_DESCRIPTION.getRepresentation()), (Serializable)((Object)this.getText("confluence-search.filter.spaces"))));
        typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.COMMENT.getRepresentation()), (Serializable)((Object)this.getText("type.comments"))));
        typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.PERSONAL_INFORMATION.getRepresentation()), (Serializable)((Object)this.getText("confluence-search.filter.people"))));
        List siteSearchPluginModules = this.pluginAccessor.getEnabledModulesByClass(SiteSearchPluginModule.class);
        siteSearchPluginModules.stream().flatMap(module -> module.getContentTypeDescriptors().stream()).forEach(descriptor -> typeOptions.add(new PairType((Serializable)((Object)descriptor.getIdentifier()), (Serializable)((Object)this.getText(descriptor.getI18NKey())))));
        return typeOptions;
    }

    public List<PairType> getDateRanges() {
        LinkedList<PairType> dateRanges = new LinkedList<PairType>();
        dateRanges.add(new PairType(null, (Serializable)((Object)this.getText("modified.any.date"))));
        dateRanges.add(new PairType((Serializable)DateRangeEnum.LASTDAY, (Serializable)((Object)this.getText("modified.today"))));
        dateRanges.add(new PairType((Serializable)DateRangeEnum.LASTWEEK, (Serializable)((Object)this.getText("modified.lastweek"))));
        dateRanges.add(new PairType((Serializable)DateRangeEnum.LASTMONTH, (Serializable)((Object)this.getText("modified.lastmonth"))));
        dateRanges.add(new PairType((Serializable)DateRangeEnum.LASTYEAR, (Serializable)((Object)this.getText("modified.lastyear"))));
        return dateRanges;
    }

    public void setCQLSearcher(CQLSearcher cqlSearcher) {
        this.cqlSearcher = cqlSearcher;
    }

    public SearchPageResponse getCqlSearchResults() {
        return this.cqlSearchResults;
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public UUID getSessionUuid() {
        return this.sessionUuid;
    }

    public void setSessionUuid(UUID sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public static int getPageSize() {
        return 10;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getWhere() {
        return StringUtils.isEmpty((CharSequence)this.where) ? ALL : this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public WebInterfaceManager getWebInterfaceManager() {
        return this.webInterfaceManager;
    }

    public void setWebInterfaceManager(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateRangeEnum getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(DateRangeEnum lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isSuggestArchivedSpaces() {
        return !this.spaceManager.getAllSpaceKeys(SpaceStatus.ARCHIVED).isEmpty();
    }

    public boolean isIncludeArchivedSpaces() {
        return this.includeArchivedSpaces;
    }

    public void setIncludeArchivedSpaces(boolean includeArchivedSpaces) {
        this.includeArchivedSpaces = includeArchivedSpaces;
    }

    public String getCql() {
        return this.cql;
    }

    public void setCql(String cql) {
        this.cql = cql;
    }

    public String getQueryParameters() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty((CharSequence)this.queryString)) {
            builder.append("queryString=").append(HtmlUtil.urlEncode((String)this.queryString)).append("&");
        }
        if (StringUtils.isNotEmpty((CharSequence)this.where)) {
            builder.append("where=").append(HtmlUtil.urlEncode((String)this.where)).append("&");
        }
        if (StringUtils.isNotEmpty((CharSequence)this.type)) {
            builder.append("type=").append(HtmlUtil.urlEncode((String)this.type)).append("&");
        }
        if (StringUtils.isNotEmpty((CharSequence)this.contributor)) {
            builder.append("contributor=").append(HtmlUtil.urlEncode((String)this.contributor)).append("&");
        }
        if (StringUtils.isNotEmpty((CharSequence)this.contributorUsername)) {
            builder.append("contributorUsername=").append(HtmlUtil.urlEncode((String)this.contributorUsername)).append("&");
        }
        if (this.lastModified != null) {
            builder.append("lastModified=").append(HtmlUtil.urlEncode((String)this.lastModified.toString())).append("&");
        }
        if (this.includeArchivedSpaces) {
            builder.append("includeArchivedSpaces=true").append("&");
        }
        if (StringUtils.isNotEmpty((CharSequence)this.cql)) {
            builder.append("cql=").append(HtmlUtil.urlEncode((String)this.cql)).append("&");
        }
        return builder.toString();
    }

    public BackwardsCompatibility getSearchQuery() {
        return this.searchQuery;
    }

    public void setSearchQuery(BackwardsCompatibility searchQuery) {
        this.searchQuery = searchQuery;
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @ParameterSafe
    public class BackwardsCompatibility {
        public void setQueryString(String queryString) {
            SearchAction.this.queryString = queryString;
        }

        public void setSpaceKey(String spaceKey) {
            SearchAction.this.where = spaceKey;
        }

        public void setType(String type) {
            SearchAction.this.type = type;
        }

        public void setLastModified(String lastModified) {
            SearchAction.this.lastModified = StringUtils.isBlank((CharSequence)lastModified) ? null : DateRangeEnum.valueOf((String)lastModified.toUpperCase());
        }
    }
}

