/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.LongRangeQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  com.atlassian.confluence.search.v2.sort.FieldSort
 *  com.atlassian.confluence.search.v2.sort.MultiSearchSort
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskSortParameter;
import com.atlassian.confluence.plugins.tasklist.search.SortOrder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.LongRangeQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import com.atlassian.confluence.search.v2.sort.FieldSort;
import com.atlassian.confluence.search.v2.sort.MultiSearchSort;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SearchTaskParametersToSearchQueryParametersConverter {
    private static final Logger log = LoggerFactory.getLogger(SearchTaskParametersToSearchQueryParametersConverter.class);
    private final LabelManager labelManager;
    private static final SortInfo DEFAULT_SORT_INFO = new SortInfo("createDateMs", SearchSort.Type.LONG);
    private static final SearchSort DEFAULT_SORT = new FieldSort(SearchTaskParametersToSearchQueryParametersConverter.DEFAULT_SORT_INFO.fieldName, SearchTaskParametersToSearchQueryParametersConverter.DEFAULT_SORT_INFO.fieldType, SearchTaskParametersToSearchQueryParametersConverter.getSortOrder(SortOrder.DESCENDING));
    private static final Map<String, Function<SortOrder, SortInfo>> SORT_ORDER_MAPPING = Map.of("create_date", sortOrder -> DEFAULT_SORT_INFO, "due_date", sortOrder -> {
        switch (sortOrder) {
            case ASCENDING: {
                return new SortInfo("dueDateEmptyValueLastMs", SearchSort.Type.LONG);
            }
            case DESCENDING: {
                return new SortInfo("dueDateMs", SearchSort.Type.LONG);
            }
        }
        throw new IllegalArgumentException("Illegal sort order: " + sortOrder);
    }, "assignee", sortOrder -> {
        switch (sortOrder) {
            case ASCENDING: {
                return new SortInfo("assigneeNameEmptyValuesLast", SearchSort.Type.STRING);
            }
            case DESCENDING: {
                return new SortInfo("assigneeName", SearchSort.Type.STRING);
            }
        }
        throw new IllegalArgumentException("Illegal sort order: " + sortOrder);
    }, "page_title", sortOrder -> new SortInfo("pageTitleCI", SearchSort.Type.STRING));

    public SearchTaskParametersToSearchQueryParametersConverter(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    SearchQueryParameters convert(SearchTaskParameters macroParams) {
        SearchQueryParameters searchParams = new SearchQueryParameters();
        BooleanQuery.Builder builder = BooleanQuery.builder();
        this.addTaskStatusFilter(builder, macroParams.getStatus());
        this.addLabelsFilter(searchParams, macroParams.getLabelIds());
        this.addSpacesAndPagesFilter(builder, new HashSet<Long>(macroParams.getSpaceIds()), new HashSet<Long>(macroParams.getPageIds()));
        this.addDateFilter(builder, "createDateMs", macroParams.getStartCreatedDate(), macroParams.getEndCreatedDate());
        this.addDateFilter(builder, "dueDateMs", macroParams.getStartDueDate(), macroParams.getEndDueDate());
        this.addUserFilter(builder, "creatorKey", macroParams.getCreatorUserKeys());
        this.addUserFilter(builder, "assigneeKey", macroParams.getAssigneeUserKeys());
        if (builder.isEmpty()) {
            builder.addFilter((SearchQuery)AllQuery.getInstance());
        }
        searchParams.setSearchQueryFilter(builder.build());
        this.setSortParameters(searchParams, macroParams.getSortParameters());
        return searchParams;
    }

    private void setSortParameters(SearchQueryParameters searchParams, SearchTaskSortParameter sortParameters) {
        if (sortParameters == null) {
            return;
        }
        log.trace("Sort parameters: {}, {}", (Object)sortParameters.getSortColumn(), (Object)sortParameters.getSortOrder());
        SortInfo sortInfo = SORT_ORDER_MAPPING.get(sortParameters.getSortColumn().name().toLowerCase()).apply(sortParameters.getSortOrder());
        if (sortInfo == null || StringUtils.isEmpty((CharSequence)sortInfo.fieldName)) {
            searchParams.setSort(DEFAULT_SORT);
            return;
        }
        FieldSort primarySort = new FieldSort(sortInfo.fieldName, sortInfo.fieldType, SearchTaskParametersToSearchQueryParametersConverter.getSortOrder(sortParameters.getSortOrder()));
        searchParams.setSort((SearchSort)new MultiSearchSort(List.of(primarySort, DEFAULT_SORT)));
    }

    private static SearchSort.Order getSortOrder(SortOrder sortOrder) {
        if (sortOrder == null) {
            return null;
        }
        switch (sortOrder) {
            case ASCENDING: {
                return SearchSort.Order.ASCENDING;
            }
            case DESCENDING: {
                return SearchSort.Order.DESCENDING;
            }
        }
        throw new IllegalArgumentException("Undefined sort order " + sortOrder);
    }

    private void addUserFilter(BooleanQuery.Builder builder, String fieldName, List<String> userKeys) {
        if (userKeys == null || userKeys.isEmpty()) {
            return;
        }
        builder.addMust((Object)new TermSetQuery(fieldName, new HashSet<String>(userKeys)));
    }

    private void addDateFilter(BooleanQuery.Builder builder, String fieldName, Date startDate, Date endDate) {
        if (startDate == null && endDate == null) {
            return;
        }
        long from = startDate != null ? startDate.getTime() : 0L;
        long to = endDate != null ? endDate.getTime() : Long.MAX_VALUE;
        LongRangeQuery dateRangeQuery = new LongRangeQuery(fieldName, Range.range((Object)from, (Object)to, (boolean)true, (boolean)true));
        builder.addMust((Object)dateRangeQuery);
    }

    private void addLabelsFilter(SearchQueryParameters searchParams, Collection<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return;
        }
        Set labels = labelIds.stream().filter(Objects::nonNull).map(arg_0 -> ((LabelManager)this.labelManager).getLabel(arg_0)).filter(Objects::nonNull).map(Label::getName).collect(Collectors.toSet());
        if (!labels.isEmpty()) {
            searchParams.setLabels(labels);
        }
    }

    private SearchQuery getQueryForPages(Set<Long> pageIds) {
        Set pageIdsAsStrings = pageIds.stream().filter(Objects::nonNull).map(p -> Long.toString(p)).collect(Collectors.toSet());
        return new TermSetQuery("ancestorIds", pageIdsAsStrings);
    }

    private SearchQuery getQueryForSpaces(Set<Long> spaceIds) {
        Set spaceIdsAsStrings = spaceIds.stream().filter(Objects::nonNull).map(s -> Long.toString(s)).collect(Collectors.toSet());
        return new TermSetQuery("spaceId", spaceIdsAsStrings);
    }

    private void addSpacesAndPagesFilter(BooleanQuery.Builder mainBuilder, Set<Long> spaceIds, Set<Long> pageIds) {
        BooleanQuery.Builder builder = BooleanQuery.builder();
        if (pageIds != null && !pageIds.isEmpty()) {
            builder.addShould((Object)this.getQueryForPages(pageIds));
        }
        if (spaceIds != null && !spaceIds.isEmpty()) {
            builder.addShould((Object)this.getQueryForSpaces(spaceIds));
        }
        if (!builder.isEmpty()) {
            mainBuilder.addMust((Object)builder.build());
        }
    }

    private void addTaskStatusFilter(BooleanQuery.Builder builder, TaskStatus taskStatus) {
        builder.addMust((Object)new TermQuery("taskStatus", taskStatus.name()));
    }

    private static class SortInfo {
        String fieldName;
        SearchSort.Type fieldType;

        public SortInfo(String fieldName, SearchSort.Type fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }
    }
}

