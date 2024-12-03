/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpLoginOption;
import com.atlassian.plugins.authentication.api.config.LoginFormLoginOption;
import com.atlassian.plugins.authentication.api.config.LoginGatewayType;
import com.atlassian.plugins.authentication.api.config.LoginOption;
import com.atlassian.plugins.authentication.api.config.LoginOptionsService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.exception.UnsupportedHttpMethodException;
import com.atlassian.plugins.authentication.impl.web.filter.AbstractJohnsonAwareFilter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AuthenticationFilter
extends AbstractJohnsonAwareFilter {
    public static final String DESTINATION_REQUEST_PARAM = "atlassian.plugin.auth.destination";
    static final String SITEMESH_ALREADY_FILTERED_ATTRIBUTE_NAME = "com.atlassian.prettyurls.filter.PrettyUrlsSiteMeshFilter";
    protected final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    public static final String AUTH_FALLBACK_QUERY_PARAM = "auth_fallback";
    public static final String ATLASSIAN_RECOVERY_PASSWORD = "atlassian.recovery.password";
    public static final String NATIVE_LOGIN_PARAM = "native_login";
    protected final AuthenticationHandlerProvider authenticationHandlerProvider;
    protected final IdpConfigService idpConfigService;
    protected final LoginOptionsService loginOptionsService;

    public AuthenticationFilter(AuthenticationHandlerProvider authenticationHandlerProvider, IdpConfigService idpConfigService, LoginOptionsService loginOptionsService, JohnsonChecker johnsonChecker) {
        super(johnsonChecker);
        this.authenticationHandlerProvider = authenticationHandlerProvider;
        this.idpConfigService = idpConfigService;
        this.loginOptionsService = loginOptionsService;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        try {
            boolean authFallbackParamPresent = this.isAuthFallbackParamPresent(httpRequest);
            List<LoginOption> loginOptions = this.loginOptionsService.getLoginOptions(authFallbackParamPresent, this.getLoginGatewayType());
            if (this.isProductInRecoveryMode()) {
                this.log.trace("Not attempting external authentication, Atlassian password recovery set");
                this.continueToNativeLoginForm(chain, httpRequest, httpResponse);
            } else if (loginOptions.isEmpty()) {
                this.log.warn("No login options are available, fall backing on to the login form");
                this.continueToNativeLoginForm(chain, httpRequest, httpResponse);
            } else if (this.isForcingNativeLogin(authFallbackParamPresent, loginOptions, httpRequest)) {
                this.continueToNativeLoginForm(chain, httpRequest, httpResponse);
            } else if (this.isProductSpecificSkip(loginOptions, httpRequest)) {
                this.log.warn("Skipping because of product specific configuration");
                this.continueToNativeLoginForm(chain, httpRequest, httpResponse);
            } else if (this.isSupportedHttpMethod(httpRequest)) {
                if (loginOptions.size() == 1) {
                    this.handleSingleLoginOption(chain, httpRequest, httpResponse, (LoginOption)Iterables.getOnlyElement(loginOptions));
                } else {
                    this.forceSitemeshToProcessRequest(httpRequest);
                    this.saveRequestedUrl(httpRequest, this.extractRequestedUrl(httpRequest));
                    httpRequest.getRequestDispatcher("/plugins/servlet/login").forward(request, response);
                }
            } else {
                chain.doFilter(request, response);
            }
        }
        catch (UnsupportedHttpMethodException e) {
            this.log.warn(httpRequest.getMethod() + " method is not supported, thus sending '303 See Other' redirect");
            this.httpResponseSendSeeOtherRedirect(httpRequest, httpResponse);
        }
        catch (IllegalArgumentException e) {
            httpResponse.sendError(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage());
        }
    }

    protected boolean isProductSpecificSkip(List<LoginOption> loginOptions, HttpServletRequest httpRequest) {
        return false;
    }

    private boolean isForcingNativeLogin(boolean authFallbackParamPresent, List<LoginOption> loginOptions, HttpServletRequest request) {
        return loginOptions.contains(LoginFormLoginOption.INSTANCE) && (authFallbackParamPresent || this.isNativeLoginRequested(request));
    }

    private void handleSingleLoginOption(FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse, LoginOption loginOption) throws IOException, ServletException {
        switch (loginOption.getType()) {
            case LOGIN_FORM: {
                this.continueToNativeLoginForm(chain, httpRequest, httpResponse);
                return;
            }
            case IDP: {
                this.handleIdpLogin((IdpLoginOption)loginOption, chain, httpRequest, httpResponse);
                return;
            }
        }
        throw new IllegalStateException("Doesnt support this login type " + (Object)((Object)loginOption.getType()));
    }

    private void continueToNativeLoginForm(FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        this.log.trace("Not attempting external authentication, native login is the only option");
        chain.doFilter((ServletRequest)httpRequest, (ServletResponse)httpResponse);
    }

    protected boolean isSupportedHttpMethod(HttpServletRequest httpRequest) {
        return httpRequest.getMethod().equals("GET");
    }

    private void handleIdpLogin(IdpLoginOption idpLoginOption, FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        IdpConfig idpConfig = this.idpConfigService.getIdpConfig(idpLoginOption.getId());
        AuthenticationHandler authenticationHandler = this.authenticationHandlerProvider.getAuthenticationHandler(idpConfig.getSsoType());
        if (authenticationHandler.isCorrectlyConfigured(idpConfig)) {
            this.log.trace("Redirecting to external IDP login page for idp (id='{}', name='{}') as it is the only available login option", (Object)idpConfig.getId(), (Object)idpConfig.getName());
            authenticationHandler.processAuthenticationRequest(httpRequest, httpResponse, this.extractRequestedUrl(httpRequest), idpConfig);
        } else {
            this.log.trace("External IdP (id='{}', name='{}') is not correctly configured, continuing to product login page", (Object)idpConfig.getId(), (Object)idpConfig.getName());
            chain.doFilter((ServletRequest)httpRequest, (ServletResponse)httpResponse);
        }
    }

    private boolean isProductInRecoveryMode() {
        return System.getProperty(ATLASSIAN_RECOVERY_PASSWORD) != null;
    }

    private boolean isNativeLoginRequested(HttpServletRequest request) {
        return request.getParameter(NATIVE_LOGIN_PARAM) != null;
    }

    private void forceSitemeshToProcessRequest(HttpServletRequest req) {
        req.removeAttribute(SITEMESH_ALREADY_FILTERED_ATTRIBUTE_NAME);
    }

    private void saveRequestedUrl(HttpServletRequest req, String url) {
        req.setAttribute(DESTINATION_REQUEST_PARAM, (Object)url);
    }

    private boolean isAuthFallbackParamPresent(HttpServletRequest request) {
        return request.getParameter(AUTH_FALLBACK_QUERY_PARAM) != null;
    }

    @Nullable
    protected abstract String extractRequestedUrl(HttpServletRequest var1);

    protected LoginGatewayType getLoginGatewayType() {
        return LoginGatewayType.GLOBAL_LOGIN_GATEWAY;
    }

    private void httpResponseSendSeeOtherRedirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        httpResponse.setStatus(Response.Status.SEE_OTHER.getStatusCode());
        httpResponse.setHeader("Location", httpRequest.getRequestURI());
        httpResponse.flushBuffer();
    }
}

