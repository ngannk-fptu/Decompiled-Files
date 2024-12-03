/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableList
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrowdLoggedEventsServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final EventPreprocessor eventPreprocessor;
    private final LoginPageRedirector loginPageRedirector;
    private final UserPermissionsHelper userPermissionsHelper;

    public CrowdLoggedEventsServlet(TemplateRenderer renderer, EventPreprocessor eventPreprocessor, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper) {
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
        context.put("String", String.class);
        context.put("application-name", "Crowd");
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

    private RawEvent createDummyEvent(String eventName, Map<String, Object> properties) {
        return new RawEvent.Builder().name(eventName).server("server.somewhere.com").product("crowd").version("2.10.0").user("admin").session("-1016800166").sen("34534251324").sourceIP("14.124.84.20").properties(properties).build();
    }

    private List<RawEvent> generateEvents() {
        return ImmutableList.of((Object)new SampleAnalyticsEvent(this.createDummyEvent("crowd.directory.synchronisation.successful", (Map<String, Object>)ImmutableMap.of((Object)"bucket", (Object)"TEN_SECONDS_ONE_MINUTE", (Object)"timeTaken", (Object)35400, (Object)"connectorType", (Object)"FEDORA_DS", (Object)"directoryType", (Object)"CONNECTOR", (Object)"directoryId", (Object)12345)), "This event is fired after a directory is synchronized"), (Object)new SampleAnalyticsEvent(this.createDummyEvent("crowd.statistics.directory.type", (Map<String, Object>)ImmutableMap.of((Object)"type.CONNECTOR", (Object)3, (Object)"type.INTERNAL", (Object)1, (Object)"type.CROWD", (Object)5)), "This event is fired daily to collect information about the types of configured directories"), (Object)new SampleAnalyticsEvent(this.createDummyEvent("crowd.statistics.directory.count", (Map<String, Object>)ImmutableMap.of((Object)"total", (Object)6, (Object)"withNestedGroups", (Object)1, (Object)"active", (Object)5, (Object)"withCachingDisabled", (Object)2)), "This event is fired daily to collect information about the number and configuration of configured directories"), (Object)new SampleAnalyticsEvent(this.createDummyEvent("crowd.statistics.application.count", (Map<String, Object>)ImmutableMap.of((Object)"total", (Object)4, (Object)"withAliases", (Object)1, (Object)"active", (Object)4, (Object)"withGroupAggregation", (Object)2, (Object)"withLowercasedOutput", (Object)1)), "This event is fired daily to collect information about the number and configuration of configured applications"), (Object)new SampleAnalyticsEvent(this.createDummyEvent("crowd.statistics.environment.jvm", (Map<String, Object>)ImmutableMap.of((Object)"bitness", (Object)64, (Object)"heapSizeMb", (Object)3641, (Object)"javaVersion", (Object)108)), "This event is fired daily to collect information about the Java VM configuration"));
    }
}

