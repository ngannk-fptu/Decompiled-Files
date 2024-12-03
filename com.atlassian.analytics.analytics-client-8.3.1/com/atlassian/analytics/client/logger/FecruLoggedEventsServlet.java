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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FecruLoggedEventsServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final EventPreprocessor eventPreprocessor;
    private final LoginPageRedirector loginPageRedirector;
    private final UserPermissionsHelper userPermissionsHelper;

    public FecruLoggedEventsServlet(TemplateRenderer renderer, EventPreprocessor eventPreprocessor, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper) {
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
        context.put("application-name", "FishEye/Crucible");
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
        return new RawEvent.Builder().name(eventName).server("server.somewhere.com").product("fisheye").version("3.6.0").user("admin").session("-1016800166").sen("34534251324").sourceIP("14.124.84.20").properties(properties).build();
    }

    private List<RawEvent> generateEvents() {
        ArrayList<RawEvent> events = new ArrayList<RawEvent>();
        String dailyFiredEventMessage = "This event is fired daily to collect general statistics about your instance";
        events.add(new SampleAnalyticsEvent(this.dummyReviewCreateEvent(), "This event is fired when a new Crucible review is created"));
        events.add(new SampleAnalyticsEvent(this.dummyReviewStateChange(), "This event is fired when a review's state progresses"));
        events.add(new SampleAnalyticsEvent(this.dummyRepoAdd(), "This event is fired when a new Git repository is added"));
        events.add(new SampleAnalyticsEvent(this.dummyUserStatistics(), dailyFiredEventMessage));
        events.add(new SampleAnalyticsEvent(this.dummySystemStatistics(), dailyFiredEventMessage));
        events.add(new SampleAnalyticsEvent(this.dummyApplinkStatistics(), dailyFiredEventMessage));
        events.add(new SampleAnalyticsEvent(this.dummyCruStatistics(), dailyFiredEventMessage));
        events.add(new SampleAnalyticsEvent(this.dummyRepoStats(), dailyFiredEventMessage));
        return events;
    }

    private RawEvent dummyUserStatistics() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"stats.groups", (Object)"5").put((Object)"stats.groupsWithActiveUsers", (Object)"5").put((Object)"stats.usersActive", (Object)"22").put((Object)"stats.usersCrucible:", (Object)"22").put((Object)"stats.usersFisheye", (Object)"22").build();
        return this.createDummyEvent("fecru.statistics.user", (Map<String, Object>)properties);
    }

    private RawEvent dummySystemStatistics() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"stats.totalMemory", (Object)"477").build();
        return this.createDummyEvent("fecru.statistics.system", (Map<String, Object>)properties);
    }

    private RawEvent dummyApplinkStatistics() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"stats.applinksBamboo", (Object)"0").put((Object)"stats.applinksConfluence", (Object)"0").put((Object)"stats.applinksCrowd", (Object)"0").put((Object)"stats.applinksFecru", (Object)"0").put((Object)"stats.applinksGeneric", (Object)"0").put((Object)"stats.applinksJira", (Object)"2").put((Object)"stats.applinksRefapp", (Object)"0").put((Object)"stats.applinksStash", (Object)"3").put((Object)"stats.applinksTotal", (Object)"5").build();
        return this.createDummyEvent("fecru.statistics.applinks", (Map<String, Object>)properties);
    }

    private RawEvent dummyCruStatistics() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"stats.cruciblePermSchemes", (Object)"2").put((Object)"stats.crucibleProjects", (Object)"7").put((Object)"stats.reviewCommentDefects", (Object)"23").put((Object)"stats.reviewComments", (Object)"399").put((Object)"stats.reviewInlineIssues", (Object)"3").put((Object)"stats.reviews", (Object)"197").put((Object)"stats.snippetComments", (Object)"55").put((Object)"stats.snippets", (Object)"12").build();
        return this.createDummyEvent("fecru.statistics.cru", (Map<String, Object>)properties);
    }

    private RawEvent dummyRepoStats() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"stats.reposCvs", (Object)"0").put((Object)"stats.reposEnabled", (Object)"14").put((Object)"stats.reposGit", (Object)"4").put((Object)"stats.reposHg", (Object)"1").put((Object)"stats.reposP4", (Object)"2").put((Object)"stats.reposSvn", (Object)"8").put((Object)"stats.reposTotal", (Object)"15").put((Object)"stats.reposWithStoreDiff", (Object)"12").build();
        return this.createDummyEvent("fecru.statistics.repos", (Map<String, Object>)properties);
    }

    private RawEvent dummyReviewCreateEvent() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"timestamp", (Object)"1411705474144").build();
        return this.createDummyEvent("cru.review.create", (Map<String, Object>)properties);
    }

    private RawEvent dummyReviewStateChange() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"action", (Object)"Close").put((Object)"newState", (Object)"Closed").put((Object)"oldState", (Object)"Review").put((Object)"timestamp", (Object)"1411706052919").build();
        return this.createDummyEvent("cru.review.state.change", (Map<String, Object>)properties);
    }

    private RawEvent dummyRepoAdd() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"repositoryName", (Object)"TEST").build();
        return this.createDummyEvent("fecru.repository.create.git", (Map<String, Object>)properties);
    }
}

