/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService
 *  com.atlassian.confluence.web.filter.CachingHeaders
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.util.concurrent.LazyReference
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response$Status
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskReportParametersManager;
import com.atlassian.confluence.plugins.tasklist.macro.TaskEntity;
import com.atlassian.confluence.plugins.tasklist.macro.TasksDetailPaginated;
import com.atlassian.confluence.plugins.tasklist.macro.TasksReportParameters;
import com.atlassian.confluence.plugins.tasklist.macro.validator.DateValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.DisplayColumnsValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.TasksReportValidator;
import com.atlassian.confluence.plugins.tasklist.rest.ResourceErrorType;
import com.atlassian.confluence.plugins.tasklist.rest.ResourceException;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.plugins.tasklist.service.TaskEntityHelper;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;

@Path(value="/")
@Consumes(value={"application/json"})
public class TasksReportResource {
    static final DateTime TASK_REPORT_RELEASE_DATE = new DateTime(2014, 4, 22, 0, 0);
    private final InlineTaskService taskService;
    private final TaskEntityHelper taskEntityHelper;
    private final TaskReportParametersManager contentRetriever;
    private final LazyReference<DateTime> defaultStartCreatedDate;

    public TasksReportResource(InlineTaskService taskService, TaskEntityHelper taskEntityHelper, TaskReportParametersManager contentRetriever, final FeatureDiscoveryService featureDiscoveryService) {
        this.taskService = taskService;
        this.taskEntityHelper = taskEntityHelper;
        this.contentRetriever = contentRetriever;
        this.defaultStartCreatedDate = new LazyReference<DateTime>(){

            protected DateTime create() throws Exception {
                Date taskReportMacroInstallDate = featureDiscoveryService.getFeatureInstallationDate("com.atlassian.confluence.plugins.confluence-inline-tasks", "tasks-report");
                DateTime installDate = taskReportMacroInstallDate != null ? new DateTime(taskReportMacroInstallDate.getTime()) : TASK_REPORT_RELEASE_DATE;
                return installDate.minusMonths(6);
            }
        };
    }

    @Path(value="/task-report")
    @Produces(value={"application/json"})
    @GET
    @AnonymousAllowed
    public TasksDetailPaginated taskReportMacro(@QueryParam(value="pageIndex") int pageIndex, @QueryParam(value="pageSize") int pageSize, @QueryParam(value="reportParameters") TasksReportParameters reportParameters, @Context HttpServletResponse response) {
        this.validatePagination(pageIndex, pageSize);
        this.validateReportParameters(reportParameters);
        this.applyNoCacheHeaders(response);
        return this.getTaskReport(pageIndex, pageSize, this.contentRetriever.convertToSearchTaskParameters(reportParameters));
    }

    @Path(value="/my-task-report")
    @Produces(value={"application/json"})
    @GET
    public TasksDetailPaginated myTasks(@QueryParam(value="pageIndex") int pageIndex, @QueryParam(value="pageSize") int pageSize, @QueryParam(value="reportParameters") TasksReportParameters reportParameters, @Context HttpServletResponse response) {
        this.validatePagination(pageIndex, pageSize);
        this.validateReportParameters(reportParameters);
        this.applyNoCacheHeaders(response);
        SearchTaskParameters searchParams = this.contentRetriever.convertToSearchTaskParameters(reportParameters);
        if (searchParams.getStartCreatedDate() == null && searchParams.getEndCreatedDate() == null) {
            searchParams.setStartCreatedDate(((DateTime)this.defaultStartCreatedDate.get()).toDate());
        }
        return this.getTaskReport(pageIndex, pageSize, searchParams);
    }

    private TasksDetailPaginated getTaskReport(int pageIndex, int pageSize, SearchTaskParameters searchParams) {
        searchParams.setPageIndex(pageIndex);
        searchParams.setPageSize(pageSize);
        PageResponse<Task> pageResponse = this.taskService.searchTasks(searchParams);
        List allTasks = pageResponse.getResults();
        int endTaskIndex = Math.min(pageSize, allTasks.size());
        List<Task> tasks = allTasks.subList(0, endTaskIndex);
        List<TaskEntity> entityTasks = this.taskEntityHelper.createTaskEntities(tasks);
        TasksDetailPaginated result = new TasksDetailPaginated();
        result.setDetailLines(entityTasks);
        result.setCurrentPage(pageIndex);
        result.setTotalPages(searchParams.getTotalPages());
        result.setAdaptive(pageResponse.hasMore());
        return result;
    }

    private void validateReportParameters(TasksReportParameters reportParams) {
        DisplayColumnsValidator displayColumnsValidator = new DisplayColumnsValidator("com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.columns.label", reportParams.getColumns());
        DateValidator dateValidator = new DateValidator("com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.createddateFrom.label", reportParams.getCreatedateFrom());
        TasksReportValidator validator = new TasksReportValidator();
        validator.addValidators(dateValidator, displayColumnsValidator);
        if (!validator.validate()) {
            throw new ResourceException("Validation is failed!", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_INVALID, validator.getErrors());
        }
    }

    private void validatePagination(int pageIndex, int pageSize) {
        if (pageSize <= 0) {
            throw new ResourceException("Requested page size is not valid", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_INVALID, (Object)pageSize);
        }
        if (pageIndex < 0) {
            throw new ResourceException("Requested page index is not valid", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_INVALID, (Object)pageIndex);
        }
    }

    private void applyNoCacheHeaders(HttpServletResponse response) {
        CachingHeaders.PREVENT_CACHING.apply(response);
    }
}

