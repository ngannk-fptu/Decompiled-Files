/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater
 */
package com.atlassian.confluence.extra.dynamictasklist2.dao;

import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.extra.dynamictasklist2.dao.TaskListMacroDefinitionHandler;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListDeserializer;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListSerializer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;

class TaskListMacroDefinitionUpdater
implements MacroDefinitionUpdater {
    private final TaskList updatedTaskList;
    private final TaskListSerializer taskListSerializer;
    private final TaskListMacroDefinitionHandler taskListMacroDefinitionHandler;
    private boolean taskListUpdated = false;

    public TaskListMacroDefinitionUpdater(TaskList updatedTaskList, TaskListSerializer taskListSerializer, TaskListDeserializer taskListDeserializer) {
        this.updatedTaskList = updatedTaskList;
        this.taskListSerializer = taskListSerializer;
        this.taskListMacroDefinitionHandler = new TaskListMacroDefinitionHandler(new TaskListId(updatedTaskList.getName(), updatedTaskList.getOccurance()), taskListDeserializer);
    }

    public MacroDefinition update(MacroDefinition macroDefinition) {
        this.taskListMacroDefinitionHandler.handle(macroDefinition);
        if (!this.taskListUpdated && this.taskListMacroDefinitionHandler.getMatchingTaskList() != null) {
            PlainTextMacroBody macroBody = new PlainTextMacroBody(this.taskListSerializer.serialize(this.updatedTaskList));
            MacroDefinitionBuilder updatedMacroDefinition = MacroDefinition.builder((String)macroDefinition.getName()).withMacroBody((MacroBody)macroBody).withParameters(macroDefinition.getParameters());
            updatedMacroDefinition.setDefaultParameterValue(macroDefinition.getDefaultParameterValue());
            this.taskListUpdated = true;
            return updatedMacroDefinition.build();
        }
        return macroDefinition;
    }
}

