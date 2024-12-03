/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.analytics.client.configuration;

import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.LoggingProperties;
import com.atlassian.analytics.client.servlet.AbstractSysAdminServlet;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;

public class AnalyticsConfigServlet
extends AbstractSysAdminServlet {
    private final AnalyticsConfig analyticsConfig;
    private final TemplateRenderer renderer;
    private final AnalyticsPropertyService applicationProperties;
    private final String analyticsAbsoluteLogDirectoryPath;

    public AnalyticsConfigServlet(LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper, AnalyticsConfig analyticsConfig, TemplateRenderer renderer, AnalyticsPropertyService applicationProperties, WebSudoManager webSudoManager, LoggingProperties loggingProperties) {
        super(webSudoManager, loginPageRedirector, userPermissionsHelper);
        this.analyticsConfig = analyticsConfig;
        this.renderer = renderer;
        this.applicationProperties = applicationProperties;
        this.analyticsAbsoluteLogDirectoryPath = loggingProperties.getAbsoluteLogDirectoryPath();
    }

    @Override
    protected void doRestrictedGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImmutableMap context = ImmutableMap.of((Object)"analytics-enabled", (Object)this.analyticsConfig.isAnalyticsEnabled(), (Object)"analytics-logs-location", (Object)new File(this.analyticsAbsoluteLogDirectoryPath), (Object)"application-name", (Object)this.applicationProperties.getDisplayName(), (Object)"application-nameHtml", (Object)StringEscapeUtils.escapeHtml4((String)this.applicationProperties.getDisplayName()));
        response.setContentType("text/html; charset=UTF-8");
        this.renderer.render("templates/analytics-config.vm", (Map)context, (Writer)response.getWriter());
    }
}

