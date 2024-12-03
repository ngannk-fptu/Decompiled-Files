/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.util.RequestAnnotations
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Status
 *  com.atlassian.sal.api.auth.OAuthRequestVerifier
 *  com.atlassian.sal.api.auth.OAuthRequestVerifierFactory
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  net.oauth.OAuthMessage
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthProblemUtils;
import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthRequestUtils;
import com.atlassian.oauth.serviceprovider.internal.util.UserAgentUtil;
import com.atlassian.oauth.util.RequestAnnotations;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.auth.OAuthRequestVerifier;
import com.atlassian.sal.api.auth.OAuthRequestVerifierFactory;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import net.oauth.OAuthMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthFilter
implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthFilter.class);
    private final Authenticator authenticator;
    private final AuthenticationListener authenticationListener;
    private final AuthenticationController authenticationController;
    private final ApplicationProperties applicationProperties;
    private final OAuthRequestVerifierFactory verifierFactory;

    public OAuthFilter(Authenticator authenticator, AuthenticationListener authenticationListener, AuthenticationController authenticationController, ApplicationProperties applicationProperties, OAuthRequestVerifierFactory verifierFactory) {
        this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
        this.authenticationListener = Objects.requireNonNull(authenticationListener, "authenticationListener");
        this.authenticationController = Objects.requireNonNull(authenticationController, "authenticationController");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.verifierFactory = Objects.requireNonNull(verifierFactory, "oAuthRequestVerifierFactory");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        String userAgency = request.getHeader("User-Agent");
        if (UserAgentUtil.isOsxFinder(userAgency) || UserAgentUtil.isMicrosoftMiniRedirector(userAgency)) {
            chain.doFilter((ServletRequest)request, res);
        } else {
            OAuthWWWAuthenticateAddingResponse response = new OAuthWWWAuthenticateAddingResponse((HttpServletResponse)res, this.applicationProperties);
            OAuthRequestVerifier verifier = this.verifierFactory.getInstance(req);
            boolean verifierStatus = verifier.isVerified();
            if (!this.mayProceed(request, (HttpServletResponse)response, verifier)) {
                LOG.debug("OAuth blocked the request [{}]", (Object)request.getRequestURL());
                return;
            }
            try {
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
            }
            finally {
                if (verifierStatus) {
                    verifier.setVerified(true);
                } else {
                    verifier.clear();
                }
                if (OAuthRequestUtils.isOAuthAccessAttempt(request) && request.getSession(false) != null) {
                    request.getSession().invalidate();
                    LOG.debug("OAuth invalidated the session for an OAuth request [{}]", (Object)request.getRequestURL());
                }
            }
        }
    }

    private boolean mayProceed(HttpServletRequest request, HttpServletResponse response, OAuthRequestVerifier verifier) {
        if (!this.authenticationController.shouldAttemptAuthentication(request)) {
            this.authenticationListener.authenticationNotAttempted(request, response);
            return true;
        }
        if (!OAuthRequestUtils.isOAuthAccessAttempt(request)) {
            this.authenticationListener.authenticationNotAttempted(request, response);
            return true;
        }
        Authenticator.Result result = this.authenticator.authenticate(request, response);
        if (result.getStatus() == Authenticator.Result.Status.FAILED) {
            this.authenticationListener.authenticationFailure(result, request, response);
            OAuthProblemUtils.logOAuthRequest(request, "OAuth authentication FAILED.", LOG);
            return false;
        }
        if (result.getStatus() == Authenticator.Result.Status.ERROR) {
            this.authenticationListener.authenticationError(result, request, response);
            OAuthProblemUtils.logOAuthRequest(request, "OAuth authentication ERRORED.", LOG);
            return false;
        }
        this.authenticationListener.authenticationSuccess(result, request, response);
        RequestAnnotations.markAsOAuthRequest((HttpServletRequest)request);
        OAuthProblemUtils.logOAuthRequest(request, "OAuth authentication successful. Request marked as OAuth.", LOG);
        verifier.setVerified(true);
        OAuthProblemUtils.logOAuthRequest(request, "OAuth authentication successful. Thread marked as Verified.", LOG);
        return true;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    private static final class OAuthWWWAuthenticateAddingResponse
    extends HttpServletResponseWrapper {
        private final ApplicationProperties applicationProperties;

        public OAuthWWWAuthenticateAddingResponse(HttpServletResponse response, ApplicationProperties applicationProperties) {
            super(response);
            this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        }

        public void sendError(int sc, String msg) throws IOException {
            if (sc == 401) {
                this.addOAuthAuthenticateHeader();
            }
            super.sendError(sc, msg);
        }

        public void sendError(int sc) throws IOException {
            if (sc == 401) {
                this.addOAuthAuthenticateHeader();
            }
            super.sendError(sc);
        }

        public void setStatus(int sc, String sm) {
            if (sc == 401) {
                this.addOAuthAuthenticateHeader();
            }
            super.setStatus(sc, sm);
        }

        public void setStatus(int sc) {
            if (sc == 401) {
                this.addOAuthAuthenticateHeader();
            }
            super.setStatus(sc);
        }

        private void addOAuthAuthenticateHeader() {
            try {
                OAuthMessage message = new OAuthMessage(null, null, null);
                super.addHeader("WWW-Authenticate", message.getAuthorizationHeader(this.applicationProperties.getBaseUrl()));
            }
            catch (IOException e) {
                throw new RuntimeException("Somehow the OAuth.net library threw an IOException, even though it's not doing any IO operations", e);
            }
        }
    }
}

