/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.datetime.DateFormatService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.EntityException
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.LocalDateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.confluence.api.service.datetime.DateFormatService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.macro.TaskEntity;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexTaskRegistrator;
import com.atlassian.confluence.plugins.tasklist.service.TaskEntityHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.EntityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultTaskEntityHelper
implements TaskEntityHelper {
    private static final Logger log = LoggerFactory.getLogger(DefaultTaskEntityHelper.class);
    private final PageManager pageManager;
    private final UserAccessor userAccessor;
    private final XhtmlContent xhtmlContent;
    private final DateFormatService dateFormatService;
    private final IndexTaskRegistrator indexTaskRegistrator;

    @Autowired
    public DefaultTaskEntityHelper(PageManager pageManager, UserAccessor userAccessor, XhtmlContent xhtmlContent, DateFormatService dateFormatService, IndexTaskRegistrator indexTaskRegistrator) {
        this.pageManager = pageManager;
        this.userAccessor = userAccessor;
        this.xhtmlContent = xhtmlContent;
        this.dateFormatService = dateFormatService;
        this.indexTaskRegistrator = indexTaskRegistrator;
    }

    @Override
    public TaskEntity createSingleTaskEntity(Task task) throws EntityException {
        AbstractPage content = this.pageManager.getAbstractPage(task.getContentId());
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)content.toPageContext());
        return this.createTaskEntity(task, content, (ConversionContext)context);
    }

    @Override
    public List<TaskEntity> createTaskEntities(List<Task> tasks) {
        ArrayList<TaskEntity> entities = new ArrayList<TaskEntity>(tasks.size());
        ContentAccessor pageAccessor = new ContentAccessor();
        for (Task task : tasks) {
            AbstractPage content = pageAccessor.getContent(task.getContentId());
            if (content != null) {
                DefaultConversionContext context = new DefaultConversionContext((RenderContext)content.toPageContext());
                entities.add(this.createTaskEntity(task, content, (ConversionContext)context));
                continue;
            }
            this.deleteAllTasksForThePage(task.getContentId(), task.getGlobalId());
        }
        return entities;
    }

    private void deleteAllTasksForThePage(long contentId, long globalTaskId) {
        log.warn("A task with global id {} has a reference to content with id {} which can't be found in the database. It would happen if the task and its content record were physically removed so the search index was not notified. + To resolve the issue, all tasks with the same content id will be removed from the search index. No further actions required.", (Object)globalTaskId, (Object)contentId);
        this.indexTaskRegistrator.requestToRemoveAllTasksOnThePage(contentId);
    }

    private TaskEntity createTaskEntity(Task task, AbstractPage content, ConversionContext context) {
        TaskEntity entity = new TaskEntity();
        entity.setGlobalId(task.getGlobalId());
        entity.setTaskId(task.getId());
        entity.setPageTitle(content.getTitle());
        entity.setPageUrl(GeneralUtil.appendAmpersandOrQuestionMark((String)content.getUrlPath()) + "focusedTaskId=" + task.getId());
        entity.setTaskCompleted(task.getStatus() == TaskStatus.CHECKED);
        List<String> labels = this.getLabelTexts(content);
        entity.setLabels(labels);
        if (StringUtils.isNotEmpty((CharSequence)task.getAssigneeName())) {
            entity.setAssigneeUserName(task.getAssignee());
            entity.setAssigneeFullName(task.getAssigneeName());
        } else {
            ConfluenceUser assignee = this.userAccessor.getUserByName(task.getAssignee());
            if (assignee != null) {
                entity.setAssigneeUserName(assignee.getName());
                entity.setAssigneeFullName(assignee.getFullName());
            }
        }
        if (task.getDueDate() != null) {
            entity.setDueDate(this.dateFormatService.getFormattedDateByUserLocale(new LocalDateTime((Object)task.getDueDate())));
        }
        if (task.getCompleteDate() != null) {
            entity.setCompleteDate(this.dateFormatService.getFormattedDateByUserLocale(new LocalDateTime((Object)task.getCompleteDate())));
        }
        entity.setTaskHtml(this.renderTaskHtml(task, context));
        return entity;
    }

    private String renderTaskHtml(Task task, ConversionContext context) {
        try {
            context.setProperty("isTaskBeingRendered", (Object)Boolean.TRUE);
            String body = this.xhtmlContent.convertStorageToView(task.getBody(), context);
            InlineTaskList list = new InlineTaskList();
            list.addItem(new InlineTaskListItem(Long.toString(task.getId()), task.getStatus() == TaskStatus.CHECKED, body));
            return this.xhtmlContent.convertInlineTaskListToView(list, context);
        }
        catch (XhtmlException e) {
            throw new RuntimeException("Error rendering inline task item id = " + task.getGlobalId(), e);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Error rendering inline task item id = " + task.getGlobalId(), e);
        }
    }

    private List<String> getLabelTexts(AbstractPage content) {
        ArrayList<String> texts = new ArrayList<String>();
        for (Label label : content.getLabels()) {
            texts.add(label.getDisplayTitle());
        }
        return texts;
    }

    private class ContentAccessor {
        private Map<Long, AbstractPage> cache = new HashMap<Long, AbstractPage>();

        private ContentAccessor() {
        }

        AbstractPage getContent(long id) {
            AbstractPage content = this.cache.get(id);
            if (content == null) {
                content = DefaultTaskEntityHelper.this.pageManager.getAbstractPage(id);
                this.cache.put(id, content);
            }
            return content;
        }
    }
}

