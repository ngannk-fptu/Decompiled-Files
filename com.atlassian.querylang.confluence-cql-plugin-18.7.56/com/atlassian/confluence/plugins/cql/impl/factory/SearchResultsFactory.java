/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.search.ContainerSummary
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchOptions$Excerpt
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.core.datetime.DateFormatterFactory
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.collections.CompositeMap
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl.factory;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.search.ContainerSummary;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.plugins.cql.impl.SearchTypeManager;
import com.atlassian.confluence.plugins.cql.impl.factory.ContentSearchResultsFactory;
import com.atlassian.confluence.plugins.cql.impl.factory.ModelResultFactory;
import com.atlassian.confluence.plugins.cql.impl.factory.SpaceSearchResultsFactory;
import com.atlassian.confluence.plugins.cql.impl.factory.UserSearchResultsFactory;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.collections.CompositeMap;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchResultsFactory {
    private static final Logger log = LoggerFactory.getLogger(SearchResultsFactory.class);
    public static final String SEARCH_SCREEN_FEATURE_KEY = "cql.search.screen";
    private final ContentSearchResultsFactory contentSearchResultsFactory;
    private final SpaceSearchResultsFactory spaceSearchResultsFactory;
    private final UserSearchResultsFactory userSearchResultsFactory;
    private final SearchTypeManager searchTypeManager;
    private final I18nResolver i18nResolver;
    private final DateFormatterFactory dateFormatterFactory;
    private Iterable<ModelResultFactory> delegateFactories;
    private final ImmutableSet<String> requiredIndexFields = ImmutableSet.of((Object)SearchFieldNames.ATTACHMENT_OWNER_REAL_TITLE, (Object)SearchFieldNames.ATTACHMENT_OWNER_URL_PATH, (Object)SearchFieldNames.PAGE_URL_PATH, (Object)SearchFieldNames.PAGE_DISPLAY_TITLE, (Object)SearchFieldNames.HOME_PAGE, (Object)SearchFieldNames.ATTACHMENT_MIME_TYPE, (Object[])new String[]{SearchFieldNames.LAST_MODIFICATION_DATE});

    @Autowired
    public SearchResultsFactory(ContentSearchResultsFactory contentSearchResultsFactory, SpaceSearchResultsFactory spaceSearchResultsFactory, UserSearchResultsFactory userSearchResultsFactory, SearchTypeManager searchTypeManager, @ComponentImport I18nResolver i18nResolver, @ComponentImport DateFormatterFactory dateFormatterFactory) {
        this.contentSearchResultsFactory = contentSearchResultsFactory;
        this.spaceSearchResultsFactory = spaceSearchResultsFactory;
        this.userSearchResultsFactory = userSearchResultsFactory;
        this.searchTypeManager = searchTypeManager;
        this.delegateFactories = ImmutableList.of((Object)contentSearchResultsFactory, (Object)spaceSearchResultsFactory, (Object)userSearchResultsFactory);
        this.i18nResolver = i18nResolver;
        this.dateFormatterFactory = dateFormatterFactory;
    }

    public ImmutableSet<String> getRequiredIndexFields() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (ModelResultFactory factory : this.delegateFactories) {
            builder.addAll(factory.getRequiredIndexFields());
        }
        builder.addAll(this.requiredIndexFields);
        return builder.build();
    }

    public Map<SearchResult, com.atlassian.confluence.api.model.search.SearchResult<?>> buildFrom(Iterable<SearchResult> searchResults, SearchOptions options, String cqlQuery, Expansion ... expansions) {
        if (Iterables.isEmpty(searchResults)) {
            return Collections.emptyMap();
        }
        boolean shouldHighlightResult = this.shouldHighlightResult(options);
        ImmutableMap.Builder resultBuilder = ImmutableMap.builder();
        Map<SearchResult, Object> entityByResult = this.buildResultEntities(searchResults, expansions);
        FriendlyDateFormatter friendlyDateFormatter = new FriendlyDateFormatter(this.dateFormatterFactory.createForUser());
        for (SearchResult searchResult : searchResults) {
            Object entity = entityByResult.get(searchResult);
            if (entity == null) {
                if (DarkFeatures.isDarkFeatureEnabled((String)SEARCH_SCREEN_FEATURE_KEY)) {
                    log.warn("Ignoring result of type {} as it is not implemented but the {} dark feature is enabled", (Object)searchResult.getType(), (Object)SEARCH_SCREEN_FEATURE_KEY);
                    continue;
                }
                throw new NotImplementedServiceException("Search result type not implemented: " + searchResult.getType());
            }
            String friendlyLastModified = null;
            if (searchResult.getLastModificationDate() != null) {
                Message formatMessage = friendlyDateFormatter.getFormatMessage(searchResult.getLastModificationDate());
                if (formatMessage.getArguments() != null) {
                    Serializable[] arguments = (Serializable[])Arrays.copyOf(formatMessage.getArguments(), formatMessage.getArguments().length, Serializable[].class);
                    friendlyLastModified = this.i18nResolver.getText(formatMessage.getKey(), arguments);
                } else {
                    friendlyLastModified = this.i18nResolver.getText(formatMessage.getKey());
                }
            }
            resultBuilder.put((Object)searchResult, (Object)com.atlassian.confluence.api.model.search.SearchResult.builder((Object)entity).bodyExcerpt(this.getExcerpt(searchResult, options)).url(searchResult.getUrlPath()).title(shouldHighlightResult ? searchResult.getDisplayTitleWithHighlights() : searchResult.getDisplayTitle()).entityParentContainer(this.buildParentContainer(searchResult)).resultGlobalContainer(this.buildGlobalContainer(searchResult)).iconCssClass(this.searchTypeManager.getIconCssClass(searchResult)).lastModified(new DateTime((Object)searchResult.getLastModificationDate())).friendlyLastModified(friendlyLastModified).build());
        }
        return resultBuilder.build();
    }

    private ContainerSummary buildGlobalContainer(SearchResult searchResult) {
        String spaceKey = searchResult.getSpaceKey();
        String spaceName = searchResult.getSpaceName();
        if (Strings.isNullOrEmpty((String)spaceKey) || Strings.isNullOrEmpty((String)spaceName)) {
            return null;
        }
        return ContainerSummary.builder().title(spaceName).displayUrl(new Space(spaceKey).getUrlPath()).build();
    }

    private ContainerSummary buildParentContainer(SearchResult searchResult) {
        String parentTitle = null;
        String parentUrl = null;
        if (searchResult.getType().equals("attachment")) {
            parentTitle = this.getFieldFromSearchResult(searchResult, SearchFieldNames.ATTACHMENT_OWNER_REAL_TITLE);
            parentUrl = this.getFieldFromSearchResult(searchResult, SearchFieldNames.ATTACHMENT_OWNER_URL_PATH);
        } else if (searchResult.getType().equals("comment")) {
            parentUrl = this.getFieldFromSearchResult(searchResult, SearchFieldNames.PAGE_URL_PATH);
            parentTitle = this.getFieldFromSearchResult(searchResult, SearchFieldNames.PAGE_DISPLAY_TITLE);
        }
        if (parentTitle != null && parentUrl != null) {
            return ContainerSummary.builder().title(parentTitle).displayUrl(parentUrl).build();
        }
        return null;
    }

    private Map<SearchResult, Object> buildResultEntities(Iterable<SearchResult> searchResults, Expansion ... expansions) {
        Expansions parsedExpansions = new Expansions(expansions);
        Map<SearchResult, Content> contentResults = this.contentSearchResultsFactory.buildFrom(this.filterForFactory(searchResults, this.contentSearchResultsFactory), parsedExpansions.getSubExpansions("content"));
        Map<SearchResult, com.atlassian.confluence.api.model.content.Space> spaceResults = this.spaceSearchResultsFactory.buildFrom(this.filterForFactory(searchResults, this.spaceSearchResultsFactory), parsedExpansions.getSubExpansions("space"));
        Map<SearchResult, User> userResults = this.userSearchResultsFactory.buildFrom(this.filterForFactory(searchResults, this.userSearchResultsFactory), parsedExpansions.getSubExpansions("user"));
        return CompositeMap.of((Map)CompositeMap.of(contentResults, spaceResults), userResults);
    }

    private Iterable<SearchResult> filterForFactory(Iterable<SearchResult> searchResults, ModelResultFactory resultFactory) {
        return StreamSupport.stream(searchResults.spliterator(), false).filter(input -> resultFactory.handles(ContentTypeEnum.getByRepresentation((String)input.getType()))).collect(Collectors.toList());
    }

    @HtmlSafe
    private String getExcerpt(SearchResult result, SearchOptions options) {
        switch (options.getExcerptStrategy().serialise()) {
            case "none": {
                return "";
            }
            case "highlight": 
            case "highlight_unescaped": {
                return result.getResultExcerptWithHighlights().trim();
            }
            case "indexed": {
                String excerpt = result.getResultExcerpt();
                return HtmlUtil.htmlEncode((String)excerpt.substring(0, Math.min(50, excerpt.length())));
            }
            case "indexed_unescaped": {
                String excerpt1 = result.getResultExcerpt();
                return excerpt1.substring(0, Math.min(50, excerpt1.length()));
            }
        }
        throw new IllegalArgumentException("Unknown excerpt strategy :" + options.getExcerptStrategy());
    }

    private String getFieldFromSearchResult(SearchResult searchResult, String field) {
        Preconditions.checkArgument((boolean)this.getRequiredIndexFields().contains((Object)field), (Object)("Indexed field was not specified as required : " + field));
        return searchResult.getField(field);
    }

    private boolean shouldHighlightResult(SearchOptions options) {
        return options.getExcerptStrategy().equals((Object)SearchOptions.Excerpt.HIGHLIGHT) || options.getExcerptStrategy().equals((Object)SearchOptions.Excerpt.HIGHLIGHT_UNESCAPED);
    }
}

