/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.core.util.WebResources;
import com.atlassian.applinks.ui.validators.CallbackParameterValidator;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RedirectController {
    private static final Logger log = LoggerFactory.getLogger(RedirectController.class);
    public static final String REDIRECT_WARNING_TEMPLATE = "com/atlassian/applinks/ui/auth/invalidRedirectUrl.vm";
    private final CallbackParameterValidator callbackParameterValidator;
    private final TemplateRenderer templateRenderer;
    private final WebResourceManager webResourceManager;

    @Autowired
    public RedirectController(CallbackParameterValidator callbackParameterValidator, TemplateRenderer templateRenderer, WebResourceManager webResourceManager) {
        this.callbackParameterValidator = callbackParameterValidator;
        this.templateRenderer = templateRenderer;
        this.webResourceManager = webResourceManager;
    }

    public void redirectOrPrintRedirectionWarning(HttpServletResponse response, String redirectUrl) throws IOException {
        boolean callbackUrlValid = this.callbackParameterValidator.isCallbackUrlValid(redirectUrl);
        if (callbackUrlValid) {
            response.sendRedirect(redirectUrl);
        } else {
            log.warn("Prevented redirect to an invalid url: {}", (Object)redirectUrl);
            this.printRedirectWarningMessage(response);
        }
    }

    private void printRedirectWarningMessage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        Map<String, Object> context = this.buildMessageContext();
        this.templateRenderer.render(REDIRECT_WARNING_TEMPLATE, context, (Writer)response.getWriter());
    }

    private Map<String, Object> buildMessageContext() {
        RendererContextBuilder builder = new RendererContextBuilder();
        this.webResourceManager.requireResource("com.atlassian.applinks.applinks-plugin:oauth-dance");
        StringWriter stringWriter = new StringWriter();
        this.webResourceManager.includeResources((Writer)stringWriter, UrlMode.RELATIVE);
        WebResources webResources = new WebResources();
        webResources.setIncludedResources(stringWriter.getBuffer().toString());
        builder.put("webResources", webResources);
        return builder.build();
    }
}

