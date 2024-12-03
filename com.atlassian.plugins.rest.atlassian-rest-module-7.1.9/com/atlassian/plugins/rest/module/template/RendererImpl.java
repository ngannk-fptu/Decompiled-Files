/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.module.template;

import com.atlassian.plugins.rest.common.template.Renderer;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.sun.jersey.api.core.HttpContext;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class RendererImpl
implements Renderer {
    private final TemplateRenderer templateRenderer;
    private final OutputStreamWriter writer;
    private final HttpContext httpContext;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    RendererImpl(TemplateRenderer templateRenderer, OutputStreamWriter writer, HttpContext httpContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer can't be null");
        this.writer = Objects.requireNonNull(writer, "writer can't be null");
        this.httpContext = Objects.requireNonNull(httpContext, "httpContext can't be null");
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void render(Object model, String template) throws IOException {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("it", model);
        context.put("renderer", this);
        context.put("httpContext", this.httpContext);
        context.put("request", this.httpServletRequest);
        context.put("response", this.httpServletResponse);
        this.templateRenderer.render(this.getAbsolutePath(model.getClass(), template), context, (Writer)this.writer);
    }

    private String getAbsolutePath(Class<?> resourceClass, String path) {
        if (StringUtils.startsWith((CharSequence)path, (CharSequence)"/")) {
            return path;
        }
        if (StringUtils.isEmpty((CharSequence)path) || StringUtils.equals((CharSequence)path, (CharSequence)"/")) {
            path = "index";
        }
        return this.getAbsolutePath(resourceClass) + '/' + path;
    }

    private String getAbsolutePath(Class<?> resourceClass) {
        return '/' + resourceClass.getName().replace('.', '/').replace('$', '/');
    }
}

