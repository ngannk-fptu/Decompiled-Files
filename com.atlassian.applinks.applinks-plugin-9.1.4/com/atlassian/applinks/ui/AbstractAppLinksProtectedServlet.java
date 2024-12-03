/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.XsrfProtectedServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAppLinksProtectedServlet
extends AbstractApplinksServlet
implements XsrfProtectedServlet {
    public AbstractAppLinksProtectedServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, AdminUIAuthenticator adminUIAuthenticator, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, documentationLinker, loginUriProvider, internalHostApplication, adminUIAuthenticator, xsrfTokenAccessor, xsrfTokenValidator);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.checkAccess(request)) {
            this.doProtectedService(request, response);
        } else {
            this.handleUnauthorizedAccess(request);
        }
    }

    protected abstract void handleUnauthorizedAccess(HttpServletRequest var1);

    protected abstract boolean checkAccess(HttpServletRequest var1);

    protected void doProtectedService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}

