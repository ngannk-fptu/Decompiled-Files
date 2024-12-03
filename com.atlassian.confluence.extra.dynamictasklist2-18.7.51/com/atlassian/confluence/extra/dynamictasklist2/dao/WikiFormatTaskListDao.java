/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.base.Throwables
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.dynamictasklist2.dao;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.extra.dynamictasklist2.dao.TaskListMacroDefinitionHandler;
import com.atlassian.confluence.extra.dynamictasklist2.dao.TaskListMacroDefinitionUpdater;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListDeserializer;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListSerializer;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WikiFormatTaskListDao {
    private final PageManager pageManager;
    private final XhtmlContent xhtmlContent;
    private final TaskListSerializer taskListSerializer;
    private final TaskListDeserializer taskListDeserializer;

    @Autowired
    public WikiFormatTaskListDao(@ComponentImport PageManager pageManager, @ComponentImport XhtmlContent xhtmlContent, TaskListSerializer taskListSerializer, TaskListDeserializer taskListDeserializer) {
        this.pageManager = pageManager;
        this.xhtmlContent = xhtmlContent;
        this.taskListSerializer = taskListSerializer;
        this.taskListDeserializer = taskListDeserializer;
    }

    public void save(TaskList taskList, ContentEntityObject content, String comment) {
        String updatedBody;
        ContentEntityObject original;
        try {
            original = (ContentEntityObject)content.clone();
        }
        catch (Exception e) {
            throw Throwables.propagate((Throwable)e);
        }
        try {
            updatedBody = this.xhtmlContent.updateMacroDefinitions(content.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)content.toPageContext()), (MacroDefinitionUpdater)new TaskListMacroDefinitionUpdater(taskList, this.taskListSerializer, this.taskListDeserializer));
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
        content.setBodyAsString(updatedBody);
        if (taskList.getConfig().getEnableVersioning()) {
            content.setVersionComment("Dynamic Task List: " + comment);
            DefaultSaveContext saveContext = new DefaultSaveContext(true, true, false);
            this.pageManager.saveContentEntity(content, original, (SaveContext)saveContext);
        }
    }

    public TaskList getTaskList(String listName, int occurance, ContentEntityObject content) {
        TaskListId taskListId = new TaskListId(listName, occurance);
        TaskListMacroDefinitionHandler handler = new TaskListMacroDefinitionHandler(taskListId, this.taskListDeserializer);
        try {
            this.xhtmlContent.handleMacroDefinitions(content.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)content.toPageContext()), (MacroDefinitionHandler)handler);
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
        return handler.getMatchingTaskList();
    }

    public static final class Column {
        public static final Column COMPLETED = new Column("Completed");
        public static final Column PRIORITY = new Column("Priority");
        public static final Column LOCKED = new Column("Locked");
        public static final Column CREATED_DATE = new Column("CreatedDate");
        public static final Column COMPLETED_DATE = new Column("CompletedDate");
        public static final Column ASSIGNEE = new Column("Assignee");
        public static final Column NAME = new Column("Name");
        private String name;

        Column(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Column column = (Column)o;
            return this.name.equals(column.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public static Column[] values() {
            return new Column[]{COMPLETED, PRIORITY, LOCKED, CREATED_DATE, COMPLETED_DATE, ASSIGNEE, NAME};
        }
    }
}

