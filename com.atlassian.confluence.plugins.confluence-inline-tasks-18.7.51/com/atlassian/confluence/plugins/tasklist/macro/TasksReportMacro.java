/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.thumbnail.Dimensions
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.tasklist.macro;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskReportParametersManager;
import com.atlassian.confluence.plugins.tasklist.macro.ColumnNameMapper;
import com.atlassian.confluence.plugins.tasklist.macro.TaskEntity;
import com.atlassian.confluence.plugins.tasklist.macro.TasksReportParameters;
import com.atlassian.confluence.plugins.tasklist.macro.validator.DateValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.DisplayColumnsValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.TasksReportValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.ValidatedErrorType;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.TasksFinderViaSearchIndex;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.plugins.tasklist.service.TaskEntityHelper;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.codehaus.jackson.map.ObjectMapper;

public class TasksReportMacro
implements Macro,
EditorImagePlaceholder {
    private static final String FORCE_USE_SEARCH_GET_PARAMETER_NAME = "use-search";
    private static final String USE_SEARCH_INDEX_FOR_MACRO_FEATURE_NAME = "confluence.task-report-macro.use-search-index";
    public static final String IS_TASK_BEING_RENDERED = "isTaskBeingRendered";
    private static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-inline-tasks:tasks-report-resources";
    private final TemplateRenderer templateRenderer;
    private final TaskEntityHelper taskEntityHelper;
    private final LocaleManager localeManager;
    private final InlineTaskService taskService;
    private final I18NBeanFactory i18NBeanFactory;
    private final TaskReportParametersManager contentRetriever;
    private final DarkFeatureManager darkFeatureManager;
    private final UserAccessor userAccessor;
    private final TasksFinderViaSearchIndex tasksFinderViaSearchIndex;
    private static final String PLACEHOLDER_IMAGE_PATH = "/download/resources/com.atlassian.confluence.plugins.confluence-inline-tasks/tasks-report-image-resources/task-report-placeholder.png";

    @Deprecated
    public TasksReportMacro(TemplateRenderer templateRenderer, TaskEntityHelper taskEntityHelper, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, InlineTaskService taskService, TaskReportParametersManager contentRetriever) {
        this(templateRenderer, taskEntityHelper, localeManager, i18NBeanFactory, taskService, contentRetriever, null, null, null);
    }

    public TasksReportMacro(TemplateRenderer templateRenderer, TaskEntityHelper taskEntityHelper, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, InlineTaskService taskService, TaskReportParametersManager contentRetriever, DarkFeatureManager darkFeatureManager, @ComponentImport UserAccessor userAccessor, TasksFinderViaSearchIndex tasksFinderViaSearchIndex) {
        this.templateRenderer = templateRenderer;
        this.taskEntityHelper = taskEntityHelper;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.taskService = taskService;
        this.contentRetriever = contentRetriever;
        this.darkFeatureManager = darkFeatureManager;
        this.userAccessor = userAccessor;
        this.tasksFinderViaSearchIndex = tasksFinderViaSearchIndex;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        StopWatch stopWatch = StopWatch.createStarted();
        if (Boolean.TRUE.equals(context.getProperty(IS_TASK_BEING_RENDERED))) {
            throw new MacroExecutionException(this.getI18NBean().getText("inline-tasks.macro.tasks-report.inside.task"));
        }
        String renderResult = "";
        HashMap renderMap = Maps.newHashMap();
        DisplayColumnsValidator displayColumnsValidator = new DisplayColumnsValidator("com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.columns.label", parameters.get("columns"));
        DateValidator dateValidator = new DateValidator("com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.createddateFrom.label", parameters.get("createddateFrom"));
        TasksReportValidator validator = new TasksReportValidator();
        validator.addValidators(dateValidator, displayColumnsValidator);
        if (!validator.validate()) {
            if (context.getOutputType().equals("preview")) {
                List messages = Lists.transform(validator.getErrors(), (Function)new Function<ValidatedErrorType, String>(){

                    public String apply(@Nullable ValidatedErrorType error) {
                        return TasksReportMacro.this.getI18NBean().getText("com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report.field.warning.format", new Object[]{TasksReportMacro.this.getI18NBean().getText(error.getFieldNameCode()), TasksReportMacro.this.getI18NBean().getText(error.getMessageCode(), (Object[])error.getParams())});
                    }
                });
                renderMap.put("messages", messages);
                renderResult = this.renderFromSoy(RESOURCE_KEY, "Confluence.InlineTasks.Report.Templates.taskReportBrowserWarning.soy", renderMap);
            } else {
                renderResult = this.renderFromSoy(RESOURCE_KEY, "Confluence.InlineTasks.Report.Templates.taskReportWarning.soy", renderMap);
            }
            return renderResult;
        }
        try {
            TasksReportParameters reportParameters = new TasksReportParameters(parameters);
            SearchTaskParameters searchParams = this.contentRetriever.convertToSearchTaskParameters(reportParameters);
            PageResponse<Task> pageResponse = this.taskService.searchTasks(searchParams);
            List allTasks = pageResponse.getResults();
            int endTaskIndex = Math.min(searchParams.getPageSize(), allTasks.size());
            List<Task> tasks = allTasks.subList(0, endTaskIndex);
            if (tasks.size() == 0) {
                return this.renderEmptyResult(parameters.get("status"));
            }
            if (searchParams != null) {
                renderMap.put("pageSize", searchParams.getPageSize());
                renderMap.put("totalPages", searchParams.getTotalPages());
                renderMap.put("pageLimit", searchParams.getDisplayedPages());
            }
            renderMap.put("adaptive", pageResponse.hasMore());
            renderMap.put("headings", reportParameters.getColumns());
            renderMap.put("headingTexts", this.getHeadingTexts());
            List<TaskEntity> entityTasks = this.taskEntityHelper.createTaskEntities(tasks);
            renderMap.put("tasks", entityTasks);
            renderMap.put("reportParameters", new ObjectMapper().writeValueAsString((Object)reportParameters));
            this.addDebugInformationIfRequired(renderMap, stopWatch);
            renderResult = this.renderFromSoy(RESOURCE_KEY, "Confluence.InlineTasks.Report.Templates.tasksReport.soy", renderMap);
        }
        catch (IllegalArgumentException e) {
            renderResult = RenderUtils.blockError((String)e.getMessage(), (String)"");
        }
        catch (IOException e) {
            renderResult = RenderUtils.blockError((String)e.getMessage(), (String)"");
        }
        return renderResult;
    }

    private void addDebugInformationIfRequired(Map<String, Object> renderMap, StopWatch stopWatch) {
        if (!this.shouldPrintDebugInformation()) {
            return;
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        boolean confluenceAdministrator = currentUser != null && this.userAccessor.hasMembership("confluence-administrators", currentUser.getName());
        renderMap.put("confluenceAdministrator", confluenceAdministrator);
        renderMap.put("printDebugInformation", true);
        renderMap.put("retrievingMechanism", "NOT IMPLEMENTED");
        renderMap.put("duration", stopWatch.toString());
    }

    public boolean shouldPrintDebugInformation() {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request == null) {
            return false;
        }
        return StringUtils.isNotEmpty((CharSequence)request.getParameter("debug")) || StringUtils.isNotEmpty((CharSequence)request.getParameter(FORCE_USE_SEARCH_GET_PARAMETER_NAME));
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext context) {
        return new DefaultImagePlaceholder(PLACEHOLDER_IMAGE_PATH, new Dimensions(310, 172), true);
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    private String renderFromSoy(String pluginKey, String soyTemplate, Map<String, Object> soyContext) {
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, pluginKey, soyTemplate, soyContext);
        return output.toString();
    }

    private Map<String, String> getHeadingTexts() {
        HashMap texts = Maps.newHashMap();
        for (String column : ColumnNameMapper.COLUMNS) {
            texts.put(column, this.getI18NBean().getText("inline-tasks.macro.tasks-report.heading." + column));
        }
        return texts;
    }

    String renderEmptyResult(String paramStatus) {
        HashMap renderMap = Maps.newHashMap();
        String reportBlankKey = "complete".equals(paramStatus) ? "task.report.blank.nocompletetask" : "task.report.blank.noincompletetask";
        renderMap.put("blankTitle", this.getI18NBean().getText("task.report.blank.title"));
        renderMap.put("blankDescription", this.getI18NBean().getText(reportBlankKey));
        renderMap.put("customClass", "tasks-report-blank");
        return this.renderFromSoy(RESOURCE_KEY, "Confluence.UI.Components.BlankPlaceholderBox.Templates.blankBox.soy", renderMap);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }
}

