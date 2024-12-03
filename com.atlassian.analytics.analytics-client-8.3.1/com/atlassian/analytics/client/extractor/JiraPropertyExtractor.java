/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.application.api.ApplicationKey
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.jira.application.ApplicationRole
 *  com.atlassian.jira.application.ApplicationRoleManager
 *  com.atlassian.jira.component.ComponentAccessor
 *  com.atlassian.jira.config.IssueTypeManager
 *  com.atlassian.jira.event.AbstractWorkflowEvent
 *  com.atlassian.jira.event.ProjectCreatedEvent
 *  com.atlassian.jira.event.config.IssueTypeCreatedEvent
 *  com.atlassian.jira.event.issue.IssueEvent
 *  com.atlassian.jira.event.type.EventTypeManager
 *  com.atlassian.jira.event.user.UserEvent
 *  com.atlassian.jira.issue.Issue
 *  com.atlassian.jira.issue.issuetype.IssueType
 *  com.atlassian.jira.project.Project
 *  com.atlassian.jira.project.ProjectManager
 *  com.atlassian.jira.security.JiraAuthenticationContext
 *  com.atlassian.jira.user.ApplicationUser
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.EntityObjectPropertyContributor;
import com.atlassian.analytics.client.extractor.PluginPropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.atlassian.analytics.client.extractor.SystemIssueEventType;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.application.api.ApplicationKey;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.application.ApplicationRole;
import com.atlassian.jira.application.ApplicationRoleManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.event.AbstractWorkflowEvent;
import com.atlassian.jira.event.ProjectCreatedEvent;
import com.atlassian.jira.event.config.IssueTypeCreatedEvent;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.event.user.UserEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraPropertyExtractor
implements PropertyExtractor {
    private static final Logger log = LoggerFactory.getLogger(JiraPropertyExtractor.class);
    private static final ImmutableSet<String> EXCLUDE_PROPERTIES = ImmutableSet.of((Object)"source", (Object)"class", (Object)"timestamp", (Object)"time", (Object)"token", (Object)"params.password.token", (Object[])new String[]{"params.password.hours"});
    private static final String UNKNOWN_PROPERTY = "unknown";
    @VisibleForTesting
    static final String PROJECT_TYPE = "projectType";
    private final PropertyExtractorHelper helper = new PropertyExtractorHelper((Set<String>)EXCLUDE_PROPERTIES, new EntityObjectPropertyContributor(), new PluginPropertyContributor());
    private final JiraAuthenticationContext authenticationContext;
    private final EventTypeManager eventTypeManager;
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;
    private final ApplicationRoleManager applicationRoleManager;

    public JiraPropertyExtractor(EventTypeManager eventTypeManager, IssueTypeManager issueTypeManager, ProjectManager projectManager, ApplicationRoleManager applicationRoleManager) {
        this.eventTypeManager = eventTypeManager;
        this.issueTypeManager = issueTypeManager;
        this.projectManager = projectManager;
        this.applicationRoleManager = applicationRoleManager;
        this.authenticationContext = this.getJiraAuthenticationContext();
    }

    protected JiraAuthenticationContext getJiraAuthenticationContext() {
        return (JiraAuthenticationContext)ComponentAccessor.getComponent(JiraAuthenticationContext.class);
    }

    @Override
    public Map<String, Object> extractProperty(String name, Object value) {
        log.debug("Extracting property {}", (Object)name);
        if (this.isExcluded(name)) {
            return Collections.emptyMap();
        }
        if (value instanceof User) {
            return ImmutableMap.of((Object)(name + ".name"), (Object)((User)value).getName());
        }
        if (value instanceof ApplicationUser) {
            return ImmutableMap.of((Object)(name + ".name"), (Object)((ApplicationUser)value).getName());
        }
        if (value instanceof Issue) {
            Issue issue = (Issue)value;
            String issueId = issue.getId().toString();
            Long projectId = issue.getProjectId();
            String projectIdStr = projectId == null ? UNKNOWN_PROPERTY : projectId.toString();
            Project project = issue.getProjectObject();
            String projectTypeKey = project == null ? UNKNOWN_PROPERTY : project.getProjectTypeKey().getKey();
            return ImmutableMap.of((Object)"id", (Object)issueId, (Object)"projectId", (Object)projectIdStr, (Object)PROJECT_TYPE, (Object)projectTypeKey);
        }
        return this.helper.extractProperty(name, value);
    }

    @Override
    public boolean isExcluded(String name) {
        return this.helper.isExcluded(name);
    }

    @Override
    public String extractUser(Object event, Map<String, Object> properties) {
        if (event instanceof IssueEvent) {
            IssueEvent issueEvent = (IssueEvent)event;
            return issueEvent.getUser() == null ? null : issueEvent.getUser().getName();
        }
        if (event instanceof UserEvent && properties.get("initiatingUser.name") != null) {
            return properties.get("initiatingUser.name").toString();
        }
        if (this.authenticationContext.getLoggedInUser() != null) {
            return this.authenticationContext.getLoggedInUser().getName();
        }
        return this.getRemoteUser();
    }

    @Override
    public String extractName(Object event) {
        if (event instanceof IssueEvent) {
            IssueEvent issueEvent = (IssueEvent)event;
            Long eventTypeId = issueEvent.getEventTypeId();
            String name = JiraPropertyExtractor.getSystemIssueEventTypeName(eventTypeId);
            if (name == null) {
                name = eventTypeId < 10000L ? "Unknown Issue Event" : "User Defined Issue Event";
            }
            return name.replace(" ", "").toLowerCase();
        }
        if (event instanceof UserEvent) {
            UserEvent userEvent = (UserEvent)event;
            String name = this.getUserEventType(userEvent.getEventType());
            return name.replace(" ", "").toLowerCase();
        }
        return this.helper.extractName(event);
    }

    @Override
    public Map<String, Object> enrichProperties(Object event) {
        IssueTypeCreatedEvent issueTypeCreatedEvent;
        IssueType issueType;
        ImmutableMap.Builder newProperties = ImmutableMap.builder();
        if (event instanceof IssueEvent) {
            IssueEvent issueEvent = (IssueEvent)event;
            newProperties.put((Object)"eventTypeName", (Object)this.eventTypeManager.getEventType(issueEvent.getEventTypeId()).getName());
            this.addProjectTypeProperty((ImmutableMap.Builder<String, Object>)newProperties, issueEvent.getProject());
        } else if (event instanceof ProjectCreatedEvent) {
            ProjectCreatedEvent projectCreatedEvent = (ProjectCreatedEvent)event;
            newProperties.put((Object)"projectName", (Object)this.projectManager.getProjectObj(projectCreatedEvent.getId()).getName());
            this.addProjectTypeProperty((ImmutableMap.Builder<String, Object>)newProperties, projectCreatedEvent.getProject());
        } else if (event instanceof AbstractWorkflowEvent) {
            AbstractWorkflowEvent workflowEvent = (AbstractWorkflowEvent)event;
            newProperties.put((Object)"workflowName", (Object)workflowEvent.getWorkflow().getName());
        } else if (event instanceof IssueTypeCreatedEvent && (issueType = this.issueTypeManager.getIssueType((issueTypeCreatedEvent = (IssueTypeCreatedEvent)event).getId())) != null) {
            newProperties.put((Object)"issueTypeName", (Object)issueType.getName());
        }
        return newProperties.build();
    }

    private void addProjectTypeProperty(ImmutableMap.Builder<String, Object> properties, Project project) {
        if (project != null && project.getProjectTypeKey() != null && project.getProjectTypeKey().getKey() != null) {
            properties.put((Object)PROJECT_TYPE, (Object)project.getProjectTypeKey().getKey());
        }
    }

    @Override
    public String extractSubProduct(Object event, String product) {
        return this.helper.extractSubProduct(event, product);
    }

    @Override
    public String getApplicationAccess() {
        if (!this.authenticationContext.isLoggedInUser()) {
            return "";
        }
        Set rolesForUser = this.applicationRoleManager.getRolesForUser(this.authenticationContext.getLoggedInUser());
        return this.getApplicationKeys(rolesForUser).stream().collect(Collectors.joining(",,", ",", ","));
    }

    @Override
    public String extractRequestCorrelationId(RequestInfo request) {
        return this.helper.extractRequestCorrelationId(request);
    }

    private List<String> getApplicationKeys(Collection<ApplicationRole> applicationRoles) {
        return applicationRoles.stream().map(ApplicationRole::getKey).map(ApplicationKey::value).map(String::toLowerCase).sorted().collect(Collectors.toList());
    }

    protected String getRemoteUser() {
        ApplicationUser currentUser = this.authenticationContext.getUser();
        return currentUser == null ? null : currentUser.getUsername();
    }

    private static String getSystemIssueEventTypeName(Long eventTypeId) {
        return Arrays.stream(SystemIssueEventType.values()).filter(iet -> eventTypeId.equals(iet.getEventType())).findAny().map(SystemIssueEventType::getEventTypeName).orElse(null);
    }

    private String getUserEventType(int eventType) {
        switch (eventType) {
            case 0: {
                return "User Signup";
            }
            case 1: {
                return "User Created";
            }
            case 2: {
                return "User Forgot Password";
            }
            case 3: {
                return "User Forgot Username";
            }
            case 4: {
                return "User Cannot Change Password";
            }
            case 5: {
                return "User Login";
            }
            case 6: {
                return "User Logout";
            }
        }
        return "User";
    }
}

