/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.filter;

import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.SessionInvalidator;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.seraph.util.RedirectUtils;
import com.atlassian.seraph.util.SecurityUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseLoginFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(BaseLoginFilter.class);
    private FilterConfig filterConfig = null;
    public static final String LOGIN_SUCCESS = "success";
    public static final String LOGIN_FAILED = "failed";
    public static final String LOGIN_ERROR = "error";
    public static final String LOGIN_NOATTEMPT = null;
    public static final String OS_AUTHSTATUS_KEY = "os_authstatus";
    public static final String AUTHENTICATION_ERROR_TYPE = "auth_error_type";
    private SecurityConfig securityConfig = null;

    public void init(FilterConfig config) {
        this.filterConfig = config;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String METHOD = "doFilter : ";
        boolean dbg = log.isDebugEnabled();
        SecurityHttpRequestWrapper httpServletRequest = new SecurityHttpRequestWrapper((HttpServletRequest)servletRequest);
        HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
        if (!SecurityUtils.isSeraphFilteringDisabled((ServletRequest)httpServletRequest) && this.getSecurityConfig().getController().isSecurityEnabled()) {
            SecurityUtils.disableSeraphFiltering((ServletRequest)httpServletRequest);
            httpServletRequest.setAttribute(OS_AUTHSTATUS_KEY, LOGIN_NOATTEMPT);
            if (dbg) {
                log.debug("doFilter : ____ Attempting login for : '" + this.getRequestUrl((HttpServletRequest)httpServletRequest) + "'");
            }
            String status = this.login((HttpServletRequest)httpServletRequest, httpServletResponse);
            httpServletRequest.setAttribute(OS_AUTHSTATUS_KEY, status);
            if (dbg) {
                String userName = httpServletRequest.getRemoteUser();
                log.debug("doFilter : Login completed for '" + userName + "' - " + OS_AUTHSTATUS_KEY + " = '" + status + "'");
            }
            if (LOGIN_SUCCESS.equals(status) && this.redirectToOriginalDestination((HttpServletRequest)httpServletRequest, httpServletResponse)) {
                return;
            }
            if (status == LOGIN_NOATTEMPT && this.redirectIfUserIsAlreadyLoggedIn((HttpServletRequest)httpServletRequest, httpServletResponse)) {
                return;
            }
        } else if (this.getSecurityConfig().isInvalidateSessionOnWebsudo() && httpServletRequest.getAttribute(this.getSecurityConfig().getWebsudoRequestKey()) != null) {
            if (dbg) {
                log.debug("doFilter : ____ Invalidating session for websudo");
            }
            SessionInvalidator si = new SessionInvalidator(this.getSecurityConfig().getInvalidateWebsudoSessionExcludeList());
            si.invalidateSession((HttpServletRequest)httpServletRequest);
        }
        filterChain.doFilter((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
    }

    private String getRequestUrl(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getServletPath() + (httpServletRequest.getPathInfo() == null ? "" : httpServletRequest.getPathInfo()) + (httpServletRequest.getQueryString() == null ? "" : "?" + httpServletRequest.getQueryString());
    }

    private boolean redirectIfUserIsAlreadyLoggedIn(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        HttpSession session;
        Principal principal;
        if (httpServletRequest.getParameterMap().get("os_destination") != null && (principal = this.getAuthenticator().getUser(httpServletRequest, httpServletResponse)) != null && (session = httpServletRequest.getSession()) != null && session.getAttribute(SecurityConfigFactory.getInstance().getOriginalURLKey()) == null) {
            return this.redirectToOriginalDestination(httpServletRequest, httpServletResponse);
        }
        return false;
    }

    public abstract String login(HttpServletRequest var1, HttpServletResponse var2);

    protected boolean redirectToOriginalDestination(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String METHOD = "redirectToOriginalDestination : ";
        boolean dbg = log.isDebugEnabled();
        String redirectURL = httpServletRequest.getParameter("os_destination");
        String originalURLKey = this.getSecurityConfig().getOriginalURLKey();
        HttpSession httpSession = httpServletRequest.getSession();
        if (redirectURL == null) {
            redirectURL = (String)httpSession.getAttribute(originalURLKey);
        }
        httpSession.removeAttribute(originalURLKey);
        if (redirectURL == null) {
            return false;
        }
        if (!this.getSecurityConfig().getRedirectPolicy().allowedRedirectDestination(redirectURL, httpServletRequest)) {
            log.warn("redirectToOriginalDestination : Redirect request to '" + redirectURL + "' is not allowed. Will send user to the context root instead.");
            redirectURL = "/";
        }
        if (!this.isAbsoluteUrl(redirectURL)) {
            redirectURL = RedirectUtils.appendPathToContext(httpServletRequest.getContextPath(), redirectURL);
        }
        if (dbg) {
            log.debug("redirectToOriginalDestination : Login redirect to: " + redirectURL);
        }
        httpServletResponse.sendRedirect(redirectURL);
        return true;
    }

    protected boolean isAbsoluteUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost() != null;
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    protected Authenticator getAuthenticator() {
        return this.getSecurityConfig().getAuthenticator();
    }

    protected ElevatedSecurityGuard getElevatedSecurityGuard() {
        return this.getSecurityConfig().getElevatedSecurityGuard();
    }

    protected SecurityConfig getSecurityConfig() {
        if (this.securityConfig == null) {
            this.securityConfig = (SecurityConfig)this.filterConfig.getServletContext().getAttribute("seraph_config");
        }
        return this.securityConfig;
    }

    protected AuthenticationContext getAuthenticationContext() {
        return this.getSecurityConfig().getAuthenticationContext();
    }

    class SecurityHttpRequestWrapper
    extends HttpServletRequestWrapper {
        private HttpServletRequest delegateHttpServletRequest;

        public SecurityHttpRequestWrapper(HttpServletRequest delegateHttpServletRequest) {
            super(delegateHttpServletRequest);
            this.delegateHttpServletRequest = delegateHttpServletRequest;
        }

        public String getRemoteUser() {
            Principal user = this.getUserPrincipal();
            return user == null ? null : user.getName();
        }

        public Principal getUserPrincipal() {
            if (BaseLoginFilter.this.getAuthenticator().getClass().isAnnotationPresent(AuthenticationContextAwareAuthenticator.class)) {
                return BaseLoginFilter.this.getAuthenticationContext().getUser();
            }
            return BaseLoginFilter.this.getAuthenticator().getUser(this.delegateHttpServletRequest);
        }
    }
}

