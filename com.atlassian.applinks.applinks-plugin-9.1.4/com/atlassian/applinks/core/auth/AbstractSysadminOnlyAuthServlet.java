/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.auth.AuthServletUtils;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.ui.AbstractAppLinksSysadminOnlyServlet;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractSysadminOnlyAuthServlet
extends AbstractAppLinksSysadminOnlyServlet {
    public static String HOST_URL_PARAM = "hostUrl";
    private final ApplicationLinkService applicationLinkService;

    protected AbstractSysadminOnlyAuthServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.applicationLinkService = applicationLinkService;
    }

    protected ApplicationLink getRequiredApplicationLink(HttpServletRequest request) throws AbstractApplinksServlet.NotFoundException, AbstractApplinksServlet.BadRequestException {
        return AuthServletUtils.getApplicationLink(this.applicationLinkService, this.messageFactory, request);
    }

    @Override
    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-plugin:auth-config-css");
    }
}

