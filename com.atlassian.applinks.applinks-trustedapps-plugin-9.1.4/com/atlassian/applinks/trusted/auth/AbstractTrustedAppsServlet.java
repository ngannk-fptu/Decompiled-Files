/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.auth.AbstractSysadminOnlyAuthServlet
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.trusted.ApplinksTrustedApps
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$BadRequestException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.auth.AbstractSysadminOnlyAuthServlet;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.trusted.ApplinksTrustedApps;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.trusted.auth.Action;
import com.atlassian.applinks.trusted.auth.TrustConfigurator;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

abstract class AbstractTrustedAppsServlet
extends AbstractSysadminOnlyAuthServlet {
    public static final String WEB_RESOURCE_KEY = "com.atlassian.applinks.applinks-trustedapps-plugin:";
    public static final String TRUSTED_APPS_INCOMING_ID = ApplinksTrustedApps.PROPERTY_TRUSTED_APPS_INCOMING_ID;
    protected static final String VM_TEMPLATE = "com/atlassian/applinks/trusted/auth/config.vm";
    protected final TrustedApplicationsConfigurationManager trustedAppsManager;
    protected final AuthenticationConfigurationManager configurationManager;
    protected final TrustedApplicationsManager trustedApplicationsManager;
    protected final TrustConfigurator trustConfigurator;

    protected AbstractTrustedAppsServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, AdminUIAuthenticator adminUIAuthenticator, ApplicationLinkService applicationLinkService, InternalHostApplication internalHostApplication, TrustedApplicationsManager trustedApplicationsManager, AuthenticationConfigurationManager configurationManager, TrustedApplicationsConfigurationManager trustedAppsManager, TrustConfigurator trustConfigurator, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.trustedAppsManager = trustedAppsManager;
        this.trustedApplicationsManager = trustedApplicationsManager;
        this.configurationManager = configurationManager;
        this.trustConfigurator = trustConfigurator;
    }

    protected Action getAction(HttpServletRequest request) {
        String value = this.getRequiredParameter(request, "action");
        try {
            return Action.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            throw new AbstractApplinksServlet.BadRequestException(this.messageFactory.newI18nMessage("auth.trusted.config.reciprocal.action.missing", new Serializable[]{value}));
        }
    }

    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-trustedapps-plugin:trusted-auth");
    }

    protected boolean peerHasUAL(HttpServletRequest request) {
        return !request.getServletPath().endsWith("-non-ual");
    }

    protected void render(HttpServletRequest request, HttpServletResponse response, String consumer, String consumerAppType, String provider, String providerAppType, boolean enabled, Map<String, Object> renderContext) throws IOException {
        Object view = renderContext.get("view");
        String role = request.getServletPath().replaceFirst(".*/([^/?]+).*", "$1").startsWith("inbound") ? "provider" : "consumer";
        this.render(VM_TEMPLATE, new RendererContextBuilder(renderContext).put("stringUtils", (Object)new StringUtils()).put("enabled", (Object)enabled).put("view", ObjectUtils.defaultIfNull((Object)view, (Object)(enabled ? "enabled" : "disabled"))).put("nonUAL", (Object)(!this.peerHasUAL(request) ? 1 : 0)).put("formLocation", (Object)(request.getContextPath() + request.getServletPath() + request.getPathInfo())).put("consumer", (Object)consumer).put("consumerAppType", (Object)consumerAppType).put("providerAppType", (Object)providerAppType).put("provider", (Object)provider).put("role", (Object)role).build(), request, response);
    }
}

