/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.jira.beans;

import com.atlassian.confluence.plugins.jira.beans.BasicJiraIssueBean;
import java.util.Collections;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraIssueBean
extends BasicJiraIssueBean {
    @XmlElement
    private String summary;
    @XmlElement
    private String description;
    @XmlElement
    private String projectId;
    @XmlElement
    private String issueTypeId;
    @XmlElement
    private String reporter;
    @XmlElement
    private String assignee;
    @XmlElement
    private String priority;
    @XmlElement
    private Map<String, String> errors;
    @XmlElement
    private Map<String, String> fields;

    public JiraIssueBean() {
    }

    public JiraIssueBean(String projectId, String issueTypeId, String summary, String description) {
        this.projectId = projectId;
        this.issueTypeId = issueTypeId;
        this.summary = summary;
        this.description = description;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getIssueTypeId() {
        return this.issueTypeId;
    }

    public void setIssueTypeId(String issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public String getReporter() {
        return this.reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Map<String, String> getErrors() {
        return this.errors != null ? this.errors : Collections.emptyMap();
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getFields() {
        return this.fields != null ? this.fields : Collections.emptyMap();
    }
}

