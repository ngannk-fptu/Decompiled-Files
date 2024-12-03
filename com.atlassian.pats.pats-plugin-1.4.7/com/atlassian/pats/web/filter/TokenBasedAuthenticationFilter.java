/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.web.filter;

import com.atlassian.pats.api.TokenAuthenticationService;
import com.atlassian.pats.checker.ActiveUserNotFoundException;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.web.filter.LastAccessedTimeBatcher;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserKey;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.time.Clock;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenBasedAuthenticationFilter
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TokenBasedAuthenticationFilter.class);
    private static final String BEARER = "Bearer";
    private final AuthenticationListener authenticationListener;
    private final LastAccessedTimeBatcher accessedTimeBatcher;
    private final Clock utcClock;
    private final ProductUserProvider userChecker;
    private final Message successMessage;
    private final TokenAuthenticationService tokenAuthenticationService;

    public TokenBasedAuthenticationFilter(AuthenticationListener authenticationListener, I18nResolver i18nResolver, Clock utcClock, LastAccessedTimeBatcher accessedTimeBatcher, TokenAuthenticationService tokenAuthenticationService, ProductUserProvider userChecker) {
        this.authenticationListener = authenticationListener;
        this.utcClock = utcClock;
        this.accessedTimeBatcher = accessedTimeBatcher;
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.userChecker = userChecker;
        this.successMessage = i18nResolver.createMessage("personal.access.tokens.filter.authentication.success", new Serializable[0]);
    }

    public void init(FilterConfig filterConfig) {
        logger.info("Initialising: [{}]", (Object)TokenBasedAuthenticationFilter.class.getSimpleName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.trace(">>> TokenBasedAuthenticationFilter.doFilter");
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        boolean isAuthenticatedUsingPats = false;
        try {
            Optional<String> tokenFromRequest;
            if (SystemProperty.PATS_ENABLED.getValue().booleanValue() && (tokenFromRequest = this.getTokenFromRequest(req)).isPresent()) {
                isAuthenticatedUsingPats = this.authenticateUsingToken(req, resp, tokenFromRequest.get());
            }
            chain.doFilter((ServletRequest)req, (ServletResponse)resp);
        }
        finally {
            if (isAuthenticatedUsingPats) {
                this.invalidateSession(req);
            }
        }
        logger.trace("<<< TokenBasedAuthenticationFilter.doFilter");
    }

    private void invalidateSession(HttpServletRequest req) {
        if (SystemProperty.INVALIDATE_SESSION_ENABLED.getValue().booleanValue()) {
            HttpSession session = req.getSession(false);
            try {
                if (session != null) {
                    logger.trace("Invalidating session authenticated using personal access token with an ID [{}]", (Object)session.getId());
                    session.invalidate();
                }
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    private boolean authenticateUsingToken(HttpServletRequest req, HttpServletResponse resp, String tokenFromRequest) {
        try {
            TokenDTO authenticatedToken = this.tokenAuthenticationService.authenticate(tokenFromRequest);
            return this.userChecker.getActiveUserByKey(new UserKey(authenticatedToken.getUserKey())).map(principal -> {
                this.handleAuthSuccessfulResponse(req, resp, (Principal)principal, authenticatedToken);
                return true;
            }).orElseThrow(() -> new ActiveUserNotFoundException("Unable to locate active user: " + authenticatedToken.getUserKey()));
        }
        catch (Exception authException) {
            logger.trace("Auth failure: [{}]", (Object)authException.getMessage());
            return false;
        }
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getHeader("Authorization")).filter(header -> header.trim().startsWith(BEARER)).map(bearerHeader -> StringUtils.split((String)bearerHeader, (char)' ')).filter(array -> ((String[])array).length == 2).map(array -> array[1]);
    }

    private void handleAuthSuccessfulResponse(HttpServletRequest request, HttpServletResponse response, Principal principal, TokenDTO authenticatedToken) {
        logger.trace("Auth SUCCESS for user: [{}] and tokenId: [{}] and expiry:[{}]", new Object[]{authenticatedToken.getUserKey(), authenticatedToken.getTokenId(), authenticatedToken.getExpiringAt()});
        this.authenticationListener.authenticationSuccess((Authenticator.Result)new Authenticator.Result.Success(this.successMessage, principal), request, response);
        this.accessedTimeBatcher.onAuthSuccessEvent(authenticatedToken.getId(), this.utcClock.instant());
        this.setAttributeForWebSudo(request);
    }

    private void setAttributeForWebSudo(HttpServletRequest req) {
        req.setAttribute("access.token.request", (Object)Boolean.TRUE);
    }

    public void destroy() {
        logger.info("Destroying: [{}]", (Object)TokenBasedAuthenticationFilter.class.getSimpleName());
    }
}

