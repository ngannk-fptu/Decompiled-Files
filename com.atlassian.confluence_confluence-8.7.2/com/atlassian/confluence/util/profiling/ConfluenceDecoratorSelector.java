/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.DecoratorMapper
 *  com.opensymphony.module.sitemesh.DefaultSitemeshBuffer
 *  com.opensymphony.module.sitemesh.HTMLPage
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.SitemeshBuffer
 *  com.opensymphony.module.sitemesh.parser.AbstractPage
 *  com.opensymphony.sitemesh.Content
 *  com.opensymphony.sitemesh.Decorator
 *  com.opensymphony.sitemesh.DecoratorSelector
 *  com.opensymphony.sitemesh.SiteMeshContext
 *  com.opensymphony.sitemesh.compatability.Content2HTMLPage
 *  com.opensymphony.sitemesh.compatability.HTMLPage2Content
 *  com.opensymphony.sitemesh.compatability.OldDecorator2NewDecorator
 *  com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext
 *  javax.servlet.DispatcherType
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.profiling.ConfluenceSitemeshDecorator;
import com.atlassian.confluence.util.profiling.ConfluenceSitemeshErrorDecorator;
import com.atlassian.confluence.util.profiling.ConfluenceSitemeshNoDecorator;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.parser.AbstractPage;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.sitemesh.compatability.HTMLPage2Content;
import com.opensymphony.sitemesh.compatability.OldDecorator2NewDecorator;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.views.velocity.VelocityManager;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceDecoratorSelector
implements DecoratorSelector {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceDecoratorSelector.class);
    private final DecoratorMapper decoratorMapper;
    private final DispatcherType dispatcherType;
    private ThemeManager themeManager;
    private SpaceManagerInternal spaceManager;
    private VelocityManager velocityManager;

    public ConfluenceDecoratorSelector(DecoratorMapper decoratorMapper, @Nullable DispatcherType dispatcherType) {
        this.decoratorMapper = decoratorMapper;
        this.dispatcherType = dispatcherType;
    }

    private synchronized ThemeManager getThemeManager() {
        if (this.themeManager == null && ContainerManager.isContainerSetup()) {
            this.themeManager = (ThemeManager)ContainerManager.getComponent((String)"themeManager");
        }
        return this.themeManager;
    }

    private synchronized SpaceManagerInternal getSpaceManager() {
        if (this.spaceManager == null && ContainerManager.isContainerSetup()) {
            this.spaceManager = (SpaceManagerInternal)ContainerManager.getComponent((String)"spaceManager");
        }
        return this.spaceManager;
    }

    private VelocityManager getVelocityManager() {
        if (this.velocityManager == null && ContainerManager.isContainerSetup()) {
            this.velocityManager = (VelocityManager)ContainerManager.getComponent((String)"velocityManager");
            return this.velocityManager;
        }
        return (VelocityManager)SetupContext.get().getBean(VelocityManager.class);
    }

    public Decorator selectDecorator(Content content, SiteMeshContext context) {
        SiteMeshWebAppContext webAppContext = (SiteMeshWebAppContext)context;
        HttpServletRequest request = webAppContext.getRequest();
        com.opensymphony.module.sitemesh.Decorator decorator = this.decoratorMapper.getDecorator(request, (Page)new Content2HTMLPage(content, request));
        if (decorator == null || decorator.getPage() == null) {
            this.diagnostics(content, context);
            return new ConfluenceSitemeshNoDecorator();
        }
        if (request.getParameter("sitemeshDispatcher") == null) {
            if (DispatcherType.ERROR.equals((Object)this.dispatcherType)) {
                return new ConfluenceSitemeshErrorDecorator(this.getThemeManager(), this.getSpaceManager(), decorator, this.getVelocityManager());
            }
            return new ConfluenceSitemeshDecorator(this.getThemeManager(), this.getSpaceManager(), decorator, this.getVelocityManager());
        }
        return new OldDecorator2NewDecorator(decorator);
    }

    private void diagnostics(Content content, SiteMeshContext context) {
        HTMLPage page;
        if (log.isDebugEnabled() && content instanceof HTMLPage2Content && (page = this.privateGet(content, HTMLPage2Content.class, "page", HTMLPage.class)) != null) {
            if (page instanceof AbstractPage) {
                SitemeshBuffer buffer = this.privateGet(page, AbstractPage.class, "sitemeshBuffer", SitemeshBuffer.class);
                if (buffer != null) {
                    if (buffer instanceof DefaultSitemeshBuffer) {
                        log.debug("Outputting non decorated page for " + ((SiteMeshWebAppContext)context).getRequest().getRequestURI() + " with buffer length: " + buffer.getBufferLength() + ", total length: " + buffer.getTotalLength() + ", has fragments: " + buffer.hasFragments() + ", and actual buffer length: " + buffer.getCharArray().length);
                        if (log.isTraceEnabled()) {
                            StringWriter writer = new StringWriter();
                            try {
                                buffer.writeTo((Writer)writer, 0, buffer.getBufferLength());
                            }
                            catch (IOException e) {
                                log.error("Error writing out to memory buffer?", (Throwable)e);
                            }
                            String str = writer.toString();
                            log.trace("Page content with length( " + str.length() + ") is:\n" + str);
                        }
                    } else {
                        log.debug("Non sitemesh buffer: " + buffer.getClass().getName());
                    }
                }
            } else {
                log.debug("Non abstract page: " + page.getClass().getName());
            }
        }
    }

    private <C> C privateGet(Object object, Class<?> fieldClass, String fieldName, Class<C> fieldType) {
        try {
            Field field = fieldClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(object));
        }
        catch (Exception e) {
            log.debug("Unable to get field " + fieldName + " from " + object.getClass().getName(), (Throwable)e);
            return null;
        }
    }
}

