/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module.template;

import com.atlassian.plugins.rest.module.template.RendererImpl;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.template.TemplateProcessor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityTemplateProcessor
implements TemplateProcessor {
    private static final Logger log = LoggerFactory.getLogger(VelocityTemplateProcessor.class);
    private static final String VM = ".vm";
    private final ServiceTracker templateRendererServiceTracker;
    @Context
    private HttpContext httpContext;
    @Context
    private HttpServletRequest httpServletRequest;
    @Context
    private HttpServletResponse httpServletResponse;

    VelocityTemplateProcessor(ServiceTracker templateRendererServiceTracker) {
        this.templateRendererServiceTracker = Objects.requireNonNull(templateRendererServiceTracker);
    }

    @Override
    public String resolve(String path) {
        return this.executeOnTemplateRenderer(renderer -> {
            String resolvedPath = path + VM;
            return renderer.resolve(resolvedPath) ? resolvedPath : null;
        });
    }

    @Override
    public void writeTo(String resolvedPath, Object model, OutputStream out) throws IOException {
        try {
            this.executeOnTemplateRenderer(renderer -> {
                OutputStreamWriter writer = new OutputStreamWriter(out);
                HashMap context = Maps.newHashMap();
                context.put("renderer", new RendererImpl(renderer, writer, this.httpContext, this.httpServletRequest, this.httpServletResponse));
                context.put("it", model);
                context.put("httpContext", this.httpContext);
                context.put("request", this.httpServletRequest);
                context.put("response", this.httpServletResponse);
                try {
                    renderer.render(resolvedPath, (Map)context, (Writer)writer);
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                return null;
            });
        }
        catch (RuntimeException re) {
            if (re.getCause() instanceof IOException) {
                throw (IOException)re.getCause();
            }
            throw re;
        }
    }

    public void closeTemplateRendererServiceTracker() {
        this.templateRendererServiceTracker.close();
    }

    private <T> T executeOnTemplateRenderer(TemplateRendererCommand<T> templateRendererCommand) {
        TemplateRenderer renderer = (TemplateRenderer)this.templateRendererServiceTracker.getService();
        if (renderer != null) {
            return templateRendererCommand.execute(renderer);
        }
        log.warn("No template renderer service available, not executing command");
        return null;
    }

    private static interface TemplateRendererCommand<T> {
        public T execute(TemplateRenderer var1);
    }
}

