/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Maybe
 *  javax.activation.DataSource
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.macro.TaskEntity;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskSortParameter;
import com.atlassian.confluence.plugins.tasklist.search.SortColumn;
import com.atlassian.confluence.plugins.tasklist.search.SortOrder;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.plugins.tasklist.service.TaskEntityHelper;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Maybe;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecommendedEmailTaskRenderer {
    private static final String TEMPLATE_LOCATION = "com.atlassian.confluence.plugins.confluence-inline-tasks:email-resources";
    private static final String RECOMMENDED_TEMPLATE_NAME = "Confluence.Templates.InlineTasks.Notifications.recommendedEmailPanel.soy";
    private static final String IMAGE_RESOURCES_KEY = "inline-task-mail-resources";
    private final InlineTaskService inlineTaskService;
    private final TaskEntityHelper taskEntityHelper;
    private final TemplateRenderer templateRenderer;
    private final DataSourceFactory imageDataSourceFactory;
    private final PluginDataSourceFactory pluginDataSourceFactory;

    @Autowired
    public RecommendedEmailTaskRenderer(InlineTaskService inlineTaskService, TaskEntityHelper taskEntityHelper, TemplateRenderer templateRenderer, DataSourceFactory imageDataSourceFactory) {
        this.inlineTaskService = inlineTaskService;
        this.taskEntityHelper = taskEntityHelper;
        this.templateRenderer = templateRenderer;
        this.imageDataSourceFactory = imageDataSourceFactory;
        Maybe pluginFactory = imageDataSourceFactory.forPlugin("com.atlassian.confluence.plugins.confluence-inline-tasks");
        this.pluginDataSourceFactory = (PluginDataSourceFactory)pluginFactory.getOrNull();
    }

    public String renderUpcomingTasksForMail(ConfluenceUser user, Map<String, Object> context) {
        SearchTaskParameters searchParams = new SearchTaskParameters();
        searchParams.setAssigneeUserKeys(Arrays.asList(user.getKey().getStringValue()));
        searchParams.setStatus(TaskStatus.UNCHECKED);
        searchParams.setEndDueDate(this.getAWeekFromToday());
        searchParams.setPageSize(5);
        searchParams.setDisplayedPages(1);
        searchParams.setSortParameters(new SearchTaskSortParameter(SortColumn.DUE_DATE, SortOrder.ASCENDING));
        List tasks = this.inlineTaskService.searchTasks(searchParams).getResults();
        if (!tasks.isEmpty() && context.containsKey("WebPanelDataSources")) {
            List dataSources = (List)context.get("WebPanelDataSources");
            dataSources.add((DataSource)this.pluginDataSourceFactory.resourceFromModuleByName(IMAGE_RESOURCES_KEY, "inline-task-calendar-icon").get());
            dataSources.add((DataSource)this.pluginDataSourceFactory.resourceFromModuleByName(IMAGE_RESOURCES_KEY, "inline-task-unchecked-icon").get());
        }
        List<TaskEntity> taskEntities = this.taskEntityHelper.createTaskEntities(tasks);
        HashMap<String, List<TaskEntity>> templateContextMap = new HashMap<String, List<TaskEntity>>();
        templateContextMap.put("taskEntities", taskEntities);
        StringBuilder content = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)content, TEMPLATE_LOCATION, RECOMMENDED_TEMPLATE_NAME, templateContextMap);
        return content.toString();
    }

    private Date getAWeekFromToday() {
        Calendar cal = Calendar.getInstance();
        cal.add(6, 7);
        return cal.getTime();
    }
}

