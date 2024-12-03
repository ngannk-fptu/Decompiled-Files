/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 */
package com.atlassian.confluence.extra.dynamictasklist2.dao;

import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListDeserializer;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;
import com.atlassian.confluence.extra.dynamictasklist2.util.TaskListUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import java.util.HashMap;
import java.util.Map;

class TaskListMacroDefinitionHandler
implements MacroDefinitionHandler {
    private final TaskListId taskListId;
    private final TaskListDeserializer taskListDeserializer;
    private final Map<String, Integer> taskListNameToOccurrence = new HashMap<String, Integer>();
    private TaskList result;

    public TaskListMacroDefinitionHandler(TaskListId taskListId, TaskListDeserializer taskListDeserializer) {
        this.taskListId = taskListId;
        this.taskListDeserializer = taskListDeserializer;
    }

    public void handle(MacroDefinition macroDefinition) {
        if (this.result != null) {
            return;
        }
        if ("tasklist".equals(macroDefinition.getName())) {
            String taskListName = TaskListUtil.getTaskListName(macroDefinition);
            if (!this.taskListId.getListName().equals(taskListName)) {
                return;
            }
            Integer occurrence = this.taskListNameToOccurrence.get(taskListName);
            if (occurrence == null) {
                occurrence = 1;
                this.taskListNameToOccurrence.put(taskListName, occurrence);
            } else {
                occurrence = occurrence + 1;
                this.taskListNameToOccurrence.put(taskListName, occurrence);
            }
            if (this.taskListId.getOccurrence() != occurrence.intValue()) {
                return;
            }
            TaskList taskListResult = this.taskListDeserializer.deserialize(this.taskListId.getListName(), this.taskListId.getOccurrence(), macroDefinition.getParameters(), macroDefinition.getBodyText());
            taskListResult.setSourceMacro("tasklist");
            this.result = taskListResult;
        }
    }

    public TaskList getMatchingTaskList() {
        return this.result;
    }
}

