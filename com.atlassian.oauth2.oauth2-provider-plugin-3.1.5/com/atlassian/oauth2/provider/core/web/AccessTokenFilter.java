/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.token.AccessTokenAuthenticationHandler
 *  com.atlassian.oauth2.provider.api.token.AuthenticationResult
 *  com.atlassian.oauth2.scopes.api.Closeable
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.web;

import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.token.AccessTokenAuthenticationHandler;
import com.atlassian.oauth2.provider.api.token.AuthenticationResult;
import com.atlassian.oauth2.provider.core.plugin.PluginChecker;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.core.web.ApplicationNameSupplier;
import com.atlassian.oauth2.scopes.api.Closeable;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessTokenFilter
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AccessTokenFilter.class);
    private final AccessTokenAuthenticationHandler accessTokenAuthenticationHandler;
    private final PluginChecker pluginChecker;
    private final ScopesRequestCache scopesRequestCache;
    private final ClientService clientService;

    public AccessTokenFilter(AccessTokenAuthenticationHandler accessTokenAuthenticationHandler, PluginChecker pluginChecker, ScopesRequestCache scopesRequestCache, ClientService clientService) {
        this.accessTokenAuthenticationHandler = accessTokenAuthenticationHandler;
        this.pluginChecker = pluginChecker;
        this.scopesRequestCache = scopesRequestCache;
        this.clientService = clientService;
    }

    public void init(FilterConfig filterConfig) {
        logger.info("Initializing: [{}]", (Object)AccessTokenFilter.class.getSimpleName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!this.pluginChecker.isOAuth2ScopesPluginEnabled()) {
            logger.debug("OAuth 2 is not enabled as OAuth 2 Scopes plugin is disabled.");
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        AuthenticationResult authenticationResult = AuthenticationResult.notAuthenticated();
        AtomicBoolean chainDoFilterCalled = new AtomicBoolean(false);
        try {
            if (SystemProperty.ENABLE_ACCESS_TOKENS.getValue().booleanValue()) {
                authenticationResult = this.attemptAuthenticationWithAccessToken(httpRequest, httpResponse);
            }
            if (authenticationResult.isAuthenticated()) {
                this.setAttributeForWebSudo(httpRequest);
                this.setAttributeForLoginFilters(httpRequest);
            }
            try (Closeable scopes = this.scopesRequestCache.withScopes(authenticationResult.getScope(), (Supplier)new ApplicationNameSupplier(authenticationResult.getClientId(), this.clientService));){
                chainDoFilterCalled.set(true);
                chain.doFilter((ServletRequest)httpRequest, (ServletResponse)httpResponse);
            }
        }
        catch (Exception e) {
            logger.debug("Failure in OAuth 2 filter", (Throwable)e);
            if (e instanceof ServletException) {
                throw (ServletException)e;
            }
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new ServletException((Throwable)e);
        }
        finally {
            try {
                if (!chainDoFilterCalled.get()) {
                    logger.debug("chain.doFilter was not called. So calling it now");
                    chain.doFilter((ServletRequest)httpRequest, (ServletResponse)httpResponse);
                }
            }
            finally {
                if (authenticationResult.isAuthenticated()) {
                    this.invalidateAnyExistingSession(httpRequest);
                }
            }
        }
    }

    private AuthenticationResult attemptAuthenticationWithAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<String> bearerToken = this.extractToken(request);
            if (bearerToken.isPresent()) {
                AuthenticationResult authenticationResult = this.accessTokenAuthenticationHandler.authenticate(request, response, bearerToken.get());
                response.setHeader("Cache-Control", "private");
                return authenticationResult;
            }
        }
        catch (Exception e) {
            logger.debug("Failed to authenticate request via OAuth2 access token [{}] - [{}]", (Object)request.hashCode(), (Object)e);
        }
        return AuthenticationResult.notAuthenticated();
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization")).map(header -> header.split(" ", 2)).filter(authorizationSegments -> ((String[])authorizationSegments).length == 2).flatMap(authorizationHeaderValue -> {
            Optional<String> token = this.extractBearerToken((String[])authorizationHeaderValue);
            if (!token.isPresent()) {
                token = this.extractTokenFromBasicAuthentication((String[])authorizationHeaderValue);
            }
            return token;
        });
    }

    private Optional<String> extractBearerToken(String[] authorizationParameters) {
        if ("Bearer".equals(authorizationParameters[0])) {
            return Optional.of(authorizationParameters[1]);
        }
        return Optional.empty();
    }

    private Optional<String> extractTokenFromBasicAuthentication(String[] authorizationParameters) {
        if (SystemProperty.TOKEN_VIA_BASIC_AUTHENTICATION.getValue().booleanValue() && "Basic".equalsIgnoreCase(authorizationParameters[0])) {
            try {
                String[] basicAuthorizationUserAndPasswordPair = new String(Base64.getDecoder().decode(authorizationParameters[1]), StandardCharsets.ISO_8859_1).split(":");
                if (basicAuthorizationUserAndPasswordPair.length == 2) {
                    return Optional.ofNullable(basicAuthorizationUserAndPasswordPair[1]);
                }
            }
            catch (Exception e) {
                logger.debug("Failed to decode basic authentication value", (Throwable)e);
            }
        }
        return Optional.empty();
    }

    private void setAttributeForWebSudo(HttpServletRequest req) {
        req.setAttribute("access.token.request", (Object)Boolean.TRUE);
    }

    private void setAttributeForLoginFilters(HttpServletRequest req) {
        req.setAttribute("loginfilter.already.filtered", (Object)Boolean.TRUE);
    }

    private void invalidateAnyExistingSession(HttpServletRequest httpRequest) {
        if (SystemProperty.INVALIDATE_SESSION_ENABLED.getValue().booleanValue()) {
            HttpSession session = httpRequest.getSession(false);
            try {
                if (session != null) {
                    logger.debug("Invalidating session authenticated using OAuth 2 token with an ID [{}]", (Object)session.getId());
                    session.invalidate();
                }
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    public void destroy() {
        logger.info("Destroying: [{}]", (Object)AccessTokenFilter.class.getSimpleName());
    }
}

