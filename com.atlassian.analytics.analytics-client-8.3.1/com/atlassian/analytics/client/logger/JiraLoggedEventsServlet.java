/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.jira.bc.user.search.UserSearchParams
 *  com.atlassian.jira.bc.user.search.UserSearchService
 *  com.atlassian.jira.config.IssueTypeManager
 *  com.atlassian.jira.event.WorkflowCreatedEvent
 *  com.atlassian.jira.event.config.IssueTypeCreatedEvent
 *  com.atlassian.jira.event.issue.IssueEvent
 *  com.atlassian.jira.event.type.EventType
 *  com.atlassian.jira.issue.Issue
 *  com.atlassian.jira.issue.IssueManager
 *  com.atlassian.jira.issue.issuetype.IssueType
 *  com.atlassian.jira.project.Project
 *  com.atlassian.jira.project.ProjectManager
 *  com.atlassian.jira.user.ApplicationUser
 *  com.atlassian.jira.workflow.JiraWorkflow
 *  com.atlassian.jira.workflow.WorkflowManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.ofbiz.core.entity.GenericEntityException
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.analytics.client.logger.SampleAnalyticsEvent;
import com.atlassian.analytics.client.pipeline.preprocessor.EventPreprocessor;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.client.servlet.AbstractSysAdminServlet;
import com.atlassian.analytics.client.session.SalSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.event.WorkflowCreatedEvent;
import com.atlassian.jira.event.config.IssueTypeCreatedEvent;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.core.entity.GenericEntityException;

public class JiraLoggedEventsServlet
extends AbstractSysAdminServlet {
    private final String EMPTY_USER_QUERY = "";
    private final UserSearchParams ALL_USERS_SEARCH_PARAM = UserSearchParams.builder().allowEmptyQuery(true).includeActive(true).includeInactive(true).build();
    private final TemplateRenderer renderer;
    private final EventPreprocessor eventPreprocessor;
    private final UserSearchService userSearchService;
    private final ProjectManager projectManager;
    private final IssueManager issueManager;
    private final WorkflowManager workflowManager;
    private final IssueTypeManager issueTypeManager;
    private final EventSerializer eventSerializer;
    private final SessionIdProvider sessionIdProvider;
    private final I18nResolver i18nResolver;

    public JiraLoggedEventsServlet(TemplateRenderer renderer, EventPreprocessor eventPreprocessor, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper, UserSearchService userSearchService, ProjectManager projectManager, IssueManager issueManager, WorkflowManager workflowManager, IssueTypeManager issueTypeManager, @Named(value="eventSerializer") EventSerializer eventSerializer, I18nResolver i18nResolver, HttpContext httpContext, WebSudoManager webSudoManager) {
        super(webSudoManager, loginPageRedirector, userPermissionsHelper);
        this.renderer = renderer;
        this.eventPreprocessor = eventPreprocessor;
        this.eventSerializer = eventSerializer;
        this.i18nResolver = i18nResolver;
        this.sessionIdProvider = new SalSessionIdProvider(httpContext);
        this.userSearchService = userSearchService;
        this.projectManager = projectManager;
        this.issueManager = issueManager;
        this.workflowManager = workflowManager;
        this.issueTypeManager = issueTypeManager;
    }

    @Override
    protected void doRestrictedGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> context = this.getDummyEventsContext(request);
        context.put("String", String.class);
        context.put("application-name", "JIRA");
        response.setContentType("text/html; charset=UTF-8");
        this.renderer.render("templates/events-logged.vm", context, (Writer)response.getWriter());
    }

    private Map<String, Object> getDummyEventsContext(HttpServletRequest request) {
        HashMap<String, Object> dummyEventsContext = new HashMap<String, Object>();
        List<RawEvent> rawEvents = this.generateEvents(request);
        ArrayList<ProcessedEvent> processedEvents = new ArrayList<ProcessedEvent>();
        for (RawEvent event : rawEvents) {
            processedEvents.add(this.eventPreprocessor.preprocess(event));
        }
        dummyEventsContext.put("rawEvents", rawEvents);
        dummyEventsContext.put("processedEvents", processedEvents);
        dummyEventsContext.put("date", new Date());
        return dummyEventsContext;
    }

    private List<RawEvent> generateEvents(HttpServletRequest request) {
        ArrayList<RawEvent> events = new ArrayList<RawEvent>();
        List projects = this.projectManager.getProjectObjects();
        ApplicationUser user = (ApplicationUser)this.userSearchService.findUsers("", this.ALL_USERS_SEARCH_PARAM).stream().findFirst().orElseThrow(() -> new IllegalStateException("No users found but sysadmin is logged in, how?"));
        Project project = projects.isEmpty() ? null : (Project)projects.get(new Random().nextInt(projects.size()));
        List issues = null;
        try {
            Collection issueIdsForProject;
            if (project != null && project.getId() != null && !(issueIdsForProject = this.issueManager.getIssueIdsForProject(project.getId())).isEmpty()) {
                issues = this.issueManager.getIssueObjects(issueIdsForProject);
            }
        }
        catch (GenericEntityException issueIdsForProject) {
            // empty catch block
        }
        Issue issue = issues == null || issues.isEmpty() ? null : (Issue)issues.get(new Random().nextInt(issues.size()));
        RawEvent projectCreatedEvent = this.generateProjectCreatedEvent(project, user, request);
        String projectCreatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.projectcreate.help", new Serializable[]{(String)projectCreatedEvent.getProperties().get("projectName")});
        events.add(new SampleAnalyticsEvent(projectCreatedEvent, projectCreatedMessage));
        RawEvent workflowCreatedEvent = this.generateWorkflowCreatedEvent(request);
        String workflowCreatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.workflowcreate.help", new Serializable[]{(String)workflowCreatedEvent.getProperties().get("workflowName")});
        events.add(new SampleAnalyticsEvent(workflowCreatedEvent, workflowCreatedMessage));
        RawEvent workflowUpdatedEvent = this.generateWorkflowUpdatedEvent(request);
        String workflowUpdatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.workflowupdate.help");
        events.add(new SampleAnalyticsEvent(workflowUpdatedEvent, workflowUpdatedMessage));
        RawEvent issueTypeCreatedEvent = this.generateIssueTypeCreatedEvent(request);
        String issueTypeCreatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.issuetypecreate.help");
        events.add(new SampleAnalyticsEvent(issueTypeCreatedEvent, issueTypeCreatedMessage));
        RawEvent issueCreatedEvent = this.generateIssueEvent(issue, user, EventType.ISSUE_CREATED_ID, request);
        String issueCreatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.issuecreate.help");
        events.add(new SampleAnalyticsEvent(issueCreatedEvent, issueCreatedMessage));
        RawEvent issueCommentedEvent = this.generateIssueEvent(issue, user, EventType.ISSUE_COMMENTED_ID, request);
        String issueCommentedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.issuecomment.help");
        events.add(new SampleAnalyticsEvent(issueCommentedEvent, issueCommentedMessage));
        RawEvent issueUpdatedEvent = this.generateIssueEvent(issue, user, EventType.ISSUE_GENERICEVENT_ID, request);
        String issueUpdatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.issueupdate.help");
        events.add(new SampleAnalyticsEvent(issueUpdatedEvent, issueUpdatedMessage));
        RawEvent issueDeletedEvent = this.generateIssueEvent(issue, user, EventType.ISSUE_DELETED_ID, request);
        String issueDeletedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.issuedelete.help");
        events.add(new SampleAnalyticsEvent(issueDeletedEvent, issueDeletedMessage));
        RawEvent userCreatedEvent = this.generateUserCreatedEvent(user, request);
        String userCreatedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.usercreate.help", new Serializable[]{user.getName()});
        events.add(new SampleAnalyticsEvent(userCreatedEvent, userCreatedMessage));
        RawEvent userDeletedEvent = this.generateUserDeletedEvent(user, request);
        String userDeletedMessage = this.i18nResolver.getText("analytics.eventslogged.jira.userdelete.help", new Serializable[]{user.getName()});
        events.add(new SampleAnalyticsEvent(userDeletedEvent, userDeletedMessage));
        return events;
    }

    private RawEvent generateProjectCreatedEvent(Project project, ApplicationUser user, HttpServletRequest request) {
        if (project == null || user == null) {
            return this.getDummyProjectCreatedMessage();
        }
        return this.eventSerializer.toAnalyticsEvent(new ProjectCreatedEvent(project.getId()), this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateIssueEvent(@Nullable Issue issue, ApplicationUser user, long eventTypeId, HttpServletRequest request) {
        if (issue == null || user == null) {
            if (eventTypeId == EventType.ISSUE_GENERICEVENT_ID) {
                return this.getDummyGenericIssueEventMessage();
            }
            if (eventTypeId == EventType.ISSUE_DELETED_ID) {
                return this.getDummyIssueDeletedEventMessage();
            }
            return this.getDummyIssueEventMessage();
        }
        IssueEvent issueEvent = new IssueEvent(issue, new HashMap(), user, Long.valueOf(eventTypeId));
        return this.eventSerializer.toAnalyticsEvent(issueEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateUserCreatedEvent(ApplicationUser user, HttpServletRequest request) {
        if (user == null) {
            return this.getDummyUserCreatedMessage();
        }
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
        return this.eventSerializer.toAnalyticsEvent(userCreatedEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateWorkflowCreatedEvent(HttpServletRequest request) {
        Collection workflows = this.workflowManager.getWorkflows();
        if (workflows.isEmpty()) {
            return this.getDummyWorkflowCreatedMessage();
        }
        WorkflowCreatedEvent workflowCreatedEvent = new WorkflowCreatedEvent((JiraWorkflow)workflows.iterator().next());
        return this.eventSerializer.toAnalyticsEvent(workflowCreatedEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateWorkflowUpdatedEvent(HttpServletRequest request) {
        Collection workflows = this.workflowManager.getWorkflows();
        if (workflows.isEmpty()) {
            return this.getDummyWorkflowUpdatedMessage();
        }
        return this.eventSerializer.toAnalyticsEvent(new WorkflowUpdatedEvent((JiraWorkflow)workflows.iterator().next()), this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateIssueTypeCreatedEvent(HttpServletRequest request) {
        Collection issueTypes = this.issueTypeManager.getIssueTypes();
        if (issueTypes.isEmpty()) {
            return this.getDummyIssueTypeCreatedMessage();
        }
        IssueTypeCreatedEvent issueTypeCreatedEvent = new IssueTypeCreatedEvent((IssueType)issueTypes.iterator().next(), "test");
        return this.eventSerializer.toAnalyticsEvent(issueTypeCreatedEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateUserDeletedEvent(ApplicationUser user, HttpServletRequest request) {
        if (user == null) {
            return this.getDummyUserDeletedMessage();
        }
        UserDeletedEvent userDeletedEvent = new UserDeletedEvent((Object)this, (Directory)new DirectoryImpl(), user.getName());
        return this.eventSerializer.toAnalyticsEvent(userDeletedEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent getDummyProjectCreatedMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"projectName", (Object)"Test project name").build();
        return this.createDummyEvent("projectcreated", (Map<String, Object>)properties);
    }

    private RawEvent getDummyGenericIssueEventMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"changeLog.created", (Object)"2013-11-29 09:06:46").put((Object)"changeLog.id", (Object)"10003").put((Object)"changeLog.author", (Object)"admin").put((Object)"changeLog.issue", (Object)"10001").build();
        return this.createDummyEvent("genericissue", (Map<String, Object>)properties);
    }

    private RawEvent getDummyIssueDeletedEventMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"user.name", (Object)"admin").put((Object)"subtasksUpdated", (Object)"false").put((Object)"params.baseurl", (Object)"http://server.somewhere.com:2990/jira").put((Object)"params.com.atlassian.jira.event.issue.WATCHERS[0].name", (Object)"admin").put((Object)"eventTypeName", (Object)"Issue Deleted").put((Object)"eventTypeId", (Object)"8").put((Object)"sendMail", (Object)"true").build();
        return this.createDummyEvent("issuedeleted", (Map<String, Object>)properties);
    }

    private RawEvent getDummyIssueEventMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"user.name", (Object)"admin").put((Object)"params.eventsource", (Object)"action").put((Object)"subtasksUpdated", (Object)"false").put((Object)"params.baseurl", (Object)"http://server.somewhere.com:2990/jira").put((Object)"sendMail", (Object)"true").build();
        return this.createDummyEvent("issuecreated", (Map<String, Object>)properties);
    }

    private RawEvent getDummyUserCreatedMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"user.name", (Object)"jonathan").build();
        return this.createDummyEvent("usercreated", (Map<String, Object>)properties);
    }

    private RawEvent getDummyWorkflowCreatedMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"workflowName", (Object)"Test workflow").build();
        return this.createDummyEvent("workflowcreated", (Map<String, Object>)properties);
    }

    private RawEvent getDummyWorkflowUpdatedMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"workflowName", (Object)"Test workflow").build();
        return this.createDummyEvent("workflowupdated", (Map<String, Object>)properties);
    }

    private RawEvent getDummyIssueTypeCreatedMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").build();
        return this.createDummyEvent("issuetypecreated", (Map<String, Object>)properties);
    }

    private RawEvent getDummyUserDeletedMessage() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"id", (Object)"10001").put((Object)"username", (Object)"andrew").build();
        return this.createDummyEvent("userdeleted", (Map<String, Object>)properties);
    }

    private RawEvent createDummyEvent(String eventName, Map<String, Object> properties) {
        return new RawEvent.Builder().name(eventName).server("server.somewhere.com").product("jira").version("6.1.2").user("admin").session("-1016800166").sen("34534251324").sourceIP("14.124.84.20").properties(properties).build();
    }

    private static class UserCreatedEvent {
        private UserCreatedEvent() {
        }
    }

    private static class WorkflowUpdatedEvent {
        private final JiraWorkflow workflow;

        WorkflowUpdatedEvent(JiraWorkflow workflow) {
            this.workflow = workflow;
        }

        public JiraWorkflow getWorkflow() {
            return this.workflow;
        }
    }

    public static class ProjectCreatedEvent {
        private final Long projectId;

        ProjectCreatedEvent(Long id) {
            this.projectId = id;
        }

        public Long getId() {
            return this.projectId;
        }
    }
}

