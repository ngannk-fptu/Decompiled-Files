/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.search.contentnames.QueryToken
 *  com.atlassian.confluence.search.contentnames.QueryToken$Type
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.PrefixQuery
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 *  org.apache.lucene.queryparser.flexible.standard.QueryParserUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.search.impl;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.model.LightweightPersistentSubCalendar;
import com.atlassian.confluence.extra.calendar3.search.CalendarSearcher;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.confluence.search.v2.query.PrefixQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCalendarSearcher
implements CalendarSearcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCalendarSearcher.class);
    private static final SearchQuery CALENDAR_CONTENT_TYPE_SEARCH_QUERY = new CustomContentTypeQuery(new String[]{"com.atlassian.confluence.extra.team-calendars:calendar-content-type"});
    private final SearchManager searchManager;
    private final CalendarPermissionManager calendarPermissionManager;

    @Autowired
    public DefaultCalendarSearcher(@ComponentImport SearchManager searchManager, CalendarPermissionManager calendarPermissionManager) {
        this.searchManager = searchManager;
        this.calendarPermissionManager = calendarPermissionManager;
    }

    @Override
    public Set<String> findSubCalendars(ConfluenceUser user, String term, int startIndex, int pageSize) throws InvalidSearchException {
        String finalTerm = QueryParserUtil.escape((String)term);
        return this.findSubCalendars(finalTerm, startIndex, pageSize).stream().filter(searchResult -> {
            if (StringUtils.isEmpty(searchResult)) {
                return false;
            }
            LightweightPersistentSubCalendar subCalendarToCheck = new LightweightPersistentSubCalendar((String)searchResult);
            return this.calendarPermissionManager.hasViewEventPrivilege(subCalendarToCheck, user);
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<String> findSubCalendars(String term, int startIndex, int pageSize) throws InvalidSearchException {
        return new HashSet<String>(this.findSubCalendars((ISearch)new ContentSearch(CALENDAR_CONTENT_TYPE_SEARCH_QUERY, (SearchSort)new TitleSort(SearchSort.Order.DESCENDING), startIndex * pageSize, pageSize), searchQuery -> BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{searchQuery, BooleanQuery.orQuery((SearchQuery[])new SearchQuery[]{new PrefixQuery("title", term), new TextFieldQuery("title", term, BooleanOperator.OR)})}), (Searchable searchable) -> ((CustomContentEntityObject)searchable).getProperties().getStringProperty("subCalendarId")));
    }

    @Override
    public <T> Collection<T> findSubCalendars(ConfluenceUser user, List<QueryToken> queryTokens, int startIndex, int pageSize, Function<Searchable, T> transformer) throws InvalidSearchException {
        Collection returnCollection = this.findSubCalendars(queryTokens, Integer.MAX_VALUE, (Searchable searchable) -> {
            CustomContentEntityObject calendarContentType = (CustomContentEntityObject)searchable;
            String subCalendarId = calendarContentType.getProperties().getStringProperty("subCalendarId");
            if (!this.calendarPermissionManager.hasViewEventPrivilege(new LightweightPersistentSubCalendar(subCalendarId), user)) {
                return null;
            }
            return searchable;
        }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        Collection<T> limitedCollection = this.limit(returnCollection, startIndex, pageSize);
        return limitedCollection.stream().map(transformer).collect(Collectors.toList());
    }

    @Override
    public <T> Collection<T> findSubCalendars(List<QueryToken> queryTokens, int limit, Function<Searchable, T> transformer) throws InvalidSearchException {
        return this.findSubCalendars((ISearch)new ContentSearch(CALENDAR_CONTENT_TYPE_SEARCH_QUERY, (SearchSort)new TitleSort(SearchSort.Order.DESCENDING), 0, limit), searchQuery -> {
            ArrayList<Object> termQueries = new ArrayList<Object>(queryTokens.size());
            for (QueryToken queryToken : queryTokens) {
                if (queryToken.getType() == QueryToken.Type.PARTIAL) {
                    termQueries.add(new PrefixQuery(SearchFieldNames.TITLE, queryToken.getText()));
                    continue;
                }
                termQueries.add(new TextFieldQuery(SearchFieldNames.TITLE, queryToken.getText(), BooleanOperator.OR));
            }
            return BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{searchQuery, BooleanQuery.orQuery((SearchQuery[])termQueries.toArray(new SearchQuery[0]))});
        }, transformer);
    }

    <T> Collection<T> findSubCalendars(ISearch searchOption, Function<SearchQuery, SearchQuery> queryBuilder, Function<Searchable, T> transformer) throws InvalidSearchException {
        SearchQuery searchQuery = queryBuilder.apply(searchOption.getQuery());
        ContentSearch search = new ContentSearch(searchQuery, searchOption.getSort(), searchOption.getStartOffset(), searchOption.getLimit());
        SearchResults searchResults = this.searchManager.search((ISearch)search);
        List resultEntities = this.searchManager.convertToEntities(searchResults, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        Collection returnCollection = resultEntities.stream().map(transformer).filter(Objects::nonNull).collect(Collectors.toList());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("===========Search results===========");
            for (Object result : returnCollection) {
                LOGGER.debug("search result : {}", (Object)result.toString());
            }
            LOGGER.debug("===========Search results===========");
        }
        return returnCollection;
    }

    private <T> Collection<T> limit(Collection<T> sourceCollection, int fromIndex, int maxItems) {
        ArrayList sourceList = Lists.newArrayList(sourceCollection);
        int size = sourceList.size();
        fromIndex = fromIndex > size ? 0 : fromIndex;
        int toIndex = fromIndex + maxItems;
        toIndex = Math.min(toIndex, size);
        return sourceList.subList(fromIndex, toIndex);
    }
}

