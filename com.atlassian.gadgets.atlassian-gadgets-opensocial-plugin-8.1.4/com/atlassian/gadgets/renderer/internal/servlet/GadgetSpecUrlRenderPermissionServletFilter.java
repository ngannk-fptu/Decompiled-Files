/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 *  com.atlassian.gadgets.GadgetSpecUrlChecker
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Preconditions
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import com.atlassian.gadgets.GadgetSpecUrlChecker;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GadgetSpecUrlRenderPermissionServletFilter
implements Filter {
    private static final String URL_PARAM = "url";
    private final GadgetSpecUrlChecker gadgetChecker;
    private final I18nResolver i18n;
    private final Logger log = LoggerFactory.getLogger(GadgetSpecUrlRenderPermissionServletFilter.class);
    private final TemplateRenderer renderer;

    public GadgetSpecUrlRenderPermissionServletFilter(GadgetSpecUrlChecker gadgetChecker, @ComponentImport I18nResolver i18nResolver, @ComponentImport TemplateRenderer renderer) {
        Preconditions.checkNotNull((Object)gadgetChecker, (Object)"gadgetChecker");
        Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.gadgetChecker = gadgetChecker;
        this.i18n = i18nResolver;
        this.renderer = renderer;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        URI gadgetSpecUri;
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        String uri = req.getParameter(URL_PARAM);
        if (StringUtils.isBlank((CharSequence)uri)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            gadgetSpecUri = new URI(uri);
        }
        catch (URISyntaxException urise) {
            this.log.warn("GadgetSpecUrlRenderPermissionServletFilter: couldn't parse URI from request", (Throwable)urise);
            resp.sendError(400, "couldn't parse given 'url' parameter as a URI");
            return;
        }
        try {
            this.gadgetChecker.assertRenderable(gadgetSpecUri.toString());
        }
        catch (GadgetSpecUriNotAllowedException igsue) {
            resp.setStatus(410);
            resp.setContentType("text/html");
            if (resp.getWriter() != null) {
                HashMap<String, Object> context = new HashMap<String, Object>();
                context.put("message", this.i18n.getText("error.gadget.gone"));
                context.put("unescaper", Unescaper.getSingleton());
                this.renderer.render("/gadgetRemovedError.vm", context, (Writer)resp.getWriter());
            }
            return;
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public static class Unescaper {
        private static Unescaper singleton = new Unescaper();

        @HtmlSafe
        public String html(String text) {
            return text;
        }

        public static Unescaper getSingleton() {
            return singleton;
        }
    }
}

