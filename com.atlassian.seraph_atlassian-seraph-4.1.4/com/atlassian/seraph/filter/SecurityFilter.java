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
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.filter;

import com.atlassian.seraph.SecurityService;
import com.atlassian.seraph.auth.AuthType;
import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.util.RedirectUtils;
import com.atlassian.seraph.util.SecurityUtils;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityFilter
implements Filter {
    private FilterConfig config = null;
    private SecurityConfig securityConfig = null;
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);
    static final String ALREADY_FILTERED = "os_securityfilter_already_filtered";
    public static final String ORIGINAL_URL = "atlassian.core.seraph.original.url";

    public void init(FilterConfig config) {
        log.debug("SecurityFilter.init");
        this.config = config;
        String configFileLocation = null;
        if (config.getInitParameter("config.file") != null) {
            configFileLocation = config.getInitParameter("config.file");
            log.debug("Security config file location: " + configFileLocation);
        }
        this.securityConfig = SecurityConfigFactory.getInstance(configFileLocation);
        config.getServletContext().setAttribute("seraph_config", (Object)this.securityConfig);
        log.debug("SecurityFilter.init completed successfully.");
    }

    public void destroy() {
        log.debug("SecurityFilter.destroy");
        if (this.securityConfig == null) {
            log.warn("Trying to destroy a SecurityFilter with null securityConfig.");
        } else {
            this.securityConfig.destroy();
            this.securityConfig = null;
        }
        this.config = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getAttribute(ALREADY_FILTERED) != null || !this.getSecurityConfig().getController().isSecurityEnabled()) {
            chain.doFilter(req, res);
            return;
        }
        req.setAttribute(ALREADY_FILTERED, (Object)Boolean.TRUE);
        String METHOD = "doFilter : ";
        boolean dbg = log.isDebugEnabled();
        if (!SecurityUtils.isSeraphFilteringDisabled(req)) {
            log.warn("doFilter : LoginFilter not yet applied to this request - terminating filter chain");
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest)req;
        HttpServletResponse httpServletResponse = (HttpServletResponse)res;
        String originalURL = httpServletRequest.getServletPath() + (httpServletRequest.getPathInfo() == null ? "" : httpServletRequest.getPathInfo()) + (httpServletRequest.getQueryString() == null ? "" : "?" + httpServletRequest.getQueryString());
        httpServletRequest.setAttribute(ORIGINAL_URL, (Object)originalURL);
        if (dbg) {
            log.debug("doFilter : Storing the originally requested URL (atlassian.core.seraph.original.url=" + originalURL + ")");
        }
        HashSet<String> requiredRoles = new HashSet<String>();
        HashSet<String> missingRoles = new HashSet<String>();
        for (SecurityService service : this.getSecurityConfig().getServices()) {
            Set<String> serviceRoles = service.getRequiredRoles(httpServletRequest);
            requiredRoles.addAll(serviceRoles);
        }
        if (dbg) {
            log.debug("doFilter : requiredRoles = " + requiredRoles);
        }
        boolean needAuth = false;
        Authenticator authenticator = this.getSecurityConfig().getAuthenticator();
        Principal user = authenticator.getUser(httpServletRequest, httpServletResponse);
        if (user == null) {
            AuthType authType = AuthType.getAuthTypeInformation(httpServletRequest, this.getSecurityConfig());
            if (RedirectUtils.isBasicAuthentication(httpServletRequest, this.getSecurityConfig().getAuthType())) {
                return;
            }
            if (authType == AuthType.COOKIE && httpServletRequest.getSession(false) == null) {
                httpServletResponse.sendError(401, "os_authType was 'cookie' but no valid cookie was sent.");
                return;
            }
            if (authType == AuthType.ANY && this.hasJSessionCookie(httpServletRequest.getCookies()) && httpServletRequest.getSession(false) == null) {
                httpServletResponse.sendError(401, "os_authType was 'any' and an invalid cookie was sent.");
                return;
            }
        }
        if (dbg) {
            log.debug("doFilter : Setting Auth Context to be '" + (user == null ? "anonymous " : user.getName()) + "'");
        }
        AuthenticationContext authenticationContext = this.getAuthenticationContext();
        authenticationContext.setUser(user);
        for (Object e : requiredRoles) {
            String role = (String)e;
            if (this.getSecurityConfig().getRoleMapper().hasRole(user, httpServletRequest, role)) continue;
            log.info("doFilter : '" + user + "' needs (and lacks) role '" + role + "' to access " + originalURL);
            needAuth = true;
            missingRoles.add(role);
        }
        if (httpServletRequest.getServletPath() != null && httpServletRequest.getServletPath().equals(this.getSecurityConfig().getLoginURL())) {
            if (dbg) {
                log.debug("doFilter : Login page requested so no additional authorization required.");
            }
            needAuth = false;
        }
        if (needAuth) {
            String loginForwardPath = this.getSecurityConfig().getLoginForwardPath();
            if (this.isPOST(httpServletRequest) && StringUtils.isNotBlank((CharSequence)loginForwardPath)) {
                if (dbg) {
                    log.debug("doFilter : Need Authentication for POST: Forwarding to: " + loginForwardPath + " from: " + originalURL);
                }
                httpServletRequest.getRequestDispatcher(loginForwardPath).forward((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
                return;
            }
            if (dbg) {
                log.debug("doFilter : Need Authentication: Redirecting to: " + this.getSecurityConfig().getLoginURL() + " from: " + originalURL);
            }
            httpServletRequest.getSession().setAttribute(this.getSecurityConfig().getOriginalURLKey(), (Object)originalURL);
            if (!httpServletResponse.isCommitted()) {
                httpServletResponse.sendRedirect(this.getLoginUrl(httpServletRequest, missingRoles));
            }
            return;
        }
        try {
            chain.doFilter(req, res);
        }
        finally {
            authenticationContext.clearUser();
        }
    }

    protected String getLoginUrl(HttpServletRequest httpServletRequest, Set<String> missingRoles) {
        return RedirectUtils.getLoginUrl(httpServletRequest);
    }

    private boolean isPOST(HttpServletRequest httpServletRequest) {
        return "POST".equals(httpServletRequest.getMethod());
    }

    private boolean hasJSessionCookie(Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if (!cookie.getName().equals("JSESSIONID")) continue;
            return true;
        }
        return false;
    }

    protected SecurityConfig getSecurityConfig() {
        if (this.securityConfig == null) {
            this.securityConfig = (SecurityConfig)this.config.getServletContext().getAttribute("seraph_config");
        }
        return this.securityConfig;
    }

    protected AuthenticationContext getAuthenticationContext() {
        return this.getSecurityConfig().getAuthenticationContext();
    }
}

