/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.config.IssueTypeManager
 *  com.atlassian.jira.config.StatusManager
 *  com.atlassian.jira.license.JiraLicenseManager
 *  com.atlassian.jira.ofbiz.OfBizDelegator
 *  com.atlassian.jira.project.ProjectManager
 */
package com.atlassian.analytics.client.base;

import com.atlassian.analytics.client.AnalyticsMd5Hasher;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.JiraBaseDataEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.project.ProjectManager;

public class JiraBaseDataLogger
implements BaseDataLogger {
    private final EventPublisher eventPublisher;
    private final JiraLicenseManager jiraLicenseManager;
    private final StatusManager statusManager;
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;
    private final OfBizDelegator ofBizDelegator;

    public JiraBaseDataLogger(EventPublisher eventPublisher, JiraLicenseManager jiraLicenseManager, StatusManager statusManager, IssueTypeManager issueTypeManager, ProjectManager projectManager, OfBizDelegator ofBizDelegator) {
        this.eventPublisher = eventPublisher;
        this.jiraLicenseManager = jiraLicenseManager;
        this.statusManager = statusManager;
        this.issueTypeManager = issueTypeManager;
        this.projectManager = projectManager;
        this.ofBizDelegator = ofBizDelegator;
    }

    @Override
    public void logBaseData() {
        long numUsers = this.ofBizDelegator.getCount("User");
        long numProjects = this.projectManager.getProjectCount();
        long numIssues = this.ofBizDelegator.getCount("Issue");
        long numWorkflows = this.ofBizDelegator.getCount("Workflow");
        int numStatuses = this.statusManager.getStatuses().size();
        int numIssueTypes = this.issueTypeManager.getIssueTypes().size();
        String serverKey = AnalyticsMd5Hasher.md5Hex(this.jiraLicenseManager.getServerId());
        this.eventPublisher.publish((Object)this.createBaseEvent(numProjects, numIssues, numWorkflows, numStatuses, numIssueTypes, numUsers, serverKey));
    }

    private JiraBaseDataEvent createBaseEvent(long numProjects, long numIssues, long numWorkflows, int numStatuses, int numIssueTypes, long numUsers, String serverKey) {
        return new JiraBaseDataEvent(numProjects, numIssues, numWorkflows, numStatuses, numIssueTypes, numUsers, serverKey);
    }
}

