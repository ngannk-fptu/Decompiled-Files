/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BambooLoggedEventsServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final EventPreprocessor eventPreprocessor;
    private final LoginPageRedirector loginPageRedirector;
    private final UserPermissionsHelper userPermissionsHelper;

    public BambooLoggedEventsServlet(TemplateRenderer renderer, EventPreprocessor eventPreprocessor, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper) {
        this.renderer = renderer;
        this.eventPreprocessor = eventPreprocessor;
        this.loginPageRedirector = loginPageRedirector;
        this.userPermissionsHelper = userPermissionsHelper;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        Map<String, Object> context = this.getDummyEventsContext();
        context.put("application-name", "Bamboo");
        response.setContentType("text/html; charset=UTF-8");
        this.renderer.render("templates/events-logged.vm", context, (Writer)response.getWriter());
    }

    private Map<String, Object> getDummyEventsContext() {
        HashMap<String, Object> dummyEventsContext = new HashMap<String, Object>();
        List<RawEvent> rawEvents = this.generateEvents();
        ArrayList<ProcessedEvent> processedEvents = new ArrayList<ProcessedEvent>();
        for (RawEvent event : rawEvents) {
            processedEvents.add(this.eventPreprocessor.preprocess(event));
        }
        dummyEventsContext.put("rawEvents", rawEvents);
        dummyEventsContext.put("processedEvents", processedEvents);
        dummyEventsContext.put("date", new Date());
        return dummyEventsContext;
    }

    private List<RawEvent> generateEvents() {
        ArrayList<RawEvent> events = new ArrayList<RawEvent>();
        events.add(new SampleAnalyticsEvent(this.dummyApplinkStatistics(), "This event is fired daily to collect general statistics about your instance"));
        events.add(new SampleAnalyticsEvent(this.dummyInstanceSizeStatistics(), "This event is fired daily to collect general statistics about your instance"));
        events.add(new SampleAnalyticsEvent(this.dummyPlanCreated(), "This event is fired when a new plan is created"));
        events.add(new SampleAnalyticsEvent(this.dummyBuildCreated(), "This event is fired when a new job is created"));
        events.add(new SampleAnalyticsEvent(this.dummyBuildCompleted(), "This event is fired when a build finishes running"));
        return events;
    }

    private RawEvent createDummyEvent(String eventName, Map<String, Object> properties) {
        return new RawEvent.Builder().name(eventName).server("server.somewhere.com").product("bamboo").version("5.8.0").user("admin").session("-1016800166").sen("34534251324").sourceIP("14.124.84.20").properties(properties).build();
    }

    private RawEvent dummyApplinkStatistics() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"p.applinksBamboo", (Object)"0").put((Object)"p.applinksConfluence", (Object)"0").put((Object)"p.applinksCrowd", (Object)"0").put((Object)"p.applinksFecru", (Object)"0").put((Object)"p.applinksGeneric", (Object)"0").put((Object)"p.applinksJira", (Object)"2").put((Object)"p.applinksRefapp", (Object)"0").put((Object)"p.applinksStash", (Object)"3").put((Object)"p.applinksTotal", (Object)"5").build();
        return this.createDummyEvent("bamboo.base.applinks", (Map<String, Object>)properties);
    }

    private RawEvent dummyInstanceSizeStatistics() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"p.remoteAgentCount", (Object)"63").put((Object)"p.manualStageCount", (Object)"610").put((Object)"p.projectCount", (Object)"108").put((Object)"p.elasticConfigured", (Object)"true").put((Object)"p.stageCount", (Object)"3810").put((Object)"p.planCount", (Object)"507").put((Object)"p.jobCount", (Object)"13328").build();
        return this.createDummyEvent("bamboo.base.instance.size", (Map<String, Object>)properties);
    }

    private RawEvent dummyPlanCreated() {
        return this.createDummyEvent("bamboo.plan.created", Collections.emptyMap());
    }

    private RawEvent dummyBuildCreated() {
        return this.createDummyEvent("bamboo.build.created", Collections.emptyMap());
    }

    private RawEvent dummyBuildCompleted() {
        return this.createDummyEvent("bamboo.build.completed", Collections.emptyMap());
    }
}

