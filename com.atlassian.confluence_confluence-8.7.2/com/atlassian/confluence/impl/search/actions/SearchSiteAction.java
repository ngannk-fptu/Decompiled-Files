/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.search.v2.summary.Summary
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.Event
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.ParameterSafe
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.queryparser.classic.QueryParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.search.SearchPerformedEvent;
import com.atlassian.confluence.impl.search.actions.Timer;
import com.atlassian.confluence.internal.search.DelegatedSearchResultRenderer;
import com.atlassian.confluence.internal.search.SpacePickerHelper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.search.SearchResultRenderContext;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.plugin.SiteSearchPluginModule;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.confluence.search.summary.HitHighlighterFactory;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.search.v2.summary.Summary;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.util.PairType;
import com.atlassian.event.Event;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.ParameterSafe;
import com.atlassian.xwork.PermittedMethods;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SearchSiteAction
extends ConfluenceActionSupport
implements Beanable {
    private static final Logger log = LoggerFactory.getLogger(SearchSiteAction.class);
    private static final Pattern QUERY_SPLIT_REGEX = Pattern.compile("\\s+(?:and\\s+|or\\s+)?");
    private static final Timer timer = new Timer();
    private SpacePickerHelper spacePickerHelper;
    private PaginationSupport paginationSupport;
    private SearchManager searchManager;
    private HitHighlighterFactory hitHighlighterFactory;
    private SpaceManager spaceManager;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private LuceneSearchMapper searchMapper;
    private ContentTypesDisplayMapper contentTypesDisplayMapper;
    private PluginAccessor pluginAccessor;
    private Analyzer queryAnalyzer;
    private HitHighlighter highlighter;
    private int startIndex;
    private List<SearchResult> results;
    private String searchUuid;
    private UserDetails validatedContributor;
    private String key;
    private String queryString;
    private String where;
    private String[] types;
    private String contributor;
    private String contributorUsername;
    private DateRangeEnum lastModified;
    private static final int MAX_RESULTS_PER_PAGE = 10;
    private int totalSearchResults;
    private List<String> searchWords = Collections.emptyList();
    private final BackwardsCompatibility searchQuery = new BackwardsCompatibility();
    private final List<PairType> typeOptions = new ArrayList<PairType>(9);
    private final List<PairType> dateRanges = new ArrayList<PairType>(5);
    private List<Label> relatedLabels;
    private static final int MAX_RELATED_LABELS_TO_DISPLAY = 10;
    private static final String AUTOCOMPLETE_USER_DETAILS_FORMAT = "{0} ({1})";
    private boolean includeArchivedSpaces = false;
    private DelegatedSearchResultRenderer renderer;

    public SearchSiteAction() {
        this.paginationSupport = new PaginationSupport(10);
    }

    @Override
    public void validate() {
        super.validate();
        timer.start("Query validation");
        if (StringUtils.isBlank((CharSequence)this.queryString)) {
            this.addFieldError("queryString", this.getText("error.missing.search.term"));
            return;
        }
        if (this.queryString.startsWith("*")) {
            this.addFieldError("queryString", this.getText("error.star.cannot.lead"));
        } else {
            try {
                this.searchMapper.convertToLuceneQuery(new TextFieldQuery(SearchFieldNames.CONTENT, this.queryString, BooleanOperator.AND));
            }
            catch (IllegalArgumentException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error parsing query.", (Throwable)e);
                }
                this.addFieldError("queryString", this.getText("error.invalid.search.term"));
            }
        }
        if (StringUtils.isNotBlank((CharSequence)this.contributorUsername)) {
            this.validatedContributor = StringUtils.isNotBlank((CharSequence)this.contributor) ? new UserDetails(this.contributorUsername, this.contributor) : new UserDetails(this.contributorUsername);
        } else if (StringUtils.isNotBlank((CharSequence)this.contributor)) {
            List<UserDetails> contributors = this.getContributors(this.contributor);
            if (contributors.isEmpty()) {
                this.addFieldError("contributor", this.getText("error.invalid.search.nouser"));
            } else if (contributors.size() > 1) {
                this.addFieldError("contributor", this.getText("error.invalid.search.multipleusers"));
            } else {
                this.validatedContributor = contributors.get(0);
            }
        }
        timer.stop("Query validation");
    }

    @Override
    public String doDefault() throws Exception {
        return super.doDefault();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        SearchResults searchResults;
        ConfluenceUser user;
        if (StringUtils.isBlank((CharSequence)this.queryString)) {
            return "success";
        }
        SearchQueryParameters params = new SearchQueryParameters(this.queryString);
        SpaceCategoryEnum spaceCategory = SpaceCategoryEnum.get(this.where);
        params.setCategory(spaceCategory);
        if (spaceCategory == null && this.where != null) {
            params.setSpaceKey(this.where);
        }
        DateRangeQuery.DateRange lastModifiedRange = this.lastModified != null ? this.lastModified.dateRange() : null;
        params.setLastModified(lastModifiedRange);
        HashSet<ContentTypeEnum> contentTypes = new HashSet<ContentTypeEnum>();
        HashSet<ContentTypeSearchDescriptor> pluginContentTypes = new HashSet<ContentTypeSearchDescriptor>();
        this.splitIntoContentTypes(this.types, contentTypes, pluginContentTypes);
        params.setContentTypes(contentTypes);
        params.setPluginContentTypes(pluginContentTypes);
        params.setIncludeArchivedSpaces(this.includeArchivedSpaces);
        if (this.validatedContributor != null && (user = this.userAccessor.getUserByName(this.validatedContributor.getUsername())) != null) {
            params.setContributor(user);
            this.setContributor(this.validatedContributor.getFormattedName());
            this.setContributorUsername(this.validatedContributor.getUsername());
        }
        timer.start("Search");
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(params, this.startIndex, 10);
        try {
            searchResults = this.searchManager.search(search);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid query params specified: [" + params + "] produced an invalid search query.", e);
        }
        catch (InvalidSearchException e) {
            log.warn("Failure executing search for term " + this.queryString);
            String string = "error";
            return string;
        }
        finally {
            timer.stop("Search");
        }
        SearchPerformedEvent searchPerformedEvent = new SearchPerformedEvent(this, search.getQuery(), AuthenticatedUserThreadLocal.get(), searchResults.size());
        this.eventManager.publishEvent((Event)searchPerformedEvent);
        this.totalSearchResults = searchResults.getUnfilteredResultsCount();
        this.searchWords = searchResults.getSearchWords();
        this.paginationSupport.setTotal(this.totalSearchResults);
        this.paginationSupport.setStartIndex(this.startIndex);
        this.results = searchResults.getAll();
        this.searchUuid = searchPerformedEvent.getUuid();
        return "success";
    }

    @Override
    public Object getBean() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", this.paginationSupport.getTotal());
        result.put("startIndex", this.paginationSupport.getStartIndex());
        result.put("results", this.results);
        return result;
    }

    @HtmlSafe
    public String newSearchResult(SearchResult searchResult, String showExcerpts, String queryString) {
        SearchResultRenderContext renderContext = new SearchResultRenderContext(queryString, showExcerpts);
        return this.renderer.render(searchResult, renderContext);
    }

    private List<UserDetails> getContributors(String rawUserQuery) {
        List<Object> userResults;
        String userQuery = QueryParser.escape((String)rawUserQuery);
        UserDetails exactMatchDetails = this.exactUsernameSearch(userQuery);
        if (exactMatchDetails != null) {
            return Collections.singletonList(exactMatchDetails);
        }
        ISearch search = this.predefinedSearchBuilder.buildUsersSearch(userQuery, 10);
        try {
            SearchResults searchResults = this.searchManager.search(search);
            this.eventManager.publishEvent((Event)new SearchPerformedEvent(this, search.getQuery(), AuthenticatedUserThreadLocal.get(), searchResults.size()));
            userResults = searchResults.getAll();
        }
        catch (InvalidSearchException e) {
            log.warn("Error performing a search for users with: " + userQuery, (Throwable)e);
            userResults = Collections.emptyList();
        }
        ArrayList<UserDetails> result = new ArrayList<UserDetails>(userResults.size());
        for (SearchResult searchResult : userResults) {
            String username = searchResult.getExtraFields().get("username");
            String fullName = searchResult.getExtraFields().get("fullName");
            if (!StringUtils.isNotBlank((CharSequence)username) || !StringUtils.isNotBlank((CharSequence)fullName)) continue;
            UserDetails userDetails = new UserDetails(username, MessageFormat.format(AUTOCOMPLETE_USER_DETAILS_FORMAT, fullName, username));
            result.add(userDetails);
        }
        return result;
    }

    public WebInterfaceContext getWebInterfaceContext(SearchResult result) {
        DefaultWebInterfaceContext context = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        context.setParameter("searchResult", result);
        return context;
    }

    private UserDetails exactUsernameSearch(String username) {
        TextFieldQuery usernameFieldQuery = new TextFieldQuery("username", username, BooleanOperator.AND);
        ContentTypeQuery personalInformationQuery = new ContentTypeQuery(ContentTypeEnum.PERSONAL_INFORMATION);
        SearchQuery andQuery = BooleanQuery.andQuery(personalInformationQuery, usernameFieldQuery);
        UserDetails userDetails = null;
        try {
            SearchResults results = this.searchManager.search(new ContentSearch(andQuery, null, 0, 1));
            Iterator<SearchResult> it = results.iterator();
            if (it.hasNext()) {
                SearchResult result = it.next();
                String fullname = result.getExtraFields().get("fullName");
                userDetails = StringUtils.isNotBlank((CharSequence)fullname) ? new UserDetails(username, MessageFormat.format(AUTOCOMPLETE_USER_DETAILS_FORMAT, fullname, username)) : new UserDetails(username);
            } else {
                log.debug("No PersonalInformation found in the index for username " + username);
            }
        }
        catch (InvalidSearchException ex) {
            log.warn("Failure during search for username " + username, (Throwable)ex);
        }
        return userDetails;
    }

    private HitHighlighter getHighlighter() {
        if (this.highlighter == null) {
            this.highlighter = this.hitHighlighterFactory.create(this.queryString);
        }
        return this.highlighter;
    }

    @HtmlSafe
    public String getSummaryForResult(SearchResult result) {
        String content = result.getContent();
        String title = result.getDisplayTitle();
        if (content != null && content.startsWith(title)) {
            return this.getHighlighter().getSummary(content.substring(title.length()));
        }
        return this.getHighlighter().getSummary(content);
    }

    @HtmlSafe
    public String getTitleForResult(SearchResult result) {
        return this.getHighlighter().highlightText(result.getDisplayTitle());
    }

    public List<SearchResult> getResults() {
        return this.results;
    }

    public List<SpacePickerHelper.SpaceDTO> getAvailableGlobalSpaces() {
        return this.getSpacePickerHelper().getAvailableGlobalSpaces(this.getAuthenticatedUser());
    }

    public List<PairType> getAggregateOptions() {
        return this.getSpacePickerHelper().getAggregateOptions(this);
    }

    public List<SpacePickerHelper.SpaceDTO> getFavouriteSpaces() {
        return this.getSpacePickerHelper().getFavouriteSpaces(this.getAuthenticatedUser());
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    private SpacePickerHelper getSpacePickerHelper() {
        if (this.spacePickerHelper == null) {
            this.spacePickerHelper = new SpacePickerHelper(this.spaceManager, this.labelManager);
        }
        return this.spacePickerHelper;
    }

    private List<Label> getMatchingLabels() {
        Set<String> unparsedLabelNames = SearchSiteAction.splitSearchToLabels(this.queryString);
        return this.labelManager.getLabels(unparsedLabelNames);
    }

    public List<Label> getRelatedLabels() {
        if (this.relatedLabels == null) {
            this.relatedLabels = new ArrayList<Label>();
            List<Label> matchingLabels = this.getMatchingLabels();
            this.relatedLabels.addAll(matchingLabels);
            this.relatedLabels.addAll(this.labelManager.getRelatedLabels(matchingLabels, null, 5));
            this.relatedLabels = this.relatedLabels.subList(0, Math.min(this.relatedLabels.size(), 10));
        }
        return this.relatedLabels;
    }

    public String getLabelNames(Labelable obj) {
        return LabelUtil.convertToDelimitedString(obj, this.getAuthenticatedUser());
    }

    public List getContentLabels(ContentEntityObject content, int amount) {
        List<Label> labels = content.getLabels();
        if (labels.size() < amount) {
            return labels;
        }
        return labels.subList(0, amount);
    }

    public Space getSpace(String spaceKey) {
        return this.spaceManager.getSpace(spaceKey);
    }

    public String getTypeName(String type) {
        for (PairType pairType : this.getTypeOptions()) {
            if (!type.equals(pairType.getKey())) continue;
            return pairType.getValue().toString().toLowerCase();
        }
        return "";
    }

    public List<PairType> getTypeOptions() {
        this.typeOptions.add(new PairType(null, (Serializable)((Object)this.getText("type.allcontent"))));
        this.typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.PAGE.getRepresentation()), (Serializable)((Object)this.getText("type.pages"))));
        this.typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.BLOG.getRepresentation()), (Serializable)((Object)this.getText("type.newsitems"))));
        this.typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.COMMENT.getRepresentation()), (Serializable)((Object)this.getText("type.comments"))));
        this.typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.ATTACHMENT.getRepresentation()), (Serializable)((Object)this.getText("type.attachments"))));
        this.typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.PERSONAL_INFORMATION.getRepresentation()), (Serializable)((Object)this.getText("type.profiles"))));
        this.typeOptions.add(new PairType((Serializable)((Object)ContentTypeEnum.SPACE_DESCRIPTION.getRepresentation()), (Serializable)((Object)this.getText("type.space.desc"))));
        List siteSearchPluginModules = this.pluginAccessor.getEnabledModulesByClass(SiteSearchPluginModule.class);
        for (SiteSearchPluginModule siteSearchPluginModule : siteSearchPluginModules) {
            for (ContentTypeSearchDescriptor contentTypeDescriptor : siteSearchPluginModule.getContentTypeDescriptors()) {
                this.typeOptions.add(new PairType((Serializable)((Object)contentTypeDescriptor.getIdentifier()), (Serializable)((Object)this.getText(contentTypeDescriptor.getI18NKey()))));
            }
        }
        return this.typeOptions;
    }

    public List<PairType> getDateRanges() {
        if (this.dateRanges.size() != 0) {
            return this.dateRanges;
        }
        this.dateRanges.add(new PairType(null, (Serializable)((Object)this.getText("modified.any.date"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTDAY), (Serializable)((Object)this.getText("modified.today"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTTWODAYS), (Serializable)((Object)this.getText("modified.yesterday"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTWEEK), (Serializable)((Object)this.getText("modified.lastweek"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTMONTH), (Serializable)((Object)this.getText("modified.lastmonth"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTSIXMONTHS), (Serializable)((Object)this.getText("modified.lastsixmonths"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTYEAR), (Serializable)((Object)this.getText("modified.lastyear"))));
        this.dateRanges.add(new PairType((Serializable)((Object)DateRangeEnum.LASTTWOYEARS), (Serializable)((Object)this.getText("modified.lasttwoyears"))));
        return this.dateRanges;
    }

    @Override
    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public CriteriaParameters getCurrentSearch() {
        return new CriteriaParameters(null, this.queryString, this.where, this.getContentType(), this.lastModified, this.contributor, this.contributorUsername);
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getEndIndex() {
        return Math.min(this.startIndex + 10, this.totalSearchResults);
    }

    public Timer getTimer() {
        return timer;
    }

    static Set<String> splitSearchToLabels(String queryStr) {
        if (StringUtils.isBlank((CharSequence)queryStr)) {
            return Collections.emptySet();
        }
        String[] terms = QUERY_SPLIT_REGEX.split(queryStr.toLowerCase());
        HashSet<String> uniqueTerms = new HashSet<String>(terms.length);
        uniqueTerms.addAll(Arrays.asList(terms));
        return uniqueTerms;
    }

    public Summary getLabelsSummaryForResult(SearchResult result) {
        String labels = StringUtils.join(result.getLabels(AuthenticatedUserThreadLocal.get()), (String)" ");
        return GeneralUtil.makeSummary(labels, this.queryString);
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        if (this.types == null || this.types.length == 0) {
            return null;
        }
        return this.types[0];
    }

    private void splitIntoContentTypes(String[] types, Set<ContentTypeEnum> builtInTypes, Set<ContentTypeSearchDescriptor> pluginTypes) {
        if (types == null) {
            return;
        }
        for (String type : types) {
            if (StringUtils.isBlank((CharSequence)type)) continue;
            ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation(type);
            if (contentType == null) {
                ContentTypeSearchDescriptor descriptor = this.findSearchTypeDescriptorByRepresentation(type);
                if (descriptor == null) {
                    log.warn("Unknown type (" + type + ") was specified in the search. ContentType will be ignored.");
                    continue;
                }
                pluginTypes.add(descriptor);
                continue;
            }
            builtInTypes.add(contentType);
        }
    }

    private ContentTypeSearchDescriptor findSearchTypeDescriptorByRepresentation(String type) {
        List modules = this.pluginAccessor.getEnabledModulesByClass(SiteSearchPluginModule.class);
        for (SiteSearchPluginModule module : modules) {
            for (ContentTypeSearchDescriptor searchDescriptor : module.getContentTypeDescriptors()) {
                if (!searchDescriptor.getIdentifier().equals(type)) continue;
                return searchDescriptor;
            }
        }
        return null;
    }

    public ContentTypeEnum getContentType() {
        return ContentTypeEnum.getByRepresentation(this.getType());
    }

    public void setType(String type) {
        this.types = new String[]{type};
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public DateRangeEnum getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(DateRangeEnum lastModified) {
        this.lastModified = lastModified;
    }

    public int getTotalSearchResults() {
        return this.totalSearchResults;
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public void setSearchMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
    }

    public ContentTypesDisplayMapper getContentTypesDisplayMapper() {
        return this.contentTypesDisplayMapper;
    }

    @Override
    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setContentTypesDisplayMapper(ContentTypesDisplayMapper contentTypesDisplayMapper) {
        this.contentTypesDisplayMapper = contentTypesDisplayMapper;
    }

    public void setQueryAnalyzer(Analyzer queryAnalyzer) {
        this.queryAnalyzer = queryAnalyzer;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public List<String> getSearchWords() {
        return this.searchWords;
    }

    public BackwardsCompatibility getSearchQuery() {
        return this.searchQuery;
    }

    public void setContributor(String who) {
        this.contributor = who;
    }

    public String getContributor() {
        return this.contributor;
    }

    public String getContributorUsername() {
        return this.contributorUsername;
    }

    public void setContributorUsername(String contributorUsername) {
        this.contributorUsername = contributorUsername;
    }

    public void setPredefinedSearchBuilder(PredefinedSearchBuilder predefinedSearchBuilder) {
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    public boolean isIncludeArchivedSpaces() {
        return this.includeArchivedSpaces;
    }

    public void setIncludeArchivedSpaces(boolean includeArchivedSpaces) {
        this.includeArchivedSpaces = includeArchivedSpaces;
    }

    public boolean isSuggestArchivedSpaces() {
        return !this.spaceManager.getAllSpaceKeys(SpaceStatus.ARCHIVED).isEmpty();
    }

    public String getSearchUuid() {
        return this.searchUuid;
    }

    public void setDelegatedSearchResultRenderer(DelegatedSearchResultRenderer defaultRenderer) {
        this.renderer = defaultRenderer;
    }

    public void setHitHighlighterFactory(HitHighlighterFactory hitHighlighterFactory) {
        this.hitHighlighterFactory = hitHighlighterFactory;
    }

    public static class UserDetails {
        private final String username;
        private final String formattedName;

        public UserDetails(String username, String formattedName) {
            this.username = username;
            this.formattedName = formattedName;
        }

        public UserDetails(String username) {
            this.username = username;
            this.formattedName = username;
        }

        public String getUsername() {
            return this.username;
        }

        public String getFormattedName() {
            return this.formattedName;
        }
    }

    @ParameterSafe
    public class BackwardsCompatibility {
        public void setQueryString(String queryString) {
            SearchSiteAction.this.queryString = queryString;
        }

        public void setSpaceKey(String spaceKey) {
            SearchSiteAction.this.where = spaceKey;
        }

        public void setType(String type) {
            SearchSiteAction.this.types = new String[]{type};
        }

        public void setLastModified(String lastModified) {
            SearchSiteAction.this.lastModified = StringUtils.isBlank((CharSequence)lastModified) ? null : DateRangeEnum.valueOf(lastModified.toUpperCase());
        }
    }

    public static class CriteriaParameters {
        private final String queryString;
        private final String spaceKey;
        private final ContentTypeEnum type;
        private final DateRangeEnum lastModified;
        private final String contributor;
        private final String contributorUsername;
        private final String labelKey;

        public CriteriaParameters(String labelKey, String queryString, String spaceKey, ContentTypeEnum type, DateRangeEnum lastModified, String contributor, String contributorUsername) {
            this.labelKey = labelKey;
            this.queryString = queryString;
            this.spaceKey = spaceKey;
            this.type = type;
            this.lastModified = lastModified;
            this.contributor = contributor;
            this.contributorUsername = contributorUsername;
        }

        @HtmlSafe
        public String getQueryParameters() {
            StringBuilder result = new StringBuilder();
            if (StringUtils.isNotBlank((CharSequence)this.queryString)) {
                result.append("&").append("queryString=").append(HtmlUtil.urlEncode(this.queryString));
            }
            if (StringUtils.isNotBlank((CharSequence)this.spaceKey)) {
                result.append("&").append("where=").append(HtmlUtil.urlEncode(this.spaceKey));
            } else {
                result.append("&").append("where=").append(HtmlUtil.urlEncode(SpaceCategoryEnum.ALL.getRepresentation()));
            }
            if (this.type != null) {
                result.append("&").append("type=").append(HtmlUtil.urlEncode(this.type.getRepresentation()));
            }
            if (this.lastModified != null) {
                result.append("&").append("lastModified=").append(HtmlUtil.urlEncode(this.lastModified.name()));
            }
            if (StringUtils.isNotBlank((CharSequence)this.contributor)) {
                result.append("&").append("contributor=").append(HtmlUtil.urlEncode(this.contributor));
            }
            if (StringUtils.isNotBlank((CharSequence)this.contributorUsername)) {
                result.append("&").append("contributorUsername=").append(HtmlUtil.urlEncode(this.contributorUsername));
            }
            return result.substring(1);
        }

        public String getLabelKey() {
            return this.labelKey;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public ContentTypeEnum getType() {
            return this.type;
        }

        public DateRangeEnum getLastModified() {
            return this.lastModified;
        }

        public String getQueryString() {
            return this.queryString;
        }

        public String getContributor() {
            return this.contributor;
        }

        public String getContributorUsername() {
            return this.contributorUsername;
        }
    }
}

