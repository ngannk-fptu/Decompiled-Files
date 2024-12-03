/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.atlassian.streams.api.common.uri.UriBuilder
 *  com.google.common.base.Preconditions
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.servlet;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.api.common.uri.UriBuilder;
import com.atlassian.streams.internal.FeedBuilder;
import com.atlassian.streams.internal.HttpParameters;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.FeedRenderer;
import com.atlassian.streams.internal.feed.FeedRendererContext;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamsActivityServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(StreamsActivityServlet.class);
    private static final String CACHE_BUSTER_PARAM = "_";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private final FeedBuilder feedBuilder;
    private final FeedRenderer feedRenderer;
    private final FeedRendererContext feedRendererContext;
    private final StreamsFeedUriBuilderFactory uriBuilderFactory;
    private final ApplicationProperties applicationProperties;

    public StreamsActivityServlet(FeedBuilder feedBuilder, FeedRenderer feedRenderer, FeedRendererContext feedRendererContext, StreamsFeedUriBuilderFactory uriBuilderFactory, ApplicationProperties applicationProperties) {
        this.uriBuilderFactory = uriBuilderFactory;
        this.feedBuilder = (FeedBuilder)Preconditions.checkNotNull((Object)feedBuilder, (Object)"feedBuilder");
        this.feedRenderer = (FeedRenderer)Preconditions.checkNotNull((Object)feedRenderer, (Object)"feedRenderer");
        this.feedRendererContext = (FeedRendererContext)Preconditions.checkNotNull((Object)feedRendererContext, (Object)"feedRendererContext");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FeedModel feed;
        HttpParameters parameters = HttpParameters.parameters(request);
        String contextPath = request.getContextPath();
        URI baseUri = parameters.calculateContextUrl(this.applicationProperties, contextPath);
        try {
            feed = this.feedBuilder.getFeed(this.getSelf(request), contextPath, parameters, request.getHeader("Accept-Language"));
        }
        catch (RuntimeException e) {
            log.error("Error getting activity", (Throwable)e);
            response.sendError(500, "Error occured getting activity: " + e.getMessage());
            return;
        }
        try {
            response.setContentType(this.feedRenderer.getContentType());
            response.setCharacterEncoding(CHARACTER_ENCODING);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", -1L);
            this.feedRenderer.writeFeed(baseUri, feed, response.getWriter(), this.feedRendererContext);
        }
        catch (Exception e) {
            log.error("Error sending feed", (Throwable)e);
            response.sendError(500, "Error occurred sending feed");
        }
    }

    private Uri getSelf(HttpServletRequest request) {
        UriBuilder builder = new UriBuilder(request);
        builder.setPath(request.getContextPath() + "/activity");
        builder.removeQueryParameter(CACHE_BUSTER_PARAM);
        builder.removeQueryParameter(HttpParameters.RELATIVE_LINKS_KEY);
        for (Pair auth : this.getAuthParameter()) {
            List authParams = builder.getQueryParameters((String)auth.first());
            if (authParams != null && !authParams.isEmpty()) continue;
            builder.addQueryParameter((String)auth.first(), (String)auth.second());
        }
        return builder.toUri();
    }

    private Option<Pair<String, String>> getAuthParameter() {
        URI uriWithAuth = this.uriBuilderFactory.getStreamsFeedUriBuilder("http://localhost").addAuthenticationParameterIfLoggedIn().getUri();
        Iterator iterator = Uri.fromJavaUri((URI)uriWithAuth).getQueryParameters().entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            return Option.some((Object)Pair.pair(entry.getKey(), ((List)entry.getValue()).get(0)));
        }
        return Option.none();
    }
}

