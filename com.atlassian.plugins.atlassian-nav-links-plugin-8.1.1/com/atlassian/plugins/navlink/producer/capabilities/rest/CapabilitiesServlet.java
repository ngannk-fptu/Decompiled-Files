/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.capabilities.rest;

import com.atlassian.plugins.navlink.producer.capabilities.ApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.services.CapabilityService;
import com.atlassian.plugins.navlink.util.CacheControlFactory;
import com.atlassian.plugins.navlink.util.JsonStringEncoder;
import com.atlassian.plugins.navlink.util.LastModifiedFormatter;
import com.atlassian.plugins.navlink.util.date.UniversalDateFormatter;
import com.atlassian.plugins.navlink.util.url.BaseUrl;
import com.atlassian.plugins.navlink.util.url.SelfUrl;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilitiesServlet
extends HttpServlet {
    private static final String VELOCITY_TEMPLATES = "templates/capabilities.vm";
    private final Logger logger = LoggerFactory.getLogger(CapabilitiesServlet.class);
    private final CapabilityService capabilityService;
    private final TemplateRenderer templateRenderer;
    private final ApplicationProperties applicationProperties;

    public CapabilitiesServlet(CapabilityService capabilityService, TemplateRenderer templateRenderer, ApplicationProperties applicationProperties) {
        this.capabilityService = capabilityService;
        this.templateRenderer = templateRenderer;
        this.applicationProperties = applicationProperties;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        try {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            httpServletResponse.setHeader("Cache-Control", CacheControlFactory.withConfiguredMaxAgeAndStaleContentExtension().toString());
            httpServletResponse.setHeader("Last-Modified", LastModifiedFormatter.formatCurrentTimeMillis());
            Map<String, Object> context = this.createContext(httpServletRequest);
            PrintWriter writer = httpServletResponse.getWriter();
            this.renderTemplate(context, writer);
        }
        catch (IOException e) {
            this.handleException(httpServletResponse, e);
        }
    }

    private void renderTemplate(Map<String, Object> context, Writer writer) throws IOException {
        this.templateRenderer.render(VELOCITY_TEMPLATES, context, writer);
    }

    private Map<String, Object> createContext(HttpServletRequest httpServletRequest) {
        BaseUrl baseUrl = new BaseUrl(this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        String selfUrl = SelfUrl.extractFrom(httpServletRequest);
        ApplicationWithCapabilities hostApplication = this.capabilityService.getHostApplication();
        return ImmutableMap.builder().put((Object)"baseUrl", (Object)baseUrl).put((Object)"selfUrl", (Object)selfUrl).put((Object)"buildDate", (Object)this.formatBuildDate()).put((Object)"hostApplication", (Object)hostApplication).put((Object)"json", (Object)new JsonStringEncoder()).build();
    }

    private void handleException(HttpServletResponse httpServletResponse, Exception e) {
        this.logger.warn("Failed to serialize application capabilities: {}", (Object)e.getMessage());
        this.logger.debug("Stacktrace:", (Throwable)e);
        httpServletResponse.setStatus(500);
    }

    private String formatBuildDate() {
        Date buildDate = this.applicationProperties.getBuildDate();
        return UniversalDateFormatter.formatUtc(buildDate.toInstant().atZone(ZoneOffset.UTC));
    }
}

