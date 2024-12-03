/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.macro.TasksReportParameters;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskSortParameter;
import com.atlassian.confluence.plugins.tasklist.search.SortColumn;
import com.atlassian.confluence.plugins.tasklist.search.SortOrder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskReportParametersManager {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern((String)"dd-MM-yyyy");
    private final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private final LabelManager labelManager;
    private final TimeZoneManager timeZoneManager;
    private final FormatSettingsManager formatSettingsManager;
    public static final int NUM_DISPLAYED_PAGES = 7;
    private final Function<String, String> USERNAME_TO_USERKEY_FUNCTION = new Function<String, String>(){

        public String apply(@Nullable String username) {
            ConfluenceUser assignee = TaskReportParametersManager.this.userAccessor.getUserByName(username);
            if (assignee != null) {
                return assignee.getKey().getStringValue();
            }
            return "0";
        }
    };

    @Autowired
    public TaskReportParametersManager(UserAccessor userAccessor, SpaceManager spaceManager, LabelManager labelManager, TimeZoneManager timeZoneManager, FormatSettingsManager formatSettingsManager) {
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
        this.timeZoneManager = timeZoneManager;
        this.formatSettingsManager = formatSettingsManager;
    }

    public SearchTaskParameters convertToSearchTaskParameters(TasksReportParameters reportParams) {
        List<String> pages;
        List<String> labels;
        SearchTaskParameters params = new SearchTaskParameters();
        params.setPageSize(reportParams.getPageSize());
        List<String> assigneeKeys = this.toUserKeys(reportParams.getAssignees());
        params.setAssigneeUserKeys(assigneeKeys);
        List<String> creatorKeys = this.toUserKeys(reportParams.getCreators());
        params.setCreatorUserKeys(creatorKeys);
        List<String> spaceKeys = reportParams.getSpaces();
        if (CollectionUtils.isNotEmpty(spaceKeys)) {
            ArrayList spaceIds = Lists.newArrayList((Iterable)Iterables.transform((Iterable)Iterables.filter(spaceKeys, (Predicate)Predicates.notNull()), (Function)new Function<String, Long>(){

                public Long apply(@Nullable String spaceKey) {
                    Space space = TaskReportParametersManager.this.spaceManager.getSpace(spaceKey);
                    if (space != null) {
                        return space.getId();
                    }
                    return 0L;
                }
            }));
            params.setSpaceIds(spaceIds);
        }
        if (CollectionUtils.isNotEmpty(labels = reportParams.getLabels())) {
            ArrayList labelIds = Lists.newArrayList((Iterable)Iterables.transform((Iterable)Iterables.filter(labels, (Predicate)Predicates.notNull()), (Function)new Function<String, Long>(){

                public Long apply(@Nullable String labelName) {
                    Label label = TaskReportParametersManager.this.labelManager.getLabel(new Label(labelName));
                    if (label != null) {
                        return label.getId();
                    }
                    return 0L;
                }
            }));
            params.setLabelIds(labelIds);
        }
        if (CollectionUtils.isNotEmpty(pages = reportParams.getPages())) {
            ArrayList pageIds = Lists.newArrayList((Iterable)Iterables.transform((Iterable)Iterables.filter(pages, (Predicate)Predicates.notNull()), (Function)new Function<String, Long>(){

                public Long apply(@Nullable String page) {
                    return Long.parseLong(page);
                }
            }));
            params.setPageIds(pageIds);
        }
        params.setStatus(this.getTaskStatus(reportParams));
        this.createDueDateRange(params, reportParams);
        this.createCreatedDateRange(params, reportParams);
        params.setDisplayedPages(7);
        params.setSortParameters(this.toSortParameters(reportParams.getSortColumn(), reportParams.isReverseSort()));
        return params;
    }

    private SearchTaskSortParameter toSortParameters(String sortColumn, boolean isReverseSort) {
        SortColumn column = SortColumn.DUE_DATE;
        if (StringUtils.isNotBlank((CharSequence)sortColumn)) {
            for (SortColumn c : SortColumn.values()) {
                if (!c.toString().equals(sortColumn)) continue;
                column = c;
                break;
            }
        }
        return new SearchTaskSortParameter(column, isReverseSort ? SortOrder.DESCENDING : SortOrder.ASCENDING);
    }

    private List<String> toUserKeys(List<String> usernames) {
        return Lists.newArrayList((Iterable)Iterables.transform((Iterable)Iterables.filter(usernames, (Predicate)Predicates.notNull()), this.USERNAME_TO_USERKEY_FUNCTION));
    }

    private TaskStatus getTaskStatus(TasksReportParameters params) {
        String status = params.getStatus();
        if (TaskStatus.CHECKED.getDisplayedText().equals(status)) {
            return TaskStatus.CHECKED;
        }
        return TaskStatus.UNCHECKED;
    }

    private void createDueDateRange(SearchTaskParameters params, TasksReportParameters reportParams) {
        String dueDateFrom = reportParams.getDuedateFrom();
        String dueDateTo = reportParams.getDuedateTo();
        if (StringUtils.isNotBlank((CharSequence)dueDateFrom)) {
            params.setStartDueDate(this.getDate(dueDateFrom));
        }
        if (StringUtils.isNotBlank((CharSequence)dueDateTo)) {
            params.setEndDueDate(this.getDate(dueDateTo));
        }
    }

    private void createCreatedDateRange(SearchTaskParameters params, TasksReportParameters reportParams) {
        String createDateFrom = reportParams.getCreatedateFrom();
        String createDateTo = reportParams.getCreatedateTo();
        if (StringUtils.isNotBlank((CharSequence)createDateFrom)) {
            params.setStartCreatedDate(this.getDate(createDateFrom));
        }
        if (StringUtils.isNotBlank((CharSequence)createDateTo)) {
            params.setEndCreatedDate(this.getDate(createDateTo));
        }
    }

    private Date getDate(String dateStr) {
        return DATE_FORMAT.parseDateTime(dateStr).toDate();
    }
}

