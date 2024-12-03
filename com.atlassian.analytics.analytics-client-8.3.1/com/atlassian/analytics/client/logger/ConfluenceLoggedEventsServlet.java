/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.space.SpaceCreateEvent
 *  com.atlassian.confluence.event.events.user.UserCreateEvent
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.search.page.Pager
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.util.CollectionUtils
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
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.event.events.user.UserCreateEvent;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.search.page.Pager;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.CollectionUtils;

public class ConfluenceLoggedEventsServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final EventPreprocessor eventPreprocessor;
    private final LoginPageRedirector loginPageRedirector;
    private final UserPermissionsHelper userPermissionsHelper;
    private final EventSerializer eventSerializer;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final UserManager userManager;
    private final SessionIdProvider sessionIdProvider;

    public ConfluenceLoggedEventsServlet(TemplateRenderer renderer, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper, HttpContext httpContext, EventPreprocessor eventPreprocessor, UserManager userManager, SpaceManager spaceManager, PageManager pageManager, EventSerializer eventSerializer) {
        this.renderer = renderer;
        this.loginPageRedirector = loginPageRedirector;
        this.userPermissionsHelper = userPermissionsHelper;
        this.eventPreprocessor = eventPreprocessor;
        this.sessionIdProvider = new SalSessionIdProvider(httpContext);
        this.eventSerializer = eventSerializer;
        this.userManager = userManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        Map<String, Object> context = this.getDummyEventsContext(request);
        context.put("String", String.class);
        context.put("application-name", "Confluence");
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
        List spaces = this.spaceManager.getAllSpaces();
        Space space = spaces.isEmpty() ? null : (Space)spaces.get(new Random().nextInt(spaces.size()));
        List pages = space == null ? null : this.pageManager.getPages(space, false);
        Page page = pages == null || pages.isEmpty() ? null : (Page)pages.get(new Random().nextInt(pages.size()));
        List blogPosts = this.pageManager.getBlogPosts(space, false);
        BlogPost blogPost = blogPosts.isEmpty() ? null : (BlogPost)blogPosts.get(new Random().nextInt(blogPosts.size()));
        Pager users = null;
        try {
            users = this.userManager.getUsers();
        }
        catch (EntityException entityException) {
            // empty catch block
        }
        User user = users == null || users.isEmpty() ? null : (User)users.iterator().next();
        RawEvent spaceCreatedEvent = this.generateSpaceCreateEvent(space, request);
        String spaceCreatedMessage = "This event would have been fired when the space with name '" + spaceCreatedEvent.getProperties().get("spaceName") + "' was created.";
        events.add(new SampleAnalyticsEvent(spaceCreatedEvent, spaceCreatedMessage));
        RawEvent pageCreatedEvent = this.generatePageCreateEvent(page, request);
        String pageCreatedMessage = "This event would have been fired when a page was created.";
        events.add(new SampleAnalyticsEvent(pageCreatedEvent, "This event would have been fired when a page was created."));
        RawEvent blogPostCreatedEvent = this.generateBlogPostCreateEvent(blogPost, request);
        String blogPostCreatedMessage = "This event would have been fired when a blog post was created.";
        events.add(new SampleAnalyticsEvent(blogPostCreatedEvent, "This event would have been fired when a blog post was created."));
        RawEvent commentCreatedEvent = this.generateCommentCreateEvent(pages, request);
        String commentCreatedMessage = "This event would have been fired when a comment was made on a page.";
        events.add(new SampleAnalyticsEvent(commentCreatedEvent, "This event would have been fired when a comment was made on a page."));
        RawEvent userCreatedEvent = this.generateUserCreatedEvent(user, request);
        String userCreatedMessage = "This event would have been fired when a user with name '" + userCreatedEvent.getProperties().get("user.name") + "' was created";
        events.add(new SampleAnalyticsEvent(userCreatedEvent, userCreatedMessage));
        return events;
    }

    private RawEvent generateSpaceCreateEvent(Space space, HttpServletRequest request) {
        if (space == null) {
            return this.getDummySpaceCreateEvent();
        }
        SpaceCreateEvent spaceCreateEvent = new SpaceCreateEvent((Object)this, space);
        return this.eventSerializer.toAnalyticsEvent(spaceCreateEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generatePageCreateEvent(Page page, HttpServletRequest request) {
        if (page == null) {
            return this.getDummyPageCreateEvent();
        }
        PageCreateEvent pageCreatedEvent = new PageCreateEvent((Object)this, page);
        return this.eventSerializer.toAnalyticsEvent(pageCreatedEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateBlogPostCreateEvent(BlogPost blogPost, HttpServletRequest request) {
        if (blogPost == null) {
            return this.getDummyBlogPostCreateEvent();
        }
        BlogPostCreateEvent blogPostCreateEvent = new BlogPostCreateEvent((Object)this, blogPost);
        return this.eventSerializer.toAnalyticsEvent(blogPostCreateEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateCommentCreateEvent(List<Page> pages, HttpServletRequest request) {
        List comments = null;
        if (pages != null) {
            Page page;
            Iterator<Page> iterator = pages.iterator();
            while (iterator.hasNext() && CollectionUtils.isEmpty((Collection)(comments = (page = iterator.next()).getComments()))) {
            }
        }
        if (CollectionUtils.isEmpty(comments)) {
            return this.getDummyCommentCreateEvent();
        }
        CommentCreateEvent commentCreateEvent = new CommentCreateEvent((Object)this, (Comment)comments.get(new Random().nextInt(comments.size())));
        return this.eventSerializer.toAnalyticsEvent(commentCreateEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent generateUserCreatedEvent(User user, HttpServletRequest request) {
        if (user == null) {
            return this.getDummyUserCreatedEvent();
        }
        UserCreateEvent userCreateEvent = new UserCreateEvent((Object)this, user);
        return this.eventSerializer.toAnalyticsEvent(userCreateEvent, this.sessionIdProvider.getSessionId(), RequestInfo.fromRequest(request)).get();
    }

    private RawEvent getDummySpaceCreateEvent() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"space.id", (Object)"432423").put((Object)"spaceName", (Object)"Testing a new space.").build();
        return this.createDummyEvent("spacecreate", (Map<String, Object>)properties);
    }

    private RawEvent getDummyPageCreateEvent() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"content.homePage", (Object)"false").put((Object)"content.type", (Object)"page").put((Object)"page.id", (Object)"819259").put((Object)"page.homePage", (Object)"false").put((Object)"page.type", (Object)"page").put((Object)"content.space.id", (Object)"983044").put((Object)"content.id", (Object)"819259").put((Object)"page.space.id", (Object)"983044").build();
        return this.createDummyEvent("pagecreate", (Map<String, Object>)properties);
    }

    private RawEvent getDummyBlogPostCreateEvent() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"blogPost.space.id", (Object)"983044").put((Object)"blogPost.type", (Object)"blogpost").put((Object)"blogPost.id", (Object)"819261").put((Object)"content.type", (Object)"blogpost").put((Object)"content.space.id", (Object)"983044").put((Object)"content.id", (Object)"819261").build();
        return this.createDummyEvent("blogpostcreate", (Map<String, Object>)properties);
    }

    private RawEvent getDummyCommentCreateEvent() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"comment.id", (Object)"819252").put((Object)"comment.owner.type", (Object)"page").put((Object)"content.type", (Object)"comment").put((Object)"content.owner.space.id", (Object)"983043").put((Object)"comment.owner.homePage", (Object)"true").put((Object)"comment.type", (Object)"comment").put((Object)"content.id", (Object)"819252").put((Object)"content.owner.homePage", (Object)"true").put((Object)"comment.owner.space.id", (Object)"983043").put((Object)"content.owner.type", (Object)"page").put((Object)"comment.owner.id", (Object)"819251").put((Object)"content.owner.id", (Object)"819251").build();
        return this.createDummyEvent("commentcreate", (Map<String, Object>)properties);
    }

    private RawEvent getDummyUserCreatedEvent() {
        ImmutableMap properties = ImmutableMap.builder().put((Object)"user.name", (Object)"testuser").build();
        return this.createDummyEvent("usercreated", (Map<String, Object>)properties);
    }

    private RawEvent createDummyEvent(String name, Map<String, Object> properties) {
        return new RawEvent.Builder().name(name).server("server.somewhere.com").product("confluence").version("5.3.4").user("admin").session("-1016800166").sen("34534251324").sourceIP("14.124.84.20").properties(properties).build();
    }
}

