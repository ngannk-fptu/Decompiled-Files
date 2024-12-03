/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.tasklist.macro;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class TaskEntity {
    private String pageTitle;
    private String pageUrl;
    private long globalId;
    private long taskId;
    private String taskHtml;
    private String assigneeUserName;
    private String assigneeFullName;
    private boolean taskCompleted;
    private String dueDate;
    private String completeDate;
    private List<String> labels;

    public long getGlobalId() {
        return this.globalId;
    }

    public void setGlobalId(long globalId) {
        this.globalId = globalId;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskHtml() {
        return this.taskHtml;
    }

    public void setTaskHtml(String taskHtml) {
        this.taskHtml = taskHtml;
    }

    public String getAssigneeUserName() {
        return this.assigneeUserName;
    }

    public void setAssigneeUserName(String assigneeUserName) {
        this.assigneeUserName = assigneeUserName;
    }

    public String getAssigneeFullName() {
        return this.assigneeFullName;
    }

    public void setAssigneeFullName(String assigneeFullName) {
        this.assigneeFullName = assigneeFullName;
    }

    public boolean isTaskCompleted() {
        return this.taskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCompleteDate() {
        return this.completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String toString() {
        return "TaskEntity{pageTitle='" + this.pageTitle + "', pageUrl='" + this.pageUrl + "', globalId=" + this.globalId + ", taskId=" + this.taskId + ", taskHtml='" + this.taskHtml + "', assigneeUserName='" + this.assigneeUserName + "', assigneeFullName='" + this.assigneeFullName + "', taskCompleted=" + this.taskCompleted + ", dueDate='" + this.dueDate + "', completeDate='" + this.completeDate + "', labels=" + this.labels + "}";
    }
}

