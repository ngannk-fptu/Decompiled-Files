/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.security.auth.trustedapps.IPAddressFormatException
 *  com.atlassian.security.auth.trustedapps.RequestConditions
 *  com.atlassian.security.auth.trustedapps.RequestConditions$RulesBuilder
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.trusted.auth.AbstractTrustedAppsServlet;
import com.atlassian.applinks.trusted.auth.Action;
import com.atlassian.applinks.trusted.auth.TrustConfigurator;
import com.atlassian.applinks.trusted.auth.TrustedAppsAuthenticationProviderPluginModule;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.auth.trustedapps.IPAddressFormatException;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class ProviderConfigurationServlet
extends AbstractTrustedAppsServlet {
    private final WebSudoManager webSudoManager;

    public ProviderConfigurationServlet(I18nResolver i18nResolver, TemplateRenderer templateRenderer, AdminUIAuthenticator adminUIAuthenticator, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, MessageFactory messageFactory, TrustedApplicationsConfigurationManager trustedAppsManager, AuthenticationConfigurationManager configurationManager, TrustedApplicationsManager trustedApplicationsManager, InternalHostApplication hostApplication, TrustConfigurator trustConfigurator, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, adminUIAuthenticator, applicationLinkService, hostApplication, trustedApplicationsManager, configurationManager, trustedAppsManager, trustConfigurator, loginUriProvider, documentationLinker, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink link = this.getRequiredApplicationLink(request);
            if (!StringUtils.isBlank((CharSequence)request.getParameter("result"))) {
                this.processPeerResponse(request, response, link);
            } else {
                this.render(this.getRequiredApplicationLink(request), request, response, this.emptyContext());
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private void processPeerResponse(HttpServletRequest request, HttpServletResponse response, ApplicationLink link) throws IOException {
        RendererContextBuilder contextBuilder = new RendererContextBuilder();
        if (!this.peerWasSuccessful(request)) {
            contextBuilder.put("error", (Object)this.messageFactory.newI18nMessage("auth.trusted.config.consumer.save.peer.failed", new Serializable[]{request.getParameter("message")}));
        }
        this.render(link, request, response, contextBuilder.build());
    }

    private boolean peerWasSuccessful(HttpServletRequest request) {
        return "success".equals(this.getRequiredParameter(request, "result").toLowerCase());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink link = this.getRequiredApplicationLink(request);
            RendererContextBuilder contextBuilder = new RendererContextBuilder();
            boolean success = false;
            try {
                this.configureLocalTrust(request, link);
                if (this.peerHasUAL(request)) {
                    response.sendRedirect(this.createRedirectURL(request, link));
                    return;
                }
                success = true;
            }
            catch (InputValidationException ive) {
                contextBuilder.put(ive.getField(), (Object)ive.getMessage());
            }
            catch (TrustConfigurator.ConfigurationException ce) {
                contextBuilder.put("error", (Object)ce.getMessage());
            }
            if (!success && this.getAction(request) == Action.ENABLE) {
                contextBuilder.put("view", (Object)"edit");
            }
            this.render(link, request, response, contextBuilder.build());
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private void configureLocalTrust(HttpServletRequest request, ApplicationLink link) throws TrustConfigurator.ConfigurationException {
        if (Action.ENABLE == this.getAction(request)) {
            this.issueLocalTrust(request, link);
        } else {
            this.trustConfigurator.revokeInboundTrust(link);
        }
    }

    private void issueLocalTrust(HttpServletRequest request, ApplicationLink link) throws TrustConfigurator.ConfigurationException, InputValidationException {
        RequestConditions.RulesBuilder rulesBuilder = RequestConditions.builder();
        String ipPatternsInput = request.getParameter("ipPatternsInput");
        String urlPatternsInput = request.getParameter("urlPatternsInput");
        String timeoutInput = request.getParameter("timeoutInput");
        if (!StringUtils.isBlank((CharSequence)ipPatternsInput)) {
            try {
                rulesBuilder.addIPPattern(StringUtils.split((String)ipPatternsInput, (String)"\n\r"));
            }
            catch (IPAddressFormatException e) {
                throw new InputValidationException(this.i18nResolver.getText("auth.trusted.config.error.ip.patterns", new Serializable[]{"<br>\"192.168.*.*<br>127.0.0.1\""}), "ipPatternsInputErrorHtml");
            }
        }
        if (!StringUtils.isBlank((CharSequence)urlPatternsInput)) {
            try {
                rulesBuilder.addURLPattern(StringUtils.split((String)urlPatternsInput, (String)"\n\r"));
            }
            catch (IllegalArgumentException e) {
                throw new InputValidationException(this.i18nResolver.getText("auth.trusted.config.error.url.patterns"), "urlPatternsInputError");
            }
        }
        if (!StringUtils.isBlank((CharSequence)timeoutInput)) {
            try {
                rulesBuilder.setCertificateTimeout(Long.parseLong(timeoutInput));
            }
            catch (IllegalArgumentException iae) {
                throw new InputValidationException(this.i18nResolver.getText("auth.trusted.config.error.timeout"), "timeoutInputError");
            }
        } else {
            rulesBuilder.setCertificateTimeout(10000L);
        }
        this.trustConfigurator.updateInboundTrust(link, rulesBuilder.build());
    }

    private String createRedirectURL(HttpServletRequest request, ApplicationLink link) throws IOException {
        URI remoteDisplayUrl = !StringUtils.isEmpty((CharSequence)request.getParameter(HOST_URL_PARAM)) ? URI.create(request.getParameter(HOST_URL_PARAM)) : link.getDisplayUrl();
        String callbackUrl = URIUtil.uncheckedConcatenate((URI)RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.internalHostApplication.getBaseUrl()), (String[])new String[]{request.getServletPath(), request.getPathInfo()}) + "?" + HOST_URL_PARAM + "=" + URIUtil.utf8Encode((URI)remoteDisplayUrl);
        URI targetBase = URIUtil.uncheckedConcatenate((URI)remoteDisplayUrl, (String[])new String[]{TrustedAppsAuthenticationProviderPluginModule.CONSUMER_SERVLET_LOCATION_UAL + this.internalHostApplication.getId()});
        return String.format("%s?callbackUrl=%s&action=%s", targetBase.toString(), URIUtil.utf8Encode((String)callbackUrl), this.getAction(request).name());
    }

    private void render(ApplicationLink appLink, HttpServletRequest request, HttpServletResponse response, Map<String, Object> renderContext) throws IOException {
        String trustedAppsId = (String)appLink.getProperty(TRUSTED_APPS_INCOMING_ID);
        boolean enabled = null != trustedAppsId;
        String consumer = appLink.getName();
        String consumerAppType = this.i18nResolver.getText(appLink.getType().getI18nKey());
        String provider = this.internalHostApplication.getName();
        String providerAppType = this.i18nResolver.getText(this.internalHostApplication.getType().getI18nKey());
        RendererContextBuilder contextBuilder = new RendererContextBuilder(renderContext).put("urlPatternsInput", (Object)request.getParameter("urlPatternsInput")).put("ipPatternsInput", (Object)request.getParameter("ipPatternsInput")).put("timeoutInput", (Object)request.getParameter("timeoutInput")).put("hostUrl", (Object)request.getParameter(AbstractAdminOnlyAuthServlet.HOST_URL_PARAM));
        if (enabled) {
            RequestConditions conditions = this.trustedApplicationsManager.getTrustedApplication(trustedAppsId).getRequestConditions();
            contextBuilder.put("urlPatterns", (Object)this.join(conditions.getURLPatterns(), '\n')).put("ipPatterns", (Object)this.join(conditions.getIPPatterns(), '\n')).put("timeout", (Object)Long.toString(conditions.getCertificateTimeout()));
        }
        this.render(request, response, consumer, consumerAppType, provider, providerAppType, enabled, contextBuilder.build());
    }

    private String join(Iterable<String> iterable, char delimiter) {
        return StringUtils.join(iterable.iterator(), (char)delimiter);
    }

    private static class InputValidationException
    extends RuntimeException {
        private final String field;

        private InputValidationException(String message, String field) {
            super(message);
            this.field = field;
        }

        public String getField() {
            return this.field;
        }
    }
}

