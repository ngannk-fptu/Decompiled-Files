/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.Template
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.runtime.log.JdkLogChute
 *  org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
 */
package com.atlassian.streams.confluence;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ConfluenceStreamsServlet
extends HttpServlet {
    private final VelocityEngine velocity;
    private final WebResourceManager webResourceManager;
    private final I18nResolver i18nResolver;
    private final StreamsFeedUriBuilderFactory streamsFeedUriBuilderFactory;

    public ConfluenceStreamsServlet(WebResourceManager webResourceManager, I18nResolver i18nResolver, StreamsFeedUriBuilderFactory streamsFeedUriBuilderFactory) {
        this.webResourceManager = webResourceManager;
        this.i18nResolver = i18nResolver;
        this.streamsFeedUriBuilderFactory = streamsFeedUriBuilderFactory;
        this.velocity = new VelocityEngine();
        this.velocity.addProperty("runtime.log.logsystem.class", (Object)JdkLogChute.class.getName());
        this.velocity.addProperty("resource.loader", (Object)"classpath");
        this.velocity.addProperty("classpath.resource.loader.class", (Object)ClasspathResourceLoader.class.getName());
    }

    public void init() throws ServletException {
        try {
            this.velocity.init();
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.webResourceManager.requireResource("com.atlassian.streams.confluence:streamsWebResources");
        Template template = this.getTemplate("/templates/confluence-streams.vm");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("baseurl", request.getContextPath());
        map.put("feedUrl", this.streamsFeedUriBuilderFactory.getStreamsFeedUriBuilder(request.getContextPath()).getUri().toASCIIString());
        map.put("resourceKey", "com.atlassian.streams.confluence");
        map.put("i18n", this.i18nResolver);
        map.put("title", "Confluence Activity Stream");
        map.put("detailedTitle", true);
        this.render(template, map, response);
    }

    private Template getTemplate(String templateName) throws ServletException {
        try {
            return this.velocity.getTemplate(templateName);
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }

    private void render(Template template, Map<String, Object> map, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        VelocityContext context = new VelocityContext(map);
        template.merge((Context)context, (Writer)response.getWriter());
    }
}

