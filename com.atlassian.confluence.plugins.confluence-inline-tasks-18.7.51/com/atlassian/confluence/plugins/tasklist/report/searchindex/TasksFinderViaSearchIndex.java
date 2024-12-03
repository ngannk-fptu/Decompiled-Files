/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.CustomSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex;

import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.InlineTaskSearchIndexAccessor;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.SearchTaskParametersToSearchQueryParametersConverter;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.CustomSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.query.AllQuery;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TasksFinderViaSearchIndex {
    private static final Logger log = LoggerFactory.getLogger(TasksFinderViaSearchIndex.class);
    private final PredefinedSearchBuilder predefinedSearchBuilder;
    private final InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor;
    private final SearchTaskParametersToSearchQueryParametersConverter converter;

    @Autowired
    public TasksFinderViaSearchIndex(InlineTaskSearchIndexAccessor inlineTaskSearchIndexAccessor, PredefinedSearchBuilder predefinedSearchBuilder, SearchTaskParametersToSearchQueryParametersConverter converter) {
        this.inlineTaskSearchIndexAccessor = inlineTaskSearchIndexAccessor;
        this.predefinedSearchBuilder = predefinedSearchBuilder;
        this.converter = converter;
    }

    public boolean hasAnything() throws InvalidSearchException {
        CustomSearch search = new CustomSearch((SearchQuery)AllQuery.getInstance(), null, 0, 1);
        return this.inlineTaskSearchIndexAccessor.search((ISearch)search, null).size() > 0;
    }

    public List<Task> find(SearchTaskParameters macroParams, int offset, int limit) throws InvalidSearchException {
        SearchQueryParameters searchParams = this.converter.convert(macroParams);
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(searchParams, offset, limit);
        SearchResults searchResults = this.inlineTaskSearchIndexAccessor.search(search, null);
        return searchResults.getAll().stream().map(this::convertToTask).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Task convertToTask(SearchResult searchResult) {
        Task.Builder builder = new Task.Builder();
        String contentId = searchResult.getField("contentId");
        if (StringUtils.isEmpty((CharSequence)contentId)) {
            return null;
        }
        try {
            return builder.withGlobalId(this.convertToLong(searchResult.getField("globalId"), "globalId")).withId(this.convertToLong(searchResult.getField("taskId"), "taskId")).withContentId(this.convertToLong(contentId, "contentId")).withStatus(this.getTaskStatus(searchResult.getField("taskStatus"))).withPageTitle(searchResult.getField("pageTitle")).withBody(searchResult.getField("taskBody")).withCreator(searchResult.getField("creatorKey")).withAssignee(searchResult.getField("assignee")).withAssigneeKey(searchResult.getField("assigneeKey")).withAssigneeName(searchResult.getField("assigneeName")).withDueDate(this.getDateField(searchResult.getField("dueDate"))).build();
        }
        catch (Exception e) {
            log.warn("Unable to build a task from the search results: " + e.getMessage(), (Throwable)e);
            return null;
        }
    }

    private Date getDateField(String dateValue) {
        return !StringUtils.isEmpty((CharSequence)dateValue) ? LuceneUtils.stringToDate((String)dateValue) : null;
    }

    private TaskStatus getTaskStatus(String value) {
        try {
            return value != null ? TaskStatus.valueOf(value) : null;
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    private long convertToLong(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("Value of field " + fieldName + " is null.");
        }
        return Long.parseLong(value);
    }
}

