/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Authorization
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Version
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  net.oauth.OAuth
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.internal.Randomizer;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRenderer;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRequestProcessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="postAuthorizationProcessor")
final class PostAuthorization
implements AuthorizationRequestProcessor {
    private static final String AUTH_NO_CALLBACK_APPROVAL_V1_TEMPLATE = "templates/auth/no-callback-approval-v1.vm";
    private static final String AUTH_NO_CALLBACK_APPROVAL_V1A_TEMPLATE = "templates/auth/no-callback-approval-v1a.vm";
    private static final String AUTH_NO_CALLBACK_DENIED_TEMPLATE = "templates/auth/no-callback-denied.vm";
    private final ServiceProviderTokenStore store;
    private final Randomizer randomizer;
    private final UserManager userManager;
    private final AuthorizationRenderer renderer;
    private final TemplateRenderer templateRenderer;

    @Autowired
    public PostAuthorization(@Qualifier(value="tokenStore") ServiceProviderTokenStore store, Randomizer randomizer, UserManager userManager, AuthorizationRenderer renderer, TemplateRenderer templateRenderer) {
        this.store = Objects.requireNonNull(store, "store");
        this.randomizer = Objects.requireNonNull(randomizer, "randomizer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
    }

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, ServiceProviderToken token) throws IOException {
        ServiceProviderToken newToken;
        if (request.getParameter("approve") != null) {
            String verifier = this.randomizer.randomAlphanumericString(6);
            newToken = token.authorize(this.getLoggedInUser(request), verifier);
        } else if (request.getParameter("deny") != null) {
            newToken = token.deny(this.getLoggedInUser(request));
        } else {
            this.renderer.render(request, response, token);
            return;
        }
        this.redirectBackToConsumer(request, response, this.store.put(newToken));
    }

    private Principal getLoggedInUser(HttpServletRequest request) {
        return this.userManager.resolve(this.userManager.getRemoteUsername(request));
    }

    private void redirectBackToConsumer(HttpServletRequest request, HttpServletResponse response, ServiceProviderToken token) throws IOException {
        if (ServiceProviderToken.Version.V_1_0_A.equals((Object)token.getVersion())) {
            this.redirectBackToConsumerVersion1a(request, response, token);
        } else {
            this.redirectBackToConsumerVersion1(request, response, token);
        }
    }

    private void redirectBackToConsumerVersion1(HttpServletRequest request, HttpServletResponse response, ServiceProviderToken token) throws IOException {
        String callback = request.getParameter("oauth_callback");
        if (StringUtils.isEmpty((CharSequence)callback) && token.getConsumer().getCallback() != null) {
            callback = token.getConsumer().getCallback().toString();
        }
        if (StringUtils.isEmpty((CharSequence)callback)) {
            response.setContentType("text/html");
            if (token.getAuthorization() == ServiceProviderToken.Authorization.AUTHORIZED) {
                this.templateRenderer.render(AUTH_NO_CALLBACK_APPROVAL_V1_TEMPLATE, Collections.singletonMap("token", token), (Writer)response.getWriter());
            } else {
                this.templateRenderer.render(AUTH_NO_CALLBACK_DENIED_TEMPLATE, Collections.singletonMap("token", token), (Writer)response.getWriter());
            }
        } else {
            if (token.getToken() != null) {
                callback = OAuth.addParameters((String)callback, (String[])new String[]{"oauth_token", token.getToken()});
            }
            response.sendRedirect(callback);
        }
    }

    private void redirectBackToConsumerVersion1a(HttpServletRequest request, HttpServletResponse response, ServiceProviderToken token) throws IOException {
        URI callback;
        URI uRI = callback = token.getCallback() == null ? token.getConsumer().getCallback() : token.getCallback();
        if (callback == null) {
            response.setContentType("text/html");
            if (token.getAuthorization() == ServiceProviderToken.Authorization.AUTHORIZED) {
                this.templateRenderer.render(AUTH_NO_CALLBACK_APPROVAL_V1A_TEMPLATE, Collections.singletonMap("token", token), (Writer)response.getWriter());
            } else {
                this.templateRenderer.render(AUTH_NO_CALLBACK_DENIED_TEMPLATE, Collections.singletonMap("token", token), (Writer)response.getWriter());
            }
        } else {
            response.sendRedirect(OAuth.addParameters((String)callback.toString(), (String[])new String[]{"oauth_token", token.getToken(), "oauth_verifier", token.getAuthorization() == ServiceProviderToken.Authorization.AUTHORIZED ? token.getVerifier() : "denied"}));
        }
    }
}

