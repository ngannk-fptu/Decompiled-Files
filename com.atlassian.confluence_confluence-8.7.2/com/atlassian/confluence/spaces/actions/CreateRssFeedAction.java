/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.rometools.rome.feed.synd.SyndFeed
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.rss.FeedCustomContentType;
import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.confluence.rss.RssFeedExecutionEvent;
import com.atlassian.confluence.rss.SyndFeedService;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContainingContentTypeQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.InheritedLabelQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.rometools.rome.feed.synd.SyndFeed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiresAnyConfluenceAccess
public class CreateRssFeedAction
extends ConfluenceActionSupport {
    private static final String DEFAULT_RSS_TYPE = "atom";
    private static final String DEFAULT_SORT = "modified";
    private static final int UNLIMITED_TIMESPAN = -1;
    private static final String ATOM03 = "atom03";
    private static final String ATOM10 = "atom10";
    private static final Logger log = LoggerFactory.getLogger(CreateRssFeedAction.class);
    private String rssType;
    private String sort;
    private String title;
    private List<String> spaces = new ArrayList<String>();
    private List<String> excludedSpaceKeys = new ArrayList<String>();
    private List<String> types = new ArrayList<String>();
    private int maxResults;
    private int timeSpan;
    private String labelString;
    private SyndFeedService syndFeedService;
    private EventPublisher eventPublisher;
    private boolean showContent = true;
    private List<String> pageSubTypes = new ArrayList<String>();
    private List<String> blogSubTypes = new ArrayList<String>();
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    public SyndFeed getSyndFeed() {
        ISearch search = this.createSearchQuery();
        FeedProperties props = new FeedProperties(this.getTitle(), this.getDescription(), this.showContent, this.getAuthenticatedUser() == null);
        return this.syndFeedService.createSyndFeed(search, props);
    }

    private String getDescription() {
        return "Confluence Syndication Feed";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.eventPublisher.publish((Object)new RssFeedExecutionEvent(this.getRssType()));
        String rssType = this.getRssType();
        if (!Arrays.asList("rss", "rss1", "rss2", DEFAULT_RSS_TYPE, ATOM03, ATOM10).contains(rssType)) {
            return "blank";
        }
        return rssType;
    }

    public @NonNull String getRssType() {
        if (this.rssType == null || this.rssType.contains(",")) {
            return DEFAULT_RSS_TYPE;
        }
        return this.rssType;
    }

    public void setBlogpostSubTypes(List<String> blogSubTypes) {
        this.blogSubTypes = blogSubTypes;
    }

    public void setPageSubTypes(List<String> pageSubTypes) {
        this.pageSubTypes = pageSubTypes;
    }

    public void setRssType(String rssType) {
        this.rssType = rssType;
    }

    public String getSort() {
        if (this.sort == null) {
            return DEFAULT_SORT;
        }
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setSpaces(List<String> spaces) {
        this.spaces = spaces;
    }

    public void setExcludedSpaceKeys(List<String> excludedSpaceKeys) {
        this.excludedSpaceKeys = excludedSpaceKeys;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void setTimeSpan(int timeSpan) {
        this.timeSpan = timeSpan;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFeedBuilder(SyndFeedService feedBuilder) {
        this.syndFeedService = feedBuilder;
    }

    public String getLabelString() {
        return this.labelString;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }

    public boolean isShowContent() {
        return this.showContent;
    }

    public void setShowContent(boolean showContent) {
        this.showContent = showContent;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public List<Label> getLabels() {
        ArrayList<Label> labels = new ArrayList<Label>();
        for (String labelName : LabelUtil.split(this.getLabelString())) {
            Label label = this.labelManager.getLabel(labelName);
            if (label != null) {
                labels.add(label);
                continue;
            }
            labels.add(new Label(labelName));
        }
        return labels;
    }

    public ISearch createSearchQuery() {
        HashSet<SearchQuery> mustQueries = new HashSet<SearchQuery>();
        HashSet<InSpaceQuery> notQueries = new HashSet<InSpaceQuery>();
        HashSet<String> spaceKeys = new HashSet<String>();
        HashSet<SpaceCategoryEnum> spaceCategories = new HashSet<SpaceCategoryEnum>();
        mustQueries.add(this.getTimeSpanQuery());
        this.splitIntoSpaceKeysAndCategories(this.spaces.iterator(), spaceKeys, spaceCategories);
        if (!(spaceCategories.contains((Object)SpaceCategoryEnum.ALL) || spaceCategories.isEmpty() && this.spaces.isEmpty())) {
            mustQueries.add(this.createSpacesQuery(spaceKeys, spaceCategories));
        }
        if (!this.excludedSpaceKeys.isEmpty()) {
            notQueries.add(new InSpaceQuery((Set<String>)new HashSet<String>(this.excludedSpaceKeys)));
        }
        this.addQueriesIfNotEmpty(mustQueries, this.createContentTypeQueries());
        this.addQueriesIfNotEmpty(mustQueries, this.createLabelQueries());
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        searchQueryBuilder.addMust(mustQueries);
        searchQueryBuilder.addMustNot(notQueries);
        searchQueryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        int limit = Math.min(this.maxResults, this.settingsManager.getGlobalSettings().getMaxRssItems());
        return new ContentSearch(searchQueryBuilder.build(), this.getSearchSort(), 0, limit);
    }

    private void addQueriesIfNotEmpty(Set<SearchQuery> querySet, Set<SearchQuery> queriesToAdd) {
        if (!queriesToAdd.isEmpty()) {
            querySet.add(BooleanQuery.composeOrQuery(queriesToAdd));
        }
    }

    private Set<SearchQuery> createLabelQueries() {
        HashSet<SearchQuery> labelQueries = new HashSet<SearchQuery>();
        for (Label label : this.getLabels()) {
            labelQueries.add(new LabelQuery(label));
            labelQueries.add(new InheritedLabelQuery(label));
        }
        return labelQueries;
    }

    private Set<SearchQuery> createContentTypeQueries() {
        Set<ContentTypeEnum> blogContentTypes;
        HashSet<SearchQuery> typeQueries = new HashSet<SearchQuery>();
        Set<ContentTypeEnum> contentTypes = this.toContentTypeEnumSet(this.types);
        Set<FeedCustomContentType> customContentTypes = this.toCustomContentTypeSet(this.types);
        if (!contentTypes.isEmpty()) {
            typeQueries.add(new ContentTypeQuery(contentTypes));
        }
        for (FeedCustomContentType customContentType : customContentTypes) {
            typeQueries.add(customContentType.toSearchQuery());
        }
        Set<ContentTypeEnum> pageContentTypes = this.toContentTypeEnumSet(this.pageSubTypes);
        if (!pageContentTypes.isEmpty()) {
            typeQueries.add(ContainingContentTypeQuery.searchForTypesWithinContainerType(ContentTypeEnum.PAGE, pageContentTypes));
        }
        if (!(blogContentTypes = this.toContentTypeEnumSet(this.blogSubTypes)).isEmpty()) {
            typeQueries.add(ContainingContentTypeQuery.searchForTypesWithinContainerType(ContentTypeEnum.BLOG, blogContentTypes));
        }
        return typeQueries;
    }

    private Set<FeedCustomContentType> toCustomContentTypeSet(List<String> types) {
        HashSet<FeedCustomContentType> customContentTypes = new HashSet<FeedCustomContentType>();
        for (String type : types) {
            FeedCustomContentType customType = this.getCustomContentType(type);
            if (customType == null) continue;
            customContentTypes.add(customType);
        }
        return customContentTypes;
    }

    private FeedCustomContentType getCustomContentType(String identifier) {
        for (FeedCustomContentType customContentType : this.pluginAccessor.getEnabledModulesByClass(FeedCustomContentType.class)) {
            if (!customContentType.getIdentifier().equals(identifier)) continue;
            return customContentType;
        }
        return null;
    }

    private SearchQuery createSpacesQuery(Set<String> spaceKeys, Set<SpaceCategoryEnum> spaceCategories) {
        HashSet<SearchQuery> spaceQueries = new HashSet<SearchQuery>();
        if (!spaceCategories.isEmpty()) {
            spaceQueries.add(new SpaceCategoryQuery(spaceCategories, this.labelManager));
        } else if (!this.spaces.isEmpty()) {
            spaceQueries.add(new InSpaceQuery(spaceKeys));
        }
        return BooleanQuery.composeOrQuery(spaceQueries);
    }

    private Set<ContentTypeEnum> toContentTypeEnumSet(List<String> typeStrings) {
        HashSet<ContentTypeEnum> contentTypes = new HashSet<ContentTypeEnum>();
        for (String type : typeStrings) {
            ContentTypeEnum typeEnum = ContentTypeEnum.getByRepresentation(type);
            if (typeEnum == null) continue;
            contentTypes.add(typeEnum);
        }
        return contentTypes;
    }

    private SearchQuery getTimeSpanQuery() {
        int daysAgo = this.timeSpan == 0 ? 7 : this.timeSpan;
        DateRangeQuery timeLimitingQuery = null;
        if (daysAgo != -1) {
            Calendar fromDate = Calendar.getInstance();
            fromDate.add(6, -daysAgo);
            timeLimitingQuery = new DateRangeQuery(new DateRangeQuery.DateRange(fromDate.getTime(), null, true, false), DateRangeQuery.DateRangeQueryType.MODIFIED);
        }
        return timeLimitingQuery;
    }

    private void splitIntoSpaceKeysAndCategories(Iterator<String> keys, Collection<String> spaceKeys, Collection<SpaceCategoryEnum> spaceCategories) {
        while (keys.hasNext()) {
            String key = keys.next();
            SpaceCategoryEnum category = SpaceCategoryEnum.get(key);
            if (category == null) {
                spaceKeys.add(key);
                continue;
            }
            spaceCategories.add(category);
        }
    }

    private SearchSort getSearchSort() {
        if ("created".equals(this.getSort())) {
            return CreatedSort.DEFAULT;
        }
        if (DEFAULT_SORT.equals(this.getSort())) {
            return ModifiedSort.DEFAULT;
        }
        log.warn("Unrecognised search sort order: " + this.getSort() + ", defaulting to sort by date modified");
        return ModifiedSort.DEFAULT;
    }

    public SiteSearchPermissionsQueryFactory getSiteSearchPermissionsQueryFactory() {
        return this.siteSearchPermissionsQueryFactory;
    }

    public void setSiteSearchPermissionsQueryFactory(SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }
}

