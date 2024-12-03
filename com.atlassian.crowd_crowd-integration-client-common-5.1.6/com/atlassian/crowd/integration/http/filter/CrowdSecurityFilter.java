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
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.integration.http.filter;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.filter.AuthenticationUrlProvider;
import com.atlassian.crowd.service.client.ClientProperties;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdSecurityFilter
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CrowdSecurityFilter.class);
    private static final String BASE_NAME = "com.atlassian.crowd.security";
    private static final String FILTER_RUN = "com.atlassian.crowd.security.FILTER_RUN";
    public static final String ORIGINAL_URL = "com.atlassian.crowd.security.ORIGINAL_URL";
    private final CrowdHttpAuthenticator httpAuthenticator;
    private final AuthenticationUrlProvider authenticationUrlProvider;

    public CrowdSecurityFilter(CrowdHttpAuthenticator httpAuthenticator, ClientProperties clientProperties) {
        this(httpAuthenticator, CrowdSecurityFilter.fixedAuthenticationUrlProvider(clientProperties.getApplicationAuthenticationURL()));
    }

    public CrowdSecurityFilter(CrowdHttpAuthenticator httpAuthenticator, AuthenticationUrlProvider authenticationUrlProvider) {
        this.httpAuthenticator = httpAuthenticator;
        this.authenticationUrlProvider = authenticationUrlProvider;
    }

    private static AuthenticationUrlProvider fixedAuthenticationUrlProvider(final String applicationAuthenticationUrl) {
        return new AuthenticationUrlProvider(){

            @Override
            public String authenticationUrl(HttpServletRequest request) {
                return applicationAuthenticationUrl;
            }
        };
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Date filterStart = new Date();
        try {
            Boolean filterRun = (Boolean)servletRequest.getAttribute(FILTER_RUN);
            if (filterRun != null && filterRun.booleanValue()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                servletRequest.setAttribute(FILTER_RUN, (Object)Boolean.TRUE);
                HttpServletRequest request = (HttpServletRequest)servletRequest;
                HttpServletResponse response = (HttpServletResponse)servletResponse;
                boolean isValidated = this.httpAuthenticator.isAuthenticated(request, response);
                StringBuffer originalURL = request.getRequestURL();
                boolean foundParameter = false;
                if (request.getParameterMap().size() > 0) {
                    originalURL.append("?");
                    Enumeration params = request.getParameterNames();
                    while (params.hasMoreElements()) {
                        String[] values;
                        if (!foundParameter) {
                            foundParameter = true;
                        } else {
                            originalURL.append("&");
                        }
                        String name = (String)params.nextElement();
                        for (String value : values = request.getParameterValues(name)) {
                            originalURL.append(name).append("=").append(value);
                        }
                    }
                }
                if (!isValidated) {
                    logger.info("Requesting URL is: " + originalURL);
                    request.getSession().setAttribute(ORIGINAL_URL, (Object)originalURL.toString());
                    String location = this.authenticationUrl(request);
                    logger.info("Authentication is not valid, redirecting to: {}", (Object)location);
                    response.sendRedirect(location);
                } else {
                    request.removeAttribute(ORIGINAL_URL);
                    filterChain.doFilter(servletRequest, servletResponse);
                    if (servletRequest.getAttribute(FILTER_RUN) != null) {
                        servletRequest.removeAttribute(FILTER_RUN);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), (Throwable)e);
            throw new ServletException(e.getMessage(), (Throwable)e);
        }
        finally {
            if (logger.isDebugEnabled()) {
                Date now = new Date();
                logger.debug("Filter time to run: " + (now.getTime() - filterStart.getTime()) + " ms");
            }
        }
    }

    protected String authenticationUrl(HttpServletRequest request) {
        return this.authenticationUrlProvider.authenticationUrl(request);
    }
}

