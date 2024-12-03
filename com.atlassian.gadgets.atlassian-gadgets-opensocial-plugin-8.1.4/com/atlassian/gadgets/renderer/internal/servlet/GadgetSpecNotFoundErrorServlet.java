/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import com.atlassian.gadgets.renderer.internal.servlet.GadgetSpecUrlRenderPermissionServletFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Objects;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GadgetSpecNotFoundErrorServlet
extends HttpServlet {
    private final I18nResolver i18nResolver;
    private final TemplateRenderer velocityTemplateRenderer;

    public GadgetSpecNotFoundErrorServlet(@ComponentImport I18nResolver i18nResolver, @ComponentImport TemplateRenderer renderer) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
        this.velocityTemplateRenderer = Objects.requireNonNull(renderer);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(404);
        resp.setContentType("text/html");
        if (resp.getWriter() != null) {
            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put("message", this.i18nResolver.getText("error.gadget.gone"));
            context.put("unescaper", GadgetSpecUrlRenderPermissionServletFilter.Unescaper.getSingleton());
            this.velocityTemplateRenderer.render("/gadgetRemovedError.vm", context, (Writer)resp.getWriter());
        }
    }
}

