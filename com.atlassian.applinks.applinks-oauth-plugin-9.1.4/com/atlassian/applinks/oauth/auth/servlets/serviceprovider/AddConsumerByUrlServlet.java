/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.ServletPathConstants
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$ForbiddenException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Consumer$InstanceBuilder
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth.servlets.serviceprovider;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.oauth.auth.servlets.serviceprovider.AbstractConsumerServlet;
import com.atlassian.applinks.oauth.auth.servlets.serviceprovider.AddConsumerReciprocalServlet;
import com.atlassian.applinks.oauth.auth.servlets.serviceprovider.ConfigureOutgoingTwoLeggedOAuthReciprocalServlet;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.oauth.Consumer;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddConsumerByUrlServlet
extends AbstractConsumerServlet {
    private final ManifestRetriever manifestRetriever;
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final WebSudoManager webSudoManager;
    private static final String INCOMING_APPLINKS_TEMPLATE = "com/atlassian/applinks/oauth/auth/incoming_applinks.vm";
    private static final String COMMUNICATION_CONTEXT_PARAM = "communication";
    private static final String FIELD_ERROR_MESSAGES_CONTEXT_PARAM = "fieldErrorMessages";
    private static final String REMOTE_URL_CONTEXT_PARAM = "remoteURL";
    private static final String REMOTE_2LO_URL_CONTEXT_PARAM = "remote2LOURL";
    private static final String SUCCESS_MSG_CONTEXT_PARAM = "success-msg";
    public static final String UI_POSITION = "uiposition";
    private static final String TWO_LO_ENABLED = "twoLoEnabled";
    private static final String TWO_LO_EXECUTE_AS = "twoLoExecuteAs";
    private static final String TWO_LO_IMPERSONATION_ENABLED = "twoLoImpersonationEnabled";
    private static final String TWO_LO_ENABLED_ERROR_VALUE = "twoLoEnabledErrorValue";
    private static final String TWO_LO_EXECUTE_AS_ERROR_VALUE = "twoLoExecuteAsErrorValue";
    private static final String TWO_LO_IMPERSONATION_ENABLED_ERROR_VALUE = "twoLoImpersonationEnabledErrorValue";
    private static final String TWO_LO_SUCCESS_MESSAGE = "twoLoSuccessMessage";
    private static final String TWO_LO_ERROR_MESSAGE = "twoLoErrorMessage";
    private static final String ENABLE_DISABLE_OAUTH_PARAM = "ENABLE_DISABLE_OAUTH_PARAM";
    private static final String ENABLE_DISABLE_OUTGOING_TWO_LEGGED_OAUTH_PARAM = "ENABLE_DISABLE_OUTGOING_TWO_LEGGED_OAUTH_PARAM";
    private static final String ENABLE_DISABLE_OUTGOING_TWO_LEGGED_I_OAUTH_PARAM = "ENABLE_DISABLE_OUTGOING_TWO_LEGGED_I_OAUTH_PARAM";
    public static final String OUTGOING_2LO_ENABLED_CONTEXT_PARAM = "outgoing2LOEnabled";
    public static final String OUTGOING_2LOI_ENABLED_CONTEXT_PARAM = "outgoing2LOiEnabled";
    public static final String PARENT_FRAME_UNDERSTANDS_OUTGOING_2LO_PARAM = "parentFrameUnderstandsOutgoing2LO";
    private static final Logger LOG = LoggerFactory.getLogger(AddConsumerByUrlServlet.class);

    protected AddConsumerByUrlServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, RequestFactory requestFactory, ManifestRetriever manifestRetriever, InternalHostApplication internalHostApplication, ServiceProviderStoreService serviceProviderStoreService, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, requestFactory, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.manifestRetriever = manifestRetriever;
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        try {
            String remote2LOConfigURL;
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            RendererContextBuilder builder = this.createContextBuilder(applicationLink);
            String parameter = request.getParameter("success");
            if (parameter != null) {
                HashMap<String, String> fieldErrorMessages;
                boolean successfulEnabledOAuthInRemoteApp = Boolean.parseBoolean(parameter);
                if (successfulEnabledOAuthInRemoteApp) {
                    fieldErrorMessages = new HashMap<String, String>();
                    boolean enabled = Boolean.parseBoolean(request.getParameter("oauth-incoming-enabled"));
                    this.addOrRemoveConsumer(applicationLink, enabled, fieldErrorMessages);
                    String message = this.getMessage(request);
                    if (!StringUtils.isEmpty((CharSequence)message) && fieldErrorMessages.isEmpty()) {
                        builder.put(SUCCESS_MSG_CONTEXT_PARAM, (Object)message);
                    }
                    if (!fieldErrorMessages.isEmpty()) {
                        builder.put(FIELD_ERROR_MESSAGES_CONTEXT_PARAM, fieldErrorMessages);
                    }
                } else {
                    fieldErrorMessages = new HashMap();
                    fieldErrorMessages.put(COMMUNICATION_CONTEXT_PARAM, this.getMessage(request));
                    builder.put(FIELD_ERROR_MESSAGES_CONTEXT_PARAM, fieldErrorMessages);
                }
            } else {
                String message = this.getMessage(request);
                if (!StringUtils.isEmpty((CharSequence)message)) {
                    builder.put(SUCCESS_MSG_CONTEXT_PARAM, (Object)message);
                }
            }
            String uiPosition = request.getParameter(UI_POSITION);
            String remoteOAuthConfigURL = this.getOAuthConfigRemoteURL(applicationLink, uiPosition, request);
            if (remoteOAuthConfigURL != null) {
                builder.put(REMOTE_URL_CONTEXT_PARAM, (Object)remoteOAuthConfigURL);
            }
            if ((remote2LOConfigURL = this.get2LOConfigRemoteURL(applicationLink, uiPosition, request, this.getCallbackUrl(applicationLink, uiPosition, request), ENABLE_DISABLE_OUTGOING_TWO_LEGGED_OAUTH_PARAM, ENABLE_DISABLE_OUTGOING_TWO_LEGGED_I_OAUTH_PARAM)) != null) {
                builder.put(REMOTE_2LO_URL_CONTEXT_PARAM, (Object)remote2LOConfigURL);
            }
            builder.put(UI_POSITION, (Object)uiPosition);
            builder.put("isSysadmin", (Object)this.userManager.isSystemAdmin(this.userManager.getRemoteUsername()));
            if (applicationLink.getProperty(OAUTH_INCOMING_CONSUMER_KEY) != null) {
                if ("local".equals(uiPosition)) {
                    this.populateIncoming2LOContextParams(request, applicationLink, builder);
                } else {
                    this.populateOutgoing2LOContextParams(request, applicationLink, builder);
                }
            }
            this.render(INCOMING_APPLINKS_TEMPLATE, builder.build(), request, resp, applicationLink);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, resp);
        }
    }

    private void populateOutgoing2LOContextParams(HttpServletRequest request, ApplicationLink applicationLink, RendererContextBuilder builder) {
        boolean twoLOAllowed;
        String outgoing2LOEnabledParam = request.getParameter(OUTGOING_2LO_ENABLED_CONTEXT_PARAM);
        Consumer consumer = this.serviceProviderStoreService.getConsumer(applicationLink);
        boolean bl = twoLOAllowed = consumer != null && consumer.getTwoLOAllowed();
        if (twoLOAllowed && !Boolean.parseBoolean(outgoing2LOEnabledParam)) {
            builder.put("outgoingTwoLoShouldBeEnabled", (Object)true);
        }
        if (!twoLOAllowed && Boolean.parseBoolean(outgoing2LOEnabledParam)) {
            builder.put("outgoingTwoLoShouldBeDisabled", (Object)true);
        }
        builder.put("incoming2LOEnabledOnRemote", (Object)twoLOAllowed);
        if (!StringUtils.isEmpty((CharSequence)request.getParameter(OUTGOING_2LOI_ENABLED_CONTEXT_PARAM))) {
            boolean twoLOiAllowed;
            builder.put("outgoingTwoLoiShouldBeVisible", (Object)true);
            String outgoing2LOiEnabledParam = request.getParameter(OUTGOING_2LOI_ENABLED_CONTEXT_PARAM);
            boolean bl2 = twoLOiAllowed = consumer != null && consumer.getTwoLOImpersonationAllowed();
            if (twoLOiAllowed && !Boolean.parseBoolean(outgoing2LOiEnabledParam)) {
                builder.put("outgoingTwoLoiShouldBeEnabled", (Object)true);
            }
            if (!twoLOiAllowed && Boolean.parseBoolean(outgoing2LOiEnabledParam)) {
                builder.put("outgoingTwoLoiShouldBeDisabled", (Object)true);
            }
            builder.put("incoming2LOiEnabledOnRemote", (Object)twoLOiAllowed);
        } else {
            builder.put("outgoingTwoLoiShouldBeVisible", (Object)false);
        }
        String outgoing2LOSuccessParam = request.getParameter("outgoing_2lo_success");
        if (outgoing2LOSuccessParam != null) {
            if (Boolean.parseBoolean(outgoing2LOSuccessParam)) {
                builder.put("outgoingTwoLoSuccessMessage", (Object)this.getMessage(request));
            } else {
                builder.put("outgoingTwoLoErrorMessage", (Object)this.getMessage(request));
            }
        }
    }

    private void populateIncoming2LOContextParams(HttpServletRequest request, ApplicationLink applicationLink, RendererContextBuilder builder) {
        if (request.getParameter(TWO_LO_ERROR_MESSAGE) != null) {
            builder.put(TWO_LO_ERROR_MESSAGE, (Object)request.getParameter(TWO_LO_ERROR_MESSAGE));
            builder.put(TWO_LO_ENABLED, (Object)Boolean.parseBoolean(request.getParameter(TWO_LO_ENABLED_ERROR_VALUE)));
            builder.put(TWO_LO_EXECUTE_AS, (Object)request.getParameter(TWO_LO_EXECUTE_AS_ERROR_VALUE));
            builder.put(TWO_LO_IMPERSONATION_ENABLED, (Object)Boolean.parseBoolean(request.getParameter(TWO_LO_IMPERSONATION_ENABLED_ERROR_VALUE)));
        } else {
            Consumer consumer = this.serviceProviderStoreService.getConsumer(applicationLink);
            if (consumer != null) {
                builder.put(TWO_LO_ENABLED, (Object)consumer.getTwoLOAllowed());
                builder.put(TWO_LO_EXECUTE_AS, (Object)consumer.getExecutingTwoLOUser());
                builder.put(TWO_LO_IMPERSONATION_ENABLED, (Object)consumer.getTwoLOImpersonationAllowed());
                if (request.getParameter(TWO_LO_SUCCESS_MESSAGE) != null) {
                    builder.put(TWO_LO_SUCCESS_MESSAGE, (Object)request.getParameter(TWO_LO_SUCCESS_MESSAGE));
                }
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            if (request.getParameter("update-2lo") != null) {
                this.updateIncoming2LOConfig(request, response);
            } else {
                this.updateIncomingOAuthConfig(request, response);
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private void updateIncomingOAuthConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
        boolean enabled = Boolean.parseBoolean(request.getParameter("oauth-incoming-enabled"));
        HashMap<String, String> fieldErrorMessages = new HashMap<String, String>();
        this.addOrRemoveConsumer(applicationLink, enabled, fieldErrorMessages);
        String uiPosition = request.getParameter(UI_POSITION);
        if (fieldErrorMessages.isEmpty()) {
            String message = enabled ? this.i18nResolver.getText("auth.oauth.config.serviceprovider.consumer.enabled") : this.i18nResolver.getText("auth.oauth.config.serviceprovider.consumer.disabled");
            response.sendRedirect("./" + applicationLink.getId() + "?" + "message" + "=" + URIUtil.utf8Encode((String)message) + "&uiposition=" + uiPosition);
        } else {
            RendererContextBuilder builder = this.createContextBuilder(applicationLink);
            builder.put(FIELD_ERROR_MESSAGES_CONTEXT_PARAM, fieldErrorMessages);
            builder.put(UI_POSITION, (Object)uiPosition);
            this.render(INCOMING_APPLINKS_TEMPLATE, builder.build(), request, response, applicationLink);
        }
    }

    private void updateIncoming2LOConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uiPosition = request.getParameter(UI_POSITION);
        ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
        Consumer consumer = this.serviceProviderStoreService.getConsumer(applicationLink);
        String targetUrl = "./" + applicationLink.getId() + "?uiposition=" + uiPosition;
        if (consumer == null) {
            throw new IllegalStateException("trying to edit an OAuth consumer that doesn't exist!");
        }
        boolean twoLoEnabled = Boolean.parseBoolean(request.getParameter(TWO_LO_ENABLED));
        boolean foundError = false;
        String executeAsUser = request.getParameter(TWO_LO_EXECUTE_AS);
        if (twoLoEnabled) {
            if (!StringUtils.isEmpty((CharSequence)executeAsUser) && this.userManager.resolve(executeAsUser) == null) {
                foundError = true;
                targetUrl = targetUrl + "&twoLoErrorMessage=" + URIUtil.utf8Encode((String)this.i18nResolver.getText("auth.oauth.config.2lo.username.error"));
                targetUrl = targetUrl + "&twoLoEnabledErrorValue=" + Boolean.parseBoolean(request.getParameter(TWO_LO_ENABLED));
                targetUrl = targetUrl + "&twoLoExecuteAsErrorValue=" + URIUtil.utf8Encode((String)executeAsUser);
                targetUrl = targetUrl + "&twoLoImpersonationEnabledErrorValue=" + Boolean.parseBoolean(request.getParameter(TWO_LO_IMPERSONATION_ENABLED));
            } else if (this.userManager.isSystemAdmin(executeAsUser)) {
                foundError = true;
                targetUrl = targetUrl + "&twoLoErrorMessage=" + URIUtil.utf8Encode((String)this.i18nResolver.getText("auth.oauth.config.2lo.username.cannot.be.sysadmin"));
                targetUrl = targetUrl + "&twoLoEnabledErrorValue=" + Boolean.parseBoolean(request.getParameter(TWO_LO_ENABLED));
                targetUrl = targetUrl + "&twoLoExecuteAsErrorValue=" + URIUtil.utf8Encode((String)executeAsUser);
                targetUrl = targetUrl + "&twoLoImpersonationEnabledErrorValue=" + Boolean.parseBoolean(request.getParameter(TWO_LO_IMPERSONATION_ENABLED));
            } else if (!this.isSysadmin() && Boolean.parseBoolean(request.getParameter(TWO_LO_IMPERSONATION_ENABLED))) {
                throw new AbstractApplinksServlet.ForbiddenException(this.messageFactory.newI18nMessage("applinks.error.only.sysadmin.operation", new Serializable[0]));
            }
        }
        if (!foundError) {
            boolean twoLoIEnabled = twoLoEnabled ? Boolean.parseBoolean(request.getParameter(TWO_LO_IMPERSONATION_ENABLED)) : false;
            Consumer updatedConsumer = new Consumer.InstanceBuilder(consumer.getKey()).name(consumer.getName()).description(consumer.getDescription()).publicKey(consumer.getPublicKey()).signatureMethod(consumer.getSignatureMethod()).callback(consumer.getCallback()).twoLOAllowed(twoLoEnabled).executingTwoLOUser(twoLoEnabled ? executeAsUser : null).twoLOImpersonationAllowed(twoLoIEnabled).build();
            this.serviceProviderStoreService.removeConsumer(applicationLink);
            this.serviceProviderStoreService.addConsumer(updatedConsumer, applicationLink);
            targetUrl = targetUrl + "&twoLoSuccessMessage=" + URIUtil.utf8Encode((String)this.i18nResolver.getText("auth.oauth.config.2lo.update.success"));
            targetUrl = RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.internalHostApplication.getBaseUrl()) + ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/add-consumer-by-url/" + targetUrl;
            targetUrl = this.get2LOConfigRemoteURL(applicationLink, uiPosition, request, targetUrl, Boolean.toString(twoLoEnabled), Boolean.toString(twoLoIEnabled));
        }
        response.sendRedirect(targetUrl);
    }

    @Override
    protected List<String> getRequiredWebResources() {
        return new ImmutableList.Builder().addAll(super.getRequiredWebResources()).add((Object)"com.atlassian.applinks.applinks-oauth-plugin:oauth-2lo-config").build();
    }

    @Override
    protected void render(String template, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ApplicationLink applicationLink) throws IOException {
        RendererContextBuilder builder = new RendererContextBuilder(params);
        String passedEnabledParam = request.getParameter("oauth-outgoing-enabled");
        boolean isEnabledOnOtherSide = passedEnabledParam != null ? Boolean.parseBoolean(request.getParameter("oauth-outgoing-enabled")) || Boolean.parseBoolean(request.getParameter("success")) : true;
        builder.put("enabled", (Object)(applicationLink.getProperty(OAUTH_INCOMING_CONSUMER_KEY) != null && isEnabledOnOtherSide ? 1 : 0));
        String outgoing2LOParam = request.getParameter(OUTGOING_2LO_ENABLED_CONTEXT_PARAM);
        String outgoing2LOiParam = request.getParameter(OUTGOING_2LOI_ENABLED_CONTEXT_PARAM);
        if (outgoing2LOParam != null || outgoing2LOiParam != null) {
            builder.put(PARENT_FRAME_UNDERSTANDS_OUTGOING_2LO_PARAM, (Object)true);
        }
        if (outgoing2LOParam != null) {
            builder.put(OUTGOING_2LO_ENABLED_CONTEXT_PARAM, (Object)Boolean.parseBoolean(outgoing2LOParam));
        }
        if (outgoing2LOiParam != null) {
            builder.put(OUTGOING_2LOI_ENABLED_CONTEXT_PARAM, (Object)Boolean.parseBoolean(outgoing2LOiParam));
        }
        super.render(template, builder.build(), request, response);
    }

    private String getOAuthConfigRemoteURL(ApplicationLink applicationLink, String uiPosition, HttpServletRequest request) {
        try {
            Manifest manifest = this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
            if (manifest.getAppLinksVersion() != null) {
                URI remoteDisplayUrl = this.getRemoteDisplayUrl(applicationLink, request);
                String encodedCallbackUrl = URIUtil.utf8Encode((String)this.getCallbackUrl(applicationLink, uiPosition, request));
                return AddConsumerReciprocalServlet.getReciprocalServletUrl(remoteDisplayUrl, this.internalHostApplication.getId(), encodedCallbackUrl, ENABLE_DISABLE_OAUTH_PARAM);
            }
        }
        catch (Exception e) {
            LOG.warn("An Error occurred when building the URL to the '" + AddConsumerReciprocalServlet.class + "' servlet of the remote application.", (Throwable)e);
        }
        return null;
    }

    private String get2LOConfigRemoteURL(ApplicationLink applicationLink, String uiPosition, HttpServletRequest request, String callbackUrl, String actionParamValue, String actionParamValue2) {
        try {
            Manifest manifest = this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
            if (manifest.getAppLinksVersion() != null) {
                URI remoteDisplayUrl = this.getRemoteDisplayUrl(applicationLink, request);
                String encodedCallbackUrl = URIUtil.utf8Encode((String)callbackUrl);
                return ConfigureOutgoingTwoLeggedOAuthReciprocalServlet.getReciprocalServletUrl(remoteDisplayUrl, this.internalHostApplication.getId(), encodedCallbackUrl, actionParamValue, actionParamValue2);
            }
        }
        catch (Exception e) {
            LOG.warn("An Error occurred when building the URL to the '" + ConfigureOutgoingTwoLeggedOAuthReciprocalServlet.class + "' servlet of the remote application.", (Throwable)e);
        }
        return null;
    }

    private String getCallbackUrl(ApplicationLink applicationLink, String uiPosition, HttpServletRequest request) {
        String outgoing2LOiParam;
        URI remoteDisplayUrl = this.getRemoteDisplayUrl(applicationLink, request);
        String callbackUrl = RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.internalHostApplication.getBaseUrl()) + ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/add-consumer-by-url/" + applicationLink.getId() + "/" + AuthenticationDirection.INBOUND.name() + "?" + "oauth-incoming-enabled" + "=" + ENABLE_DISABLE_OAUTH_PARAM + "&" + UI_POSITION + "=" + uiPosition + "&" + HOST_URL_PARAM + "=" + URIUtil.utf8Encode((URI)remoteDisplayUrl);
        String outgoing2LOParam = request.getParameter(OUTGOING_2LO_ENABLED_CONTEXT_PARAM);
        if (outgoing2LOParam != null) {
            callbackUrl = callbackUrl + "&outgoing2LOEnabled=ENABLE_DISABLE_OUTGOING_TWO_LEGGED_OAUTH_PARAM";
        }
        if ((outgoing2LOiParam = request.getParameter(OUTGOING_2LOI_ENABLED_CONTEXT_PARAM)) != null) {
            callbackUrl = callbackUrl + "&outgoing2LOiEnabled=ENABLE_DISABLE_OUTGOING_TWO_LEGGED_I_OAUTH_PARAM";
        }
        return callbackUrl;
    }

    private URI getRemoteDisplayUrl(ApplicationLink applicationLink, HttpServletRequest request) {
        return !StringUtils.isEmpty((CharSequence)request.getParameter(HOST_URL_PARAM)) ? URI.create(request.getParameter(HOST_URL_PARAM)) : applicationLink.getDisplayUrl();
    }

    private void addOrRemoveConsumer(ApplicationLink applicationLink, boolean enabled, Map<String, String> fieldErrorMessages) throws IOException {
        if (enabled) {
            try {
                Consumer consumer = OAuthHelper.fetchConsumerInformation(applicationLink);
                this.serviceProviderStoreService.addConsumer(consumer, applicationLink);
            }
            catch (ResponseException e) {
                LOG.error("Error occurred when trying to fetch the consumer information from the remote application for application link '" + applicationLink + "'", (Throwable)e);
                fieldErrorMessages.put(COMMUNICATION_CONTEXT_PARAM, this.i18nResolver.getText("auth.oauth.config.error.communication.consumer", new Serializable[]{e.getMessage()}));
            }
            catch (Exception e) {
                LOG.error("Error occurred when trying to store consumer information for application link '" + applicationLink + "'", (Throwable)e);
                fieldErrorMessages.put(COMMUNICATION_CONTEXT_PARAM, this.i18nResolver.getText("auth.oauth.config.error.consumer.add", new Serializable[]{e.getMessage()}));
            }
        } else {
            try {
                this.serviceProviderStoreService.removeConsumer(applicationLink);
            }
            catch (Exception e) {
                LOG.error("Error occurred when trying to remove consumer from application link '" + applicationLink + "'.", (Throwable)e);
                fieldErrorMessages.put(COMMUNICATION_CONTEXT_PARAM, this.i18nResolver.getText("auth.oauth.config.error.consumer.remove", new Serializable[]{e.getMessage()}));
            }
        }
    }
}

