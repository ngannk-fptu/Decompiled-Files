/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.tasklist;

import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class TaskModfication {
    private final Operation taskOperation;
    private final Task task;
    @JsonIgnore
    private String htmlContent;

    @JsonCreator
    public TaskModfication(@JsonProperty(value="task") Task task, @JsonProperty(value="taskOperation") Operation taskOperation) {
        this.task = task;
        this.taskOperation = taskOperation;
    }

    public Task getTask() {
        return this.task;
    }

    public void setHtmlContent(String html) {
        this.htmlContent = html;
    }

    @HtmlSafe
    public String getHtmlContent() {
        return this.htmlContent;
    }

    public Operation getTaskOperation() {
        return this.taskOperation;
    }

    public String getTaskOperationAsString() {
        return this.taskOperation.toString();
    }

    public String toString() {
        return "TaskModfication [" + this.taskOperation + ", " + this.task + ", " + this.htmlContent + "]";
    }

    public static enum Operation {
        ASSIGNED("tasks.mail.templates.assigned.task"),
        UNASSIGNED("tasks.mail.templates.unassigned.task"),
        COMPLETE("tasks.mail.templates.marked.complete"),
        IN_COMPLETE("tasks.mail.templates.marked.incomplete"),
        REWORDED("tasks.mail.templates.reworded"),
        DELETED("tasks.mail.templates.deleted");

        private String i18nKey;

        private Operation(String i18nKey) {
            this.i18nKey = i18nKey;
        }

        public String getI18nKey() {
            return this.i18nKey;
        }
    }
}

