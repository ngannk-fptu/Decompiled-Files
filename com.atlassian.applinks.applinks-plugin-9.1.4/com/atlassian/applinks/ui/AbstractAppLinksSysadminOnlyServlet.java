/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.ui.AbstractAppLinksProtectedServlet;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractAppLinksSysadminOnlyServlet
extends AbstractAppLinksProtectedServlet {
    protected final UserManager userManager;

    public AbstractAppLinksSysadminOnlyServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, AdminUIAuthenticator adminUIAuthenticator, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, documentationLinker, loginUriProvider, internalHostApplication, adminUIAuthenticator, xsrfTokenAccessor, xsrfTokenValidator);
        this.userManager = userManager;
    }

    @Override
    protected final boolean checkAccess(HttpServletRequest request) {
        return this.adminUIAuthenticator.checkSysadminUIAccessBySessionOrCurrentUser(request);
    }

    @Override
    protected void handleUnauthorizedAccess(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            throw new AbstractApplinksServlet.UnauthorizedException(this.messageFactory.newI18nMessage("applinks.error.only.sysadmin.operation", new Serializable[0]));
        }
        throw new AbstractApplinksServlet.UnauthorizedBecauseUnauthenticatedException();
    }
}

