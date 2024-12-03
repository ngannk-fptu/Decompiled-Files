/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.seraph.util.RedirectUtils
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
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
 *  org.springframework.security.core.session.SessionInformation
 *  org.springframework.security.core.session.SessionRegistry
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.util.UserAgentUtil;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.util.RedirectUtils;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import java.io.IOException;
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
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

public final class HttpSessionRegistrarFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpSessionRegistrarFilter.class);
    private final LazyComponentReference<SessionRegistry> registry = new LazyComponentReference("sessionRegistry");

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!ContainerManager.isContainerSetup()) {
            chain.doFilter(request, response);
            return;
        }
        SecurityConfig config = SecurityConfigFactory.getInstance();
        if (!config.getController().isSecurityEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        HttpSession httpSession = httpServletRequest.getSession();
        SessionInformation sessionInformation = this.sessionRegistry().getSessionInformation(httpSession.getId());
        if (null != sessionInformation && sessionInformation.isExpired()) {
            try {
                config.getAuthenticator().logout(httpServletRequest, httpServletResponse);
            }
            catch (AuthenticatorException e) {
                log.info("Unable to logout user using configured authenticator, invalidating current http session and redirecting to login.");
                log.debug(e.getMessage());
                httpSession.invalidate();
            }
            finally {
                if (this.isMobileAppBrowserFamily(httpServletRequest)) {
                    httpServletResponse.setStatus(401);
                } else {
                    httpServletResponse.sendRedirect(RedirectUtils.getLoginUrl((HttpServletRequest)httpServletRequest));
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }

    private SessionRegistry sessionRegistry() {
        return (SessionRegistry)this.registry.get();
    }

    private boolean isMobileAppBrowserFamily(HttpServletRequest request) {
        UserAgentUtil.UserAgent userAgentInfo = UserAgentUtil.getUserAgentInfo(UserAgentUtil.getUserAgent(request));
        UserAgentUtil.BrowserFamily browserFamily = userAgentInfo.getBrowser().getBrowserFamily();
        return UserAgentUtil.BrowserFamily.ATLASSIAN_MOBILE == browserFamily || UserAgentUtil.BrowserFamily.CONFLUENCE_MOBILE_APP == browserFamily;
    }
}

