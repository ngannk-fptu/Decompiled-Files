/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.util.Message
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Consumer$SignatureMethod
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth.servlets.consumer;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.servlets.AbstractOAuthConfigServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddServiceProviderManuallyServlet
extends AbstractOAuthConfigServlet {
    private static final String CONSUMER_KEY_PARAMETER = "consumerKey";
    private static final String NAME_PARAMETER = "name";
    private static final String DESCRIPTION_PARAMETER = "description";
    private static final String SHARED_SECRET_PARAMETER = "sharedSecret";
    private static final String SERVICE_PROVIDER_REQUEST_TOKEN_URL_PARAMETER = "requestTokenUrl";
    private static final String SERVICE_PROVIDER_ACCESS_TOKEN_URL_PARAMETER = "accessTokenUrl";
    private static final String SERVICE_PROVIDER_AUTHORIZE_URL_PARAMETER = "authorizeUrl";
    private static final String TEMPLATE = "com/atlassian/applinks/oauth/auth/outbound_nonapplinks.vm";
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerService consumerService;
    private final WebSudoManager webSudoManager;
    public static final String CONSUMER_KEY_OUTBOUND = ApplinksOAuth.AUTH_CONFIG_CONSUMER_KEY_OUTBOUND;
    public static final String SERVICE_PROVIDER_REQUEST_TOKEN_URL = ApplinksOAuth.SERVICE_PROVIDER_REQUEST_TOKEN_URL;
    public static final String SERVICE_PROVIDER_ACCESS_TOKEN_URL = ApplinksOAuth.SERVICE_PROVIDER_ACCESS_TOKEN_URL;
    public static final String SERVICE_PROVIDER_AUTHORIZE_URL = ApplinksOAuth.SERVICE_PROVIDER_AUTHORIZE_URL;
    private static final String OUTGOING_ENABLED = "enabled";
    private static final Logger LOG = LoggerFactory.getLogger(AddServiceProviderManuallyServlet.class);
    private static final String OAUTH_OUTGOING_ENABLED_PARAM = "oauth-outgoing-enabled";

    protected AddServiceProviderManuallyServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, InternalHostApplication internalHostApplication, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerService = consumerService;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            this.view(req, resp);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            this.save(req, resp);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    private void view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
        RendererContextBuilder builder = this.createContextBuilder(applicationLink);
        if (this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class)) {
            Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
            if (config != null && config.containsKey(CONSUMER_KEY_OUTBOUND)) {
                String consumerKey = (String)config.get(CONSUMER_KEY_OUTBOUND);
                Consumer consumer = this.consumerService.getConsumerByKey(consumerKey);
                String requestTokenUrl = (String)config.get(SERVICE_PROVIDER_REQUEST_TOKEN_URL);
                String accessTokenUrl = (String)config.get(SERVICE_PROVIDER_ACCESS_TOKEN_URL);
                String authorizeUrl = (String)config.get(SERVICE_PROVIDER_AUTHORIZE_URL);
                if (consumer == null) {
                    LOG.warn("Failed to find information for service provider. No consumer with key '" + consumerKey + "' in OAuth store found. Application Link and OAuth store are out of sync. Has someone deleted this information?");
                } else {
                    builder.put(CONSUMER_KEY_PARAMETER, (Object)consumer.getKey()).put(NAME_PARAMETER, (Object)consumer.getName()).put(DESCRIPTION_PARAMETER, (Object)consumer.getDescription()).put(SHARED_SECRET_PARAMETER, (Object)"").put(OUTGOING_ENABLED, (Object)true).put(SERVICE_PROVIDER_REQUEST_TOKEN_URL_PARAMETER, (Object)requestTokenUrl).put(SERVICE_PROVIDER_ACCESS_TOKEN_URL_PARAMETER, (Object)accessTokenUrl).put(SERVICE_PROVIDER_AUTHORIZE_URL_PARAMETER, (Object)authorizeUrl).put("success-msg", (Object)this.getMessage(request));
                }
            }
        } else {
            builder.put(OUTGOING_ENABLED, (Object)false).put("success-msg", (Object)this.getMessage(request));
        }
        this.render(TEMPLATE, builder.build(), request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map config;
        boolean enable = Boolean.parseBoolean(request.getParameter(OAUTH_OUTGOING_ENABLED_PARAM));
        ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
        HashMap fieldErrorMessages = Maps.newHashMap();
        String consumerKey = this.checkRequiredParameter(request, CONSUMER_KEY_PARAMETER, fieldErrorMessages, "auth.oauth.config.consumer.serviceprovider.key.is.required");
        String name = this.checkRequiredParameter(request, NAME_PARAMETER, fieldErrorMessages, "auth.oauth.config.consumer.serviceprovider.name.is.required");
        String description = request.getParameter(DESCRIPTION_PARAMETER);
        String requestTokenUrl = this.checkRequiredParameter(request, SERVICE_PROVIDER_REQUEST_TOKEN_URL_PARAMETER, fieldErrorMessages, "auth.oauth.config.error.request.token.url");
        String accessTokenUrl = this.checkRequiredParameter(request, SERVICE_PROVIDER_ACCESS_TOKEN_URL_PARAMETER, fieldErrorMessages, "auth.oauth.config.error.access.token.url");
        String authorizeUrl = this.checkRequiredParameter(request, SERVICE_PROVIDER_AUTHORIZE_URL_PARAMETER, fieldErrorMessages, "auth.oauth.config.error.authorize.url");
        if (!StringUtils.isBlank((CharSequence)requestTokenUrl)) {
            requestTokenUrl = this.relativitize(requestTokenUrl, applicationLink.getRpcUrl(), SERVICE_PROVIDER_REQUEST_TOKEN_URL_PARAMETER, "auth.oauth.config.error.invalid.request.token.url", fieldErrorMessages);
        }
        if (!StringUtils.isBlank((CharSequence)accessTokenUrl)) {
            accessTokenUrl = this.relativitize(accessTokenUrl, applicationLink.getRpcUrl(), SERVICE_PROVIDER_ACCESS_TOKEN_URL_PARAMETER, "auth.oauth.config.error.invalid.access.token.url", fieldErrorMessages);
        }
        if (!StringUtils.isBlank((CharSequence)authorizeUrl)) {
            authorizeUrl = this.relativitize(authorizeUrl, applicationLink.getDisplayUrl(), SERVICE_PROVIDER_AUTHORIZE_URL_PARAMETER, "auth.oauth.config.error.invalid.authorize.url", fieldErrorMessages);
        }
        String sharedSecret = this.checkRequiredParameter(request, SHARED_SECRET_PARAMETER, fieldErrorMessages, "auth.oauth.config.consumer.serviceprovider.shared.secret.is.required");
        if (!fieldErrorMessages.isEmpty() && enable) {
            RendererContextBuilder builder = this.createContextBuilder(applicationLink).put("fieldErrorMessages", (Object)fieldErrorMessages).put(CONSUMER_KEY_PARAMETER, (Object)consumerKey).put(NAME_PARAMETER, (Object)name).put(DESCRIPTION_PARAMETER, (Object)description).put(SHARED_SECRET_PARAMETER, (Object)sharedSecret).put(SERVICE_PROVIDER_REQUEST_TOKEN_URL_PARAMETER, (Object)requestTokenUrl).put(SERVICE_PROVIDER_ACCESS_TOKEN_URL_PARAMETER, (Object)accessTokenUrl).put(SERVICE_PROVIDER_AUTHORIZE_URL_PARAMETER, (Object)authorizeUrl);
            this.render(TEMPLATE, builder.build(), request, response);
            return;
        }
        if (enable) {
            if (this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class)) {
                config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
                if (config != null && config.containsKey(CONSUMER_KEY_OUTBOUND)) {
                    String oldConsumerKey = (String)config.get(CONSUMER_KEY_OUTBOUND);
                    this.consumerService.removeConsumerByKey(oldConsumerKey);
                }
            } else {
                Consumer existingConsumerDuplicatedServiceName = this.consumerService.getConsumer(name);
                Consumer existingConsumerDuplicatedKey = this.consumerService.getConsumerByKey(consumerKey);
                if (existingConsumerDuplicatedServiceName != null) {
                    fieldErrorMessages.put(NAME_PARAMETER, this.messageFactory.newI18nMessage("auth.oauth.config.consumer.serviceprovider.service.name.exists", new Serializable[]{existingConsumerDuplicatedServiceName.getKey()}));
                }
                if (existingConsumerDuplicatedKey != null) {
                    fieldErrorMessages.put(CONSUMER_KEY_PARAMETER, this.messageFactory.newI18nMessage("auth.oauth.config.consumer.serviceprovider.consumer.key.exists", new Serializable[]{existingConsumerDuplicatedKey.getName()}));
                }
                if (!fieldErrorMessages.isEmpty()) {
                    RendererContextBuilder builder = this.createContextBuilder(applicationLink).put("fieldErrorMessages", (Object)fieldErrorMessages).put(CONSUMER_KEY_PARAMETER, (Object)consumerKey).put(NAME_PARAMETER, (Object)name).put(DESCRIPTION_PARAMETER, (Object)description).put(SHARED_SECRET_PARAMETER, (Object)sharedSecret).put(SERVICE_PROVIDER_REQUEST_TOKEN_URL_PARAMETER, (Object)requestTokenUrl).put(SERVICE_PROVIDER_ACCESS_TOKEN_URL_PARAMETER, (Object)accessTokenUrl).put(SERVICE_PROVIDER_AUTHORIZE_URL_PARAMETER, (Object)authorizeUrl);
                    this.render(TEMPLATE, builder.build(), request, response);
                    return;
                }
            }
            this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), OAuthAuthenticationProvider.class, (Map)ImmutableMap.of((Object)CONSUMER_KEY_OUTBOUND, (Object)consumerKey, (Object)SERVICE_PROVIDER_REQUEST_TOKEN_URL, (Object)requestTokenUrl, (Object)SERVICE_PROVIDER_ACCESS_TOKEN_URL, (Object)accessTokenUrl, (Object)SERVICE_PROVIDER_AUTHORIZE_URL, (Object)authorizeUrl));
            Consumer consumer = Consumer.key((String)consumerKey).name(name).signatureMethod(Consumer.SignatureMethod.HMAC_SHA1).description(description).build();
            this.consumerService.add(name, consumer, sharedSecret);
        } else {
            config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
            if (config != null && config.containsKey(CONSUMER_KEY_OUTBOUND)) {
                String oldConsumerKey = (String)config.get(CONSUMER_KEY_OUTBOUND);
                this.consumerService.removeConsumerByKey(oldConsumerKey);
            }
            this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), OAuthAuthenticationProvider.class);
        }
        String message = enable ? this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.success") : this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.deleted");
        response.sendRedirect("./" + applicationLink.getId() + "?" + "message" + "=" + URIUtil.utf8Encode((String)message));
    }

    private String relativitize(String uri, URI supposedlyBaseUri, String parameterName, String messageKey, Map<String, Message> errorMessages) throws IllegalArgumentException {
        try {
            return AddServiceProviderManuallyServlet.relativitize(uri, supposedlyBaseUri);
        }
        catch (IllegalArgumentException iae) {
            errorMessages.put(parameterName, this.messageFactory.newI18nMessage(messageKey, new Serializable[0]));
            return uri;
        }
    }

    protected static String relativitize(String uri, URI supposedlyBaseUri) throws IllegalArgumentException {
        String relativitizedUri;
        if (uri.charAt(0) == '/') {
            return uri;
        }
        if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
            uri = "http://" + uri;
        }
        if (!(relativitizedUri = supposedlyBaseUri.relativize(URI.create(uri)).toASCIIString()).equals(uri)) {
            return "/" + relativitizedUri;
        }
        return uri;
    }

    protected final String checkRequiredParameter(HttpServletRequest request, String parameterName, Map<String, Message> errorMessages, String messageKey) {
        if (StringUtils.isBlank((CharSequence)request.getParameter(parameterName))) {
            errorMessages.put(parameterName, this.messageFactory.newI18nMessage(messageKey, new Serializable[0]));
        }
        return request.getParameter(parameterName);
    }
}

