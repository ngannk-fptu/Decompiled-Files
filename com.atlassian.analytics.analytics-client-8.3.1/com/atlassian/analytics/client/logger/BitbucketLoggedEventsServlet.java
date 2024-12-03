/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.event.project.ProjectCreatedEvent
 *  com.atlassian.bitbucket.project.Project
 *  com.atlassian.bitbucket.project.ProjectService
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.analytics.client.logger.SampleAnalyticsEvent;
import com.atlassian.analytics.client.pipeline.preprocessor.EventPreprocessor;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.client.session.SalSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;
import com.atlassian.bitbucket.event.project.ProjectCreatedEvent;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BitbucketLoggedEventsServlet
extends HttpServlet {
    private final EventPreprocessor eventPreprocessor;
    private final TemplateRenderer renderer;
    private final LoginPageRedirector loginPageRedirector;
    private final UserPermissionsHelper userPermissionsHelper;
    private final EventSerializer eventSerializer;
    private final SessionIdProvider sessionIdProvider;
    private final I18nResolver i18nResolver;
    private final ProjectService projectService;

    public BitbucketLoggedEventsServlet(EventPreprocessor eventPreprocessor, TemplateRenderer renderer, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper, I18nResolver i18nResolver, HttpContext httpContext, ProjectService projectService, EventSerializer eventSerializer) {
        this.eventPreprocessor = eventPreprocessor;
        this.renderer = renderer;
        this.loginPageRedirector = loginPageRedirector;
        this.userPermissionsHelper = userPermissionsHelper;
        this.i18nResolver = i18nResolver;
        this.sessionIdProvider = new SalSessionIdProvider(httpContext);
        this.eventSerializer = eventSerializer;
        this.projectService = projectService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        Map<String, Object> context = this.getDummyEventsContext(request);
        context.put("String", String.class);
        context.put("application-name", "Bitbucket");
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
        return dummyEventsContext;
    }

    private List<RawEvent> generateEvents(HttpServletRequest request) {
        ArrayList<RawEvent> events = new ArrayList<RawEvent>();
        List projectKeys = this.projectService.findAllKeys();
        Project project = projectKeys.isEmpty() ? null : this.projectService.getByKey((String)projectKeys.get(new Random().nextInt(projectKeys.size())));
        RawEvent projectCreatedEvent = this.generateProjectCreatedEvent(project, request);
        String projectName = String.valueOf(projectCreatedEvent.getProperties().get("project.name"));
        String projectCreatedMessage = MessageFormat.format(this.i18nResolver.getText("analytics.eventslogged.stash.project.created.help"), projectName);
        events.add(new SampleAnalyticsEvent(projectCreatedEvent, projectCreatedMessage));
        return events;
    }

    private RawEvent generateProjectCreatedEvent(Project project, HttpServletRequest request) {
        if (project == null) {
            return this.getDummyProjectCreatedEvent();
        }
        ProjectCreatedEvent projectCreatedEvent = new ProjectCreatedEvent((Object)this, project);
        return this.eventSerializer.toAnalyticsEvent(projectCreatedEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent getDummyProjectCreatedEvent() {
        ImmutableMap properties = ImmutableMap.of((Object)"project.name", (Object)"Test Project");
        return this.createDummyEvent((Map<String, Object>)properties);
    }

    private RawEvent createDummyEvent(Map<String, Object> properties) {
        return new RawEvent.Builder().name("stash.project.created").server("server.somewhere.com").product("stash").version("3.1.0").user("admin").session("-1016800166").sen("34534251324").sourceIP("14.124.84.20").properties(properties).build();
    }
}

