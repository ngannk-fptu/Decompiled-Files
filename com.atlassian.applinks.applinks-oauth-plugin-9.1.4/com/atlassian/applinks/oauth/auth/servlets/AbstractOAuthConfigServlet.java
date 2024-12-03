/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.oauth.auth.servlets;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractOAuthConfigServlet
extends AbstractAdminOnlyAuthServlet {
    public static final String MESSAGE_PARAM = "message";
    public static final String WEB_RESOURCE_KEY = "com.atlassian.applinks.applinks-oauth-plugin:";

    protected AbstractOAuthConfigServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
    }

    protected String getMessage(HttpServletRequest request) {
        if (request.getParameterMap().containsKey(MESSAGE_PARAM)) {
            return request.getParameter(MESSAGE_PARAM);
        }
        return null;
    }

    protected final String checkRequiredFormParameter(HttpServletRequest request, String parameterName, Map<String, String> errorMessages, String messageKey) {
        if (StringUtils.isBlank((CharSequence)request.getParameter(parameterName))) {
            errorMessages.put(parameterName, this.i18nResolver.getText(messageKey));
        }
        return request.getParameter(parameterName);
    }

    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-oauth-plugin:oauth-auth");
    }
}

