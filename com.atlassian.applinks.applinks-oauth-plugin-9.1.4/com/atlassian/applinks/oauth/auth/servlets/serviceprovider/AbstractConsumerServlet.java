/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.oauth.auth.servlets.serviceprovider;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.servlets.AbstractOAuthConfigServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractConsumerServlet
extends AbstractOAuthConfigServlet {
    protected static final String CONSUMER_KEY = "key";
    protected static final String CONSUMER_NAME = "consumerName";
    protected static final String CONSUMER_DESCRIPTION = "description";
    protected static final String CONSUMER_PUBLIC_KEY = "publicKey";
    protected static final String CONSUMER_CALLBACK = "callback";
    protected static final String CONSUMER_TWO_LO_ALLOWED = "two-lo-enabled";
    protected static final String CONSUMER_EXECUTING_TWO_LO_USER = "two-lo-execute-as";
    protected static final String CONSUMER_TWO_LO_IMPERSONATION_ALLOWED = "two-lo-impersonation-enabled";
    protected static final String IS_SYSADMIN = "isSysadmin";
    public static final String OAUTH_INCOMING_CONSUMER_KEY = ApplinksOAuth.PROPERTY_INCOMING_CONSUMER_KEY;
    public static final String OAUTH_INCOMING_ENABLED = "oauth-incoming-enabled";
    public static final String OAUTH_OUTGOING_ENABLED = "oauth-outgoing-enabled";
    protected final RequestFactory requestFactory;
    protected static final String ENABLED_CONTEXT_PARAM = "enabled";
    protected final UserManager userManager;

    protected AbstractConsumerServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, RequestFactory requestFactory, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.requestFactory = requestFactory;
        this.userManager = userManager;
    }

    protected final URI getCallbackUri(HttpServletRequest request, Map<String, String> fieldErrorMessages) {
        URI callback;
        String uriParam = request.getParameter(CONSUMER_CALLBACK);
        if (uriParam == null || StringUtils.isEmpty((CharSequence)uriParam)) {
            return null;
        }
        try {
            if (!uriParam.endsWith("/")) {
                uriParam = uriParam + "/";
            }
            callback = new URI(uriParam);
        }
        catch (URISyntaxException e) {
            fieldErrorMessages.put(CONSUMER_CALLBACK, this.i18nResolver.getText("auth.oauth.config.serviceprovider.invalid.uri"));
            return null;
        }
        if (!callback.isAbsolute()) {
            fieldErrorMessages.put(CONSUMER_CALLBACK, this.i18nResolver.getText("auth.oauth.config.serviceprovider.callback.uri.must.be.absolute"));
            return null;
        }
        if (!"http".equals(callback.getScheme()) && !"https".equals(callback.getScheme())) {
            fieldErrorMessages.put(CONSUMER_CALLBACK, this.i18nResolver.getText("auth.oauth.config.serviceprovider.callback.uri.must.be.http.or.https"));
            return null;
        }
        return callback;
    }

    protected final PublicKey getPublicKey(HttpServletRequest request, Map<String, String> fieldErrorMessages) {
        String publicKeyParam = this.checkRequiredFormParameter(request, CONSUMER_PUBLIC_KEY, fieldErrorMessages, "auth.oauth.config.serviceprovider.missing.public.key");
        if (publicKeyParam == null) {
            return null;
        }
        PublicKey publicKey = null;
        try {
            publicKey = publicKeyParam.startsWith("-----BEGIN CERTIFICATE-----") ? RSAKeys.fromEncodedCertificateToPublicKey((String)publicKeyParam) : RSAKeys.fromPemEncodingToPublicKey((String)publicKeyParam);
        }
        catch (GeneralSecurityException e) {
            fieldErrorMessages.put(CONSUMER_PUBLIC_KEY, this.i18nResolver.getText("auth.oauth.config.serviceprovider.invalid.public.key", new Serializable[]{e.getMessage()}));
        }
        return publicKey;
    }

    protected void render(String template, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ApplicationLink applicationLink) throws IOException {
        RendererContextBuilder builder = new RendererContextBuilder(params);
        builder.put(ENABLED_CONTEXT_PARAM, (Object)(applicationLink.getProperty(OAUTH_INCOMING_CONSUMER_KEY) != null ? 1 : 0));
        super.render(template, builder.build(), request, response);
    }

    protected boolean isSysadmin() {
        return this.userManager.isSystemAdmin(this.userManager.getRemoteUsername());
    }
}

