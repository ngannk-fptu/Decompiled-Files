/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.sitemesh.Content
 *  com.opensymphony.sitemesh.SiteMeshContext
 *  com.opensymphony.sitemesh.compatability.Content2HTMLPage
 *  com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext
 *  com.opensymphony.sitemesh.webapp.decorator.NoDecorator
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.impl.core.persistence.hibernate.ExceptionMonitorPredicates;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.profiling.ConfluenceSitemeshDecorator;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.NoDecorator;
import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.velocity.VelocityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSitemeshErrorDecorator
extends ConfluenceSitemeshDecorator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSitemeshErrorDecorator.class);
    private SiteMeshWebAppContext siteMeshWebAppContext;

    ConfluenceSitemeshErrorDecorator(ThemeManager themeManager, SpaceManagerInternal spaceManager, Decorator decorator, VelocityManager velocityManager) {
        super(themeManager, spaceManager, decorator, ERROR_THROWING_STRATEGY, velocityManager);
    }

    @Override
    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext siteMeshWebAppContext) throws IOException, ServletException {
        if (this.shouldBypassDecorator(request)) {
            log.warn("Bypassing error page decorator");
            new NoDecorator().render(content, (SiteMeshContext)siteMeshWebAppContext);
        } else {
            this.siteMeshWebAppContext = siteMeshWebAppContext;
            super.render(content, request, response, servletContext, siteMeshWebAppContext);
        }
    }

    @Override
    protected void renderInternal(Content content, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Content2HTMLPage page = new Content2HTMLPage(content, request);
        try {
            this.applyDecorator((Page)page, this.getDecorator(), request, response);
        }
        catch (Exception e) {
            log.error("Failed to render error decorator. Falling back to using no decorator", (Throwable)e);
            NoDecorator noDecorator = new NoDecorator();
            noDecorator.render(content, (SiteMeshContext)this.siteMeshWebAppContext);
        }
    }

    private boolean shouldBypassDecorator(HttpServletRequest request) {
        return ConfluenceSitemeshErrorDecorator.isHttp500Error(request) && ExceptionMonitorPredicates.shortCircuitRequestTester().test(request);
    }

    private static boolean isHttp500Error(HttpServletRequest request) {
        return request.getDispatcherType() == DispatcherType.ERROR && Integer.valueOf(500).equals(request.getAttribute("javax.servlet.error.status_code"));
    }
}

