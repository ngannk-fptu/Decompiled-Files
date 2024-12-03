/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.tasklist.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class TaskStorageFormatBuilder {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final String CHECKED = "complete";
    public static final String UNCHECKED = "incomplete";
    private Long id;
    private String status = "incomplete";
    private String body;
    private String assignee = "";
    private String duedate = "";

    public TaskStorageFormatBuilder id(long taskId) {
        this.id = taskId;
        return this;
    }

    public TaskStorageFormatBuilder status(String taskStatus) {
        this.status = taskStatus;
        return this;
    }

    public TaskStorageFormatBuilder bodyText(String taskBody) {
        return this.bodyXml(StringEscapeUtils.escapeXml((String)taskBody));
    }

    public TaskStorageFormatBuilder bodyXml(String taskBody) {
        return this.bodyRawXml("<span>" + taskBody + "</span>");
    }

    public TaskStorageFormatBuilder bodyRawXml(String taskBody) {
        this.body = taskBody;
        return this;
    }

    public TaskStorageFormatBuilder assignee(String userKey) {
        this.assignee = StringUtils.isBlank((CharSequence)userKey) ? "" : "<ac:link><ri:user ri:userkey=\"" + userKey + "\" /></ac:link>&nbsp;";
        return this;
    }

    public TaskStorageFormatBuilder duedate(Date date) {
        this.duedate = date == null ? "" : "&nbsp;<time datetime=\"" + DATE_FORMATTER.format(date) + "\"></time>";
        return this;
    }

    public TaskStorageFormatBuilder duedate(String dateString) {
        this.duedate = StringUtils.isBlank((CharSequence)dateString) ? "" : "<time datetime=\"" + dateString + "\"></time>";
        return this;
    }

    public String build() {
        return "<ac:task>\n" + (String)(this.id == null ? "" : "<ac:task-id>" + this.id + "</ac:task-id>\n") + "<ac:task-status>" + this.status + "</ac:task-status>\n<ac:task-body>" + this.assignee + this.body + this.duedate + "</ac:task-body>\n</ac:task>";
    }

    public long getId() {
        return this.id;
    }
}

