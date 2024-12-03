/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService
 *  com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager
 *  com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent$ContentStatus
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.tasklist.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.ao.dao.TooManySidsException;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskSortParameter;
import com.atlassian.confluence.plugins.tasklist.search.SortColumn;
import com.atlassian.confluence.plugins.tasklist.search.SortOrder;
import com.atlassian.confluence.plugins.tasklist.service.NonCheckingPermissionsTaskPaginationService;
import com.atlassian.confluence.plugins.tasklist.service.SpaceAndPageFilter;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AOInlineTasksWithFastPermissionsDao {
    private static final Logger log = LoggerFactory.getLogger(AOInlineTasksWithFastPermissionsDao.class);
    protected static final int INITIAL_CONDITION_LIMIT = Integer.getInteger("confluence.task-report-macro.initial-query-limit", 1000);
    private static final int MAX_LIMIT = Integer.getInteger("confluence.task-report-macro.max-limit", 5000);
    final ActiveObjects ao;
    final BulkPermissionService bulkPermissionService;
    final DarkFeaturesManager darkFeaturesManager;
    final DenormalisedPermissionStateManager denormalisedPermissionStateManager;

    @Autowired
    public AOInlineTasksWithFastPermissionsDao(@ConfluenceImport ActiveObjects ao, @ConfluenceImport BulkPermissionService bulkPermissionService, @ConfluenceImport DarkFeaturesManager darkFeaturesManager, @ConfluenceImport DenormalisedPermissionStateManager denormalisedPermissionStateManager) {
        this.ao = ao;
        this.bulkPermissionService = bulkPermissionService;
        this.darkFeaturesManager = darkFeaturesManager;
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
    }

    public PageResponse<Task> searchTaskViaFastPermissions(@Nonnull SearchTaskParameters params, NonCheckingPermissionsTaskPaginationService taskPaginationService, PageRequest pageRequest, SpaceAndPageFilter spaceAndPageFilter, Set<Long> sidIds, boolean permissionsExempt) throws TooManySidsException {
        int inClauseLimit;
        ArrayList<Object> sqlQueryParams = new ArrayList<Object>();
        if (spaceAndPageFilter.hasAnyConfiguredSpacesOrPages() && spaceAndPageFilter.getDirectlyConfiguredSpaceIds().isEmpty() && spaceAndPageFilter.getDirectlyConfiguredPageIds().isEmpty()) {
            return taskPaginationService.filter(Collections.emptyList(), pageRequest, AuthenticatedUserThreadLocal.get());
        }
        if (!permissionsExempt && (inClauseLimit = this.getInClauseLimit()) > 0 && inClauseLimit < sidIds.size()) {
            throw new TooManySidsException("Current user has too many groups (" + sidIds.size() + ") having content / space permissions. Fast permissions do not support more than " + inClauseLimit + " groups (for current database). The request will be processed without using fast permissions.");
        }
        String contentPermissionsJoin = this.calculateContentPermissionsJoin(sidIds, permissionsExempt);
        String spacePermissionsCondition = this.calculateSpacePermissionsCondition(sidIds, spaceAndPageFilter, permissionsExempt);
        SortOrder sortOrder = params.getSortParameters().getSortOrder() != null ? params.getSortParameters().getSortOrder() : SortOrder.ASCENDING;
        int queryLimit = Math.min(pageRequest.getStart() + pageRequest.getLimit(), MAX_LIMIT);
        String fullSqlQuery = this.buildNonRecursiveCoreSubQuery(params, sqlQueryParams, contentPermissionsJoin, spacePermissionsCondition) + this.buildRecursiveQueryReachingAllVisibleAncestors(spaceAndPageFilter, contentPermissionsJoin) + this.buildDistinctQuery() + this.buildFinalSubquery(spaceAndPageFilter, sortOrder, queryLimit);
        String postProcessedFullSqlQuery = this.postProcessQuery(fullSqlQuery);
        log.debug("Task report query (via fast permissions):\n{}\n-- Sql query end ---", (Object)postProcessedFullSqlQuery);
        List<AOInlineTask> aoInlineTasks = StringUtils.isNotBlank((CharSequence)postProcessedFullSqlQuery) ? this.findWithSQL(postProcessedFullSqlQuery, sqlQueryParams.toArray()) : Collections.emptyList();
        return taskPaginationService.filter(aoInlineTasks, pageRequest, AuthenticatedUserThreadLocal.get());
    }

    protected int getInClauseLimit() {
        return 0;
    }

    private String buildDistinctQuery() {
        return this.buildWithStatement("distinct_query", "real_task, \"GLOBAL_ID\", \"DUE_DATE\", \"BODY\", parent_id, content_id, space_id, sort_column") + " AS (\n    SELECT DISTINCT * FROM core_query\n),\n\n";
    }

    protected abstract String buildWithStatement(String var1, String var2);

    protected abstract String castFieldToText(String var1);

    protected String postProcessQuery(String sqlQuery) {
        return sqlQuery;
    }

    protected String castNumberToBigIntStatement(long number) {
        return String.valueOf(number);
    }

    protected abstract String buildLimitStatement(int var1);

    private String buildFinalSubquery(SpaceAndPageFilter spaceAndPageFilter, SortOrder sortOrder, int limit) {
        String sortColumnTemplate = "case when sort_column is null then 0 else 1 end %s, sort_column %s\n";
        String finalOrderByStatement = String.format("case when sort_column is null then 0 else 1 end %s, sort_column %s\n", new Object[]{sortOrder, sortOrder});
        String finalOrderColumn = String.format("case when sort_column is null then 0 else 1 end %s, sort_column %s\n", "", "");
        return this.buildWithStatement("final_query", "real_task, \"GLOBAL_ID\", \"DUE_DATE\", \"BODY\", parent_id, content_id, space_id, sort_column") + " AS (\n    " + this.getStartingNonRecursiveStatementForFinalQuery(spaceAndPageFilter) + "    " + this.getUnionStatement() + "\n    SELECT distinct_query.real_task, distinct_query.\"GLOBAL_ID\", distinct_query.\"DUE_DATE\", distinct_query.\"BODY\", distinct_query.parent_id, distinct_query.content_id, distinct_query.space_id, distinct_query.sort_column \n        FROM distinct_query\n            JOIN final_query ON distinct_query.parent_id = final_query.content_id AND final_query.real_task = 0\n)\n\nSELECT DISTINCT real_task, \"GLOBAL_ID\", \"DUE_DATE\", \"BODY\", parent_id, content_id, space_id, " + finalOrderColumn + " FROM final_query\n    WHERE real_task = 1\n    ORDER BY " + finalOrderByStatement + "\n    " + this.buildLimitStatement(limit) + "\n";
    }

    private String buildRecursiveQueryReachingAllVisibleAncestors(SpaceAndPageFilter spaceAndPageFilter, String contentPermissionsJoin) {
        StringBuilder reachingParentsCondition = new StringBuilder("WHERE (core_query.parent_id is not null");
        if (!spaceAndPageFilter.getDirectlyConfiguredPageIds().isEmpty()) {
            reachingParentsCondition.append(" OR core_query.parent_id IN (");
            reachingParentsCondition.append(this.toStringSequence(spaceAndPageFilter.getDirectlyConfiguredPageIds()));
            reachingParentsCondition.append(")");
        }
        reachingParentsCondition.append(")\n");
        return this.buildWithStatement("core_query", "real_task, \"GLOBAL_ID\", \"DUE_DATE\", \"BODY\", parent_id, content_id, space_id, sort_column") + " AS (\n    SELECT * FROM core_non_recursive_subquery\n    " + this.getUnionStatement() + "\n    SELECT 0 as real_task, " + this.castNumberToBigIntStatement(0L) + " as \"GLOBAL_ID\", \"DUE_DATE\", " + this.castFieldToText("''") + ", content.parent_id, content.ID as content_id, content.space_id, sort_column\n        FROM core_query\n            JOIN DENORMALISED_CONTENT content ON content.ID = core_query.parent_id\n            " + contentPermissionsJoin + "\n            " + reachingParentsCondition.toString() + "),\n\n";
    }

    private String buildNonRecursiveCoreSubQuery(SearchTaskParameters params, List<Object> sqlQueryParams, String contentPermissionsJoin, String spacePermissionsCondition) {
        String[] allQueryConditions = new String[]{this.calculateStatusCondition(params.getStatus(), sqlQueryParams), spacePermissionsCondition, this.getStartDateRangeCondition(params.getStartCreatedDate(), sqlQueryParams), this.getEndDateRangeCondition(params.getEndCreatedDate(), sqlQueryParams), this.getContentLabelCondition(params.getLabelIds()), this.getAssigneeCondition(params.getAssigneeUserKeys()), this.getCreatedByCondition(params.getCreatorUserKeys())};
        CharSequence[] fieldsToReturn = new String[]{"1 as real_task", "task.\"GLOBAL_ID\"", "task.\"DUE_DATE\"", this.castFieldToText("task.\"BODY\""), "content.parent_id", "content.ID as content_id", "content.space_id", this.getSortingFieldName(params.getSortParameters().getSortColumn()) + " as sort_column"};
        return "WITH " + this.addRecursiveKeywordIsApplicable() + this.buildWithStatement("core_non_recursive_subquery", "real_task, \"GLOBAL_ID\", \"DUE_DATE\", \"BODY\", parent_id, content_id, space_id, sort_column") + " AS (\n    SELECT " + String.join((CharSequence)", ", fieldsToReturn) + "\n    FROM \"AO_BAF3AA_AOINLINE_TASK\" task\n    JOIN DENORMALISED_CONTENT content on task.\"CONTENT_ID\" = content.ID AND content.status = " + SimpleContent.ContentStatus.CURRENT.ordinal() + "\n        " + contentPermissionsJoin + "        " + this.getContentLabelJoinStatement(params.getLabelIds()) + "        " + this.getUserMappingJoinStatement(params.getSortParameters()) + "    WHERE \n        " + Arrays.stream(allQueryConditions).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining("\nAND ")) + "    " + this.getOrderBy(params.getSortParameters().getSortColumn(), params.getSortParameters().getSortOrder()) + "    " + this.buildLimitStatement(INITIAL_CONDITION_LIMIT) + "\n),\n\n";
    }

    protected abstract String addRecursiveKeywordIsApplicable();

    protected String getUserMappingJoinStatement(SearchTaskSortParameter searchTaskSortParameter) {
        return searchTaskSortParameter != null && SortColumn.ASSIGNEE == searchTaskSortParameter.getSortColumn() ? "LEFT JOIN user_mapping um on task.\"ASSIGNEE_USER_KEY\" = um.user_key LEFT JOIN cwd_user cu on um.username = cu.user_name\n" : "";
    }

    protected String getContentLabelJoinStatement(List<Long> labelIds) {
        return ObjectUtils.isNotEmpty(labelIds) ? "LEFT JOIN CONTENT_LABEL cl on content.ID = cl.CONTENTID LEFT JOIN LABEL l on cl.LABELID = l.LABELID\n" : "";
    }

    protected String getAssigneeCondition(List<String> assigneeUserKeys) {
        if (ObjectUtils.isNotEmpty(assigneeUserKeys)) {
            String userKeyStringSequence = assigneeUserKeys.stream().map(v -> "'" + v + "'").collect(Collectors.joining(","));
            return "task.\"ASSIGNEE_USER_KEY\" in (" + userKeyStringSequence + ")";
        }
        return "";
    }

    protected String getCreatedByCondition(List<String> creatorUserKeys) {
        if (ObjectUtils.isNotEmpty(creatorUserKeys)) {
            String userKeyStringSequence = creatorUserKeys.stream().map(v -> "'" + v + "'").collect(Collectors.joining(","));
            return "task.\"CREATOR_USER_KEY\" in (" + userKeyStringSequence + ")";
        }
        return "";
    }

    protected String getContentLabelCondition(List<Long> labelIds) {
        if (labelIds.isEmpty()) {
            return "";
        }
        String labelIdStringSequence = this.toStringSequence(labelIds);
        return "l.LABELID in (" + labelIdStringSequence + ")";
    }

    protected String getStartDateRangeCondition(Date startCreateDate, List<Object> queryParams) {
        if (startCreateDate != null) {
            queryParams.add(startCreateDate);
            return "task.\"CREATE_DATE\" >= ?";
        }
        return "";
    }

    protected String getEndDateRangeCondition(Date endCreateDate, List<Object> queryParams) {
        if (endCreateDate != null) {
            queryParams.add(endCreateDate);
            return "task.\"CREATE_DATE\" < ?";
        }
        return "";
    }

    protected String getUnionStatement() {
        return "UNION";
    }

    private String getStartingNonRecursiveStatementForFinalQuery(SpaceAndPageFilter spaceAndPageFilter) {
        String selectStatement = "SELECT * FROM core_query ";
        if (spaceAndPageFilter.getDirectlyConfiguredPageIds().isEmpty()) {
            return "SELECT * FROM core_query WHERE parent_id is null\n";
        }
        if (spaceAndPageFilter.getDirectlyConfiguredSpaceIds().isEmpty()) {
            return "SELECT * FROM core_query WHERE content_id IN ( " + this.toStringSequence(spaceAndPageFilter.getDirectlyConfiguredPageIds()) + ")\n";
        }
        return "SELECT * FROM core_query WHERE (parent_id is null AND space_id IN (" + this.toStringSequence(spaceAndPageFilter.getDirectlyConfiguredSpaceIds()) + ") OR content_id IN (" + this.toStringSequence(spaceAndPageFilter.getDirectlyConfiguredPageIds()) + "))\n";
    }

    private String toStringSequence(Collection<Long> set) {
        return set.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    protected String getOrderBy(SortColumn sortColumn, SortOrder sortOrder) {
        String fieldName = this.getSortingFieldName(sortColumn);
        if (StringUtils.isEmpty((CharSequence)fieldName)) {
            throw new IllegalArgumentException("Sort column in Task Report Macro can't be empty");
        }
        String sortOrderAsString = sortOrder != null ? sortOrder.toString() : SortOrder.ASCENDING.toString();
        return "ORDER BY case when " + fieldName + " is null then 0 else 1 end " + sortOrderAsString + ", " + fieldName + " " + sortOrderAsString + "\n";
    }

    protected String getSortingFieldName(SortColumn sortColumn) {
        switch (sortColumn) {
            case ASSIGNEE: {
                return "cu.lower_display_name";
            }
            case DUE_DATE: {
                return "task.\"DUE_DATE\"";
            }
            case PAGE_TITLE: {
                return "content.title";
            }
        }
        return "\"DUE_DATE\"";
    }

    private String calculateContentPermissionsJoin(Set<Long> sidIds, boolean permissionsExempt) {
        if (permissionsExempt) {
            return "";
        }
        String sidIdSequence = this.toStringSequence(sidIds);
        return "JOIN DENORMALISED_CONTENT_VIEW_PERMISSIONS view_permissions ON view_permissions.CONTENT_ID = content.ID    AND view_permissions.SID_ID in (" + sidIdSequence + ")";
    }

    private String calculateSpacePermissionsCondition(Set<Long> sidIds, SpaceAndPageFilter spaceAndPageFilter, boolean permissionsExempt) {
        if (spaceAndPageFilter.hasAnyConfiguredSpacesOrPages()) {
            HashSet<Long> fullListOfSpaceIds = new HashSet<Long>(spaceAndPageFilter.getDirectlyConfiguredSpaceIds());
            fullListOfSpaceIds.addAll(spaceAndPageFilter.getSpaceIdsForDirectlyConfiguredPages());
            String spaceIdSequence = this.toStringSequence(fullListOfSpaceIds);
            return "content.space_id IN (" + spaceIdSequence + ") \n";
        }
        if (permissionsExempt) {
            return "";
        }
        String sidIdSequence = this.toStringSequence(sidIds);
        return "content.space_id IN (SELECT SPACE_ID from DENORMALISED_SPACE_VIEW_PERMISSIONS space_perm   WHERE space_perm.SID_ID in (" + sidIdSequence + ") and space_perm.SPACE_ID = content.space_id) \n";
    }

    protected String calculateStatusCondition(TaskStatus taskStatus, List<Object> parameters) {
        if (taskStatus == null) {
            return "";
        }
        parameters.add(taskStatus.name());
        return "task.\"TASK_STATUS\" = ?\n";
    }

    private List<AOInlineTask> findWithSQL(String sql, Object ... params) {
        log.debug("Executing AO SQL [{}] with params {}", (Object)sql, Arrays.asList(params));
        Object[] aoInlineTasks = (AOInlineTask[])this.ao.findWithSQL(AOInlineTask.class, "GLOBAL_ID", sql, params);
        return Lists.newArrayList((Object[])aoInlineTasks);
    }
}

